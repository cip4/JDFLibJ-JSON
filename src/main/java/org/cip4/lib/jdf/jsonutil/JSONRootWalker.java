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
package org.cip4.lib.jdf.jsonutil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.JDFConstants;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.datatypes.JDFAttributeMap;
import org.cip4.jdflib.util.ContainerUtil;
import org.cip4.jdflib.util.StringUtil;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONPrefix;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONRoot;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

class JSONRootWalker extends JSONObjHelper
{

	static final Log log = LogFactory.getLog(JSONRootWalker.class);
	ArrayList<String> contextStack;

	/**
	 * 
	 */
	private final JSONWriter jsonWriter;

	JSONRootWalker(final JSONWriter jsonWriter, final KElement root)
	{
		super(new JSONObject());
		this.jsonWriter = jsonWriter;
		this.xmlRoot = root;
		contextStack = new ArrayList<>();

	}

	/**
	 * @param e
	 * @return
	 */
	public void convert()
	{
		KElement e = xmlRoot;
		if (this.jsonWriter.prepWalker != null)
		{
			e = xmlRoot.cloneNewDoc();
			jsonWriter.prepWalker.walkTree(e, null);
		}
		walk(e, getRoot());
		final JSONObject j2 = updateRoot();
		this.setRoot(j2);
		this.jsonWriter.setRoot(j2);
	}

	final KElement xmlRoot;

	/**
	 * @param e
	 * @param parent
	 * @return
	 */
	public boolean walk(final KElement e, final JSONAware parent)
	{
		final int contextSize = contextStack.size();
		final String nodeName = getNodeName(e);
		final JDFAttributeMap map = getAttributes(e);
		boolean hasContent = false;
		hasContent = !JDFAttributeMap.isEmpty(map);
		final String text = e.getText();
		String txt = StringUtil.replaceChar(text, '\n', "\\n", 0);
		txt = StringUtil.normalize(text, false);
		txt = StringUtil.replaceString(text, "\\n", "\n");
		if (this.jsonWriter.mixedText != null && txt != null && this.jsonWriter.mixedElements.contains(this.jsonWriter.getCheckName(e)))
		{
			map.put(this.jsonWriter.mixedText, txt);
			txt = null;
		}
		for (final String key : map.getKeyList())
		{
			map.renameKey(key, nodeName + '/' + key);
		}
		final JSONObject me = jsonWriter.createJSonFromAttributes(map);
		final boolean hasChildren = processChildren(e, me);
		if (txt != null)
		{
			if (hasContent || hasChildren)
			{
				final JSONArray a = new JSONArray();
				a.add(me);
				a.add(jsonWriter.getObjectFromVal(nodeName, txt));
				addToParent(parent, nodeName, a);
			}
			else
			{
				addToParent(parent, nodeName, this.jsonWriter.getObjectFromVal(nodeName, txt));
			}
		}
		else
		{
			addToParent(parent, nodeName, me);
		}
		updateContext(contextSize, me, e);
		return true;
	}

	void updateContext(final int contextSize, final JSONObject me, final KElement e)
	{
		if (eJSONPrefix.context.equals(jsonWriter.prefix))
		{
			if (!StringUtil.isEmpty(e.getPrefix()))
			{
				final String newPrefix = JDFConstants.XMLNS + JDFConstants.COLON + e.getPrefix();
				if (!contextStack.contains(newPrefix))
					contextStack.add(newPrefix);
			}
			if (contextStack.size() > contextSize)
			{
				final JSONObjHelper h = new JSONObjHelper(me);
				if (contextSize == contextStack.size() - 1)
				{
					final Object c = updateSingleContext(h, e);
					if (c != null)
						me.put("@context", c);
				}
				else
				{
					final JSONArray a = new JSONArray();
					while (contextSize < contextStack.size())
					{
						final Object c = updateSingleContext(h, e);
						ContainerUtil.add(a, c);
					}
					if (!a.isEmpty())
						me.put("@context", a);
				}
			}
		}
	}

	Object updateSingleContext(final JSONObjHelper h, final KElement e)
	{
		final String xmlns = contextStack.remove(contextStack.size() - 1);
		final String prefix = StringUtil.token(xmlns, 1, JDFConstants.COLON);
		final String url = e.getNamespaceURIFromPrefix(prefix);
		if ("xsi".equals(prefix))
			return null;
		if (prefix != null)
		{
			final JSONObject o = new JSONObject();
			o.put(prefix, url);
			return o;
		}
		else
		{
			return url;
		}
	}

	/**
	 * @param parent
	 * @param nodeName
	 * @param obj
	 */
	public void addToParent(final JSONAware parent, final String nodeName, final Object obj)
	{
		if (parent instanceof JSONArray)
		{
			((JSONArray) parent).add(obj);
		}
		else if (parent instanceof JSONObject)
		{
			putNonEmpty(parent, nodeName, obj);

		}
	}

	public void createJSonFromAttributes(final JDFAttributeMap map)
	{
		if (!JDFAttributeMap.isEmpty(map))
		{
			for (final Entry<String, String> entry : map.entrySet())
			{
				final String val = entry.getValue();
				final String key = entry.getKey();
				final Object jVal = jsonWriter.getObjectFromVal(key, val);
				putNonEmpty(getRoot(), StringUtil.token(key, -1, JDFConstants.SLASH), jVal);
			}
		}
	}

