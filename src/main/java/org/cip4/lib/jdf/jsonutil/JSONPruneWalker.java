package org.cip4.lib.jdf.jsonutil;

import java.util.Collection;

import org.cip4.jdflib.core.StringArray;
import org.json.simple.JSONObject;

public class JSONPruneWalker extends JSONWalker
{
	private final Collection<String> zappKeys;

	public JSONPruneWalker(final JSONObjHelper root)
	{
		super(root);
		setKeyInArray(true);
		zappKeys = new StringArray();
	}

	@Override
	protected Object walkSimple(final String key, final Object a)
	{
		return a;
	}

	@Override
	protected Object walkTree(final String rootKey, final JSONObject o)
	{
		zappKeys(o);
		return super.walkTree(rootKey, o);
	}

	void zappKeys(final JSONObject o)
	{
		for (final String zap : zappKeys)
		{
			o.remove(zap);
		}

	}

	public boolean add(final String e)
	{
		return zappKeys.add(e);
	}

	public boolean addAll(final Collection<? extends String> c)
	{
		return zappKeys.addAll(c);
	}

}
