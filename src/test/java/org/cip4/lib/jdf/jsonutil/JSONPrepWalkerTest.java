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

import java.util.List;

import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFResourceLink.EnumUsage;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.extensions.SetHelper;
import org.cip4.jdflib.extensions.XJDFConstants;
import org.cip4.jdflib.extensions.XJDFHelper;
import org.cip4.jdflib.extensions.XJMFHelper;
import org.cip4.jdflib.jmf.JDFMessage.EnumFamily;
import org.cip4.jdflib.jmf.JDFMessage.EnumType;
import org.cip4.jdflib.resource.process.JDFBoxFoldingParams;
import org.cip4.jdflib.resource.process.JDFMedia;
import org.cip4.jdflib.resource.process.JDFMediaLayers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author rainer prosi
 */
class JSONPrepWalkerTest extends JSONTestCaseBase
{

	/**
	 *
	 */
	@Test
    void testConvertAudit()
	{
		final KElement xjdf = KElement.createRoot(XJDFConstants.XJDF, null);
		xjdf.setXPathValue("AuditPool/AuditCreated/Header/@DeviceID", "foo");
		final JSONPrepWalker w = new JSONPrepWalker();
		w.setExplicitAudit(false);
		w.walkTree(xjdf, null);
		Assertions.assertEquals("foo", xjdf.getXPathAttribute("AuditPool[@Name=\"AuditCreated\"]/Header/@DeviceID", null));
	}

	/**
	 *
	 */
	@Test
    void testToString()
	{
		final JSONPrepWalker w = new JSONPrepWalker();
		Assertions.assertNotNull(w.toString());
	}

	/**
	 *
	 */
	@Test
    void testIsSplitXJMF()
	{
		final JSONPrepWalker w = new JSONPrepWalker();
		Assertions.assertFalse(w.isSplitXJMF());
		w.setSplitXJMF(true);
		Assertions.assertTrue(w.isSplitXJMF());
	}

	/**
	 *
	 */
	@Test
    void testSplitXJMF0()
	{
		final JSONPrepWalker w = new JSONPrepWalker();
		w.setSplitXJMF(true);
		final XJMFHelper h0 = new XJMFHelper();
		final List<KElement> l = w.splitXML(h0.getRoot());
		Assertions.assertEquals(h0.getRoot(), l.get(0));
	}

	/**
	 *
	 */
	@Test
    void testSplitXJMF1()
	{
		final JSONPrepWalker w = new JSONPrepWalker();
		w.setSplitXJMF(true);
		final XJMFHelper h0 = new XJMFHelper();
		h0.appendMessage(EnumFamily.Signal, EnumType.Status);
		final List<KElement> l = w.splitXML(h0.getRoot());
		Assertions.assertEquals(h0.getRoot(), l.get(0));
	}

	/**
	 *
	 */
	@Test
    void testSplitXJMF2()
	{
		final JSONPrepWalker w = new JSONPrepWalker();
		w.setSplitXJMF(true);
		final XJMFHelper h0 = new XJMFHelper();
		h0.appendMessage(EnumFamily.Signal, EnumType.Status);
		h0.appendMessage(EnumFamily.Signal, EnumType.Status);
		h0.appendMessage(EnumFamily.Signal, EnumType.Status);
		final List<KElement> l = w.splitXML(h0.getRoot());
		Assertions.assertEquals(3, l.size());
		Assertions.assertEquals(1, l.get(0).numChildElements(XJDFConstants.Header, null));
	}

	/**
	 *
	 */
	@Test
    void testConvertAuditExplicit()
	{
		final KElement xjdf = KElement.createRoot(XJDFConstants.XJDF, null);
		xjdf.setXPathValue("AuditPool/AuditCreated/Header/@DeviceID", "foo");
		final JSONPrepWalker w = new JSONPrepWalker();
		w.setExplicitAudit(true);
		w.walkTree(xjdf, null);
		Assertions.assertEquals("foo", xjdf.getXPathAttribute("AuditPool/Audit[@Name=\"AuditCreated\"]/Header/@DeviceID", null));
	}

	/**
	 *
	 */
	@Test
    void testConvertMediaLayer()
	{
		final XJDFHelper h = new XJDFHelper("j1", "p1");
		final SetHelper ms = h.getCreateSet(ElementName.MEDIA, EnumUsage.Input);
		final JDFMedia m = (JDFMedia) ms.getCreatePartition(0, true).getResource();
		final JDFMediaLayers mediaLayers = m.appendMediaLayers();
		mediaLayers.appendElement(ElementName.GLUE);
		mediaLayers.appendElement(ElementName.MEDIA);
		mediaLayers.appendElement(ElementName.GLUE);
		mediaLayers.appendElement(ElementName.GLUE);

		final JSONPrepWalker w = new JSONPrepWalker();
		w.walkTree(h.getRoot(), null);
		Assertions.assertEquals("Glue", m.getXPathAttribute("MediaLayers[1]/@Name", null));
		Assertions.assertEquals("Media", m.getXPathAttribute("MediaLayers[2]/@Name", null));
		Assertions.assertEquals("Glue", m.getXPathAttribute("MediaLayers[3]/@Name", null));
		Assertions.assertEquals("Glue", m.getXPathAttribute("MediaLayers[4]/@Name", null));
	}

	/**
	 *
	 */
	@Test
    void testConvertBoxFold()
	{
		final XJDFHelper h = new XJDFHelper("j1", "p1");
		final SetHelper ms = h.getCreateSet(ElementName.BOXFOLDINGPARAMS, EnumUsage.Input);
		final JDFBoxFoldingParams bp = (JDFBoxFoldingParams) ms.getCreatePartition(0, true).getResource();
		bp.appendElement(ElementName.GLUE);
		bp.appendElement(ElementName.BOXFOLDACTION).setAttribute(AttributeName.ACTION, "A1");
		bp.appendElement(ElementName.GLUE);
		bp.appendElement(ElementName.GLUE);

		final JSONPrepWalker w = new JSONPrepWalker();
		w.walkTree(h.getRoot(), null);
		Assertions.assertEquals("Glue", bp.getXPathAttribute("BoxFoldAction[1]/@Action", null));
		Assertions.assertEquals("A1", bp.getXPathAttribute("BoxFoldAction[2]/@Action", null));
		Assertions.assertEquals("Glue", bp.getXPathAttribute("BoxFoldAction[3]/@Action", null));
		Assertions.assertEquals("Glue", bp.getXPathAttribute("BoxFoldAction[4]/@Action", null));
	}

	/**
	 *
	 */
	@Test
    void testConvertPTKHeaders()
	{
		final KElement e = KElement.parseFile(sm_dirTestData + "ptk/confirmation.ptk");
		final JSONPrepWalker w = new JSONPrepWalker();
		w.walkTree(e, null);
		Assertions.assertEquals("https://worker.example.org/XJDF", e.getXPathAttribute("Header/From/Credential/@Identity", null));
		Assertions.assertNull(e.getXPathAttribute("Header/From/Credential/Identity", null), "https://worker.example.org/XJDF");
	}

}
