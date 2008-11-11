/*
 * DMap.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Dec 5, 2005.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets.delegate;

import java.util.Comparator;
import java.util.TreeMap;

import nuggets.IAssembler;
import nuggets.ICruncher;


/** DMap 
 * @author walczak
 * @since  Dec 5, 2005
 */
public class DTreeMap extends DMap
{

	/**
	 * @param o
	 * @param mill
	 * @see nuggets.delegate.DMap#persist(java.lang.Object, nuggets.ICruncher)
	 */
	public void persist(Object o, ICruncher mill, ClassLoader classloader)
	{
		TreeMap map = (TreeMap)o;
		declareReferences(map, mill, classloader);
		int cmpID = mill.declare(map.comparator(), classloader);
		mill.startConcept(o);
		mill.put("comparator", cmpID);
		addReferences(map, mill);
	}

	/** 
	 * @param clazz
	 * @param asm 
	 * @return a TreeMap
	 * @throws Exception
	 */
	public Object getInstance(Class clazz, IAssembler asm) throws Exception
	{
		Comparator cmp = (Comparator)asm.getAttributeValue("comparator");
		return new TreeMap(cmp);
	}
}
