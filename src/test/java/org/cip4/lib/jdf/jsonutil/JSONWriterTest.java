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
 *
 * (c) 2016-2020 Heidelberger Druckmaschinen AG
 *
 */
package org.cip4.lib.jdf.jsonutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.cip4.jdflib.auto.JDFAutoDeviceFilter.EnumDeviceDetails;
import org.cip4.jdflib.auto.JDFAutoStatusQuParams.EnumJobDetails;
import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.core.JDFElement.EnumVersion;
import org.cip4.jdflib.core.JDFNodeInfo;
import org.cip4.jdflib.core.JDFResourceLink.EnumUsage;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.elementwalker.ElementWalker;
import org.cip4.jdflib.extensions.MessageResourceHelper;
import org.cip4.jdflib.extensions.SetHelper;
import org.cip4.jdflib.extensions.XJDFConstants;
import org.cip4.jdflib.extensions.XJDFHelper;
import org.cip4.jdflib.extensions.XJMFHelper;
import org.cip4.jdflib.extensions.xjdfwalker.jdftoxjdf.JDFToXJDF;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage.EnumFamily;
import org.cip4.jdflib.jmf.JMFBuilderFactory;
import org.cip4.jdflib.node.JDFNode;
import org.cip4.jdflib.node.JDFNode.EnumType;
import org.cip4.jdflib.resource.intent.JDFLayoutIntent;
import org.cip4.jdflib.util.ByteArrayIOStream;
import org.cip4.jdflib.util.ByteArrayIOStream.ByteArrayIOInputStream;
import org.cip4.jdflib.util.FileUtil;
import org.cip4.jdflib.util.StringUtil;
import org.cip4.jdflib.util.UrlUtil;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONCase;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONPrefix;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONRoot;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author rainer prosi
 */
class JSONWriterTest extends JSONTestCaseBase
{

	/**
	 *
	 */
	@Test
	void testConvert()
	{
		final JDFJMF jmf = JMFBuilderFactory.getJMFBuilder(null).buildStatusSignal(EnumDeviceDetails.Full, EnumJobDetails.MIS);
		final KElement xjmf = new JDFToXJDF().convert(jmf);
		final JSONObject o = new JSONWriter().convert(xjmf);
		assertNotNull(o.toJSONString());
		log.info(o.toJSONString());
	}

	/**
	 *
	 */
	@Test
	void testConvertRoot()
	{
		final JDFJMF jmf = JMFBuilderFactory.getJMFBuilder(null).buildStatusSignal(EnumDeviceDetails.Full, EnumJobDetails.MIS);
		final KElement xjmf = new JDFToXJDF().convert(jmf);
		for (final eJSONRoot r : eJSONRoot.values())
		{
			final JSONWriter jsonWriter = new JSONWriter();
			jsonWriter.setJsonRoot(r);
			final JSONObject o = jsonWriter.convert(xjmf);
			new JSONObjHelper(o).writeToFile(sm_dirTestDataTemp + r.name() + ".json");
			assertNotNull(o.toJSONString());
			log.info(o.toJSONString());
		}
	}

	@Test
	void testEquals()
	{
		final JSONWriter w1 = new JSONWriter();
		final JSONWriter w2 = new JSONWriter();
		assertEquals(w1, w2);
		w2.setLearnArrays(false);
		w1.setLearnArrays(true);
		assertNotEquals(w1, w2);
	}

