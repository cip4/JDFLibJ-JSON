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
 *
 * (c) 2016-2020 Heidelberger Druckmaschinen AG
 *
 */
package org.cip4.lib.jdf.jsonutil;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFConstants;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.core.StringArray;
import org.cip4.jdflib.datatypes.JDFAttributeMap;
import org.cip4.jdflib.datatypes.JDFNumberList;
import org.cip4.jdflib.elementwalker.ElementWalker;
import org.cip4.jdflib.util.ByteArrayIOStream;
import org.cip4.jdflib.util.ContainerUtil;
import org.cip4.jdflib.util.StringUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

/**
 * @author rainer prosi
 */
public class JSONWriter extends JSONObjHelper
{

	private static final String XML_SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
	static final String TEXT = "Text";
	boolean wantArray;
	boolean learnArrays;
	boolean typeSafe;
	ElementWalker prepWalker;

	/**
	 * @return the prepWalker
	 */
	public ElementWalker getPrepWalker()
	{
		return prepWalker;
	}

	/**
	 * apply standard xjdf settings
	 */
	public void setXJDF()
	{
		final JSONPrepWalker jsonPrepWalker = new JSONPrepWalker();
		jsonPrepWalker.setExplicitAudit(false);
		this.prepWalker = jsonPrepWalker;
		setPrefix(eJSONPrefix.retain);
		setKeyCase(eJSONCase.retain);
		setValueCase(eJSONCase.retain);
		setMixedText(TEXT);
		addMixed(ElementName.COMMENT);
	}

	/**
	 * @param prepWalker the prepWalker to set
	 */
	public void setPrepWalker(final ElementWalker prepWalker)
	{
		this.prepWalker = prepWalker;
	}

	eJSONPrefix prefix;

	/**
	 * @return the wantPrefix
	 */
	public eJSONPrefix getPrefix()
	{
		return prefix;
	}

	/**
	 * @param prefix the wantPrefix to set
	 */
	public void setPrefix(final eJSONPrefix prefix)
	{
		this.prefix = prefix;
	}

	private String mixedText;

	public enum eJSONCase
	{
		retain, lower, upper, lowerfirst;

		public static List<String> getNames()
		{
			final StringArray a = new StringArray();
			for (final eJSONCase e : values())
			{
				a.add(e.name());
			}
			return a;
		}

		public static eJSONCase getEnum(final String name)
		{
			for (final eJSONCase e : values())
			{
				if (e.name().equalsIgnoreCase(name))
					return e;
			}
			return null;
		}
	}

	public enum eJSONRoot
	{
		retain, none, schema;

		public static List<String> getNames()
		{
			final StringArray a = new StringArray();
			for (final eJSONRoot e : values())
			{
				a.add(e.name());
			}
			return a;
		}

		public static eJSONRoot getEnum(final String name)
		{
			for (final eJSONRoot e : values())
			{
				if (e.name().equalsIgnoreCase(name))
					return e;
			}
			return null;
		}
	}

	public enum eJSONPrefix
	{
		retain, underscore, none;

		public static List<String> getNames()
		{
			final StringArray a = new StringArray();
			for (final eJSONPrefix e : values())
			{
				a.add(e.name());
			}
			return a;
		}

		public static eJSONPrefix getEnum(final String name)
		{
			for (final eJSONPrefix e : values())
			{
				if (e.name().equalsIgnoreCase(name))
					return e;
			}
			return null;
		}

	}

	eJSONCase keyCase;
	eJSONCase valueCase;
	static final Log log = LogFactory.getLog(JSONWriter.class);
	static final String SCHEMA = "Schema";

	public boolean isTypeSafe()
	{
		return typeSafe;
	}

	public void setTypeSafe(final boolean typeSafe)
	{
		this.typeSafe = typeSafe;
	}

	public boolean isLearnArrays()
	{
		return learnArrays;
	}

	public void setLearnArrays(final boolean learnArrays)
	{
		this.learnArrays = learnArrays;
	}

