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
import java.util.Map.Entry;
import java.util.Set;

import org.cip4.jdflib.core.StringArray;
import org.cip4.jdflib.extensions.XJDFConstants;
import org.cip4.jdflib.jmf.JDFMessage.EnumFamily;
import org.cip4.jdflib.util.StringUtil;
import org.cip4.lib.jdf.jsonutil.JSONArrayHelper;
import org.cip4.lib.jdf.jsonutil.JSONObjHelper;
import org.json.simple.JSONObject;

public class JSONSchemaMerger extends JSONSchemaUpdate
{
	public JSONSchemaMerger(final File f)
	{
		super(f);
	}

	public JSONSchemaMerger(final InputStream is)
	{
		super(is);
	}

	public JSONSchemaMerger(final JSONObject base)
	{
		super(base);
	}

	void updateAbstract()
	{
		updateAbstractIntent();
		updateAbstractResource();
		updateAbstractMessages();
		// updateAdditionalAbstract();
		removeAbstractRefs();
	}

	void updateXjdfXjmf()
	{
		remove(PROPERTIES);

		final StringArray xRoots = new StringArray(new String[] { XJDFConstants.XJDF, XJDFConstants.XJMF });
		final StringArray roots = new StringArray(xRoots);
		updateOneOf(xRoots, roots);

	}

	void moveToDefs(final String root)
	{
		final JSONObjHelper target = getHelper(DEFS).getCreateObject(root);
		for (final String key : new String[] { PROPERTIES, REQUIRED, TYPE })
		{
			target.put(key, getRoot().remove(key));
		}
	}

	public void mergeSchema(final File f)
	{
		final JSONSchemaMerger up2 = new JSONSchemaMerger(f);
		up2.setJsonCase(getJsonCase());
		up2.moveToDefs("XJMF");

		moveToDefs("XJDF");
		updateDefs(up2);
		updateAuditPool();
		updateAbstract();
		jsonSchemaWalker.setJsonCase(getJsonCase());
		jsonSchemaWalker.setSorted(true);
		jsonSchemaWalker.setKeyInArray(true);
		final JSONObjHelper oh = jsonSchemaWalker.walk();

		updateXjdfXjmf();
		fixbroken();
		setRoot(oh.getRoot());
	}

	void fixbroken()
	{
		setString(DEFS + "/SurfaceColor/type", OBJECT);

	}

	void updateAuditPool()
	{
		final StringArray audits = new StringArray("AuditCreated AuditNotification AuditResource AuditStatus AuditProcessRun");
		for (final String audit : audits)
		{
			updateSingleAudit(audit);
		}

		final JSONObjHelper aph = getHelper("$defs/AuditPool");
		aph.setString(TYPE, ARRAY);
		final JSONObjHelper items = aph.getCreateObject(ITEMS);
		items.remove(ONE_OF);
		final JSONArrayHelper oneof = items.getCreateArray(ANY_OF);
		for (final String audit : audits)
		{
			final JSONObject o = new JSONObject();
			new JSONObjHelper(o).setString(REF, HASH_DEFS + audit);
			oneof.add(o);
		}
	}

	void updateSingleAudit(final String audit)
	{
		final JSONObjHelper a = getHelper(DEFS_SLASH + audit);
		if (a != null)
		{

			JSONObject p0 = a.getObject(ALL_OF + "[0]/" + PROPERTIES);
			JSONArrayHelper r0 = a.getArrayHelper(ALL_OF + "[0]/" + REQUIRED);
			if (p0 == null)
			{
				p0 = a.getObject(ALL_OF + "[1]/" + PROPERTIES);
				r0 = a.getArrayHelper(ALL_OF + "[1]/" + REQUIRED);
				if (r0 != null)
				{
					final JSONArrayHelper req = a.getCreateArray(REQUIRED);
					for (final String r : r0.getStrings())
						req.appendUnique(r);
				}
				a.setObj(PROPERTIES, p0);
			}
			a.setString("properties/Header/$ref", "#/$defs/Header");
			a.setString(TYPE, OBJECT);
			a.getCreateArray("properties/Name/enum").appendUnique(audit);
			a.remove(REF);
			a.remove(ALL_OF);
			final JSONArrayHelper req = a.getCreateArray(REQUIRED);
			req.appendUnique("Header");
			req.appendUnique("Name");
		}
	}

	JSONObjHelper updateSingleName(final String newName)
	{
		final JSONObjHelper h = getHelper(DEFS_SLASH + newName);
		final JSONObjHelper o = getSchemaParent(h);
		final JSONObjHelper p = o == null ? null : o.getHelper(PROPERTIES);
		if (p != null)
		{
			final JSONObjHelper name = p.getCreateObject("Name");
			name.setString(TYPE, STRING);
			name.getCreateArray("enum").addString(newName);
			return o;
		}
		return null;
	}

