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
 * (c) 2019-2021 Heidelberger Druckmaschinen AG
 *
 */
package org.cip4.lib.jdf.jsonutil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.JDFElement;
import org.cip4.jdflib.core.JDFElement.EnumVersion;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.extensions.XJDFConstants;
import org.cip4.jdflib.util.StringUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author rainer prosi
 *
 */
public class JSONReader
{
	private static Log log = LogFactory.getLog(JSONReader.class);
	private boolean wantAttributes;
	private final Set<String> text;

	public boolean isWantAttributes()
	{
		return wantAttributes;
	}

	public void setWantAttributes(final boolean wantAttributes)
	{
		this.wantAttributes = wantAttributes;
	}

	/**
	 * true if we are json, duh!
	 *
	 * @param contentType
	 * @return
	 */
	public static boolean isJSON(final String contentType)
	{
		return JSONWriter.APPLICATION_JSON.equalsIgnoreCase(StringUtil.normalize(contentType, false));
	}

	public JSONReader()
	{
		super();
		wantAttributes = true;
		text = new HashSet<>();
	}

	public KElement getElement(final String s)
	{

		if (s == null)
		{
			return null;
		}
		return getElement(new StringReader(s));
	}

	public KElement getElement(final JSONObject o)
	{

		if (o == null)
		{
			return null;
		}
		return walkTree(o, null);
	}

	/**
	 *
	 * @param a
	 * @return
	 */
	public KElement getElement(final JSONArray a)
	{
		final KElement root = KElement.createRoot("array", null);
		walkArray(null, a, root);
		return root;
	}

	public KElement getElement(final InputStream is)
	{
		if (is == null)
		{
			return null;
		}
		return getElement(new InputStreamReader(is));
	}

	public KElement getElement(final Reader reader)
	{
		if (reader == null)
		{
			return null;
		}
		final JSONParser p = new JSONParser();
		try
		{
			final Object parsed = p.parse(reader);
			if (parsed instanceof JSONObject)
			{
				final JSONObject o = (JSONObject) parsed;
				return walkTree(o, null);
			}
			else if (parsed instanceof JSONArray)
			{
				return getElement((JSONArray) parsed);
			}
		}
		catch (final Exception e)
		{
			log.error("cannot parse stream", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	KElement walkTree(final JSONObject o, KElement root)
	{
		if (root == null && o.size() > 1)
		{
			root = KElement.createRoot("json", null);
		}
		KElement next = root;
		for (final Entry<String, Object> kid : (Set<Entry<String, Object>>) o.entrySet())
		{
			final String key = kid.getKey();
			final Object val = kid.getValue();
			if (val instanceof JSONObject)
			{
				next = root == null ? createRoot(key) : root.appendElement(key);
				walkTree((JSONObject) val, next);
			}
			else if (val instanceof JSONArray)
			{
				if (root == null)
				{
					next = createRoot(key);
					walkArray(null, (JSONArray) val, next);
				}
				else
				{
					walkArray(key, (JSONArray) val, root);
				}
			}
			else if (root == null)
			{
				next = createRoot(key);
				walkSimple(null, val, next);
			}
			else
			{
				walkSimple(key, val, root);
			}
		}
		return next;
	}

	KElement createRoot(final String key)
	{
		final String xmlnsLocalName = KElement.xmlnsLocalName(key);
		if (XJDFConstants.XJDF.equals(xmlnsLocalName) || XJDFConstants.XJMF.equals(xmlnsLocalName))
		{
			return JDFElement.createRoot(xmlnsLocalName, EnumVersion.Version_2_1);
		}
		else
		{
			return KElement.createRoot(key, null);
		}
	}

	void walkArray(final String key, final JSONArray val, final KElement e0)
	{
		boolean hasComplex = false;
		boolean hasSimple = false;
		if (wantAttributes)
		{
			for (final Object a : val)
			{
				if ((a instanceof JSONObject))
				{
					hasComplex = true;
				}
				else if (val != null)
				{
					hasSimple = true;
				}

			}
		}
		final Collection<KElement> oldList = hasComplex && hasSimple ? e0.getChildArray(key, null) : null;
		for (final Object a : val)
		{
			if (a instanceof JSONObject)
			{
				final KElement next = (key == null) ? e0 : e0.appendElement(key);
				walkTree((JSONObject) a, next);
			}
			else if (a instanceof JSONArray)
			{
				walkArray(key, (JSONArray) a, wantAttributes ? e0 : e0.appendElement(key));
			}
			else
			{
				walkSimple(key, a, e0);
			}
		}
		// special handling for mix of element / attribute content
		if (hasSimple && hasComplex && key != null)
		{
			final String txt = e0.getNonEmpty(key);
			e0.removeAttribute(key);
			final Collection<KElement> v = e0.getChildArray(key, null);
			if (v != null)
			{
				if (oldList != null)
				{
					v.removeAll(oldList);
				}
				for (final KElement e : v)
				{
					e.setText(txt);
				}
			}
		}
	}

	void walkSimple(final String key, final Object a, final KElement a0)
	{
		if (a != null)
		{
			if (key == null || key.equals(JSONWriter.TEXT))
			{
				a0.appendText(a.toString());
			}
			else if (wantAttributes)
			{
				a0.appendAttribute(key, a.toString(), null, null, false);
			}
			else
			{
				a0.appendElement(key).setText(a.toString());
			}
		}
	}

	@Override
	public String toString()
	{
		return "JSONReader [wantAttributes=" + wantAttributes + "]";
	}
}