	final Set<String> arrayNames;
	final Set<String> mixedElements;
	final Set<String> alwaysString;
	final Set<String> stringArray;
	final Set<String> skipPool;
	final Set<String> transferFunction;
	private eJSONRoot jsonRoot;

	public eJSONRoot getJsonRoot()
	{
		return jsonRoot;
	}

	public void setJsonRoot(final eJSONRoot jsonRoot)
	{
		this.jsonRoot = jsonRoot;
	}

	/**
	 * @return true if all elements should be wrapped in an array
	 */
	public boolean isWantArray()
	{
		return wantArray;
	}

	/**
	 * @param wantArray true if all elements should be wrapped in an array (default=true)
	 */
	public void setWantArray(final boolean wantArray)
	{
		this.wantArray = wantArray;
	}

	/**
	 * @param element
	 * @return
	 */
	public boolean addArray(final String element)
	{
		final String key = StringUtil.normalize(element, true, "_ -");
		if (key != null)
		{
			return arrayNames.add(key);
		}
		return false;
	}

	public boolean addSkipPool(final String element)
	{
		final String key = StringUtil.normalize(element, true, "_ -");
		if (key != null)
		{
			return skipPool.add(key);
		}
		return false;
	}

	public boolean addString(final String attribute)
	{
		final String key = StringUtil.normalize(attribute, true, "_ -");
		if (key != null)
		{
			return alwaysString.add(key);
		}
		return false;
	}

	public boolean addMixed(final String element)
	{
		final String key = StringUtil.normalize(element, true, "_ -");
		if (key != null)
		{
			return mixedElements.add(key);
		}
		return false;
	}

	public boolean addStringArray(final String attribute)
	{
		final String key = StringUtil.normalize(attribute, true, "_ -");
		if (key != null)
		{
			return stringArray.add(key);
		}
		return false;
	}

	/**
	 * @param schema
	 */
	public void fillTypesFromSchema(final KElement schema)
	{

		final Collection<KElement> ve = schema == null ? null : schema.getChildrenByTagName("element", XML_SCHEMA_NS, null, false, true, 0);
		if (ve != null)
		{
			for (final KElement e : ve)
			{
				final String maxOcc = e.getNonEmpty("maxOccurs");
				if ("unbounded".equals(maxOcc) || StringUtil.parseInt(maxOcc, 0) > 1)
				{
					fillArrayFromSchema(e);
				}
			}
		}

		final Collection<KElement> va = schema == null ? null : schema.getChildrenByTagName("attribute", XML_SCHEMA_NS, null, false, true, 0);
		if (va != null)
		{
			final Set<String> types = new HashSet<>();
			types.addAll(new StringArray(new String[] { "float", "double", "int", "integer", "long", "boolean", "CMYKColor", "FloatList", "IntegerList", "IntegerRange", "LabColor",
					"matrix", "rectangle", "shape", "sRGBColor", "XYPair", "TransferFunction" }));
			for (final KElement e : va)
			{
				final String type = getTypeFromSchemaAttribute(e);
				final String name = StringUtil.normalize(e.getNonEmpty("name"), true, "_:-");
				if ("NMTOKENS".equals(type) || "IDREFS".equals(type))
				{
					addStringArray(name);
				}
				else if ("TransferFunction".equals(type))
				{
					addTransferFunction(name);
				}
				else if (!types.contains(type))
				{
					addString(name);
				}
			}
		}
	}

	KElement getAncestor(final KElement e, final String name)
	{
		if (e == null)
			return null;
		final KElement parent = e.getParentNode_KElement();
		if (parent != null && name.equals(parent.getLocalName()))
			return parent;
		return getAncestor(parent, name);
	}

