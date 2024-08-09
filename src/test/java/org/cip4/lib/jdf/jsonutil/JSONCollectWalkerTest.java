package org.cip4.lib.jdf.jsonutil;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Map;

import org.junit.jupiter.api.Test;

class JSONCollectWalkerTest extends JSONTestCaseBase
{

	@Test
	void testCollectSchema()
	{
		final File f = new File(sm_dirTestData + "schema/Version_2_3/xjdf.json");
		assertTrue(f.canRead());
		final JSONObjHelper oh = new JSONObjHelper(f);
		final JSONCollectWalker w = new JSONCollectWalker(oh);
		w.setFilter("(.)*(\\$)ref");
		w.setKeyInArray(false);
		w.walk();
		final Map<String, Object> m = w.getCollected();
		assertNotNull(m);
		assertTrue(m.containsValue("#/$defs/AuditNotification"));
	}

}
