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
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.image.ChromakeyImageFilter;

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
	private int id;

	/** イメージ */
	private BufferedImage image;

	/** 矩形 */
	private Rectangle rect;

	/**
	 * サーフェスを構築します。
	 * 
	 * @param id サーフェス ID
	 * @param image サーフェスイメージ
	 */
	public SakuraSurface(int id, BufferedImage image)
	{
		this.id = id;
		this.image = image;
		this.rect = new Rectangle(0, 0, image.getWidth(), image.getHeight());
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
	 * サーフェスの文字列表現を返します。
	 * 
	 * @return サーフェスの文字列表現
	 */
	public String toString()
	{
		return String.format("%s[%dx%d, 0x%x]", id, rect.width, rect.height, image.getRGB(0, 0));
	}

	/**
	 * このサーフェスのイメージを描画します。
	 * 
	 * @param g Graphics2D コンテキスト
	 */
	public void draw(Graphics2D g)
	{
//		g.drawImage(image, null, rect.x, rect.y);
		Toolkit tk = Toolkit.getDefaultToolkit();
		ChromakeyImageFilter chromakey = new ChromakeyImageFilter(getBackground().getRGB());
		Image img = tk.createImage(new FilteredImageSource(image.getSource(), chromakey));
		g.drawImage(img, rect.x, rect.y, null);
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
		log.trace("bounds: " + rect.getBounds());
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
		log.trace("affinetransform: " + affinetransform);
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
