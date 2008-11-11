/*
 * DString.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 17, 2006.  
 * Last revision $Revision: 4687 $ by:
 * $Author: walczak $ on $Date: 2006-12-21 11:38:59 +0100 (Do, 21 Dez 2006) $.
 */
package nuggets.delegate;

import java.io.File;

import nuggets.IAssembler;
import nuggets.ICruncher;


/** DString 
 * @author walczak
 * @since  Jan 17, 2006
 */
public class DFile extends ADelegate
{

	/** 
	 * @param clazz
	 * @param asm
	 * @return a new instance of this array
	 * @throws Exception
	 */
	public Object getInstance(Class clazz,IAssembler asm) throws Exception
   {
      return new File((String)asm.getAttributeValue("v"));
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
  
   
//   
//	/** 
//	 * @param clazz
//	 * @param value
//	 * @return the boolean expression
//	 * @see nuggets.delegate.ASimpleDelegate#unmarshall(java.lang.Class, java.lang.Object)
//	 */
//	public Object unmarshall(Class clazz, Object value)
//	{
//		return new File((String)value);
//	}

}


/* 
 * $Log$
 * Revision 1.8  2006/12/21 10:38:59  walczak
 * removed the unmarshall methods. not tested with reflection
 *
 * Revision 1.7  2006/12/20 22:55:56  walczak
 * Moved some classes represented as string to the reference representation.
 * Subclasses when serialized as a string loose their class identity with the old approach.
 *
 * Revision 1.6  2006/06/29 17:27:25  walczak
 * created a reflection delegate. alpha
 *
 *
 */