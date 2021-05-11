/*
 * The CIP4 Software License, Version 1.0
 *
 *
 * Copyright (c) 2001-2021 The International Cooperation for the Integration of Processes in Prepress, Press and Postpress (CIP4). All rights reserved.
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
 * (C) 2020-2021 Heidelberger Druckmaschinen AG
 */
package org.cip4.lib.jdf.jsonutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;

import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFElement;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.core.XMLDoc;
import org.cip4.jdflib.datatypes.JDFTransferFunction;
import org.cip4.jdflib.resource.process.JDFColorControlStrip;
import org.cip4.jdflib.util.FileUtil;
import org.json.simple.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author prosirai
 *
 */
public class JSONReaderTest extends JSONTestCaseBase
{

	@Test
	public void testSimpleStream()
	{
		final JSONReader r = new JSONReader();
		final String root = "{\"a\":{\"b\":[{\"c\":\"d\"}]}}";
		final KElement a = r.getElement(new ByteArrayInputStream(root.getBytes()));
		assertNotNull(a);
		assertEquals("a", a.getLocalName());
	}

	@Test
	public void testComment()
	{
		final JSONReader r = new JSONReader();
		final String root = "{\"a\":{\"b\":[{\"" + JSONWriter.TEXT + "\":\"d\"}]}}";
		final KElement a = r.getElement(new ByteArrayInputStream(root.getBytes()));
		assertNotNull(a);
		assertEquals("<a><b>d</b></a>", a.toDisplayXML(0));
	}

	@Test
	public void testRootArray()
	{
		final JSONReader r = new JSONReader();
		final String root = "[{\"c\":\"d\"},\"a\"]";
		final KElement a = r.getElement(new ByteArrayInputStream(root.getBytes()));
		assertNotNull(a);
		assertEquals("array", a.getLocalName());
	}

