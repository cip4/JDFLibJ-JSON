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
 * (c) 2020-2021 Heidelberger Druckmaschinen AG
 *
 */
package org.cip4.lib.jdf.jsonutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.cip4.jdflib.auto.JDFAutoMedia.EnumMediaType;
import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFComment;
import org.cip4.jdflib.core.JDFNodeInfo;
import org.cip4.jdflib.core.JDFResourceLink.EnumUsage;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.datatypes.JDFAttributeMap;
import org.cip4.jdflib.extensions.AuditHelper;
import org.cip4.jdflib.extensions.AuditHelper.eAudit;
import org.cip4.jdflib.extensions.AuditPoolHelper;
import org.cip4.jdflib.extensions.ResourceHelper;
import org.cip4.jdflib.extensions.SetHelper;
import org.cip4.jdflib.extensions.XJDFConstants;
import org.cip4.jdflib.extensions.XJDFHelper;
import org.cip4.jdflib.jmf.JDFDeviceInfo;
import org.cip4.jdflib.resource.JDFProcessRun;
import org.cip4.jdflib.resource.process.JDFAddress;
import org.cip4.jdflib.resource.process.JDFCompany;
import org.cip4.jdflib.resource.process.JDFContact;
import org.cip4.jdflib.resource.process.JDFMedia;
import org.cip4.jdflib.resource.process.JDFMediaLayers;
import org.cip4.jdflib.util.JDFDate;
import org.json.simple.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rainer prosi
 *
 */
public class XJDFJSONWriterTest extends JSONTestCaseBase
{

	/**
	 *
	 */
	@Test
	public void testConvertBoolean()
	{
		final KElement xjdf = KElement.parseFile(sm_dirTestData + "xjdf/Poster.xjdf");
		final JSONWriter jsonWriter = getXJDFWriter();
		final JSONObject o = jsonWriter.convert(xjdf);

		assertNotNull(o.toJSONString());
		JSONObjHelper jsonObjHelper = new JSONObjHelper(o);
		assertEquals(Boolean.TRUE, jsonObjHelper.getPathObject("XJDF/ProductList/Product/IsRoot"));
	}

	/**
	 *
	 */
	@Test
	@Ignore
	public void testConvertSkipProductList()
	{
		final KElement xjdf = KElement.parseFile(sm_dirTestData + "xjdf/Poster.xjdf");
		final JSONWriter jsonWriter = getXJDFWriter();
		jsonWriter.addSkipPool(XJDFConstants.ProductList);
		final JSONObject o = jsonWriter.convert(xjdf);
		assertNotNull(o.toJSONString());
		new JSONObjHelper(o).writeToFile(sm_dirTestDataTemp + "json/poster.noproductlist.json");
	}

	/**
	 *
	 */
	@Test
	@Ignore
	public void testConvertSkipProductListBroc()
	{
		final KElement xjdf = KElement.parseFile(sm_dirTestData + "xjdf/brochure.xjdf");
		final JSONWriter jsonWriter = getXJDFWriter();
		jsonWriter.addSkipPool(XJDFConstants.ProductList);
		final JSONObject o = jsonWriter.convert(xjdf);
		assertNotNull(o.toJSONString());
		new JSONObjHelper(o).writeToFile(sm_dirTestDataTemp + "json/Brochure.noproductlist.json");
	}

	public static JSONWriter getXJDFWriter()
	{
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setXJDF(true, false);

		jsonWriter.fillTypesFromSchema(getXJDFSchemaElement(MINOR), true);
		return jsonWriter;
	}

	/**
	 *
	 */
	@Test
	public void testAddressLine()
	{
		final JSONWriter jsonWriter = getXJDFWriter();

		final XJDFHelper h = getBaseXJDF();
		final SetHelper cs = h.appendSet(ElementName.CONTACT, EnumUsage.Input);
		final JDFContact c = (JDFContact) cs.getCreatePartition(0, true).getResource();
		final JDFAddress add = c.appendAddress();
		add.appendAddressLine().setText("line 1");
		add.appendAddressLine().setText("line 2");
		add.appendAddressLine().setText("line 3");
		h.cleanUp();

		writeBothJson(add, jsonWriter, "addressline.json", true);
	}

	/**
	 *
	 */
	@Test
	public void testResourceSet()
	{
		final JSONWriter jsonWriter = getXJDFWriter();
		final XJDFHelper h = new XJDFHelper("J1", null);
		h.setTypes("Product");
		ResourceHelper p = h.getSet(ElementName.NODEINFO, null).getCreatePartition(0, true);
		p.setDescriptiveName("my node");
		p.setPartMap(new JDFAttributeMap("SheetName", "Sheet1"));
		JDFNodeInfo ni = (JDFNodeInfo) p.getResource();
		ni.setAttribute(AttributeName.STATUS, "Waiting");
		ni.setStart(new JDFDate());

		h.cleanUp();
		writeBothJson(h.getRoot(), jsonWriter, "nodeinfo.json", true);
	}

	/**
	 *
	 */
	@Test
	public void testComment()
	{
		final JSONWriter jsonWriter = getXJDFWriter();
		final XJDFHelper h = getBaseXJDF();
		final JDFComment c = (JDFComment) h.appendElement(ElementName.COMMENT);
		c.setText("line 1 \nline 2");
		c.setAuthor("Wyle E Coyote");
		c.setPersonalID("p123");
		h.cleanUp();
		// jsonWriter.convert(h.getRoot());

		writeBothJson(h.getRoot(), jsonWriter, "comment.json", false);
	}

