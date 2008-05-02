/*
 * SakuraSurface.java
 * NanikaKit
 *
 * Created by tarchan on 2008/03/28.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.geom.WrappedPoint;
import com.mac.tarchan.image.ChromakeyImageFilter;
import com.mac.tarchan.nanika.nar.NanikaArchive;
import com.mac.tarchan.nanika.nar.NanikaEntry;

/**
 * このクラスは、サーフェスを表すために使用します。
 * 
 * @since 1.0
 * @author tarchan
 */
public class SakuraSurface implements Shape
{
	/** ロガー */
	private static final Log log = LogFactory.getLog(SakuraSurface.class);

	/** サーフェス ID */
	private final String id;

	/** イメージ */
	private BufferedImage image;

	/** 矩形 */
	private final Rectangle rect;

	/** 当たり判定 */
	private HashMap<String, Rectangle> collisions = new HashMap<String, Rectangle>();

	/**
	 * サーフェスをロードします。
	 * 
	 * @param id サーフェス ID
	 * @param nar NAR アーカイブ
	 * @return サーフェス
	 * @throws IOException 入力エラーが発生した場合
	 */
	public static SakuraSurface getSurface(int id, NanikaArchive nar) throws IOException
	{
		File file = new File(nar.getShellDirectory(), String.format("surface%s.png", id));
		log.debug(id + "=" + file);
		NanikaEntry entry = nar.getEntry(file);
//		log.debug(id + "=" + entry.getName());

		String descript = loadDescript(id, nar);
		BufferedImage image = ImageIO.read(entry.getInputStream());
//		log.debug("image=" + image);
//		log.debug("image=" + image.getWidth() + "x" + image.getHeight() + ","  + image.getType() + "," + image.getColorModel());
//		int rgb = image.getRGB(0, 0);
//		log.debug("rgb=0x" + Integer.toHexString(rgb));
		SakuraSurface surface = new SakuraSurface("" + id, image, descript);
		return surface;
	}

	/**
	 * サーフェス定義をロードします。
	 * 
	 * @param id サーフェス ID
	 * @param nar NAR アーカイブ
	 * @return サーフェス定義
	 * @throws IOException 入力エラーが発生した場合
	 */
	private static String loadDescript(int id, NanikaArchive nar) throws IOException
	{
		File file = new File(nar.getShellDirectory(), "surfaces.txt");
		NanikaEntry entry = nar.getEntry(file);
		if (entry != null)
		{
			Scanner s = new Scanner(entry.getInputStream(), "Shift_JIS");
			String name = "surface" + id;
			String find = s.findWithinHorizon("(" + Pattern.quote(name) + ")\\s*\\{\\s*([^{]*)\\}", 0);
			if (find == null) return null;
//			System.out.println("find=" + find);

			MatchResult m = s.match();
			String head = m.group(1);
			String body = m.group(2);
			log.debug("head=\"" + head + "\"");
			log.debug("body=\"" + body + "\"");
//			parseSurface(new Scanner(body));
			return body;
		}
		else
		{
			file = new File(nar.getShellDirectory(), String.format("surface%s.txt", id));
			entry = nar.getEntry(file);
			if (entry == null) return null;

			return null;
		}
	}

	/**
	 * サーフェスを構築します。
	 * 
	 * @param id サーフェス ID
	 * @param image サーフェスイメージ
	 */
	public SakuraSurface(String id, BufferedImage image)
	{
		this(id, image, null);
	}

	/**
	 * サーフェスを構築します。
	 * 
	 * @param id サーフェス ID
	 * @param image サーフェスイメージ
	 * @param descript サーフェス定義
	 */
	public SakuraSurface(String id, BufferedImage image, String descript)
	{
		this.id = id;
		this.image = image;
		this.rect = new Rectangle(0, 0, image.getWidth(), image.getHeight());
		if (descript != null) parseDescript(descript);
	}

	/**
	 * サーフェス定義を解析します。
	 * 
	 * @param descript サーフェス定義
	 */
	private void parseDescript(String descript)
	{
		Scanner s = new Scanner(descript);
		while (true)
		{
			if (s.hasNext("(collision.+?),(.+)"))
			{
				s.next("(collision.+?),(.+)");
				MatchResult m = s.match();
				String head = m.group(1);
				String body = m.group(2);
				String[] token = body.split(",");
				int x1 = Integer.parseInt(token[0]);
				int y1 = Integer.parseInt(token[1]);
				int x2 = Integer.parseInt(token[2]);
				int y2 = Integer.parseInt(token[3]);
				String name = token[4];
				Rectangle rect = new Rectangle(new Point(x1, y1));
				rect.add(new Point(x2, y2));
				System.out.println("当たり判定: " + head + ": " + name + ": " + rect);
				collisions.put(name, rect);
			}
			else if (s.hasNext("(element.+?),(.+)"))
			{
				s.next("(element.+?),(.+)");
				MatchResult m = s.match();
				String head = m.group(1);
				String body = m.group(2);
				String[] token = body.split(",");
				String type = token[0];
				String filename = token[1];
				int x = Integer.parseInt(token[2]);
				int y = Integer.parseInt(token[3]);
				Point p = new Point(x, y);
				System.out.println("ベースサーフェス: " + head + ": " + Arrays.toString(new Object[]{type, filename, p}));
			}
			else if (s.hasNext("(.+?interval),(.+)"))
			{
				s.next("(.+?interval),(.+)");
				MatchResult m = s.match();
				String head = m.group(1);
				String body = m.group(2);
				System.out.println("アニメーション開始: " + head + ": " + Arrays.toString(body.split(",")));
			}
			else if (s.hasNext("(.+?pattern.+?),(.+)"))
			{
				s.next("(.+?pattern.+?),(.+)");
				MatchResult m = s.match();
				String head = m.group(1);
				String body = m.group(2);
				System.out.println("アニメーションパターン: " + head + ": " + Arrays.toString(body.split(",")));
			}
			else if (s.hasNext("(.+?option),(.+)"))
			{
				s.next("(.+?option),(.+)");
				MatchResult m = s.match();
				String head = m.group(1);
				String body = m.group(2);
				System.out.println("オプション: " + head + ": " + Arrays.toString(body.split(",")));
			}
			else if (s.hasNextLine())
			{
				String line = s.nextLine();
				if (line.trim().length() > 0) System.out.println("未定義: " + line);
			}
			else
			{
				break;
			}
		}
	}

