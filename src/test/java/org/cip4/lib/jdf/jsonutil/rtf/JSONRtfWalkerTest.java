package org.cip4.lib.jdf.jsonutil.rtf;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.cip4.jdflib.util.ByteArrayIOStream;
import org.cip4.jdflib.util.FileUtil;
import org.cip4.lib.jdf.jsonutil.JSONObjHelper;
import org.cip4.lib.jdf.jsonutil.JSONTestCaseBase;
import org.junit.Test;

public class JSONRtfWalkerTest extends JSONTestCaseBase
{

	@Test
	public void testSimple() throws IOException
	{
		final JSONRtfWalker w = new JSONRtfWalker(new JSONObjHelper("{\"a\":{\"b\":[{\"c\":\"d\"}]}}"));
		final ByteArrayIOStream ios = new ByteArrayIOStream();
		w.writeStream(ios);
		final String s = new String(ios.getInputStream().getBuf());
		assertNotNull(s);
	}

	@Test
	public void testFile() throws IOException
	{
		final JSONRtfWalker w = new JSONRtfWalker(new JSONObjHelper("{\"a\":{\"b\":[{\"c1\":\"d1\",\"e1\":\"e2\"},{\"c2\":\"d2\"}]}}"));
		FileUtil.writeFile(w, new File(sm_dirTestDataTemp + "test.rtf"));
	}

}
