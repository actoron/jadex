/*
 * BeanCruncher.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Dec 2, 2005.  
 * Last revision $Revision: 4252 $ by:
 * $Author: walczak $ on $Date: 2006-05-30 11:05:06 +0200 (Di, 30 Mai 2006) $.
 */
package nuggets;

import java.util.HashMap;
import java.util.Map;

import nuggets.util.IdHashMap;


/**
 * BeanCruncher - transforms beans into parts and attributes. To be used on one
 * communication line. Uses references to minimize the number of items send over
 * the wire or to disk.
 */
public class BeanCruncher implements ICruncher
{
	private IWriter			codec;

	/** the default package */
	private String			ontology;

	final private IdHashMap	ids					= new IdHashMap();

	private Map idstodo = new HashMap();
	
	static final int		MAX_VALUE_LENGTH	= 256;

	/**
	 * Constructor for BeanCruncher.
	 * 
	 * @param codec
	 */
	public BeanCruncher(IWriter codec)
	{
		this.codec = codec;
	}

	// ----------- ICruncher interface ----------
	/**
	 * @param obj
	 * @return the id of this object or 0
	 */
	public int declare(Object obj, ClassLoader classloader)
	{
		if(obj != null) 
			return persist_recursive(obj, classloader);
		return 0;
	}

	/** 
	 * @param attr
	 * @param id
	 * @see nuggets.ICruncher#put(java.lang.String, int)
	 */
	public void put(String attr, int id)
	{
		codec.putRef(attr, Integer.toHexString(id));
	}

	/** 
	 * @param value
	 * @see nuggets.ICruncher#add(Object)
	 */
	public void add(Object value)
	{
		codec.addToken(Integer.toHexString(getID(value)));
	}

	/**
	 * @param attr
	 * @param value
	 * @see nuggets.ICruncher#put(java.lang.String, java.lang.String)
	 */
	public void put(String attr, String value)
	{
		if(value == null)
		{
			codec.put(attr, "0");
		}
		else if(value.length() > MAX_VALUE_LENGTH)
		{
			put(attr, getID(value));
//			codec.put(attr, Integer.toHexString(getID(value)));
		}
		else
		{
			codec.put(attr, value);
		}
	}

	/**
	 * @param value
	 * @see nuggets.ICruncher#addToken(java.lang.String)
	 */
	public void addToken(String value)
	{
		codec.addToken(value);
	}


	/** 
	 * @param ba
	 * @see nuggets.ICruncher#setData(byte[])
	 */
	public void setData(byte[] ba)
	{
		codec.write(ba);
	}
	
	
	/** 
	 * @param text
	 * @see nuggets.ICruncher#setText(java.lang.String)
	 */
	public void setText(String text)
	{
		codec.write(text);
		
	}
	
	/** 
	 * @param chars 
	 * @param start 
	 * @param len 
	 * @see nuggets.ICruncher#setText(java.lang.String)
	 */
	public void setText(char[] chars, int start, int len)
	{
		codec.write(chars, start, len);
		
	}

	// -----------------------------------------

	/**
	 * Root marshall call point
	 * 
	 * @param root
	 */
	public void persist(Object root, ClassLoader classloader)
	{
		clear();
		if (root!=null) setOntology(root.getClass());	

		codec.start(ontology);
		persist_recursive(root, classloader);
		Object[] todo = idstodo.keySet().toArray();
		for(int i=0; i<todo.length; i++)
		{
			int key = ((Integer)idstodo.get(todo[i])).intValue();
			persist_recursive(key, todo[i], classloader);
		}
		codec.end();
	}

	/**
	 * This clears the object ids an the scrap
	 */
	public void clear()
	{
		ontology=null;
		ids.clear();
		idstodo.clear();
		seq = 1;
	}

	/**
	 * @param o
	 * @return the id of this object
	 */
	protected int persist_recursive(Object o, ClassLoader classloader)
	{
		int id=ids.get(o);
		if(id==0)
		{
			ids.put(o, id=seq++);
			idstodo.remove(o);
			Class clazz = o.getClass();

			// find delegate
			IDelegate m = PersistenceHelper.getDelegate(clazz, classloader);
			try
			{
				m.persist(o, this, classloader);
				codec.end(getTag(clazz));
			}
			catch(Exception e)
			{
				throw new PersistenceException(e);
			}
		}
		return id;
	}
	
	/**
	 * @param o
	 */
	protected void persist_recursive(int id, Object o, ClassLoader classloader)
	{
		ids.put(o, id);
		idstodo.remove(o);
		Class clazz = o.getClass();

		// find delegate
		IDelegate m = PersistenceHelper.getDelegate(clazz, classloader);
		try
		{
			m.persist(o, this, classloader);
			codec.end(getTag(clazz));
		}
		catch(Exception e)
		{
			throw new PersistenceException(e);
		}
	}

	/**
	 * @param o  
	 */
	public void startConcept(Object o)
	{
		codec.start(getTag(o.getClass()), Integer.toHexString(getID(o)));
	}
	

	/**
	 * @param clazz
	 * @return the tag for this class
	 */
	protected String getTag(Class clazz)
	{
		if(clazz.isArray()) return "array";
		String cn = clazz.getName();
		if(cn.startsWith(ontology + '.')) return cn.substring(ontology.length() + 1);
		return cn;
	}

	/** <code>seq</code>: */
	protected int	seq;

	/**
	 * @param o
	 * @return the id for this object
	 */
	protected int getID(Object o)
	{
		if(o == null) return 0;
		int id = ids.get(o);
		if(id == 0) 
		{	
			id = seq++;
//			ids.put(o, id = seq++);
			idstodo.put(o, Integer.valueOf(id));
		}
		return id;
	}
	
	/** 
	 * @param clazz
	 */
	private void setOntology(Class clazz)
	{
		if (clazz.isPrimitive() || clazz.isArray()) return;
		String clazz_name=clazz.getName();
		String tpck="";
		int i=clazz_name.lastIndexOf('.');
		if (i>0) {
			tpck=clazz_name.substring(0, i);
		}
		if (ontology == null || ontology.length()>tpck.length()) {
			ontology=tpck;
		}
	}

}