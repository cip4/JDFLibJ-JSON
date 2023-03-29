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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFDoc;
import org.cip4.jdflib.core.JDFResourceLink.EnumUsage;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.extensions.SetHelper;
import org.cip4.jdflib.extensions.XJDFConstants;
import org.cip4.jdflib.extensions.XJDFHelper;
import org.cip4.jdflib.resource.process.JDFBoxFoldingParams;
import org.cip4.jdflib.resource.process.JDFMedia;
import org.cip4.jdflib.resource.process.JDFMediaLayers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rainer prosi
 */
public class JSONPostWalkerTest extends JSONTestCaseBase
{

	/**
	 *
	 */
	@Test
	public void testConvertAudit()
	{
		final KElement xjdf = KElement.createRoot(XJDFConstants.XJDF, null);
		xjdf.setXPathValue("AuditPool/AuditCreated/Header/@DeviceID", "foo");
		final KElement xjdf2 = xjdf.cloneNewDoc();
		final JSONPrepWalker w = new JSONPrepWalker();
		w.setExplicitAudit(false);
		w.walkTree(xjdf, null);
		assertEquals("foo", xjdf.getXPathAttribute("AuditPool[@Name=\"Created\"]/Header/@DeviceID", null));
		final JSONPostWalker pw = new JSONPostWalker();
		pw.walkTree(xjdf, null);
		assertEquals("foo", xjdf.getXPathAttribute("AuditPool/AuditCreated/Header/@DeviceID", null));
		assertTrue(xjdf2.isEqual(xjdf));
	}

	/**
	 *
	 */
	@Test
	public void testConvertAuditExplicit()
	{
		final KElement xjdf = KElement.createRoot(XJDFConstants.XJDF, null);
		xjdf.setXPathValue("AuditPool/AuditCreated/Header/@DeviceID", "foo");
		final KElement xjdf2 = xjdf.cloneNewDoc();
		final JSONPrepWalker w = new JSONPrepWalker();
		w.setExplicitAudit(true);
		w.walkTree(xjdf, null);
		assertEquals("foo", xjdf.getXPathAttribute("AuditPool/Audit[@Name=\"Created\"]/Header/@DeviceID", null));
		final JSONPostWalker pw = new JSONPostWalker();
		pw.walkTree(xjdf, null);
		assertEquals("foo", xjdf.getXPathAttribute("AuditPool/AuditCreated/Header/@DeviceID", null));
		assertTrue(xjdf2.isEqual(xjdf));
	}

	/**
	 *
	 */
	@Test
	public void testConvertMediaLayer()
	{
		final XJDFHelper h = new XJDFHelper("j1", "p1");
		final SetHelper ms = h.getCreateSet(ElementName.MEDIA, EnumUsage.Input);
		final JDFMedia m = (JDFMedia) ms.getCreatePartition(0, true).getResource();
		final JDFMediaLayers mediaLayers = m.appendMediaLayers();
		mediaLayers.appendElement(ElementName.GLUE);
		mediaLayers.appendElement(ElementName.MEDIA);
		mediaLayers.appendElement(ElementName.GLUE);
		mediaLayers.appendElement(ElementName.GLUE);

		final KElement xjdf2 = h.getRoot().cloneNewDoc();
		h.getRoot().write2File(sm_dirTestDataTemp + "ml0.xjdf");

		final JSONPrepWalker w = new JSONPrepWalker();
		w.walkTree(h.getRoot(), null);
		h.getRoot().write2File(sm_dirTestDataTemp + "ml1.xjdf");
		assertEquals("Glue", m.getXPathAttribute("MediaLayers[1]/@Name", null));
		assertEquals("Media", m.getXPathAttribute("MediaLayers[2]/@Name", null));
		assertEquals("Glue", m.getXPathAttribute("MediaLayers[3]/@Name", null));
		assertEquals("Glue", m.getXPathAttribute("MediaLayers[4]/@Name", null));
		final JSONPostWalker pw = new JSONPostWalker();
		pw.walkTree(h.getRoot(), null);
		h.getRoot().write2File(sm_dirTestDataTemp + "ml2.xjdf");
		assertTrue(xjdf2.isEqual(h.getRoot()));
	}

	/**
	 *
	 */
	@Test
	public void testConvertBoxFold()
	{
		final XJDFHelper h = new XJDFHelper("j1", "p1");
		final SetHelper ms = h.getCreateSet(ElementName.BOXFOLDINGPARAMS, EnumUsage.Input);
		final JDFBoxFoldingParams bp = (JDFBoxFoldingParams) ms.getCreatePartition(0, true).getResource();
		bp.appendElement(ElementName.GLUE);
		bp.appendElement(ElementName.BOXFOLDACTION).setAttribute(AttributeName.ACTION, "A1");
		bp.appendElement(ElementName.GLUE);
		bp.appendElement(ElementName.GLUE);
		final KElement xjdf2 = h.getRoot().cloneNewDoc();

		final JSONPrepWalker w = new JSONPrepWalker();
		w.walkTree(h.getRoot(), null);
		assertEquals("Glue", bp.getXPathAttribute("BoxFoldAction[1]/@Action", null));
		assertEquals("A1", bp.getXPathAttribute("BoxFoldAction[2]/@Action", null));
		assertEquals("Glue", bp.getXPathAttribute("BoxFoldAction[3]/@Action", null));
		assertEquals("Glue", bp.getXPathAttribute("BoxFoldAction[4]/@Action", null));
		final JSONPostWalker pw = new JSONPostWalker();
		pw.walkTree(h.getRoot(), null);
		assertTrue(xjdf2.isEqual(h.getRoot()));
	}

	/**
	 *
	 */
	@Test
	@Ignore
	public void testConvertPTKHeaders()
	{
		final KElement e = JDFDoc.parseFile(sm_dirTestData + "ptk/confirmation.ptk").getRoot();
		final JSONPrepWalker w = new JSONPrepWalker();
		w.walkTree(e, null);
		assertEquals("https://worker.example.org/XJDF", e.getXPathAttribute("Header/From/Credential/@Identity", null));
		assertNull("https://worker.example.org/XJDF", e.getXPathAttribute("Header/From/Credential/Identity", null));
	}

}
