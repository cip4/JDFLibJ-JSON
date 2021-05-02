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
 * (C) 2018-2021 Heidelberger Druckmaschinen AG
 */
package org.cip4.lib.jdf.jsonutil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.JDFConstants;
import org.cip4.jdflib.core.StringArray;
import org.cip4.jdflib.ifaces.IStreamWriter;
import org.cip4.jdflib.util.ByteArrayIOStream;
import org.cip4.jdflib.util.ContainerUtil;
import org.cip4.jdflib.util.FileUtil;
import org.cip4.jdflib.util.StreamUtil;
import org.cip4.jdflib.util.StringUtil;
import org.cip4.jdflib.util.UrlUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author rainer prosi
 */
public class JSONObjHelper implements IStreamWriter
{

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((obj == null) ? 0 : obj.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final JSONObjHelper other = (JSONObjHelper) obj;
		if (this.obj == null)
		{
			if (other.obj != null)
			{
				return false;
			}
		}
		else if (!this.obj.equals(other.obj))
		{
			return false;
		}
		return true;
	}

	private static final String ROOT = "root";
	public final static String APPLICATION_JSON = UrlUtil.APPLICATION_JSON;
	private JSONObject obj;
	private static Log log = LogFactory.getLog(JSONObjHelper.class);
	private boolean isRootJson;

	/**
	 * @param base
	 */
	public JSONObjHelper()
	{
		super();
		isRootJson = true;
		obj = null;
	}

	/**
	 * @param base
	 */
	public JSONObjHelper(final JSONObject base)
	{
		this();
		obj = base;
		isRootJson = true;
	}

	public JSONObjHelper(final Reader reader)
	{
		this();
		if (reader == null)
		{
			return;
		}
		final JSONParser p = new JSONParser();
		try
		{
			final Object parse = p.parse(reader);
			if (parse instanceof JSONObject)
			{
				obj = (JSONObject) parse;
				isRootJson = true;
			}
			else if (parse != null)
			{
				isRootJson = false;
				obj = new JSONObject();
				setObj(ROOT, parse);
			}
		}
		catch (final Exception e)
		{
			log.error("cannot parse stream", e);
			obj = null;
		}
	}

	/**
	 * @param s
	 */
	public JSONObjHelper(final String s)
	{
		this(s == null ? null : new StringReader(s));
	}

	/**
	 * @param s
	 */
	public JSONObjHelper(final byte[] s)
	{
		this(s == null ? null : new StringReader(new String(s, StandardCharsets.UTF_8)));
	}

	/**
	 * @param f
	 */
	public JSONObjHelper(final File f)
	{
		this(getFileReader(f));
	}

	public static String undertocamel(final String toConvert)
	{
		final StringArray v = StringArray.getVString(toConvert, JDFConstants.UNDERSCORE);
		if (StringArray.isEmpty(v))
		{
			return null;
		}
		if (v.size() == 1)
		{
			return StringUtils.isAllUpperCase(v.get(0)) ? StringUtils.capitalize(v.get(0).toLowerCase()) : StringUtils.capitalize(v.get(0));
		}
		else
		{
			String ret = "";
			for (final String s : v)
			{
				ret += StringUtils.capitalize(s.toLowerCase());
			}
			return ret;
		}
	}

	public static JSONObjHelper getHelper(final InputStream is)
	{
		try
		{
			if (is == null || is.available() == 0)
			{
				return null;
			}
		}
		catch (final IOException e)
		{
			return null;
		}
		final JSONObjHelper h = new JSONObjHelper(is);
		StreamUtil.close(is);
		return h.getRoot() == null ? null : h;
	}

	/**
	 * @param f
	 */
	public JSONObjHelper(final InputStream is)
	{
		this(new InputStreamReader(is));
	}

	static FileReader getFileReader(final File f)
	{
		try
		{
			return f == null ? null : new FileReader(f);
		}
		catch (final FileNotFoundException e)
		{
			return null;
		}
	}

