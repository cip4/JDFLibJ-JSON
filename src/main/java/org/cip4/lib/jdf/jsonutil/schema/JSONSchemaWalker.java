package org.cip4.lib.jdf.jsonutil.schema;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.StringArray;
import org.cip4.jdflib.extensions.XJDFSchemaWalker;
import org.cip4.jdflib.util.ListMap;
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
		xsdWalker = null;
		enumMap = new ListMap<>();
	}

	public XJDFSchemaWalker getXsdWalker()
	{
		return xsdWalker;
	}

	public void setXsdWalker(final XJDFSchemaWalker xsdWalker)
	{
		this.xsdWalker = xsdWalker;
	}

	eJSONCase jsonCase;
	private XJDFSchemaWalker xsdWalker;
	private final ListMap<String, String> enumMap;

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
		else if (JSONSchemaUpdate.REQUIRED.equals(key))
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
		final JSONObjHelper oh = new JSONObjHelper(o);
		updateClazz(oh);
		updateArrayLength(oh);
		fillEnum(oh);
	}

	void fillEnum(final JSONObjHelper oh)
	{
		final JSONArray a = oh.getArray("enum");
		final JSONArrayHelper ah = JSONArrayHelper.getHelper(a);
		if (ah != null)
		{
			final String key = getParents().getString("/", null, null).substring(1);
			enumMap.put(key, ah.getStrings());
		}

	}

	void updateArrayLength(final JSONObjHelper o)
	{
		final StringArray p = getParents();
		if (p.size() > 3)
		{
			final String newPath = p.get(-3) + "/" + p.get(-1);
			final int l = xsdWalker.getLength(newPath);
			if (l > 0)
			{
				o.setInt("minItems", l);
				o.setInt("maxItems", l);
				final Integer min = xsdWalker.getMin(newPath);
				if (min != null)
					o.setDouble("minimum", min);
				final Integer max = xsdWalker.getMax(newPath);
				if (max != null)
					o.setDouble("maximum", max);
				log.info(newPath + " " + l + " " + min + "-" + max);
			}
		}
	}

	void updateClazz(final JSONObjHelper oh)
	{
		final JSONObjHelper c = (JSONObjHelper) oh.remove("Clazz");
		if (c != null)
			oh.setObj("Class", c);
		final JSONArrayHelper a = oh.getArrayHelper(JSONSchemaUpdate.REQUIRED);
		final int i = a != null ? a.indexOf("Clazz") : -1;
		if (i >= 0)
			a.set(i, "Class");
	}

	@Override
	public JSONObjHelper walk()
	{
		enumMap.clear();
		return super.walk();
	}

	public ListMap<String, String> getEnumMap()
	{
		return enumMap;
	}

}
