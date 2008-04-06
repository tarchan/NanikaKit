/*
 * SakuraScript.java
 * NanikaKit
 *
 * Created by tarchan on 2008/04/02.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika;

import java.util.Scanner;

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

	/**
	 * さくらスクリプトを評価して、ゴーストを操作します。
	 * 
	 * @param script さくらスクリプト
	 * @param ghost さくらスクリプトの実行によって操作されるゴースト
	 * @return さくらスクリプトの実行によって返される値
	 */
	public static Object eval(String script, SakuraGhost ghost)
	{
		Scanner s = new Scanner(script).useDelimiter("\\\\");
		while (s.hasNext())
		{
			String next = s.next();
			log.debug("next=" + next);
		}
		return null;
	}
}
