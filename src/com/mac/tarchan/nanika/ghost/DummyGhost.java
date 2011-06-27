/*
 * Copyright (c) 2009 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika.ghost;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * DummyGhost
 * 
 * @author v-togura
 */
public class DummyGhost implements InvocationHandler
{
	/**
	 * ダミーゴーストを作成します。
	 * 
	 * @param names 各スコープの名前
	 * @return NanikaGhost
	 */
	public static NanikaGhost newGhost(String... names)
	{
		return (NanikaGhost)Proxy.newProxyInstance(NanikaGhost.class.getClassLoader(), new Class<?>[]{NanikaGhost.class}, new DummyGhost(names));
	}

	/**
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		System.out.println(String.format("invoke: method=%s, args=%s", method, args != null ? Arrays.asList(args) : null));
		if (method.getName().equals("setScope"))
		{
			setScope((Integer)args[0]);
		}
		else if (method.getName().equals("setSurface"))
		{
			setSurface((String)args[0]);
		}
		else if (method.getName().equals("talk"))
		{
			talk((String)args[0]);
		}
		return null;
	}

	Scope[] scopes;

	int currentScope;

	/**
	 * ダミーゴーストを作成します。
	 * 
	 * <pre>
	 * NanikaGhost ghost = DummyGhost.newGhost(new String[]{"さくら", "うにゅう"});
	 * </pre>
	 * @param names
	 */
	DummyGhost(String... names)
	{
		scopes = new Scope[names.length];
		for (int i = 0; i < names.length; i++)
		{
			String name = names[i];
			scopes[i] = new Scope(i, name);
			System.out.println("init: " + scopes[i]);
		}
	}

	/**
	 * 現在のスコープを設定します。
	 * 
	 * @param scope 現在のスコープ
	 */
	public void setScope(int scope)
	{
		this.currentScope = scope;
	}

	/**
	 * 現在のスコープのサーフェスを設定します。
	 * 
	 * @param id サーフェスID
	 */
	public void setSurface(String id)
	{
		scopes[currentScope].setSurface(id);
	}

	/**
	 * 現在のスコープでしゃべります。
	 *  
	 * @param message メッセージ
	 */
	public void talk(String message)
	{
		scopes[currentScope].talk(message);
	}

	static class Scope
	{
		int scope;

		String name;

		String surface;

		Scope(int scope, String name)
		{
			this.scope = scope;
			this.name = name;
			this.surface = null;
		}

		public void setSurface(String id)
		{
			this.surface = id;
		}

		public void talk(String message)
		{
			System.out.format("%s[%s] %s\n", name, surface, message);
		}

		public String toString()
		{
			StringBuilder buf = new StringBuilder();
			buf.append(super.toString());
			buf.append("scope=");
			buf.append(scope);
			buf.append(", name=");
			buf.append(name);
			buf.append(", surface=");
			buf.append(surface);
			return buf.toString();
		}
	}
}