	/**
	 *
	 */
	@Test
	public void testOrgUnit()
	{
		final JSONWriter jsonWriter = getXJDFWriter();

		final XJDFHelper h = getBaseXJDF();
		final SetHelper cs = h.appendSet(ElementName.CONTACT, EnumUsage.Input);
		final JDFContact c = (JDFContact) cs.getCreatePartition(0, true).getResource();
		final JDFCompany cm = c.appendCompany();
		cm.setOrganizationName("ACME");
		cm.appendOrganizationalUnit("ACME Unit 1");
		cm.appendOrganizationalUnit("ACME Unit 2");
		cm.appendOrganizationalUnit("ACME Unit 3");
		h.cleanUp();

		writeBothJson(c, jsonWriter, "orgunit.json", true);
	}

	/**
	 *
	 */
	@Test
	public void testForeign()
	{
		final JSONWriter jsonWriter = getXJDFWriter();

		final XJDFHelper h = getBaseXJDF();
		SetHelper set = h.getCreateSet("Foo:FooBar", EnumUsage.Input);
		ResourceHelper rh = set.getCreatePartition(0, false);
		rh.getRoot().appendElement("Foo:FooBar", "www.foo.com");
		h.cleanUp();
		writeBothJson(h.getRoot(), jsonWriter, "foreign.json", false);
	}

	/**
	 *
	 */
	@Test
	public void testForeignAttribute()
	{
		final JSONWriter jsonWriter = getXJDFWriter();

		final XJDFHelper h = getBaseXJDF();
		SetHelper set = h.getCreateSet(ElementName.CONVENTIONALPRINTINGPARAMS, EnumUsage.Input);
		ResourceHelper rh = set.getCreatePartition(0, true);
		rh.getResource().setAttribute("bar:foo", "abc", "www.bar.com");
		h.cleanUp();
		writeBothJson(rh.getRoot(), jsonWriter, "foreignattribute.json", false);
	}

	/**
	 *
	 */
	@Test
	public void testMultiForeignAttribute()
	{
		final JSONWriter jsonWriter = getXJDFWriter();

		final XJDFHelper h = getBaseXJDF();
		SetHelper set = h.getCreateSet(ElementName.CONVENTIONALPRINTINGPARAMS, EnumUsage.Input);
		ResourceHelper rh = set.getCreatePartition(0, true);
		rh.getResource().setAttribute("bar:foo", "abc", "www.bar.com");
		rh.getResource().setAttribute("bar2:foo2", "abc", "www.bar2.com");
		h.cleanUp();
		writeBothJson(rh.getRoot(), jsonWriter, "foreignattributes.json", false);
	}

	/**
	 *
	 */
	@Test
	public void testAuditPool()
	{
		final JSONWriter jsonWriter = getXJDFWriter();

		final XJDFHelper h = getBaseXJDF();
		KElement xjdf = h.getRoot();
		final AuditPoolHelper ap = h.getCreateAuditPool();
		ap.appendAudit(eAudit.Created);
		AuditHelper status = ap.appendAudit(eAudit.Status);
		JDFDeviceInfo di = (JDFDeviceInfo) status.appendElement(ElementName.DEVICEINFO);
		di.setAttribute("Status", "Production");

		KElement ri0 = ap.appendAudit(eAudit.Resource).appendElement(ElementName.RESOURCEINFO);
		ri0.appendElement(XJDFConstants.ResourceSet).setAttribute("Name", "Component");
		AuditHelper status2 = ap.appendAudit(eAudit.Status);
		JDFDeviceInfo di2 = (JDFDeviceInfo) status2.appendElement(ElementName.DEVICEINFO);
		di2.setAttribute(AttributeName.TOTALPRODUCTIONCOUNTER, "424242");
		di2.setAttribute("Status", "Production");
		AuditHelper rah = ap.appendAudit(eAudit.Resource);
		KElement ri = rah.appendElement(ElementName.RESOURCEINFO);
		ri.appendElement(XJDFConstants.ResourceSet).setAttribute("Name", "Component");

		ap.appendAudit(eAudit.Notification).appendElement(ElementName.NOTIFICATION).setAttribute("Class", "Warning");
		JDFProcessRun pr = (JDFProcessRun) ap.appendAudit(eAudit.ProcessRun).appendElement(ElementName.PROCESSRUN);
		pr.setAttribute("Start", new JDFDate().getDateTimeISO());
		pr.setAttribute("End", new JDFDate().getDateTimeISO());
		pr.setAttribute("EndStatus", "Completed");
		h.cleanUp();

		jsonWriter.convert(xjdf);
		final String output = "auditpool.json";
		writeBothJson(xjdf, jsonWriter, output, true);
	}

	/**
	 *
	 */
	@Test
	public void testMediaLayers()
	{
		final JSONWriter jsonWriter = getXJDFWriter();

		final XJDFHelper h = getBaseXJDF();
		KElement xjdf = h.getRoot();
		final JDFMedia m = (JDFMedia) h.getCreateSet(ElementName.MEDIA, EnumUsage.Input).getCreatePartition(0, true).getResource();
		m.setMediaType(EnumMediaType.SelfAdhesive);
		final JDFMediaLayers mls = m.appendMediaLayers();
		mls.appendMedia().setMediaType(EnumMediaType.Paper);
		mls.appendElement(ElementName.GLUE).setAttribute(AttributeName.AREAGLUE, true, null);
		mls.appendMedia().setMediaType(EnumMediaType.Paper);
		h.cleanUp();
		SetHelper sh = h.getSet(ElementName.MEDIA, null);

		final String output = "medialayers.json";
		writeBothJson(sh.getSet(), jsonWriter, output, true);
	}

	XJDFHelper getBaseXJDF()
	{
		final XJDFHelper h = new XJDFHelper("J1", null);
		h.setTypes("Product");
		h.removeSet(ElementName.NODEINFO);
		return h;
	}

}
