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
package org.cip4.lib.jdf.jsonutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.core.JDFElement;
import org.cip4.jdflib.core.JDFElement.EnumVersion;
import org.cip4.jdflib.core.JDFParser;
import org.cip4.jdflib.core.JDFParserFactory;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.core.VElement;
import org.cip4.jdflib.core.XMLDoc;
import org.cip4.jdflib.extensions.BaseXJDFHelper;
import org.cip4.jdflib.extensions.XJDFConstants;
import org.cip4.jdflib.extensions.XJDFHelper;
import org.cip4.jdflib.util.ContainerUtil;
import org.cip4.jdflib.util.FileUtil;
import org.cip4.jdflib.util.StringUtil;
import org.cip4.jdflib.util.UrlPart;
import org.cip4.jdflib.util.UrlUtil;
import org.cip4.lib.jdf.jsonutil.rtf.JSONRtfWalker;
import org.cip4.lib.jdf.jsonutil.schema.JSONSchemaReader;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.w3c.dom.Comment;
import org.w3c.dom.Node;

import com.networknt.schema.ValidationMessage;

/**
 * base class for JDFLib test case classes
 *
 * @author prosirai
 *
 */
public abstract class JSONTestCaseBase
{

	/**
	 *
	 * @param e
	 * @param major
	 * @param minor
	 * @return
	 */
	protected boolean reparse(final KElement e, final int minor)
	{
		final String written = e.toXML();
		assertNotNull(written);
		final JDFParser p = getXJDFSchemaParser(minor);
		final JDFDoc xParsed = p.parseString(written);
		return xParsed.isSchemaValid();
	}

	protected static EnumVersion exampleVersion = EnumVersion.Version_2_2;

	/**
	 *
	 * @return
	 */
	public static String getXJDFSchema(final int minor)
	{
		final String file = StringUtil.replaceToken(sm_dirTestSchema, -1, File.separator, "Version_2_" + minor) + File.separator + "xjdf.xsd";
		final String normalize = FilenameUtils.normalize(file);
		if (!new File(normalize).exists())
		{
			final String strUrl = "http://schema.cip4.org/jdfschema_2_" + minor + "/xjdf.xsd";
			final UrlPart schemaUrlPart = UrlUtil.writeToURL(strUrl, null, UrlUtil.GET, null, null);
			FileUtil.streamToFile(schemaUrlPart.getResponseStream(), new File(normalize));
		}
		return normalize;
	}

	/**
	 *
	 * @return
	 */
	public static KElement getXJDFSchemaElement(final int minor)
	{
		return KElement.parseFile(getXJDFSchema(minor));
	}

	/**
	 *
	 * @return
	 */
	protected JDFParser getXJDFSchemaParser(final int minor)
	{
		final JDFParser parser = JDFParserFactory.getFactory().get();
		parser.setSchemaLocation(JDFElement.getSchemaURL(2, minor), getXJDFSchema(minor));
		return parser;
	}

	/**
	 *
	 */
	public JSONTestCaseBase()
	{
		super();
	}

	/**
	 *
	 * @param h
	 * @param startFirst
	 */
	protected void setSnippet(final BaseXJDFHelper h, final boolean startFirst)
	{
		if (h != null)
		{
			setSnippet(h.getRoot(), startFirst);
		}
	}

	/**
	 *
	 * @param e
	 * @param startFirst if true include the enclosing element, if false exclude it
	 */
	protected void setSnippet(final KElement e, final boolean startFirst)
	{
		if (e != null)
		{
			final Node parent = e.getParentNode();
			final String start = " START SNIPPET ";
			final String end = " END SNIPPET ";
			Comment newChild = e.getOwnerDocument().createComment(startFirst ? start : end);
			parent.insertBefore(newChild, e);
			newChild = e.getOwnerDocument().createComment(startFirst ? end : start);
			parent.insertBefore(newChild, e.getNextSibling());
		}
	}

	/**
	 *
	 * @param h
	 */
	protected void cleanSnippets(final BaseXJDFHelper h, final boolean removeBase)
	{
		if (h == null || h.getRoot() == null)
			return;
		BaseXJDFHelper h2 = BaseXJDFHelper.getBaseHelper(h.getRoot().getDocRoot());
		if (h2 == null)
			h2 = h;

		h2.cleanUp();
		setSnippet(h, true);
		if (h2 instanceof XJDFHelper && removeBase)
		{
			setSnippet(((XJDFHelper) h).getAuditPool(), false);
			setSnippet(((XJDFHelper) h).getSet(ElementName.NODEINFO, 0), false);
		}
		final VElement headers = h.getRoot().getChildrenByTagName(XJDFConstants.Header, null, null, false, false, 0);
		for (final KElement header : headers)
		{
			header.removeAttribute(AttributeName.AGENTNAME);
			header.removeAttribute(AttributeName.AGENTVERSION);
		}
	}

	public JSONObjHelper writeBothJson(final KElement e, final JSONWriter jsonWriter, final String output)
	{
		return writeBothJson(e, jsonWriter, output, true, false, true);
	}

	public JSONObjHelper writeBothJson(final KElement e, final JSONWriter jsonWriter, final String output, final boolean equals)
	{
		return writeBothJson(e, jsonWriter, output, equals, false, true);
	}

	public JSONObjHelper writeBothJson(final KElement e, final JSONWriter jsonWriter, final String output, final boolean equals, final boolean cleansnippets)

