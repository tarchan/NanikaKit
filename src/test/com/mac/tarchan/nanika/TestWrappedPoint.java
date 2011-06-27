/*
 * TestWrappedPoint.java
 * NanikaKit
 *
 * Created by tarchan on 2008/04/10.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package test.com.mac.tarchan.nanika;

import static org.junit.Assert.*;

import java.awt.Rectangle;
import org.junit.Test;
import com.mac.tarchan.geom.WrappedPoint;


/**
 * @since 1.0
 * @author tarchan
 */
public class TestWrappedPoint
{
	/**
	 * 
	 */
	@Test
	public void test001()
	{
		assertEquals(100, new WrappedPoint(100, 50).x);
	}

	/**
	 * 
	 */
	@Test
	public void test002()
	{
		assertEquals(-10, new WrappedPoint(-10, 50).x);
	}

	/**
	 * 
	 */
	@Test
	public void test003()
	{
		assertEquals(90, new WrappedPoint(-10, 50).setBounds(new Rectangle(100, 100)).x);
	}

	/**
	 * 
	 */
	@Test
	public void test004()
	{
		assertEquals(50, new WrappedPoint(-10, -50).setBounds(new Rectangle(100, 100)).y);
	}

	/**
	 * 
	 */
	@Test
	public void test005()
	{
		assertEquals(20, new WrappedPoint(10, 50).setBounds(new Rectangle(10, 10, 100, 100)).x);
	}

	/**
	 * 
	 */
	@Test
	public void test006()
	{
		assertEquals(60, new WrappedPoint(10, 50).setBounds(new Rectangle(10, 10, 100, 100)).y);
	}

	/**
	 * 
	 */
	@Test
	public void test007()
	{
		assertEquals(100, new WrappedPoint(-10, -50).setBounds(new Rectangle(10, 10, 100, 100)).x);
	}

	/**
	 * 
	 */
	@Test
	public void test008()
	{
		assertEquals(60, new WrappedPoint(-10, -50).setBounds(new Rectangle(10, 10, 100, 100)).y);
	}
}