	/**
	 * 背景色を返します。
	 * 
	 * @return 背景色
	 */
	public Color getBackground()
	{
		return new Color(image.getRGB(0, 0), true);
	}

	/**
	 * 位置を設定します。
	 * 
	 * @param x X 座標
	 * @param y Y 座標
	 */
	public void setLocation(int x, int y)
	{
		rect.setLocation(x, y);
	}

	/**
	 * サーフェスの文字列表現を返します。
	 * 
	 * @return サーフェスの文字列表現
	 */
	public String toString()
	{
		return String.format("%s[%d, %d, %dx%d, 0x%x]", id, rect.x, rect.y, rect.width, rect.height, image.getRGB(0, 0));
	}

	/**
	 * このサーフェスのイメージを描画します。
	 * 
	 * @param g Graphics2D コンテキスト
	 */
	public void draw(Graphics2D g)
	{
		draw(g, null);
	}

	/**
	 * このサーフェスのイメージを描画します。
	 * 
	 * @param g Graphics2D コンテキスト
	 * @param bounds 描画範囲
	 */
	public void draw(Graphics2D g, Rectangle bounds)
	{
//		log.trace("draw surface: " + rect);
//		Rectangle rect = getBounds();
//		g.drawImage(image, null, rect.x, rect.y);
		Toolkit tk = Toolkit.getDefaultToolkit();
		ChromakeyImageFilter chromakey = new ChromakeyImageFilter(getBackground().getRGB());
		Image img = tk.createImage(new FilteredImageSource(image.getSource(), chromakey));
		if (bounds != null)
		{
			WrappedPoint point = new WrappedPoint(rect.x, rect.y).setBounds(bounds);
			g.drawImage(img, point.x, point.y, null);
		}
		else
		{
			g.drawImage(img, rect.x, rect.y, null);
		}

		g.setColor(Color.red);
		for (Map.Entry<String, Rectangle> entry : collisions.entrySet())
		{
			String name = entry.getKey();
			Rectangle rect = entry.getValue();
			g.drawString(name, rect.x, rect.y);
			g.draw(rect);
		}
	}

	/**
	 * @see java.awt.Shape#contains(java.awt.geom.Point2D)
	 */
	public boolean contains(Point2D point2d)
	{
		log.trace("point2d: " + point2d);
		return rect.contains(point2d);
	}

	/**
	 * @see java.awt.Shape#contains(java.awt.geom.Rectangle2D)
	 */
	public boolean contains(Rectangle2D rectangle2d)
	{
		log.trace("rectangle2d: " + rectangle2d);
		return rect.contains(rectangle2d);
	}

	/**
	 * @see java.awt.Shape#contains(double, double)
	 */
	public boolean contains(double d, double d1)
	{
		log.trace("d: " + d + ", d1: " + d1);
		return rect.contains(d, d1);
	}

	/**
	 * @see java.awt.Shape#contains(double, double, double, double)
	 */
	public boolean contains(double d, double d1, double d2, double d3)
	{
		log.trace("d: " + d + ", d1: " + d1 + ", d2: " + d2 + ", d3: " + d3);
		return rect.contains(d, d1, d2, d3);
	}

	/**
	 * @see java.awt.Shape#getBounds()
	 */
	public Rectangle getBounds()
	{
//		log.trace("bounds: " + rect.getBounds());
		return rect.getBounds();
	}

	/**
	 * @see java.awt.Shape#getBounds2D()
	 */
	public Rectangle2D getBounds2D()
	{
		log.trace("bounds2d: " + rect.getBounds2D());
		return rect.getBounds2D();
	}

	/**
	 * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform)
	 */
	public PathIterator getPathIterator(AffineTransform affinetransform)
	{
//		log.trace("affinetransform: " + affinetransform, new Exception());
//		log.trace("affinetransform: " + affinetransform);
		return rect.getPathIterator(affinetransform);
	}

	/**
	 * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform, double)
	 */
	public PathIterator getPathIterator(AffineTransform affinetransform, double d)
	{
		log.trace("affinetransform: " + affinetransform + ", d: " + d);
		return rect.getPathIterator(affinetransform, d);
	}

	/**
	 * @see java.awt.Shape#intersects(java.awt.geom.Rectangle2D)
	 */
	public boolean intersects(Rectangle2D rectangle2d)
	{
		log.trace("rectangle2d: " + rectangle2d);
		return rect.intersects(rectangle2d);
	}

	/**
	 * @see java.awt.Shape#intersects(double, double, double, double)
	 */
	public boolean intersects(double d, double d1, double d2, double d3)
	{
		log.trace("d: " + d + ", d1: " + d1 + ", d2: " + d2 + ", d3: " + d3);
		return rect.intersects(d, d1, d2, d3);
	}
}
