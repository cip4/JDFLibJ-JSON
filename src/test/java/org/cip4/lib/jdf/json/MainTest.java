package org.cip4.lib.jdf.json;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class MainTest
{

	@Test
	public void testMain()
	{
		Main.main(new String[] {});
	}

	@Test
	public void testMain2()
	{
		assertNotNull(new Main().getJdfLibVersion());
	}

}