	Object putNonEmpty(final JSONAware parent, final String nodeName, final Object obj)
	{
		final String key = obj == null ? null : jsonWriter.getKey(nodeName, jsonWriter.keyCase);
		return key == null ? null : ((JSONObject) parent).put(key, obj);
	}

	@SuppressWarnings("unchecked")
	JSONObject updateRoot()
	{
		if (xmlRoot.getParentNode_KElement() == null && !eJSONRoot.retain.equals(jsonWriter.getJsonRoot()))
		{
			final Set<String> keys = getRoot().keySet();
			if (ContainerUtil.size(keys) == 1)
			{
				final String key = keys.iterator().next();
				final JSONObject first = (JSONObject) getRoot().get(key);
				if (eJSONRoot.schema.equals(jsonWriter.getJsonRoot()) || eJSONRoot.xmlname.equals(jsonWriter.getJsonRoot()))
				{
					first.put(AttributeName.NAME, key);
				}
				return first;
			}
			else
			{
				JSONWriter.log.warn("Not modifying multi-root object");
			}
		}
		return getRoot();

	}

	/**
	 * @param parentElem
	 * @param me
	 * @return
	 */
	boolean processChildren(final KElement parentElem, final JSONObject me)
	{
		boolean hasContent = false;
		final String parentName = getNodeName(parentElem);
		final boolean isArray = this.jsonWriter.isArray(parentName);
		final JSONArray parentArray = new JSONArray();
		final Collection<KElement> v = parentElem.getChildArray(null, null);
		if (!ContainerUtil.isEmpty(v))
		{
			final HashSet<String> processedNames = new HashSet<>();
			for (final KElement e : v)
			{
				if (this.jsonWriter.isSkipKey(getNodeName(e)))
				{
					hasContent = processChildren(e, me) || hasContent;
				}
				else if (isArray)
				{
					final JSONObject o = new JSONObject();
					walk(e, o);
					parentArray.add(o);
					hasContent = true;
				}
				else
				{
					hasContent = processChild(parentElem, me, hasContent, processedNames, e);
				}
			}
		}
		if (isArray)
		{
			me.put(parentName, parentArray);
		}

		return hasContent;
	}

	String getNodeName(final KElement e)
	{
		if (eJSONPrefix.underscore.equals(jsonWriter.prefix))
		{
			return StringUtil.replaceChar(e.getNodeName(), ':', JDFConstants.UNDERSCORE, 0);
		}
		else if (eJSONPrefix.none.equals(jsonWriter.prefix))
		{
			return e.getLocalName();
		}
		return e.getNodeName();
	}

	/**
	 * @param e never null
	 */
	JDFAttributeMap getAttributes(final KElement e)
	{
		final JDFAttributeMap atts = e.getAttributeMap_KElement();
		atts.remove(AttributeName.XSITYPE);
		final List<String> keyList = ContainerUtil.getKeyList(atts);
		final boolean needXmlns = eJSONPrefix.context.equals(jsonWriter.prefix);
		if (keyList != null)
		{
			for (final String key : keyList)
			{
				final String prefix = StringUtil.token(key, 0, JDFConstants.COLON);
				if (AttributeName.XMLNS.equalsIgnoreCase(prefix))
				{
					if (needXmlns && !contextStack.contains(key))
					{
						contextStack.add(key);
					}
					atts.remove(key);
				}
				else if (needXmlns && key.indexOf(JDFConstants.COLON) > 0)
				{
					final String newKey = AttributeName.XMLNS + JDFConstants.COLON + prefix;
					if (!contextStack.contains(newKey))
					{
						contextStack.add(newKey);
					}

				}
			}
		}
		return atts;
	}

	boolean processChild(final KElement parentElem, final JSONObject me, boolean hasContent, final HashSet<String> names, final KElement e)
	{
		final String childName = getNodeName(e);
		final String parentName = getNodeName(parentElem);
		if (!names.contains(childName))
		{
			final Collection<KElement> v1 = parentElem.getChildArray(e.getLocalName(), null);
			names.add(childName);
			final int size = v1.size();
			if (size > 0)
			{
				final JSONAware a = getParent(parentName, childName, size);
				boolean hasChild = false;
				for (final KElement cc : v1)
				{
					final boolean hc2 = walk(cc, a == null ? me : a);
					hasChild = hasChild || hc2;
				}
				if (hasChild)
				{
					hasContent = true;
					putNonEmpty(me, childName, a);
				}
			}
			else
			{
				final boolean hasChild = walk(e, me);
				hasContent = hasChild || hasContent;
			}
		}
		return hasContent;
	}

	JSONArray getParent(final String parentName, final String childName, final int n)
	{
		final String key = StringUtil.normalize(childName, true, "_ -");

		if (jsonWriter.skipPool.contains(key))
		{
			return null;
		}
		else if (jsonWriter.wantArray || jsonWriter.arrayNames.contains(key))
		{
			return new JSONArray();
		}
		else
		{
			final String key2 = StringUtil.normalize(parentName, true, "_ -");
			if (key2 != null)
			{
				return getParent(null, key2 + JDFConstants.SLASH + key, n);
			}
			else if (n > 1 && !jsonWriter.knownElems.contains(key))
			{
				if (jsonWriter.isLearnArrays() && jsonWriter.addArray(childName))
				{
					log.info("found new array type: " + childName);
				}
				return new JSONArray();
			}
		}
		return null;
	}

}