	/**
	 *
	 */
	@Test
	void testConvertSchema()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		final KElement xjdfSchemaElement = getXJDFSchemaElement(1);
		jsonWriter.convert(xjdfSchemaElement);
		jsonWriter.setKeyCase(eJSONCase.lower);
		jsonWriter.convert(xjdfSchemaElement);
		jsonWriter.convert(xjdfSchemaElement);
		jsonWriter.writeToFile(sm_dirTestDataTemp + "XJDF.xsd.json");

	}

	@Test
	void testHash()
	{
		final JSONWriter w1 = new JSONWriter();
		final JSONWriter w2 = new JSONWriter();
		assertEquals(w1.hashCode(), w2.hashCode());
	}

	@Test
	void testClear()
	{
		final JSONWriter w1 = new JSONWriter();
		w1.clearArray();
	}

	@Test
	void testRemoveArray()
	{
		final JSONWriter w1 = new JSONWriter();
		w1.removeArray("a");
	}

	@Test
	void testToString()
	{
		final JSONWriter w1 = new JSONWriter();
		assertNotNull(w1.toString());
	}

	@Test
	void testPrepWalker()
	{
		final JSONWriter w = new JSONWriter();
		assertNull(w.getPrepWalker());
		w.setPrepWalker(new ElementWalker(null));
	}

	@Test
	void testPrefix()
	{
		final JSONWriter w = new JSONWriter();
		w.setPrefix(eJSONPrefix.none);
		assertEquals(eJSONPrefix.none, w.getPrefix());
	}

	@Test
	void testTypesafe()
	{
		final JSONWriter w = new JSONWriter();
		w.setTypeSafe(true);
		assertTrue(w.isTypeSafe());
	}

	@Test
	void tesMixText()
	{
		final JSONWriter w = new JSONWriter();
		w.setMixedText("a");
		assertEquals("a", w.getMixedText());
	}

	@Test
	void testWantArray()
	{
		final JSONWriter w = new JSONWriter();
		w.setWantArray(true);
		assertTrue(w.isWantArray());
	}

	@Test
	void testPrefix2()
	{
		for (final String n : eJSONPrefix.getNames())
		{
			assertNotNull(eJSONPrefix.getEnum(n));
		}
	}

	@Test
	void testCase()
	{
		for (final String n : eJSONCase.getNames())
		{
			assertNotNull(eJSONCase.getEnum(n));
		}
	}

	@Test
	void testCase2()
	{
		final JSONWriter w = new JSONWriter();
		for (final eJSONCase n : eJSONCase.values())
		{
			assertNotNull(w.getKey("a:b", n));
			w.setKeyCase(n);
			assertEquals(n, w.getKeyCase());
			w.setValueCase(n);
			assertEquals(n, w.getValueCase());
		}
	}

	/**
	 *
	 */
	@Test
	void testGetString()
	{
		final JDFJMF jmf = JMFBuilderFactory.getJMFBuilder(null).buildStatusSignal(EnumDeviceDetails.Full, EnumJobDetails.MIS);
		final KElement xjmf = new JDFToXJDF().convert(jmf);
		final String s = new JSONWriter().getString(xjmf);
		assertNotNull(s);
		log.info(s);
	}

	/**
	 * @throws UnsupportedEncodingException
	 */
	@Test
	void testGetStream() throws UnsupportedEncodingException
	{
		final JDFJMF jmf = JMFBuilderFactory.getJMFBuilder(null).buildStatusSignal(EnumDeviceDetails.Full, EnumJobDetails.MIS);
		final KElement xjmf = new JDFToXJDF().convert(jmf);
		final InputStream s = new JSONWriter().getStream(xjmf);
		final ByteArrayIOInputStream ios = ByteArrayIOStream.getBufferedInputStream(s);

		final String st = new String(ios.getBuf(), 0, ios.getBuf().length, StringUtil.UTF8);
		assertNotNull(st);
		log.info(st);
	}

	/**
	 *
	 */
	@Test
	void testConvertArray()
	{
		final JDFJMF jmf = JMFBuilderFactory.getJMFBuilder(null).buildStatusSignal(EnumDeviceDetails.Full, EnumJobDetails.MIS);

		final KElement xjmf = new JDFToXJDF().convert(jmf);
		final JSONObject o = new JSONWriter().convert(xjmf);
		assertNotNull(o.toJSONString());
		log.info(o.toJSONString());
	}

	/**
	 *
	 */
	@Test
	void testConvertArrayEmpty()
	{
		final KElement e = KElement.parseString("<e><a/><a/><a b=\"c\"/><a/></e>");
		final JSONObject o = new JSONWriter().convert(e);
		assertNotNull(o.toJSONString());
		log.info(o.toJSONString());
		assertEquals(4, new JSONObjHelper(o).getArray("e/a", false).size());
	}

	/**
	 *
	 */
	@Test
	void testNumberArray()
	{
		final KElement e = KElement.createRoot("a", null);
		e.setAttribute("XY", "1 2 3");
		final JSONObject o = new JSONWriter().convert(e);
		final String jsonString = o.toJSONString();
		assertNotNull(jsonString);
		assertTrue(jsonString.indexOf("[1,2,3]") > 0);

		log.info(jsonString);
	}

	/**
	 *
	 */
	@Test
	void testSkipKey()
	{
		final KElement e0 = KElement.createRoot("a", null);
		final KElement b = e0.appendElement("b");
		final KElement e = b.appendElement("e");
		e.setAttribute("XY", "1 2 3");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.addSkipPool("b");
		jsonWriter.setWantArray(false);
		final JSONObject o = jsonWriter.convert(e0);
		final String jsonString = o.toJSONString();
		assertNotNull(jsonString);
		final JSONObjHelper h = new JSONObjHelper(jsonString);
		assertNull(h.getObject("a/b"));
		assertNotNull(h.getObject("a/e"));
		assertTrue(jsonString.indexOf("[1,2,3]") > 0);

		log.info(jsonString);
	}

	/**
	 *
	 */
	@Test
	void testAddNull()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.addSkipPool("b");
		jsonWriter.setWantArray(false);
		assertFalse(jsonWriter.addSkipPool(null));
		assertFalse(jsonWriter.addArray(null));
		assertFalse(jsonWriter.addMixed(null));
		assertFalse(jsonWriter.addStringArray(null));
		assertFalse(jsonWriter.addKnownAttribute(null));
		assertFalse(jsonWriter.addKnownElem(null));
	}

	/**
	 *
	 */
	@Test
	void testAddNotNull()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.addSkipPool("b");
		jsonWriter.setWantArray(false);
		assertTrue(jsonWriter.addSkipPool("a"));
		assertTrue(jsonWriter.addArray("a"));
		assertTrue(jsonWriter.addMixed("a"));
		assertTrue(jsonWriter.addStringArray("a"));
		assertTrue(jsonWriter.addKnownAttribute("a"));
		assertTrue(jsonWriter.addKnownElem("a"));

		assertFalse(jsonWriter.addSkipPool("a"));
		assertFalse(jsonWriter.addArray("a"));
		assertFalse(jsonWriter.addMixed("a"));
		assertFalse(jsonWriter.addStringArray("a"));
		assertFalse(jsonWriter.addKnownAttribute("a"));
		assertFalse(jsonWriter.addKnownElem("a"));
	}

	/**
	 *
	 */
	@Test
	void testConvertMap()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		assertNotNull(jsonWriter.convertMap(null));
	}

	/**
	 *
	 */
	@Test
	void testNumberArray0()
	{
		final JSONArray a = new JSONArray();
		a.add(Integer.valueOf(1));
		a.add(Integer.valueOf(2));
		assertEquals(a, new JSONWriter().getObjectFromVal("aa", "1 2"));
	}

	/**
	 *
	 */
	@Test
	void testTransferFunction()
	{
		final JSONArray a = new JSONArray();
		a.add(Integer.valueOf(0));
		a.add(Integer.valueOf(0));
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.addTransferFunction("aa");
		final JSONArray jarray = (JSONArray) jsonWriter.getObjectFromVal("aa", "0 0 0.1 0.2 1 1");
		assertEquals(a, jarray.get(0));
	}

	/**
	 *
	 */
	@Test
	void testGetKey()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setPrefix(eJSONPrefix.underscore);
		assertEquals("HDM_Foo", jsonWriter.getKey("HDM:Foo", eJSONCase.retain));
		assertNull(jsonWriter.getKey("xmlns", eJSONCase.retain));
	}

	/**
	 *
	 */
	@Test
	void testAddToParentRaw()
	{
		JSONWriter.addToParentRaw(new JSONObject(), "foo", "bar");
		JSONWriter.addToParentRaw(new JSONArray(), "foo", "bar");
	}

	/**
	 *
	 */
	@Test
	void testNumberArray2()
	{
		final JSONArray a = new JSONArray();
		a.add(Integer.valueOf(1));
		a.add(Double.valueOf(2.3));
		assertEquals(a, new JSONWriter().getObjectFromVal("bb", "1 2.3"));
	}

	/**
	 *
	 */
	@Test
	void testConvertProduct()
	{
		final XJDFHelper h = new XJDFHelper("a", "b");
		h.getCreateRootProduct(0).setAmount(123);
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"), false);
		final JSONObject o = jsonWriter.convert(h.getRoot());
		assertNotNull(o.toJSONString());
		final JSONObjHelper oh = new JSONObjHelper(o);
		assertTrue(oh.getBool("XJDF/ProductList/Product[0]/IsRoot", false));
		assertEquals(123, oh.getInt("XJDF/ProductList/Product[0]/Amount", -1));
	}

	/**
	 *
	 */
	@Test
	void testConvertSchemaArray()
	{
		final XJMFHelper xjmfHelper = new XJMFHelper(KElement.createRoot("XJMF", null));
		final MessageResourceHelper mh = (MessageResourceHelper) xjmfHelper.appendMessage(EnumFamily.Signal, "Resource");
		final SetHelper sh = mh.appendSet("foo");
		xjmfHelper.cleanUp();
		final KElement xjdf = xjmfHelper.getRoot();
		xjdf.setXPathValue("Comment", "foo");

		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"), false);
		final JSONObject o = jsonWriter.convert(xjdf);
		assertNotNull(o.toJSONString());
		final String jsonString = o.toJSONString();
		assertTrue(jsonString.indexOf("\"Header\":{") > 0);
		assertTrue(jsonString.indexOf("[") > 0);
		log.info(jsonString);
	}

	/**
	 *
	 */
	@Test
	void testSimpleXJDF()
	{
		final KElement xjdf = KElement.createRoot("XJDF", null);
		xjdf.setAttribute("JobID", "Job_" + KElement.uniqueID(0));
		final String fileName = sm_dirTestDataTemp + "simple.json";
		FileUtil.streamToFile(new JSONWriter().getStream(xjdf), fileName);
		assertTrue(new File(fileName).exists());
	}

	/**
	 *
	 */
	@Test
	void testConvertComment()
	{
		final KElement xjdf = new XJDFHelper("a", null, null).getRoot();
		xjdf.setXPathValue("Comment", "foo");
		final JSONObject o = new JSONWriter().convert(xjdf);
		final String jsonString = o.toJSONString();
		assertTrue(jsonString.indexOf("\"Comment\":\"foo\"") > 0);
		log.info(jsonString);
	}

	/**
	 *
	 */
	@Test
	void testConvertCommentNoArray()
	{
		final KElement xjdf = new XJDFHelper("a", null, null).getRoot();
		xjdf.setXPathValue("Comment", "foo");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		final JSONObject o = jsonWriter.convert(xjdf);
		final String jsonString = o.toJSONString();
		assertTrue(jsonString.indexOf("\"Comment\":\"foo\"") > 0);
		log.info(jsonString);
	}

	/**
	 *
	 */
	@Test
	void testConvertCommentNoArrayMixed()
	{
		final KElement xjdf = new XJDFHelper("a", null, null).getRoot();
		xjdf.setXPathValue("Comment", "foo");
		xjdf.setXPathValue("Comment/@ID", "bar");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		final JSONObject o = jsonWriter.convert(xjdf);
		final String jsonString = o.toJSONString();
		assertTrue(jsonString.indexOf("\"Comment\":[") > 0);
		assertTrue(jsonString.indexOf("\"ID\":\"bar\"") > 0);
		assertTrue(jsonString.indexOf("[\"foo\",") > 0 || jsonString.indexOf(",\"foo\"]") > 0);
		log.info(jsonString);
	}

	/**
	 *
	 */
	@Test
	void testConvertCommentMixed()
	{
		final KElement xjdf = KElement.createRoot(XJDFConstants.XJDF, null);
		xjdf.setXPathValue("Comment", "foo");
		xjdf.setXPathValue("Comment/@ID", "bar");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.addMixed(ElementName.COMMENT);
		jsonWriter.addArray(ElementName.COMMENT);
		final JSONObject o = jsonWriter.convert(xjdf);
		final String jsonString = o.toJSONString();
		assertTrue(jsonString.indexOf("\"Comment\":[") > 0);
		assertTrue(jsonString.indexOf("\"ID\":\"bar\"") > 0);
		assertTrue(jsonString.indexOf("\"Text\":\"foo\"") > 0);
		log.info(jsonString);
	}

	/**
	 *
	 */
	@Test
	void testConvertXJDFTotalDuration()
	{
		final XJDFHelper xjdfHelper = new XJDFHelper("j", null, null);
		final SetHelper sh = xjdfHelper.getCreateSet(ElementName.NODEINFO, EnumUsage.Input);
		final JDFNodeInfo ni = (JDFNodeInfo) sh.getCreatePartition(0, true).getResource();
		ni.setAttribute(AttributeName.TOTALDURATION, "1234");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"), false);
		final JSONObject o = jsonWriter.convert(xjdfHelper.getRoot());

		final String jsonString = o.toJSONString();
		assertTrue(jsonString.indexOf("\"TotalDuration\":\"1234\"") > 0);
		log.info(jsonString);
	}

	/**
	 *
	 */
	@Test
	void testArrayFilespec()
	{
		final XJDFHelper xjdfHelper = new XJDFHelper("j", null, null);
		final SetHelper sh = xjdfHelper.getCreateSet(ElementName.NODEINFO, EnumUsage.Input);
		final JDFNodeInfo ni = (JDFNodeInfo) sh.getCreatePartition(0, true).getResource();
		ni.setAttribute(AttributeName.TOTALDURATION, "1234");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setXJDF(true, false, EnumVersion.Version_2_2);
		// jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "schema/Version_2_2/xjdf.xsd"), false);
		assertTrue(jsonWriter.arrayNames.contains("shapedef/filespec"));
		assertTrue(jsonWriter.arrayNames.contains("shapetemplate/filespec"));
	}

	/**
	 *
	 */
	@Test
	void testLayoutIntentPages()
	{
		final JDFNode n = new JDFDoc(ElementName.JDF).getJDFRoot();
		n.setType(EnumType.Product);
		final JDFLayoutIntent li = (JDFLayoutIntent) n.addResource(ElementName.LAYOUTINTENT, EnumUsage.Input);
		li.appendPages().setActual(3);
		final JDFToXJDF conv = new JDFToXJDF();
		final KElement xjdf = conv.convert(n);
		assertEquals("3", xjdf.getXPathAttribute("ProductList/Product/Intent/LayoutIntent/@Pages", null));
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setXJDF(true, false, EnumVersion.Version_2_1);
		final JSONObject o = jsonWriter.convert(xjdf);
		assertEquals(3, new JSONObjHelper(o).getPathObject("ProductList/Product/Intent/LayoutIntent/Pages"));
	}

	/**
	 *
	 */
	@Test
	void testFillTotalDuration()
	{
		final XJDFHelper xjdfHelper = new XJDFHelper("j", null, null);
		final SetHelper sh = xjdfHelper.getCreateSet(ElementName.NODEINFO, EnumUsage.Input);
		final JDFNodeInfo ni = (JDFNodeInfo) sh.getCreatePartition(0, true).getResource();
		ni.setAttribute(AttributeName.TOTALDURATION, "1234");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"), false);
		assertTrue(jsonWriter.alwaysString.contains("totalduration"));
	}

	/**
	 *
	 */
	@Test
	void testFillTotalDuration2()
	{
		final XJDFHelper xjdfHelper = new XJDFHelper("j", null, null);
		final SetHelper sh = xjdfHelper.getCreateSet(ElementName.NODEINFO, EnumUsage.Input);
		final JDFNodeInfo ni = (JDFNodeInfo) sh.getCreatePartition(0, true).getResource();
		ni.setAttribute(AttributeName.TOTALDURATION, "1234");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setXJDF(true, false);
		assertTrue(jsonWriter.alwaysString.contains("totalduration"));
	}

	/**
	 *
	 */
	@Test
	void testConvertXJMFNoArray()
	{
		final XJMFHelper xjmfHelper = new XJMFHelper(KElement.createRoot("XJMF", null));
		xjmfHelper.appendMessage(EnumFamily.Signal, "Resource");
		xjmfHelper.cleanUp();
		final KElement xjdf = xjmfHelper.getRoot();
		xjdf.setXPathValue("Comment", "foo");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		final JSONObject o = jsonWriter.convert(xjdf);
		final String jsonString = o.toJSONString();
		assertTrue(jsonString.indexOf("\"Header\":{") > 0);
		assertTrue(jsonString.indexOf("[") < 0);
		log.info(jsonString);
	}

	/**
	 *
	 */
	@Test
	void testSplitXJMF()
	{
		final XJMFHelper xjmfHelper = new XJMFHelper();
		xjmfHelper.appendMessage(EnumFamily.Signal, "Resource");
		xjmfHelper.appendMessage(EnumFamily.Signal, "Status");
		xjmfHelper.cleanUp();
		final KElement xjdf = xjmfHelper.getRoot();
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setXJDF(true, false);
		final List<JSONObject> os = jsonWriter.splitConvert(xjdf);
		assertEquals(2, os.size());
		for (final JSONObject o : os)
		{
			final String jsonString = o.toJSONString();
			assertTrue(jsonString.indexOf("\"Header\":{") > 0);
			assertTrue(jsonString.indexOf("[") < 0);

			log.info(jsonString);
		}
	}

	/**
	 *
	 */
	@Test
	void testXJMFMessageNoArray()
	{
		final XJMFHelper xjmfHelper = new XJMFHelper();
		xjmfHelper.appendMessage(EnumFamily.Signal, "Resource");
		xjmfHelper.cleanUp();
		final KElement xjdf = xjmfHelper.getRoot();
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setXJDF(true, false);
		final List<JSONObject> os = jsonWriter.splitConvert(xjdf);
		assertEquals(1, os.size());
		for (final JSONObject o : os)
		{
			assertNull(new JSONObjHelper(o).getArray("SignalResource", false));
			assertNotNull(new JSONObjHelper(o).getHelper("SignalResource"));
		}
	}

	/**
	 *
	 */
	@Test
	void testNoArrayDouble()
	{
		final KElement e = KElement.createRoot("a", null);
		e.appendElement("b").setAttribute("c", "d");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		final JSONObject o = jsonWriter.convert(e);
		final String jsonString = o.toJSONString();
		assertEquals("{\"a\":{\"b\":{\"c\":\"d\"}}}", jsonString);

		e.appendElement("b").setAttribute("e", "f");
		final JSONObject o2 = jsonWriter.convert(e);
		final String jsonString2 = o2.toJSONString();
		assertEquals("{\"a\":{\"b\":[{\"c\":\"d\"},{\"e\":\"f\"}]}}", jsonString2);

	}

	/**
	 *
	 */
	@Test
	void testEmptyElements()
	{
		final KElement e = KElement.createRoot("a", null);
		for (int i = 0; i < 3; i++)
		{
			e.appendElement("b");
		}
		e.appendElement("b").setAttribute("c", "d");
		for (int i = 0; i < 3; i++)
		{
			e.appendElement("b");
		}
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		final JSONObject o = jsonWriter.convert(e);
		final String jsonString = o.toJSONString();
		final JSONObject a = (JSONObject) o.get("a");
		final JSONArray b = (JSONArray) a.get("b");
		assertEquals(7, b.size());
		final JSONObject b4 = (JSONObject) b.get(3);
		assertEquals("d", b4.get("c"));

	}

	/**
	 *
	 */
	@Test
	void testLearnArray()
	{
		final KElement e = KElement.createRoot("a", null);
		e.appendElement("b").setAttribute("c", "d");

		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		final JSONObject o = jsonWriter.convert(e);
		final String jsonString = o.toJSONString();
		assertEquals("{\"a\":{\"b\":{\"c\":\"d\"}}}", jsonString);

		e.appendElement("b").setAttribute("e", "f");
		final JSONObject o2 = jsonWriter.convert(e);
		final String jsonString2 = o2.toJSONString();
		assertEquals("{\"a\":{\"b\":[{\"c\":\"d\"},{\"e\":\"f\"}]}}", jsonString2);
		assertTrue(jsonWriter.getArrayNames().contains("a/b"));
		e.removeChild("b", null, 1);
		final JSONObject o3 = jsonWriter.convert(e);
		final String jsonString3 = o3.toJSONString();
		assertEquals("{\"a\":{\"b\":[{\"c\":\"d\"}]}}", jsonString3);

	}

	/**
	 *
	 */
	@Test
	void testArrayFromSchema()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(getXJDFSchemaElement(MINOR), false);
		assertTrue(jsonWriter.getArrayNames().contains("resource/part"));
		assertTrue(jsonWriter.getArrayNames().contains("layout/placedobject"));
		assertTrue(jsonWriter.getArrayNames().contains("xjdf/comment"));
		assertTrue(jsonWriter.getArrayNames().contains("address/addressline"));
		assertFalse(jsonWriter.getArrayNames().contains("xjdf"));
		assertFalse(jsonWriter.getArrayNames().contains("xjmf/header"));
		assertFalse(jsonWriter.getArrayNames().contains("placedobject/markobject"));

		assertTrue(jsonWriter.isArray("resource/part"));
	}

	/**
	 *
	 */
	@Test
	void testConvertArrayFromSchema()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(getXJDFSchemaElement(MINOR), false);

		final KElement e = KElement.createRoot("XJDF", null);
		e.appendElement("ResourceSet").appendElement("Resource").appendElement("Part");
		final JSONObject o = jsonWriter.convert(e);
		assertTrue(o.toJSONString().indexOf("[") > 0);
	}

	/**
	 *
	 */
	@Test
	void testNumFromSchema()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(getXJDFSchemaElement(MINOR), false);
		assertTrue(jsonWriter.numbers.contains("amount"));
	}

	/**
	 *
	 */
	@Test
	void testNumListFromSchema()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(getXJDFSchemaElement(MINOR), false);
		assertTrue(jsonWriter.numList.contains("ctm"));
	}

	/**
	 *
	 */
	@Test
	void testNumListRetainSchema()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(getXJDFSchemaElement(MINOR), false);
		assertTrue(jsonWriter.numList.contains("ctm"));
		final KElement xjdf = KElement.parseString("<XJDF CTM=\"foo\" />");
		final JSONObject o = jsonWriter.convert(xjdf);
		assertEquals("foo", new JSONObjHelper(o).getPathObject("XJDF/CTM"));
		assertTrue(jsonWriter.numList.contains("ctm"));
		assertFalse(jsonWriter.alwaysString.contains("ctm"));
		xjdf.appendElement("PlacedObject").setAttribute("CTM", "1");
		final JSONObject o2 = jsonWriter.convert(xjdf);
		assertEquals(Integer.valueOf(1), new JSONObjHelper(o2).getPathObject("XJDF/PlacedObject/CTM[0]"));
	}

	/**
	 *
	 */
	@Test
	void testArrayRetainSchema()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(getXJDFSchemaElement(MINOR), false);
		assertTrue(jsonWriter.arrayNames.contains("xjdf/resourceset"));
		final XJDFHelper xjdf = new XJDFHelper(EnumVersion.Version_2_0, "a");
		xjdf.getCreateSet("Foo", null);
		xjdf.getRoot().appendElement(ElementName.AUDITPOOL);
		xjdf.getRoot().appendElement(ElementName.AUDITPOOL);
		final JSONObject o = jsonWriter.convert(xjdf.getRoot());
		assertFalse(jsonWriter.arrayNames.contains("auditpool"));
		assertFalse(jsonWriter.arrayNames.contains("xjdf/auditpool"));
	}

	/**
	 *
	 */
	@Test
	void testStringFromSchema()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"), false);
		assertFalse(jsonWriter.isTypesafeKey(AttributeName.JOBID));
		assertFalse(jsonWriter.isTypesafeKey(AttributeName.JOBPARTID));
		assertTrue(jsonWriter.isTypesafeKey("Amount"));
		assertTrue(jsonWriter.isTypesafeKey(AttributeName.DIMENSION));
		assertTrue(jsonWriter.isTypesafeKey(AttributeName.DIMENSIONS));
		assertTrue(jsonWriter.isTypesafeKey(AttributeName.ACTUALAMOUNT));
		assertTrue(jsonWriter.isTypesafeKey(AttributeName.CMYK));
	}

	/**
	 *
	 */
	@Test
	void testStringArray()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(true);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"), false);
		assertFalse(jsonWriter.isTypesafeKey(AttributeName.JOBID));
		assertFalse(jsonWriter.isTypesafeKey(AttributeName.JOBPARTID));
		assertTrue(jsonWriter.isTypesafeKey("Amount"));
		assertTrue(jsonWriter.isTypesafeKey(ElementName.COLORANTORDER));
	}

	/**
	 *
	 */
	@Test
	void testStringArrayTest()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(true);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"), false);
		final KElement xjdf = KElement.createRoot("ColorantControl", null);
		xjdf.setAttribute(ElementName.COLORANTORDER, "C M Y K");
		final String fileName = sm_dirTestDataTemp + "co.json";
		final JSONObject o = jsonWriter.convert(xjdf);
		assertEquals("Y", new JSONObjHelper(o).getPathObject("ColorantControl/ColorantOrder[2]"));

	}

	/**
	 *
	 */
	@Test
	void testTransferSchemaTest()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(true);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"), false);
		assertTrue(jsonWriter.isTransferCurve(AttributeName.SPECTRUM));

	}

	/**
	 *
	 */
	@Test
	void testMany()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"), false);
		final File[] xjdfs = FileUtil.listFilesWithExtension(new File(sm_dirTestData + "xjdf"), "xjdf");
		for (final File x : xjdfs)
		{
			assertNotNull(FileUtil.streamToFile(jsonWriter.getStream(KElement.parseFile(x.getAbsolutePath())),
					sm_dirTestDataTemp + "json/" + UrlUtil.newExtension(x.getName(), "json")));

		}
	}

	/**
	 *
	 */
	@Test
	void testQCTransferCurce()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"), false);
		FileUtil.streamToFile(jsonWriter.getStream(KElement.parseFile(sm_dirTestData + "xjdf/QualityControlColorSpectrum.xjdf")),
				sm_dirTestDataTemp + "json/QualityControlColorSpectrum.json");
		jsonWriter.getRoot();

	}

	/**
	 *
	 */
	@Test
	@Disabled
	void testManyCIP4()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.setPrefix(eJSONPrefix.none);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"), false);
		jsonWriter.fillTypesFromSchema(KElement.parseFile("C:\\gitreps\\schema\\printtalk-schema\\PrintTalk.xsd"), false);
		jsonWriter.setPrepWalker(new JSONPrepWalker());
		final Vector<File> xjdfs = FileUtil.listFilesInTree(new File("C:\\gitreps\\schema\\xjdf-schema\\src\\main\\resources\\samples"), "*.xjdf");
		final Vector<File> xjmfs = FileUtil.listFilesInTree(new File("C:\\gitreps\\schema\\xjdf-schema\\src\\main\\resources\\samples"), "*.xjmf");
		final Vector<File> ptks = FileUtil.listFilesInTree(new File("C:\\gitreps\\schema\\printtalk-schema\\samples"), "*.ptk");
		final List<File> all = new ArrayList<>();
		// all.addAll(ptks);
		all.addAll(xjmfs);
		all.addAll(xjdfs);
		for (final File x : all)
		{
			final KElement parseFile = KElement.parseFile(x.getAbsolutePath());
			final String fileNamex = sm_dirTestDataTemp + "jsonex/" + x.getName();
			parseFile.getOwnerDocument_KElement().write2File(fileNamex, 2, false);
			final String fileName = UrlUtil.newExtension(fileNamex, "json");
			assertNotNull(FileUtil.streamToFile(jsonWriter.getStream(parseFile), fileName));

		}
	}

	/**
	 *
	 */
	@Test
	void testConvertXJMFNoArrayExclude()
	{
		final XJMFHelper xjmfHelper = new XJMFHelper(KElement.createRoot("XJMF", null));
		xjmfHelper.appendMessage(EnumFamily.Signal, "Resource");
		xjmfHelper.cleanUp();
		final KElement xjdf = xjmfHelper.getRoot();
		xjdf.setXPathValue("Comment", "foo");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.addArray("Header");
		final JSONObject o = jsonWriter.convert(xjdf);
		final String jsonString = o.toJSONString();
		assertTrue(jsonString.indexOf("\"Header\":[{") > 0);
		log.info(jsonString);
	}

	/**
	 *
	 */
	@Test
	void testConvertCase()
	{
		final XJMFHelper xjmfHelper = new XJMFHelper(KElement.createRoot("XJMF", null));
		xjmfHelper.appendMessage(EnumFamily.Signal, "Resource");
		xjmfHelper.cleanUp();
		final KElement xjdf = xjmfHelper.getRoot();
		xjdf.setXPathValue("Comment", "foo");
		final JSONObject o = new JSONWriter().convert(xjdf);
		final String jsonString = o.toJSONString();
		assertTrue(jsonString.indexOf("\"SignalResource\":") > 0);
		log.info(jsonString);
	}

	/**
	 *
	 */
	@Test
	void testConvertLowerCase()
	{
		final XJMFHelper xjmfHelper = new XJMFHelper(KElement.createRoot("XJMF", null));
		xjmfHelper.appendMessage(EnumFamily.Signal, "Resource");
		xjmfHelper.cleanUp();
		final KElement xjdf = xjmfHelper.getRoot();
		xjdf.setXPathValue("Comment", "foo");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setKeyCase(eJSONCase.lower);
		final JSONObject o = jsonWriter.convert(xjdf);
		final String jsonString = o.toJSONString();
		assertTrue(jsonString.indexOf("\"signalresource\":") > 0);
		assertFalse(jsonString.indexOf("\"SignalResource\":") > 0);
		log.info(jsonString);
	}

	/**
	*
	*/
	@Test
	void testConvertLowerLowerCase()
	{
		final XJMFHelper xjmfHelper = new XJMFHelper(KElement.createRoot("XJMF", null));
		xjmfHelper.appendMessage(EnumFamily.Signal, "Resource");
		xjmfHelper.cleanUp();
		final KElement xjdf = xjmfHelper.getRoot();
		xjdf.setXPathValue("Comment", "foo ÄÖÜ");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setKeyCase(eJSONCase.lower);
		jsonWriter.setValueCase(eJSONCase.lower);
		final JSONObject o = jsonWriter.convert(xjdf);
		final String jsonString = o.toJSONString();
		for (char a = 'A'; a <= 'Z'; a++)
		{
			assertEquals(-1, jsonString.indexOf(a));
		}
		assertEquals(-1, jsonString.indexOf('Ä'));
		assertEquals(-1, jsonString.indexOf('Ö'));
		assertEquals(-1, jsonString.indexOf('Ü'));

		log.info(jsonString);
	}
}