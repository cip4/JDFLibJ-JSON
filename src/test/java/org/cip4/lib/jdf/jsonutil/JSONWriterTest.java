/*
 * The CIP4 Software License, Version 1.0
 *
 *
 * Copyright (c) 2001-2022 The International Cooperation for the Integration of Processes in Prepress, Press and Postpress (CIP4). All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.cip4.jdflib.auto.JDFAutoStatusQuParams.EnumDeviceDetails;
import org.cip4.jdflib.auto.JDFAutoStatusQuParams.EnumJobDetails;
import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFNodeInfo;
import org.cip4.jdflib.core.JDFResourceLink.EnumUsage;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.elementwalker.ElementWalker;
import org.cip4.jdflib.extensions.SetHelper;
import org.cip4.jdflib.extensions.XJDFConstants;
import org.cip4.jdflib.extensions.XJDFHelper;
import org.cip4.jdflib.extensions.XJMFHelper;
import org.cip4.jdflib.extensions.xjdfwalker.jdftoxjdf.JDFToXJDF;
import org.cip4.jdflib.jmf.JDFDeviceInfo;
import org.cip4.jdflib.jmf.JDFJMF;
import org.cip4.jdflib.jmf.JDFMessage.EnumFamily;
import org.cip4.jdflib.jmf.JDFSignal;
import org.cip4.jdflib.jmf.JMFBuilderFactory;
import org.cip4.jdflib.util.ByteArrayIOStream;
import org.cip4.jdflib.util.ByteArrayIOStream.ByteArrayIOInputStream;
import org.cip4.jdflib.util.FileUtil;
import org.cip4.jdflib.util.StringUtil;
import org.cip4.jdflib.util.UrlUtil;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONCase;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONPrefix;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rainer prosi
 */
public class JSONWriterTest extends JSONTestCaseBase
{

	/**
	 *
	 */
	@Test
	public void testConvert()
	{
		final JDFJMF jmf = JMFBuilderFactory.getJMFBuilder(null).buildStatusSignal(EnumDeviceDetails.Full, EnumJobDetails.MIS);
		final KElement xjmf = new JDFToXJDF().convert(jmf);
		final JSONObject o = new JSONWriter().convert(xjmf);
		assertNotNull(o.toJSONString());
		log.info(o.toJSONString());
	}

	@Test
	public void testEquals()
	{
		final JSONWriter w1 = new JSONWriter();
		final JSONWriter w2 = new JSONWriter();
		assertEquals(w1, w2);
		w2.setLearnArrays(false);
		w1.setLearnArrays(true);
		assertNotEquals(w1, w2);
	}

	@Test
	public void testHash()
	{
		final JSONWriter w1 = new JSONWriter();
		final JSONWriter w2 = new JSONWriter();
		assertEquals(w1.hashCode(), w2.hashCode());
	}

	@Test
	public void testClear()
	{
		final JSONWriter w1 = new JSONWriter();
		w1.clearArray();
	}

	@Test
	public void testRemoveArray()
	{
		final JSONWriter w1 = new JSONWriter();
		w1.removeArray("a");
	}

	@Test
	public void testToString()
	{
		final JSONWriter w1 = new JSONWriter();
		assertNotNull(w1.toString());
	}

	@Test
	public void testPrepWalker()
	{
		final JSONWriter w = new JSONWriter();
		assertNull(w.getPrepWalker());
		w.setPrepWalker(new ElementWalker(null));
	}

	@Test
	public void testPrefix()
	{
		final JSONWriter w = new JSONWriter();
		w.setPrefix(eJSONPrefix.none);
		assertEquals(eJSONPrefix.none, w.getPrefix());
	}

	@Test
	public void testTypesafe()
	{
		final JSONWriter w = new JSONWriter();
		w.setTypeSafe(true);
		assertTrue(w.isTypeSafe());
	}

	@Test
	public void tesMixText()
	{
		final JSONWriter w = new JSONWriter();
		w.setMixedText("a");
		assertEquals("a", w.getMixedText());
	}

