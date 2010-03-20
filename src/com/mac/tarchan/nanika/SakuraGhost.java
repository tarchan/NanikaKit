/*
 * SakuraGhost.java
 * NanikaKit
 *
 * Created by tarchan on 2008/03/29.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.event.MouseTracker;
import com.mac.tarchan.nanika.nar.NanikaArchive;

/**
 * このクラスは、ゴーストを実装します。
 *
 * @since 1.0
 * @author tarchan
 */
public class SakuraGhost
{
	/** ログ */
	private static final Log log = LogFactory.getLog(SakuraGhost.class);

//	/** アーカイブ */
//	private LinkedHashMap<String, NanikaArchive> nar = new LinkedHashMap<String, NanikaArchive>();

	/** シェル */
	private LinkedHashMap<Integer, SakuraShell> shells = new LinkedHashMap<Integer, SakuraShell>();

	/** NAR ファイル */
	private NanikaArchive nar;

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
		log.info("install: " + newNar);
		if (nar == null) nar = newNar;
		else nar.setNext(newNar);

//		nar.put(name, newNar);
//		log.debug("nar=" + newNar);
//		Properties props = nar.get(name).getProperties();
//		log.debug("props=" + props);

		return this;
	}

	/**
	 * ゴーストの名前を返します。
	 *
	 * @return ゴーストの名前
	 */
	public String getName()
	{
		return nar.forType("ghost").getProperty("name");
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
	protected void requestForSecond()
	{
		log.info("requestForSecond");

		final SakuraScript sakura = new SakuraScript();
		sakura.put("ghost", this);
		sakura.put("system", this);

		final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

		// ポップアップメニュー
		final JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(new AbstractAction("えんいー")
		{
			 /** シリアルバージョンID */
			private static final long serialVersionUID = 8146721621170583261L;

			public void actionPerformed(ActionEvent actionevent)
			{
				String script = "\\-";
				GhostRunner runner = new GhostRunner(sakura, script);
				service.execute(runner);
			}
		});

		MouseTracker clicker = new MouseTracker()
		{
			/**
			 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
			 */
			@Override
			public void mousePressed(MouseEvent mouseevent)
			{
				popupNow(mouseevent);
			}

			/**
			 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseReleased(MouseEvent mouseevent)
			{
				popupNow(mouseevent);
			}

			/**
			 * @see com.mac.tarchan.event.MouseTracker#mouseClicked(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseClicked(MouseEvent mouseevent)
			{
				if (popupMenu.isVisible()) return;

				// 当たり判定
				hit(mouseevent.getPoint());

//				reset();
				String script = shiori.request("OnMouseClick");
//				sakura.eval(script);
				GhostRunner runner = new GhostRunner(sakura, script);
				service.execute(runner);
			}

			/**
			 * @see com.mac.tarchan.event.MouseTracker#mouseWheelMoved(java.awt.event.MouseWheelEvent)
			 */
			@Override
			public void mouseWheelMoved(MouseWheelEvent mousewheelevent)
			{
//				reset();
				String script = shiori.request("OnMouseWheel");
//				sakura.eval(script);
				GhostRunner runner = new GhostRunner(sakura, script);
				service.execute(runner);
			}

			private void popupNow(MouseEvent mouseevent)
			{
				if (mouseevent.isPopupTrigger())
				{
					popupMenu.show(mouseevent.getComponent(), mouseevent.getX(), mouseevent.getY());
				}
			}
		};
		observer.addMouseListener(clicker);
		observer.addMouseWheelListener(clicker);

		String script = shiori.request("OnBoot");
//		sakura.eval(script);
		GhostRunner runner = new GhostRunner(sakura, script);
		service.execute(runner);

		service.schedule(new Runnable()
		{
			public void run()
			{
//				log.info("5 seconds after");
//				String script = shiori.request("OnSecondChange");
////				script = "\\0\\s2ひゃっ\\e";
//				sakura.eval(script);
			}
		}, 5, TimeUnit.SECONDS);
	}

	/**
	 * さくらスクリプトを実行します。
	 *
	 * @since 1.0
	 * @author tarchan
	 */
	class GhostRunner implements Runnable
	{
		/** スクリプトエンジン */
		private SakuraScript sakura;

		/** さくらスクリプト */
		private String script;

		/**
		 * さくらスクリプト実行オブジェクトを構築します。
		 *
		 * @param sakura スクリプトエンジン
		 * @param script さくらスクリプト
		 */
		public GhostRunner(SakuraScript sakura, String script)
		{
			this.sakura = sakura;
			this.script = script;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run()
		{
			reset();
			sakura.eval(script);
		}
	}

	/**
	 * 再描画を依頼します。
	 */
	protected void repaint()
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
			NanikaArchive nar = this.nar.forType("ghost");
			log.debug("load shiori from: " + nar);
			if (nar == null) return;

			File ghostDir = nar.getGhostDirectory();
			Properties defaults = new Properties();
			defaults.setProperty("shiori", "shiori.dll");
			Properties descript = nar.getEntry(new File(ghostDir, nar.getProperty("ghost.descript"))).asDescript(defaults);
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

			shiori.load(nar);
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
		SakuraBalloon balloon = nar.getBalloon("sakura");
		if (balloon == null) return;

		String name = balloon.getName();
		String craftman = balloon.getCraftman();
		log.info(String.format("loading Balloon named as \"%s\" crafted by \"%s\"", name, craftman));
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
		SakuraShell sakura = new SakuraShell("sakura", nar);
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
		shells.put(0, sakura);
//		shell.get(0).setSurface(0);

		// kero
		shells.put(1, kero);
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
		shells.clear();
		nar = null;
		currentShell = null;

		// 終了
		throw new RuntimeException("ghost closed");
	}

	/**
	 * ゴーストを消滅させます。
	 *
	 * @return このゴーストへの参照
	 */
	public SakuraGhost vanish()
	{
		log.info("vanish");
		shells.clear();
		nar = null;
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
//		setScope(1).setSurface(10).setBalloonSurface(-1);
//		setScope(0).setSurface(0).setBalloonSurface(-1);
		setScope(1).setSurface(10).clear();
		setScope(0).setSurface(0).clear();

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
		currentShell = shells.get(scope);

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
	 * ウエイト
	 *
	 * @param ms ウエイト時間
	 * @return このゴーストへの参照
	 */
	public SakuraGhost waitTime(final long ms)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					Thread.sleep(ms);
				}
				catch (InterruptedException e)
				{
					log.error("sleep error: " + ms, e);
				}
			}
		});

		return this;
	}

	/** トークバッファ */
	private StringBuilder talk = new StringBuilder();

	/**
	 * トーク中かどうか判定します。
	 *
	 * @return トーク中の場合は true
	 */
	public boolean isTalking()
	{
		synchronized(talk)
		{
			return talk.length() > 0;
		}
	}

	/**
	 * 現在のスコープのバルーンにメッセージを表示します。
	 *
	 * @param message メッセージ
	 * @return このゴーストへの参照
	 */
	public SakuraGhost talk(String message)
	{
//		log.debug(message);
//		if (currentShell.getBalloon() != null)
//		{
//			currentShell.getBalloon().append(message);
//		}
		synchronized(talk)
		{
			talk.append(message);
		}
		repaint();
		while (isTalking())
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
			}
		}

		return this;
	}

	/**
	 * 改行
	 *
	 * @return このゴーストへの参照
	 */
	public SakuraGhost newLine()
	{
		if (currentShell.getBalloon() != null)
		{
			currentShell.getBalloon().append("\n");
		}
		repaint();

		return this;
	}

	/**
	 * 通常の半分の高さだけ改行
	 *
	 * @return このゴーストへの参照
	 */
	public SakuraGhost halfLine()
	{
		if (currentShell.getBalloon() != null)
		{
			currentShell.getBalloon().append("\n");
		}
		repaint();

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
			currentShell.getBalloon().clear();
		}
		repaint();

		return this;
	}

	/**
	 * ゴーストを描画します。
	 *
	 * @param g Graphics2D コンテキスト
	 */
	public void draw(Graphics2D g)
	{
		SakuraShell sakura = shells.get(0);
		SakuraShell kero = shells.get(1);

		Rectangle clip = g.getClipBounds();
//		log.debug("clip=" + clip);
		int x = clip.x;
		int y = clip.y;
		int right = x + clip.width;
		int bottom = y + clip.height;

		// サクラ
		if (sakura != null && sakura.getSurface() != null)
		{
//			AffineTransform tx = new AffineTransform();
//			tx.scale(0.8, 0.8);
//			Rectangle rect = sakura.getBounds();
//			Rectangle rect = tx.createTransformedShape(sakura.getSurface()).getBounds();
			Rectangle rect = sakura.getSurface().getBounds();
//			log.debug("rect=" + rect);
			rect.x = right - rect.width;
			rect.y = bottom - rect.height;
//			tx.shear(-0.5, 0);
//			tx.rotate(Math.toRadians(0), right - rect.width / 2, bottom);
//			tx.translate(rect.x, rect.y);
//			g.setTransform(tx);
			sakura.setLocation(rect.getLocation());
			sakura.draw(g);
			right = rect.x;
		}

		// ケロ
		if (kero != null && kero.getSurface() != null)
		{
//			AffineTransform tx = new AffineTransform();
			Rectangle rect = kero.getSurface().getBounds();
//			rect.x = x + (right - x) / 2 - rect.width / 2;
			rect.x = right - rect.width;
			rect.y = bottom - rect.height;
//			tx.translate(rect.x, rect.y);
//			tx.shear(0.5, 0);
//			g.setTransform(tx);
			kero.setLocation(rect.getLocation());
			kero.draw(g);
		}

//		observer.getToolkit().sync();

		if (isTalking())
		{
			synchronized(talk)
			{
				String ch = talk.substring(0, 1);
				talk = new StringBuilder(talk.substring(1));
				if (currentShell.getBalloon() != null)
				{
					currentShell.getBalloon().append(ch);
				}
			}

			repaint();
		}
	}

	/**
	 * 当たり判定を確認します。
	 *
	 * @param p クリック位置
	 */
	public void hit(Point p)
	{
		System.out.println("click: " + p);
		log.debug("hit: " + p + ", shell: " + shells);
		for (Map.Entry<Integer, SakuraShell> entry : shells.entrySet())
		{
			int id = entry.getKey();
			SakuraShell s = entry.getValue();
			String name = s.hit(p);
			if (name != null) System.out.println("hit! " + name);
			log.debug("id: " + id + ", shell: " + s);
		}
	}

	/**
	 * サムネールを返します。
	 *
	 * @return サムネール
	 */
	public BufferedImage getThumbnail()
	{
		return nar.getThumbnail();
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
