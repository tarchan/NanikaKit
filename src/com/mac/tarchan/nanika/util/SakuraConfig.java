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
package com.mac.tarchan.nanika.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SakuraConfig
 *
 * @author tarchan
 */
public class SakuraConfig extends Properties
{
	/** シリアルバージョンID */
	private static final long serialVersionUID = -1622755309805589864L;

	/** ログ */
	private static final Log log = LogFactory.getLog(SakuraConfig.class);

	/**
	 * 設定ファイルを読み込みます。
	 *
	 * @param in 入力ストリーム
	 * @throws IOException 読み込めない場合
	 */
	public SakuraConfig(InputStream in) throws IOException
	{
		this(in, null);
	}

	/**
	 * 設定ファイルを読み込みます。
	 *
	 * @param in 入力ストリーム
	 * @param charset 文字コード
	 * @throws IOException 読み込めない場合
	 */
	public SakuraConfig(InputStream in, String charset) throws IOException
	{
		in = new BufferedInputStream(in);
		in.mark(0);
		load(in, charset);
	}

	/**
	 * 指定された文字コードで読み込みます。
	 *
	 * @param in 入力ストリーム
	 * @param charset 文字コード
	 * @throws IOException 読み込めない場合
	 */
	protected void load(InputStream in, String charset) throws IOException
	{
		BufferedReader reader;
		if (charset != null)
		{
			reader = new BufferedReader(new InputStreamReader(in, charset));
		}
		else
		{
			reader = new BufferedReader(new InputStreamReader(in));
		}

		while (true)
		{
			// 1行読み込み
			String line = reader.readLine();
			if (line == null) break;
			if (log.isTraceEnabled()) log.debug("line=" + line);

			// キーと値に分割
			String[] split = line.split(" *, *", 2);
			if (split.length < 2) continue;
			String key = split[0];
			String value = split[1];

			// 文字コードが指定されたら再ロード
			if (charset == null && key.equals("charset"))
			{
				in.reset();
				load(in, value);
				return;
			}

			// 設定を追加
			if (log.isTraceEnabled()) log.debug("put: " + key + "=" + value);
			put(key, value);
		}
		reader.close();
	}
}
