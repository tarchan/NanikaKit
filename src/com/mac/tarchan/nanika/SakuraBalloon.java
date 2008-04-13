/*
 * SakuraBalloon.java
 * NanikaKit
 *
 * Created by tarchan on 2008/04/05.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.nanika.nar.NanikaArchive;
import com.mac.tarchan.nanika.nar.NanikaEntry;

/**
 * @since 1.0
 * @author tarchan
 */
public class SakuraBalloon
{
	/** ロガー */
	private static final Log log = LogFactory.getLog(SakuraBalloon.class);

	/** 名前 */
	private String name;

//	/** NAR ファイル */
//	private NanikaArchive nar;

	/** 詳細 */
	private Properties descript;

	/** 表示オフセット */
	private Point offset = new Point(0, 0);

	/** 表示位置 */
	private String align;

	/** サーフェス */
	private SakuraSurface surface;

	/** サーフェスリスト */
	private HashMap<Integer, SakuraSurface> surfaces = new HashMap<Integer, SakuraSurface>();

	/** スクロールマーカ(上) */
	private SakuraSurface arrow0;

	/** スクロールマーカ(下) */
	private SakuraSurface arrow1;

	/** ONLINE マーカ */
	private SakuraSurface onlinemarker;

	/** SSTP マーカ */
	private SakuraSurface sstpmarker;

	/** ホームポジション */
	private Point origin;

	/** 自動改行位置 */
	private Point wordwrappoint;

	/** フォント */
	private Font font;

	/** フォントカラー */
	private Color fontColor;

	/**
	 * バルーンを構築します。
	 * 
	 * @param name sakura または kero
	 * @param nar NAR ファイル
	 * @throws IOException 入力エラーが発生した場合
	 */
	public SakuraBalloon(String name, NanikaArchive nar) throws IOException
	{
		log.debug("nar=" + nar);
		this.name = name;
//		this.nar = nar;

		File balloonDir = nar.getBalloonDirectory();
		log.debug("balloonDir=" + balloonDir);
		log.debug("balloon=" + new File(balloonDir, nar.getProperty("balloon.descript")));

		// descript
		descript = nar.getEntry(new File(balloonDir, nar.getProperty("balloon.descript"))).readDescript();
		log.debug("descript=" + descript);

		{
			int x = Integer.parseInt(descript.getProperty("origin.x", "0"));
			int y = Integer.parseInt(descript.getProperty("origin.y", "0"));
			origin = new Point(x, y);
			log.debug("origin=" + origin);
		}

		{
			int left = Integer.parseInt(descript.getProperty("validrect.left", "0"));
			int top = Integer.parseInt(descript.getProperty("validrect.top", "0"));
			int right = Integer.parseInt(descript.getProperty("validrect.right", "0"));
			int bottom = Integer.parseInt(descript.getProperty("validrect.bottom", "0"));
			Point validrect = new Point(left, top);
			Point validrectEnd = new Point(right, bottom);
			log.debug("validrect=" + validrect + "," + validrectEnd);
		}

		{
			int x = Integer.parseInt(descript.getProperty("wordwrappoint.x", "0"));
			int y = Integer.parseInt(descript.getProperty("wordwrappoint.y", "0"));
			wordwrappoint = new Point(x, y);
			log.debug("wordwrappoint=" + wordwrappoint);
		}

		{
			String face = descript.getProperty("font.height");
			int height = Integer.parseInt(descript.getProperty("font.height", "0"));
			height = 24;
			int r = Integer.parseInt(descript.getProperty("font.color.r", "0"));
			int g = Integer.parseInt(descript.getProperty("font.color.g", "0"));
			int b = Integer.parseInt(descript.getProperty("font.color.b", "0"));
			font = new Font(face, Font.PLAIN, height);
			fontColor = new Color(r, g, b);
			log.debug("font=" + font + "," + fontColor);
		}

		{
			int x = Integer.parseInt(descript.getProperty("arrow0.x", "0"));
			int y = Integer.parseInt(descript.getProperty("arrow0.y", "0"));
			BufferedImage image = nar.getEntry(new File(balloonDir, nar.getProperty("balloon.arrow0"))).readImage();
			arrow0 = new SakuraSurface("arrow0", image);
			arrow0.setLocation(x, y);
			log.debug("arrow0=" + arrow0);
		}

		{
			int x = Integer.parseInt(descript.getProperty("arrow1.x", "0"));
			int y = Integer.parseInt(descript.getProperty("arrow1.y", "0"));
			BufferedImage image = nar.getEntry(new File(balloonDir, nar.getProperty("balloon.arrow1"))).readImage();
			arrow1 = new SakuraSurface("arrow1", image);
			arrow1.setLocation(x, y);
			log.debug("arrow1=" + arrow1);
		}

		{
			int x = Integer.parseInt(descript.getProperty("onlinemarker.x", "0"));
			int y = Integer.parseInt(descript.getProperty("onlinemarker.y", "0"));
			BufferedImage image = nar.getEntry(new File(balloonDir, nar.getProperty("balloon.onlinemarker"))).readImage();
			onlinemarker = new SakuraSurface("onlinemarker", image);
			onlinemarker.setLocation(x, y);
			log.debug("onlinemarker=" + onlinemarker);
		}

		{
			int x = Integer.parseInt(descript.getProperty("sstpmarker.x", "0"));
			int y = Integer.parseInt(descript.getProperty("sstpmarker.y", "0"));
			BufferedImage image = nar.getEntry(new File(balloonDir, nar.getProperty("balloon.sstpmarker"))).readImage();
			sstpmarker = new SakuraSurface("sstpmarker", image);
			sstpmarker.setLocation(x, y);
			log.debug("sstpmarker=" + sstpmarker);
		}

		// surface
		String pat;
		if (name.equals("sakura"))
		{
//			image = nar.getEntry(new File(balloonHome, "balloons0.png")).readImage();
//			surface = new SakuraSurface(0, image);
			pat = "balloons(.+)\\.png";
		}
		else
		{
//			image = nar.getEntry(new File(balloonHome, "balloonk0.png")).readImage();
//			surface = new SakuraSurface(0, image);
			pat = "balloonk(.+)\\.png";
		}

		NanikaEntry[] png = nar.list(new File(balloonDir, pat).getPath());
		log.debug("png=" + png.length + "," + Arrays.toString(png));
		Pattern p = Pattern.compile(pat);
		for (NanikaEntry entry : png)
		{
			String n = entry.getName();
			Matcher m = p.matcher(n);
			m.find();
			int id = Integer.parseInt(m.group(1));
//			surfaces.put(id, nar.getSurface(id));
			log.debug("entry=" + n + "," + id);
			BufferedImage image = entry.readImage();
			surfaces.put(id, new SakuraSurface(new File(n).getName(), image));
		}
	}

