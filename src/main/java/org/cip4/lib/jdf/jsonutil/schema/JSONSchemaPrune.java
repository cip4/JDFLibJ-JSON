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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFConstants;
import org.cip4.jdflib.core.StringArray;
import org.cip4.jdflib.core.VString;
import org.cip4.jdflib.extensions.XJDFConstants;
import org.cip4.jdflib.util.ContainerUtil;
import org.cip4.jdflib.util.StringUtil;
import org.cip4.lib.jdf.jsonutil.JSONArrayHelper;
import org.cip4.lib.jdf.jsonutil.JSONCollectWalker;
import org.cip4.lib.jdf.jsonutil.JSONObjHelper;
import org.cip4.lib.jdf.jsonutil.JSONPruneWalker;
import org.json.simple.JSONObject;

public class JSONSchemaPrune extends JSONSchemaUpdate
{
	private final StringArray pruneRoots;
	private final StringArray allowedMessages;
	private final StringArray allowedResources;
	private final StringArray allowedPartitions;
	private final StringArray pruneType;
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

	public JSONSchemaPrune(final File f)
	{
		super(f);
		pruneRoots = new StringArray();
		allowedMessages = new StringArray();
		allowedResources = new StringArray();
		allowedPartitions = new StringArray();
		pruneType = new StringArray();
		pruneKeys = new StringArray();
		preparePrune();
	}

	public JSONSchemaPrune(final InputStream is)
	{
		super(is);
		pruneRoots = new StringArray();
		allowedMessages = new StringArray();
		allowedResources = new StringArray();
		allowedPartitions = new StringArray();
		pruneType = new StringArray();
		pruneKeys = new StringArray();
		preparePrune();
	}

	public JSONSchemaPrune(final JSONObject base)
	{
		super(base);
		pruneRoots = new StringArray();
		allowedMessages = new StringArray();
		allowedResources = new StringArray();
		allowedPartitions = new StringArray();
		pruneType = new StringArray();
		pruneKeys = new StringArray();
		preparePrune();
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

	private final static Log log = LogFactory.getLog(JSONSchemaPrune.class);

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
		prunemessages();
		pruneResources();
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
		jsonSchemaWalker.setJsonCase(getJsonCase());
		jsonSchemaWalker.setSorted(true);
		final JSONObjHelper oh = jsonSchemaWalker.walk();
		setRoot(oh.getRoot());
	}

	void preparePrune()
	{
		prepareResources();

	}

	void prepareResources()
	{
		final JSONObjHelper resource = getDef("Resource");
		final List<String> keys = resource.getKeys();
		keys.remove(ElementName.AMOUNTPOOL); // the only non-array in resource
		for (final String key : keys)
		{
			if (resource.getObject(key) != null)
			{
				explicitAbstract.add(key);
			}
		}

	}

	void pruneResources()
	{
		if (!allowedResources.isEmpty())
		{
			final JSONArrayHelper rq = getCreateArray("$defs/Resource/required");
			final JSONObjHelper resProp = getHelper("$defs/Resource/properties");

			final List<String> alldefs = resProp.getKeys();
			for (final String key : alldefs)
			{
				if (allowedResources.contains(key))
				{
					rq.addString(key);
				}
				else if (explicitAbstract.contains(key))
				{
					resProp.remove(key);
				}
			}
		}

	}

	void prunemessages()
	{
		if (!allowedMessages.isEmpty())
		{
			final JSONArrayHelper rq = getArrayHelper("$defs/XJMF/required");
			final JSONObjHelper xjmfprop = getHelper("$defs/XJMF/properties");

			final List<String> alldefs = xjmfprop.getKeys();
			alldefs.remove("Header");
			for (final String key : alldefs)
			{
				if (allowedMessages.contains(key))
				{
					rq.addString(key);
				}
				else
				{
					xjmfprop.remove(key);
				}
			}
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
			remove(ONE_OF);
			setString(PROPERTIES + JDFConstants.SLASH + roots.get(0) + JDFConstants.SLASH + REF, HASH_DEFS + roots.get(0));
		}

	}

	@Override
	public String toString()
	{
		return "JSONSchemaPrunee [ pruneRoots=" + pruneRoots + ", allowedMessages=" + allowedMessages + ", allowedResources=" + allowedResources + ", allowedPartitions="
				+ allowedPartitions + ", pruneMore=" + pruneType + "]";
	}

}
