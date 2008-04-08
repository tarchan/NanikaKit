/*
 * SakuraUnit.java
 * NanikaKit
 *
 * Created by tarchan on 2008/04/07.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package test.com.mac.tarchan.nanika;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.mac.tarchan.nanika.SakuraScript;

/**
 * さくらスクリプトをユニットテストします。
 * 
 * @since 1.0
 * @author tarchan
 */
public class SakuraUnit
{
	/**
	 * メイン
	 * 
	 * @param args なし
	 */
	public static void main(String[] args)
	{
		SakuraScript sakura = new SakuraScript();
		args = new String[]{
			"\\0\\s[0]こんにちは。\\1\\s[10]よぉ。\\e",
			"\\0\\s[0]\\1\\s[10]\\0普通のポストで終わりたくないなぁ…\\w2…\\w2\\n\\_w[126]\\n[half]\\1…\\w2…\\w2っていうかお前、\\_w[78]自分を普通だと思ってたのか。\\_w[84]\\n\\n[half]\\0え？\\e ",
			"\\q[選択肢1,1]\\q[選択肢2,2]",
			"\\*タイムアウトしない",
			"\\&[識別子]",
			"\\![open,browser]",
			"えんいー\\e"};
		for (String script : args)
		{
			sakura.eval(script);
		}
	}

	/** スクリプトエンジン */
	private SakuraScript sakura;

	/** ユニットテストを初期化します。 */
	@Before
	public void init()
	{
		sakura = new SakuraScript();
		sakura.put("ghost", new SakuraUnit());
	}

	/** こんにちは */
	@Test
	public void hello()
	{
		assertEquals("0s1se", sakura.eval("\\0\\s[0]こんにちは。\\1\\s[10]よぉ。\\e"));
	}

	/** ポスト */
	@Test
	public void post()
	{
		assertEquals("0s1s0wwn_wn1ww_w_wnn0e", sakura.eval("\\0\\s[0]\\1\\s[10]\\0普通のポストで終わりたくないなぁ…\\w2…\\w2\\n\\_w[126]\\n[half]\\1…\\w2…\\w2っていうかお前、\\_w[78]自分を普通だと思ってたのか。\\_w[84]\\n\\n[half]\\0え？\\e"));
	}

	/** 選択肢 */
	@Test
	public void choice()
	{
		assertEquals("qq", sakura.eval("\\q[選択肢1,1]\\q[選択肢2,2]"));
	}

	/** タイムアウト */
	@Test
	public void timeout()
	{
		assertEquals("*", sakura.eval("\\*タイムアウトしない"));
	}

	/** 識別子 */
	@Test
	public void id()
	{
		assertEquals("&", sakura.eval("\\&[識別子]"));
	}

	/** オープン */
	@Test
	public void openbrowser()
	{
		assertEquals("!", sakura.eval("\\![open,browser]"));
	}

	/** えんいー */
	@Test
	public void yene()
	{
		assertEquals("e", sakura.eval("えんいー\\e"));
	}

	/** スコープ */
	private int scope;

	/**
	 * スコープを変更します。
	 * 
	 * @param scope スコープ番号
	 * @return このゴーストへの参照
	 */
	public SakuraUnit setScope(int scope)
	{
		this.scope = scope;
		return this;
	}

	/**
	 * 現在のスコープのバルーンにメッセージを表示します。
	 * 
	 * @param message メッセージ
	 * @return このゴーストへの参照
	 */
	public SakuraUnit talk(String message)
	{
		String pre = scope == 0 ? "さ：" : "う：";
		System.out.format("%s\"%s\"\n", pre, message);
		return this;
	}
}
