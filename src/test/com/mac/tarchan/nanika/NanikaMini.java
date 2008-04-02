/*
 * NanikaMini.java
 * NanikaKit
 *
 * Created by tarchan on 2008/03/28.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package test.com.mac.tarchan.nanika;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.nanika.SakuraGhost;

/**
 * @version 1.0
 * @author tarchan
 */
public class NanikaMini extends Canvas
{
	/** ロガー */
	private static final Log log = LogFactory.getLog(NanikaMini.class);

	/** ゴースト */
	private SakuraGhost ghost;

//	/** サクラ側サーフェス */
//	private SakuraSurface sakura;
//
//	/** ケロ側サーフェス */
//	private SakuraSurface kero;

	/** サムネール */
	private BufferedImage thumbnail;

	/**
	 * @param args <nar ファイル名>
	 */
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("NanikaKit.jar <nar files>");
			System.exit(1);
		}

		NanikaMini mini = new NanikaMini();
		mini.setSize(640, 480);
		mini.setNanika(args);

		JFrame frame = new JFrame("Nanika mini");
		frame.getContentPane().add(mini);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setBackground(new Color(0, true));
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * NAR ファイルを読み込みます。
	 * 
	 * @param args NAR ファイルリスト
	 */
	public void setNanika(String[] args)
	{
		try
		{
//			name = "nanika/nar/akane_v001.zip";
//			name = "nanika/nar/mayura_v340.zip";
//			name = "nanika/nar/sakura020212.nar";
//			name = "nanika/nar/yohko020317.nar";
//			name = "nanika/nar/nekoshoRX202.nar";
//			args[0] = "nanika/nar/alto_v110.nar";

			ghost = new SakuraGhost();
//			ghost.install(name);

			// すべての NAR ファイルをインストール
			for (String name : args)
			{
				ghost.install(name);
			}

			// ゴーストを実体化
			ghost.materialize();

//			NanikaArchive nar = new NanikaArchive(name);
//			log.debug("nar=" + nar);
//			Properties props = nar.getProperties();
//			log.debug("props=" + props);
//
//			SakuraSurface surface0 = nar.getSurface("0");
////			SakuraSurface surface0 = nar.getSurface("surface2053");
//			log.debug("surface0=" + surface0);
//			sakura = surface0;
//
//			SakuraSurface surface10 = nar.getSurface("10");
//			log.debug("surface10=" + surface10);
//			kero = surface10;
//
//			thumbnail = nar.getThumbnail();
			thumbnail = ghost.getThumbnail();

//			String readme = nar.getReadme();
//			log.debug("readme=" + readme);
		}
		catch (IOException e)
		{
			log.error("set nanika error", e);
		}
	}

	/**
	 * 背景を描画します。
	 * 
	 * @param g Graphics コンテキスト
	 */
	public void paint(Graphics g)
	{
		paint2d((Graphics2D)g);
	}

	/**
	 * 背景を描画します。
	 * 
	 * @param g Graphics2D コンテキスト
	 */
	public void paint2d(Graphics2D g)
	{
		log.debug("Graphics2D=" + g.getClass().getName());
		// sun.java2d.SunGraphics2D
//		g.fill(sakura);
//		g.fill(kero);

		// 背景
//		g.setColor(Color.white);
//		g.fill(g.getClipBounds());
		g.setColor(Color.gray);
//		g.setColor(transparent);
//		if (sakura != null) g.setColor(sakura.getBackground());
		int x = 8;
		int y = 8;
		int w = 640 - 16;
//		int h = 480 - 16 - 22;
		int h = 480 - 16;
		int arcW = 16;
		int arcH = 16;
//		g.fillRoundRect(x, y, w, h, arcW, arcH);
		RoundRectangle2D.Float rrect = new RoundRectangle2D.Float(x, y, w, h, arcW, arcH);
		g.clip(rrect);
		g.fill(rrect);
//		int right = x + w - 16;
//		x += 128;
//		int right = x + w;
//		int bottom = y + h;

		if (ghost != null) ghost.draw(g);

//		// サクラ
//		if (sakura != null)
//		{
//			AffineTransform tx = new AffineTransform();
////			tx.scale(0.8, 0.8);
////			Rectangle rect = sakura.getBounds();
//			Rectangle rect = tx.createTransformedShape(sakura).getBounds();
//			log.debug("rect=" + rect);
//			rect.x = right - rect.width;
//			rect.y = bottom - rect.height;
////			tx.shear(-0.5, 0);
//			tx.rotate(Math.toRadians(0), right - rect.width / 2, bottom);
//			tx.translate(rect.x, rect.y);
//			g.setTransform(tx);
//			sakura.draw(g);
//			right = rect.x;
//		}
//
//		// ケロ
//		if (kero != null)
//		{
//			AffineTransform tx = new AffineTransform();
//			Rectangle rect = kero.getBounds();
//			rect.x = x + (right - x) / 2 - rect.width / 2;
//			rect.y = bottom - rect.height;
//			tx.translate(rect.x, rect.y);
////			tx.shear(0.5, 0);
//			g.setTransform(tx);
//			kero.draw(g);
//		}

		// ロゴ
		if (thumbnail != null)
		{
			g.setClip(null);
			AffineTransform tx = new AffineTransform();
			g.setTransform(tx);
			g.drawImage(thumbnail, null, 8, 8);
		}
	}
}
