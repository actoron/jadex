/*
 * DString.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 17, 2006.  
 * Last revision $Revision: 4064 $ by:
 * $Author: walczak $ on $Date: 2006-02-23 18:48:47 +0100 (Do, 23 Feb 2006) $.
 */
package nuggets.delegate;

import java.util.BitSet;

import nuggets.IAssembler;
import nuggets.ICruncher;


/** DString 
 * @author walczak
 * @since  Jan 17, 2006
 */
public class DBitSet extends ADelegate
{
	/** 
	 * @param clazz
	 * @param asm 
	 * @return the string stored in "v"
	 * @throws Exception
	 */
	public Object getInstance(Class clazz, IAssembler asm) throws Exception
	{
		String t = asm.nextToken();
		int i = t!=null?t.length():0;
		BitSet set = new BitSet(i);
		while(i > 0)
		{
			if(t.charAt(--i) == '1') set.set(i);
		}
		return set;
	}

	/** 
	 * @param o
	 * @param mill
	 * @see nuggets.delegate.ASimpleDelegate#persist(java.lang.Object, nuggets.ICruncher)
	 */
	public void persist(Object o, ICruncher mill)
	{

		  mill.startConcept(o);
		BitSet set = (BitSet)o;
		int len = set.length();
		StringBuffer sb = new StringBuffer(len);
		for(int i = 0; i < len; i++)
		{
			sb.append(set.get(i) ? '1' : '0');
		}
		mill.addToken(sb.toString());
	}

}


/* 
 * $Log$
 * Revision 1.4  2006/02/23 17:46:25  walczak
 * LF
 *
 * Revision 1.3  2006/02/17 12:48:54  walczak
 * yet even faster
 *
 * Revision 1.2  2006/01/20 18:11:02  walczak
 * ------------------------
 *
 * Revision 1.1  2006/01/18 13:59:47  walczak
 * Introduced the nuggets package.
 *
 */