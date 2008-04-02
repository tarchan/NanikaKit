/*
 * ChromaKeyOp.java
 * NanikaKit
 *
 * Created by tarchan on 2008/03/31.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package com.mac.tarchan.image;

import java.awt.image.ColorConvertOp;

/**
 * このクラスは、クロマキー合成を実行します。
 * 
 * @version 1.0
 * @author tarchan
 */
public class ChromakeyOp extends ColorConvertOp
{
	/**
	 * ChromaKeyOp を構築します。
	 */
	public ChromakeyOp()
	{
		super(null);
	}
}
