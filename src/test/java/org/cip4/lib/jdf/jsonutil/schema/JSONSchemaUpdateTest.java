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
package org.cip4.lib.jdf.jsonutil.schema;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;

import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.resource.JDFResource.EnumPartIDKey;
import org.cip4.jdflib.util.FileUtil;
import org.cip4.jdflib.util.UrlUtil;
import org.cip4.lib.jdf.jsonutil.JSONObjHelper;
import org.cip4.lib.jdf.jsonutil.JSONTestCaseBase;
import org.cip4.lib.jdf.jsonutil.JSONWriter;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONCase;
import org.cip4.lib.jdf.jsonutil.XJDFJSONWriterTest;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import com.networknt.schema.ValidationMessage;

class JSONSchemaUpdateTest extends JSONTestCaseBase
{

	@Test
	void testJSONSchemaUpdateBad() throws URISyntaxException
	{
		final File f = new File(sm_dirTestDataTemp + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final KElement e = KElement.createRoot("foo");
		final JSONWriter jsonWriter = XJDFJSONWriterTest.getXJDFWriter(false);
		final JSONObject jo = jsonWriter.convert(e);
		final JSONSchemaReader srf = new JSONSchemaReader(new File(sm_dirTestDataTemp + "schema/Version_2_3/xjdf.json"));
		final Collection<ValidationMessage> ret = srf.checkJSON(jo.toJSONString());
		assertFalse(ret.isEmpty());
	}

	@Test
	void testJSONSchemaUpdate() throws URISyntaxException
	{
		final File f = new File(sm_dirTestData + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final JSONSchemaUpdate up = new JSONSchemaUpdate(f);
		up.update();
		FileUtil.writeFile(up, new File(sm_dirTestDataTemp + "schema/Version_2_3/xjdf.json"));
	}

	@Test
	void testJSONSchemaReaderDigiPrint() throws URISyntaxException
	{
		testJSONSchemaUpdate();
		final JSONSchemaReader sr = new JSONSchemaReader(UrlUtil.fileToUrl(new File(sm_dirTestDataTemp + "schema/Version_2_2/xjdf.json"), true));
		assertNotNull(sr.getTheSchema());
		final String jos = FileUtil.fileToString(new File(sm_dirTestData + "json/Duplex-1Up.XJDF.json"), null);
		assertNotNull(new JSONObjHelper(jos).getRoot());
		final Collection<ValidationMessage> ret = sr.checkJSON(jos);
		assertTrue(ret.isEmpty());
	}

	@Test
	void testToString() throws URISyntaxException
	{
		final File f = new File(sm_dirTestData + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final JSONSchemaUpdate up = new JSONSchemaUpdate(f);
		assertNotNull(up.toString());
	}

	@Test
	void testGetSchemaParent()
	{
		final File f = new File(sm_dirTestData + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final JSONSchemaUpdate up = new JSONSchemaUpdate(f);
		assertNull(up.getSchemaParent(null));
		assertNull(up.getSchemaParent(new JSONObjHelper()));
	}

	@Test
	void testJSONSchemaUpdateXJDF() throws URISyntaxException
	{
		final File f = new File(sm_dirTestData + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final JSONSchemaUpdate up = new JSONSchemaUpdate(f);
		up.addPruneRoot("XJDF");
		up.update();
		FileUtil.writeFile(up, new File(sm_dirTestDataTemp + "schema/Version_2_3/xjmfonly.json"));
	}

	@Test
	void testJSONSchemaUpdateXJMF() throws URISyntaxException
	{
		final File f = new File(sm_dirTestData + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final JSONSchemaUpdate up = new JSONSchemaUpdate(f);
		up.addPruneRoot("XJMF");
		up.update();
		FileUtil.writeFile(up, new File(sm_dirTestDataTemp + "schema/Version_2_3/xjmfonly.json"));
	}

	@Test
	void testJSONSchemaUpdateXJMFSignalNotification() throws URISyntaxException
	{
		final File f = new File(sm_dirTestData + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final JSONSchemaUpdate up = new JSONSchemaUpdate(f);
		up.addPruneRoot("XJMF");
		up.addSingleMessage("SignalNotification");
		up.update();
		FileUtil.writeFile(up, new File(sm_dirTestDataTemp + "schema/Version_2_3/signalnotification.json"));
	}

	@Test
	void testJSONSchemaUpdateXJMFSignalResourceMedia() throws URISyntaxException
	{
		final File f = new File(sm_dirTestData + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final JSONSchemaUpdate up = new JSONSchemaUpdate(f);
		up.addPruneRoot("XJMF");
		up.addSingleMessage("SignalResource");
		up.addSingleResource("Media");
		up.update();
		FileUtil.writeFile(up, new File(sm_dirTestDataTemp + "schema/Version_2_3/signalresmedia.json"));
	}

	@Test
	void testJSONSchemaUpdateXJMFSignalResourceMediaNoID() throws URISyntaxException
	{
		final File f = new File(sm_dirTestData + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final JSONSchemaUpdate up = new JSONSchemaUpdate(f);
		up.addPruneRoot("XJMF");
		up.addSingleMessage("SignalResource");
		up.addSingleResource("Media");
		up.addPruneMore(ElementName.IDENTIFICATIONFIELD);
		up.addPruneMore(ElementName.MISDETAILS);
		up.addPruneMore(ElementName.MEDIALAYERS);

		up.addPruneKey(AttributeName.JOBID);
		up.update();
		FileUtil.writeFile(up, new File(sm_dirTestDataTemp + "schema/Version_2_3/signalresmediajobid.json"));
	}

	@Test
	void testJSONSchemaUpdateXJMFSignalResourceMiscConsumable() throws URISyntaxException
	{
		final File f = new File(sm_dirTestData + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final JSONSchemaUpdate up = new JSONSchemaUpdate(f);
		up.addPruneRoot("XJMF");
		up.addSingleMessage("SignalResource");
		up.addSingleResource(ElementName.MISCCONSUMABLE);
		up.update();
		FileUtil.writeFile(up, new File(sm_dirTestDataTemp + "schema/Version_2_3/signalresmisc.json"));
	}

	@Test
	void testJSONSchemaUpdateXJMFSignalStatus() throws URISyntaxException
	{
		final File f = new File(sm_dirTestData + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final JSONSchemaUpdate up = new JSONSchemaUpdate(f);
		up.addPruneRoot("XJMF");
		up.addSingleMessage("SignalStatus");
		up.update();
		FileUtil.writeFile(up, new File(sm_dirTestDataTemp + "schema/Version_2_3/signalstatus.json"));
	}

	@Test
	void testPrune2() throws URISyntaxException
	{
		final File f = new File(sm_dirTestData + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final JSONSchemaUpdate up = new JSONSchemaUpdate(f);
		up.addSingleMessage("SignalStatus");
		up.update();
		up.addPruneRoot("XJMF");
		up.prune();
		up.prune();
		FileUtil.writeFile(up, new File(sm_dirTestDataTemp + "schema/Version_2_3/signalstatusPrune2.json"));
	}

	@Test
	void testJSONSchemaUpdateXJMFSignalStatusOnly() throws URISyntaxException
	{
		final File f = new File(sm_dirTestData + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final JSONSchemaUpdate up = new JSONSchemaUpdate(f);
		up.addPruneRoot("SignalStatus");
		up.addSingleMessage("SignalStatus");
		up.update();
		FileUtil.writeFile(up, new File(sm_dirTestDataTemp + "schema/Version_2_3/signalstatus2.json"));
	}

	@Test
	void testJSONSchemaUpdateXJMFSignalResourceMediaPart() throws URISyntaxException
	{
		final File f = new File(sm_dirTestData + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final JSONSchemaUpdate up = new JSONSchemaUpdate(f);
		up.addPruneRoot("XJMF");
		up.addSingleMessage("SignalResource");
		up.addSingleResource("Media");
		up.addPartidkey(EnumPartIDKey.SheetName.getName());
		up.addPartidkey(EnumPartIDKey.Side.getName());
		up.addPartidkey(EnumPartIDKey.Separation.getName());
		up.addPartidkey(EnumPartIDKey.PartVersion.getName());
		up.update();
		FileUtil.writeFile(up, new File(sm_dirTestDataTemp + "schema/Version_2_3/signalresmediapart.json"));
	}

	@Test
	void testJSONSchemaUpdateLower() throws URISyntaxException
	{
		final File f = new File(sm_dirTestData + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final JSONSchemaUpdate up = new JSONSchemaUpdate(f);
		up.setJsonCase(eJSONCase.lower);
		up.update();
		FileUtil.writeFile(up, new File(sm_dirTestDataTemp + "schema/Version_2_3/xjdf.lower.json"));
	}

}
