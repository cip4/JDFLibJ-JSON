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
 * (C) 2020 Heidelberger Druckmaschinen AG
 */
package org.cip4.lib.jdf.jsonutil;

import java.util.List;

import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.core.StringArray;
import org.cip4.jdflib.core.VElement;
import org.cip4.jdflib.core.VString;
import org.cip4.jdflib.datatypes.JDFAttributeMap;
import org.cip4.jdflib.elementwalker.BaseElementWalker;
import org.cip4.jdflib.elementwalker.BaseWalker;
import org.cip4.jdflib.elementwalker.BaseWalkerFactory;
import org.cip4.jdflib.extensions.MessageHelper;
import org.cip4.jdflib.extensions.XJDFConstants;
import org.cip4.jdflib.extensions.XJMFHelper;
import org.cip4.jdflib.util.ContainerUtil;

/**
 * @author rainer prosi class to preprocess xjdf or printTalk for json conversion
 */
public class JSONPrepWalker extends BaseElementWalker
{

	private boolean splitXJMF;

	/**
	 *
	 */
	public JSONPrepWalker()
	{
		super(new BaseWalkerFactory());
		explicitAudit = false;
		setSplitXJMF(false);
	}

	private boolean explicitAudit;

	/**
	 * @return the explicitAudit
	 */
	public boolean isExplicitAudit()
	{
		return explicitAudit;
	}

	public List<KElement> splitXML(final KElement input)
	{
		final List<KElement> split = split(input);
		for (final KElement e : split)
		{
			walkTree(e, null);
		}
		return split;
	}

	public List<KElement> split(final KElement input)
	{
		final XJMFHelper xh = splitXJMF ? XJMFHelper.getHelper(input) : null;
		if (xh != null)
		{
			final List<KElement> l = splitXJMF(xh);
			return l;
		}

		final VElement v = new VElement();
		ContainerUtil.add(v, input);
		return v;
	}

	List<KElement> splitXJMF(final XJMFHelper xh)
	{
		final List<MessageHelper> mhs = xh.getMessageHelpers();
		final VElement v = new VElement();
		if (ContainerUtil.size(mhs) > 1)
		{
			final KElement header = xh.getHeader();
			header.removeAttribute(AttributeName.ID);
			final JDFAttributeMap xjmfMap = xh.getRoot().getAttributeMap();
			for (final MessageHelper mh : mhs)
			{
				final XJMFHelper h = new XJMFHelper();
				final KElement root = h.getRoot();
				root.removeChildren(XJDFConstants.Header, null);
				root.setAttributes(xjmfMap);
				root.copyElement(header, null);
				h.copyHelper(mh);
				h.cleanUp();
				v.add(root);
			}
		}
		else
		{
			ContainerUtil.add(v, xh.getRoot());
		}
		return v;
	}

	/**
	 * @param explicitAudit the explicitAudit to set
	 */
	public void setExplicitAudit(final boolean explicitAudit)
	{
		this.explicitAudit = explicitAudit;
	}

	/**
	 * the default is to simply stop walking and ignore these they may have been evaluated in a parent
	 */
	public class WalkElement extends BaseWalker
	{
		public WalkElement()
		{
			super(getFactory());
		}

		/**
		 * @param e
		 * @return the created resource
		 */
		@Override
		public KElement walk(final KElement e, final KElement trackElem)
		{
			return e;
		}

	}

	public class WalkAuditPool extends WalkElement
	{

		/**
		 *
		 */
		public WalkAuditPool()
		{
			super();
		}

		final private StringArray auditnames = StringArray.getVString("AuditCreated AuditNotification AuditProcessRun AuditResource AuditStatus", null);

		/**
		 * @param xjdf
		 * @return true if must continue
		 */
		@Override
		public KElement walk(final KElement e, final KElement xjdf)
		{
			final List<KElement> elems = e.getChildArray_KElement(null, null, null, true, 0);
			KElement parent = e.getParentNode_KElement();
			if (parent == null)
			{
				parent = e;
			}
			for (final KElement elem : elems)
			{
				if (auditnames.contains(elem.getLocalName()))
				{
					parent.moveElement(elem, e);
					walkTree(elem, xjdf);
				}
			}
			e.deleteNode();
			return null;
		}

		/**
		 * @see org.cip4.jdflib.elementwalker.BaseWalker#getElementNames()
		 */
		@Override
		public VString getElementNames()
		{
			return new VString(ElementName.AUDITPOOL);
		}

