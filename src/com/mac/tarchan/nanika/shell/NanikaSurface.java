/*
 *  Copyright (c) 2009 tarchan. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY TARCHAN ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 *  EVENT SHALL TARCHAN OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 *  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are
 *  those of the authors and should not be interpreted as representing official
 *  policies, either expressed or implied, of tarchan.
 */
package com.mac.tarchan.nanika.shell;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.image.ChromakeyImageFilter;

/**
 * NanikaSurface
 *
 * @author tarchan
 */
public class NanikaSurface extends Canvas
{
	/** シリアルバージョンID */
	private static final long serialVersionUID = 3959050048154932702L;

	/** ログ */
	private static final Log log = LogFactory.getLog(NanikaSurface.class);

	/** シェル */
	protected NanikaShell shell;

	/** ID */
	protected String id;

	/** イメージ */
	protected BufferedImage image;

	/** 定義 */
	protected String descript;

	/** 当たり判定 */
	protected Map<String, SerikoCollision> collisions = new LinkedHashMap<String, SerikoCollision>();

	/** アニメーション、着せ替え */
	protected Map<String, SerikoAnimation> animations = new LinkedHashMap<String, SerikoAnimation>();

	/** X 座標 */
	public int x;

	/** Y 座標 */
	public int y;


//	/**
//	 * サーフェスを初期化します。
//	 *
//	 * @param id サーフェス ID
//	 * @param in 入力ストリーム
//	 * @throws IOException イメージを読み込めない場合
//	 */
//	public NanikaSurface(String id, InputStream in) throws IOException
//	{
//		this.id = id;
//		setImage(ImageIO.read(in));
//	}

	/**
	 * サーフェスを初期化します。
	 *
	 * @param shell シェル
	 * @param id サーフェス ID
	 */
	public NanikaSurface(NanikaShell shell, String id)
	{
		this.shell = shell;
		this.id = id;
		String name = String.format("surface%s.png", id);
		BufferedImage image = shell.readImage(name);
		setImage(image);
	}

	/** 描画用イメージ */
	Image img;

	/**
	 * イメージを設定します。
	 *
	 * @param image イメージ
	 */
	public void setImage(BufferedImage image)
	{
		this.image = image;
		setSize(image.getWidth(), image.getHeight());
		Toolkit tk = Toolkit.getDefaultToolkit();
		ChromakeyImageFilter chromakey = new ChromakeyImageFilter(getBackground().getRGB());
		img = tk.createImage(new FilteredImageSource(image.getSource(), chromakey));
	}

	/**
	 * 定義を設定します。
	 *
	 * @param descript 定義
	 */
	public void setDescript(String descript)
	{
		this.descript = descript;
		try
		{
			BufferedReader r = new BufferedReader(new StringReader(descript));
			while (true)
			{
				String line = r.readLine();
				if (line == null) break;

				if (line.length() == 0) continue;

				String[] split = line.split(",");
				String id = split[0];
				if (id.startsWith("collision"))
				{
					int x = Integer.parseInt(split[1]);
					int y = Integer.parseInt(split[2]);
					int w = Integer.parseInt(split[3]);
					int h = Integer.parseInt(split[4]);
					String name = split[5];
					SerikoCollision collision = new SerikoCollision(x, y, w, h, name);
					collisions.put(id, collision);
					log.debug("collision id=" + id + ", " + collision);
				}
				else if (id.matches(".+interval"))
				{
					Matcher m = Pattern.compile("(.+)interval").matcher(id);
					m.matches();
					id = m.group(1);
					String type = split[1];
					SerikoAnimation animation = new SerikoAnimation(this, id, type);
					animations.put(id, animation);
					log.debug("interval id=" + id + ", " + animation);
				}
				else if (id.matches(".+pattern.+"))
				{
					Matcher m = Pattern.compile("(.+)pattern(.+)").matcher(id);
					m.matches();
					id = m.group(1);
//					String index = m.group(2);
					String method;
					String surfaceId;
					int interval;
					if (split[3].matches("[0-9]+"))
					{
						method = split[1];
						surfaceId = split[2];
						interval = Integer.parseInt(split[3]);
					}
					else
					{
						surfaceId = split[1];
						interval = Integer.parseInt(split[2]);
						method = split[3];
					}
					int offsetX = Integer.parseInt(split[4]);
					int offsetY = Integer.parseInt(split[5]);
					SerikoAnimation animation = animations.get(id);
					animation.addPattern(method, surfaceId, interval, offsetX, offsetY);
					log.debug("pattern id=" + id + ", " + animation + ", method=" + method);
				}
				else
				{
					log.debug("unknown id=" + id);
				}
			}
		}
		catch (IOException x)
		{
			log.error("不正な定義です。", x);
		}
	}