	{
		return writeBothJson(e, jsonWriter, output, equals, cleansnippets, true);
	}

	public JSONObjHelper writeBothJson(final KElement e, final JSONWriter jsonWriter, final String output, final boolean equals, final boolean cleansnippets, final boolean checkJSONSchema)

	{
		final File xmlFile = new File(sm_dirTestDataTemp + "xjdf/xjdf", UrlUtil.newExtension(output, "xml"));
		final BaseXJDFHelper baseHelper = BaseXJDFHelper.getBaseHelper(e);
		if (cleansnippets || e.equals(baseHelper.getRoot()))
		{
			cleanSnippets(baseHelper, cleansnippets);
		}
		else
		{
			setSnippet(e, true);
		}
		e.getDocRoot().write2File(xmlFile);
		final JDFParser jdfparser = getSchemaParser();
		final JDFDoc docJDF = jdfparser.parseFile(xmlFile);
		final XMLDoc dVal0 = docJDF.getValidationResult();
		final String valResult0 = dVal0.getRoot().getAttribute("ValidationResult");
		if (!VALID.equals(valResult0))
		{
			dVal0.write2File(xmlFile.getPath() + ".xjdf.val.xml", 2, false);
		}

		assertEquals(VALID, valResult0);

		final JSONObject jo = jsonWriter.convert(e);
		FileUtil.writeFile(jsonWriter, new File(sm_dirTestDataTemp + "xjdf/json", output));
		FileUtil.writeFile(new JSONRtfWalker(jsonWriter), new File(sm_dirTestDataTemp + "xjdf/rtf", output + ".rtf"));
		final String root = e.getLocalName();
		// jo.put("$schema", "http://www.CIP4.org/JDFSchema_2_0/#/properties/" + root);
		final JSONSchemaReader srf = new JSONSchemaReader(new File(sm_dirTestDataTemp + "schema/Version_2_3/xjdf.json"));
		final Collection<ValidationMessage> ret = srf.checkJSON(jo.toJSONString());
		if (checkJSONSchema)
		{
			assertTrue(ContainerUtil.isEmpty(ret));
		}
		final JSONReader reader = new JSONReader();
		reader.setXJDF();
		final KElement roundtrip = reader.getElement(jo);
		BaseXJDFHelper bh = BaseXJDFHelper.getBaseHelper(roundtrip);
		if (bh == null)
			bh = new XJDFHelper(roundtrip);
		cleanSnippets(bh, cleansnippets);
		final File roundtripFile = new File(sm_dirTestDataTemp + "xjdf/xjdfroundtrip", UrlUtil.newExtension(output, "xml"));
		roundtrip.write2File(roundtripFile);
		final JDFDoc docRound = jdfparser.parseFile(roundtripFile);
		final XMLDoc dVal1 = docRound.getValidationResult();
		final String valResult1 = dVal1.getRoot().getAttribute("ValidationResult");
		if (!VALID.equals(valResult1))
		{
			dVal0.write2File(roundtripFile.getPath() + ".xjdf.val.xml", 2, false);
		}
		assertEquals(VALID, valResult1);

		if (equals)
		{
			assertTrue(e.isEqual(roundtrip));
		}
		return JSONObjHelper.getHelper(jo);
	}

	final protected static int MINOR = 2;
	private static final String VALID = "Valid";

	static protected final String sm_dirTestData = getTestDataDir();
	static protected final String sm_dirTestDataTemp = sm_dirTestData + "temp" + File.separator;
	static protected final String sm_dirTestSchemaBase = sm_dirTestData + "schema" + File.separator + "Version_";
	static protected final String sm_dirTestSchema = sm_dirTestSchemaBase;

	private static String getTestDataDir()
	{
		String path = null;
		final URL resource = JSONTestCaseBase.class.getResource("/data");
		if (resource != null)
		{
			path = resource.getPath();
		}
		if (path == null)
		{
			// legacy - pre maven file structure support
			path = "test" + File.separator + "data";
		}
		path = FilenameUtils.normalize(path) + File.separator;
		return path;
	}

	protected Log log;

	// //////////////////////////////////////////////////////////////////////////
	/**
	 *
	 * @see junit.framework.TestCase#setUp()
	 */
	@BeforeEach
	public void setUp() throws Exception
	{
		JDFElement.setDefaultJDFVersion(EnumVersion.Version_2_1);
		log = LogFactory.getLog(getClass());
	}

	/**
	 * general cleanup after each test
	 *
	 * @see junit.framework.TestCase#tearDown()
	 */
	@AfterEach
	public void tearDown() throws Exception
	{
		JDFElement.setDefaultJDFVersion(EnumVersion.Version_2_1);
	}

	/**
	 *
	 * @return
	 */
	protected static JDFParser getSchemaParser()
	{
		final JDFParser parser = JDFParserFactory.getFactory().get();
		parser.setSchemaLocation(JDFElement.getSchemaURL(2, 0), getXJDFSchema(2, XJDFHelper.defaultVersion().getMinorVersion()));
		return parser;
	}

	/**
	 *
	 * @return
	 */
	public static String getXJDFSchema(final int major, final int minor)
	{
		final String file = StringUtil.replaceToken(sm_dirTestSchema, -1, File.separator, "Version_" + major + "_" + minor) + "/xjdf.xsd";
		return UrlUtil.normalize(file);
	}

}