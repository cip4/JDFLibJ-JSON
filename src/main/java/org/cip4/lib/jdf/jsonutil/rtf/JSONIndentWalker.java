/**
 * The CIP4 Software License, Version 1.0
 *
 * Copyright (c) 2001-2025 The International Cooperation for the Integration of
 * Processes in  Prepress, Press and Postpress (CIP4).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        The International Cooperation for the Integration of
 *        Processes in  Prepress, Press and Postpress (www.cip4.org)"
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "CIP4" and "The International Cooperation for the Integration of
 *    Processes in  Prepress, Press and Postpress" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact info@cip4.org.
 *
 * 5. Products derived from this software may not be called "CIP4",
 *    nor may "CIP4" appear in their name, without prior written
 *    permission of the CIP4 organization
 *
 * Usage of this software in commercial products is subject to restrictions. For
 * details please consult info@cip4.org.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE INTERNATIONAL COOPERATION FOR
 * THE INTEGRATION OF PROCESSES IN PREPRESS, PRESS AND POSTPRESS OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the The International Cooperation for the Integration
 * of Processes in Prepress, Press and Postpress and was
 * originally based on software
 * copyright (c) 1999-2001, Heidelberger Druckmaschinen AG
 * copyright (c) 1999-2001, Agfa-Gevaert N.V.
 *
 * For more information on The International Cooperation for the
 * Integration of Processes in  Prepress, Press and Postpress , please see
 * <http://www.cip4.org/>.
 *
 *
 */
package org.cip4.lib.jdf.jsonutil.rtf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.commons.io.output.NullPrintStream;
import org.cip4.jdflib.ifaces.IStreamWriter;
import org.cip4.jdflib.util.StringUtil;
import org.cip4.lib.jdf.jsonutil.JSONObjHelper;
import org.cip4.lib.jdf.jsonutil.JSONWalker;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class JSONIndentWalker extends JSONWalker implements IStreamWriter
{
	private int singleIndent = 2;

	/**
	 * @return the singleIndent
	 */
	public int getSingleIndent()
	{
		return singleIndent;
	}

	boolean condensed;

	public boolean isCondensed()
	{
		return condensed;
	}

	public void setCondensed(final boolean condensed)
	{
		this.condensed = condensed;
	}

	/**
	 * @param singleIndent the singleIndent to set
	 */
	public void setSingleIndent(final int singleIndent)
	{
		this.singleIndent = singleIndent;
	}

	PrintStream ps;

	protected PrintStream getPs()
	{
		return ps;
	}

	int indent;

	public JSONIndentWalker(final JSONObjHelper root)
	{
		super(root);
		ps = null;
		indent = 0;
		condensed = false;
		setKeyInArray(false);
	}

	@Override
	protected Object walkSimple(final String key, final Object a)
	{
		printKey(key);

		if (a instanceof JSONObject)
		{
			printObject((JSONObject) a);
		}
		else if (a instanceof JSONArray)
		{
			printArray((JSONArray) a);
		}
		else if (a instanceof String)
		{
			printString((String) a);
		}
		else
		{
			printBase(a);
		}
		return a;
	}

	protected void printKey(final String key)
	{
		if (!StringUtil.isEmpty(key))
		{
			printLine();
			printQuoted(key);
			ps.print(":");
		}
	}

	protected void printQuoted(final String key)
	{
		ps.print("\"" + JSONObject.escape(key) + "\"");
	}

	protected void printLine()
	{
		if (!condensed)
		{
			ps.println();
			indent();
		}
	}

	protected void indent()
	{
		for (int i = 0; i < indent; i++)
			ps.print(' ');
	}

	protected void printBase(final Object a)
	{
		ps.print(a);
	}

	protected void printString(final String a)
	{
		printQuoted(a);
	}

	protected void printArray(final JSONArray a)
	{
		indent += singleIndent;
		ps.print(getBeginArray());
	}

	protected String getBeginArray()
	{
		return "[";
	}

	protected void printObject(final JSONObject o)
	{
		if (!o.isEmpty() && !condensed && !o.equals(getRoot().getRootObject()))
		{
			printLine();
		}
		indent += singleIndent;
		ps.print(getBeginObj());
	}

	protected String getBeginObj()
	{
		return "{";
	}

	@Override
	public void writeStream(final OutputStream os) throws IOException
	{
		if (!isRetainNull())
		{
			ps = NullPrintStream.INSTANCE;
			walk();
		}
		ps = new PrintStream(os);
		writeHeader();
		walk();
		writeFooter();
		ps.flush();
	}

	protected void writeFooter()
	{
		// nop
	}

	protected void writeHeader()
	{
		// nop
	}

	/**
	 * @see org.cip4.lib.jdf.jsonutil.JSONWalker#postWalk(java.lang.String, org.json.simple.JSONObject)
	 */
	@Override
	protected void postWalk(final String rootKey, final JSONObject o)
	{
		indent -= singleIndent;
		if (!o.isEmpty())
		{
			printLine();
		}
		ps.print(getEndObj());
		super.postWalk(rootKey, o);
	}

	protected String getEndObj()
	{
		return "}";
	}

	/**
	 * @see org.cip4.lib.jdf.jsonutil.JSONWalker#postWalk(java.lang.String, org.json.simple.JSONArray)
	 */
	@Override
	protected void postWalk(final String key, final JSONArray val)
	{
		indent -= singleIndent;
		if (needsLine(val))
		{
			printLine();
		}
		ps.print(getEndArray());
		super.postWalk(key, val);
	}

	boolean needsLine(final JSONArray val)
	{
		for (final Object o : val)
		{
			if (o instanceof JSONAware)
				return true;
		}
		return false;
	}

	protected String getEndArray()
	{
		return "]";
	}

	/**
	 * @see org.cip4.lib.jdf.jsonutil.JSONWalker#postArrayElement(java.lang.String, java.lang.Object, int)
	 */
	@Override
	protected void postArrayElement(final String key, final Object a, final int i, final int size)
	{
		if (i < size - 1)
		{
			ps.print(getArraySep());
		}
		super.postArrayElement(key, a, i, size);
	}

	/**
	 * @see org.cip4.lib.jdf.jsonutil.JSONWalker#postArrayElement(java.lang.String, java.lang.Object, int)
	 */
	@Override
	protected void postObjectElement(final String key, final Object a, final int i, final int size)
	{
		if (i < size - 1)
		{
			ps.print(getObjectSep());
		}
		super.postObjectElement(key, a, i, size);
	}

	protected String getArraySep()
	{
		return ",";
	}

	protected String getObjectSep()
	{
		return ",";
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return super.toString() + " [singleIndent=" + singleIndent + ", indent=" + indent + "]";
	}

}
