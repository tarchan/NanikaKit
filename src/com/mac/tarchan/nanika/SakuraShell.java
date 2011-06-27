/*
 * SakuraShell.java
 * NanikaKit
 *
 * Created by tarchan on 2008/03/29.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
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
 * このクラスは、シェルを表すために使用します。
 *
 * @since 1.0
 * @author tarchan
 * @deprecated
 */
public class SakuraShell
{
	/** ログ */
	private static final Log log = LogFactory.getLog(SakuraShell.class);

	/** シェル名 */
	private String name;

	/** NAR ファイル */
	private NanikaArchive nar;

	/** サーフェス */
	private SakuraSurface surface;

	/** サーフェスリスト */
	private HashMap<Integer, SakuraSurface> surfaces = new HashMap<Integer, SakuraSurface>();

//	/** さくら側バルーンオフセット */
//	private Point sakuraBalloonOffset = new Point();
//
//	/** ケロ側バルーンオフセット */
//	private Point keroBalloonOffset = new Point();
//
//	/** バルーン位置 */
//	private Balloon balloonAlignment = Balloon.NONE;
//
//	/** バルーン位置情報 */
//	private enum Balloon
//	{
//		/** 自動調整、shell の y軸座標が画面中央より左なら右、右なら左に表示 */
//		NONE,
//		/** 常に左に表示 */
//		LEFT,
//		/** 常に右に表示 */
//		RIGHT,
//	};

	/** 詳細 */
	private Properties descript;

	/** バルーン */
	private SakuraBalloon balloon;

	/** 表示座標 */
	private Point loc = new Point();

	/**
	 * シェルを構築します。
	 *
	 * @param name シェル名
	 * @param nar NAR ファイル
	 */
	public SakuraShell(String name, NanikaArchive nar)
	{
		this.name = name;
		this.nar = nar;

		File shellDir = nar.getShellDirectory();

		// descript
		try
		{
			descript = nar.getEntry(new File(shellDir, nar.getProperty("shell.descript"))).asDescript();
			log.debug("descript=" + descript);
		}
		catch (IOException e)
		{
			log.error("read shell descript error", e);
		}
		catch (NumberFormatException e)
		{
			log.error("parse int error", e);
		}

		// surface
		loadSurface();

		// balloon
		loadBalloon(name);
	}

	/**
	 * シェルを構築します。
	 *
	 * @param name シェル名
	 * @param pair ペアのシェル
	 */
	public SakuraShell(String name, SakuraShell pair)
	{
		this.name = name;
		this.nar = pair.nar;
		this.descript = pair.descript;
		this.surfaces = pair.surfaces;
		loadBalloon(name);
	}

	/**
	 * サーフェスを読み込みます。
	 */
	private void loadSurface()
	{
		File shellDir = nar.getShellDirectory();

		// image
		NanikaEntry[] png = nar.list(new File(shellDir, "surface(.+)\\.png").getPath());
		log.debug("png=" + png.length + "," + Arrays.toString(png));
		Pattern p = Pattern.compile("surface(.+)\\.png");
		for (NanikaEntry entry : png)
		{
			String n = entry.getName();
			Matcher m = p.matcher(n);
			m.find();
			int id = Integer.parseInt(m.group(1));
			surfaces.put(id, nar.getSurface(id));
			log.debug("entry=" + n + "," + id);
		}

		// collision
		NanikaEntry[] txt = nar.list(new File(shellDir, "surface.+\\.txt").getPath());
		log.debug("txt=" + txt.length + "," + Arrays.toString(txt));
	}

	/**
	 * バルーンを読み込みます。
	 *
	 * @param name 名前
	 */
	private void loadBalloon(String name)
	{
		try
		{
			// balloon
//			balloon = new SakuraBalloon(name, nar);
			balloon = nar.getBalloon(name);

			// balloon offset
			int x = Integer.parseInt(descript.getProperty(name + ".balloon.offsetx", "0"));
			int y = Integer.parseInt(descript.getProperty(name + ".balloon.offsety", "0"));
			balloon.setOffset(x, y);

			// balloon alignment
			String align = descript.getProperty(name + ".balloon.alignment");
			balloon.setAlignment(align);
		}
//		catch (IOException e)
//		{
//			log.error("read balloon error", e);
//		}
		catch (Exception e)
		{
			log.error("read balloon error", e);
		}
	}

	/**
	 * シェルの ID を返します。
	 *
	 * @return シェルの ID
	 */
	public String getId()
	{
		return descript.getProperty("id");
	}

	/**
	 * シェルの名前を返します。
	 *
	 * @return シェルの名前
	 */
	public String getName()
	{
		return descript.getProperty("name");
	}

	/**
	 * シェルの作成者名を返します。
	 *
	 * @return シェルの作成者名
	 */
	public String getCraftman()
	{
		return descript.getProperty("craftmanw", descript.getProperty("craftman"));
	}

//	/**
//	 * シェルのタイプを返します。
//	 *
//	 * @return シェルのタイプ
//	 */
//	public String getType()
//	{
//		return descript.getProperty("type");
//	}

	/**
	 * サーフェスの数を返します。
	 *
	 * @return サーフェスの数
	 */
	public int getSurfaceCount()
	{
		return surfaces.size();
	}

	/**
	 * シェルのサーフェスを変更します。
	 *
	 * @param id サーフェス ID
	 */
	public void setSurface(int id)
	{
//		surface = nar.getSurface(id);
		if (surfaces.containsKey(id)) surface = surfaces.get(id);
		else surface = null;
		log.debug(String.format("set surface: %s(%s): %s", name, id, surface));
	}

	/**
	 * サーフェスを返します。
	 *
	 * @return サーフェス
	 */
	public SakuraSurface getSurface()
	{
		return surface;
	}

	/**
	 * バルーンを返します。
	 *
	 * @return バルーン
	 */
	public SakuraBalloon getBalloon()
	{
		return balloon;
	}

	/**
	 * 表示座標を設定します。
	 *
	 * @param p 表示座標
	 */
	public void setLocation(Point p)
	{
		loc.setLocation(p);
	}

	/**
	 * サーフェスを描画します。
	 *
	 * @param g Graphics2D コンテキスト
	 */
	public void draw(Graphics2D g)
	{
		AffineTransform tx = new AffineTransform();
		tx.translate(loc.x, loc.y);
		g.setTransform(tx);

		if (surface != null) surface.draw(g);
		if (balloon != null) balloon.draw(g);
	}

	/**
	 * 当たり判定します。
	 *
	 * @param p 当たり判定座標
	 * @return 当たりの場合は当たった部分の名前。そうでない場合は null
	 */
	public String hit(Point p)
	{
		return surface != null ? surface.hit(p.x - loc.x, p.y - loc.y) : null;
	}

//	/**
//	 * 吹き出しの位置を返します。
//	 *
//	 * @param bounds シェルの表示範囲
//	 * @return バルーンオフセット
//	 */
//	public Point getBalloonOffset(Rectangle bounds)
//	{
//		switch (balloonAlignment)
//		{
//		case NONE:
//			break;
//		case LEFT:
//			break;
//		case RIGHT:
//			break;
//		default:
//			break;
//		}
//
//		return new Point(sakuraBalloonOffset);
//	}

	/**
	 * シェルの文字列表現を返します。
	 *
	 * @return シェルの文字列表現
	 */
	public String toString()
	{
		return String.format("shell: name=%s, %s", name, surface);
	}
}
