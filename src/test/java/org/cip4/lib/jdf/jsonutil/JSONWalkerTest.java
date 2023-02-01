/*
 * The CIP4 Software License, Version 1.0
 *
 *
 * Copyright (c) 2001-2023 The International Cooperation for the Integration of Processes in Prepress, Press and Postpress (CIP4). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must include the following acknowledgment: "This product includes
 * software developed by the The International Cooperation for the Integration of Processes in Prepress, Press and Postpress (www.cip4.org)"
 * Alternately, this acknowledgment may appear in the software itself, if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "CIP4" and "The International Cooperation for the Integration of Processes in Prepress, Press and Postpress" must not be used
 * to endorse or promote products derived from this software without prior written permission. For written permission, please contact
 * info@cip4.org.
 *
 * 5. Products derived from this software may not be called "CIP4", nor may "CIP4" appear in their name, without prior written permission of
 * the CIP4 organization
 *
 * Usage of this software in commercial products is subject to restrictions. For details please consult info@cip4.org.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE INTERNATIONAL COOPERATION FOR THE INTEGRATION OF
 * PROCESSES IN PREPRESS, PRESS AND POSTPRESS OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many individuals on behalf of the The International Cooperation for the
 * Integration of Processes in Prepress, Press and Postpress and was originally based on software copyright (c) 1999-2001, Heidelberger
 * Druckmaschinen AG copyright (c) 1999-2001, Agfa-Gevaert N.V.
 *
 * For more information on The International Cooperation for the Integration of Processes in Prepress, Press and Postpress , please see
 * <http://www.cip4.org/>.
 *
 *
 */
/**
 * (c) 2020 Heidelberger Druckmaschinen AG
 */

package org.cip4.lib.jdf.jsonutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.json.simple.JSONObject;
import org.junit.Test;

public class JSONWalkerTest extends JSONTestCaseBase
{

	class TestWalker extends JSONWalker
	{

		public TestWalker(JSONObjHelper root)
		{
			super(root);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Object walkSimple(String key, Object a)
		{
			log.info(key);
			return a;
		}

	}

	@Test
	public void testJW()
	{
		JSONWalker w = new TestWalker(new JSONObjHelper(new JSONObject()));
		assertNotNull(w.toString());
	}

	@Test
	public void testWalk()
	{
		JSONWalker w = new TestWalker(new JSONObjHelper("{\"a\":{\"b\":[{\"c\":\"d\"}]}}"));
		assertNotNull(w.walk());
	}

	@Test
	public void testWalkNull()
	{
		String s0 = "{\"a\":{\"b\":[{\"c\":null}]}}";
		JSONWalker w = new TestWalker(new JSONObjHelper(s0));
		w.setRetainNull(true);
		assertTrue(w.isRetainNull());
		JSONObjHelper walk = w.walk();
		String s = walk.toJSONString();
		assertEquals(s0, s);
	}

	@Test
	public void testWalkNullArray()
	{
		String s0 = "{\"a\":{\"b\":[{\"c\":[]}]}}";
		JSONWalker w = new TestWalker(new JSONObjHelper(s0));
		w.setRetainNull(true);
		assertTrue(w.isRetainNull());
		JSONObjHelper walk = w.walk();
		String s = walk.toJSONString();
		assertEquals(s0, s);
	}

	@Test
	public void testWalkNullZapp()
	{
		String s0 = "{\"a\":{\"b\":[{\"c\":null}]}}";
		JSONWalker w = new TestWalker(new JSONObjHelper(s0));
		w.setRetainNull(false);
		JSONObjHelper walk = w.walk();
		assertNull(walk);
	}

	@Test
	public void testWalkNullZapp1()
	{
		String s0 = "{\"a\":{\"b\":[{\"c\":null,\"c1\":\"\"}]}}";
		JSONWalker w = new TestWalker(new JSONObjHelper(s0));
		w.setRetainNull(false);
		JSONObjHelper walk = w.walk();
		assertNotNull(walk);
		assertEquals(-1, walk.toJSONString().indexOf("null"));
	}

	@Test
	public void testToString()
	{
		JSONWalker w = new TestWalker(new JSONObjHelper("{\"a\":{\"b\":[{\"c\":\"d\"}]}}"));
		assertNotNull(w.toString());
	}

}
