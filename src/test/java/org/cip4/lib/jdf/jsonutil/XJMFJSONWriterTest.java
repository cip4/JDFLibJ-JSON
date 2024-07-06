/*
 * The CIP4 Software License, Version 1.0
 *
 *
 * Copyright (c) 2001-2024 The International Cooperation for the Integration of Processes in Prepress, Press and Postpress (CIP4). All rights reserved.
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

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.cip4.jdflib.auto.JDFAutoNotification.EnumClass;
import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFElement;
import org.cip4.jdflib.core.JDFElement.EnumNodeStatus;
import org.cip4.jdflib.core.JDFElement.EnumVersion;
import org.cip4.jdflib.core.JDFResourceLink.EnumUsage;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.datatypes.JDFAttributeMap;
import org.cip4.jdflib.extensions.MessageHelper;
import org.cip4.jdflib.extensions.ResourceHelper;
import org.cip4.jdflib.extensions.SetHelper;
import org.cip4.jdflib.extensions.XJDFConstants;
import org.cip4.jdflib.extensions.XJMFHelper;
import org.cip4.jdflib.jmf.JDFDeviceInfo;
import org.cip4.jdflib.jmf.JDFJobPhase;
import org.cip4.jdflib.jmf.JDFMessage.EnumFamily;
import org.cip4.jdflib.jmf.JDFMessage.EnumType;
import org.cip4.jdflib.jmf.JDFResourceInfo;
import org.cip4.jdflib.jmf.JMFBuilderFactory;
import org.cip4.jdflib.resource.JDFNotification;
import org.cip4.jdflib.util.JDFDate;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONRoot;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

/**
 * @author rainer prosi
 *
 */
public class XJMFJSONWriterTest extends JSONTestCaseBase
{

	public static JSONWriter getXJDFWriter()
	{
		JSONWriter.setSchemaUrl(exampleVersion, "foo");
		return XJDFJSONWriterTest.getXJDFWriter(true);
	}

	private long totalProductionCounter = 0;

	/**
	 *
	 */
	@Test
	public void testConvertXJMFFromFile()
	{
		final KElement xjmf = KElement.parseFile(sm_dirTestData + "xjmf/JMF1.xjmf");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setXJDF();
		final JSONObject o = jsonWriter.convert(xjmf);
		final String jsonString = o.toJSONString();
		assertTrue(jsonString.indexOf("\"Header\":{") > 0);
		assertTrue(jsonString.indexOf("\"SignalStatus\":{") > 0);
		new JSONObjHelper(o).writeToFile(sm_dirTestDataTemp + "status.xjmf.json");
		log.info(jsonString);
	}

	/**
	 *
	 */
	@Test
	public void testConvertXJMFFromFileNoSplit()
	{
		final KElement xjmf = KElement.parseFile(sm_dirTestData + "xjmf/JMF1.xjmf");
		final JSONWriter jsonWriter = new JSONWriter();
		jsonWriter.setXJDF(false, false);
		final JSONObject o = jsonWriter.convert(xjmf);
		final String jsonString = o.toJSONString();
		assertTrue(jsonString.indexOf("\"Header\":{") > 0);
		assertTrue(jsonString.indexOf("\"SignalStatus\":[{") > 0);
		new JSONObjHelper(o).writeToFile(sm_dirTestDataTemp + "status.xjmf.json");
		log.info(jsonString);
	}

	/**
	 *
	 */
	@Test
	public void testMinimal()
	{
		final JSONWriter jsonWriter = getXJDFWriter();

		final MessageHelper h = getBaseXJMF(EnumFamily.Signal, EnumType.Notification);
		final JDFNotification n = (JDFNotification) h.appendElement(ElementName.NOTIFICATION);
		n.setClass(EnumClass.Event);
		writeBothJson(h.getRoot().getParentNode_KElement(), jsonWriter, "minimalxjmf.json", true, false);
	}