	@Test
	public void testRootNoName()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"id\": 159877, 	\"product_id\": 107274, 	\"created_at\": \"2019-04-01T13:55:45.837Z\", 	\"updated_at\": \"2019-04-01T14:05:49.518Z\"}");
		assertNotNull(a);
		assertEquals("json", a.getLocalName());
		assertEquals("159877", a.getAttribute("id"));
		assertEquals("2019-04-01T13:55:45.837Z", a.getAttribute("created_at"));

	}

	@Test
	public void testSimpleNull()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":{\"b\":[{\"c\":\"d\"}]}}");
		assertNotNull(a);
		assertEquals("a", a.getLocalName());

	}

	@Test
	public void testStringOnly()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":\"b\"}");
		assertEquals("a", a.getLocalName());
		assertEquals("b", a.getText());

	}

	@Test
	public void ArrayrootStringOnly()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("[\"a\",\"b\"]");
		assertEquals("array", a.getLocalName());
		assertEquals("ab", a.getText());

	}

	@Test
	public void testIntOnly()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":2}");
		assertEquals("a", a.getLocalName());
		assertEquals("2", a.getText());

	}

	@Test
	public void testSlashOnly()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":\"a\\/b\"}");
		assertEquals("a", a.getLocalName());
		assertEquals("a/b", a.getText());

	}

	@Test
	public void testBackSlashOnly()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":\"a\\\\b\"}");
		assertEquals("a", a.getLocalName());
		assertEquals("a\\b", a.getText());

	}

	@Test
	public void testdoubleOnly()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":2.33}");
		assertEquals("a", a.getLocalName());
		assertEquals("2.33", a.getText());

	}

	@Test
	public void testBoolOnly()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":true}");
		assertEquals("a", a.getLocalName());
		assertEquals("true", a.getText());

	}

	@Test
	public void testNullOnly()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":null}");
		assertEquals("a", a.getLocalName());
		assertNull(a.getText());

	}

	@Test
	public void testArray()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":{\"b\":[{\"c1\":\"d1\"},{\"c2\":\"d2\"}]}}");
		assertNotNull(a);
		assertEquals("a", a.getLocalName());
		assertNotNull(a.getElement("b"));
		assertNotNull(a.getElement("b", null, 1));

	}

	@Test
	public void testMixedArray()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":{\"b\":[1,{\"c1\":\"d1\"},{\"c2\":\"d2\"},2]}}");
		assertNotNull(a);
		assertEquals("a", a.getLocalName());
		assertNotNull(a.getElement("b"));
		assertNotNull(a.getElement("b", null, 1));
		assertEquals("1 2", a.getElement("b").getText());
		assertEquals("1 2", a.getElement("b", null, 1).getText());

	}

	@Test
	public void testMixedArray3()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":{\"b\":[[1,{\"c1\":\"d1\"}],[{\"c2\":\"d2\"},2]]}}");
		assertNotNull(a);
		assertEquals("a", a.getLocalName());
		assertNotNull(a.getElement("b"));
		assertNotNull(a.getElement("b", null, 1));
		assertEquals("1", a.getElement("b").getText());
		assertEquals("2", a.getElement("b", null, 1).getText());

	}

	@Test
	public void testMixedArray2()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":{\"b\":[\"txt\",{\"c1\":\"d1\"}]}}");
		assertNotNull(a);
		assertEquals("a", a.getLocalName());
		final KElement b = a.getElement("b");
		assertNotNull(b);
		assertNull(a.getElement("b", null, 1));
		assertEquals("txt", b.getText());
		assertEquals("d1", b.getAttribute("c1"));

	}

	@Test
	public void testSimpleArray()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":{\"b\":[1,2,3]}}");
		assertNotNull(a);
		assertEquals("a", a.getLocalName());
		assertNull(a.getElement("b"));
		assertEquals("1 2 3", a.getAttribute("b"));

	}

	@Test
	public void testDoubleSimpleArray()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":{\"b\":[[1,2,3],[4,5],[6],7]}}");
		assertNotNull(a);
		assertEquals("a", a.getLocalName());
		assertNull(a.getElement("b"));
		assertEquals("1 2 3 4 5 6 7", a.getAttribute("b"));

	}

	@Test
	public void testDoubleSimpleArrayElem()
	{
		final JSONReader r = new JSONReader();
		r.setWantAttributes(false);
		final KElement a = r.getElement("{\"a\":{\"b\":[[1,2,3],[4,5],[6],7]}}");
		assertNotNull(a);
		assertEquals("a", a.getLocalName());
		assertEquals("2", a.getXPathAttribute("b[1]/b[2]", null));
		assertEquals("5", a.getXPathAttribute("b[2]/b[2]", null));
		assertEquals("6", a.getXPathAttribute("b[3]/b[1]", null));
		assertEquals("7", a.getXPathAttribute("b[4]", null));

	}

	@Test
	public void testTripleSimpleArray()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":{\"b\":[[[1,2,3],[4,5],[6],7],[[[[[8.9]]],null]]]}}");
		assertNotNull(a);
		assertEquals("a", a.getLocalName());
		assertNull(a.getElement("b"));
		assertEquals("1 2 3 4 5 6 7 8.9", a.getAttribute("b"));

	}

	@Test
	public void testSimpleObj()
	{
		final XMLDoc d = new XMLDoc();
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":{\"b\":{\"c1\":\"d1\",\"c2\":\"d2\"}}}");
		assertNotNull(a);
		assertEquals("a", a.getLocalName());
		assertNotNull(a.getElement("b"));
		assertNull(a.getElement("b", null, 1));

	}

	@Test
	public void testSimpleObjNS()
	{
		final XMLDoc d = new XMLDoc();
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"x:a\":{\"x:b\":{\"y:c1\":\"d1\",\"z:c2\":\"d2\"}}}");
		assertNotNull(a);
		assertEquals("a", a.getLocalName());
		assertNotNull(a.getElement("b"));
		assertNull(a.getElement("b", null, 1));

	}

	@Test
	public void testDoubleArray1()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":[{\"b\":[{\"c1\":\"d1\"},{\"c2\":\"d2\"}]}]}");
		assertNotNull(a);
		assertEquals("a", a.getLocalName());
		assertNotNull(a.getElement("b"));
		assertNotNull(a.getElement("b", null, 1));

	}

	@Test
	public void testDoubleArray2()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":[[{\"b\":[{\"c1\":\"d1\"},{\"c1\":\"d2\"}]}]]}");
		assertNotNull(a);
		assertEquals("a", a.getLocalName());
		assertNotNull(a.getElement("b"));
		assertNotNull(a.getElement("b", null, 1));

	}

	@Test
	public void testDoubleArray3()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"a\":[{\"b\":[[{\"c1\":\"d1\"},{\"c2\":\"d2\"}]]}]}");
		assertNotNull(a);
		assertEquals("a", a.getLocalName());
		assertNotNull(a.getElement("b"));
		assertNull(a.getElement("b").getElement("b"));
		assertNotNull(a.getElement("b", null, 1));

	}

	@Test
	public void testElem()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"XJDF\":1}");
		assertTrue(a instanceof JDFElement);

	}

	@Test
	public void testTextArray()
	{
		final JSONReader r = new JSONReader();
		final KElement a = r.getElement("{\"r\":{\"a\":[\"a1\",\"a2\"]}}");
		assertEquals("a1 a2", a.getAttribute("a"));

	}

	/**
	 *
	 */
	@Test
	public void testCreateRoot()
	{
		final JSONReader r = new JSONReader();
		assertTrue(r.createRoot("XJDF") instanceof JDFElement);
		assertTrue(r.createRoot("XJMF") instanceof JDFElement);
		assertFalse(r.createRoot("xxx") instanceof JDFElement);
	}

	/**
	 *
	 */
	@Test
	@Ignore
	public void testQCTransferCurve()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(getXJDFSchemaElement(MINOR));
		FileUtil.streamToFile(jsonWriter.getStream(KElement.parseFile(sm_dirTestData + "xjdf/QualityControlColorSpectrum.xjdf")), sm_dirTestDataTemp
				+ "json/QualityControlColorSpectrum.json");
		final JSONObject o = jsonWriter.getRoot();
		final JSONReader r = new JSONReader();
		final KElement e = r.getElement(o);
		e.write2File(sm_dirTestDataTemp + "json/QualityControlColorSpectrum.json.xjdf");
		final JDFColorControlStrip ccs = (JDFColorControlStrip) e.getChildByTagName(ElementName.COLORCONTROLSTRIP, null, 0, null, false, false);
		for (int i = 0; i < 10; i++)
		{
			assertEquals(JDFTransferFunction.createTransferFunction("400 0 450 0.5 500 1.0 550 0.8 600 0.3 650 0.2 700 0"), ccs.getPatch(i).getSpectrum());
		}
	}

}
