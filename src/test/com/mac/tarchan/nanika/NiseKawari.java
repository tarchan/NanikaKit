/*
 * NiseKawari.java
 * NanikaKit
 *
 * Created by tarchan on 2008/04/11.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package test.com.mac.tarchan.nanika;

import com.mac.tarchan.nanika.SakuraShiori;

/**
 * 華和梨を実装します。
 * 
 * @since 1.0
 * @author tarchan
 */
public class NiseKawari extends SakuraShiori
{
	/**
	 * さくらスクリプトを返します。
	 */
	@Override
	public String request(String command)
	{
		return "\\0\\s[0]こんにちは。\\_w[1500]\\1\\s[10]よぉ。\\e";
//		return "\\0\\s[0]\\1\\s[10]\\0普通のポストで終わりたくないなぁ…\\w2…\\w2\\n\\_w[126]\\n[half]\\1…\\w2…\\w2っていうかお前、\\_w[78]自分を普通だと思ってたのか。\\_w[84]\\n\\n[half]\\0え？\\e";
	}
}
