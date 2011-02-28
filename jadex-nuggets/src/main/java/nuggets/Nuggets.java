/*
 * Nuggets.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 18, 2006.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import nuggets.util.CharStream;


/**
 * Nuggets
 */
public class Nuggets
{
	static final int			BUFFER_SIZE	= 2046;

	private final CharStream	cos;

	private final BeanCruncher	mill;

	private final BeanAssembler	ba;

	/**
	 * Constructor for Nuggets.
	 */
	public Nuggets()
	{
		cos = new CharStream(BUFFER_SIZE);
		mill = new BeanCruncher(new JavaXMLWriter(cos));
		ba = new BeanAssembler();
		ba.setReader(new JavaXMLReader());
	}


	/**
	 * @param obj
	 * @param os
	 * @throws IOException
	 */
	public void write(Object obj, OutputStream os, ClassLoader classloader) throws IOException
	{
		synchronized(cos)
		{
			cos.reset();
			mill.persist(obj, classloader);
			cos.writeTo(new OutputStreamWriter(os));
		}
	}

	/**
	 * @param is
	 * @return the object read from the is stream
	 */
	public Object readObject(InputStream is, ClassLoader classloader)
	{
		InputStreamReader isr = new InputStreamReader(is);
		synchronized(ba)
		{
			return ba.assemble(isr, classloader);
		}
	}

	/**
	 * @param o
	 * @return the string representation of this object in XML
	 */
	public String toXML(Object o, ClassLoader classloader)
	{
		synchronized(cos)
		{
			cos.reset();
			mill.persist(o, classloader);
			return cos.toString();			
		}
	}

	/**
	 * @param str
	 * @return the Object from String representation
	 */
	public Object fromXML(String str, ClassLoader classloader)
	{
		try
		{
			synchronized(ba)
			{
				return ba.assemble(new StringReader(str), classloader);
			}
		}
		catch(PersistenceException e)
		{
			e.setMessage(str);
			throw e;
		}
	}

	/** The nuggets codec. */
	protected static Nuggets	nuggets;

	/**
	 * Encode data with the codec.
	 * 
	 * @param val The value.
	 * @return The encoded object.
	 */
	public static String objectToXML(Object val, ClassLoader classloader)
	{
		if(nuggets == null) nuggets = new Nuggets();
		return nuggets.toXML(val, classloader);
	}

	/**
	 * Decode data with the codec.
	 * 
	 * @param val The string value.
	 * @return The encoded object.
	 */
	public static Object objectFromXML(String val, ClassLoader classloader)
	{
		if(nuggets == null) nuggets = new Nuggets();
		return nuggets.fromXML(val, classloader);
	}

}