	@Test
	public void testWantArray()
	{
		final JSONWriter w = new JSONWriter();
		w.setWantArray(true);
		assertTrue(w.isWantArray());
	}

	@Test
	public void testPrefix2()
	{
		for (String n : eJSONPrefix.getNames())
		{
			assertNotNull(eJSONPrefix.getEnum(n));
		}
	}

	@Test
	public void testPrefix3()
	{
		final JSONWriter w = new JSONWriter();
		KElement e = KElement.createRoot("a:b", "a.com");
		for (String n : eJSONPrefix.getNames())
		{
			eJSONPrefix enum1 = eJSONPrefix.getEnum(n);
			assertNotNull(enum1);
			assertNotNull(w.getNodeName(e));
		}
	}

	@Test
	public void testCase()
	{
		for (String n : eJSONCase.getNames())
		{
			assertNotNull(eJSONCase.getEnum(n));
		}
	}

	@Test
	public void testCase2()
	{
		final JSONWriter w = new JSONWriter();
		for (eJSONCase n : eJSONCase.values())
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
	public void testGetString()
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
	public void testGetStream() throws UnsupportedEncodingException
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
	public void testConvertArray()
	{
		final JDFJMF jmf = JMFBuilderFactory.getJMFBuilder(null).buildStatusSignal(EnumDeviceDetails.Full, EnumJobDetails.MIS);
		final JDFSignal sig = jmf.getSignal(0);
		final JDFDeviceInfo di = sig.getDeviceInfo(0);
		sig.copyElement(di, null);

		final KElement xjmf = new JDFToXJDF().convert(jmf);
		final JSONObject o = new JSONWriter().convert(xjmf);
		assertNotNull(o.toJSONString());
		log.info(o.toJSONString());
	}

	/**
	 *
	 */
	@Test
	public void testConvertArrayEmpty()
	{
		final KElement e = KElement.parseString("<e><a/><a/><a b=\"c\"/><a/></e>");
		final JSONObject o = new JSONWriter().convert(e);
		assertNotNull(o.toJSONString());
		log.info(o.toJSONString());
		assertEquals(4, new JSONObjHelper(o).getArray("e/a").size());
	}

	/**
	 *
	 */
	@Test
	public void testNumberArray()
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
	public void testSkipKey()
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
	public void testAddNull()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.addSkipPool("b");
		jsonWriter.setWantArray(false);
		assertFalse(jsonWriter.addSkipPool(null));
		assertFalse(jsonWriter.addArray(null));
		assertFalse(jsonWriter.addMixed(null));
		assertFalse(jsonWriter.addStringArray(null));
	}