	/**
	 * このサーフェスの ID を返します。
	 *
	 * @return サーフェス ID
	 */
	public String getID()
	{
		return id;
	}

	/**
	 * アニメーションかどうか判定します。
	 *
	 * @return アニメーションの場合は true
	 */
	public boolean isAnimations()
	{
		return animations.size() > 0;
	}

	/**
	 * アニメーション定義のコレクションを返します。
	 *
	 * @return アニメーション定義
	 */
	public Collection<SerikoAnimation> getAnimations()
	{
		return animations.values();
	}

	/**
	 * 指定された座標の当たり判定をします。
	 *
	 * @param x X座標
	 * @param y Y座標
	 * @return 当たりの場合は当たった箇所の名前。そうでない場合は null
	 */
	public String hit(int x, int y)
	{
		return null;
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
	 * サーフェスの幅を返します。
	 *
	 * @return サーフェスの幅
	 */
	public int getWidth()
	{
		return image.getWidth();
	}

	/**
	 * サーフェスの高さを返します。
	 *
	 * @return サーフェスの高さ
	 */
	public int getHeight()
	{
		return image.getHeight();
	}

	/**
	 * イメージを描画します。
	 *
	 * @param g グラフィックス
	 * @param x X座標
	 * @param y Y座標
	 */
	protected void drawImage(Graphics2D g, int x, int y)
	{
//		Toolkit tk = Toolkit.getDefaultToolkit();
//		ChromakeyImageFilter chromakey = new ChromakeyImageFilter(getBackground().getRGB());
//		Image img = tk.createImage(new FilteredImageSource(image.getSource(), chromakey));
		g.drawImage(img, x, y, null);
	}

	/**
	 * イメージを描画します。
	 *
	 * @param g グラフィックス
	 */
	public void draw(Graphics2D g)
	{
		if (log.isTraceEnabled()) log.debug("id=" + id + ", size=" + getSize());
//		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
//		g.setBackground(new Color(0, true));
//		g.clearRect(0, 0, getWidth(), getHeight());
		if (animations.isEmpty())
		{
			drawImage(g, x, y);
//			Toolkit tk = Toolkit.getDefaultToolkit();
//			ChromakeyImageFilter chromakey = new ChromakeyImageFilter(getBackground().getRGB());
//			Image img = tk.createImage(new FilteredImageSource(image.getSource(), chromakey));
//			g.drawImage(img, x, y, null);
		}
		else
		{
			for (SerikoAnimation animation : animations.values())
			{
				animation.draw(g);
			}
//			new Thread(new Runnable()
//			{
//				@Override
//				public void run()
//				{
//					try
//					{
//						log.debug("sleep");
//						Thread.sleep(90);
//						log.debug("wakeup");
////						repaint(90);
////						invalidate();
////						validate();
//						repaint();
//					}
//					catch (InterruptedException x)
//					{
//						// 何もしない
//					}
//				}
//			}).start();
		}

		if (log.isDebugEnabled())
		{
			g.setPaint(Color.RED);
			g.drawRect(x, y, getWidth() - 1, getHeight() - 1);
			if (descript != null)
			{
				g.fillRect(0, 0, 8, 8);
			}
			for (SerikoCollision collision : collisions.values())
			{
				Rectangle clip = collision.getClip();
				g.draw(clip);
				g.drawString(collision.getName(), clip.x + 1, clip.y + 12);
			}
		}
	}

	/**
	 * @see java.awt.Canvas#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g)
	{
		draw((Graphics2D)g);
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		buf.append(super.toString());
		buf.append("[surface id=");
		buf.append(id);
		buf.append("]");
		return buf.toString();
	}

	/**
	 * 指定された ID のサーフェスを返します。
	 *
	 * @param id サーフェス ID
	 * @return サーフェス
	 * @see NanikaShell#getSurface(String)
	 */
	public NanikaSurface getSurface(String id)
	{
		return shell.getSurface(id);
	}
}
