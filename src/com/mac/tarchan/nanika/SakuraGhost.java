/*
 * SakuraGhost.java
 * NanikaKit
 *
 * Created by tarchan on 2008/03/29.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.nanika.nar.NanikaArchive;

/**
 * このクラスは、ゴーストを実装します。
 * 
 * @since 1.0
 * @author tarchan
 */
public class SakuraGhost
{
	/** ロガー */
	private static final Log log = LogFactory.getLog(SakuraGhost.class);

	/** アーカイブ */
	private LinkedHashMap<String, NanikaArchive> nar = new LinkedHashMap<String, NanikaArchive>();

	/** シェル */
	private LinkedHashMap<Integer, SakuraShell> shell = new LinkedHashMap<Integer, SakuraShell>();

	/** NAR ファイル */
	private NanikaArchive currentNar;

	/** 現在のシェル */
	private SakuraShell currentShell;

	/** ゴーストの栞 */
	private SakuraShiori shiori;

	/** イメージオブザーバー */
	private Component observer;

	/**
	 * オブザーバーを設定します。
	 * 
	 * @param observer オブザーバー
	 * @return このゴーストへの参照
	 */
	public SakuraGhost setObserver(Component observer)
	{
		this.observer = observer;
		return this;
	}

	/**
	 * NAR ファイルをインストールします。
	 * 
	 * @param name NAR ファイル名
	 * @return このゴーストへの参照
	 * @throws IOException インストール中にエラーが発生した場合
	 */
	public SakuraGhost install(String name) throws IOException
	{
		NanikaArchive newNar = new NanikaArchive(name);
		if (currentNar == null) currentNar = newNar;
		nar.put(name, newNar);
		log.debug("nar=" + newNar);
		Properties props = nar.get(name).getProperties();
		log.debug("props=" + props);

		return this;
	}

	/**
	 * ゴーストの姿を現します。
	 * 
	 * @return このゴーストへの参照
	 */
	public SakuraGhost materialize()
	{
		// loading
		log.info("starting up engine");
		loadShiori();
		loadBalloon();
		loadGhost();
		loadShell();

		// materialize
		setScope(0);
		setSurface(0);
		setBalloonSurface(0);
		setScope(1);
		setSurface(10);
		setBalloonSurface(0);
		log.info("materialized");

		requestForSecond();

		return this;
	}

	/**
	 * 定期的に SHIORI にリクエストを送ります。
	 */
	public void requestForSecond()
	{
		log.info("requestForSecond");
		final SakuraScript sakura = new SakuraScript();
		sakura.put("ghost", this);
		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
		service.schedule(new Runnable()
		{
			public void run()
			{
				log.info("5 seconds after");
				String script = shiori.request("OnSecondChange");
//				script = "\\0\\s2ひゃっ\\e";
				sakura.eval(script);
			}
		}, 5, TimeUnit.SECONDS);
	}

	/**
	 * オブザーバーに再描画を依頼します。
	 */
	private void repaint()
	{
		if (observer != null) observer.repaint();
	}

	/**
	 * SHIORI サブシステムをロードします。
	 */
	private void loadShiori()
	{
		try
		{
			File ghostHome = currentNar.getGhostHome();
			Properties defaults = new Properties();
			defaults.setProperty("shiori", "shiori.dll");
			Properties descript = currentNar.getEntry(new File(ghostHome, currentNar.getProperty("ghost.descript"))).readDescript(defaults);
			log.debug("ghost.descript=" + descript);

			// 「shiori」で定義された SHIORI をロード
			String shioriDll = descript.getProperty("shiori");
			log.info("loading SHIORI subsystem: " + shioriDll);
			String shioriClass = System.getProperty("com.mac.tarchan.nanika." + shioriDll);
//			log.debug("SHIORI=" + shioriClass);
			if (shioriClass != null)
			{
				try
				{
					Class<?> cls = Class.forName(shioriClass);
					shiori = (SakuraShiori)cls.newInstance();
				}
				catch (Exception e)
				{
					log.error("shiori not found: " + shioriClass);
				}
			}

			// 未初期化の場合は、デフォルトの SHIORI をロード
			if (shiori == null) shiori = new SakuraShiori();

			shiori.load(currentNar);
			shiori.request("OnBoot");
		}
		catch (IOException e)
		{
			log.error("load shiori error", e);
		}
	}

