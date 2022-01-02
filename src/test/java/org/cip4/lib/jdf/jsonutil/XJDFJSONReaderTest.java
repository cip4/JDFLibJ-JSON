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
 * (c) 2020-2021 Heidelberger Druckmaschinen AG
 *
 */
package org.cip4.lib.jdf.jsonutil;

import static org.junit.Assert.assertEquals;

import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFElement;
import org.cip4.jdflib.core.JDFResourceLink.EnumUsage;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.extensions.AuditPoolHelper;
import org.cip4.jdflib.extensions.SetHelper;
import org.cip4.jdflib.extensions.XJDFConstants;
import org.cip4.jdflib.extensions.XJDFHelper;
import org.cip4.jdflib.resource.process.JDFAddress;
import org.cip4.jdflib.resource.process.JDFCompany;
import org.cip4.jdflib.resource.process.JDFContact;
import org.json.simple.JSONObject;
import org.junit.Test;

/**
 * @author rainer prosi
 *
 */
public class XJDFJSONReaderTest extends JSONTestCaseBase
{

	public static JSONReader getXJDFReader()
	{
		final JSONReader jr = new JSONReader();
		jr.setXJDF();
		return jr;
	}

	/**
	 *
	 */
	@Test
	public void testAddressLine()
	{
		final JSONWriter jsonWriter = XJDFJSONWriterTest.getXJDFWriter();

		final KElement xjdf = JDFElement.createRoot(XJDFConstants.XJDF);
		final XJDFHelper h = XJDFHelper.getHelper(xjdf);
		final SetHelper cs = h.appendSet(ElementName.CONTACT, EnumUsage.Input);
		final JDFContact c = (JDFContact) cs.getCreatePartition(0, true).getResource();
		final JDFAddress add = c.appendAddress();
		add.appendAddressLine().setText("line 1");
		add.appendAddressLine().setText("line 2");
		add.appendAddressLine().setText("line 3");
		h.cleanUp();

		final JSONObject o = jsonWriter.convert(xjdf);
		final JSONReader jr = getXJDFReader();
		final KElement xjdf2 = jr.getElement(o);
		xjdf2.write2File(sm_dirTestDataTemp + "addressline.xjdf");
		assertEquals("line 1", xjdf2.getXPathAttribute("ResourceSet/Resource/Contact/Address/AddressLine[1]", null));
		assertEquals("line 2", xjdf2.getXPathAttribute("ResourceSet/Resource/Contact/Address/AddressLine[2]", null));
		assertEquals("line 3", xjdf2.getXPathAttribute("ResourceSet/Resource/Contact/Address/AddressLine[3]", null));

	}

	/**
	 *
	 */
	@Test
	public void testOrgUnit()
	{
		final JSONWriter jsonWriter = XJDFJSONWriterTest.getXJDFWriter();

		final KElement xjdf = JDFElement.createRoot(XJDFConstants.XJDF);
		final XJDFHelper h = XJDFHelper.getHelper(xjdf);
		final SetHelper cs = h.appendSet(ElementName.CONTACT, EnumUsage.Input);
		final JDFContact c = (JDFContact) cs.getCreatePartition(0, true).getResource();
		final JDFCompany cm = c.appendCompany();
		cm.setOrganizationName("ACME");
		cm.appendOrganizationalUnit("ACME Unit 1");
		cm.appendOrganizationalUnit("ACME Unit 2");
		cm.appendOrganizationalUnit("ACME Unit 3");
		h.cleanUp();

		final JSONObject o = jsonWriter.convert(xjdf);
		new JSONObjHelper(o).writeToFile(sm_dirTestDataTemp + "orgunit.json");
		h.writeToFile(sm_dirTestDataTemp + "orgunit.xjdf");
		final JSONReader jr = getXJDFReader();
		final KElement xjdf2 = jr.getElement(o);
		xjdf2.write2File(sm_dirTestDataTemp + "orgunit2.xjdf");
		assertEquals("ACME Unit 1", xjdf2.getXPathAttribute("ResourceSet/Resource/Contact/Company/OrganizationalUnit[1]", null));
	}

	/**
	 *
	 */
	@Test
	public void testAuditPool()
	{
		final JSONWriter jsonWriter = XJDFJSONWriterTest.getXJDFWriter();

		final KElement xjdf = JDFElement.createRoot(XJDFConstants.XJDF);
		final XJDFHelper h = XJDFHelper.getHelper(xjdf);
		final AuditPoolHelper ap = h.getCreateAuditPool();
		ap.appendAudit("AuditCreated");
		ap.appendAudit("AuditStatus").appendElement(ElementName.DEVICEINFO);
		ap.appendAudit("AuditResource").appendElement(ElementName.RESOURCEINFO);
		ap.appendAudit("AuditStatus").appendElement(ElementName.DEVICEINFO);
		ap.appendAudit("AuditResource").appendElement(ElementName.RESOURCEINFO);
		ap.appendAudit("AuditNotification");
		ap.appendAudit("AuditProcessRun");
		h.cleanUp();

		final JSONObject o = jsonWriter.convert(xjdf);
		new JSONObjHelper(o).writeToFile(sm_dirTestDataTemp + "auditpool.json");
		final JSONReader jr = getXJDFReader();
		final KElement xjdf2 = jr.getElement(o);
		xjdf2.write2File(sm_dirTestDataTemp + "auditpool2.xjdf");
	}

}
