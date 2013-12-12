/*
 * DshortsArray.java
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

/** DshortsArray 
 * @author walczak
 * @since  Jan 18, 2006
 */
public class DShortObjectArray extends ADelegate
{
	/** 
	 * @param o
	 * @param mill
	 * @see nuggets.delegate.ADelegate#persist(java.lang.Object, nuggets.ICruncher)
	 */
	public void persist(Object o, ICruncher mill, ClassLoader classloader)
	{

		  mill.startConcept(o);
        mill.put("type", o.getClass().getName());
		Short[] a=(Short[]) o;
		int len=a.length;
		mill.put("length", Integer.toString(len));
		for(int i=0; i<len; i++) {
			mill.addToken(a[i]!=null?a[i].toString():"null");
		}
	}

	/** 
	 * @param clazz
	 * @param asm
	 * @return a new instance of this array
	 * @throws Exception
	 */
	public Object getInstance(Class clazz,IAssembler asm) throws Exception
	{
		int l=Integer.parseInt((String)asm.getAttributeValue("length"));
		Short[] a=new Short[l];
		for(int i=0; i<l; i++) {
			try {
				a[i] = Short.valueOf(asm.nextToken());
			} catch(NumberFormatException e) { /* NOP */ }
		}
		return a;
	}
}
