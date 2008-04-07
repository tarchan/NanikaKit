/*
 * ISakura.java
 * NanikaKit
 *
 * Created by tarchan on 2008/04/07.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.nanika;

/**
 * @since 1.0
 * @author tarchan
 */
public interface SakuraGhost
{

	/**
	 * スコープを変更します。
	 * 
	 * @param scope スコープ番号
	 * @return このゴーストへの参照
	 */
	public abstract SakuraGhost setScope(int scope);

	/**
	 * 現在のスコープのサーフェスを変更します。
	 * 
	 * @param id サーフェス ID
	 * @return このゴーストへの参照
	 */
	public abstract SakuraGhost setSurface(int id);

	/**
	 * 現在のスコープのバルーンサーフェスを変更します。
	 * 
	 * @param id バルーンサーフェス ID
	 * @return このゴーストへの参照
	 */
	public abstract SakuraGhost setBalloonSurface(int id);

	/**
	 * 現在のスコープのバルーンにメッセージを表示します。
	 * 
	 * @param message メッセージ
	 */
	public abstract void talk(String message);

	/**
	 * ゴーストの姿を消します。
	 * 
	 * @return このゴーストへの参照
	 */
	public abstract SakuraGhost vanish();
}
