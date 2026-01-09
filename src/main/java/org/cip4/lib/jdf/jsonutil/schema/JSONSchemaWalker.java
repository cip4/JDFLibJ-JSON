/*
 * The CIP4 Software License, Version 1.0
 *
 *
 * Copyright (c) 2001-2026 The International Cooperation for the Integration of Processes in Prepress, Press and Postpress (CIP4). All rights reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cip4.lib.jdf.jsonutil.JSONArrayHelper;
import org.cip4.lib.jdf.jsonutil.JSONObjHelper;
import org.cip4.lib.jdf.jsonutil.JSONWalker;
import org.cip4.lib.jdf.jsonutil.JSONWriter;
import org.cip4.lib.jdf.jsonutil.JSONWriter.eJSONCase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONSchemaWalker extends JSONWalker
{
	private static final Log log = LogFactory.getLog(JSONSchemaWalker.class);

	public JSONSchemaWalker(final JSONObjHelper root)
	{
		super(root);
		setKeyInArray(false);
		jsonCase = eJSONCase.retain;
		additional = false;
	}

	eJSONCase jsonCase;
	private boolean additional;

	public boolean isAdditional()
	{
		return additional;
	}

	public void setAdditional(boolean additional)
	{
		this.additional = additional;
	}

	public eJSONCase getJsonCase()
	{
		return jsonCase;
	}

	public void setJsonCase(final eJSONCase jsonCase)
	{
		this.jsonCase = jsonCase;
	}

	@Override
	protected Object walkSimple(final String key, final Object a)
	{
		reduceAbstract(key, a);

		if (a instanceof JSONObject)
		{
			walkObject((JSONObject) a);
		}
		else if (a instanceof JSONArray)
		{
			walkArray((JSONArray) a);
		}
		if (!eJSONCase.retain.equals(jsonCase))
		{
			updateCase(key, a);
		}
		return a;
	}

	void updateCase(final String key, final Object a)
	{
		if (JSONSchemaUpdate.PROPERTIES.equals(key))
		{
			final JSONObjHelper oh = JSONObjHelper.getHelper(a);
			for (final String k : oh.getKeys())
			{
				oh.setObj(JSONWriter.updateCase(k, jsonCase), oh.remove(k));
			}

		}
		else if (JSONSchemaUpdate.REQUIRED.equals(key) || JSONSchemaUpdate.ENUM.equals(key))
		{
			final JSONArrayHelper ah = JSONArrayHelper.getHelper(a);
			if (ah != null)
			{
				for (int i = 0; i < ah.size(); i++)
				{
					ah.set(i, JSONWriter.updateCase(ah.getString(i), jsonCase));
				}
			}
		}
	}

	void reduceAbstract(final String key, final Object a)
	{
		if (JSONSchemaUpdate.PROPERTIES.equals(key))
		{
			final JSONObjHelper oh = JSONObjHelper.getHelper(a);
			oh.remove("Any");
			oh.remove("any");
			oh.remove("otherAttributes");
		}
		else if (JSONSchemaUpdate.REQUIRED.equals(key))
		{
			final JSONArrayHelper ah = JSONArrayHelper.getHelper(a);
			if (ah != null)
			{
				ah.remove("Any");
				ah.remove("any");
				ah.remove("otherAttributes");
			}
		}
	}

	void walkArray(final JSONArray a)
	{
		// TODO Auto-generated method stub

	}

	void walkObject(final JSONObject o)
	{
		final JSONObjHelper oh = JSONObjHelper.getHelper(o);

		updateAdditional(oh);
	}

	void updateAdditional(JSONObjHelper o)
	{
		if (!additional)
		{
			final String typ = o.getString(JSONSchemaUpdate.TYPE);
			if (JSONSchemaUpdate.ARRAY.equals(typ))
			{
				o.setBool(JSONSchemaUpdate.ADDITIONAL_ITEMS, additional);
			}
			else if (JSONSchemaUpdate.OBJECT.equals(typ))
			{
				o.setBool(JSONSchemaUpdate.ADDITIONAL_PROPERTIES, additional);
			}
		}
	}

}
