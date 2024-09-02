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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFConstants;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.core.StringArray;
import org.cip4.jdflib.core.VString;
import org.cip4.jdflib.extensions.MessageHelper;
import org.cip4.jdflib.extensions.MessageHelper.EFamily;
import org.cip4.jdflib.extensions.XJDFConstants;
import org.cip4.jdflib.extensions.XJDFSchemaWalker;
import org.cip4.jdflib.jmf.JDFMessage.EnumFamily;
import org.cip4.jdflib.util.ContainerUtil;
import org.cip4.jdflib.util.FileUtil;
import org.cip4.jdflib.util.StringUtil;
import org.cip4.lib.jdf.jsonutil.JSONArrayHelper;
import org.cip4.lib.jdf.jsonutil.JSONCollectWalker;
import org.cip4.lib.jdf.jsonutil.JSONObjHelper;
import org.cip4.lib.jdf.jsonutil.JSONPruneWalker;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONCase;
import org.json.simple.JSONObject;

public class JSONSchemaUpdate extends JSONObjHelper
{
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
	static final String REQUIRED = "required";
	static final String DEFS = "$defs";
	static final String DEFS_SLASH = DEFS + JDFConstants.SLASH;
	private eJSONCase jsonCase;
	private final StringArray pruneRoots;
	private final StringArray allowedMessages;
	private final StringArray allowedResources;
	private final StringArray allowedPartitions;
	private final StringArray pruneType;
	final JSONSchemaWalker jsonSchemaWalker;
	private final StringArray pruneKeys;

	public List<String> getSingleMessages()
	{
		return allowedMessages;
	}

	public void addSingleMessage(final String singleMessage)
	{
		ContainerUtil.appendUnique(allowedMessages, singleMessage);
	}

	public void addSingleResource(final String resource)
	{
		ContainerUtil.appendUnique(allowedResources, resource);
	}

	public JSONSchemaUpdate(final File f)
	{
		super(f);
		pruneRoots = new StringArray();
		allowedMessages = new StringArray();
		allowedResources = new StringArray();
		allowedPartitions = new StringArray();
		pruneType = new StringArray();
		pruneKeys = new StringArray();
		jsonSchemaWalker = new JSONSchemaWalker(this);
		final File xsd = FileUtil.newExtension(f, "xsd");
		final KElement xsdRoot = KElement.parseFile(xsd.getPath());
		final XJDFSchemaWalker xsdWalker = new XJDFSchemaWalker();
		xsdWalker.walkTree(xsdRoot, null);
		jsonSchemaWalker.setXsdWalker(xsdWalker);
	}

	public JSONSchemaUpdate(final InputStream is)
	{
		super(is);
		pruneRoots = new StringArray();
		allowedMessages = new StringArray();
		allowedResources = new StringArray();
		allowedPartitions = new StringArray();
		pruneType = new StringArray();
		pruneKeys = new StringArray();
		jsonSchemaWalker = new JSONSchemaWalker(this);
	}

	public JSONSchemaUpdate(final JSONObject base)
	{
		super(base);
		pruneRoots = new StringArray();
		allowedMessages = new StringArray();
		allowedResources = new StringArray();
		allowedPartitions = new StringArray();
		pruneType = new StringArray();
		pruneKeys = new StringArray();
		jsonSchemaWalker = new JSONSchemaWalker(this);
	}

	/**
	 * add the NAME of a root to RETAIN - all others will be removed
	 *
	 * @param pruneRoot
	 */
	public void addPruneRoot(final String pruneRoot)
	{
		ContainerUtil.appendUnique(pruneRoots, pruneRoot);
	}

	/**
	 * add the Type of object to remove
	 *
	 * @param pruneRoot
	 */
	public void addPruneMore(final String skip)
	{
		ContainerUtil.appendUnique(pruneType, skip);
	}

	/**
	 * add the Name of object to remove
	 *
	 * @param pruneRoot
	 */
	public void addPruneKey(final String skip)
	{
		ContainerUtil.appendUnique(pruneKeys, skip);
	}

	/**
	 * add the Name of partidkey to remove
	 *
	 * @param pruneRoot
	 */
	public void addPartidkey(final String partidkey)
	{
		ContainerUtil.appendUnique(allowedPartitions, partidkey);
	}

	void setPrune(final Collection<String> newPruneRoots)
	{
		pruneRoots.clear();
		ContainerUtil.appendUnique(pruneRoots, newPruneRoots);
	}

	private final static Log log = LogFactory.getLog(JSONSchemaUpdate.class);

