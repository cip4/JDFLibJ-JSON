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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JSONArrayHelperTest extends JSONTestCaseBase
{

	@Test
    void testSimpleStream()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		Assertions.assertEquals("d", r.getJSONHelper(0).getPathObject("a/b[0]/c"));
		Assertions.assertEquals(null, r.getJSONHelper(0).getPathObject("a/b[1]/c"));
		Assertions.assertEquals("d", r.getJSONHelper(0).getPathObject("a/b/c"));
	}

	@Test
    void testBadStream()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		Assertions.assertNull(r.getArray());
	}

	@Test
    void testSimpleFil()
	{
		final JSONArrayHelper r = new JSONArrayHelper((File) null);
		Assertions.assertEquals(0, r.size());
	}

	@Test
    void testGetArray()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		Assertions.assertNotNull(r.getArray());
	}

	@Test
    void testSize()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		Assertions.assertEquals(1, r.size());
	}

	@Test
    void testSizeList()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		Assertions.assertEquals(1, r.getJSONObjects().size());
	}

	@Test
    void testGetJSonHelper()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		Assertions.assertNotNull(r.getJSONHelper(0));
		Assertions.assertNull(r.getJSONHelper(42));
	}

	@Test
    void testGetListString()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		Assertions.assertNotNull(r.getListString());
	}

	@Test
    void testToString()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		Assertions.assertNotNull(r.toString());
	}

	@Test
    void testGetString()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		Assertions.assertNull(r.getString(0));
	}

	@Test
    void testIsEmpty()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		Assertions.assertFalse(r.isEmpty());
		final JSONArrayHelper r2 = new JSONArrayHelper((String) null);
		Assertions.assertTrue(r2.isEmpty());
	}

	@Test
    void testIsEmptyStatic()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		Assertions.assertFalse(JSONArrayHelper.isEmpty(r));
		final JSONArrayHelper r0 = new JSONArrayHelper();
		Assertions.assertTrue(JSONArrayHelper.isEmpty(r0));
		Assertions.assertTrue(JSONArrayHelper.isEmpty(null));
	}

	@Test
    void testSizeStatic()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		Assertions.assertEquals(1, JSONArrayHelper.size(r));
		final JSONArrayHelper r0 = new JSONArrayHelper();
		Assertions.assertEquals(0, JSONArrayHelper.size(r0));
		Assertions.assertEquals(0, JSONArrayHelper.size(null));
	}

	@Test
    void testCopyOf()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		Assertions.assertNotNull(r.copyOf());
	}

	@Test
    void testAppendUnique()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		final JSONObjHelper o = new JSONObjHelper("{\"c\":\"d\"}");
		Assertions.assertTrue(r.appendUnique(o));
		Assertions.assertFalse(r.appendUnique(o));
	}

	@Test
    void testRemove()
	{
		final String root = "[{\"a\":{\"b\":[{\"c\":\"d\"}]}}]";
		final JSONArrayHelper r = new JSONArrayHelper(root);
		r.remove(0);
	}

	@Test
    void testGetObjectsArray()
	{
		JSONArrayHelper a = new JSONArrayHelper("[0,1,2,3,4]");
		List<Object> inheritedObjects = a.getObjects();
		for (int i = 0; i < 5; i++)
			Assertions.assertEquals(Long.valueOf(i), inheritedObjects.get(i));
		Assertions.assertEquals(5, inheritedObjects.size());
	}

	@Test
    void testGetInheritedObjectsArray()
	{
		JSONArrayHelper a = new JSONArrayHelper("[0,1,2,[3,4]]");
		List<Object> inheritedObjects = a.getObjects();
		for (int i = 0; i < 5; i++)
			Assertions.assertEquals(Long.valueOf(i), inheritedObjects.get(i));
		Assertions.assertEquals(5, inheritedObjects.size());
	}

}