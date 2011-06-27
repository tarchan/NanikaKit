/**
 * Copyright (c) 2009 tarchan. All rights reserved.
 */
package test.com.mac.tarchan.nanika;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.mac.tarchan.nanika.ghost.DummyGhost;
import com.mac.tarchan.nanika.ghost.NanikaGhost;
import com.mac.tarchan.nanika.ghost.SakuraScript;
import com.mac.tarchan.nanika.shell.NanikaShell;
import com.mac.tarchan.nanika.util.NarFile;
import com.mac.tarchan.nanika.util.NarFile.Type;

/**
 * 何かテスト
 *
 * @author tarchan
 */
public class TestNanikaKit
{
	/** ログ */
	private static final Log log = LogFactory.getLog(TestNanikaKit.class);

	static
	{
//		PropertyConfigurator.configure("log4j.properties");
	}

	/**
	 * スクリプトを評価します。
	 *
	 * @param script スクリプト
	 * @return スクリプトの評価結果
	 */
	public String eval(String script)
	{
		System.out.println("script=" + script);
		Scanner s = new Scanner(script).useDelimiter("\\\\");
		StringBuilder r = new StringBuilder();
		while (s.hasNext())
		{
			String next = s.next();
//			System.out.println("next=" + next);
			Pattern p = Pattern.compile("([01sbnwcxtq*&iv45\\-m!e]|_w|_q|_s|_n|_l|_u|_m|_v|_V)([0-9]|\\[(.+)\\])?(.*)");
			Matcher m = p.matcher(next);
			if (m.find())
			{
				System.out.format("match=%s/%s/%s/%s/\"%s\"\n", m.group(0), m.group(1), m.group(2), m.group(3), m.group(4));
				r.append(m.group(1));
			}
			else
			{
				System.out.println("unmatch=" + next);
			}
//			MatchResult m = s.match();
//			System.out.format("next=%s (%s)\n", next, m);
		}
		return r.toString();
	}

	/**
	 * こんにちは
	 */
	@Test
	public void hello()
	{
		assertEquals("0s1se", new TestNanikaKit().eval("\\0\\s[0]こんにちは。\\1\\s[10]よぉ。\\e"));
	}

	/**
	 * 終わりたくないなぁ…
	 */
	@Test
	public void pray()
	{
		assertEquals("0s1s0wwn_wn1ww_w_wnn0e", new TestNanikaKit().eval("\\0\\s[0]\\1\\s[10]\\0普通のポストで終わりたくないなぁ…\\w2…\\w2\\n\\_w[126]\\n[half]\\1…\\w2…\\w2っていうかお前、\\_w[78]自分を普通だと思ってたのか。\\_w[84]\\n\\n[half]\\0え？\\e"));
	}

	/**
	 * surfaces.txt をロードします。
	 *
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void loadSurfaces(String path) throws FileNotFoundException
	{
		File txt = new File(path);
		Scanner s = new Scanner(txt, "Shift_JIS");
		while (true)
		{
			String find = s.findWithinHorizon("(\\S+)\\s*\\{\\s*([^{]*)\\}", 0);
			if (find == null) break;
			System.out.println("find=" + find);

			MatchResult m = s.match();
			String head = m.group(1);
			String body = m.group(2);
			System.out.println("head=\"" + head + "\"");
			System.out.println("body=\"" + body + "\"");
			parseSurface(new Scanner(body));
		}
	}

	/**
	 * 単一のサーフェスをロードします。
	 *
	 * @param path
	 * @param name
	 * @throws FileNotFoundException
	 */
	public void loadSurfacesOne(String path, String name) throws FileNotFoundException
	{
		File txt = new File(path);
		Scanner s = new Scanner(txt, "Shift_JIS");
		String find = s.findWithinHorizon("(" + Pattern.quote(name) + ")\\s*\\{\\s*([^{]*)\\}", 0);
		System.out.println("find=\"" + find + "\"");
		if (find != null)
		{
			MatchResult m = s.match();
			String head = m.group(1);
			String body = m.group(2);
			System.out.println("head=\"" + head + "\"");
			System.out.println("body=\"" + body + "\"");
			parseSurface(new Scanner(body));
		}
	}

