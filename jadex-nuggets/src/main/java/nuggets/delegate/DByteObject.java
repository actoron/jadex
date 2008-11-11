/*
 * DString.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 17, 2006.  
 * Last revision $Revision: 4401 $ by:
 * $Author: walczak $ on $Date: 2006-06-29 19:27:25 +0200 (Do, 29 Jun 2006) $.
 */
package nuggets.delegate;

import nuggets.IAssembler;
import nuggets.ICruncher;


/** DString 
 * @author walczak
 * @since  Jan 17, 2006
 */
public class DByteObject extends ASimpleDelegate
{

   /** 
    * @param clazz
    * @param asm 
    * @return the string stored in "v"
    * @throws Exception
    */
   public Object getInstance(Class clazz, IAssembler asm) throws Exception
   {
      return new Byte((String)asm.getAttributeValue("v"));
   }

   /** 
    * @param o
    * @param mill
    * @see nuggets.delegate.ASimpleDelegate#persist(java.lang.Object, nuggets.ICruncher)
    */
   public void persist(Object o, ICruncher mill, ClassLoader classloader)
   {
		  mill.startConcept(o);
      mill.put("v", o.toString());
   }
   
	/** 
	 * @param clazz
	 * @param value
	 * @return the boolean expression
	 * @see nuggets.delegate.ASimpleDelegate#unmarshall(java.lang.Class, java.lang.Object)
	 */
	public Object unmarshall(Class clazz, Object value)
	{
		return new Byte((String)value);
	}
  
}


/* 
 * $Log$
 * Revision 1.3  2006/06/29 17:27:25  walczak
 * created a reflection delegate. alpha
 *
 * Revision 1.2  2006/02/23 17:46:25  walczak
 * LF
 *
 * Revision 1.1  2006/02/21 15:02:16  walczak
 * *** empty log message ***
 *
 * Revision 1.4  2006/02/17 12:48:54  walczak
 * yet even faster
 *
 * Revision 1.3  2006/02/16 17:41:08  walczak
 * no reference to strings in Maps but a direct inclusion.
 *
 * Revision 1.2  2006/01/20 18:11:01  walczak
 * ------------------------
 *
 * Revision 1.1  2006/01/18 13:59:47  walczak
 * Introduced the nuggets package.
 *
 */