	/**
	 *
	 */
	@Test
	public void testMinimalSchema()
	{
		final JSONWriter jsonWriter = getXJDFWriter();
		jsonWriter.setJsonRoot(eJSONRoot.schema);

		final MessageHelper h = getBaseXJMF(EnumFamily.Signal, EnumType.Notification);
		final JDFNotification n = (JDFNotification) h.appendElement(ElementName.NOTIFICATION);
		n.setClass(EnumClass.Event);
		writeBothJson(h.getRoot().getParentNode_KElement(), jsonWriter, "minimalxjmf.schema.json", true, false);
	}

	MessageHelper getBaseXJMF(final EnumFamily family, final EnumType typ)
	{
		final XJMFHelper h = new XJMFHelper();
		h.setVersion(EnumVersion.Version_2_2);
		return h.appendMessage(family, typ);
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		JDFElement.setDefaultJDFVersion(exampleVersion);
		totalProductionCounter = System.currentTimeMillis() % 1000000;
		JSONWriter.setSchemaUrl(EnumVersion.Version_2_1, "dummy");

	}

	/**
	 *
	 */
	@Test
	public void testSignalStatus()
	{
		final JSONWriter jsonWriter = getXJDFWriter();

		JMFBuilderFactory.getJMFBuilder(XJDFConstants.XJMF).setSenderID("DeviceID");
		XJMFHelper xjmfHelper = new XJMFHelper();
		xjmfHelper.getHeader().setAttribute(AttributeName.TIME, new JDFDate().setTime(16, 30, 0).getDateTimeISO());
		MessageHelper s = xjmfHelper.appendMessage(EnumFamily.Signal, EnumType.Status);
		s.getHeader().setID("S1");
		s.getHeader().setAttribute(AttributeName.REFID, "SubStatus");
		s.getHeader().setAttribute(AttributeName.TIME, new JDFDate().setTime(16, 30, 0).getDateTimeISO());
		JDFDeviceInfo di = (JDFDeviceInfo) s.getRoot().appendElement(ElementName.DEVICEINFO);
		di.setAttribute(AttributeName.STATUS, "Setup");
		JDFJobPhase p = addJobPhase(di, "j1", "sheet1", "ws1", 0, 100);
		p.setStatus(EnumNodeStatus.Setup);
		p.setStartTime(new JDFDate().setTime(16, 20, 0));
		p.setEndTime(new JDFDate().setTime(16, 30, 0));
		xjmfHelper.cleanUp();
		setSnippet(xjmfHelper, true);
		writeBothJson(xjmfHelper.getRoot(), jsonWriter, "statusSignalSetup.json", true, false);

		xjmfHelper = new XJMFHelper();
		xjmfHelper.getHeader().setAttribute(AttributeName.TIME, new JDFDate().setTime(17, 00, 0).getDateTimeISO());
		s = xjmfHelper.appendMessage(EnumFamily.Signal, EnumType.Status);
		s.getHeader().setID("S2");
		s.getHeader().setAttribute(AttributeName.REFID, "SubStatus");
		s.getHeader().setAttribute(AttributeName.TIME, new JDFDate().setTime(17, 0, 0).getDateTimeISO());
		di = (JDFDeviceInfo) s.getRoot().appendElement(ElementName.DEVICEINFO);
		di.setAttribute(AttributeName.STATUS, "Production");
		p = addJobPhase(di, "j1", "sheet1", "ws1", 2000, 0);
		p.setStatus(EnumNodeStatus.InProgress);
		p.setStartTime(new JDFDate().setTime(16, 30, 0));
		xjmfHelper.cleanUp();
		setSnippet(xjmfHelper, true);
		writeBothJson(xjmfHelper.getRoot(), jsonWriter, "statusSignal.json", true, false);
	}

