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

import com.mac.tarchan.nanika.SakuraGhost;

/**
 * 何か。ミニ劇場を実装します。
 *
 * @since 1.0
 * @author tarchan
 */
public class NanikaMini extends Canvas
{
	/** シリアルバージョンID */
	private static final long serialVersionUID = -5743971128293967183L;

	/** ログ */
	private static final Log log = LogFactory.getLog(NanikaMini.class);

	/** ゴースト */
	private SakuraGhost ghost;

	/** サムネール */
	private BufferedImage thumbnail;

	/** デスクトップモード */
	private boolean desktop = false;

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
		mini.setSize(800, 450);
		mini.setNanika(args);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
		{
			public void run()
			{
				log.info("えんいー");
				mini.ghost.close();
			}
		}));

		JFrame frame = new JFrame("Nanika mini");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mini);
		if (mini.desktop)
		{
//			GraphicsConfiguration gc = frame.getGraphicsConfiguration();
//			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
//			Rectangle desktop = gc.getBounds();
//			frame.setBounds(desktop);
//			System.out.println("desktop=" + desktop);

			frame.setBackground(new Color(0, true));
			frame.setUndecorated(true);
			frame.setAlwaysOnTop(true);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		else
		{
			frame.pack();
		}
		frame.setVisible(true);
	}

	/**
	 * ゴーストを実体化する表示エリアを構築します。
	 */
	public NanikaMini()
	{
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
			ghost = new SakuraGhost();
			for (String name : args)
			{
				if (name.equals("-desktop")) desktop = true;
				else ghost.install(name);
			}

			// サムネールを取得
			thumbnail = ghost.getThumbnail();

			// オブザーバーを設定
			ghost.setObserver(this);

			// SHIORI の実装クラスを設定
			// 里々
			System.setProperty("com.mac.tarchan.nanika.satori.dll", "test.com.mac.tarchan.nanika.NiseSatori");

			// ゴーストを実体化
			log.info(String.format("\"%s\"を実体化します。", ghost.getName()));
			ghost.materialize();

//			String readme = nar.getReadme();
//			log.debug("readme=" + readme);
		}
		catch (IOException e)
		{
			log.error("set nanika error", e);
		}
	}

	/**
	 * ゴーストの名前を返します。
	 *
	 * @return ゴーストの名前
	 */
	public String getName()
	{
		return ghost.getName();
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
		// 背景
		Rectangle rect = g.getClipBounds();
		int x = 8;
		int y = 8;
		int w = rect.width - 16;
		int h = rect.height - 16;
		int arcW = 16;
		int arcH = 16;
		RoundRectangle2D.Float rrect = new RoundRectangle2D.Float(x, y, w, h, arcW, arcH);
		if (!desktop)
		{
			g.setColor(Color.green.darker().darker().darker());
			g.fill(rrect);
			g.clip(rrect);
		}

		// さくら＆ケロを描画
		if (ghost != null) ghost.draw(g);

		// サムネール＆ロゴを描画
		if (!desktop && thumbnail != null)
		{
			g.setClip(null);
			AffineTransform tx = new AffineTransform();
			g.setTransform(tx);
			g.drawImage(thumbnail, null, x, y);
		}
	}
}
