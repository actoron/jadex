/*
 * DBoolArray.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 18, 2006.  
 * Last revision $Revision: 4064 $ by:
 * $Author: walczak $ on $Date: 2006-02-23 18:48:47 +0100 (Do, 23 Feb 2006) $.
 */
package nuggets.delegate;

import nuggets.IAssembler;
import nuggets.ICruncher;


/** DBoolArray 
 * @author walczak
 * @since  Jan 18, 2006
 */
public class DBooleanArray extends ADelegate
{
	/** 
	 * @param o
	 * @param mill
	 * @see nuggets.delegate.ADelegate#persist(java.lang.Object, nuggets.ICruncher)
	 */
	public void persist(Object o, ICruncher mill, ClassLoader classloader)
	{

		  mill.startConcept(o);
		StringBuffer sb=new StringBuffer();
		mill.put("type", "[Z");
		boolean[] a=(boolean[]) o;
		for(int i=0; i<a.length; i++) {
			sb.append(a[i]?'1':'0');
		}
		mill.addToken(sb.toString());
	}

	/** 
	 * @param clazz
	 * @param asm 
	 * @return a new instance of this array
	 * @throws Exception
	 */
	public Object getInstance(Class clazz, IAssembler asm) throws Exception
	{
		String v=asm.nextToken();
		int i=v!=null?v.length():0;
		boolean[] a=new boolean[i];
		while(i>0) {
			a[--i] = v.charAt(i)=='1';
		}
		return a;
	}
}


/* 
 * $Log$
 * Revision 1.4  2006/02/23 17:46:25  walczak
 * LF
 *
 * Revision 1.3  2006/02/21 15:02:16  walczak
 * *** empty log message ***
 *
 *
 */