	/**
	 * サーフェス毎の定義をロードします。
	 *
	 * @param s
	 */
	public void parseSurface(Scanner s)
	{
		while (true)
		{
			if (s.hasNext("(collision.+?),(.+)"))
			{
				s.next("(collision.+?),(.+)");
				MatchResult m = s.match();
				String head = m.group(1);
				String body = m.group(2);
				String[] token = body.split(",");
				int x1 = Integer.parseInt(token[0]);
				int y1 = Integer.parseInt(token[1]);
				int x2 = Integer.parseInt(token[2]);
				int y2 = Integer.parseInt(token[3]);
				String name = token[4];
				Rectangle rect = new Rectangle(new Point(x1, y1));
				rect.add(new Point(x2, y2));
				System.out.println("当たり判定: " + head + ": " + name + ": " + rect);
			}
			else if (s.hasNext("(element.+?),(.+)"))
			{
				s.next("(element.+?),(.+)");
				MatchResult m = s.match();
				String head = m.group(1);
				String body = m.group(2);
				String[] token = body.split(",");
				String type = token[0];
				String filename = token[1];
				int x = Integer.parseInt(token[2]);
				int y = Integer.parseInt(token[3]);
				Point p = new Point(x, y);
				System.out.println("ベースサーフェス: " + head + ": " + Arrays.toString(new Object[]{type, filename, p}));
			}
			else if (s.hasNext("(.+?interval),(.+)"))
			{
				s.next("(.+?interval),(.+)");
				MatchResult m = s.match();
				String head = m.group(1);
				String body = m.group(2);
				System.out.println("アニメーション開始: " + head + ": " + Arrays.toString(body.split(",")));
			}
			else if (s.hasNext("(.+?pattern.+?),(.+)"))
			{
				s.next("(.+?pattern.+?),(.+)");
				MatchResult m = s.match();
				String head = m.group(1);
				String body = m.group(2);
				System.out.println("アニメーションパターン: " + head + ": " + Arrays.toString(body.split(",")));
			}
			else if (s.hasNext("(.+?option),(.+)"))
			{
				s.next("(.+?option),(.+)");
				MatchResult m = s.match();
				String head = m.group(1);
				String body = m.group(2);
				System.out.println("オプション: " + head + ": " + Arrays.toString(body.split(",")));
			}
			else if (s.hasNextLine())
			{
				String line = s.nextLine();
				if (line.trim().length() > 0) System.out.println("未定義: " + line);
			}
			else
			{
				break;
			}
		}
	}

	/**
	 * あかねサーフェステスト
	 */
	@Test
	public void load001()
	{
		try
		{
			loadSurfaces("surfaces.txt");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Firstサーフェステスト
	 */
	@Test
	public void load002()
	{
		try
		{
			loadSurfaces("surfaces_first.txt");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 単一サーフェステスト
	 */
	@Test
	public void load003()
	{
		try
		{
			loadSurfacesOne("surfaces_first.txt", "surface8");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 単一サーフェステスト
	 */
	@Test
	public void load004()
	{
		try
		{
			loadSurfacesOne("surfaces_first.txt", "sakura.surface.alias");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 正規表現エスケープテスト
	 */
	@Test
	public void quot001()
	{
		System.out.println(Pattern.quote("\\s"));
	}

	/**
	 * ダミーテスト
	 */
	@Test
	public void dummyGhost()
	{
		NanikaGhost dummy = (NanikaGhost)Proxy.newProxyInstance(NanikaGhost.class.getClassLoader(), new Class<?>[]{NanikaGhost.class}, new InvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
//				System.out.println("invoke: " + method + ", " + Arrays.asList(args));
//				System.out.println("invoke: proxy=" + proxy.getClass().getName());
//				System.out.println("invoke: method=" + method + ", args=" + Arrays.asList(args));
				System.out.println(String.format("invoke: method=%s, args=%s", method, Arrays.asList(args)));
				return null;
			}
		});
		dummy.setScope(0);
		dummy.setSurface("00");
	}

	/** さくらスクリプトをテストします。 */
	@Test
	public void evalSakuraScript()
	{
		NanikaGhost dummy = DummyGhost.newGhost(new String[]{"さくら", "うにゅう"});
		String[] args = new String[]
		{
			"\\0\\s[0]こんにちは。\\1\\s[10]よぉ。\\e",
			"\\0\\s[0]\\1\\s[10]\\0普通のポストで終わりたくないなぁ…\\w2…\\w2\\n\\_w[126]\\n[half]\\1…\\w2…\\w2っていうかお前、\\_w[78]自分を普通だと思ってたのか。\\_w[84]\\n\\n[half]\\0え？\\e",
			"\\q[選択肢1,1]\\q[選択肢2,2]",
			"\\*タイムアウトしない",
			"\\&[識別子]",
			"\\![open,browser]",
			"えんいー\\e",
			"\\0\\s[1]こんにちは。[ユーザー]さん",
		};
		for (String script : args)
		{
			SakuraScript.eval(script, dummy);
		}
	}

	/** シェルをロードするテスト */
	@Test
	public void loadShell()
	{
		String[] args = new String[]
		{
			"/Users/tarchan/Documents/nanika/nar/akane_v135.zip",
			"/Users/tarchan/Documents/nanika/nar/333-2.60.nar",
			"/Users/tarchan/Documents/nanika/nar/mayura_v340.zip",
			"/Users/tarchan/Documents/nanika/nar/cmd.nar",
		};
		for (String zip : args)
		{
			try
			{
				NarFile nar = NarFile.load(zip);
				log.info("name=" + nar.getName());
				if (nar.getType() == Type.ghost)
				{
					NanikaShell shell = new NanikaShell(nar.subdir("shell/master"));
					assertEquals(shell.getType(), Type.shell);
				}
				else
				{
					fail("不正なタイプです。" + nar.getType());
				}
			}
			catch (Exception x)
			{
				x.printStackTrace();
				fail("不正なファイルです。" + zip);
			}
		}
	}
}