	/**
	 * @param path
	 * @param def
	 * @return
	 */
	public int getInt(final String path, final int def)
	{
		final Object base = getPathObject(path);
		if (base instanceof Long)
		{
			return ((Long) base).intValue();
		}
		if (base instanceof Integer)
		{
			return ((Integer) base).intValue();
		}
		if (base instanceof Double)
		{
			return ((Double) base).intValue();
		}
		if (base instanceof String)
		{
			return StringUtil.parseInt((String) base, def);
		}
		return def;
	}

	/**
	 * @param path
	 * @param def
	 * @return
	 */
	public double getDouble(final String path, final double def)
	{
		final Object base = getPathObject(path);
		if (base instanceof Integer)
		{
			return ((Integer) base).doubleValue();
		}
		if (base instanceof Long)
		{
			return ((Long) base).doubleValue();
		}
		if (base instanceof Double)
		{
			return ((Double) base).doubleValue();
		}
		if (base instanceof String)
		{
			return StringUtil.parseDouble((String) base, def);
		}
		return def;
	}

	/**
	 * @param path
	 * @param def
	 * @return
	 */
	public boolean getBool(final String path, final boolean def)
	{
		final Object base = getPathObject(path);
		if (base instanceof Boolean)
		{
			return ((Boolean) base).booleanValue();
		}
		return def;
	}

	/**
	 * @param path
	 * @param def
	 * @return
	 */
	public String getString(final String path)
	{
		final Object base = getPathObject(path);
		if (base instanceof String)
		{
			return (String) base;
		}
		else if (base != null)
		{
			return base.toString();
		}
		return null;
	}

	/**
	 * @param path
	 * @return
	 */
	public JSONArray getArray(final String path)
	{
		final Object base = getPathObject(path);
		if (base instanceof JSONArray)
		{
			return (JSONArray) base;
		}
		return null;
	}

	/**
	 * @param path
	 * @return
	 */
	public JSONObject getObject(final String path)
	{
		final Object base = getPathObject(path);
		if (base instanceof JSONObject)
		{
			return (JSONObject) base;
		}
		return null;
	}

	/**
	 * @param path
	 * @return
	 */
	public JSONObjHelper getHelper(final String path)
	{
		final JSONObject o = getObject(path);
		return new JSONObjHelper(o);
	}

	public void setObj(final String path, final Object value)
	{
		setObj(obj, path, value);
	}

	/**
	 * @param o
	 */
	public void setRoot(final JSONObject o)
	{
		obj = o;
	}

	/**
	 * Note currently no arrays...
	 *
	 * @param o
	 * @param path
	 * @param value
	 */
	void setObj(final JSONObject o, final String path, final Object value)
	{
		if (o != null && !StringUtil.isEmpty(path) && value != null)
		{
			final String first = StringUtil.token(path, 0, JDFConstants.SLASH);
			final String next = StringUtil.removeToken(path, 0, JDFConstants.SLASH);
			if (next == null)
			{
				o.put(first, value);
			}
			else
			{
				Object newNext = o.get(first);
				if (!(newNext instanceof JSONObject))
				{
					newNext = new JSONObject();
					o.put(first, newNext);
				}
				setObj((JSONObject) newNext, next, value);
			}
		}
	}

	/**
	 * @param key xpath-like but 0 based
	 * @return
	 */
	public Object getPathObject(final String path)
	{
		if (obj == null || StringUtil.isEmpty(path))
		{
			return null;
		}
		final String first = StringUtil.token(path, 0, JDFConstants.SLASH);
		final String next = StringUtil.removeToken(path, 0, JDFConstants.SLASH);
		if (next == null)
		{
			return getFirstObject(first);
		}
		else
		{
			final Object newNext = getFirstObject(first);

			return (newNext instanceof JSONObject) ? new JSONObjHelper((JSONObject) newNext).getPathObject(next) : null;
		}
	}

	/**
	 * @param key xpath-like but 0 based
	 * @return
	 */
	public boolean hasPath(final String path)
	{
		return getPathObject(path) != null;
	}

	/**
	 * @return
	 */
	public JSONObject getRoot()
	{
		return obj;
	}

