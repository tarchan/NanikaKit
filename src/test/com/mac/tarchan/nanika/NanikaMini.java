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
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.nanika.MaterializedSakuraGhost;

/**
 * 何か。ミニ劇場を実装します。
 * 
 * @since 1.0
 * @author tarchan
 */
public class NanikaMini extends Canvas
{
	/** ロガー */
	private static final Log log = LogFactory.getLog(NanikaMini.class);

	/** ゴースト */
	private MaterializedSakuraGhost ghost;

	/** サムネール */
	private BufferedImage thumbnail;

	/**
	 * 何か。を実体化します。
	 * 
	 * @param args <nar ファイル名>
	 */
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("NanikaKit.jar <ghost nar> <balloon nar> ...");
			System.exit(1);
		}

		final NanikaMini mini = new NanikaMini();
		mini.setSize(640, 480);
//		mini.setSize(800, 600);
		mini.setNanika(args);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			public void run()
			{
				log.info("えんいー");
				mini.ghost.vanish();
			}
		}));

		JFrame frame = new JFrame("Nanika mini");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mini);
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
//			args = new String[]{"nanika/nar/akane_v118.zip", "nanika/balloon/defnekoko.ZIP"};
//			args = new String[]{"nanika/nar/sakura020212.nar"};

			// すべての NAR ファイルをインストール
			ghost = new MaterializedSakuraGhost();
			for (String name : args)
			{
				ghost.install(name);
			}

			// ゴーストを実体化
			ghost.materialize();

			// サムネールを取得
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
	private void paint2d(Graphics2D g)
	{
		log.debug("Graphics2D=" + g.getClass().getName());
		// sun.java2d.SunGraphics2D
//		g.fill(sakura);
//		g.fill(kero);

		// 背景
		Rectangle rect = g.getClipBounds();
//		g.setColor(Color.white);
//		g.fill(g.getClipBounds());
		g.setColor(Color.gray);
//		g.setColor(transparent);
//		if (sakura != null) g.setColor(sakura.getBackground());
		int x = 8;
		int y = 8;
		int w = rect.width - 16;
//		int h = rect.height - 16 - 22;
		int h = rect.height - 16;
		int arcW = 16;
		int arcH = 16;
//		g.fillRoundRect(x, y, w, h, arcW, arcH);
		RoundRectangle2D.Float rrect = new RoundRectangle2D.Float(x, y, w, h, arcW, arcH);
		g.fill(rrect);
		g.clip(rrect);
//		int right = x + w - 16;
//		x += 128;
//		int right = x + w;
//		int bottom = y + h;

		// さくら＆ケロを描画
		if (ghost != null) ghost.draw(g);

		// サムネール＆ロゴを描画
		if (thumbnail != null)
		{
			g.setClip(null);
			AffineTransform tx = new AffineTransform();
			g.setTransform(tx);
			g.drawImage(thumbnail, null, 8, 8);
		}
	}
}
