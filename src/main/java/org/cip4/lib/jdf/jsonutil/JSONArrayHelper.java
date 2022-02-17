/*
 * The CIP4 Software License, Version 1.0
 *
 *
 * Copyright (c) 2001-2022 The International Cooperation for the Integration of Processes in Prepress, Press and Postpress (CIP4). All rights reserved.
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
 * (C) 2018-2020 Heidelberger Druckmaschinen AG
 */
package org.cip4.lib.jdf.jsonutil;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.util.ContainerUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author rainer prosi
 */
public class JSONArrayHelper
{

	private JSONArray array;
	private static Log log = LogFactory.getLog(JSONArrayHelper.class);

	/**
	 * @param base
	 */
	public JSONArrayHelper(final JSONArray base)
	{
		super();
		array = base;
	}

	/**
	 * @param f
	 */
	public JSONArrayHelper(final File f)
	{
		this(JSONObjHelper.getFileReader(f));
	}

	public JSONArrayHelper(final Reader reader)
	{
		if (reader == null)
		{
			array = null;
			return;
		}
		final JSONParser p = new JSONParser();
		try
		{
			array = (JSONArray) p.parse(reader);
		}
		catch (final Exception e)
		{
			log.error("cannot parse stream", e);
			array = null;
		}
	}

	/**
	 *
	 */
	public JSONArrayHelper()
	{
		array = null;
	}

	/**
	 * @param json
	 */
	public JSONArrayHelper(final String json)
	{
		this(json == null ? null : new StringReader(json));
	}

	/**
	 * @return
	 */
	public JSONArray getArray()
	{
		return array;
	}

	/**
	 * @param i
	 * @param def
	 * @return
	 */
	public int size()
	{
		if (array == null)
		{
			return 0;
		}
		return array.size();

	}

	/**
	 * @param arrayIndex
	 * @return
	 */
	public JSONObject getJSON(final int arrayIndex)
	{
		final Object o = get(arrayIndex);
		if (o instanceof JSONObject)
		{
			return (JSONObject) o;
		}
		return null;
	}

	/**
	 * @param arrayIndex
	 * @return
	 */
	public JSONObjHelper getJSONHelper(final int arrayIndex)
	{
		final JSONObject o = getJSON(arrayIndex);
		return o == null ? null : new JSONObjHelper(o);
	}

	/**
	 * @param arrayIndex
	 * @return
	 */
	public List<JSONObject> getJSONObjects()
	{
		final List<JSONObject> l = new ArrayList<>(array.size());
		for (final Object o : array)
		{
			if (o instanceof JSONObject)
			{
				l.add((JSONObject) o);
			}
		}
		return l;
	}

	public Object get(final int arrayIndex)
	{
		return ContainerUtil.get(array, arrayIndex);
	}

	public String getString(final int arrayIndex)
	{
		final Object object = get(arrayIndex);
		if (object instanceof String)
		{
			return (String) object;
		}
		return null;
	}

	@Override
	public String toString()
	{
		return "JSONArrayHelper [array=" + array + "]";
	}

	/**
	 * @param jsonArray
	 * @return
	 */
	public static JSONArrayHelper getHelper(final JSONArray jsonArray)
	{
		return jsonArray == null ? null : new JSONArrayHelper(jsonArray);
	}

	/**
	 * @param jsonArray
	 * @return
	 */
	public static int size(final JSONArrayHelper jsonArray)
	{
		return jsonArray == null ? 0 : jsonArray.size();
	}

	/**
	 * @param jsonArray
	 * @return
	 */
	public static boolean isEmpty(final JSONArrayHelper jsonArray)
	{
		return jsonArray == null ? true : jsonArray.isEmpty();
	}

	public boolean isEmpty()
	{
		return array == null ? true : array.isEmpty();
	}

	public boolean add(final JSONObjHelper o)
	{
		final JSONObject jo = o == null ? null : o.getRoot();
		if (jo != null)
		{
			return array.add(jo);
		}
		return false;
	}

	public boolean appendUnique(final JSONObjHelper o)
	{
		final JSONObject jo = o == null ? null : o.getRoot();
		if (jo != null && !array.contains(jo))
		{
			return array.add(jo);
		}
		return false;
	}

	public String getListString()
	{
		final StringBuilder b = new StringBuilder();
		final int size = size();
		for (int i = 0; i < size; i++)
		{
			if (i > 0)
			{
				b.append(' ');
			}
			b.append(get(i).toString());
		}
		return b.toString();
	}

	public JSONArray copyOf()
	{
		final JSONArray a = new JSONArray();
		a.addAll(array);
		return a;
	}

	public void remove(final int i)
	{
		ContainerUtil.remove(array, i);
	}

}