	/**
	 * バルーンをロードします。
	 */
	private void loadBalloon()
	{
		currentNar.getBalloon();
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
		SakuraShell sakura = new SakuraShell("sakura", currentNar);
		SakuraShell kero = new SakuraShell("kero", sakura);

//		File shellHome = currentNar.getShellHome();
//		NanikaEntry[] png = currentNar.list(new File(shellHome, "surface.+\\.png").getPath());
//		NanikaEntry[] txt = currentNar.list(new File(shellHome, "surface.+\\.txt").getPath());
//		int num = png.length;
//		int max = txt.length;
		log.info(String.format("loading surface %d", sakura.getSurfaceCount()));

		String id = sakura.getId();
		String craftman = sakura.getCraftman();
		log.info(String.format("loading SHELL elements named as \"%s\" crafted by \"%s\"", id, craftman));

//		log.debug("currentNar=" + currentNar);
//		log.debug("shell=" + shell);

		// sakura
		shell.put(0, sakura);
//		shell.get(0).setSurface(0);

		// kero
		shell.put(1, kero);
//		shell.get(1).setSurface(10);
	}

	/**
	 * ゴーストの姿を消します。
	 * 
	 * @return このゴーストへの参照
	 */
	public SakuraGhost close()
	{
		log.info("close");
		nar.clear();
		shell.clear();
		currentNar = null;
		currentShell = null;

		return this;
	}

	/**
	 * ゴーストを消滅させます。
	 * 
	 * @return このゴーストへの参照
	 */
	public SakuraGhost vanish()
	{
		log.info("vanish");
		nar.clear();
		shell.clear();
		currentNar = null;
		currentShell = null;

		return this;
	}

	/**
	 * ゴーストの状態をリセットします。
	 * 
	 * @return このゴーストへの参照
	 */
	public SakuraGhost reset()
	{
		setScope(1).setSurface(10).setBalloonSurface(-1);
		setScope(0).setSurface(0).setBalloonSurface(-1);

		return this;
	}

	/**
	 * スコープを変更します。
	 * 
	 * @param scope スコープ番号
	 * @return このゴーストへの参照
	 */
	public SakuraGhost setScope(int scope)
	{
		log.debug("scope=" + scope);
		currentShell = shell.get(scope);

		return this;
	}

	/**
	 * 現在のスコープのサーフェスを変更します。
	 * 
	 * @param id サーフェス ID
	 * @return このゴーストへの参照
	 */
	public SakuraGhost setSurface(int id)
	{
		log.debug("surface=" + id);
		if (currentShell != null) currentShell.setSurface(id);
		repaint();

		return this;
	}

	/**
	 * 現在のスコープのバルーンサーフェスを変更します。
	 * 
	 * @param id バルーンサーフェス ID
	 * @return このゴーストへの参照
	 */
	public SakuraGhost setBalloonSurface(int id)
	{
		if (currentShell != null)
		{
			SakuraBalloon balloon = currentShell.getBalloon();
			if (balloon != null) balloon.setSurface(id);
		}
		repaint();

		return this;
	}

	/**
	 * 現在のスコープのバルーンにメッセージを表示します。
	 * 
	 * @param message メッセージ
	 * @return このゴーストへの参照
	 */
	public SakuraGhost talk(String message)
	{
		log.debug(message);
		if (currentShell.getBalloon() != null)
		{
			currentShell.getBalloon().drawString(message);
		}
		repaint();

		return this;
	}

	/**
	 * 改行
	 * 
	 * @return このゴーストへの参照
	 */
	public SakuraGhost newLine()
	{
		return this;
	}

	/**
	 * 通常の半分の高さだけ改行
	 * 
	 * @return このゴーストへの参照
	 */
	public SakuraGhost halfLine()
	{
		return this;
	}

	/**
	 * 現在のスコープ表示域をクリアします。
	 * 
	 * @return このゴーストへの参照
	 */
	public SakuraGhost clear()
	{
		if (currentShell.getBalloon() != null)
		{
			currentShell.getBalloon().clearString();
		}
		repaint();

		return this;
	}

	/**
	 * ウエイト
	 * 
	 * @param ms ウエイト時間
	 * @return このゴーストへの参照
	 */
	public SakuraGhost waitTime(long ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch (InterruptedException e)
		{
		}

		return this;
	}

	/**
	 * えんいー
	 * 
	 * @return このゴーストへの参照
	 */
	public SakuraGhost yen_e()
	{
		return this;
	}

	/**
	 * ゴーストを描画します。
	 * 
	 * @param g Graphics2D コンテキスト
	 */
	public void draw(Graphics2D g)
	{
		SakuraShell sakura = shell.get(0);
		SakuraShell kero = shell.get(1);

		Rectangle clip = g.getClipBounds();
		log.debug("clip=" + clip);
		int x = clip.x;
		int y = clip.y;
		int right = x + clip.width;
		int bottom = y + clip.height;

		// サクラ
		if (sakura != null && sakura.getSurface() != null)
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
		if (kero != null && kero.getSurface() != null)
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
