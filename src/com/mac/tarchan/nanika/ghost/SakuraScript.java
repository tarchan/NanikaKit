/*
 * Copyright (c) 2009 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika.ghost;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SakuraScript
 * 
 * @author v-togura
 */
public class SakuraScript
{
	/** ログ */
	private static final Log log = LogFactory.getLog(SakuraScript.class);

	/** さくらスクリプト */
	String script;

	/**
	 * さくらスクリプトを初期化します。
	 * 
	 * @param script さくらスクリプト
	 */
	SakuraScript(String script)
	{
		this.script = script;
	}

	/**
	 * さくらスクリプトを実行します。
	 * 
	 * @param ghost ゴースト
	 */
	public void eval(NanikaGhost ghost)
	{
		log.debug("script=" + script);
		Scanner s = new Scanner(script).useDelimiter("\\\\");
		boolean valid = script.startsWith("\\");
		while (s.hasNext())
		{
			String next = s.next();
			if (!valid)
			{
				// トークのみ
				if (next.length() > 0) ghost.talk(next);
				valid = true;
				continue;
			}

//			Pattern p = Pattern.compile("(_?[0-9a-zA-Z])(?:\\[(.+)\\])?(.*?)");
			Pattern p = Pattern.compile("(_*[0-9a-zA-Z*&!][0-9]?)(?:\\[(.+?)\\])?(.*?)");
			Matcher m = p.matcher(next);
			if (m.matches())
			{
				log.debug(String.format("match=\\%s/%s/%s/%s/%s", m.group(0), m.groupCount(), m.group(1), m.group(2), m.group(3)));
				String command = m.group(1);
				String[] args = m.group(2) != null ? m.group(2).split(",") : null;
				String message = m.group(3);
				// コマンド
				if (command.matches("[0-3]"))
				{
					int scope = Integer.parseInt(command);
					ghost.setScope(scope);
				}
				else if (command.equals("4"))
				{
					// TODO 現スコープのキャラクタが離れる方向に一定距離移動。主に重なり後の強制排除に使用。
				}
				else if (command.equals("5"))
				{
					// TODO 現スコープのキャラクタが接触する距離まで移動。
				}
				else if (command.equals("s"))
				{
					ghost.setSurface(args[0]);
				}
				else if (command.matches("w[0-9]"))
				{
					int n = Integer.parseInt(command.substring(1));
					ghost.waitTime(n);
				}
				else if (command.equals("_w"))
				{
					long n = Long.parseLong(args[0]);
					ghost.waitTimeMillis(n);
				}
				else if (command.equals("n"))
				{
					if (args == null) ghost.crlf();
					else ghost.half();
				}
				else if (command.equals("q"))
				{
					String title = args[0];
					String id = args[1];
					ghost.question(title, id);
				}
				else if (command.equals("!"))
				{
					ghost.system(args);
				}
				else if (command.equals("e"))
				{
					ghost.end();
				}
				else
				{
					log.warn("undef=\\" + m.group(0));
				}
				// トーク
				if (message.length() > 0) ghost.talk(message);
			}
			else
			{
				log.error("next=" + next);
			}
		}

//		Pattern p = Pattern.compile("(\\\\_?[0-9a-zA-Z])(.*?)");
////		Pattern p = Pattern.compile("([01sbnwcxtq*&iv45\\-m!e]|_w|_q|_s|_n|_l|_u|_m|_v|_V)([0-9]|\\[(.+)\\])?(.*)");
//		Scanner s = new Scanner(script);
//		while (true)
//		{
////			String next = s.findInLine("(\\\\_?[a-zA-Z0-9])(.*?)");
//			String next = s.findInLine(p);
//			if (next == null) break;
//
//			 MatchResult m = s.match();
//			log.debug("next=" + next + ", match=" + m.group(0));
//			log.debug("match=" + m + ", count=" + m.groupCount() + ", [0]=" + m.group(1) + ", [1]=" + m.group(2));
//		}
	}

	/**
	 * さくらスクリプトを実行します。
	 * 
	 * @param script さくらスクリプト
	 * @param ghost ゴースト
	 */
	public static void eval(String script, NanikaGhost ghost)
	{
		new SakuraScript(script).eval(ghost);
	}
}
