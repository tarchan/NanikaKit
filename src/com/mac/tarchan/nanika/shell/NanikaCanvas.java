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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * NanikaScope
 *
 * @author tarchan
 */
public class NanikaCanvas extends JPanel
{
	/** シリアルバージョンID */
	private static final long serialVersionUID = -8122309255321836447L;

	/** ログ */
	private static final Log log = LogFactory.getLog(NanikaCanvas.class);

	/** シェル */
	protected NanikaShell shell;

	/** サーフェス */
	protected NanikaSurface surface;

	/**
	 * シェルを設定します。
	 *
	 * @param shell シェル
	 */
	public void setShell(NanikaShell shell)
	{
		this.shell = shell;
	}

	/**
	 * 指定された ID のサーフェスに切り替えます。
	 *
	 * @param id サーフェス ID
	 */
	public void setSurface(String id)
	{
		log.debug("id=" + id);
		surface = shell.getSurface(id);
		if (surface != null)
		{
			setSize(surface.getSize());
//			if (surface.isAnimations())
//			for (SerikoAnimation animation : surface.getAnimations())
//			{
////				new SerikoTimer(90, new ActionListener()
//				new SerikoTimer(animation, new ActionListener()
//				{
//					@Override
//					public void actionPerformed(ActionEvent e)
//					{
//						if (log.isTraceEnabled()) log.debug("repaint=" + surface.getID());
//						repaint();
//					}
//				}).start();
//			}
		}
	}


	/**
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize()
	{
		return surface != null ? surface.getSize() : null;
	}

	/**
	 * @see javax.swing.JComponent#getMaximumSize()
	 */
	@Override
	public Dimension getMaximumSize()
	{
		return getPreferredSize();
	}

	/**
	 * @see javax.swing.JComponent#getMinimumSize()
	 */
	@Override
	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g)
//	public void paint(Graphics g)
	{
		if (surface == null) return;

//		g.clearRect(0, 0, getWidth(), getHeight());
		if (surface != null) surface.draw((Graphics2D)g);
//		new Thread(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				try
//				{
//					if (log.isTraceEnabled()) log.debug("sleep");
//					Thread.sleep(90);
//					if (log.isTraceEnabled()) log.debug("wakeup");
////					repaint(90);
////					invalidate();
////					validate();
//					repaint();
//				}
//				catch (InterruptedException x)
//				{
//					// 何もしない
//				}
//			}
//		}).start();
	}

	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder(super.toString());
		buf.append("[");
		buf.append("surface=");
		buf.append(surface);
		buf.append("]");
		return buf.toString();
	}
}
