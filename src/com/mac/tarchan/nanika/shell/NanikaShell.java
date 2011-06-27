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
package com.mac.tarchan.nanika.shell;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.nanika.util.NarFile;
import com.mac.tarchan.nanika.util.NarFile.Type;

/**
 * NanikaShell
 *
 * @author tarchan
 * @see <a href="http://usada.sakura.vg/contents/shell.html">伺か - シェル</a>
 */
public class NanikaShell
{
	/** ログ */
	private static final Log log = LogFactory.getLog(NanikaShell.class);

	/** sakura の基本サーフェス ID */
	public static final String SAKURA_ID = "0";

	/** kero の基本サーフェス ID */
	public static final String KERO_ID = "10";

	/** NAR ファイル */
	protected NarFile nar;

	/** サーフェスマップ */
	protected Map<String, NanikaSurface> surfaces = new LinkedHashMap<String, NanikaSurface>();

	/**
	 * 指定されたファイルをシェルとして読み込みます。
	 *
	 * @param nar NAR ファイル
	 * @throws IOException シェルを読み込めない場合
	 */
	public NanikaShell(NarFile nar) throws IOException
	{
		this.nar = nar;
		readFiles();
		readSurfaces();
	}

	/**
	 * ファイル名の配列を読み込みます。
	 *
	 * @throws IOException 読み込めない場合
	 */
	protected void readFiles() throws IOException
	{
		log.debug("nar=" + nar);
		String[] list = nar.list();
		Pattern p = Pattern.compile("surface(.*)\\.png");
		for (String name : list)
		{
			Matcher m = p.matcher(name);
			if (m.matches())
			{
				String id = m.group(1);
//				surfaces.put(id, "");
				NanikaSurface surface = new NanikaSurface(this, id);
				surfaces.put(id, surface);
//				BufferedImage image = ImageIO.read(nar.getInputStream(name));
//				surface.setImage(image);
//				log.debug("load surface: " + surface);
			}
		}

//		// load surface
//		int count = surfaces.size();
//		int index = 0;
//		for (String id : surfaces.keySet())
//		{
//			NanikaSurface surface = getSurface(id);
//			String name = String.format("surface%s.png", id);
////			BufferedImage image = ImageIO.read(nar.getInputStream(name));
//			BufferedImage image = readImage(name);
//			surface.setImage(image);
//			log.debug(String.format("load surface: %d/%d %s", ++index, count, surface));
//		}
	}

	/**
	 * サーフェス設定を読み込みます。
	 *
	 * @throws IOException サーフェス設定を読み込めない場合
	 */
	protected void readSurfaces() throws IOException
	{
		InputStream in = nar.getInputStream("surfaces.txt");
		Scanner scanner = new Scanner(in, nar.getCharset());
		log.debug("surfaces=" + scanner);
		Pattern pattern = Pattern.compile("surface(\\S+)\\s*\\{\\s*([^{]*)\\}");
		while (true)
		{
			String find = scanner.findWithinHorizon(pattern, 0);
			if (find == null) break;

			MatchResult match = scanner.match();
			String id = match.group(1);
			String descript = match.group(2);
//			log.debug("next=" + match);
//			log.debug("find=" + find);
			if (log.isTraceEnabled()) log.debug("id=" + id + ", descript=" + descript);
//			surfaces.put(id, body);
			try
			{
				getSurface(id).setDescript(descript);
			}
			catch (Exception x)
			{
				log.error("id=" + id + ", descript=" + descript, x);
			}
		}
	}

	/**
	 * イメージを読み込みます。
	 *
	 * @param name ファイル名
	 * @return イメージ
	 */
	protected BufferedImage readImage(String name)
	{
		try
		{
			BufferedImage image = ImageIO.read(nar.getInputStream(name));
			return image;
		}
		catch (IOException x)
		{
			log.error(x);
			return null;
		}
	}

	/**
	 * このシェルの名前を返します。
	 * 服装、もしくはサイズ（100%、80%等）等を表すユニークな文字列がセットされます。
	 *
	 * @return シェルの名前
	 */
	public String getName()
	{
		return nar.getProperty("name");
	}

	/**
	 * このファイルセットの種別を返します。
	 * シェルの場合は識別子 shell がセットされます。
	 *
	 * @return ファイルセットの種別
	 */
	public Type getType()
	{
		return nar.getType();
	}

	/**
	 *
	 * @return 作者名
	 */
	public String getCraftman()
	{
		String craftman = nar.getProperty("craftman");
		String craftmanw = nar.getProperty("craftmanw");
		return craftmanw != null ? craftmanw : craftman;
	}

	/**
	 * サーフェス ID のリストを返します。
	 *
	 * @return サーフェス ID のリスト
	 */
	public Set<String> getSurfaceKeys()
	{
		return surfaces.keySet();
	}

	/**
	 * このシェルに含まれるサーフェスの数を返します。
	 *
	 * @return サーフェスの数
	 */
	public int getSurfaceCount()
	{
		return surfaces.size();
	}

	/**
	 * 指定された ID のサーフェスを返します。
	 *
	 * @param id サーフェス ID
	 * @return サーフェス
	 * @throws IllegalArgumentException 存在しないサーフェスの場合
	 */
	public NanikaSurface getSurface(String id)
	{
		return surfaces.get(id);
//		if (!surfaces.containsKey(id)) throw new IllegalArgumentException("id=" + id);

//		try
//		{
//			InputStream in = nar.getInputStream(String.format("surface%s.png", id));
//			log.debug("in=" + in);
////			BufferedImage image = ImageIO.read(in);
////			log.debug("image=" + image);
//			return new NanikaSurface(id, in);
//		}
//		catch (IOException x)
//		{
//			log.error(x);
//		}
//
//		return null;
	}

//	public String getDescript()
//	{
//		return null;
//	}
//
//	public String getReadme()
//	{
//		return null;
//	}

	/**
	 * サムネールを返します。
	 *
	 * @return サムネール
	 */
	public BufferedImage getThumbnail()
	{
		return readImage("thumbnail.png");
//		try
//		{
//			InputStream in = nar.getInputStream("thumbnail.png");
//			BufferedImage image = ImageIO.read(in);
//			return image;
//		}
//		catch (IOException x)
//		{
//			log.error(x);
//			return null;
//		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		buf.append(getClass().getName());
		buf.append("[");
		buf.append("name=");
		buf.append(nar.getProperty("name"));
		buf.append(", ");
		buf.append("id=");
		buf.append(nar.getProperty("id"));
		buf.append(", ");
		buf.append("craftman=");
		buf.append(getCraftman());
		buf.append(", ");
		buf.append("url=");
		buf.append(nar.getProperty("craftmanurl"));
		buf.append(", ");
		buf.append("surface=");
		buf.append(getSurfaceKeys().size());
		buf.append(",");
		buf.append(getSurfaceKeys());
		buf.append(", ");
		buf.append("descript=");
		buf.append(nar.getDescript());
		buf.append("]");
		return buf.toString();
	}
}