	/**
	 * @return
	 */
	public Object getRootObject()
	{
		return isRootJson ? obj : obj == null ? null : getPathObject(ROOT);
	}

	Object getFirstObject(final String first)
	{
		if (first != null && first.indexOf('[') > 0)
		{
			return getFirstArrayObj(first);
		}
		else if (".".equals(first))
		{
			return obj;
		}
		else
		{
			return obj.get(first);
		}
	}

	Object getFirstArrayObj(final String first)
	{
		final StringArray tokens = StringArray.getVString(first, "[]");
		Object a0 = obj.get(tokens.get(0));
		Object r0 = a0;
		for (int i = 1; i < tokens.size(); i++)
		{
			final int arrayIndex = StringUtil.parseInt(tokens.get(i), Integer.MAX_VALUE);
			if (a0 instanceof JSONArray && arrayIndex != Integer.MAX_VALUE)
			{
				r0 = new JSONArrayHelper((JSONArray) a0).get(arrayIndex);
				if (r0 instanceof JSONArray)
				{
					a0 = r0;
				}
				else
				{
					a0 = null;
				}
			}
			else
			{
				return null;
			}
		}
		return r0;
	}

	/**
	 * @return
	 */
	public String getID()
	{
		return getString("id");
	}

	public File writeToFile(final String string)
	{
		return FileUtil.writeFile(this, UrlUtil.urlToFile(string));

	}

	@Override
	public void writeStream(final OutputStream os) throws IOException
	{
		if (obj != null)
		{
			os.write(obj.toJSONString().getBytes());
		}

	}

	@SuppressWarnings("resource")
	public InputStream getInputStream()
	{
		if (obj != null)
		{
			final String jsonString = obj.toJSONString();
			return new ByteArrayIOStream(jsonString.getBytes()).getInputStream();
		}
		else
		{
			return null;
		}

	}

	/**
	 * @return
	 */
	public String getRootName()
	{
		final Set keySet = obj == null ? null : obj.keySet();
		return ContainerUtil.isEmpty(keySet) ? null : (String) keySet.iterator().next();
	}

	/**
	 * @return
	 */
	public StringArray getRootNames()
	{
		final Set keySet = obj == null ? null : obj.keySet();
		final StringArray a = new StringArray();
		if (!ContainerUtil.isEmpty(keySet))
		{
			for (final Object o : keySet)
			{
				a.add((String) o);
				a.sort(null);
			}
		}
		return a;
	}

	/**
	 * @param path
	 * @param d
	 */
	public void setDouble(final String path, final double d)
	{
		setObj(path, Double.valueOf(d));

	}

	/**
	 * @param path
	 * @param l
	 */
	public void setInt(final String path, final long l)
	{
		setObj(path, Long.valueOf(l));

	}

	/**
	 * @param path
	 * @param s
	 */
	public void setString(final String path, final String s)
	{
		setObj(path, StringUtil.getNonEmpty(s));
	}

	/**
	 * @param path
	 * @param b
	 */
	public void setBool(final String path, final boolean b)
	{
		setObj(path, Boolean.valueOf(b));

	}

	@Override
	public String toString()
	{
		return "JSONObjHelper [" + "isRoot=" + isRootJson + (obj != null ? " obj=" + obj : "") + "]";
	}

	/**
	 * returns an empty arrayhelper that is in path
	 *
	 * @param path
	 * @return
	 */
	public JSONArrayHelper setArray(final String path)
	{
		final JSONArray a = new JSONArray();
		setObj(path, a);
		return new JSONArrayHelper(a);
	}

	/**
	 * @return
	 * @see org.json.simple.JSONObject#toJSONString()
	 */
	public String toJSONString()
	{
		return obj.toJSONString();
	}

	public boolean isNull()
	{
		return obj == null;
	}

	public byte[] getBytes()
	{
		final ByteArrayIOStream byteArrayIOStream = new ByteArrayIOStream(toJSONString().getBytes(StandardCharsets.UTF_8));
		return byteArrayIOStream.getBuf();
	}

}