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

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SerikoAnimation
 *
 * @author tarchan
 * @see <a href="http://usada.sakura.vg/contents/seriko.html">伺か - SERIKO</a>
 */
public class SerikoAnimation
{
	/** ログ */
	private static final Log log = LogFactory.getLog(SerikoAnimation.class);

	/** このアニメーショングループが自動発生する間隔を表します。 */
	public enum Interval
	{
		/** 時々（マバタキ程度）。random,2 と等価です。 */
		sometimes,
		/** さらに低率。random,4 と等価です。 */
		rarely,
		/** 1秒あたり 1/n の確率 */
		random,
		/** 永久にループ */
		always,
		/** そのベースサーフィスに切り替わった瞬間1回だけ */
		runonce,
		/** 自動では発動しない */
		never,
	}

	/** 1コマずつアニメーションパターンを定義する。 */
	public enum Method
	{
		/** オーバーレイによる部分動画 */
		overlay,
		/** 単純な全面切り替えによる全身動画。 */
		base,
		/** 再描画を伴わない全体の移動。surface は変化せず座標だけが動く。 */
		move,
	}

	/** ベースサーフェス */
	protected NanikaSurface surface;

	/** アニメーショングループ ID */
	protected String id;

	/** このアニメーショングループが自動発生する間隔 */
	protected Interval type;

	private List<Map<String, Object>> patterns = new ArrayList<Map<String, Object>>();

	/**
	 * アニメーショングループを設定します。
	 *
	 * @param surface ベースサーフェス
	 * @param id アニメーショングループ ID
	 * @param type このアニメーショングループが自動発生する間隔
	 */
	public SerikoAnimation(NanikaSurface surface, String id, String type)
	{
		this.surface = surface;
		this.id = id;
		this.type = Interval.valueOf(type);
	}

//	/**
//	 * パターンを追加します。
//	 *
//	 * @param pattern パターン
//	 */
//	public void addPattern(String pattern)
//	{
//		String[] split = pattern.split(",");
//		String method = split[0];
//		String surfaceId = split[1];
//		int interval = Integer.parseInt(split[2]);
//		int offsetX = Integer.parseInt(split[3]);
//		int offsetY = Integer.parseInt(split[4]);
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("method", method);
//		map.put("surfaceId", surfaceId);
//		map.put("interval", interval);
//		map.put("offsetX", offsetX);
//		map.put("offsetY", offsetY);
//		patterns.add(map);
//	}

	/**
	 * @param method メソッド
	 * @param surfaceId サーフェスID
	 * @param interval コマ送りスピード
	 * @param offsetX X座標
	 * @param offsetY Y座標
	 */
	public void addPattern(String method, String surfaceId, int interval, int offsetX, int offsetY)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("method", Method.valueOf(method));
		map.put("surfaceId", surfaceId);
		map.put("interval", interval);
		map.put("offsetX", offsetX);
		map.put("offsetY", offsetY);
		patterns.add(map);
	}

	/** インデックス */
	int index = 0;

	/**
	 * アニメーションを進めます。
	 */
	public void next()
	{
		index = (index + 1) % patterns.size();
	}

	/**
	 * アニメーション間隔を返します。
	 *
	 * @return ディレイ
	 */
	public int getDelay()
	{
		Map<String, Object> map = patterns.get(index);
		int interval = (Integer)map.get("interval");
		return interval;
	}

	/**
	 * @param g グラフィックス
	 */
	public void draw(Graphics2D g)
	{
		Map<String, Object> map = patterns.get(index);
		log.debug("[" + index + "]=" + map);
//		index = (index + 1) % patterns.size();

		Method method = (Method)map.get("method");
		String surfaceId = (String)map.get("surfaceId");
		int x = (Integer)map.get("offsetX");
		int y = (Integer)map.get("offsetY");
//		int interval = (Integer)map.get("interval");
		NanikaSurface surface = this.surface.getSurface(surfaceId);
		switch (method)
		{
		case overlay:
			this.surface.drawImage(g, 0, 0);
			surface.drawImage(g, x, y);
			break;
		case base:
			surface.drawImage(g, x, y);
			break;
		case move:
			break;
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		buf.append(super.toString());
		buf.append("[id=");
		buf.append(id);
		buf.append(", type=");
		buf.append(type);
		buf.append(", patterns=");
		buf.append(patterns);
		buf.append("]");
		return buf.toString();
	}
}
