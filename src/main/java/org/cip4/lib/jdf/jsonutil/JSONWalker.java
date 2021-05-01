/*
 * The CIP4 Software License, Version 1.0
 *
 *
 * Copyright (c) 2001-2021 The International Cooperation for the Integration of Processes in Prepress, Press and Postpress (CIP4). All rights reserved.
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
 * (c) 2020 Heidelberger Druckmaschinen AG
 *
 */
package org.cip4.lib.jdf.jsonutil;

import java.util.HashSet;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author rainer prosi
 *
 */
public abstract class JSONWalker
{
	private static Log log = LogFactory.getLog(JSONReader.class);
	private final JSONObjHelper root;

	public JSONWalker(final JSONObjHelper root)
	{
		super();
		this.root = root;
	}

	@SuppressWarnings("unchecked")
	protected Object walkTree(final String rootKey, final JSONObject o)
	{
		final Object b = walkSimple(rootKey, o);
		if (b == null)
		{
			return null;
		}
		final HashSet<Entry<String, Object>> copy = new HashSet<>();
		copy.addAll(o.entrySet());
		for (final Entry<String, Object> kid : copy)
		{
			final String key = kid.getKey();
			final Object val = kid.getValue();
			final Object c;
			if (val instanceof JSONObject)
			{
				c = walkTree(key, (JSONObject) val);
			}
			else if (val instanceof JSONArray)
			{
				c = walkArray(key, (JSONArray) val);
			}
			else
			{
				c = walkSimple(key, val);
			}
			if (c == null)
			{
				o.remove(key);
			}
			else
			{
				o.put(key, c);
			}
		}
		return o.isEmpty() ? null : o;
	}

	protected Object walkArray(final String key, final JSONArray val)
	{
		final Object b = walkSimple(key, val);
		if (b == null)
		{
			return null;
		}
		else
		{

			for (int i = val.size() - 1; i >= 0; i--)
			{
				final Object a = val.get(i);
				final Object c;
				if (a instanceof JSONObject)
				{
					c = walkTree(key, (JSONObject) a);
				}
				else if (a instanceof JSONArray)
				{
					c = walkArray(key, (JSONArray) a);
				}
				else
				{
					c = walkSimple(key, a);
				}
				if (c == null)
				{
					val.remove(i);
				}
				else
				{
					val.set(i, c);
				}
			}
		}
		return val.isEmpty() ? null : val;
	}

	/**
	 *
	 * @param key
	 * @param a
	 * @return the modified object - null if we want it zapped
	 */
	protected abstract Object walkSimple(final String key, final Object a);

	public JSONObjHelper walk()
	{
		final Object w = walkTree("", root.getRoot());
		return (w instanceof JSONObject) ? new JSONObjHelper((JSONObject) w) : null;
	}

}
