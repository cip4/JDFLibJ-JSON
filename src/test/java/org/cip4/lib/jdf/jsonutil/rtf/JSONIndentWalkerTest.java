/**
 * The CIP4 Software License, Version 1.0
 *
 * Copyright (c) 2001-2024 The International Cooperation for the Integration of
 * Processes in  Prepress, Press and Postpress (CIP4).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        The International Cooperation for the Integration of
 *        Processes in  Prepress, Press and Postpress (www.cip4.org)"
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "CIP4" and "The International Cooperation for the Integration of
 *    Processes in  Prepress, Press and Postpress" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact info@cip4.org.
 *
 * 5. Products derived from this software may not be called "CIP4",
 *    nor may "CIP4" appear in their name, without prior written
 *    permission of the CIP4 organization
 *
 * Usage of this software in commercial products is subject to restrictions. For
 * details please consult info@cip4.org.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE INTERNATIONAL COOPERATION FOR
 * THE INTEGRATION OF PROCESSES IN PREPRESS, PRESS AND POSTPRESS OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the The International Cooperation for the Integration
 * of Processes in Prepress, Press and Postpress and was
 * originally based on software
 * copyright (c) 1999-2001, Heidelberger Druckmaschinen AG
 * copyright (c) 1999-2001, Agfa-Gevaert N.V.
 *
 * For more information on The International Cooperation for the
 * Integration of Processes in  Prepress, Press and Postpress , please see
 * <http://www.cip4.org/>.
 *
 *
 */
package org.cip4.lib.jdf.jsonutil.rtf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.cip4.jdflib.util.ByteArrayIOStream;
import org.cip4.jdflib.util.FileUtil;
import org.cip4.lib.jdf.jsonutil.JSONObjHelper;
import org.cip4.lib.jdf.jsonutil.JSONTestCaseBase;
import org.junit.jupiter.api.Test;

class JSONIndentWalkerTest extends JSONTestCaseBase
{

	@Test
	void testSimple() throws IOException
	{
		final JSONObjHelper root = new JSONObjHelper("{\"a\":{\"b\":[{\"c\":\"d\"}]}}");
		final JSONIndentWalker w = new JSONIndentWalker(root);
		final ByteArrayIOStream ios = new ByteArrayIOStream();
		w.writeStream(ios);
		final String s = new String(ios.toByteArray());
		assertNotNull(s);
		assertEquals(root, new JSONObjHelper(s));

	}

	@Test
	void testFile() throws IOException
	{
		final JSONIndentWalker w = new JSONIndentWalker(new JSONObjHelper("{\"a\":{\"b\":[{\"c1\":\"d1\",\"e1\":\"e2\"},{\"c2\":\"d2\"}]}}"));
		FileUtil.writeFile(w, new File(sm_dirTestDataTemp + "test.json"));
	}

	@Test
	void testToString() throws IOException
	{
		final JSONIndentWalker w = new JSONIndentWalker(new JSONObjHelper("{\"a\":{\"b\":[{\"c1\":\"d1\",\"e1\":\"e2\"},{\"c2\":\"d2\"}]}}"));
		assertNotNull(w.toString());
	}

	@Test
	void testIndent() throws IOException
	{
		final JSONIndentWalker w = new JSONIndentWalker(new JSONObjHelper("{\"a\":{\"b\":[{\"c1\":\"d1\",\"e1\":\"e2\"},{\"c2\":\"d2\"}]}}"));
		w.setSingleIndent(0);
		FileUtil.writeFile(w, new File(sm_dirTestDataTemp + "test0.json"));
		assertEquals(0, w.getSingleIndent());

		final JSONIndentWalker w4 = new JSONIndentWalker(new JSONObjHelper("{\"a\":{\"b\":[{\"c1\":\"d1\",\"e1\":\"e2\"},{\"c2\":\"d2\"}]}}"));
		w.setSingleIndent(4);
		FileUtil.writeFile(w4, new File(sm_dirTestDataTemp + "test4.json"));

	}

	@Test
	void testCondense() throws IOException
	{
		final JSONIndentWalker w = new JSONIndentWalker(new JSONObjHelper("{\"a\":{\"b\":[{\"c1\":\"d1\",\"e1\":\"e2\"},{\"c2\":\"d2\"}]}}"));
		w.setSingleIndent(0);
		w.setCondensed(true);
		FileUtil.writeFile(w, new File(sm_dirTestDataTemp + "testc0.json"));
		assertTrue(w.isCondensed());

	}

	@Test
	void testCondensedEmptyArray() throws IOException
	{
		final JSONObjHelper root = new JSONObjHelper("{\"a\":{\"b\":[],\"c\":[]}}");
		final JSONIndentWalker w = new JSONIndentWalker(root);
		w.setSingleIndent(0);
		w.setCondensed(true);
		w.setRetainNull(true);
		final File file = new File(sm_dirTestDataTemp + "testce.json");
		FileUtil.writeFile(w, file);
		assertTrue(w.isCondensed());
		final JSONObjHelper roundTrip = new JSONObjHelper(file);
		assertEquals(root, roundTrip);
	}

	@Test
	void testNoRetainNullEmpty() throws IOException
	{
		final JSONObjHelper root = new JSONObjHelper("{\"a\":{\"b\":[],\"c\":{}}}");
		final JSONIndentWalker w = new JSONIndentWalker(root);
		w.setSingleIndent(0);
		w.setCondensed(true);
		w.setRetainNull(false);
		final File file = new File(sm_dirTestDataTemp + "testretnull.json");
		FileUtil.writeFile(w, file);
		assertTrue(w.isCondensed());
		final JSONObjHelper roundTrip = new JSONObjHelper(file);
		assertEquals(new JSONObjHelper("{}"), roundTrip);
	}

	@Test
	void testNoRetainNull() throws IOException
	{
		final JSONObjHelper root = new JSONObjHelper("{\"a\":{\"b\":[{\"d\":1},{},{\"d\":1}],\"c\":{\"c1\":\"a\",\"c2\":{},\"c3\":\"a\"}}}");
		final JSONObjHelper root2 = new JSONObjHelper("{\"a\":{\"b\":[{\"d\":1},{\"d\":1}],\"c\":{\"c1\":\"a\",\"c3\":\"a\"}}}");
		final JSONIndentWalker w = new JSONIndentWalker(root);
		w.setSingleIndent(0);
		w.setCondensed(true);
		w.setRetainNull(false);
		final File file = new File(sm_dirTestDataTemp + "testret.json");
		FileUtil.writeFile(w, file);
		assertTrue(w.isCondensed());
		final JSONObjHelper roundTrip = new JSONObjHelper(file);
		assertEquals(root2, roundTrip);
	}

}
