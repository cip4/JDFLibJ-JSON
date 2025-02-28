/*
 * The CIP4 Software License, Version 1.0
 *
 *
 * Copyright (c) 2001-2024 The International Cooperation for the Integration of Processes in Prepress, Press and Postpress (CIP4). All rights reserved.
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
package org.cip4.lib.jdf.jsonutil.schema;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.JDFConstants;
import org.cip4.jdflib.core.StringArray;
import org.cip4.jdflib.datatypes.JDFAttributeMap;
import org.cip4.jdflib.util.ContainerUtil;
import org.cip4.jdflib.util.ListMap;
import org.cip4.jdflib.util.StringUtil;
import org.cip4.lib.jdf.jsonutil.JSONArrayHelper;
import org.cip4.lib.jdf.jsonutil.JSONObjHelper;
import org.cip4.lib.jdf.jsonutil.JSONWriter;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONCase;
import org.json.simple.JSONObject;

public class JSONSchemaUpdate extends JSONObjHelper
{
	static final String ANY_OF = "anyOf";
	static final String ONE_OF = "oneOf";
	static final String ITEMS = "items";
	static final String HASH_DEFS = "#/$defs/";
	static final String OBJECT = "object";
	static final String PRODUCT_INTENT = "ProductIntent";
	static final String ADDITIONAL_PROPERTIES = "additionalProperties";
	static final String REF = "$ref";
	static final String STRING = "string";
	static final String ALL_OF = "allOf";
	static final String PROPERTIES = "properties";
	static final String ARRAY = "array";
	static final String TYPE = "type";
	static final String ENUM = "enum";
	static final String REQUIRED = "required";
	static final String DEFS = "$defs";
	static final String DEFS_SLASH = DEFS + JDFConstants.SLASH;
	private eJSONCase jsonCase;
	final JSONSchemaWalker jsonSchemaWalker;
	protected final Set<String> explicitAbstract;

	public JSONSchemaUpdate(final File f)
	{
		super(f);
		explicitAbstract = new HashSet<String>();
		jsonSchemaWalker = new JSONSchemaWalker(this);
	}

	public JSONSchemaUpdate(final InputStream is)
	{
		super(is);
		explicitAbstract = new HashSet<String>();
		jsonSchemaWalker = new JSONSchemaWalker(this);
	}

	public JSONSchemaUpdate(final JSONObject base)
	{
		super(base);
		explicitAbstract = new HashSet<String>();
		jsonSchemaWalker = new JSONSchemaWalker(this);
	}

	private final static Log log = LogFactory.getLog(JSONSchemaUpdate.class);

	void setCaseString(String path, String value)
	{
		path = JSONWriter.updateCase(path, jsonCase);
		value = JSONWriter.updateCase(value, jsonCase);
		setString(path, value);
	}

	String getPath(final String base, final String key)
	{
		return JSONWriter.updateCase(DEFS_SLASH + base + "/properties/" + key, jsonCase);
	}

	JSONObjHelper getDef(final String def)
	{
		return getHelper(DEFS_SLASH + def);
	}

	void setRef(final String base, final String key, final String ref)
	{
		setPath(base, key, "$ref", HASH_DEFS + ref);
	}

	void setAttribute(final String base, final String key, final String typ)
	{
		setPath(base, key, TYPE, typ);
	}

	void setPath(final String base, final String key, final String what, final String val)
	{
		final String path = getPath(base, key);
		setCaseString(path + JDFConstants.SLASH + what, val);
	}

	void updateMapCase(final ListMap<String, String> xMap)
	{
		if (jsonCase != null && !eJSONCase.retain.equals(jsonCase))
		{
			final List<String> l = ContainerUtil.getKeyList(xMap);
			for (final String key : l)
			{
				final List<String> val = xMap.remove(key);
				xMap.put(JSONWriter.updateCase(key, jsonCase), val);
			}
		}

	}

	JSONObjHelper getSchemaParent(final JSONObjHelper h)
	{
		if (h != null)
		{
			final JSONObjHelper p = h.getHelper(PROPERTIES);
			if (p != null)
			{
				return h;
			}
			final String ref = h.getString(REF);
			if (ref != null)
			{
				final JSONObjHelper refHelper = getHelper(DEFS_SLASH + StringUtil.token(ref, -1, "/"));
				return getSchemaParent(refHelper);
			}
			final JSONArrayHelper ah = h.getArrayHelper(ALL_OF);
			for (int i = 0; ah != null && i < 42; i++)
			{
				final JSONObjHelper o = ah.getJSONHelper(i);
				if (o == null)
					return null;
				final JSONObjHelper schemaParent = getSchemaParent(o);
				if (schemaParent != null)
				{

					return o.getString(REF) == null ? o : schemaParent;
				}

			}
		}
		return null;
	}

	void updateOneOf(final StringArray xRoots, final StringArray roots)
	{
		final JSONArrayHelper oneOf = getCreateArray(ONE_OF);
		oneOf.clear();
		for (final String x0 : roots)
		{
			final String x = JSONWriter.updateCase(x0, getJsonCase());
			if (xRoots.contains(x0))
			{
				final JSONObjHelper h = getHelper(DEFS_SLASH + x);
				if (h == null)
					continue;

				h.setString("properties/Name/type", STRING);
				h.getCreateArray("properties/@context/Name").addString(x);

				h.setString("properties/@context/type", STRING);
			}

			final JSONObjHelper ref = new JSONObjHelper(new JSONObject());
			ref.setString(REF, HASH_DEFS + x);
			oneOf.add(ref);
			final JSONObjHelper root = new JSONObjHelper(new JSONObject());
			root.setString(TYPE, OBJECT);
			root.getCreateArray(REQUIRED).addString(x);
			root.getCreateObject(PROPERTIES).setObj(x, ref.getPathObject(null));
			oneOf.add(root);
		}
	}

	public eJSONCase getJsonCase()
	{
		return jsonCase;
	}

	public void setJsonCase(final eJSONCase jsonCase)
	{
		this.jsonCase = jsonCase;
	}

	

}