	/**
	 * 表示オフセットを設定します。
	 * 
	 * @param x X 座標
	 * @param y Y 座標
	 */
	public void setOffset(int x, int y)
	{
		offset.move(x, y);
		log.debug(String.format("set offset: %s %d,%d %s", name, x, y, offset));
	}

	/**
	 * 表示位置を設定します。
	 * 
	 * @param align 表示位置
	 */
	public void setAlignment(String align)
	{
		this.align = align;
		log.debug(String.format("set align: %s %s", name, this.align));
	}

	/**
	 * バルーンサーフェスを変更します。
	 * 
	 * @param id サーフェス ID
	 */
	public void setSurface(int id)
	{
		if (surfaces.containsKey(id)) surface = surfaces.get(id);
		else surface = null;
	}

	/** メッセージバッファ */
	private String buf = "";

	/**
	 * 表示文字列をクリアします。
	 */
	public void clearString()
	{
		buf = "";
	}

	/**
	 * 表示文字列を追加します。
	 * 
	 * @param str 文字列
	 */
	public void drawString(CharSequence str)
	{
		buf += str.toString();
		for (int i = 0; i < str.length(); i++)
		{
//			char c = str.charAt(i);
		}
	}

	/**
	 * バルーンを描画します。
	 * 
	 * @param g Graphics2D コンテキスト
	 */
	public void draw(Graphics2D g)
	{
		log.trace("draw balloon: " + name + ", " + g.getTransform() + ", " + surface);
//		g.setClip(null);
		if (surface != null)
		{
//			AffineTransform t = g.getTransform();
			Rectangle rect = surface.getBounds();
			log.trace("rect=" + rect);
//			offset.setBounds(rect);
			log.debug("origin=" + origin);
			log.debug("offset=" + offset);
			int x = offset.x;
			int y = offset.y;
			x += -rect.width;
			g.translate(x, y);
			surface.draw(g);

//			g.setColor(Color.red);
//			g.draw(rect);

			// オプション
			arrow0.draw(g, rect);
			arrow1.draw(g, rect);
			onlinemarker.draw(g, rect);
			sstpmarker.draw(g, rect);

			g.setFont(font);
			g.setColor(fontColor);
//			String s = toString();
			String s = buf;
			FontRenderContext frc = g.getFontRenderContext();
			LineMetrics lm = font.getLineMetrics(s, frc);
			Rectangle2D fr = font.getStringBounds(s, frc);
			log.debug("font=" + lm + ", " + frc + ", " + fr);
			g.drawString(s, origin.x, origin.y + font.getSize());
		}
	}

	/**
	 * バルーンの文字列表現を返します。
	 * 
	 * @return バルーンの文字列表現
	 */
	public String toString()
	{
		return String.format("balloon: %s \"%s\"", name, descript.getProperty("name"));
	}
}
