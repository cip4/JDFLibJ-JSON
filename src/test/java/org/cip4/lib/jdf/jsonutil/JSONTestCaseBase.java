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
package org.cip4.lib.jdf.jsonutil;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.core.JDFElement;
import org.cip4.jdflib.core.JDFElement.EnumVersion;
import org.cip4.jdflib.core.JDFParser;
import org.cip4.jdflib.core.JDFParserFactory;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.util.FileUtil;
import org.cip4.jdflib.util.StringUtil;
import org.cip4.jdflib.util.UrlPart;
import org.cip4.jdflib.util.UrlUtil;
import org.junit.After;
import org.junit.Before;

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
		//		LogConfigurator.configureLog(null, null);
	}

	final protected static int MINOR = 1;

	static protected final String sm_dirTestData = getTestDataDir();
	static protected final String sm_dirTestDataTemp = sm_dirTestData + "temp" + File.separator;
	static protected final String sm_dirTestSchemaBase = sm_dirTestDataTemp + "schema" + File.separator + "Version_";
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
	@Before
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
	@After
	public void tearDown() throws Exception
	{
		JDFElement.setDefaultJDFVersion(EnumVersion.Version_2_1);
	}

	/**
	 *
	 * @return
	 */
	protected static JDFParser getSchemaParser(final EnumVersion version)
	{
		int minor = 6;
		if (EnumVersion.Version_2_1.equals(version) || EnumVersion.Version_1_7.equals(version))
			minor = 7;
		else if (EnumVersion.Version_2_2.equals(version) || EnumVersion.Version_1_8.equals(version))
			minor = 8;
		final JDFParser parser = JDFParserFactory.getFactory().get();
		final File jdfxsd = new File(sm_dirTestSchemaBase + "1_" + minor + File.separator + "JDF.xsd");
		assertTrue(jdfxsd.canRead());
		parser.setJDFSchemaLocation(jdfxsd);
		return parser;
	}

}