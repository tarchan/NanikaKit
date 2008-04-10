/*
 * WrappedPoint.java
 * NanikaKit
 *
 * Created by tarchan on 2008/04/08.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.geom;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * 範囲限定座標を表します。
 * 
 * @since 1.0
 * @author tarchan
 */
public class WrappedPoint extends Point
{
	/** オリジナルの座標 */
	private Point point;

//	/** 座標を折り返す範囲 */
//	private Rectangle bounds;

	/**
	 * 範囲限定座標を構築します。
	 * 
	 * @param x X 座標
	 * @param y Y 座標
	 */
	public WrappedPoint(int x, int y)
	{
		super(x, y);
		point = new Point(x, y);
	}

	/**
	 * @see java.awt.Point#move(int, int)
	 */
	@Deprecated
	public void move(int x, int y)
	{
	}

	/**
	 * 座標を折り返す範囲を設定します。
	 * 
	 * @param bounds 座標を折り返す範囲
	 * @return この座標への参照
	 */
	public WrappedPoint setBounds(Rectangle bounds)
	{
//		this.bounds = bounds;
		x = point.x < 0 ? point.x + bounds.x + bounds.width : point.x + bounds.x;
		y = point.y < 0 ? point.y + bounds.y + bounds.height : point.y + bounds.y;
		return this;
	}
}
