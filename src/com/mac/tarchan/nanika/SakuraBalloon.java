/*
 * SakuraBalloon.java
 * NanikaKit
 *
 * Created by tarchan on 2008/04/05.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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
	private Point offset = new Point();

	/** 表示位置 */
	private String align;

	/** サーフェス */
	private SakuraSurface surface;

	/** サーフェスリスト */
	private HashMap<Integer, SakuraSurface> surfaces = new HashMap<Integer, SakuraSurface>();

	/**
	 * バルーンを構築します。
	 * 
	 * @param name sakura または kero
	 * @param nar NAR ファイル
	 * @throws IOException 入力エラーが発生した場合
	 */
	public SakuraBalloon(String name, NanikaArchive nar) throws IOException
	{
		this.name = name;
//		this.nar = nar;

		File balloonHome = new File(nar.getProperty("balloon.directory"));
		log.debug("balloon=" + balloonHome);
		descript = nar.getEntry(new File(balloonHome, nar.getProperty("balloon.descript"))).readDescript();
		log.debug("descript=" + descript);

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

		NanikaEntry[] png = nar.list(new File(balloonHome, pat).getPath());
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
			surfaces.put(id, new SakuraSurface(id, image));
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

	/**
	 * バルーンを描画します。
	 * 
	 * @param g Graphics2D コンテキスト
	 */
	public void draw(Graphics2D g)
	{
		log.trace("draw balloon: " + name + ", " + g.getTransform());
//		g.setClip(null);
		g.setColor(Color.black);
		if (surface != null)
		{
//			AffineTransform t = g.getTransform();
			Rectangle rect = surface.getBounds();
			int x = offset.x;
			int y = offset.y;
			x += -rect.width;
			g.translate(x, y);
			surface.draw(g);
		}
		g.drawString(toString(), 8, 16);
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
