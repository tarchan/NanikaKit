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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * NarFile
 *
 * @author tarchan
 * @see <a href="http://usada.sakura.vg/contents/install.html">伺か - インストーラ</a>
 */
public class NarFile
{
	/** ログ */
	private static final Log log = LogFactory.getLog(NarFile.class);

	/** install.txt */
	public static final String INSTALL = "install.txt";

	/** descript.txt */
	public static final String DESCRIPT = "descript.txt";

	/** タイプ */
	public enum Type
	{
		/** ゴースト */
		ghost,

		/** バルーン */
		balloon,

		/** ヘッドラインセンサ */
		headline,

		/** プラグイン */
		plugin,

		/** 追加オブジェクト */
		supplement,

		/** シェル */
		shell,
	}

	/** ソース */
	protected Object source;

	/** ベース名 */
	protected String base;

	/** 設定 */
	protected SakuraConfig config;

	/**
	 * 指定された ZIP ファイルを読み込みます。
	 *
	 * @param zip ZIP ファイル
	 * @throws IOException 読み込めない場合
	 */
	protected NarFile(ZipFile zip) throws IOException
	{
		source = zip;
//		base = "";
		ZipEntry install = zip.getEntry(INSTALL);
		config = new SakuraConfig(zip.getInputStream(install), "Shift_JIS");
		log.debug("config=" + config);
	}

	/**
	 * 指定された ZIP エントリーを読み込みます。
	 *
	 * @param nar NAR ファイル
	 * @param base ベースディレクトリ
	 * @throws IOException 読み込めない場合
	 */
	protected NarFile(NarFile nar, String base) throws IOException
	{
		ZipFile zip = (ZipFile)nar.source;
		source = zip;
		log.info("base=" + base);
		this.base = base;
		String path = combinePath(base, DESCRIPT);
		ZipEntry descript = zip.getEntry(path);
		if (descript == null) throw new FileNotFoundException(DESCRIPT + " が見つかりません。: " + path);
		config = new SakuraConfig(zip.getInputStream(descript), nar.getCharset());
		config.setProperty("charset", nar.getCharset());
		log.debug("config=" + config);
	}

	/**
	 * 指定されたディレクトリを読み込みます。
	 *
	 * @param dir ディレクトリ
	 * @throws IOException 読み込めない場合
	 */
	protected NarFile(File dir) throws IOException
	{
		source = dir;
		File install = new File(dir, INSTALL);
		if (install.exists())
		{
			config = new SakuraConfig(new FileInputStream(install), "Shift_JIS");
		}
		else
		{
			File descript = new File(dir, DESCRIPT);
			if (descript.exists())
			{
				config = new SakuraConfig(new FileInputStream(descript), "Shift_JIS");
				// TODO 文字コード指定
//				config.setProperty("charset", "Shift_JIS");
			}
		}
		log.debug("config=" + config);
	}

	/**
	 * .nar ファイルをロードします。
	 *
	 * @param path パス名
	 * @return NarFile
	 * @throws IOException ファイルをロードできない場合
	 */
	public static NarFile load(String path) throws IOException
	{
		NarFile nar;
		File file = new File(path);
		log.debug("file=" + file);
		if (file.isFile())
		{
			try
			{
				nar = new NarFile(new ZipFile(file));
			}
			catch (ZipException x)
			{
				throw new IOException(x);
			}
		}
		else
		{
			nar = new NarFile(file);
		}

		return nar;
	}

	/**
	 * 2つのパスを結合します。
	 * 
	 * @param parent 親パス
	 * @param child 子パス
	 * @return 結合したパス
	 */
	protected static String combinePath(String parent, String child)
	{
//		if (parent != null) parent = parent.replaceAll("\\\\", "/");
		return parent != null ? parent + "/" + child : child;
	}

	/**
	 * このファイルセットがアーカイブファイルかどうか判定します。
	 *
	 * @return アーカイブファイルの場合は true
	 */
	public boolean isNar()
	{
		return source instanceof ZipFile;
	}

	/**
	 * 入力ストリームを返します。
	 *
	 * @param name エントリー名
	 * @return 入力ストリーム
	 * @throws IOException 入力ストリームを取得できない場合
	 */
	public InputStream getInputStream(String name) throws IOException
	{
		if (isNar())
		{
			ZipFile zip = (ZipFile)source;
			name = combinePath(base, name);
//			log.debug("name=" + name);
			try
			{
				ZipEntry entry = zip.getEntry(name);
				if (entry == null) throw new FileNotFoundException("ファイルが見つかりません。: " + name);
				return zip.getInputStream(entry);
			}
			catch (IOException x)
			{
				throw new IOException("Not Found " + name, x);
			}
		}
		else
		{
			File file = new File((File)source, name);
			if (!file.exists()) throw new FileNotFoundException("ファイルが見つかりません。: " + file);
			return new FileInputStream(file);
		}
	}

