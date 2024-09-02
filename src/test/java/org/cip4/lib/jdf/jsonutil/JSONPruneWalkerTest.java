package org.cip4.lib.jdf.jsonutil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

class JSONPruneWalkerTest
{

	@Test
	void testZappRetain()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":\"d\"}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		final JSONPruneWalker pw = new JSONPruneWalker(r);
		pw.add("b");
		pw.setRetainNull(true);
		pw.walk();
		assertEquals(new JSONObject(), r.getObject("a"));
	}

	@Test
	void testZapp()
	{
		final String root = "{\"a\":{\"b\":[{\"c\":\"d\"}]}}";
		final JSONObjHelper r = new JSONObjHelper(root);
		final JSONPruneWalker pw = new JSONPruneWalker(r);
		pw.add("b");
		pw.walk();
		assertEquals(null, r.getObject("a"));
	}

}
