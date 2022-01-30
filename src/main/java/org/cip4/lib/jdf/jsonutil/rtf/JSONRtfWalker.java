package org.cip4.lib.jdf.jsonutil.rtf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.cip4.jdflib.ifaces.IStreamWriter;
import org.cip4.jdflib.util.StringUtil;
import org.cip4.lib.jdf.jsonutil.JSONObjHelper;
import org.cip4.lib.jdf.jsonutil.JSONWalker;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONRtfWalker extends JSONWalker implements IStreamWriter
{
	private static final int INDENT = 2;
	PrintStream ps;
	int indent;

	public JSONRtfWalker(final JSONObjHelper root)
	{
		super(root);
		ps = null;
		indent = 0;
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

	private void printKey(final String key)
	{
		if (!StringUtil.isEmpty(key))
		{
			printLine();
			ps.print("\"\\cs2{" + key + "}\":");
		}
	}

	private void printLine()
	{
		ps.println();
		ps.print("\\line");
		for (int i = 0; i < indent; i++)
			ps.print(' ');

	}

	private void printBase(final Object a)
	{
		ps.print("\\cs4{" + a + "}");
	}

	private void printString(final String a)
	{
		ps.print("\"\\cs5{" + a + "}\"");
	}

	private void printArray(final JSONArray a)
	{
		indent += INDENT;
		ps.print("\\cs1{[}");
	}

	private void printObject(final JSONObject o)
	{
		indent += INDENT;
		ps.print("\\cs1{\\{}");
	}

	@Override
	public void writeStream(final OutputStream os) throws IOException
	{
		ps = new PrintStream(os);
		writeHeader();
		walk();
		ps.println("}");
		ps.flush();
	}

	private void writeHeader()
	{
		ps.println("{\\rtf1\\ansi");
		ps.println("\\deff0" + "{\\fonttbl\\f0\\fnil Courier New;}");
		ps.println("{\\stylesheet" + "{\\s1 SampleCode;}" + "{\\cs1 XMLToken;}" + "{\\cs2 XMLElementName;}" + "{\\cs3 XMLAttributeName;}" + "{\\cs4 XMLAttributeValue;}"
				+ "{\\cs5 XMLComment;}" + "}" + "\\pard\\plain\\s1");

	}

	/**
	 * @see org.cip4.lib.jdf.jsonutil.JSONWalker#postWalk(java.lang.String, org.json.simple.JSONObject)
	 */
	@Override
	protected void postWalk(final String rootKey, final JSONObject o)
	{
		indent -= INDENT;
		printLine();
		ps.print("\\cs1{\\}}");
		super.postWalk(rootKey, o);
	}

	/**
	 * @see org.cip4.lib.jdf.jsonutil.JSONWalker#postWalk(java.lang.String, org.json.simple.JSONArray)
	 */
	@Override
	protected void postWalk(final String key, final JSONArray val)
	{
		indent -= INDENT;
		printLine();
		ps.print("\\cs1{]}");
		super.postWalk(key, val);
	}

	/**
	 * @see org.cip4.lib.jdf.jsonutil.JSONWalker#postArrayElement(java.lang.String, java.lang.Object, int)
	 */
	@Override
	protected void postArrayElement(final String key, final Object a, final int i, final int size)
	{
		if (i < size - 1)
		{
			ps.print(",");
		}
		super.postArrayElement(key, a, i, size);
	}

}