	public void fillArrayFromSchema(final KElement e)
	{
		final String name = e.getNonEmpty("ref");
		KElement parentContent = getAncestor(e, "complexType");
		String contentName = null;
		if (parentContent == null || parentContent.getNonEmpty("name") == null)
		{
			parentContent = getAncestor(e, "element");
			contentName = parentContent == null || parentContent.equals(e) ? null : parentContent.getNonEmpty("name");
		}
		else
		{
			contentName = parentContent.getNonEmpty("name");
		}
		if (parentContent != null && contentName == null)
			contentName = parentContent.getNonEmpty("ref");

		final String key = contentName == null ? name : contentName + JDFConstants.SLASH + name;
		addArray(key);
	}

	public boolean addTransferFunction(final String name)
	{
		final String key = StringUtil.normalize(name, true, "_ -");
		if (key != null)
		{
			return transferFunction.add(key);
		}
		return false;
	}

	String getTypeFromSchemaAttribute(final KElement e)
	{
		String type = e.getAttribute("type");
		if (StringUtil.isEmpty(type))
		{
			type = e.getXPathAttribute("xs:restriction/@base", null);
		}
		if (StringUtil.isEmpty(type))
		{
			type = e.getXPathAttribute("xs:simpleType/xs:restriction/@base", null);
		}
		if (StringUtil.isEmpty(type) && e.getXPathElement("xs:simpleType/xs:list") != null)
		{
			type = e.getXPathAttribute("xs:simpleType/xs:list/xs:restriction/@base", null);
			type = StringUtil.token(type, -1, JDFConstants.COLON);
			if ("NMTOKEN".equals(type))
			{
				type += "S";
			}
			else
			{
				type = e.getXPathAttribute("xs:simpleType/xs:list/xs:simpleType/xs:restriction/@base", null);
				type = StringUtil.token(type, -1, JDFConstants.COLON);
				if ("NMTOKEN".equals(type))
				{
					type += "S";
				}
				else
				{
					type = "NMTOKENS";
				}
			}
		}
		type = StringUtil.token(type, -1, JDFConstants.COLON);
		return type;
	}

	/**
	 *
	 */
	public JSONWriter()
	{
		super();
		prepWalker = null;
		wantArray = false;
		learnArrays = true;
		typeSafe = true;
		arrayNames = new HashSet<>();
		alwaysString = new HashSet<>();
		mixedElements = new HashSet<>();
		stringArray = new HashSet<>();
		transferFunction = new HashSet<>();
		skipPool = new HashSet<>();
		keyCase = valueCase = eJSONCase.retain;
		mixedText = TEXT;
		prefix = eJSONPrefix.retain;
		jsonRoot = eJSONRoot.retain;
	}

	/**
	 * @param e
	 * @return
	 */
	public JSONObject convert(final KElement e0)
	{
		final JSONObject j = new JSONObject();
		KElement e = e0;
		if (prepWalker != null)
		{
			e = e0.cloneNewDoc();
			prepWalker.walkTree(e, null);
		}
		walk(e, j);
		final JSONObject j2 = updateRoot(j);

		setRoot(j2);
		return j2;
	}

	JSONObject updateRoot(final JSONObject j)
	{
		if (!eJSONRoot.retain.equals(getJsonRoot()))
		{
			final Set keys = j.keySet();
			if (ContainerUtil.size(keys) == 1)
			{
				final String key = (String) keys.iterator().next();
				final JSONObject first = (JSONObject) j.get(key);
				if (eJSONRoot.schema.equals(getJsonRoot()))
				{
					first.put(SCHEMA, key);
				}
				return first;
			}
			else
			{
				log.warn("Not modifying multi-root object");
			}
		}
		return j;

	}

	/**
	 * @param e
	 * @return
	 */
	public JSONObject convertMap(final JDFAttributeMap map)
	{
		final JSONObject j = createJSonFromAttributes(map);
		setRoot(j);
		return j;
	}

	/**
	 * @param e
	 * @return
	 */
	public String getString(final KElement e)
	{
		final JSONObject o = convert(e);
		if (o == null)
		{
			return null;
		}
		return o.toJSONString();
	}

	/**
	 * @param e
	 * @return
	 */
	public InputStream getStream(final KElement e)
	{
		final String s = getString(e);
		final byte[] b = StringUtil.getUTF8Bytes(s);
		return b == null ? null : new ByteArrayIOStream(b).getInputStream();
	}

