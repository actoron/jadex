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

import java.util.TimeZone;

import nuggets.IAssembler;
import nuggets.ICruncher;


/** DString 
 * @author walczak
 * @since  Jan 17, 2006
 */
public class DTimeZone extends ADelegate
{

	/** 
	 * @param clazz
	 * @param asm
	 * @return a new instance of this array
	 * @throws Exception
	 */
	public Object getInstance(Class clazz,IAssembler asm) throws Exception
   {
      return TimeZone.getTimeZone((String)asm.getAttributeValue("id"));
   }

   /** 
    * @param o
    * @param mill
    * @see nuggets.delegate.ASimpleDelegate#persist(java.lang.Object, nuggets.ICruncher)
    */
   public void persist(Object o, ICruncher mill)
   {
		  mill.startConcept(o);
      mill.put("id", ((TimeZone)o).getID());
   }

//   /** 
//    * @param exp
// * @return exp+".getID()";
//    * @see nuggets.delegate.ASimpleDelegate#getMarshallString(java.lang.String)
//    */
//   public String getMarshallString(String exp)
//   {
//      return exp+".getID()";
//   }

	/** 
	 * @param clazz
	 * @param o
	 * @return the name of the class
	 * @see nuggets.delegate.ADelegate#marshall(java.lang.Class, java.lang.Object)
	 */
	public String marshall(Class clazz, Object o)
	{
		return ((TimeZone)o).getID();
	}
   
//   /** 
//    * @param exp
// * @return "java.util.TimeZone.getTimeZone((String)"+exp+"))";
//    * @see nuggets.delegate.ASimpleDelegate#getUnmarshallString(String, java.lang.String)
//    */
//   public String getUnmarshallString(String className, String exp)
//   {
//      return "java.util.TimeZone.getTimeZone((String)"+exp+")";
//   }
//   
//	/** 
//	 * @param clazz
//	 * @param value
//	 * @return the boolean expression
//	 * @see nuggets.delegate.ASimpleDelegate#unmarshall(java.lang.Class, java.lang.Object)
//	 */
//	public Object unmarshall(Class clazz, Object value)
//	{
//		return TimeZone.getTimeZone((String)value);
//	}  
//  

}


/* 
 * $Log$
 * Revision 1.9  2006/12/21 10:38:59  walczak
 * removed the unmarshall methods. not tested with reflection
 *
 * Revision 1.8  2006/12/20 22:55:56  walczak
 * Moved some classes represented as string to the reference representation.
 * Subclasses when serialized as a string loose their class identity with the old approach.
 *
 * Revision 1.7  2006/06/29 17:27:25  walczak
 * created a reflection delegate. alpha
 *
 * Revision 1.6  2006/02/23 17:46:25  walczak
 * LF
 *
 * Revision 1.5  2006/02/21 15:02:16  walczak
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