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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFConstants;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.core.StringArray;
import org.cip4.jdflib.core.VElement;
import org.cip4.jdflib.core.XMLDoc;
import org.cip4.jdflib.datatypes.JDFAttributeMap;
import org.cip4.jdflib.datatypes.JDFNumberList;
import org.cip4.jdflib.elementwalker.ElementWalker;
import org.cip4.jdflib.jmf.JDFMessage.EnumFamily;
import org.cip4.jdflib.util.ByteArrayIOStream;
import org.cip4.jdflib.util.ContainerUtil;
import org.cip4.jdflib.util.ListMap;
import org.cip4.jdflib.util.StringUtil;
import org.cip4.jdflib.util.UrlPart;
import org.cip4.jdflib.util.UrlUtil;
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
	private static final String XJDF_SCHEMA_URL = "http://schema.cip4.org/jdfschema_2_1/xjdf.xsd";
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
		setXJDF(false, false);
	}

	/**
	 * apply standard xjdf settings
	 * 
	 * @param splitXJMF TODO
	 * @param explicitAudit TODO
	 */
	public void setXJDF(boolean splitXJMF, boolean explicitAudit)
	{
		final JSONPrepWalker jsonPrepWalker = new JSONPrepWalker();
		jsonPrepWalker.setExplicitAudit(false);
		jsonPrepWalker.setSplitXJMF(splitXJMF);
		this.prepWalker = jsonPrepWalker;
		setPrefix(eJSONPrefix.context);
		setKeyCase(eJSONCase.retain);
		setValueCase(eJSONCase.retain);
		setMixedText(TEXT);
		addMixed(ElementName.COMMENT);
		UrlPart part = UrlUtil.writerToURL(XJDF_SCHEMA_URL, null, UrlUtil.GET, null, null);
		if (UrlPart.isReturnCodeOK(part))
		{
			XMLDoc schema = part.getXMLDoc();
			if (schema != null)
			{
				KElement root = schema.getRoot();
				new SchemaFiller(root, splitXJMF).fillTypesFromSchema();
			}
		}
		addArray(ElementName.AUDITPOOL);
		addArray(ElementName.MEDIALAYERS);
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

	String mixedText;

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
		retain, context, underscore, none;

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
	final Set<String> knownAtts;
	final Set<String> knownElems;
	final Set<String> stringArray;
	final Set<String> numbers;
	final Set<String> numList;
	final Set<String> bool;
	final Set<String> skipPool;
	final Set<String> transferFunction;
	private eJSONRoot rootType;

	public eJSONRoot getJsonRoot()
	{
		return rootType;
	}

	public void setJsonRoot(final eJSONRoot jsonRoot)
	{
		this.rootType = jsonRoot;
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
		return addList(element, arrayNames);
	}

	public boolean addSkipPool(final String element)
	{
		return addList(element, skipPool);
	}

	public boolean addString(final String attribute)
	{
		final String key = StringUtil.normalize(attribute, true, "_ -");
		if (knownAtts.contains(key) || knownElems.contains(key))
			return false;
		return addList(attribute, alwaysString);
	}

	public boolean addMixed(final String element)
	{
		return addList(element, mixedElements);
	}

	public boolean addStringArray(final String attribute)
	{
		return addList(attribute, stringArray);
	}

	class SchemaFiller
	{
		private KElement schema;
		private boolean splitXJMF;
		final Collection<KElement> ve;
		final ListMap<String, KElement> nameMap;

		/**
		 * @param schema
		 */
		SchemaFiller(final KElement schema, boolean splitXJMF)
		{
			this.schema = schema;
			this.splitXJMF = splitXJMF;
			ve = schema == null ? null : schema.getChildrenByTagName("element", XML_SCHEMA_NS, null, false, true, 0);
			nameMap = new ListMap<>();
			nameMap.setUnique(true);
			if (ve != null)
			{
				for (final KElement e : ve)
				{
					String name = e.getNonEmpty("name");
					if (name != null)
						nameMap.putOne(name, e);
					String sg = e.getNonEmpty("substitutionGroup");
					if (sg != null)
						nameMap.putOne(sg, e);
				}
			}
		}

		/**
		 * @param schema
		 * @param splitXJMF TODO
		 */
		public void fillTypesFromSchema()
		{

			final Set<String> types = new HashSet<>();
			types.addAll(new StringArray(new String[] { "float", "double", "int", "integer", "long", "boolean", "CMYKColor", "FloatList", "IntegerList", "IntegerRange", "LabColor",
					"matrix", "rectangle", "shape", "sRGBColor", "XYPair", "TransferFunction" }));
			if (ve != null)
			{
				for (final KElement e : ve)
				{
					fillTypeFromSchema(e);
					if ("string".equals(getTypeFromSchemaAttribute(e)))
					{
						fillAttributeFromSchema(e, types);
					}
				}
			}

			final Collection<KElement> va = schema == null ? null : schema.getChildrenByTagName("attribute", XML_SCHEMA_NS, null, false, true, 0);
			if (va != null)
			{
				for (final KElement e : va)
				{
					String name = fillAttributeFromSchema(e, types);
					knownAtts.add(name);
				}
			}
		}

		String fillAttributeFromSchema(final KElement e, final Set<String> types)
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
			else if ("float".equals(type) || "double".equals(type) || "int".equals(type) || "integer".equals(type) || "long".equals(type))
			{
				addList(name, numbers);
			}
			else if ("CMYKColor".equals(type) || "FloatList".equals(type) || "IntegerList".equals(type) || "IntegerRange".equals(type) || "LabColor".equals(type)
					|| "matrix".equals(type) || "rectangle".equals(type) || "shape".equals(type) || "sRGBColor".equals(type) || "XYPair".equals(type))
			{
				addList(name, numList);
			}
			else if (!types.contains(type))
			{
				addString(name);
			}
			return name;
		}

		void fillTypeFromSchema(final KElement e)
		{
			final String maxOcc = e.getNonEmpty("maxOccurs");
			boolean isMulti = "unbounded".equals(maxOcc) || StringUtil.parseInt(maxOcc, 1) > 1;
			if (!isMulti)
			{
				KElement parentSeq = e.getDeepParent("sequence", 0);
				KElement parentElement = e.getParentNode_KElement().getDeepParent("element", 0);
				if (parentSeq != null && !parentSeq.isAncestor(parentElement))
				{
					final String maxOccSeq = parentSeq.getNonEmpty("maxOccurs");
					isMulti = "unbounded".equals(maxOccSeq) || StringUtil.parseInt(maxOccSeq, 1) > 1;
				}
			}
			if (splitXJMF && isMulti && e.hasAttribute("substitutionGroup"))
			{
				isMulti = EnumFamily.getEnum(e.getAttribute("substitutionGroup")) == null;
			}
			if (isMulti)
			{
				fillArrayFromSchema(e);
			}
			for (String name : getNamesFromSchema(e))
				addList(name, knownElems);

			final String type = getTypeFromSchemaAttribute(e);

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

		void fillArrayFromSchema(final KElement e)
		{
			final List<String> keys = getNamesFromSchema(e);
			for (String key : keys)
				addArray(key);
		}

		List<String> getNamesFromSchema(final KElement e)
		{
			List<String> names = getNamesFromRef(e);
			if (StringUtil.isEmpty(names))
				names = new StringArray(e.getNonEmpty("name"));

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
			{
				List<String> contentNames = getNamesFromRef(parentContent);
				contentName = ContainerUtil.get(contentNames, 0);
			}
			if (!StringUtil.isEmpty(contentName))
			{
				for (int i = 0; i < names.size(); i++)
				{
					names.set(i, contentName + JDFConstants.SLASH + names.get(i));
				}
			}
			return names;
		}

		protected List<String> getNamesFromRef(final KElement e)
		{
			String nonEmpty = e.getNonEmpty("ref");
			return getNamesFromRef(nonEmpty);
		}

		protected List<String> getNamesFromRef(String nonEmpty)
		{
			if (nonEmpty == null)
				return null;
			StringArray ret = new StringArray();
			List<KElement> vv = nameMap.get(nonEmpty);
			for (KElement e2 : vv)
			{
				if (nonEmpty.equals(e2.getNonEmpty("name")))
				{
					if (!StringUtil.parseBoolean("abstract", false))
						ret.add(nonEmpty);
				}
				else if (nonEmpty.equals(e2.getNonEmpty("substitutionGroup")))
				{
					ContainerUtil.addAll(ret, getNamesFromRef(e2.getNonEmpty("name")));
				}
			}
			return ret;
		}
	}

	public boolean addTransferFunction(final String name)
	{
		return addList(name, transferFunction);
	}

	public boolean addList(final String name, Set<String> list)
	{
		final String key = StringUtil.normalize(name, true, "_ -");
		if (key != null)
		{
			boolean add = list.add(key);
			return add;
		}
		return false;
	}

	String getTypeFromSchemaAttribute(final KElement e)
	{
		boolean abst = StringUtil.parseBoolean(e.getAttribute("abstract"), false);
		if (abst)
			return null;
		String type = e.getAttribute("type");
		if (StringUtil.isEmpty(type))
		{
			type = e.getXPathAttribute("xs:restriction/@base", null);
		}
		if (StringUtil.isEmpty(type))
		{
			type = e.getXPathAttribute("xs:simpleType/xs:restriction/@base", null);
		}
		if (StringUtil.isEmpty(type))
		{
			type = e.getXPathAttribute("xs:complexType/xs:simpleContent/xs:extension/@base", null);
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
		numbers = new HashSet<>();
		bool = new HashSet<>();
		numList = new HashSet<>();
		transferFunction = new HashSet<>();
		skipPool = new HashSet<>();
		knownAtts = new HashSet<>();
		knownElems = new HashSet<>();
		keyCase = valueCase = eJSONCase.retain;
		mixedText = TEXT;
		prefix = eJSONPrefix.retain;
		rootType = eJSONRoot.retain;
	}

	/**
	 * @param e
	 * @return
	 */
	public List<JSONObject> splitConvert(final KElement e0)
	{
		List<KElement> l;
		List<JSONObject> ret = new ArrayList<>();
		if (prepWalker instanceof JSONPrepWalker)
		{
			l = ((JSONPrepWalker) prepWalker).split(e0);
		}
		else
		{
			l = new VElement();
			ContainerUtil.add(l, e0);
		}
		for (KElement e : l)
		{
			JSONObject j = convert(e);
			ContainerUtil.add(ret, j);
		}
		return ret;
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

	public String getCheckName(final KElement e)
	{
		return StringUtil.normalize(e.getLocalName(), true, "_ -");
	}

	public JSONObject createJSonFromAttributes(final JDFAttributeMap map)
	{
		JSONRootWalker jsonRootWalker = new JSONRootWalker(this, null);
		jsonRootWalker.createJSonFromAttributes(map);
		return jsonRootWalker.getRoot();
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
			final String normalized = StringUtil.normalize(key, true, "_ -");

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
			else if (numList.contains(normalized) && JDFNumberList.createNumberList(val) != null)
			{
				return getNumListArray(val);
			}
			else if (numbers.contains(normalized) && StringUtil.isNumber(val))
			{
				return getNumber(val);
			}
			else if (bool.contains(normalized) && StringUtil.isBoolean(val))
			{
				return Boolean.valueOf(StringUtil.parseBoolean(val, true));
			}
			else if (isTransferCurve(key))
			{
				return getTransferCurve(val);
			}
			else if (StringUtil.isNumber(val))
			{
				numbers.add(normalized);
				return getNumber(val);
			}
			else if (StringUtil.isBoolean(val))
			{
				bool.add(normalized);
				return Boolean.valueOf(val);
			}
			else if (JDFNumberList.createNumberList(val) != null)
			{
				numbers.remove(normalized);
				numList.add(normalized);
				return getNumListArray(val);
			}
			if (addString(key))
			{
				numbers.remove(normalized);
				bool.remove(normalized);
				numList.remove(normalized);
				log.info("found new string type: " + key);
			}
		}
		return updateCase(StringUtil.getNonEmpty(val), valueCase);
	}

	protected Object getNumber(final String val)
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

	public boolean isTypesafeKey(final String key)
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
		int size = ContainerUtil.size(nl);
		for (int i = 0; i < size; i++)
		{
			final double d = nl.doubleAt(i);
			addNumber(a, d);
		}
		return a;
	}

	boolean isArray(final String nodeName)
	{
		return wantArray || arrayNames.contains(nodeName);
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

	/**
	 * @deprecated use th 2 parameter version
	 * @param xjdfSchemaElement
	 */
	@Deprecated
	public void fillTypesFromSchema(KElement xjdfSchemaElement)
	{
		new SchemaFiller(xjdfSchemaElement, false).fillTypesFromSchema();
	}

	/**
	 * 
	 * @param xjdfSchemaElement
	 * @param splitXJMF if true, xjmf messages are singular
	 */
	public void fillTypesFromSchema(KElement xjdfSchemaElement, boolean splitXJMF)
	{
		new SchemaFiller(xjdfSchemaElement, splitXJMF).fillTypesFromSchema();
	}

	public JSONObject convert(KElement xjdf)
	{
		return convertHelper(xjdf).getRoot();
	}

	public JSONObjHelper convertHelper(KElement xjdf)
	{
		JSONRootWalker jsonRootWalker = new JSONRootWalker(this, xjdf);
		jsonRootWalker.convert();
		return jsonRootWalker;
	}

	/**
	 * @param e
	 * @param parent
	 * @return
	 * @deprecated use convert
	 */
	@Deprecated
	public boolean walk(final KElement e, final JSONAware parent)
	{
		JSONObject o = convert(e);
		setRoot(o);
		return true;
	}

}
