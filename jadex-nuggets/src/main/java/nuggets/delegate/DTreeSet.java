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

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import nuggets.IAssembler;
import nuggets.ICruncher;


/** DMap 
 * @author walczak
 * @since  Dec 5, 2005
 */
public class DTreeSet extends DCollection
{

	/** 
	 * @param o
	 * @param mill
	 * @see nuggets.delegate.DMap#persist(java.lang.Object, nuggets.ICruncher)
	 */
	public void persist(Object o, ICruncher mill, ClassLoader classloader)
	{
		TreeSet set=(TreeSet)o;
		int cmp_id=mill.declare(set.comparator(), classloader);
		Iterator it = ((Collection)o).iterator();
		while(it.hasNext())			mill.declare(it.next(), classloader);

		  mill.startConcept(o);

		mill.put("comparator", cmp_id); 
		it = ((Collection)o).iterator();
		while(it.hasNext())			mill.add(it.next());
	}

	/** 
	 * @param clazz
	 * @param asm
	 * @return a new instance of this array
	 * @throws Exception
	 */
	public Object getInstance(Class clazz, IAssembler asm) throws Exception
	{
		Comparator cmp = (Comparator)asm.getAttributeValue("comparator");
		return new TreeSet(cmp);
	}


}

/* 
 * $Log$
 * Revision 1.4  2006/02/17 12:48:54  walczak
 * yet even faster
 *
 * Revision 1.3  2006/02/16 17:41:08  walczak
 * no reference to strings in Maps but a direct inclusion.
 *
 * Revision 1.2  2006/01/20 18:11:02  walczak
 * ------------------------
 *
 * Revision 1.1  2006/01/18 13:59:47  walczak
 * Introduced the nuggets package.
 *
 */