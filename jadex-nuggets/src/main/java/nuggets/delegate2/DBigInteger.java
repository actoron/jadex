/*
 * DString.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 17, 2006.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets.delegate2;

import java.math.BigInteger;

import nuggets.IAssembler;
import nuggets.ICruncher;
import nuggets.delegate.ADelegate;


/** DString 
 * @author walczak
 * @since  Jan 17, 2006
 */
public class DBigInteger extends ADelegate
{

   /** 
    * @param clazz
    * @param asm
    * @return a big integer 
    * @throws Exception
    */
   public Object getInstance(Class clazz, IAssembler asm) throws Exception
   {
	  String t = asm.nextToken();
      return new BigInteger(t);
   }

   /** 
    * @param o
    * @param mill
    * @see nuggets.delegate.ASimpleDelegate#persist(java.lang.Object, nuggets.ICruncher)
    */
   public void persist(Object o, ICruncher mill)
   {
		  mill.startConcept(o);
	  mill.addToken(o.toString());
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
//		return new BigInteger((String)value);
//	}

}


/* 
 * $Log$
 * Revision 1.5  2006/12/21 10:38:59  walczak
 * removed the unmarshall methods. not tested with reflection
 *
 * Revision 1.4  2006/12/20 22:55:56  walczak
 * Moved some classes represented as string to the reference representation.
 * Subclasses when serialized as a string loose their class identity with the old approach.
 *
 * Revision 1.3  2006/06/29 17:27:25  walczak
 * created a reflection delegate. alpha
 *
 * Revision 1.2  2006/03/22 17:26:40  walczak
 * minor bugfix
 *
 * Revision 1.4  2006/02/23 17:46:25  walczak
 * LF
 *
 * Revision 1.3  2006/02/17 12:48:54  walczak
 * yet even faster
 *
 * Revision 1.2  2006/01/20 18:11:01  walczak
 * ------------------------
 *
 * Revision 1.1  2006/01/18 13:59:47  walczak
 * Introduced the nuggets package.
 *
 */