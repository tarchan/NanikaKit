/*
 * NiseSatori.java
 * NanikaKit
 *
 * Created by tarchan on 2008/04/03.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package test.com.mac.tarchan.nanika;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.nanika.SakuraShiori;
import com.mac.tarchan.nanika.nar.NanikaArchive;
import com.mac.tarchan.nanika.nar.NanikaEntry;

/**
 * 里々を実装します。
 * 
 * @since 1.0
 * @author tarchan
 */
public class NiseSatori extends SakuraShiori
{
	/** ロガー */
	private static final Log log = LogFactory.getLog(NiseSatori.class);

	static
	{
		System.setProperty("com.mac.tarchan.nanika.satori.dll", "test.com.mac.tarchan.nanika.NiseSatori");
	}

	/** トーク辞書 */
	private Map<String, List<String>> talks = new HashMap<String, List<String>>();

	/** 単語辞書 */
	private Map<String, List<String>> words = new HashMap<String, List<String>>();

	/** 区切り文字 */
	Pattern delimiter = Pattern.compile("(＠|＊|＃).*");

	/**
	 * SHIORI をロードします。
	 * 
	 * @param nar NAR ファイル
	 * @return ロードできた場合は true、そうでない場合は false
	 */
	@Override
	public boolean load(NanikaArchive nar)
	{
		log.debug("satori nar: " + nar);

		// dic
		File ghostDir = nar.getGhostDirectory();
		String dic_txt = "dic.+.txt";
		NanikaEntry[] list = nar.list(new File(ghostDir, dic_txt).getPath());
		log.debug("dic*.txt=" + list.length + "," + Arrays.toString(list));
		for (NanikaEntry entry : list)
		{
			loadDic(entry);
		}
//		load_dic(list[0]);

		return true;
	}

	/**
	 * 里々辞書を読み込みます。
	 * 
	 * @param entry 辞書ファイル
	 */
	private void loadDic(NanikaEntry entry)
	{
		log.trace("dic=" + entry.getName());
		try
		{
			String charset = "MS932";
			BufferedReader reader = new BufferedReader(new InputStreamReader(entry.getInputStream(), charset));
			Scanner s = new Scanner(reader);

			String line = s.findInLine(delimiter);
			while (true)
			{
				if (line == null)
				{
					// 辞書の終わり
					break;
				}
				else if (line.startsWith("＊"))
				{
					// トーク
					log.trace("トーク=" + line);
					line = scanTalk(s, line);
				}
				else if (line.startsWith("＠"))
				{
					// 単語
					log.trace("単語=" + line);
					line = scanWord(s, line);
				}
				else
				{
					// コメント
					log.trace("コメント=" + line);
					line = s.findInLine(delimiter);
				}
			}

//			while (s.hasNextLine())
//			{
//				String line = s.nextLine();
////				log.trace("line=" + line);
//				if (line.startsWith("＊"))
//				{
//					scanTalk(s, line);
//				}
//				else if (line.startsWith("＠"))
//				{
//					scanWord(s, line);
//				}
//			}

			reader.close();
		}
		catch (IOException e)
		{
			log.error("load dic error", e);
		}
	}

	/**
	 * トークを読み込みます。
	 * 
	 * @param s スキャナー
	 * @param line 最初の行
	 * @return 次の行
	 */
	private String scanTalk(Scanner s, String line)
	{
		String key = line.substring(1);
		StringBuilder value = new StringBuilder();
		while (true)
		{
			if (s.hasNextLine()) line = s.nextLine();
			else
			{
				line = null;
				break;
			}

			// トークに 1 行追加
			if (delimiter.matcher(line).matches()) break;
			else value.append(line + "\n");
		}

		// トークを登録
		putTalk(key, value.toString());

		return line;
	}

	/**
	 * 単語を読み込みます。
	 * 
	 * @param s スキャナー
	 * @param line 最初の行
	 * @return 次の行
	 */
	private String scanWord(Scanner s, String line)
	{
		String key = line.substring(1);
		while (true)
		{
			if (s.hasNextLine()) line = s.nextLine();
			else
			{
				line = null;
				break;
			}

			// 単語を登録
			if (delimiter.matcher(line).matches()) break;
			else putWord(key, line);
		}

		return line;
	}

	/**
	 * トークを辞書に追加します。
	 * 
	 * @param key キー
	 * @param value トーク
	 */
	private void putTalk(String key, String value)
	{
		log.trace(String.format("\"%s\"=\"%s\"", key , value));
		List<String> list = talks.get(key);
		if (list == null)
		{
			list = new LinkedList<String>();
			talks.put(key, list);
		}
		list.add(value);
	}

	/**
	 * 単語を辞書に追加します。
	 * 
	 * @param key キー
	 * @param value 単語
	 */
	private void putWord(String key, String value)
	{
		if (value == null || value.length() == 0) return;

		log.trace(String.format("\"%s\"=\"%s\"", key , value));
		List<String> list = words.get(key);
		if (list == null)
		{
			list = new LinkedList<String>();
			words.put(key, list);
		}
		list.add(value);
	}

	/** 乱数ジェネレータ */
	private Random rand = new Random();

	/**
	 * トークを返します。
	 * 
	 * @param key キー
	 * @return トーク
	 */
	private String getTalk(String key)
	{
		List<String> list = talks.get(key);
		if (list == null) return null;

		int len = list.size();
		int index = rand.nextInt(len);
		String talk = list.get(index);
		return talk;
	}

	/**
	 * さくらスクリプトを返します。
	 * 
	 * @param command コマンド
	 * @return さくらスクリプト
	 */
	@Override
	public String request(String command)
	{
		log.debug("command: " + command);
//		return "\\0\\s[0]こんにちは。\\1\\s[10]よぉ。\\e";
		String talk = getTalk("");
		return talk;
	}
}