	/**
	 * カレントディレクトリにあるファイル名の配列を返します。
	 *
	 * @return ファイル名の配列
	 */
	public String[] list()
	{
		if (isNar())
		{
			List<String> list = new ArrayList<String>();
			ZipFile zip = (ZipFile)source;
			Enumeration<?> entries = zip.entries();
			while (entries.hasMoreElements())
			{
				ZipEntry entry = (ZipEntry)entries.nextElement();
				String name = entry.getName();

				// ベース名から始まるファイル名を選択
				if (base != null)
				{
					if (name.startsWith(base))
					{
						name = name.substring(base.length() + 1);
					}
					else
					{
						continue;
					}
				}

				// ディレクトリを含まないファイル名を選択
				int slash = name.indexOf("/");
//				if (slash >= 0 && slash < name.length() - 1) continue;
				if (slash >= 0) continue;

				// ファイル名を配列に追加
//				log.debug("list=" + name);
				list.add(name);
			}
			String[] array = (String[])list.toArray(new String[0]);
			log.debug(String.format("base=%s, list=%s", base, list));
			return array;
//			return new String[]{};
		}
		else
		{
			File dir = (File)source;
			return dir.list();
		}
	}

	/**
	 * サブディレクトリを読み込みます。
	 *
	 * @param path パス名
	 * @return サブディレクトリ
	 * @throws IOException 読み込めない場合
	 */
	public NarFile subdir(String path) throws IOException
	{
		path = combinePath(base, path);
		log.info("src=" + source + ", path=" + path);
		NarFile nar;
		if (isNar())
		{
//			ZipFile zip = (ZipFile)source;
//			ZipEntry entry = zip.getEntry(path);
//			nar = new NarFile(this, entry);
			nar = new NarFile(this, path);
		}
		else
		{
			File dir = new File((File)source, path);
			nar = new NarFile(dir);
		}
		return nar;
	}

	/**
	 * NarFile をローカルディレクトリにインストールします。
	 *
	 * @return NarFile
	 * @throws IOException インストールできない場合
	 */
	public NarFile install() throws IOException
	{
		if (isRefresh())
		{
			refresh();
		}
		File dir = new File("ghost");
		NarFile nar = load(dir.getPath());
		return nar;
	}

	/**
	 * このファイルセットをインストールする前に、ディレクトリをリセットします。
	 */
	public void refresh()
	{
		String[] list = getRefreshUndeleteMask();
		log.debug("list=" + list.length);
		for (String name : list)
		{
			log.debug("  name=" + name);
		}
	}

	/**
	 * このファイルセットのインストールにあたって、旧来のディレクトリをリセットするか否かを判定します。
	 *
	 * @return リセットする場合は true
	 */
	public boolean isRefresh()
	{
		return "1".equals(getProperty("refresh"));
	}

	/**
	 * refresh にあたって削除しないファイル名のリストを返します。
	 *
	 * @return 削除しないファイル名のリスト
	 */
	public String[] getRefreshUndeleteMask()
	{
		String mask = getProperty("refreshundeletemask");
		return mask != null ? mask.split(":") : new String[]{};
	}

	/**
	 * ゴーストが持つ全てのファイルをネットワーク経由で自動更新できます。
	 *
	 * @see <a href="http://usada.sakura.vg/contents/updatebymyself.html">伺か - ネットワーク更新</a>
	 */
	public void update()
	{
		// 削除
		// 更新
	}

	/**
	 * 指定されたキーに対応する値を返します。
	 *
	 * @param key キー
	 * @return キーに対応する値
	 */
	public String getProperty(String key)
	{
		return config.getProperty(key);
	}

	/**
	 * このファイルセットの定義ファイルを返します。
	 *
	 * @return 定義ファイル
	 */
	public SakuraConfig getDescript()
	{
		return config;
	}

	/**
	 * 文字コードを返します。
	 *
	 * @return 文字コード
	 */
	public String getCharset()
	{
		return config.getProperty("charset", "Shift_JIS");
	}

	/**
	 * このファイルセットの種別を返します。
	 *
	 * @return ファイルセットの種別
	 */
	public Type getType()
	{
		return Type.valueOf(getProperty("type"));
	}

	/**
	 * このファイルセット全体の名前を返します。
	 * 必ずしもゴーストの名前とは限りません。
	 *
	 * @return ファイルセット全体の名前
	 */
	public String getName()
	{
		return getProperty("name");
	}

	/**
	 * このファイルセットが作成するディレクトリの名称を返します。
	 *
	 * @return ファイルセットが作成するディレクトリの名称
	 */
	public String getDirectory()
	{
		return getProperty("directory");
	}

	/**
	 * バルーンデータを配置するディレクトリの名称を返します。
	 *
	 * @return バルーンデータを配置するディレクトリの名称
	 * @since INSTALL/1.2
	 */
	public String getBalloonDirectory()
	{
		return getProperty("balloon.directory");
	}

	/**
	 * 指定されたゴーストが、このファイルを受け入れるかどうか判定します。
	 *
	 * @param name ゴーストの名前
	 * @return このファイルを受け入れる場合は true
	 * @since INSTALL/1.3
	 */
	public boolean accept(String name)
	{
		if (name == null) return false;

		String acceptName = getProperty("accept");
		return name.equals(acceptName);
	}

	/**
	 * script はインストール終了後に再生されるスクリプトを記述します。
	 *
	 * @return さくらスクリプト
	 * @since INSTALL/1.3
	 */
	public String getScript()
	{
		return getProperty("script");
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		buf.append(getClass().getName());
		buf.append("[");
		buf.append("type=");
		buf.append(getType());
		buf.append(", ");
		buf.append("name=");
		buf.append(getName());
		buf.append(", ");
		buf.append("base=");
		buf.append(base);
		buf.append("]");
		return buf.toString();
	}
}
