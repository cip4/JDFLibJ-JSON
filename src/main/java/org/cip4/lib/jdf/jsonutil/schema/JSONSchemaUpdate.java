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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.core.StringArray;
import org.cip4.jdflib.extensions.MessageHelper;
import org.cip4.jdflib.extensions.MessageHelper.EFamily;
import org.cip4.jdflib.extensions.XJDFSchemaWalker;
import org.cip4.jdflib.jmf.JDFMessage.EnumFamily;
import org.cip4.jdflib.util.ContainerUtil;
import org.cip4.jdflib.util.FileUtil;
import org.cip4.jdflib.util.StringUtil;
import org.cip4.lib.jdf.jsonutil.JSONArrayHelper;
import org.cip4.lib.jdf.jsonutil.JSONCollectWalker;
import org.cip4.lib.jdf.jsonutil.JSONObjHelper;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONCase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONSchemaUpdate extends JSONObjHelper
{
	private eJSONCase jsonCase;
	private final StringArray pruneRoots;
	private final StringArray allowedMessages;
	private final StringArray allowedResources;
	private final StringArray allowedPartitions;
	private final StringArray pruneMore;
	final JSONSchemaWalker jsonSchemaWalker;

	public ArrayList<String> getSingleMessages()
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
		pruneMore = new StringArray();
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
		pruneMore = new StringArray();
		jsonSchemaWalker = new JSONSchemaWalker(this);
	}

	public JSONSchemaUpdate(final JSONObject base)
	{
		super(base);
		pruneRoots = new StringArray();
		allowedMessages = new StringArray();
		allowedResources = new StringArray();
		allowedPartitions = new StringArray();
		pruneMore = new StringArray();
		jsonSchemaWalker = new JSONSchemaWalker(this);
	}

	public void addPruneRoot(final String pruneRoot)
	{
		ContainerUtil.appendUnique(pruneRoots, pruneRoot);
	}

	public void addPruneMore(final String skip)
	{
		ContainerUtil.appendUnique(pruneMore, skip);
	}

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

	void prune()
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

	}

	void prune(final HashSet<String> retain, final String pruneroot)
	{
		final JSONObjHelper ph = getHelper("$defs/" + pruneroot);
		if (ph != null)
		{
			retain.add(pruneroot);
			collectPrune(ph, retain);
		}
	}

	void prune(final HashSet<String> retain)
	{
		final JSONObjHelper defs = getHelper("$defs");
		for (final String key : defs.getKeys())
		{
			if (!retain.contains(key))
			{
				defs.remove(key);
			}
		}
		if (!pruneMore.isEmpty())
		{
			final JSONCollectWalker cw = getPruneWalker(this);
			final Map<String, Object> m = cw.getCollected();
			for (final Entry<String, Object> e : m.entrySet())
			{
				final Object o = e.getValue();
				final String s = (String) o;
				final String token = StringUtil.token(s, -1, "/");
				if (pruneMore.contains(token))
				{
					final String key = e.getKey();

					String key0 = StringUtil.removeToken(key, -1, "/");
					if ("items".equals(StringUtil.token(key0, -1, "/")))
					{
						key0 = StringUtil.removeToken(key0, -1, "/");
					}
					final Object zapp = remove(key0);
					if (zapp == null)
					{
						log.error("no zapp " + key);
					}
				}
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
				final String token = StringUtil.token(s, -1, "/");
				if (!pruneMore.contains(token))
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

	private JSONCollectWalker getPruneWalker(final JSONObjHelper root)
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
		ah.getArrayHelper("required").addString("Name");
		ah.setString("properties/Name/type", "string");

		final JSONObjHelper aph = getHelper("$defs/AuditPool");
		aph.setString("type", "array");
		final JSONObjHelper prop = (JSONObjHelper) aph.remove("properties");
		final List<String> keys = prop.getKeys();
		for (final String key : keys)
		{
			if (key.startsWith("Audit"))
			{
				final JSONObject bigAudit = prop.getObject(key);
				aph.getObject(null).putAll(bigAudit);
			}
		}

	}

	void updateMediaLayers()
	{
		final JSONObjHelper mlh = getHelper("$defs/MediaLayers");
		mlh.setString("type", "array");
		final JSONObjHelper prop = (JSONObjHelper) mlh.remove("properties");
		final List<String> keys = prop.getKeys();

		for (final String key : keys)
		{
			if (key.startsWith("Glue"))
			{
				final JSONObjHelper gluepaper = prop.getHelper(key);
				mlh.putAll(gluepaper);
			}
		}
		updateSingleName("Glue");
		updateSingleName("Media");

	}

	void updateSingleAudit(final String audit)
	{
		final JSONObjHelper o = updateSingleName(audit);
		o.setString("properties/Header/$ref", "#/$defs/Header");
		o.getCreateArray("required").addString("Header");
	}

	JSONObjHelper updateSingleName(final String glue)
	{
		final JSONObjHelper h = getHelper("$defs/" + glue);
		final JSONObjHelper o = getSchemaParent(h);
		final JSONObjHelper p = o == null ? null : o.getHelper("properties");
		if (p != null)
		{
			final JSONObjHelper name = p.getCreateObject("Name");
			name.setString("type", "string");
			name.getCreateArray("enum").addString(glue);
			return o;
		}
		return null;
	}

	JSONObjHelper getSchemaParent(final JSONObjHelper h)
	{

		final JSONObjHelper p = h.getHelper("properties");
		if (p != null)
		{
			return h;
		}
		final JSONArrayHelper ah = h.getArrayHelper("allOf");
		for (int i = 0; ah != null && i < 42; i++)
		{
			final JSONObjHelper o = ah.getJSONHelper(i);
			if (o == null)
				return null;
			if (getSchemaParent(o) != null)
				return o;

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

		final List<String> alldefs = getHelper("$defs").getKeys();
		final StringArray fams = new StringArray(EnumFamily.getFamilies());
		fams.remove(EnumFamily.Acknowledge.getName());
		fams.remove(EnumFamily.Registration.getName());
		fams.add("Message");
		fams.add("SpecificResource");
		fams.add("ProductIntent");
		fams.add("Audit");

		final Set<String> refs = new HashSet<>();
		for (final String fam : fams)
		{
			refs.add("#/$defs/" + fam);
		}
		for (final String key : alldefs)
		{
			final JSONObjHelper def = getHelper("$defs/" + key);
			final JSONArrayHelper allOf = def.getArrayHelper("allOf");
			for (int i = JSONArrayHelper.size(allOf) - 1; i >= 0; i--)
			{
				final JSONObjHelper oh = allOf.getJSONHelper(i);
				final String ref = oh.getString("$ref");
				if (refs.contains(ref))
				{
					allOf.remove(i);
				}
			}
			if (JSONArrayHelper.size(allOf) == 1)
			{
				def.remove("allOf");
				def.putAll(allOf.getJSONHelper(0));
			}
		}
		for (final String fam : fams)
		{
			remove("$defs/" + fam);
		}

	}

	void updateAdditionalAbstract()
	{
		final StringArray abs = new StringArray("SpecificResource Audit Message");
		abs.addAll(EnumFamily.getFamilies());
		for (final String ab : abs)
		{
			final JSONObjHelper h = getHelper("$defs/" + ab);
			if (h != null)
				h.setBool("additionalProperties", true);
		}
		setBool("additionalProperties", true);

	}

	void updateAbstractResource()
	{
		final JSONObjHelper h = getHelper("$defs/Resource/properties");
		h.remove("SpecificResource");
		final List<String> keys = allowedResources.isEmpty() ? getHelper("$defs").getKeys() : allowedResources;
		for (final String key : keys)
		{
			final JSONArrayHelper defHelper = getArrayHelper("$defs/" + key + "/allOf");
			if (defHelper != null)
			{
				final String ref = defHelper.getJSONHelper(0).getString("$ref");
				if ("#/$defs/SpecificResource".equals(ref))
				{
					h.setString(key + "/$ref", "#/$defs/" + key);
				}
			}
		}
	}

	void updateAbstractIntent()
	{
		final JSONObjHelper h = getHelper("$defs/Intent/properties");
		final List<String> alldefs = getHelper("$defs").getKeys();
		h.remove("ProductIntent");
		for (final String def : alldefs)
		{
			if (def.endsWith("Intent") && !def.equals("Intent") && !def.equals("ProductIntent"))
			{
				h.setString(def + "/$ref", "#/$defs/" + def);
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
			final List<String> alldefs = getHelper("$defs").getKeys();
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
		final JSONObjHelper msg = getHelper("$defs/" + key);
		final JSONObjHelper family = getHelper("$defs/" + fam);
		final JSONObjHelper message = getHelper("$defs/Message");
		final JSONObjHelper msgProp = getSchemaParent(msg).getHelper("properties");
		final JSONObjHelper famProp = getSchemaParent(family).getHelper("properties");
		final JSONObjHelper messageProp = getSchemaParent(message).getHelper("properties");
		msgProp.putAll(famProp);
		msgProp.putAll(messageProp);
		xjmfprop.setString(key + "/$ref", "#/$defs/" + key);
	}

	JSONObject getType(final String typ)
	{
		final JSONObject o = new JSONObject();
		o.put("type", typ);
		return o;
	}

	void updateComment()
	{
		final JSONObjHelper comment = getHelper("$defs/Comment");
		final JSONObject jo = (JSONObject) comment.getPathObject("properties");
		jo.remove("Value");

		jo.put("Text", getType("string"));
		final JSONArray req = new JSONArray();
		req.add("Text");
		comment.getRoot().put("required", req);

	}

	void updateXjdfXjmf()
	{
		remove("properties");

		final JSONArrayHelper oneOf = getCreateArray("oneOf");
		final StringArray roots = pruneRoots.isEmpty() ? new StringArray(new String[] { "XJDF", "XJMF" }) : pruneRoots;
		for (final String x : roots)
		{
			final JSONObjHelper h = getHelper("$defs/" + x);

			h.setString("properties/Name/type", "string");
			h.getCreateArray("properties/@context/Name").addString(x);

			h.setString("properties/@context/type", "string");
			// TODO real schema url h.getCreateArray("properties/@context/enum").addString("foo");

			final JSONObjHelper ref = new JSONObjHelper(new JSONObject());
			ref.setString("$ref", "#/$defs/" + x);

			oneOf.add(ref);
			final JSONObjHelper root = new JSONObjHelper(new JSONObject());
			root.setString("type", "object");
			root.getCreateArray("required").addString(x);
			root.getCreateObject("properties").setObj(x, ref.getPathObject(null));
			oneOf.add(root);

		}

	}

	@Override
	public String toString()
	{
		return "JSONSchemaUpdate";
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
