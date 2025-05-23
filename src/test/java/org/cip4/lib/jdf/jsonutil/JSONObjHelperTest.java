/*
 * The CIP4 Software License, Version 1.0
 *
 *
 * Copyright (c) 2001-2025 The International Cooperation for the Integration of Processes in Prepress, Press and Postpress (CIP4). All rights reserved.
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
 *//**
	* (C) 2020-2021 Heidelberger Druckmaschinen AG
	*/
package org.cip4.lib.jdf.jsonutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

class JSONObjHelperTest extends JSONTestCaseBase
{

	@Test
	void testSimpleStream()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":\"d\"}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals("d", r.getPathObject("a/b[0]/c"));
		assertEquals(null, r.getPathObject("a/b[1]/c"));
		assertEquals("d", r.getPathObject("a/b/c"));
	}

	@Test
	void testRootName()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":\"d\"}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals("a", r.getRootNames().get(0));
		assertEquals("a", r.getRootName());
	}

	@Test
	void testBlank()
	{
		final String root = "{\"a b c\":{\"b\":[{\"c\":\"d\"}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals("a b c", r.getRootNames().get(0));
		assertEquals("a b c", r.getRootName());
	}

	@Test
	void testAdd()
	{
		final JSONObjHelper r = new JSONObjHelper(new JSONObject());
		assertNotNull(r.setArray("gg"));
	}

	@Test
	void testAdd2()
	{
		final JSONObjHelper r = new JSONObjHelper(new JSONObject());
		r.setObj("a", new JSONObjHelper("b", "c"));
		final JSONObject o = r.getObject("a");
		assertTrue(o instanceof JSONObject);
	}

	@Test
	void testSimpleFile()
	{
		final JSONObjHelper r = new JSONObjHelper((File) null);
		assertTrue(r.isNull());
	}

	@Test
	void testUndertoCamel()
	{
		assertNull(JSONObjHelper.undertocamel(""));
		assertNull(JSONObjHelper.undertocamel(""));
		assertEquals("AaBb", JSONObjHelper.undertocamel("AA_BB"));
	}

	@Test
	void testUndertoCamelLower()
	{
		assertEquals("AaBb", JSONObjHelper.undertocamel("aa_bb"));
	}

	@Test
	void testUndertoCamelLower1()
	{
		assertEquals("Aa", JSONObjHelper.undertocamel("aa"));
	}

	@Test
	void testUndertoCamel1()
	{
		assertEquals("Aa", JSONObjHelper.undertocamel("AA"));
	}

	@Test
	void testUndertoCamelCamel()
	{
		assertEquals("AaBb", JSONObjHelper.undertocamel("AaBb"));
	}

	@Test
	void testGetInt()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":4}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals(4, r.getInt("a/b[0]/c", 0));
		assertEquals(0, r.getInt("a/b", 0));
	}

	@Test
	void testGetInt2()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":\"4\"}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals(4, r.getInt("a/b[0]/c", 0));
	}

	@Test
	void testEquals()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":4}]}}";
		final String root2 = "{\"a\":{\"b\":[{\"c\":4}]}}";
		final String root3 = "{\"a\":{\"b\":[{\"c\":5}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		final JSONObjHelper r2 = new JSONObjHelper(root2);
		final JSONObjHelper r3 = new JSONObjHelper(root3);
		assertEquals(r, r2);
		assertNotEquals(r, r3);
	}

	@Test
	void testHash()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":4}]}}";
		final String root2 = "{\"a\":{\"b\":[{\"c\":4}]}}";
		final String root3 = "{\"a\":{\"b\":[{\"c\":5}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		final JSONObjHelper r2 = new JSONObjHelper(root2);
		final JSONObjHelper r3 = new JSONObjHelper(root3);
		assertEquals(r.hashCode(), r2.hashCode());
		assertNotEquals(r.hashCode(), r3.hashCode());
	}

	@Test
	void testIsNull()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":4}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertFalse(r.isNull());
	}

	@Test
	void testIsEmpty()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":4}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertFalse(r.isEmpty());
		final JSONObjHelper r0 = new JSONObjHelper();
		assertTrue(r0.isEmpty());
	}

	@Test
	void testIsEmptyStatic()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":4}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertFalse(JSONObjHelper.isEmpty(r));
		final JSONObjHelper r0 = new JSONObjHelper();
		assertTrue(JSONObjHelper.isEmpty(r0));
		assertTrue(JSONObjHelper.isEmpty(null));
	}

	@Test
	void testSizeStatic()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":4}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals(1, JSONObjHelper.size(r));
		final JSONObjHelper r0 = new JSONObjHelper();
		assertEquals(0, JSONObjHelper.size(r0));
		assertEquals(0, JSONObjHelper.size(null));
	}

	@Test
	void testGetRoot()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":4}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertNotNull(r.getRoot());
	}

	@Test
	void testGetRootName()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":4}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals("a", r.getRootName());
	}

	@Test
	void testParseString()
	{
		final String root = "\"a\"";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals("a", r.getRootObject());
	}

	@Test
	void testParseInt()
	{
		final String root = "1";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals(Long.valueOf(1), r.getRootObject());
	}

	@Test
	void testParseDouble()
	{
		final String root = "1.23";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals(Double.valueOf(1.23), r.getRootObject());
	}

	@Test
	void testStaticInputStream() throws IOException
	{
		assertNull(JSONObjHelper.getHelper((InputStream) null));
		assertNull(JSONObjHelper.getHelper(new ByteArrayInputStream(new byte[0])));
		assertNull(JSONObjHelper.getHelper(new ByteArrayInputStream(new byte[] { 0x20 })));
		final String root = "{\"a\":{\"b\":[{\"c\":4}]}}";
		final JSONObjHelper r = JSONObjHelper.getHelper(new ByteArrayInputStream(root.getBytes()));
		assertEquals(root, r.getRoot().toJSONString());
	}

	@Test
	void testBadInputStream() throws IOException
	{
		assertNull(JSONObjHelper.getHelper((InputStream) null));
		final String root = "<html/>";
		final JSONObjHelper r = JSONObjHelper.getHelper(new ByteArrayInputStream(root.getBytes()));
		assertNull(r);
	}

	@Test
	void testInputStream() throws IOException
	{
		final String root = "{\"a\":{\"b\":[{\"c\":4}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals('{', r.getInputStream().read());
	}

	@Test
	void testInputStreamLength() throws IOException
	{
		final String root = "{\"a\":{\"b\":[{\"c\":4}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);

		final byte[] b = new byte[100];
		assertEquals(root.length(), r.getInputStream().read(b));
	}

	@Test
	void testGetString()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":\"d\"}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals("d", r.getString("a/b[0]/c"));
		assertEquals(null, r.getString("a/c"));
		assertNotNull(r.getString("a/b[0]"));
	}

	@Test
	void testToString()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":\"d\"}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertNotNull(r.toString());
		assertNotNull(r.toJSONString());
		assertNotNull(r.getBytes());
	}

	@Test
	void testToStringBackSlash()
	{
		final String root = "{\"c\":\"a\\\\b\"}";

		final JSONObjHelper r = new JSONObjHelper(root);

		final String jsonString = r.toJSONString();
		assertEquals(7, jsonString.indexOf("\\\\"));
		final String s = r.getString("c");
		r.setString("a", s);
		r.setString("b", "b\\b");
		assertNotNull(r.toString());
		assertEquals(r.getString("a"), r.getString("c"));
		assertEquals("a\\b", r.getString("c"));
	}

	@Test
	void testGetHelper()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":\"d\"}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertNotNull(r.getHelper("a"));
	}

	@Test
	void testHasPath()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":\"d\"}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertTrue(r.hasPath("a"));
	}

	@Test
	void testGetDouble()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":4.2}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals(4.2, r.getDouble("a/b[0]/c", 0), 0);
		assertEquals(0, r.getDouble("a/b", 0), 0);
	}

	@Test
	void testGetDouble2()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":\"4.2\"}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals(4.2, r.getDouble("a/b[0]/c", 0), 0);
		assertEquals(0, r.getDouble("a/b", 0), 0);
	}

	@Test
	void testGetDoubleInt()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":4}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals(4, r.getDouble("a/b[0]/c", 0), 0);
	}

	@Test
	void testGetInheritedObject()
	{
		final String root = "{\"a\":{\"b\":{\"c\":4}}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		r.getObject("a").put("c2", "test");
		assertEquals("test", r.getInheritedObject("a/b/c2", true));
		assertEquals(null, r.getInheritedObject("a/b/c3", true));
		assertEquals(null, r.getInheritedObject("a/c/c2", true));
	}

	@Test
	void testGetInheritedObjects()
	{
		final String root = "{\"a\":{\"b\":{\"c\":4}}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		r.getObject("a").put("c2", "test");
		assertEquals("test", r.getInheritedObjects("a/b/c2").get(0));
		assertTrue(r.getInheritedObjects("a/b/c3").isEmpty());
		assertTrue(r.getInheritedObjects("").isEmpty());
	}

	@Test
	void testGetInheritedObjectsArray()
	{
		final String root = "{\"a\":{\"b\":{\"c\":4}}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		r.getObject("a").put("c2", new JSONArrayHelper("[0,1,2,3,4]").getArray());
		final List<Object> inheritedObjects = r.getInheritedObjects("a/b/c2");
		for (int i = 0; i < 5; i++)
			assertEquals(Long.valueOf(i), inheritedObjects.get(i));
		assertEquals(5, inheritedObjects.size());
		assertTrue(r.getInheritedObjects("a/b/c3").isEmpty());
		assertTrue(r.getInheritedObjects("").isEmpty());
	}

	@Test
	void testGetInherited()
	{
		final String root = "{\"a\":{\"b\":{\"c\":4}}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		r.getObject("a").put("c2", "test");
		assertEquals("test", r.getInheritedObject("a/b/c2"));
		assertEquals(null, r.getInheritedObject("a/b/c3"));
		assertEquals(null, r.getInheritedObject("a/c/c2"));
		assertEquals(null, r.getInheritedObject(null));
		assertEquals(null, r.getInheritedObject(""));
	}

	@Test
	void testSetDouble()
	{
		final JSONObjHelper r = new JSONObjHelper(new JSONObject());
		r.setDouble("a/b/c/d", 4.2);
		assertEquals(4.2, r.getDouble("a/b/c/d", 0), 0);
		assertEquals(4, r.getInt("a/b/c/d", 0), 0);
	}

	@Test
	void testSetInt()
	{
		final JSONObjHelper r = new JSONObjHelper(new JSONObject());
		r.setInt("a/b/c/d", 5);
		assertEquals(5, r.getDouble("a/b/c/d", 0), 0);
		assertEquals(5, r.getInt("a/b/c/d", 0), 0);
	}

	@Test
	void testSetBool()
	{
		final JSONObjHelper r = new JSONObjHelper(new JSONObject());
		r.setBool("a/b/c/d", true);
		assertTrue(r.getBool("a/b/c/d", false));
	}

	@Test
	void testSetString()
	{
		final JSONObjHelper r = new JSONObjHelper(new JSONObject());
		r.setString("a/b/c/d", "e");
		assertEquals("e", r.getString("a/b/c/d"));
	}

	@Test
	void testSetStringNull()
	{
		final JSONObjHelper r = new JSONObjHelper(new JSONObject());
		r.setString("a/b/c/d", "");
		assertNull(r.getString("a/b/c/d"));
	}

	@Test
	void testSet2String()
	{
		final JSONObjHelper r = new JSONObjHelper(new JSONObject());
		r.setString("a/b/c/d", "e");
		r.setString("a/b/c/d2", "e2");
		r.setString("a/b/c2/d2", "e3");
		r.setString("a2/b/c2/d2", "e4");
		assertEquals("e", r.getString("a/b/c/d"));
		assertEquals("e2", r.getString("a/b/c/d2"));
		assertEquals("e3", r.getString("a/b/c2/d2"));
		assertEquals("e4", r.getString("a2/b/c2/d2"));
	}

	@Test
	void testGetId()
	{
		final String root = "{\"id\":\"iidd\",\"a\":{\"b\":[{\"c\":4.2}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		assertEquals("iidd", r.getID());
	}

}