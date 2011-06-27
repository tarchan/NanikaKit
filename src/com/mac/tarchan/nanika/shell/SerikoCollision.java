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

import java.awt.Rectangle;

/**
 * SerikoCollision は、当たり判定領域を設定します。
 * 
 * @author tarchan
 */
public class SerikoCollision
{
	/** 名前 */
	protected String name;

	/** 矩形 */
	protected Rectangle clip;

	/**
	 * 当たり判定を設定します。
	 * 
	 * @param left 左端
	 * @param top 上橋
	 * @param right 右端
	 * @param bottom 下端
	 * @param name 当たり判定識別子
	 */
	public SerikoCollision(int left, int top, int right, int bottom, String name)
	{
		this.name = name;
		this.clip = new Rectangle(left, top, 0, 0);
		clip.add(right, bottom);
	}

	/**
	 * 当たり判定識別子を返します。
	 * 
	 * @return 当たり判定識別子
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * 当たり判定の矩形を返します。
	 * 
	 * @return 当たり判定の矩形
	 */
	public Rectangle getClip()
	{
		return clip;
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
		buf.append(name);
		buf.append(", ");
		buf.append("clip=");
		buf.append(clip);
		buf.append("]");
		return buf.toString();
	}
}
