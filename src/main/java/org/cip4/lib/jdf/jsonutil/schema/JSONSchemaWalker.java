package org.cip4.lib.jdf.jsonutil.schema;

import org.cip4.lib.jdf.jsonutil.JSONArrayHelper;
import org.cip4.lib.jdf.jsonutil.JSONObjHelper;
import org.cip4.lib.jdf.jsonutil.JSONWalker;
import org.cip4.lib.jdf.jsonutil.JSONWriter;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONCase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONSchemaWalker extends JSONWalker
{

	public JSONSchemaWalker(final JSONObjHelper root)
	{
		super(root);
		setKeyInArray(false);
		jsonCase = eJSONCase.retain;
	}

	eJSONCase jsonCase;

	public eJSONCase getJsonCase()
	{
		return jsonCase;
	}

	public void setJsonCase(final eJSONCase jsonCase)
	{
		this.jsonCase = jsonCase;
	}

	@Override
	protected Object walkSimple(final String key, final Object a)
	{
		reduceAbstract(key, a);

		if (a instanceof JSONObject)
		{
			walkObject((JSONObject) a);
		}
		else if (a instanceof JSONArray)
		{
			walkArray((JSONArray) a);
		}
		if (!eJSONCase.retain.equals(jsonCase))
			updateCase(key, a);
		return a;
	}

	void updateCase(final String key, final Object a)
	{
		if ("properties".equals(key))
		{
			final JSONObjHelper oh = JSONObjHelper.getHelper(a);
			for (final String k : oh.getKeys())
			{
				oh.setObj(JSONWriter.updateCase(k, jsonCase), oh.remove(k));
			}

		}
		else if ("required".equals(key))
		{
			final JSONArrayHelper ah = JSONArrayHelper.getHelper(a);
			if (ah != null)
			{
				for (int i = 0; i < ah.size(); i++)
				{
					ah.set(i, JSONWriter.updateCase(ah.getString(i), jsonCase));
				}
			}
		}
	}

	void reduceAbstract(final String key, final Object a)
	{
		if ("properties".equals(key))
		{
			final JSONObjHelper oh = JSONObjHelper.getHelper(a);
			oh.remove("Any");
			oh.remove("any");
			oh.remove("otherAttributes");
		}
		else if ("required".equals(key))
		{
			final JSONArrayHelper ah = JSONArrayHelper.getHelper(a);
			if (ah != null)
			{
				ah.remove("Any");
				ah.remove("any");
				ah.remove("otherAttributes");
			}
		}
	}

	void walkArray(final JSONArray a)
	{
		// TODO Auto-generated method stub

	}

	void walkObject(final JSONObject o)
	{
		updateClazz(new JSONObjHelper(o));
	}

	void updateClazz(final JSONObjHelper oh)
	{
		final JSONObjHelper c = (JSONObjHelper) oh.remove("Clazz");
		if (c != null)
			oh.setObj("Class", c);
		final JSONArrayHelper a = oh.getArrayHelper("required");
		final int i = a != null ? a.indexOf("Clazz") : -1;
		if (i >= 0)
			a.set(i, "Class");
	}

}