	/**
	 * @param e
	 * @param parent
	 * @return
	 */
	public boolean walk(final KElement e, final JSONAware parent)
	{
		final String nodeName = getNodeName(e);
		final JDFAttributeMap map = getAttributes(e);
		boolean hasContent = false;
		hasContent = !JDFAttributeMap.isEmpty(map);
		String txt = StringUtil.normalize(e.getText(), false);
		if (mixedText != null && txt != null && mixedElements.contains(getCheckName(e)))
		{
			map.put(mixedText, txt);
			txt = null;
		}
		final JSONObject me = createJSonFromAttributes(map);
		final boolean hasChildren = processChildren(e, me);
		if (txt != null)
		{
			if (hasContent || hasChildren)
			{
				final JSONArray a = new JSONArray();
				a.add(me);
				a.add(getObjectFromVal(nodeName, txt));
				addToParent(parent, nodeName, a);
			}
			else
			{
				addToParent(parent, nodeName, getObjectFromVal(nodeName, txt));
			}
		}
		else
		{
			addToParent(parent, nodeName, me);
		}
		return true;
	}

	public String getCheckName(final KElement e)
	{
		return StringUtil.normalize(e.getLocalName(), true, "_ -");
	}

	String getNodeName(final KElement e)
	{
		if (eJSONPrefix.underscore.equals(prefix))
		{
			return StringUtil.replaceChar(e.getNodeName(), ':', JDFConstants.UNDERSCORE, 0);
		}
		else if (eJSONPrefix.none.equals(prefix))
		{
			return e.getLocalName();
		}
		return e.getNodeName();
	}

	public JSONObject createJSonFromAttributes(final JDFAttributeMap map)
	{
		final JSONObject me = new JSONObject();
		if (!JDFAttributeMap.isEmpty(map))
		{
			for (final Entry<String, String> entry : map.entrySet())
			{
				final String val = entry.getValue();
				final String key = entry.getKey();
				final Object jVal = getObjectFromVal(key, val);
				putNonEmpty(me, key, jVal);
			}
		}
		return me;
	}

	String getKey(String key, final eJSONCase jCase)
	{
		final String token = StringUtil.token(key, 0, ":");
		if (JDFConstants.XMLNS.equals(token) || JDFConstants.XSI.equals(token))
		{
			return null;
		}
		if (eJSONPrefix.none.equals(prefix))
		{
			final String token2 = StringUtil.token(key, 1, ":");
			if (token2 != null)
			{
				key = token2;
			}
		}
		else if (eJSONPrefix.underscore.equals(prefix))
		{
			key = StringUtil.replaceChar(key, ':', JDFConstants.UNDERSCORE, 0);
		}

		return updateCase(key, jCase);
	}

	String updateCase(final String key, final eJSONCase jCase)
	{
		if (eJSONCase.lowerfirst.equals(jCase))
		{
			return key.substring(0, 1).toLowerCase() + key.substring(1);
		}
		else if (eJSONCase.lower.equals(jCase))
		{
			return key.toLowerCase();
		}
		else if (eJSONCase.upper.equals(jCase))
		{
			return key.toUpperCase();
		}
		else
		{
			return key;
		}
	}

	Object getObjectFromVal(final String key, final String val)
	{
		if (typeSafe && isTypesafeKey(key))
		{
			if (isArrayKey(key))
			{
				final StringArray a = StringArray.getVString(val, null);
				if (a != null)
				{
					final JSONArray ar = new JSONArray();
					for (final String s : a)
					{
						final String k2 = getKey(s, valueCase);
						if (k2 != null)
						{
							ar.add(k2);
						}
					}
					return ar;
				}
			}
			else if (StringUtil.isNumber(val))
			{
				if (StringUtil.isInteger(val))
				{
					return Integer.valueOf(StringUtil.parseInt(val, 0));
				}
				else if (StringUtil.isLong(val))
				{
					return Long.valueOf(StringUtil.parseLong(val, 0));
				}
				else
				{
					return Double.valueOf(StringUtil.parseDouble(val, 0));
				}
			}
			else if (StringUtil.isBoolean(val))
			{
				return Boolean.valueOf(val);
			}
			else if (isTransferCurve(key))
			{
				return getTransferCurve(val);
			}
			else if (JDFNumberList.createNumberList(val) != null)
			{
				return getNumListArray(val);
			}
			if (addString(key))
			{
				log.info("found new string type: " + key);
			}
		}
		return updateCase(StringUtil.getNonEmpty(val), valueCase);
	}

