/*
 * ChromaKeyFilter.java
 * NanikaKit
 *
 * Created by tarchan on 2008/03/31.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.image;

import java.awt.image.RGBImageFilter;

/**
 * このクラスは、クロマキー合成を実行します。
 * 
 * @since 1.0
 * @author tarchan
 */
public class ChromakeyImageFilter extends RGBImageFilter
{
	/** 背景色 */
	private int background;

	/**
	 * ChromaKeyFilter を構築します。
	 * 
	 * @param background 背景色
	 */
	public ChromakeyImageFilter(int background)
	{
		this.background = background;
	}

	/**
	 * @see java.awt.image.RGBImageFilter#filterRGB(int, int, int)
	 */
	@Override
	public int filterRGB(int x, int y, int rgb)
	{
		return rgb == background ? 0 : rgb;
	}
}