	public void update()
	{
		updateComment();
		updateXjdfXjmf();
		updateAuditPool();
		updateAbstract();
		updateMediaLayers();
		updatePart();
		jsonSchemaWalker.setJsonCase(jsonCase);
		jsonSchemaWalker.setSorted(true);
		jsonSchemaWalker.walk();
		prune();
	}

	void updatePart()
	{
		if (!allowedPartitions.isEmpty())
		{
			final JSONObjHelper ph = getHelper("$defs/Part/properties");
			ph.retainAll(allowedPartitions);
		}
	}

	/**
	 * allow for multiple chained prunes
	 */
	public void prune()
	{
		if (!pruneRoots.isEmpty())
		{
			final HashSet<String> retain = new HashSet<>();
			for (final String pruneroot : pruneRoots)
			{
				prune(retain, pruneroot);
			}

			prune(retain);
		}
		pruneMore();
		if (!pruneKeys.isEmpty())
		{
			final JSONPruneWalker zapper = new JSONPruneWalker(this);
			zapper.addAll(pruneKeys);
			zapper.walk();
		}

	}

	void prune(final HashSet<String> retain, final String pruneroot)
	{
		final JSONObjHelper ph = getHelper(DEFS_SLASH + pruneroot);
		if (ph != null)
		{
			retain.add(pruneroot);
			collectPrune(ph, retain);
		}
	}

	public void pruneMore()
	{
		if (!pruneType.isEmpty())
		{
			final JSONCollectWalker cw = getPruneWalker(this);
			final Map<String, Object> m = cw.getCollected();
			for (final String key : m.keySet())
			{
				final VString tokens = StringUtil.tokenize(key, JDFConstants.SLASH, false);
				if (pruneType.containsAny(tokens))
				{
					tokens.retainAll(pruneType);

					final String key0 = tokens.get(0);
					String key1 = key;
					while (!key0.equals(StringUtil.token(key1, -1, JDFConstants.SLASH)))
					{
						key1 = StringUtil.removeToken(key1, -1, JDFConstants.SLASH);
					}
					final Object zapp = remove(key1);
					if (zapp == null)
					{
						log.error("no zapp " + key);
					}
				}
			}
		}

	}

	void prune(final HashSet<String> retain)
	{
		final JSONObjHelper defs = getHelper(DEFS);
		for (final String key : defs.getKeys())
		{
			if (!retain.contains(key))
			{
				defs.remove(key);
			}
		}
	}

	void collectPrune(final JSONObjHelper root, final HashSet<String> retain)
	{
		final JSONCollectWalker cw = getPruneWalker(root);

		final Map<String, Object> m = cw.getCollected();
		if (m != null)
		{
			final List<String> keyList = new StringArray();
			for (final Entry<String, Object> e : m.entrySet())
			{
				final Object o = e.getValue();
				final String s = (String) o;
				final String token = StringUtil.token(s, -1, JDFConstants.SLASH);
				if (!pruneType.contains(token))
				{
					keyList.add(token);
				}
			}

			for (final String newKey : keyList)
			{
				if (!retain.contains(newKey))
				{
					prune(retain, newKey);
				}
			}
		}
	}

	JSONCollectWalker getPruneWalker(final JSONObjHelper root)
	{
		final JSONCollectWalker cw = new JSONCollectWalker(root);
		cw.setFilter("(.)*\\$ref");
		cw.setPath(true);
		cw.setKeyInArray(true);
		cw.walk();
		return cw;
	}

	void updateAuditPool()
	{
		final StringArray audits = new StringArray("AuditCreated AuditNotification AuditResource AuditStatus AuditProcessRun");
		for (final String audit : audits)
		{
			updateSingleAudit(audit);
		}
		final JSONObjHelper ah = getHelper("$defs/Audit");
		ah.getArrayHelper(REQUIRED).addString("Name");
		ah.setString("properties/Name/type", STRING);

		final JSONObjHelper aph = getHelper("$defs/AuditPool");
		aph.setString(TYPE, ARRAY);
		final JSONObjHelper prop = (JSONObjHelper) aph.remove(PROPERTIES);
		final List<String> keys = prop.getKeys();
		for (final String key : keys)
		{
			if (key.startsWith("Audit"))
			{
				final JSONObjHelper bigAudit = prop.getHelper(key);
				aph.putAll(bigAudit);
			}
		}

	}

	void updateMediaLayers()
	{
		final JSONObjHelper mlh = getHelper("$defs/MediaLayers");
		mlh.setString(TYPE, ARRAY);
		final JSONObjHelper prop = (JSONObjHelper) mlh.remove(PROPERTIES);
		final List<String> keys = prop.getKeys();

		for (final String key : keys)
		{
			if (key.startsWith(ElementName.GLUE))
			{
				final JSONObjHelper gluepaper = prop.getHelper(key);
				mlh.putAll(gluepaper);
			}
		}
		updateSingleName(ElementName.GLUE);
		updateSingleName(ElementName.MEDIA);

	}