	JSONArray getTransferCurve(final String val)
	{
		final JDFNumberList nl = JDFNumberList.createNumberList(val);
		if (nl == null)
		{
			return null;
		}
		final JSONArray a0 = new JSONArray();
		JSONArray a = null;
		for (int i = 0; i < nl.size(); i++)
		{
			if (i % 2 == 0)
			{
				a = new JSONArray();
			}
			final double d = nl.doubleAt(i);
			addNumber(a, d);
			if (i % 2 == 1)
			{
				a0.add(a);
			}

		}
		return a0;
	}

	private void addNumber(final JSONArray a, final double d)
	{
		if ((int) d == d)
		{
			a.add(Integer.valueOf((int) d));
		}
		else
		{
			a.add(Double.valueOf(d));
		}
	}

	boolean isTransferCurve(final String key)
	{
		final String normalized = StringUtil.normalize(key, true, "_ -");
		return normalized != null && transferFunction.contains(normalized);
	}

	boolean isTypesafeKey(final String key)
	{
		final String normalized = StringUtil.normalize(key, true, "_ -");
		return normalized != null && !alwaysString.contains(normalized);
	}

	boolean isArrayKey(final String key)
	{
		final String normalized = StringUtil.normalize(key, true, "_ -");
		return normalized != null && stringArray.contains(normalized);
	}

	boolean isSkipKey(final String key)
	{
		final String normalized = StringUtil.normalize(key, true, "_ -");
		return normalized != null && skipPool.contains(normalized);
	}

