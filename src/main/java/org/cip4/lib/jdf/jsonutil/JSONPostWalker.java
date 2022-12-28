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
 * (C) 2020 Heidelberger Druckmaschinen AG
 */
package org.cip4.lib.jdf.jsonutil;

import java.util.Collection;

import org.cip4.jdflib.core.AttributeName;
import org.cip4.jdflib.core.ElementName;
import org.cip4.jdflib.core.KElement;
import org.cip4.jdflib.core.VString;
import org.cip4.jdflib.elementwalker.BaseElementWalker;
import org.cip4.jdflib.elementwalker.BaseWalker;
import org.cip4.jdflib.elementwalker.BaseWalkerFactory;

/**
 * @author rainer prosi class to preprocess xjdf or printTalk for json conversion
 */
public class JSONPostWalker extends BaseElementWalker
{

	/**
	 *
	 */
	public JSONPostWalker()
	{
		super(new BaseWalkerFactory());
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

		/**
		 * @param xjdf
		 * @return true if must continue
		 */
		@Override
		public KElement walk(final KElement e, final KElement trackElem)
		{
			final String name = e.getNonEmpty(AttributeName.NAME);
			if (name != null)
			{
				e.renameElement(getName(name), null);
				e.removeAttribute(AttributeName.NAME);
				final KElement pool = ensureRealPool(e, ElementName.AUDITPOOL);
				pool.moveElement(e, null);
			}
			return super.walk(e, trackElem);
		}

		/**
		 * @see org.cip4.jdflib.elementwalker.BaseWalker#getElementNames()
		 */
		@Override
		public VString getElementNames()
		{
			return new VString(ElementName.AUDITPOOL);
		}
	}

	String getName(final String name)
	{
		return name.startsWith(ElementName.AUDIT) ? name : ElementName.AUDIT + name;
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
			final String name = e.getAttribute(AttributeName.NAME);
			e.renameElement(getName(name), null);
			e.removeAttribute(AttributeName.NAME);
			return super.walk(e, trackElem);
		}

		@Override
		public VString getElementNames()
		{
			return VString.getVString(ElementName.AUDIT, null);
		}

	}

	/**
	 * TODO invert the default is to simply stop walking and ignore these they may have been evaluated in a parent
	 */
	public class WalkMediaLayer extends WalkElement
	{
		public WalkMediaLayer()
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
			final String name = e.getNonEmpty(AttributeName.NAME);
			if (name != null)
			{
				e.renameElement(name, null);
				e.removeAttribute(AttributeName.NAME);
				final KElement eml = ensureRealMediaLayers(e);
				eml.moveElement(e, null);
			}
			return super.walk(e, trackElem);
		}

		KElement ensureRealMediaLayers(final KElement e)
		{
			return ensureRealPool(e, ElementName.MEDIALAYERS);
		}

		@Override
		public VString getElementNames()
		{
			return VString.getVString(ElementName.MEDIALAYERS, null);
		}

	}

	KElement ensureRealPool(final KElement e, final String pool)
	{
		final KElement m = e.getParentNode_KElement();
		final Collection<KElement> v = m.getChildArray(pool, null);
		for (final KElement ml : v)
		{
			if (!ml.hasNonEmpty(AttributeName.NAME))
				return ml;
		}
		KElement newPool = m.appendElement(pool);
		m.moveElement(newPool, e);
		return newPool;
	}

	/**
	 * @author rainerprosi
	 */
	public class WalkBoxFoldAction extends WalkElement
	{
		public WalkBoxFoldAction()
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
			final String name = e.getAttribute(AttributeName.ACTION);
			if (ElementName.GLUE.equals(name))
			{
				final KElement g = e.getCreateElement(ElementName.GLUE);
				e.getParentNode_KElement().moveElement(g, e);
				e.deleteNode();
			}
			return super.walk(e, trackElem);
		}

		@Override
		public VString getElementNames()
		{
			return VString.getVString(ElementName.BOXFOLDACTION, null);
		}

	}
}