	void updateSingleAudit(final String audit)
	{
		final JSONObjHelper o = updateSingleName(audit);
		if (o != null)
		{
			o.setString("properties/Header/$ref", "#/$defs/Header");
			o.getCreateArray(REQUIRED).addString("Header");
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

	JSONObjHelper getSchemaParent(final JSONObjHelper h)
	{
		if (h != null)
		{
			final JSONObjHelper p = h.getHelper(PROPERTIES);
			if (p != null)
			{
				return h;
			}
			final JSONArrayHelper ah = h.getArrayHelper(ALL_OF);
			for (int i = 0; ah != null && i < 42; i++)
			{
				final JSONObjHelper o = ah.getJSONHelper(i);
				if (o == null)
					return null;
				if (getSchemaParent(o) != null)
					return o;

			}
		}
		return null;
	}

	void updateAbstract()
	{
		updateAbstractIntent();
		updateAbstractResource();
		updateAbstractMessages();
		updateAdditionalAbstract();
		removeAbstractRefs();
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

	void updateAdditionalAbstract()
	{
		final StringArray abs = new StringArray("SpecificResource Audit Message");
		abs.addAll(EnumFamily.getFamilies());
		for (final String ab : abs)
		{
			final JSONObjHelper h = getHelper(DEFS_SLASH + ab);
			if (h != null)
				h.setBool(ADDITIONAL_PROPERTIES, true);
		}
		setBool(ADDITIONAL_PROPERTIES, true);

	}

	void updateAbstractResource()
	{
		final JSONObjHelper h = getHelper("$defs/Resource/properties");
		h.remove("SpecificResource");
		final List<String> keys = allowedResources.isEmpty() ? getHelper(DEFS).getKeys() : allowedResources;
		for (final String key : keys)
		{
			final JSONArrayHelper defHelper = getArrayHelper(DEFS_SLASH + key + "/allOf");
			if (defHelper != null)
			{
				final String ref = defHelper.getJSONHelper(0).getString(REF);
				if ("#/$defs/SpecificResource".equals(ref))
				{
					h.setString(key + "/$ref", HASH_DEFS + key);
				}
			}
		}
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
			}
		}

	}

	void updateAbstractMessages()
	{
		final JSONArrayHelper rq = getArrayHelper("$defs/XJMF/required");
		rq.remove("MessageOrany");
		if (!allowedMessages.isEmpty())
		{
			for (final String singleMessage : allowedMessages)
			{
				rq.addString(singleMessage);
				final EFamily fam = new MessageHelper(KElement.createRoot(singleMessage)).getEFamily();
				updateAbstractMessage(singleMessage, fam.name());
			}
		}
		else
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
		}
		remove("$defs/XJMF/properties/MessageOrany");

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
	}

	void updateComment()
	{
		final JSONObjHelper comment = getHelper("$defs/Comment");
		final JSONObjHelper jo = comment.getHelper(PROPERTIES);
		jo.remove("Value");

		jo.setString("Text/" + TYPE, STRING);
		comment.setArray(REQUIRED).addString("Text");
	}

	void updateXjdfXjmf()
	{
		remove(PROPERTIES);

		final StringArray xRoots = new StringArray(new String[] { XJDFConstants.XJDF, XJDFConstants.XJMF });
		final StringArray roots = pruneRoots.isEmpty() ? new StringArray(xRoots) : pruneRoots;
		boolean oo = roots.size() > 1;
		oo = oo || pruneRoots.containsAny(xRoots);
		if (oo)
		{
			updateOneOf(xRoots, roots);
		}
		else if (!roots.isEmpty())
		{
			setString(PROPERTIES + JDFConstants.SLASH + roots.get(0) + JDFConstants.SLASH + REF, HASH_DEFS + roots.get(0));
		}

	}

	void updateOneOf(final StringArray xRoots, final StringArray roots)
	{
		final JSONArrayHelper oneOf = getCreateArray("oneOf");
		for (final String x : roots)
		{
			if (xRoots.contains(x))
			{
				final JSONObjHelper h = getHelper(DEFS_SLASH + x);

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

	@Override
	public String toString()
	{
		return "JSONSchemaUpdate [jsonCase=" + jsonCase + ", pruneRoots=" + pruneRoots + ", allowedMessages=" + allowedMessages + ", allowedResources=" + allowedResources
				+ ", allowedPartitions=" + allowedPartitions + ", pruneMore=" + pruneType + "]";
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
