/*
 * NanikaFile.java
 * NanikaKit
 *
 * Created by tarchan on 2008/03/26.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika.nar;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.nanika.SakuraBalloon;
import com.mac.tarchan.nanika.SakuraSurface;

/**
 * このクラスは、NAR ファイルからエントリを読み込むために使用します。
 * 
 * @since 1.0
 * @author tarchan
 * @see ZipFile
 */
public class NanikaArchive
{
	/** ロガー */
	private static final Log log = LogFactory.getLog(NanikaArchive.class);

	/** 次候補 NAR ファイル */
	protected NanikaArchive alt;

	/** ZIP ファイル */
	protected ZipFile zip;

	/** プロパティー */
	protected Properties descript = new Properties(getDefaults());

	/**
	 * NanikaArchive オブジェクトを構築します。
	 * 
	 * @param name NAR ファイル名
	 * @throws IOException 入力エラーが発生した場合
	 */
	public NanikaArchive(String name) throws IOException
	{
		log.debug("name=" + name);
		zip = new ZipFile(name);

//		java.util.Enumeration<? extends ZipEntry> list = zip.entries();
//		while (list.hasMoreElements())
//		{
//			ZipEntry entry = list.nextElement();
//			log.debug("entry=" + entry.getName());
//		}

		NanikaEntry install = getEntry(getProperty("nanika.install"));
		if (install == null) throw new FileNotFoundException(getProperty("nanika.install"));

		Properties installDesc = install.asDescript();
		descript.putAll(installDesc);
		log.debug("install=" + descript);

//		NanikaEntry ghost = getEntry(new File(getGhostDirectory(), getProperty("ghost.descript")));
//		log.debug("ghost=" + ghost);
//		NanikaEntry shell = getEntry(new File(getShellDirectory(), getProperty("shell.descript")));
//		log.debug("shell=" + shell);
	}

	/**
	 * 次のアーカイブを設定します。
	 * 
	 * @param nar 次のアーカイブ
	 * @return このアーカイブへの参照
	 */
	public NanikaArchive setNext(NanikaArchive nar)
	{
		if (alt == null) alt = nar;
		else alt.setNext(nar);

		return this;
	}

	/**
	 * 指定したタイプのアーカイブを返します。
	 * 
	 * @param type アーカイブのタイプ
	 * @return 指定したタイプのアーカイブ
	 */
	public NanikaArchive forType(String type)
	{
		NanikaArchive nar = this;
		while (true)
		{
			if (nar.getType().equals(type)) return nar;

			if (alt != null) nar = alt;
			else return null;
		}
	}

	/**
	 * デフォルトのプロパティーを返します。
	 * 
	 * @return プロパティー
	 */
	private static Properties getDefaults()
	{
		Properties defaults = new Properties();

		// root
		defaults.setProperty("nanika.install", "install.txt");
		defaults.setProperty("nanika.readme", "readme.txt");
		defaults.setProperty("nanika.thumbnail", "thumbnail.png");

		// ghost
		defaults.setProperty("ghost.directory", new File("ghost", "master").getPath());
		defaults.setProperty("ghost.descript", "descript.txt");
		defaults.setProperty("ghost.thumbnail", "thumbnail.png");

		// shell
		defaults.setProperty("shell.directory", new File("shell", "master").getPath());
		defaults.setProperty("shell.descript", "descript.txt");
		defaults.setProperty("shell.surfaces", "surfaces.txt");

		// balloon
		defaults.setProperty("balloon.directory", "balloon");
		defaults.setProperty("balloon.descript", "descript.txt");
		defaults.setProperty("balloon.arrow0", "arrow0.png");
		defaults.setProperty("balloon.arrow1", "arrow1.png");
		defaults.setProperty("balloon.onlinemarker", "online0.png");
		defaults.setProperty("balloon.sstpmarker", "sstp.png");

		// other
		defaults.setProperty("nanika.headline", "headline");
		defaults.setProperty("nanika.plugin", "plugin");

		return defaults;
	}

	/**
	 * プロパティーを返します。
	 * 
	 * @return プロパティー
	 */
	public Properties getProperties()
	{
		return descript;
	}

	/**
	 * プロパティーを返します。
	 * 
	 * @param key キー
	 * @return プロパティー
	 */
	public String getProperty(String key)
	{
		return descript.getProperty(key);
	}

//	public String getProperty(String key, String def)
//	{
//		return properties.getProperty(key, def);
//	}

	/**
	 * NAR ファイルのタイプを返します。
	 * 
	 * @return NAR ファイルのタイプs
	 */
	public String getType()
	{
		return getProperty("type");
	}

