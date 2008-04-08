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
	public Object eval(String script)
	{
		StringBuilder result = new StringBuilder();
		log.info("script=" + script);
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
		if (command.equals("0"))
		{
//			ghost.setScope(0);
			invoke(ghost, "setScope", 0);
		}
		else if (command.equals("1"))
		{
//			ghost.setScope(1);
			invoke(ghost, "setScope", 1);
		}
		else if (command.equals("s"))
		{
			int i = Integer.parseInt(options[1]);
//			ghost.setSurface(i);
			invoke(ghost, "setSurface", i);
		}
		else if (command.equals("b"))
		{
			int i = Integer.parseInt(options[1]);
//			ghost.setBalloonSurface(i);
			invoke(ghost, "setBalloonSurface", i);
		}
		else if (command.equals("n"))
		{
			String s = options[1];
//			if ("half".equals(s)) ghost.halfLine();
//			else ghost.newLine();
			if ("half".equals(s)) invoke(ghost, "halfLine");
			else invoke(ghost, "newLine");
		}
		else if (command.equals("w"))
		{
			long i = Long.parseLong(options[0]);
//			ghost.await(50 * i);
			invoke(ghost, "waitTime", 50 * i);
		}
		else if (command.equals("_w"))
		{
			long i = Long.parseLong(options[1]);
//			ghost.await(i);
			invoke(ghost, "waitTime", i);
		}
		else if (command.equals("c"))
		{
//			ghost.clear();
			invoke(ghost, "clear");
		}
		else if (command.equals("e"))
		{
//			ghost.yen_e();
			invoke(ghost, "yen_e");
		}
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
