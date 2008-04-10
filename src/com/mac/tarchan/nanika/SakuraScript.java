/*
 * SakuraScript.java
 * NanikaKit
 *
 * Created by tarchan on 2008/04/02.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * このクラスは、さくらスクリプトを評価する機能を実装します。
 * 
 * @since 1.0
 * @author tarchan
 */
public class SakuraScript
{
	/** ロガー */
	private static final Log log = LogFactory.getLog(SakuraScript.class);

	/** ゴースト */
	protected Object ghost;

	/** システム */
	protected Object system;

	/**
	 * キーにマップする値を設定します。
	 * 
	 * @param name キー
	 * @param value 値
	 * @return このオブジェクトへの参照
	 */
	public Object put(String name, Object value)
	{
		if ("ghost".equals(name))
		{
			ghost = value;
		}
		else if ("system".equals(name))
		{
			system = value;
		}
		else
		{
			throw new IllegalArgumentException(name);
		}

		return this;
	}

	/**
	 * さくらスクリプトを評価して、ゴーストを操作します。
	 * 
	 * @param script さくらスクリプト
	 * @return さくらスクリプトの実行によって返される値
	 */
	public String eval(String script)
	{
		StringBuilder result = new StringBuilder();
		log.info("script=" + script);
		Scanner s = new Scanner(script).useDelimiter("\\\\");
		while (s.hasNext())
		{
			String next = s.next();
//			System.out.println("next=" + next);
			Pattern p = Pattern.compile("^([01sbnwcxteq*&iv45m!\\-]|_w|_q|_s|_n|_l|_u|_m|_v|_V)([0-9]|\\[(.+)\\])?(.+)?");
			Matcher m = p.matcher(next);
			if (m.find())
			{
//				System.out.format("match=%s/%s/%s/%s/\"%s\"\n", m.group(0), m.group(1), m.group(2), m.group(3), m.group(4));
				result.append(m.group(1));

				// えんいーの場合は評価を終了
				if (call(m.group(1), m.group(2), m.group(3))) break;
				if (m.group(4) != null) talk(m.group(4));
			}
			else
			{
//				System.out.println("unmatch=" + next);
				talk(next);
			}
//			MatchResult m = s.match();
//			System.out.format("next=%s (%s)\n", next, m);
		}

		return result.toString();
	}

	/**
	 * さくらスクリプトを実行します。
	 * 
	 * @param op 操作コード
	 * @param value 1 ケタ引数
	 * @param detail 角カッコ引数
	 * @return えんいーの場合は true
	 */
	protected boolean call(String op, String value, String detail)
	{
		log.debug(String.format("call: %s(%s, %s)", op, value, detail));
		if (op.equals("0"))
		{
			invoke(ghost, "setScope", 0);
		}
		else if (op.equals("1"))
		{
			invoke(ghost, "setScope", 1);
		}
		else if (op.equals("s"))
		{
			int i = Integer.parseInt(detail != null ? detail : value);
			invoke(ghost, "setSurface", i);
		}
		else if (op.equals("b"))
		{
			int i = Integer.parseInt(detail);
			invoke(ghost, "setBalloonSurface", i);
		}
		else if (op.equals("n"))
		{
			if ("half".equals(detail)) invoke(ghost, "halfLine");
			else invoke(ghost, "newLine");
		}
		else if (op.equals("w"))
		{
			long i = Long.parseLong(value);
			invoke(ghost, "waitTime", 50 * i);
		}
		else if (op.equals("_w"))
		{
			long i = Long.parseLong(detail);
			invoke(ghost, "waitTime", i);
		}
		else if (op.equals("c"))
		{
			// 表示域クリア
			invoke(ghost, "clear");
		}
		else if (op.equals("x"))
		{
			// 表示一時停止、クリック待ち。
			invoke(ghost, "waitClick");
		}
		else if (op.equals("t"))
		{
			// タイムクリティカルセクション
			invoke(ghost, "timeSession");
		}
		else if (op.equals("_q"))
		{
			// クイックセクション
			invoke(ghost, "quickSession");
		}
		else if (op.equals("_s"))
		{
			// シンクロナイズセクション
			invoke(ghost, "syncSession");
		}
		else if (op.equals("_n"))
		{
			// 自動改行のスイッチ
			invoke(ghost, "autoLine");
		}
		else if (op.equals("_l"))
		{
			// カーソル位置の絶対指定
			String[] loc = detail.split(",");
			int x = Integer.parseInt(loc[0]);
			int y = Integer.parseInt(loc[1]);
			invoke(ghost, "setCursorLocation", x, y);
		}
		else if (op.equals("e"))
		{
			// えんいー
			return true;
		}
		else if (op.equals("_v"))
		{
			// オーディオファイルを再生
			invoke(system, "playSound", detail);
		}
		else if (op.equals("_V"))
		{
			// オーディオファイルの再生終了を待つ
			invoke(system, "waitSound");
		}
		else if (op.equals("i"))
		{
			// アニメーションパターンを発動
			long i = Long.parseLong(detail);
			invoke(ghost, "startAnimation", i);
		}
		else if (op.equals("v"))
		{
			// フォアグラウンドへ
			invoke(ghost, "toFront");
		}
		else if (op.equals("4"))
		{
			// 離れる方向に移動
			invoke(ghost, "on4");
		}
		else if (op.equals("5"))
		{
			// 接触する方向に移動
			invoke(ghost, "on5");
		}
		else if (op.equals("-"))
		{
			// 即終了
			invoke(ghost, "vanish");
			return true;
		}
		else if (op.equals("!"))
		{
			// open, set, raise, change ...
			String[] args = detail.split(",");
			invoke(system, args[0]);
		}
		else
		{
			invoke(ghost, op, value, detail);
		}

		return false;
	}

	/**
	 * トークを表示します。
	 * 
	 * @param message トーク
	 */
	protected void talk(String message)
	{
//		log.debug("talk: " + message);
//		if (ghost != null) ghost.talk(message);
		invoke(ghost, "talk", message);
	}

	/**
	 * ターゲットのメソッドを実行します。
	 * 
	 * @param target ターゲット
	 * @param name メソッド名
	 * @param args 引数
	 */
	protected void invoke(Object target, String name, Object... args)
	{
		try
		{
			log.debug(String.format("%s%s", name, Arrays.toString(args)));
			Class<?>[] cls;
			if (args != null)
			{
				cls = new Class[args.length];
				int i = 0;
				for (Object obj : args)
				{
					Class<?> c;
					if (obj instanceof Integer) c = int.class;
					else if (obj instanceof Long) c = long.class;
					else c = obj.getClass();
					cls[i++] = c;
				}
			}
			else
			{
				cls = null;
			}
			Method method = target.getClass().getMethod(name, cls);
			method.invoke(target, args);
		}
		catch (NoSuchMethodException e)
		{
			log.trace(e.toString());
		}
		catch (Exception e)
		{
			log.error("invoke method error", e);
		}
	}

	/**
	 * さくらスクリプトの文字列表現を返します。
	 * 
	 * @return さくらスクリプトの文字列表現
	 */
	public String toString()
	{
		return String.format("ghost=%s, system=%s", ghost, system);
	}
}
