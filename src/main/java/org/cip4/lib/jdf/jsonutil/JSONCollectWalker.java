package org.cip4.lib.jdf.jsonutil;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import org.cip4.jdflib.util.MyInteger;
import org.cip4.jdflib.util.StringUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class JSONCollectWalker extends JSONWalker
{
	@Override
	protected String getArrayKey(final String key)
	{
		final MyInteger peek = inArray.peek();

		final int j = peek.i++;
		return super.getArrayKey(key) + "[" + j + "]";
	}

	@Override
	protected Object walkArray(final String key, final JSONArray val)
	{
		inArray.push(new MyInteger(0));
		final Object ret = super.walkArray(key, val);
		inArray.pop();
		return ret;
	}

	final Map<String, Object> collected;

	public Map<String, Object> getCollected()
	{
		return collected;
	}

	private String context;
	private String filter;
	private boolean isPath;
	final ArrayDeque<MyInteger> inArray;

	public String getFilter()
	{
		return filter;
	}

	public void setFilter(final String filter)
	{
		this.filter = filter;
	}

	public JSONCollectWalker(final JSONObjHelper root)
	{
		super(root);
		setKeyInArray(true);
		collected = new HashMap<>();
		context = null;
		isPath = true;
		inArray = new ArrayDeque<>();
	}

	@Override
	protected Object walkSimple(final String key, final Object a)
	{
		final String myPath = (a instanceof JSONAware) ? context : StringUtil.addToken(context, "/", key);
		final String path = isPath ? myPath : key;
		if (StringUtil.matches(path, filter))
		{
			collected.put(path, a);
		}
		return a;
	}

	@Override
	protected Object walkTree(final String rootKey, final JSONObject o)
	{
		context = StringUtil.addToken(context, "/", rootKey);
		final Object ret = super.walkTree(rootKey, o);
		if (!StringUtil.isEmpty(rootKey))
			context = StringUtil.removeToken(context, -1, "/");
		return ret;
	}

	public boolean isPath()
	{
		return isPath;
	}

	public void setPath(final boolean isPath)
	{
		this.isPath = isPath;
	}

}
