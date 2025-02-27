package org.cip4.lib.jdf.jsonutil.schema;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.lib.jdf.jsonutil.JSONArrayHelper;
import org.cip4.lib.jdf.jsonutil.JSONObjHelper;
import org.cip4.lib.jdf.jsonutil.JSONWalker;
import org.cip4.lib.jdf.jsonutil.JSONWriter;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONCase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONSchemaWalker extends JSONWalker
{
	private static final Log log = LogFactory.getLog(JSONSchemaWalker.class);

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
		if (JSONSchemaUpdate.PROPERTIES.equals(key))
		{
			final JSONObjHelper oh = JSONObjHelper.getHelper(a);
			for (final String k : oh.getKeys())
			{
				oh.setObj(JSONWriter.updateCase(k, jsonCase), oh.remove(k));
			}

		}
		else if (JSONSchemaUpdate.REQUIRED.equals(key) || JSONSchemaUpdate.ENUM.equals(key))
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
		if (JSONSchemaUpdate.PROPERTIES.equals(key))
		{
			final JSONObjHelper oh = JSONObjHelper.getHelper(a);
			oh.remove("Any");
			oh.remove("any");
			oh.remove("otherAttributes");
		}
		else if (JSONSchemaUpdate.REQUIRED.equals(key))
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
	}

}
