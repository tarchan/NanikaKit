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
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

	/** 親 NAR ファイル */
	protected NanikaArchive parent;

	/** ZIP ファイル */
	protected ZipFile zip;

	/** プロパティー */
	protected Properties properties = new Properties(getDefaults());

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
		if (install != null)
		{
			Properties installDesc = install.readDescript();
			log.debug("install=" + installDesc);
			properties.putAll(installDesc);
		}

		NanikaEntry ghost = getEntry(new File(getGhostHome(), getProperty("nanika.ghost.descript")));
		log.debug("ghost=" + ghost);
		NanikaEntry shell = getEntry(new File(getShellHome(), getProperty("nanika.shell.descript")));
		log.debug("shell=" + shell);
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
		defaults.setProperty("nanika.ghost", "ghost");
		defaults.setProperty("nanika.ghost.home", "master");
		defaults.setProperty("nanika.ghost.descript", "descript.txt");
		defaults.setProperty("nanika.ghost.thumbnail", "thumbnail.png");

		// shell
		defaults.setProperty("nanika.shell", "shell");
		defaults.setProperty("nanika.shell.home", "master");
		defaults.setProperty("nanika.shell.descript", "descript.txt");
		defaults.setProperty("nanika.shell.surfaces", "surfaces.txt");

		// balloon
		defaults.setProperty("balloon.directory", "balloon");
		defaults.setProperty("balloon.descript", "descript.txt");

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
		return properties;
	}

	/**
	 * プロパティーを返します。
	 * 
	 * @param key キー
	 * @return プロパティー
	 */
	public String getProperty(String key)
	{
		return properties.getProperty(key);
	}

//	public String getProperty(String key, String def)
//	{
//		return properties.getProperty(key, def);
//	}

	/**
	 * ゴーストのディレクトリを返します。
	 * 
	 * @return ゴーストのディレクトリ
	 */
	public File getGhostHome()
	{
		Properties props = getProperties();
		File shellHome = new File(props.getProperty("nanika.ghost"), props.getProperty("nanika.ghost.home"));
		return shellHome;
	}

	/**
	 * シェルのディレクトリを返します。
	 * 
	 * @return シェルのディレクトリ
	 */
	public File getShellHome()
	{
		Properties props = getProperties();
		File shellHome = new File(props.getProperty("nanika.shell"), props.getProperty("nanika.shell.home"));
		return shellHome;
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
		ZipEntry zipEntry = zip.getEntry(file.getPath());
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
		log.debug("regex=" + regex);
		java.util.Enumeration<? extends ZipEntry> in = zip.entries();
		LinkedList<NanikaEntry> out = new LinkedList<NanikaEntry>();
		while (in.hasMoreElements())
		{
			ZipEntry entry = in.nextElement();
			String name = entry.getName();
			if (name.matches(regex))
			{
				log.debug("entry=" + name);
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
		File file = new File(getShellHome(), String.format("surface%s.png", id));
		log.debug(id + "=" + file);
		NanikaEntry entry = getEntry(file);
		log.debug(id + "=" + entry.getName());
		try
		{
			BufferedImage image = ImageIO.read(entry.getInputStream());
			log.debug("image=" + image);
			log.debug("image=" + image.getWidth() + "x" + image.getHeight() + ","  + image.getType() + "," + image.getColorModel());
			int rgb = image.getRGB(0, 0);
			log.debug("rgb=0x" + Integer.toHexString(rgb));
			SakuraSurface surface = new SakuraSurface(id, image);
			return surface;
		}
		catch (IOException e)
		{
			log.error("image read error", e);
			return null;
		}
	}

	/**
	 * バルーンを取得します。
	 */
	public void getBalloon()
	{
		try
		{
			File home = new File(getProperty("balloon.directory"));
			log.debug("balloon=" + home);
			NanikaEntry descript = getEntry(new File(home, getProperty("balloon.descript")));
			if (descript == null) return;
			descript.readDescript();
			NanikaEntry balloons0 = getEntry(new File(home, "balloons0.png"));
			if (balloons0 == null) return;
			balloons0.readImage();
		}
		catch (IOException e)
		{
			log.error("read balloon error", e);
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
			if (entry == null) entry = getEntry(new File(getGhostHome(), getProperty("nanika.ghost.thumbnail")));
			log.debug("entry=" + entry);
			return entry != null ? entry.readImage() : null;
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
			return entry != null ? entry.readText() : null;
		}
		catch (IOException e)
		{
			log.error("read readme error", e);
			return null;
		}
	}
}
