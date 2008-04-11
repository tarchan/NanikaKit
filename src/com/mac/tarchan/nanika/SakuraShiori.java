/*
 * SakuraShiori.java
 * NanikaKit
 *
 * Created by tarchan on 2008/04/03.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika;

import com.mac.tarchan.nanika.nar.NanikaArchive;

/**
 * SHIORI を実装します。
 * 
 * @since 1.0
 * @author tarchan
 */
public class SakuraShiori
{
	/**
	 * SHIORI をロードします。
	 * 
	 * @param nar NAR ファイル
	 * @return ロードできた場合は true、そうでない場合は false
	 */
	public boolean load(NanikaArchive nar)
	{
		return true;
	}

	/**
	 * SHIORI のバージョンを返します。
	 * 
	 * @return バージョン文字列
	 */
	public String getVersion()
	{
		return "SHIORI/3.0";
	}

	/**
	 * さくらスクリプトを返します。
	 * 
	 * @param command コマンド
	 * @return さくらスクリプト
	 */
	public String request(String command)
	{
		return "\\0\\s[0]こんにちは。\\_w[1500]\\1\\s[10]よぉ。\\e";
	}

	/**
	 * SHIORI をアンロードします。
	 * 
	 * @return アンロードできた場合は true、そうでない場合は false
	 */
	public boolean unload()
	{
		return true;
	}
}
