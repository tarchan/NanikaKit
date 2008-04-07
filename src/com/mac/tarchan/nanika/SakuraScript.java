/*
 * SakuraScript.java
 * NanikaKit
 *
 * Created by tarchan on 2008/04/02.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika;

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
	private SakuraGhost ghost;

	/** システム */
	private Object system;

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
			ghost = (SakuraGhost)value;
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
	public Object eval(String script)
	{
		StringBuilder result = new StringBuilder();
		System.out.println("script=" + script);
		Scanner s = new Scanner(script).useDelimiter("\\\\");
		while (s.hasNext())
		{
			String next = s.next();
//			System.out.println("next=" + next);
			Pattern p = Pattern.compile("^([01sbnwcxteq*&iv45m!\\-]|_w|_q|_s|_n|_l|_u|_m|_v|_V)([0-9]|\\[(.+)\\])?(.*)");
			Matcher m = p.matcher(next);
			if (m.find())
			{
//				System.out.format("match=%s/%s/%s/%s/\"%s\"\n", m.group(0), m.group(1), m.group(2), m.group(3), m.group(4));
				call(m.group(1), m.group(2), m.group(3));
				if (m.group(4).length() > 0) talk(m.group(4));
				result.append(m.group(1));
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
	 * コマンドを実行します。
	 * 
	 * @param command コマンド
	 * @param options オプション
	 */
	protected void call(String command, String... options)
	{
		log.debug("call: " + command + "," + java.util.Arrays.toString(options));
	}

	/**
	 * トークを表示します。
	 * 
	 * @param message トーク
	 */
	protected void talk(String message)
	{
		log.debug("talk: " + message);
//		ghost.talk(message);
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
