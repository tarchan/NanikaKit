/*
 * NanikaGhost.java
 * NanikaKit
 *
 * Created by tarchan on 2008/03/29.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.nanika.nar.NanikaArchive;

/**
 * このクラスは、ゴーストを表すために使用します。
 * 
 * @version 1.0
 * @author tarchan
 */
public class SakuraGhost
{
	/** ロガー */
	private static final Log log = LogFactory.getLog(SakuraGhost.class);

	/** アーカイブ */
	private LinkedHashMap<String, NanikaArchive> nar = new LinkedHashMap<String, NanikaArchive>();

	/** シェル */
	private LinkedHashMap<String, SakuraShell> shell = new LinkedHashMap<String, SakuraShell>();

	/** NAR ファイル */
	private NanikaArchive currentNar;

	/** 現在のシェル */
	private String currentShell = "0";

	/**
	 * NAR ファイルをインストールします。
	 * 
	 * @param name NAR ファイル名
	 * @throws IOException インストール中にエラーが発生した場合
	 */
	public void install(String name) throws IOException
	{
		NanikaArchive newNar = new NanikaArchive(name);
		if (currentNar == null) currentNar = newNar;
		nar.put(name, newNar);
		log.debug("nar=" + newNar);
		Properties props = nar.get(name).getProperties();
		log.debug("props=" + props);
	}

	/**
	 * ゴーストの姿を現します。
	 */
	public void materialize()
	{
		log.info("starting up engine");
		loadShiori();
		loadBalloon();
		loadGhost();
		loadShell();
		log.info("materialized");
	}

	/**
	 * SHIORI サブシステムをロードします。
	 */
	private void loadShiori()
	{
		log.info("loading SHIORI subsystem");
		String shioriSubsystem = System.getProperty("com.mac.tarchan.nanika.shiori.dll", "DummyShiori");
		log.debug("SHIORI=" + shioriSubsystem);
	}

	/**
	 * バルーンをロードします。
	 */
	private void loadBalloon()
	{
		String name="name";
		String craftman = "craftman";
		log.info(String.format("loading Balloon named as \"%s\" crafted by %s", name, craftman));
	}

	/**
	 * ゴーストをロードします。
	 */
	private void loadGhost()
	{
		log.info("loading GHOST basic elements");
	}

	/**
	 * シェルをロードします。
	 */
	private void loadShell()
	{
		int num = 0;
		int max = currentNar.list("").length;
		log.info(String.format("loading surface %d/%d", num, max));

		String name="name";
		String craftman = "craftman";
		log.info(String.format("loading SHELL elements named as \"%s\" crafted by %s", name, craftman));

//		log.debug("currentNar=" + currentNar);
//		log.debug("shell=" + shell);

		// sakura
		shell.put("0", new SakuraShell("sakura", currentNar));
		shell.get("0").setSurface("0");

		// kero
		shell.put("1", new SakuraShell("kero", currentNar));
		shell.get("1").setSurface("10");
	}

	/**
	 * ゴーストの姿を消します。
	 */
	public void vanish()
	{
		log.info("vanish");
		nar.clear();
		shell.clear();
		currentNar = null;
		currentShell = null;
	}

	/**
	 * ゴーストを描画します。
	 * 
	 * @param g Graphics2D コンテキスト
	 */
	public void draw(Graphics2D g)
	{
		SakuraShell sakura = shell.get("0");
		SakuraShell kero = shell.get("1");

		Rectangle clip = g.getClipBounds();
		log.debug("clip=" + clip);
		int x = clip.x;
		int y = clip.y;
		int right = x + clip.width;
		int bottom = y + clip.height;

		// サクラ
		if (sakura != null)
		{
			AffineTransform tx = new AffineTransform();
//			tx.scale(0.8, 0.8);
//			Rectangle rect = sakura.getBounds();
			Rectangle rect = tx.createTransformedShape(sakura.getSurface()).getBounds();
//			log.debug("rect=" + rect);
			rect.x = right - rect.width;
			rect.y = bottom - rect.height;
//			tx.shear(-0.5, 0);
			tx.rotate(Math.toRadians(0), right - rect.width / 2, bottom);
			tx.translate(rect.x, rect.y);
			g.setTransform(tx);
			sakura.draw(g);
			right = rect.x;
		}

		// ケロ
		if (kero != null)
		{
			AffineTransform tx = new AffineTransform();
			Rectangle rect = kero.getSurface().getBounds();
			rect.x = x + (right - x) / 2 - rect.width / 2;
			rect.y = bottom - rect.height;
			tx.translate(rect.x, rect.y);
//			tx.shear(0.5, 0);
			g.setTransform(tx);
			kero.draw(g);
		}
	}

	/**
	 * サムネールを返します。
	 * 
	 * @return サムネール
	 */
	public BufferedImage getThumbnail()
	{
		return currentNar.getThumbnail();
	}

	/**
	 * ゴーストの文字列表現を返します。
	 * 
	 * @return ゴーストの文字列表現
	 */
	public String toString()
	{
		return String.format("nar=%s, current=%s", nar, currentShell);
	}
}