	/**
	 * ゴーストのディレクトリを返します。
	 * 
	 * @return ゴーストのディレクトリ
	 */
	public File getGhostDirectory()
	{
		Properties props = getProperties();
		File shellHome = new File(props.getProperty("ghost.directory"));
		return shellHome;
	}

	/**
	 * シェルのディレクトリを返します。
	 * 
	 * @return シェルのディレクトリ
	 */
	public File getShellDirectory()
	{
		if (getType().equals("shell"))
		{
			return null;
		}
		else
		{
			Properties props = getProperties();
			File shellHome = new File(props.getProperty("shell.directory"));
			return shellHome;
		}
	}

	/**
	 * バルーンのディレクトリを返します。
	 * 
	 * @return バルーンのディレクトリ
	 */
	public File getBalloonDirectory()
	{
		return getType().equals("balloon") ? null : new File(getProperty("balloon.directory"));
	}

	/**
	 * NAR ファイルエントリを返します。
	 * 
	 * @param name エントリ名
	 * @return NAR ファイルエントリ
	 */
	public NanikaEntry getEntry(String name)
	{
		return getEntry(new File(name));
	}

	/**
	 * NAR ファイルエントリを返します。
	 * 
	 * @param file エントリファイル
	 * @return NAR ファイルエントリ
	 */
	public NanikaEntry getEntry(File file)
	{
		String path = file.getPath();
//		if (path.startsWith("/")) path = path.substring(1);
		ZipEntry zipEntry = zip.getEntry(path);
//		log.debug("zipEntry=" + zipEntry);
		return zipEntry != null ? new NanikaEntry(zip, zipEntry) : null;
	}

	/**
	 * NAR エントリのリストを返します。
	 * 
	 * @param regex 正規表現
	 * @return NAR エントリのリスト
	 */
	public NanikaEntry[] list(String regex)
	{
//		if (regex.startsWith("/")) regex = regex.substring(1);
		log.debug("regex=" + regex);
		java.util.Enumeration<? extends ZipEntry> in = zip.entries();
		LinkedList<NanikaEntry> out = new LinkedList<NanikaEntry>();
		while (in.hasMoreElements())
		{
			ZipEntry entry = in.nextElement();
			String name = entry.getName();
			if (name.matches(regex))
			{
//				log.debug("entry=" + name);
				out.add(getEntry(name));
			}
		}

		return out.toArray(new NanikaEntry[0]);
	}

	/**
	 * NAR エントリのリストを返します。
	 * 
	 * @param filter ファイルフィルタ
	 * @return NAR エントリのリスト
	 */
	public NanikaEntry[] list(FileFilter filter)
	{
		return new NanikaEntry[zip.size()];
	}

	/**
	 * サーフェスを取得します。
	 * 
	 * @param id サーフェス ID
	 * @return サーフェス
	 */
	public SakuraSurface getSurface(int id)
	{
		try
		{
			return SakuraSurface.getSurface(id, this);
		}
		catch (IOException e)
		{
			log.error("read surface error", e);
			return null;
		}
	}

	/**
	 * バルーンを取得します。
	 * 
	 * @param name sakura または kero
	 * @return バルーン
	 */
	public SakuraBalloon getBalloon(String name)
	{
		try
		{
			return new SakuraBalloon(name, this);
		}
		catch (Exception e)
		{
			if (alt != null)
			{
				log.trace("alt=" + alt);
				return alt.getBalloon(name);
			}

			log.error("read balloon error", e);
			return null;
		}
	}

	/**
	 * サムネールを返します。
	 * 
	 * @return サムネール
	 */
	public BufferedImage getThumbnail()
	{
		try
		{
			NanikaEntry entry = getEntry(getProperty("nanika.thumbnail"));
			if (entry == null) entry = getEntry(new File(getGhostDirectory(), getProperty("ghost.thumbnail")));
			log.debug("entry=" + entry);
			return entry != null ? entry.asImage() : null;
		}
		catch (IOException e)
		{
			log.error("read thumbnail error", e);
			return null;
		}
	}

	/**
	 * README を読み込みます。
	 * 
	 * @return README
	 */
	public String getReadme()
	{
		try
		{
			NanikaEntry entry = getEntry(getProperty("nanika.readme"));
			return entry != null ? entry.asText() : null;
		}
		catch (IOException e)
		{
			log.error("read readme error", e);
			return null;
		}
	}

	/**
	 * NAR ファイルの文字列表現を返します。
	 * 
	 * @return NAR ファイルの文字列表現
	 */
	public String toString()
	{
		return new StringBuilder()
			.append("[")
			.append("type=" + getProperty("type"))
			.append(", directory=" + getProperty("directory"))
			.append(", name=" + getProperty("name"))
			.append(", accept=" + getProperty("accept"))
			.append("]")
			.toString();
	}
}
