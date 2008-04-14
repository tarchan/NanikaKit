/*
 * NanikaEntry.java
 * NanikaKit
 *
 * Created by tarchan on 2008/03/26.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika.nar;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * このクラスは、NAR ファイルエントリを表すために使用します。
 * 
 * @since 1.0
 * @author tarchan
 * @see ZipFile
 * @see ZipEntry
 */
public class NanikaEntry
{
	/** ロガー */
	private static final Log log = LogFactory.getLog(NanikaEntry.class);

	/** ZIP ファイル */
	private ZipFile zip;

	/** ZIP エントリー */
	private ZipEntry entry;

	/**
	 * NanikaEntry を構築します。
	 * 
	 * @param zip ZIP ファイル
	 * @param entry ZIP エントリー
	 */
	NanikaEntry(ZipFile zip, ZipEntry entry)
	{
		if (zip == null) throw new NullPointerException("ZipFile is null");
		if (entry == null) throw new NullPointerException("ZipEntry is null");

		this.zip = zip;
		this.entry = entry;
	}

	/**
	 * エントリー名を返します。
	 * 
	 * @return エントリー名
	 */
	public String getName()
	{
		return entry.getName();
	}

	/**
	 * 入力ストリームを返します。
	 * 
	 * @return 入力ストリーム
	 * @throws IOException 入力エラーが発生した場合
	 */
	public InputStream getInputStream() throws IOException
	{
		return zip.getInputStream(entry);
	}

	/**
	 * プレーンテキスト形式を読み込みます。
	 * 
	 * @return プレーンテキスト
	 * @throws IOException 入力エラーが発生した場合
	 */
	public String asText() throws IOException
	{
		InputStream in = getInputStream();
		String encoding = "Shift_JIS";
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
		int index = 0;
		StringWriter buf = new StringWriter();
		PrintWriter out = new PrintWriter(buf);
		while (true)
		{
			String line = reader.readLine();
			if (line == null) break;

			log.debug(String.format("%2d: %s", ++index, line));
			out.println(line);
		}
		reader.close();

		return buf.toString();
	}

	/**
	 * descript テキスト形式を読み込みます。
	 * 
	 * @param descript 出力先プロパティー
	 * @param encoding 文字エンコーディング
	 * @throws IOException 入力エラーが発生した場合
	 */
	private void loadDescript(Properties descript, String encoding) throws IOException
	{
		InputStream in = getInputStream();
		if (encoding == null) encoding = "Shift_JIS";
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
		int index = 0;
		while (true)
		{
			String line = reader.readLine();
			if (line == null) break;

			String[] token = line.split(" *, *", 2);
			log.debug(String.format("%2d: %s", ++index, Arrays.toString(token)));
			if (token.length == 2)
			{
				String key = token[0];
				String value = token[1];
				descript.setProperty(key, value);
			}
			else
			{
				// skip line
			}
		}
		reader.close();
	}

	/**
	 * descript テキスト形式を読み込みます。
	 * 
	 * @return プロパティーオブジェクト
	 * @throws IOException 入力エラーが発生した場合
	 */
	public Properties asDescript() throws IOException
	{
		Properties descript = new Properties();
		loadDescript(descript, null);
//		log.debug("entry=" + entry.getName());
//		InputStream in = getInputStream();
//		String encoding = "Shift_JIS";
//		BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
//		int index = 0;
//		while (true)
//		{
//			String line = reader.readLine();
//			if (line == null) break;
//
//			String[] token = line.split(" *, *", 2);
//			log.debug(String.format("%2d: %s", ++index, Arrays.toString(token)));
//			if (token.length == 2)
//			{
//				String key = token[0];
//				String value = token[1];
//				descript.setProperty(key, value);
//			}
//			else
//			{
//				// skip line
//			}
//		}

		return descript;
	}

	/**
	 * descript テキスト形式を読み込みます。
	 * 
	 * @param defaults デフォルトプロパティー
	 * @return プロパティーオブジェクト
	 * @throws IOException 入力エラーが発生した場合
	 */
	public Properties asDescript(Properties defaults) throws IOException
	{
		Properties descript = new Properties(defaults);
		loadDescript(descript, null);
		return descript;
	}

	/**
	 * イメージ形式を読み込みます。
	 * 
	 * @return イメージ
	 * @throws IOException 入力エラーが発生した場合
	 */
	public BufferedImage asImage() throws IOException
	{
		BufferedImage image = ImageIO.read(getInputStream());
		return image;
	}

	/**
	 * NAR エントリの文字列表現を返します。
	 * 
	 * @return NAR エントリの文字列表現
	 */
	public String toString()
	{
		return String.format("[entry=%s, file=%s]", entry, zip);
	}
}
