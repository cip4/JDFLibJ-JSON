/*
 * The CIP4 Software License, Version 1.0
 *
 *
 * Copyright (c) 2001-2023 The International Cooperation for the Integration of Processes in Prepress, Press and Postpress (CIP4). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must include the following acknowledgment: "This product includes software developed by the The International Cooperation for
 * the Integration of Processes in Prepress, Press and Postpress (www.cip4.org)" Alternately, this acknowledgment may appear in the software itself, if and wherever such third-party acknowledgments
 * normally appear.
 *
 * 4. The names "CIP4" and "The International Cooperation for the Integration of Processes in Prepress, Press and Postpress" must not be used to endorse or promote products derived from this software
 * without prior written permission. For written permission, please contact info@cip4.org.
 *
 * 5. Products derived from this software may not be called "CIP4", nor may "CIP4" appear in their name, without prior written permission of the CIP4 organization
 *
 * Usage of this software in commercial products is subject to restrictions. For details please consult info@cip4.org.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE INTERNATIONAL COOPERATION FOR THE INTEGRATION OF PROCESSES IN PREPRESS, PRESS AND POSTPRESS OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE. ====================================================================
 *
 * This software consists of voluntary contributions made by many individuals on behalf of the The International Cooperation for the Integration of Processes in Prepress, Press and Postpress and was
 * originally based on software copyright (c) 1999-2001, Heidelberger Druckmaschinen AG copyright (c) 1999-2001, Agfa-Gevaert N.V.
 *
 * For more information on The International Cooperation for the Integration of Processes in Prepress, Press and Postpress , please see <http://www.cip4.org/>.
 *
 *
 */
/**
 * (C) 2020 Heidelberger Druckmaschinen AG
 */
package org.cip4.lib.jdf.jsonutil;


import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JSONArrayHelperTest extends JSONTestCaseBase
{

	@Test
	public void testSimpleStream()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		assertEquals("d", r.getJSONHelper(0).getPathObject("a/b[0]/c"));
        assertNull(r.getJSONHelper(0).getPathObject("a/b[1]/c"));
		assertEquals("d", r.getJSONHelper(0).getPathObject("a/b/c"));
	}

	@Test
	public void testBadStream()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		assertNull(r.getArray());
	}

	@Test
	public void testSimpleFil()
	{
		final JSONArrayHelper r = new JSONArrayHelper((File) null);
		assertEquals(0, r.size());
	}

	@Test
	public void testGetArray()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		assertNotNull(r.getArray());
	}

	@Test
	public void testSize()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		assertEquals(1, r.size());
	}

	@Test
	public void testSizeList()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		assertEquals(1, r.getJSONObjects().size());
	}

	@Test
	public void testGetJSonHelper()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		assertNotNull(r.getJSONHelper(0));
		assertNull(r.getJSONHelper(42));
	}

	@Test
	public void testGetListString()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		assertNotNull(r.getListString());
	}

	@Test
	public void testToString()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		assertNotNull(r.toString());
	}

	@Test
	public void testGetString()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		assertNull(r.getString(0));
	}

	@Test
	public void testIsEmpty()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		assertFalse(r.isEmpty());
		final JSONArrayHelper r2 = new JSONArrayHelper((String) null);
		assertTrue(r2.isEmpty());
	}

	@Test
	public void testIsEmptyStatic()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		assertFalse(JSONArrayHelper.isEmpty(r));
		final JSONArrayHelper r0 = new JSONArrayHelper();
		assertTrue(JSONArrayHelper.isEmpty(r0));
		assertTrue(JSONArrayHelper.isEmpty(null));
	}

	@Test
	public void testSizeStatic()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		assertEquals(1, JSONArrayHelper.size(r));
		final JSONArrayHelper r0 = new JSONArrayHelper();
		assertEquals(0, JSONArrayHelper.size(r0));
		assertEquals(0, JSONArrayHelper.size(null));
	}

	@Test
	public void testCopyOf()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		assertNotNull(r.copyOf());
	}

	@Test
	public void testAppendUnique()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		final JSONObjHelper o = new JSONObjHelper("{\"c\":\"d\"}");
		assertTrue(r.appendUnique(o));
		assertFalse(r.appendUnique(o));
	}

	@Test
	public void testRemove()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		r.remove(0);
	}

	@Test
	public void testGetObjectsArray()
	{
		JSONArrayHelper a = new JSONArrayHelper("[0,1,2,3,4]");
		List<Object> inheritedObjects = a.getObjects();
		for (int i = 0; i < 5; i++)
			assertEquals(Long.valueOf(i), inheritedObjects.get(i));
		assertEquals(5, inheritedObjects.size());
	}

	@Test
	public void testGetInheritedObjectsArray()
	{
		JSONArrayHelper a = new JSONArrayHelper("[0,1,2,[3,4]]");
		List<Object> inheritedObjects = a.getObjects();
		for (int i = 0; i < 5; i++)
			assertEquals(Long.valueOf(i), inheritedObjects.get(i));
		assertEquals(5, inheritedObjects.size());
	}

}