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

import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFResourceLink.EnumUsage;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.extensions.SetHelper;
import org.cip4.jdflib.extensions.XJDFHelper;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONPrefix;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author rainer prosi
 */
class JSONRootWalkerTest extends JSONTestCaseBase
{

	@Test
    void testPrefix3()
	{
		final JSONWriter wr = new JSONWriter();
		final KElement e = KElement.createRoot("a:b", "a.com");
		JSONRootWalker w = new JSONRootWalker(wr, e);
		for (final String n : eJSONPrefix.getNames())
		{
			final eJSONPrefix enum1 = eJSONPrefix.getEnum(n);
			Assertions.assertNotNull(enum1);
			Assertions.assertNotNull(w.getNodeName(e));
		}
	}

	@Test
    void testToString()
	{
		final JSONWriter wr = new JSONWriter();
		final KElement e = KElement.createRoot("a:b", "a.com");
		JSONRootWalker w = new JSONRootWalker(wr, e);

		Assertions.assertNotNull(w.toString());

	}

	/**
	 *
	 */
	@Test
    void testConvertContext1()
	{
		final XJDFHelper xjdfHelper = new XJDFHelper("j", null, null);
		final SetHelper sh = xjdfHelper.getCreateSet(ElementName.NODEINFO, EnumUsage.Input);
		sh.getRoot().appendElement("foo:name", "abc.com");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setPrefix(eJSONPrefix.context);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"), false);
		final JSONObject o = jsonWriter.convert(xjdfHelper.getRoot());

		final String jsonString = o.toJSONString();
		log.info(jsonString);
		Assertions.assertTrue(jsonString.indexOf("\"@context\":{\"foo\":\"abc.com\"}}") > 0);
	}

	/**
	 *
	 */
	@Test
    void testConvertContextAttribute()
	{
		KElement e0 = KElement.createRoot("XJDF", null);

		e0.appendElement("AuditPool").setAttribute("foo:name", "bar", "abc.com");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setPrefix(eJSONPrefix.context);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"), false);
		final JSONObject o = jsonWriter.convert(e0);

		final String jsonString = o.toJSONString();
		log.info(jsonString);
		Assertions.assertTrue(jsonString.indexOf("\"@context\":{\"foo\":\"abc.com\"}}") > 0);
	}

	/**
	 *
	 */
	@Test
    void testConvertContextAttribute2()
	{
		KElement e0 = KElement.createRoot("XJDF", null);

		KElement ap = e0.appendElement("AuditPool");
		ap.setAttribute("foo:name", "bar", "abc.com");
		ap.setAttribute("foo2:name2", "bar2", "abc2.com");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setPrefix(eJSONPrefix.context);
		jsonWriter.fillTypesFromSchema(KElement.parseFile(sm_dirTestData + "xjdf/xjdf.xsd"), false);
		final JSONObject o = jsonWriter.convert(e0);

		final String jsonString = o.toJSONString();
		log.info(jsonString);
		Assertions.assertTrue(jsonString.indexOf("\"@context\":[{\"foo") > 0);
	}

}