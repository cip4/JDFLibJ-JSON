/*
 * The CIP4 Software License, Version 1.0
 *
 *
 * Copyright (c) 2001-2023 The International Cooperation for the Integration of Processes in Prepress, Press and Postpress (CIP4). All rights reserved.
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.JDFConstants;
import org.cip4.jdflib.core.JDFElement.EnumVersion;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.core.StringArray;
import org.cip4.jdflib.core.VElement;
import org.cip4.jdflib.core.XMLDoc;
import org.cip4.jdflib.datatypes.JDFAttributeMap;
import org.cip4.jdflib.datatypes.JDFNumberList;
import org.cip4.jdflib.elementwalker.ElementWalker;
import org.cip4.jdflib.extensions.XJDF20;
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
	private static final String XJDF_SCHEMA_Base = "http://schema.cip4.org/jdfschema_2_";
	private static final String XJDF_SCHEMA_XSD = "/xjdf.xsd";
	boolean wantArray;
	boolean learnArrays;
	boolean typeSafe;
	ElementWalker prepWalker;
	private static Map<EnumVersion, String> schemaCache = new HashMap<>();

	public static void setSchemaUrl(final EnumVersion v, final String cachePath)
	{
		if (v != null && cachePath != null)
		{
			schemaCache.put(v, cachePath);
		}
	}

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
		setXJDF(true, false);
	}

	/**
	 * apply standard xjdf settings
	 * 
	 * @param splitXJMF TODO
	 * @param explicitAudit TODO
	 */
	public void setXJDF(final boolean splitXJMF, final boolean explicitAudit)
	{
		setXJDF(splitXJMF, explicitAudit, null);
	}

	/**
	 * apply standard xjdf settings
	 * 
	 * @param splitXJMF TODO
	 * @param explicitAudit TODO
	 */
	public void setXJDF(final boolean splitXJMF, final boolean explicitAudit, EnumVersion version)
	{
		if (version == null)
			version = XJDF20.getDefaultVersion();
		final JSONPrepWalker jsonPrepWalker = new JSONPrepWalker();
		jsonPrepWalker.setExplicitAudit(false);
		jsonPrepWalker.setSplitXJMF(splitXJMF);
		this.prepWalker = jsonPrepWalker;
		setPrefix(eJSONPrefix.context);
		setKeyCase(eJSONCase.retain);
		setValueCase(eJSONCase.retain);
		setMixedText(TEXT);
		setJsonRoot(eJSONRoot.xmlname);
		addMixed(ElementName.COMMENT);
		String schemaURL = getSchemaURL(version, true);
		if (schemaURL == null)
			schemaURL = getSchemaURL(version, false);
		final UrlPart part = UrlUtil.writerToURL(schemaURL, null, UrlUtil.GET, null, null);
		XMLDoc schema = UrlPart.isReturnCodeOK(part) ? part.getXMLDoc() : null;
		if (schema == null)
		{
			schema = XMLDoc.parseStream(getClass().getResourceAsStream(XJDF_SCHEMA_XSD));
		}
		if (schema != null)
		{
			final KElement root = schema.getRoot();
			new SchemaFiller(root, splitXJMF).fillTypesFromSchema();
		}

		addArray(ElementName.AUDITPOOL);
		addArray(ElementName.MEDIALAYERS);
	}

	String getSchemaURL(final EnumVersion version, final boolean local)
	{
		final int minor = version.getMinorVersion();
		if (local)
		{
			final String localUrl = schemaCache.get(version);
			if (localUrl == null)
			{
				final URL url = ClassLoader.getSystemResource("/schema/Version2_" + minor + XJDF_SCHEMA_XSD);
				return UrlUtil.urlToString(url);
			}
			else
			{
				return localUrl;
			}
		}
		else
		{
			final String sMinor = (minor >= ((EnumVersion) ContainerUtil.get(EnumVersion.getEnumList(), -1)).getMinorVersion() ? "x" : "" + minor);
			return XJDF_SCHEMA_Base + sMinor + XJDF_SCHEMA_XSD;
		}
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
		retain, none, schema, xmlname;

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
	static final String SCHEMA = "$schema";

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

	/**
	 * @param element
	 * @return
	 */
	public boolean addKnownElem(final String element)
	{
		return addList(element, knownElems);
	}

	/**
	 * @param element
	 * @return
	 */
	public boolean addKnownAttribute(final String element)
	{
		return addList(element, knownAtts);
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
		if (!StringUtil.isEmpty(mixedText) && !StringUtil.isEmpty(element))
		{
			addList(element + JDFConstants.SLASH + mixedText, alwaysString);
		}
		return addList(element, mixedElements);
	}

	public boolean addStringArray(final String attribute)
	{
		alwaysString.remove(attribute);
		return addList(attribute, stringArray);
	}

	class SchemaFiller
	{
		private static final String SEQUENCE = "sequence";
		private static final String UNBOUNDED = "unbounded";
		private static final String MAX_OCCURS = "maxOccurs";
		private static final String ELEMENT = "element";
		private static final String COMPLEX_TYPE = "complexType";
		private static final String REF = "ref";
		private static final String ABSTRACT = "abstract";
		private static final String NAME = "name";
		private static final String SUBSTITUTION_GROUP = "substitutionGroup";
		private final KElement schema;
		private final boolean splitXJMF;
		final Collection<KElement> ve;
		final ListMap<String, KElement> nameMap;

		/**
		 * @param schema
		 */
		SchemaFiller(final KElement schema, final boolean splitXJMF)
		{
			this.schema = schema;
			this.splitXJMF = splitXJMF;
			ve = schema == null ? null : schema.getChildrenByTagName(ELEMENT, XML_SCHEMA_NS, null, false, true, 0);
			nameMap = new ListMap<>();
			nameMap.setUnique(true);
			if (ve != null)
			{
				for (final KElement e : ve)
				{
					final String name = e.getNonEmpty(NAME);
					if (name != null)
						nameMap.putOne(name, e);
					final String sg = e.getNonEmpty(SUBSTITUTION_GROUP);
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
					if ("textElement".equals(getTypeFromSchemaAttribute(e)))
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
					fillAttributeFromSchema(e, types);
				}
			}
		}

		void fillAttributeFromSchema(final KElement e, final Set<String> types)
		{
			final List<String> l = getNamesFromSchema(e);
			final String type = getTypeFromSchemaAttribute(e);
			final String name = e.getNonEmpty(NAME);
			ContainerUtil.appendUnique(l, name);
			for (final String complet : l)
			{
				final String normalize = StringUtil.normalize(complet, true, "_:-");
				addSingleAttribute(types, type, normalize);
			}
		}

		protected void addSingleAttribute(final Set<String> types, final String type, final String name)
		{
			if ("NMTOKENS".equals(type) || "IDREFS".equals(type))
			{
				addStringArray(name);
			}
			else if ("TransferFunction".equals(type))
			{
				addTransferFunction(name);
			}
			else if ("boolean".equals(type))
			{
				alwaysString.remove(name);
				addList(name, bool);
			}
			else if ("float".equals(type) || "double".equals(type) || "int".equals(type) || "integer".equals(type) || "long".equals(type))
			{
				alwaysString.remove(name);
				addList(name, numbers);
			}
			else if ("CMYKColor".equals(type) || "FloatList".equals(type) || "IntegerList".equals(type) || "IntegerRange".equals(type) || "LabColor".equals(type)
					|| "matrix".equals(type) || "rectangle".equals(type) || "shape".equals(type) || "sRGBColor".equals(type) || "XYPair".equals(type))
			{
				alwaysString.remove(name);
				addList(name, numList);
			}
			else if (!types.contains(type))
			{
				addString(name);
			}
			knownAtts.add(name);

		}

		void fillTypeFromSchema(final KElement e)
		{
			final String maxOcc = e.getNonEmpty(MAX_OCCURS);
			boolean isMulti = UNBOUNDED.equals(maxOcc) || StringUtil.parseInt(maxOcc, 1) > 1;
			if (!isMulti)
			{
				final KElement parentSeq = e.getDeepParent(SEQUENCE, 0);
				final KElement parentElement = e.getParentNode_KElement().getDeepParent(ELEMENT, 0);
				if (parentSeq != null && !parentSeq.isAncestor(parentElement))
				{
					final String maxOccSeq = parentSeq.getNonEmpty(MAX_OCCURS);
					isMulti = UNBOUNDED.equals(maxOccSeq) || StringUtil.parseInt(maxOccSeq, 1) > 1;
				}
			}
			if (!isMulti)
			{
				final KElement parentChoice = e.getDeepParent("choice", 0);
				final KElement parentElement = e.getParentNode_KElement().getDeepParent(ELEMENT, 0);
				if (parentChoice != null && !parentChoice.isAncestor(parentElement))
				{
					final String maxOccSeq = parentChoice.getNonEmpty(MAX_OCCURS);
					isMulti = UNBOUNDED.equals(maxOccSeq) || StringUtil.parseInt(maxOccSeq, 1) > 1;
				}

			}
			if (splitXJMF && isMulti && e.hasAttribute(SUBSTITUTION_GROUP))
			{
				isMulti = EnumFamily.getEnum(e.getAttribute(SUBSTITUTION_GROUP)) == null;
			}
			isMulti = isMulti && !(splitXJMF && ElementName.MESSAGE.equals(e.getAttribute(REF)));
			if (isMulti)
			{
				fillArrayFromSchema(e);
			}
			final List<String> namesFromSchema = getNamesFromSchema(e);
			for (final String name : namesFromSchema)
				addList(name, knownElems);

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
			for (final String key : keys)
				addArray(key);
		}

		List<String> getNamesFromSchema(final KElement e)
		{
			List<String> names = getNamesFromRef(e);
			if (StringUtil.isEmpty(names))
				names = new StringArray(e.getNonEmpty(NAME));

			KElement parentContent = getAncestor(e, COMPLEX_TYPE);
			String contentName = null;
			if (parentContent == null || parentContent.getNonEmpty(NAME) == null)
			{
				parentContent = getAncestor(e, ELEMENT);
				contentName = parentContent == null || parentContent.equals(e) ? null : parentContent.getNonEmpty(NAME);
			}
			else
			{
				contentName = parentContent.getNonEmpty(NAME);
			}
			if (parentContent != null && contentName == null)
			{
				final List<String> contentNames = getNamesFromRef(parentContent);
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
			final String nonEmpty = e.getNonEmpty(REF);
			return getNamesFromRef(nonEmpty);
		}

		protected List<String> getNamesFromSubstitution(final KElement e)
		{
			final String nonEmpty = e.getNonEmpty(SUBSTITUTION_GROUP);
			return getNamesFromRef(nonEmpty);
		}

		protected List<String> getNamesFromRef(final String nonEmpty)
		{
			if (nonEmpty == null)
				return null;
			final StringArray ret = new StringArray();
			final List<KElement> vv = nameMap.get(nonEmpty);
			for (final KElement e2 : vv)
			{
				if (nonEmpty.equals(e2.getNonEmpty(NAME)))
				{
					if (!StringUtil.parseBoolean(ABSTRACT, false))
						ret.add(nonEmpty);
				}
				else if (nonEmpty.equals(e2.getNonEmpty(SUBSTITUTION_GROUP)))
				{
					ContainerUtil.addAll(ret, getNamesFromRef(e2.getNonEmpty(NAME)));
				}
			}
			return ret;
		}
	}

	public boolean addTransferFunction(final String name)
	{
		alwaysString.remove(name);
		return addList(name, transferFunction);
	}

	public boolean addList(final String name, final Set<String> list)
	{
		final String key = StringUtil.normalize(name, true, "_ -");
		if (key != null)
		{
			final boolean add = list.add(key);
			return add;
		}
		return false;
	}

	String getTypeFromSchemaAttribute(final KElement e)
	{
		final boolean abst = StringUtil.parseBoolean(e.getAttribute("abstract"), false);
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
			if (StringUtil.isEmpty(type))
			{
				type = e.getXPathAttribute("xs:complexType/xs:simpleContent/xs:extension/@base", null);
			}
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
		final List<JSONObject> ret = new ArrayList<>();
		if (prepWalker instanceof JSONPrepWalker)
		{
			l = ((JSONPrepWalker) prepWalker).split(e0);
		}
		else
		{
			l = new VElement();
			ContainerUtil.add(l, e0);
		}
		for (final KElement e : l)
		{
			final JSONObject j = convert(e);
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
		final JSONRootWalker jsonRootWalker = new JSONRootWalker(this, null);
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
		if (typeSafe)
		{
			final String normalized = StringUtil.normalize(key, true, "_ -");
			final String normalized2 = StringUtil.token(normalized, 1, "/");
			if (isTypesafeKey(normalized) && isTypesafeKey(normalized2))
			{

				if (isArrayKey(key) || isArrayKey(normalized2))
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
				else if ((numList.contains(normalized) || numList.contains(normalized2)) && !numbers.contains(normalized) && JDFNumberList.createNumberList(val) != null)
				{
					return getNumListArray(val);
				}
				else if ((numbers.contains(normalized) || numbers.contains(normalized2)) && StringUtil.isNumber(val))
				{
					return getNumber(val);
				}
				else if ((bool.contains(normalized) || bool.contains(normalized2)) && StringUtil.isBoolean(val))
				{
					return Boolean.valueOf(StringUtil.parseBoolean(val, true));
				}
				else if (isTransferCurve(key) || isTransferCurve(normalized2))
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
		return normalized == null || !alwaysString.contains(normalized);
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
		final int size = ContainerUtil.size(nl);
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
	public void fillTypesFromSchema(final KElement xjdfSchemaElement)
	{
		new SchemaFiller(xjdfSchemaElement, false).fillTypesFromSchema();
	}

	/**
	 * 
	 * @param xjdfSchemaElement
	 * @param splitXJMF if true, xjmf messages are singular
	 */
	public void fillTypesFromSchema(final KElement xjdfSchemaElement, final boolean splitXJMF)
	{
		new SchemaFiller(xjdfSchemaElement, splitXJMF).fillTypesFromSchema();
	}

	public JSONObject convert(final KElement xjdf)
	{
		return convertHelper(xjdf).getRoot();
	}

	public JSONObjHelper convertHelper(final KElement xjdf)
	{
		final JSONRootWalker jsonRootWalker = new JSONRootWalker(this, xjdf);
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
		final JSONObject o = convert(e);
		setRoot(o);
		return true;
	}

}
