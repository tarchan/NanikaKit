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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SerikoTimer
 *
 * @author tarchan
 */
public class SerikoTimer extends Timer
{
	/** シリアルバージョンID */
	private static final long serialVersionUID = -3394772876027715038L;

	/** ログ */
	private static final Log log = LogFactory.getLog(SerikoTimer.class);

	/** 初期遅延とイベント間遅延を表す値 (ミリ秒) */
	protected int delay;

	/** アニメーション定義 */
	protected SerikoAnimation animation;

	/**
	 * SERIKO アニメーションのタイマーを初期化します。
	 *
	 * @param delay 初期遅延とイベント間遅延を表す値 (ミリ秒)
	 * @param listener 初期のリスナー。null の場合もあり
	 */
	public SerikoTimer(int delay, ActionListener listener)
	{
		super(delay, listener);
		this.delay = delay;
	}

	/**
	 * SERIKO アニメーションのタイマーを初期化します。
	 *
	 * @param animation アニメーション定義
	 * @param listener 初期のリスナー。null の場合もあり
	 */
	public SerikoTimer(SerikoAnimation animation, ActionListener listener)
	{
		super(0, listener);
		this.animation = animation;
		this.delay = animation.getDelay();
	}

	/**
	 * @see javax.swing.Timer#fireActionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	protected void fireActionPerformed(ActionEvent e)
	{
		if (animation != null)
		{
			animation.next();
			delay = animation.getDelay();
		}
		log.debug(toString());
		super.fireActionPerformed(e);
	}

	/**
	 * @see javax.swing.Timer#getDelay()
	 */
	@Override
	public int getDelay()
	{
		log.debug(toString());
		return delay;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder(super.toString());
		buf.append("[");
		buf.append("delay=");
		buf.append(delay);
		buf.append(", repeat=");
		buf.append(isRepeats());
		buf.append(", coalesce=");
		buf.append(isCoalesce());
		buf.append("]");
		return buf.toString();
	}
}