	JSONArray getNumListArray(final String val)
	{
		final JDFNumberList nl = JDFNumberList.createNumberList(val);
		final JSONArray a = new JSONArray();
		for (int i = 0; i < nl.size(); i++)
		{
			final double d = nl.doubleAt(i);
			addNumber(a, d);
		}
		return a;
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
		final boolean isArray = isArray(parentName);
		final JSONArray parentArray = new JSONArray();
		final Collection<KElement> v = parentElem.getChildArray(null, null);
		if (!ContainerUtil.isEmpty(v))
		{
			final HashSet<String> processedNames = new HashSet<>();
			for (final KElement e : v)
			{
				if (isSkipKey(getNodeName(e)))
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

	boolean isArray(final String nodeName)
	{
		return wantArray || arrayNames.contains(nodeName);
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

		if (skipPool.contains(key))
		{
			return null;
		}
		else if (wantArray || arrayNames.contains(key))
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
			else if (n > 1)
			{
				if (addArray(childName))
				{
					log.info("found new array type: " + childName);
				}
				return new JSONArray();
			}
		}
		return null;
	}

	/**
	 * @param parent
	 * @param nodeName
	 * @param me
	 */
	public static void addToParentRaw(final JSONAware parent, final String nodeName, final Object me)
	{
		if (parent instanceof JSONArray)
		{
			((JSONArray) parent).add(me);
		}
		else if (parent instanceof JSONObject)
		{
			((JSONObject) parent).put(nodeName, me);
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

	Object putNonEmpty(final JSONAware parent, final String nodeName, final Object obj)
	{
		final String key = obj == null ? null : getKey(nodeName, keyCase);
		return key == null ? null : ((JSONObject) parent).put(key, obj);
	}

	/**
	 * @param e never null
	 */
	JDFAttributeMap getAttributes(final KElement e)
	{
		final JDFAttributeMap atts = e.getAttributeMap();
		atts.remove(AttributeName.XSITYPE);
		final List<String> keyList = ContainerUtil.getKeyList(atts);
		if (keyList != null)
		{
			for (final String key : keyList)
			{
				if (AttributeName.XMLNS.equalsIgnoreCase(StringUtil.token(key, 0, JDFConstants.COLON)))
				{
					atts.remove(key);
				}
			}
		}
		return atts;
	}

	public void clearArray()
	{
		arrayNames.clear();
	}

	public boolean removeArray(final String arg0)
	{
		return arrayNames.remove(arg0);
	}

	public Set<String> getArrayNames()
	{
		return arrayNames;
	}

	@Override
	public String toString()
	{
		return "JSONWriter [wantArray=" + wantArray + ", learnArrays=" + learnArrays + " keyCase=" + keyCase + " valueCase=" + valueCase + ", typeSafe=" + isTypeSafe()
				+ ", arrayNames=" + arrayNames + "]";
	}

	public eJSONCase getKeyCase()
	{
		return keyCase;
	}

	public void setKeyCase(final eJSONCase keyCase)
	{
		this.keyCase = keyCase;
	}

	public eJSONCase getValueCase()
	{
		return valueCase;
	}

	public void setValueCase(final eJSONCase valueCase)
	{
		this.valueCase = valueCase;
	}

	/**
	 * @return the mixedText
	 */
	String getMixedText()
	{
		return mixedText;
	}

	/**
	 * @param mixedText the mixedText to set
	 */
	public void setMixedText(final String mixedText)
	{
		this.mixedText = mixedText;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((alwaysString == null) ? 0 : alwaysString.hashCode());
		result = prime * result + ((arrayNames == null) ? 0 : arrayNames.hashCode());
		result = prime * result + ((keyCase == null) ? 0 : keyCase.hashCode());
		result = prime * result + (learnArrays ? 1231 : 1237);
		result = prime * result + ((mixedElements == null) ? 0 : mixedElements.hashCode());
		result = prime * result + ((mixedText == null) ? 0 : mixedText.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((skipPool == null) ? 0 : skipPool.hashCode());
		result = prime * result + ((stringArray == null) ? 0 : stringArray.hashCode());
		result = prime * result + ((transferFunction == null) ? 0 : transferFunction.hashCode());
		result = prime * result + (typeSafe ? 1231 : 1237);
		result = prime * result + ((valueCase == null) ? 0 : valueCase.hashCode());
		result = prime * result + (wantArray ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final JSONWriter other = (JSONWriter) obj;
		if (alwaysString == null)
		{
			if (other.alwaysString != null)
				return false;
		}
		else if (!alwaysString.equals(other.alwaysString))
			return false;
		if (arrayNames == null)
		{
			if (other.arrayNames != null)
				return false;
		}
		else if (!arrayNames.equals(other.arrayNames))
			return false;
		if (keyCase != other.keyCase)
			return false;
		if (learnArrays != other.learnArrays)
			return false;
		if (mixedElements == null)
		{
			if (other.mixedElements != null)
				return false;
		}
		else if (!mixedElements.equals(other.mixedElements))
			return false;
		if (mixedText == null)
		{
			if (other.mixedText != null)
				return false;
		}
		else if (!mixedText.equals(other.mixedText))
			return false;
		if (prefix != other.prefix)
			return false;
		if (skipPool == null)
		{
			if (other.skipPool != null)
				return false;
		}
		else if (!skipPool.equals(other.skipPool))
			return false;
		if (stringArray == null)
		{
			if (other.stringArray != null)
				return false;
		}
		else if (!stringArray.equals(other.stringArray))
			return false;
		if (transferFunction == null)
		{
			if (other.transferFunction != null)
				return false;
		}
		else if (!transferFunction.equals(other.transferFunction))
			return false;
		if (typeSafe != other.typeSafe)
			return false;
		if (valueCase != other.valueCase)
			return false;
		if (wantArray != other.wantArray)
			return false;
		return true;
	}

}
