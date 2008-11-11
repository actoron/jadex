/*
 * DBoolArray.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 18, 2006.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets.delegate;

import nuggets.IAssembler;
import nuggets.ICruncher;
import nuggets.IDelegate;


/** DBoolArray 
 * @author walczak
 * @since  Jan 18, 2006
 */
public class DBooleanObjectArray extends ADelegate implements IDelegate
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
		mill.put("type", o.getClass().getName());
		Boolean[] a=(Boolean[]) o;
		for(int i=0; i<a.length; i++) {
			sb.append(a[i]!=null?a[i].booleanValue()?'1':'0':'2');
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
		Boolean[] a=new Boolean[i];
		while(i>0) {
			switch (v.charAt(--i)){
				case '1': a[i]=Boolean.TRUE; break;
				case '0': a[i]=Boolean.FALSE; break;
			}
		}
		return a;
	}
}


/* 
 * $Log$
 * Revision 1.2  2006/02/23 17:46:25  walczak
 * LF
 *
 * Revision 1.1  2006/02/21 15:02:16  walczak
 * *** empty log message ***
 *
 * Revision 1.2  2006/02/17 12:48:54  walczak
 * yet even faster
 *
 * Revision 1.1  2006/01/23 17:44:18  walczak
 * rennaned some delagetes for winies
 *
 * Revision 1.1  2006/01/20 18:11:01  walczak
 * ------------------------
 *
 */