		/**
		 * @see org.cip4.jdflib.elementwalker.BaseWalker#matches(org.cip4.jdflib.core.KElement)
		 */
		@Override
		public boolean matches(final KElement e)
		{
			return !explicitAudit;
		}
	}

	/**
	 * the default is to simply stop walking and ignore these they may have been evaluated in a parent
	 */
	public class WalkAudit extends WalkElement
	{
		public WalkAudit()
		{
			super();
		}

		/**
		 * @param e
		 * @return the created resource
		 */
		@Override
		public KElement walk(final KElement e, final KElement trackElem)
		{
			e.setAttribute(AttributeName.NAME, e.getLocalName());
			e.renameElement(explicitAudit ? ElementName.AUDIT : ElementName.AUDITPOOL, null);
			return super.walk(e, trackElem);
		}

		@Override
		public VString getElementNames()
		{
			return VString.getVString("AuditCreated AuditNotification AuditProcessRun AuditResource AuditStatus", null);
		}

	}

	/**
	 * the default is to simply stop walking and ignore these they may have been evaluated in a parent
	 */
	public class WalkPrintTalkSingleString extends WalkElement
	{
		public WalkPrintTalkSingleString()
		{
			super();
		}

		/**
		 * @param e
		 * @return the created resource
		 */
		@Override
		public KElement walk(final KElement e, final KElement trackElem)
		{
			final KElement parent = e.getParentNode_KElement();
			if (parent != null)
			{
				parent.setAttribute(e.getLocalName(), e.getText());
				e.deleteNode();
			}
			return null;
		}

		@Override
		public VString getElementNames()
		{
			return VString.getVString("Identity UserAgent", null);
		}

	}

	/**
	 * the default is to simply stop walking and ignore these they may have been evaluated in a parent
	 */
	public class WalkMediaLayers extends WalkElement
	{
		public WalkMediaLayers()
		{
			super();
		}

		/**
		 * @param e
		 * @return the created resource
		 */
		@Override
		public KElement walk(final KElement e, final KElement trackElem)
		{
			final List<KElement> elems = e.getChildArray_KElement(null, null, null, true, 0);
			final KElement m = e.getParentNode_KElement();
			for (final KElement elem : elems)
			{
				if (ElementName.GLUE.equals(elem.getLocalName()) || ElementName.MEDIA.equals(elem.getLocalName()))
				{
					m.moveElement(elem, null);
					elem.setAttribute(AttributeName.NAME, elem.getLocalName());
					elem.renameElement(ElementName.MEDIALAYERS, null);
				}
			}
			e.deleteNode();
			return null;
		}

		@Override
		public VString getElementNames()
		{
			return VString.getVString(ElementName.MEDIALAYERS, null);
		}

	}

	/**
	 * the default is to simply stop walking and ignore these they may have been evaluated in a parent
	 */
	public class WalkBoxFoldingParams extends WalkElement
	{
		public WalkBoxFoldingParams()
		{
			super();
		}

		/**
		 * @param e
		 * @return the created resource
		 */
		@Override
		public KElement walk(final KElement e, final KElement trackElem)
		{
			final List<KElement> elems = e.getChildArray_KElement(null, null, null, true, 0);
			for (final KElement elem : elems)
			{
				if (ElementName.GLUE.equals(elem.getLocalName()) || ElementName.BOXFOLDACTION.equals(elem.getLocalName()))
				{
					final KElement e2;
					if (ElementName.GLUE.equals(elem.getLocalName()))
					{
						e2 = e.insertBefore(ElementName.BOXFOLDACTION, elem, null);
						e2.moveElement(elem, null);
						e2.setAttribute(AttributeName.ACTION, ElementName.GLUE);
					}
					else
					{
						e2 = elem;
					}
					e2.renameElement(ElementName.BOXFOLDACTION, null);
				}
			}
			return super.walk(e, trackElem);
		}

		@Override
		public VString getElementNames()
		{
			return VString.getVString(ElementName.BOXFOLDINGPARAMS, null);
		}

	}

	@Override
	public String toString()
	{
		return "JSONPrepWalker [splitXJMF=" + splitXJMF + ", explicitAudit=" + explicitAudit + "]";
	}

	public boolean isSplitXJMF()
	{
		return splitXJMF;
	}

	public void setSplitXJMF(final boolean splitXJMF)
	{
		this.splitXJMF = splitXJMF;
	}
}