	/**
	 *
	 */
	@Test
	public void testConvertMap()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		assertNotNull(jsonWriter.convertMap(null));
	}

	/**
	 *
	 */
	@Test
	public void testNumberArray0()
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
	public void testTransferFunction()
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
	public void testGetKey()
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
	public void testAddToParentRaw()
	{
		JSONWriter.addToParentRaw(new JSONObject(), "foo", "bar");
		JSONWriter.addToParentRaw(new JSONArray(), "foo", "bar");
	}

	/**
	 *
	 */
	@Test
	public void testNumberArray2()
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
	public void testConvertProduct()
	{
		final XJDFHelper h = new XJDFHelper("a", "b");
		h.getCreateRootProduct(0).setAmount(123);
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"));
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
	public void testSimpleXJDF()
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
	public void testConvertComment()
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
	public void testConvertCommentNoArray()
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
	public void testConvertCommentNoArrayMixed()
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
	public void testConvertCommentMixed()
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
	public void testConvertXJDFTotalDuration()
	{
		final XJDFHelper xjdfHelper = new XJDFHelper("j", null, null);
		final SetHelper sh = xjdfHelper.getCreateSet(ElementName.NODEINFO, EnumUsage.Input);
		final JDFNodeInfo ni = (JDFNodeInfo) sh.getCreatePartition(0, true).getResource();
		ni.setAttribute(AttributeName.TOTALDURATION, "1234");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"));
		final JSONObject o = jsonWriter.convert(xjdfHelper.getRoot());

		final String jsonString = o.toJSONString();
		assertTrue(jsonString.indexOf("\"TotalDuration\":\"1234\"") > 0);
		log.info(jsonString);
	}

	/**
	 *
	 */
	@Test
	public void testFillTotalDuration()
	{
		final XJDFHelper xjdfHelper = new XJDFHelper("j", null, null);
		final SetHelper sh = xjdfHelper.getCreateSet(ElementName.NODEINFO, EnumUsage.Input);
		final JDFNodeInfo ni = (JDFNodeInfo) sh.getCreatePartition(0, true).getResource();
		ni.setAttribute(AttributeName.TOTALDURATION, "1234");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"));
		assertTrue(jsonWriter.alwaysString.contains("totalduration"));
	}

	/**
	 *
	 */
	@Test
	public void testConvertXJMFNoArray()
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
	public void testNoArrayDouble()
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
	public void testLearnArray()
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
	public void testArrayFromSchema()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(getXJDFSchemaElement(MINOR));
		assertTrue(jsonWriter.getArrayNames().contains("resource/part"));
		assertTrue(jsonWriter.getArrayNames().contains("layout/placedobject"));
		assertTrue(jsonWriter.getArrayNames().contains("xjdf/comment"));
		assertTrue(jsonWriter.getArrayNames().contains("address/addressline"));
		assertFalse(jsonWriter.getArrayNames().contains("xjdf"));
		assertFalse(jsonWriter.getArrayNames().contains("xjmf/header"));
		assertFalse(jsonWriter.getArrayNames().contains("placedobject/markobject"));
	}

	/**
	 *
	 */
	@Test
	public void testStringFromSchema()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"));
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
	public void testStringArray()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(true);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"));
		assertFalse(jsonWriter.isTypesafeKey(AttributeName.JOBID));
		assertFalse(jsonWriter.isTypesafeKey(AttributeName.JOBPARTID));
		assertTrue(jsonWriter.isTypesafeKey("Amount"));
		assertTrue(jsonWriter.isTypesafeKey(ElementName.COLORANTORDER));
	}

	/**
	 *
	 */
	@Test
	public void testStringArrayTest()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(true);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"));
		final KElement xjdf = KElement.createRoot("XJDF", null);
		xjdf.setAttribute(ElementName.COLORANTORDER, "C M Y K");
		final String fileName = sm_dirTestDataTemp + "co.json";
		final JSONObject o = jsonWriter.convert(xjdf);
		assertEquals("Y", new JSONObjHelper(o).getPathObject("XJDF/ColorantOrder[2]"));

	}

	/**
	 *
	 */
	@Test
	public void testTransferSchemaTest()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(true);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"));
		assertTrue(jsonWriter.isTransferCurve(AttributeName.SPECTRUM));

	}

	/**
	 *
	 */
	@Test
	public void testMany()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"));
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
	public void testQCTransferCurce()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"));
		FileUtil.streamToFile(jsonWriter.getStream(KElement.parseFile(sm_dirTestData + "xjdf/QualityControlColorSpectrum.xjdf")),
				sm_dirTestDataTemp + "json/QualityControlColorSpectrum.json");
		final JSONObject o = jsonWriter.getRoot();

	}

	/**
	 *
	 */
	@Test
	@Ignore
	public void testManyCIP4()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setWantArray(false);
		jsonWriter.setPrefix(eJSONPrefix.none);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"));
		jsonWriter.fillTypesFromSchema(KElement.parseFile("C:\\gitreps\\schema\\printtalk-schema\\PrintTalk.xsd"));
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
	public void testConvertXJMFNoArrayExclude()
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
	public void testConvertCase()
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
	public void testConvertLowerCase()
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
	public void testConvertLowerLowerCase()
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