	void updateDefs(final JSONSchemaUpdate up2)
	{
		final JSONObjHelper myDefs = getHelper(DEFS);
		final JSONObjHelper otherDefs = up2.getHelper(DEFS);
		for (final Object o : otherDefs.getRoot().entrySet())
		{
			final Entry<String, Object> def = (Entry<String, Object>) o;
			final String key = def.getKey();
			final Object val = def.getValue();
			if (val instanceof JSONObject)
			{
				final JSONObject newVal = updaterefs((JSONObject) val);
				myDefs.put(key, newVal);
			}

		}

	}

	JSONObject updaterefs(final JSONObject val)
	{
		String s = val.toJSONString();
		for (int i = 0; i < 10; i++)
		{
			// note the json escaping of the /
			s = StringUtil.replaceString(s, "https:\\/\\/schema.cip4.org\\/jdfschema_2_" + i + "\\/xjdf.json#\\/$defs\\/", "#\\/$defs\\/");
		}
		return new JSONObjHelper(s).getRoot();
	}

	void updateAbstractIntent()
	{
		final JSONObjHelper h = getHelper("$defs/Intent/properties");
		final List<String> alldefs = getHelper(DEFS).getKeys();
		h.remove(PRODUCT_INTENT);
		for (final String def : alldefs)
		{
			if (def.endsWith(XJDFConstants.Intent) && !def.equals(XJDFConstants.Intent) && !def.equals(PRODUCT_INTENT))
			{
				h.setString(def + "/$ref", HASH_DEFS + def);
				explicitAbstract.add(def);
			}
		}

	}

	void updateAbstractMessage(final String key, final String fam)
	{
		final JSONObjHelper xjmfprop = getHelper("$defs/XJMF/properties");
		final JSONObjHelper msg = getHelper(DEFS_SLASH + key);
		final JSONObjHelper family = getHelper(DEFS_SLASH + fam);
		final JSONObjHelper message = getHelper("$defs/Message");
		final JSONObjHelper msgProp = getSchemaParent(msg).getHelper(PROPERTIES);
		final JSONObjHelper famProp = getSchemaParent(family).getHelper(PROPERTIES);
		final JSONObjHelper messageProp = getSchemaParent(message).getHelper(PROPERTIES);
		msgProp.putAll(famProp);
		msgProp.putAll(messageProp);
		xjmfprop.setString(key + "/$ref", HASH_DEFS + key);
		explicitAbstract.add(key);
	}

	void updateAbstractMessages()
	{

		final List<String> alldefs = getHelper(DEFS).getKeys();
		final StringArray fams = new StringArray(EnumFamily.getFamilies());
		fams.remove(EnumFamily.Acknowledge.getName());
		fams.remove(EnumFamily.Registration.getName());
		for (final String key : alldefs)
		{
			for (final String fam : fams)
			{
				if (key.startsWith(fam) && !key.equals(fam))
				{
					updateAbstractMessage(key, fam);
				}
			}
		}
		remove("$defs/XJMF/properties/Message");

	}

	void updateAbstractResource()
	{
		remove("$defs/Resource/allOf");
		remove("$defs/Resource/oneOf");
		final JSONObjHelper h = getHelper("$defs/Resource/properties");
		h.remove("SpecificResource");
		final List<String> keys = getHelper(DEFS).getKeys();
		for (final String key : keys)
		{
			final JSONArrayHelper defHelper = getArrayHelper(DEFS_SLASH + key + "/allOf");
			if (defHelper != null)
			{
				final String ref = defHelper.getJSONHelper(0).getString(REF);
				if ("#/$defs/SpecificResource".equals(ref))
				{
					h.setString(key + "/$ref", HASH_DEFS + key);
					explicitAbstract.add(key);
				}
			}
		}
	}

	void removeAbstractRefs()
	{
		final List<String> alldefs = getHelper(DEFS).getKeys();
		final StringArray fams = new StringArray(EnumFamily.getFamilies());
		fams.remove(EnumFamily.Acknowledge.getName());
		fams.remove(EnumFamily.Registration.getName());
		fams.add("Message");
		fams.add("SpecificResource");
		fams.add(PRODUCT_INTENT);
		fams.add("Audit");

		final Set<String> refs = new HashSet<>();
		for (final String fam : fams)
		{
			refs.add(HASH_DEFS + fam);
		}
		for (final String key : alldefs)
		{
			final JSONObjHelper def = getHelper(DEFS_SLASH + key);
			final JSONArrayHelper allOf = def.getArrayHelper(ALL_OF);
			for (int i = JSONArrayHelper.size(allOf) - 1; i >= 0; i--)
			{
				final JSONObjHelper oh = allOf.getJSONHelper(i);
				final String ref = oh.getString(REF);
				if (refs.contains(ref))
				{
					allOf.remove(i);
				}
			}
			if (JSONArrayHelper.size(allOf) == 1)
			{
				def.remove(ALL_OF);
				def.putAll(allOf.getJSONHelper(0));
			}
		}
		for (final String fam : fams)
		{
			remove(DEFS_SLASH + fam);
		}

	}

}