	/**
	 *
	 */
	@Test
	public void testSignalPaper()
	{
		final JSONWriter jsonWriter = getXJDFWriter();
		JMFBuilderFactory.getJMFBuilder(XJDFConstants.XJMF).setSenderID("DeviceID");
		final XJMFHelper xjmfHelper = new XJMFHelper();
		final MessageHelper q = xjmfHelper.appendMessage(EnumFamily.Signal, EnumType.Resource);
		q.getHeader().setID("S1");
		q.getHeader().setAttribute(AttributeName.REFID, "Sub1");
		final JDFResourceInfo ri = (JDFResourceInfo) q.appendElement(ElementName.RESOURCEINFO);
		ri.setAttribute(AttributeName.SCOPE, "Job");
		ri.setAttribute(AttributeName.JOBID, "Job1");
		ri.setAttribute(AttributeName.JOBPARTID, "Printing");
		final SetHelper sh = new SetHelper(ri.appendElement(XJDFConstants.ResourceSet));
		sh.setUsage(EnumUsage.Input);
		sh.setName(ElementName.MEDIA);
		final ResourceHelper rh = sh.appendPartition(new JDFAttributeMap(AttributeName.SHEETNAME, "S1"), false);
		rh.setExternalID("MIS-ID");
		rh.setAmount(4500, new JDFAttributeMap(AttributeName.LOTID, "Lot1"), true);
		rh.setAmount(66, new JDFAttributeMap(AttributeName.LOTID, "Lot1"), false);
		rh.setAmount(2200, new JDFAttributeMap(AttributeName.LOTID, "Lot2"), true);
		rh.setAmount(22, new JDFAttributeMap(AttributeName.LOTID, "Lot2"), false);
		xjmfHelper.cleanUp();
		setSnippet(xjmfHelper, true);
		writeBothJson(xjmfHelper.getRoot(), jsonWriter, "paperLotResourceSignal.json", true, false);
	}

	/**
	 *
	 */
	@Test
	public void testSignalSimplePaper()
	{
		final JSONWriter jsonWriter = getXJDFWriter();
		JMFBuilderFactory.getJMFBuilder(XJDFConstants.XJMF).setSenderID("DeviceID");
		final XJMFHelper xjmfHelper = new XJMFHelper();
		final MessageHelper q = xjmfHelper.appendMessage(EnumFamily.Signal, EnumType.Resource);
		q.getHeader().setID("S1");
		q.getHeader().setAttribute(AttributeName.REFID, "Sub1");
		final JDFResourceInfo ri = (JDFResourceInfo) q.appendElement(ElementName.RESOURCEINFO);
		ri.setAttribute(AttributeName.SCOPE, "Job");
		ri.setAttribute(AttributeName.JOBID, "Job1");
		ri.setAttribute(AttributeName.JOBPARTID, "Printing");
		final SetHelper sh = new SetHelper(ri.appendElement(XJDFConstants.ResourceSet));
		sh.setUsage(EnumUsage.Input);
		sh.setName(ElementName.MEDIA);
		final ResourceHelper rh = sh.appendPartition(new JDFAttributeMap(AttributeName.SHEETNAME, "S1"), false);
		rh.setExternalID("MIS-ID");
		rh.setAmount(4500, null, true);
		rh.setAmount(66, null, false);
		xjmfHelper.cleanUp();
		setSnippet(xjmfHelper, true);
		writeBothJson(xjmfHelper.getRoot(), jsonWriter, "paperResourceSignal.json", true, false);
	}

	JDFJobPhase addJobPhase(final JDFDeviceInfo di, final String jobID, final String sheetName, final int good, final int waste)
	{
		return addJobPhase(di, jobID, sheetName, "ws1", good, waste);
	}

	JDFJobPhase addJobPhase(final JDFDeviceInfo di, final String jobID, final String sheetName, final String wsid, final int good, final int waste)
	{
		final JDFJobPhase p = di.appendJobPhase();
		p.setJobID(jobID);
		p.setJobPartID("p1");
		p.setPartMap(new JDFAttributeMap(AttributeName.SHEETNAME, sheetName));
		p.setAttribute(AttributeName.WORKSTEPID, wsid);
		if (good > 0)
		{
			p.setAmount(good);
			totalProductionCounter += good;
		}
		if (waste > 0)
		{
			totalProductionCounter += waste;
			p.setWaste(waste);
		}
		if (good + waste > 0)
			di.setTotalProductionCounter(totalProductionCounter);

		return p;
	}

}
