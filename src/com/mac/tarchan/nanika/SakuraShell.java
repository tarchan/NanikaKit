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
import java.awt.Rectangle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.nanika.nar.NanikaArchive;

/**
 * このクラスは、シェルを表すために使用します。
 * 
 * @version 1.0
 * @author tarchan
 */
public class SakuraShell
{
	/** ロガー */
	private static final Log log = LogFactory.getLog(SakuraShell.class);

	/**
	 * シェル名 
	 */
	private String name;

	/** NAR ファイル */
	private NanikaArchive nar;

	/** サーフェス */
	private SakuraSurface surface;

	/** バルーンオフセット */
	private Point balloonOffset = new Point();

	/** バルーン位置 */
	private Balloon balloonAlignment = Balloon.NONE;

	/** バルーン位置情報 */
	private enum Balloon
	{
		/** 自動調整、shell の y軸座標が画面中央より左なら右、右なら左に表示 */
		NONE,
		/** 常に左に表示 */
		LEFT,
		/** 常に右に表示 */
		RIGHT,
	};

	/** 説明文 */
	private String readme = "readme.txt";

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
	}

	/**
	 * シェルのサーフェスを変更します。
	 * 
	 * @param id サーフェス ID
	 */
	public void setSurface(String id)
	{
		log.debug(String.format("set surface: %s(%s)", name, id));
		surface = nar.getSurface(id);
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
	 * サーフェスを描画します。
	 * 
	 * @param g Graphics2D コンテキスト
	 */
	public void draw(Graphics2D g)
	{
		if (surface != null) surface.draw(g);
	}

	/**
	 * 吹き出しの位置を返します。
	 * 
	 * @param bounds シェルの表示範囲
	 * @return バルーンオフセット
	 */
	public Point getBalloonOffset(Rectangle bounds)
	{
		switch (balloonAlignment)
		{
		case NONE:
			break;
		case LEFT:
			break;
		case RIGHT:
			break;
		default:
			break;
		}
		return new Point(balloonOffset);
	}

	/**
	 * シェルの文字列表現を返します。
	 * 
	 * @return シェルの文字列表現
	 */
	public String toString()
	{
		return String.format("nar=%s, readme=%s", nar, readme);
	}
}
