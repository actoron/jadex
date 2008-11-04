/*
 * BeanAssembler.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 16, 2006.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

import nuggets.IReader.Buffer;


/**
 * BeanAssembler
 *
 * @author walczak
 * @since Jan 16, 2006
 */
public class BeanAssembler implements IAssembler
{
	private static final int    OBJECT_ARRAY_LENGTH = 512;

	private Object[]	 		objects	= new Object[OBJECT_ARRAY_LENGTH];

	private final ArrayList		delayed	= new ArrayList();

	private IReader				reader;

	private String				ontology;

	/**
	 * @param reader
	 */
	public void setReader(IReader reader)
	{
		this.reader = reader;
	}

	/**
	 * @param rdr
	 * @return the object with id == 0
	 */
	public Object assemble(Reader rdr)
	{
		try
		{
			clear();
			parse(rdr);
			perform_delayed();
		}
		catch(Exception e)
		{
			throw new PersistenceException(e);
		}

		return get(1);
	}

	/**
	 * remove all chached objects from this one
	 */
	public void clear()
	{
		delayed.clear();
		Arrays.fill(objects, null);
	}


	// ------------ IAssembler -----------------

	/**
	 * @param attribute
	 * @return the value of the attribute including null if not available yet
	 */
	public Object getAttributeValue(String attribute)
	{
		int i=reader.getAttributeIndex(attribute);
		if (i<0) return null;
		if (!reader.isReferenceAttribute(i)) return reader.getAttributeValue(i);

		String str_id=reader.getAttributeValue(i);

		int id=Integer.parseInt(str_id, 16);
		if (has(id)) return get(id);
		// else
		delay(new AttributeSetOperation(delegate, object, attribute, str_id));
		return null;
	}

	/** buffer for reader text */
	protected Buffer	buffer;

	/** the offset of next token - space separated */
	protected int		token_offset;

	private IDelegate	delegate;

	private Object	object;


	/**
	 * @return the text of an element
	 * @see nuggets.IAssembler#getText()
	 */
	public String getText()
	{
		if(buffer == null)
		{
			buffer = reader.getText();
			if(buffer == null) return null; // no more tokens from reader for
		}
		return reader.decodeText(buffer.chars, buffer.start, buffer.len);
	}

	/**
	 * @return return the byte array from the text data
	 * @see nuggets.IAssembler#getData()
	 */
	public byte[] getData()
	{
		return reader.getData();
	}

	/**
	 * It assumes that there is only one text content per element
	 *
	 * @return the next token from character stream
	 * @see nuggets.IAssembler#nextToken()
	 */
	public String nextToken()
	{
		if(buffer == null)
		{
			buffer = reader.getText();
			if(buffer == null) return null; // no more tokens from reader for
											// this element
			token_offset = buffer.start;
		}

		int end=buffer.start + buffer.len;
		for(int i = token_offset; i < end; i++)
		{
			char c = buffer.chars[i];
			if(c == ' ')
			{
				if(i > token_offset)
				{
					String ret = new String(buffer.chars, token_offset, i - token_offset);
					token_offset = i + 1;
					return ret;
				}
				token_offset = i + 1;
			} 
		}
		if(end > token_offset)
		{
			String ret = new String(buffer.chars, token_offset, end - token_offset);
			token_offset = end;
			return ret;
		}
		return null;
	}

	/**
	 * @param sid
	 * @return the object with id
	 * @throws InstanceNotAvailableException 
	 */
	public Object getValue(String sid) throws InstanceNotAvailableException
	{
		try {
			int id = Integer.parseInt(sid, 16);
			if (id==0) return null;
			Object ret=objects[id];
			if (ret==null) {
				throw new InstanceNotAvailableException(sid);
			}
			return ret;
		} catch(NumberFormatException nfe) { 
			throw new PersistenceException(nfe);
		}
	}

	/**
	 * @param op
	 * @see nuggets.IAssembler#delay(nuggets.IDelayedOperation)
	 */
	public void delay(IDelayedOperation op)
	{
		delayed.add(op);
	}
	
	/** 
	 * @return the reader used to read the document
	 * @see nuggets.IAssembler#getReader()
	 */
	public IReader getReader()
	{
		return reader;
	}

	// ----------------------------------------------------------------
	
	/**
	 * @param id
	 * @return tests if the object with this id is read
	 */
	private boolean has(int id)
	{
		return id==0 || id < objects.length && objects[id] != null;
	}
	
	/**
	 * @param id
	 * @return tests if the object with this id is read
	 */
	private Object get(int id)
	{
		return id < objects.length?objects[id]:null;
	}


	/**
	 * @param rdr
	 * @throws Exception
	 *
	 */
    private void parse(Reader rdr) throws Exception
	{
        reader.start(rdr);
		this.ontology = reader.getAttributeValue("xmlns");

		String tag;
		while((tag= reader.nextElement())!=null) {
			buffer = null;
			int id  = Integer.parseInt(reader.getID(), 16);
			Class   clazz = getClass(tag);
			delegate = PersistenceHelper.getDelegate(clazz);
			object = delegate.getInstance(clazz, this);
			set(id, object);
			delegate.assemble(object, this);
		}
	}

	/**
	 * @param id
	 * @param obj
	 */
	private void set(int id, Object obj)
	{
		if (objects.length<=id) {
			Object[] tmp=new Object[Math.max(id+64, objects.length<<1)];
			System.arraycopy(objects, 0, tmp, 0, objects.length);
			objects = tmp;
		}
		objects[id] = obj;
	}

	/**
	 * performs all delayed operations
	 */
	private void perform_delayed()
	{
		int len = delayed.size();
		for(int i = 0; i < len; i++)
		{
			IDelayedOperation l = (IDelayedOperation)delayed.get(i);
			try
			{
				l.perform(this);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		delayed.clear();
	}

	/**
	 * @param tag
	 * @return the class for this tag
	 */
	private Class getClass(String tag)
	{
		if (tag.startsWith("array")) {
			tag = reader.getAttributeValue("type");
		}
		try
		{
			if (ontology!=null && tag.indexOf('.')<0)
				return Class.forName(ontology+'.'+tag); //, true, Thread.currentThread().getContextClassLoader());
		}
		catch(ClassNotFoundException e1) { /*NOP*/}
		try
		{
			return Class.forName(tag); //, true, Thread.currentThread().getContextClassLoader());
		}
		catch(ClassNotFoundException e)	{
			throw new PersistenceException(e);
		}
	}
}


/*
 * $Log$
 * Revision 1.10  2006/07/06 16:21:58  pokahr
 * *** empty log message ***
 *
 * Revision 1.9  2006/07/06 09:19:50  walczak
 * getTag of BeanAssembler
 *
 * Revision 1.8  2006/05/30 09:05:06  walczak
 * fixed some bugs
 *
 * Revision 1.7  2006/02/17 12:48:54  walczak
 * yet even faster
 *
 * Revision 1.6  2006/02/16 17:41:08  walczak
 * no reference to strings in Maps but a direct inclusion.
 *
 * Revision 1.5  2006/02/15 10:42:31  walczak
 * removed StringBuffer from XMLReader,
 * removed "new" from XMLReadr,
 * made generated Delegates use set method,
 *
 * Revision 1.4  2006/02/15 08:07:50  braubach
 * *** empty log message ***
 *
 * Revision 1.3  2006/02/14 17:39:05  walczak
 * new version of nuggets
 *
 * Revision 1.2  2006/01/20 18:11:02  walczak
 * ------------------------
 * Revision 1.1 2006/01/18 13:59:47 walczak
 * Introduced the nuggets package.
 */