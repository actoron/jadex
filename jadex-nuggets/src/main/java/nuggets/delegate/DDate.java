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
package nuggets.delegate;

import java.text.SimpleDateFormat;
import java.util.Date;

import nuggets.IAssembler;
import nuggets.ICruncher;


/** DString 
 * @author walczak
 * @since  Jan 17, 2006
 */
public class DDate extends ADelegate
{
//
//   /** <code>CLASS_NAME</code>: */
//   static protected final String CLASS_NAME=DDate.class.getName();
   
   /** formats date in ISO 8601 */
   protected  final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

   /** 
    * @param clazz
    * @param asm
    * @return the string stored in "v"
    * @throws Exception
    */
   public Object getInstance(Class clazz, IAssembler asm) throws Exception
   {
      return parse(asm.getAttributeValue("v"));
   }

   /** 
    * @param o
    * @param mill
    * @see nuggets.delegate.ASimpleDelegate#persist(java.lang.Object, nuggets.ICruncher)
    */
   public void persist(Object o, ICruncher mill, ClassLoader classloader)
   {

		  mill.startConcept(o);
      mill.put("v", format((Date)o));

   }

   /** 
    * @param date
    * @return a format in ISO 8601
    */
   public String format(Date date) {
      return df.format(date);
   }
   
   /** 
    * @param exp
    * @return a date from the parsed expression in ISO 8601 format
    * @throws Exception
    */
   public Date parse(Object exp) throws Exception {
      return df.parse(String.valueOf(exp));
   }
   
//   /** 
//    * @param exp
// * @return a call to the format method
//    * @see nuggets.delegate.ASimpleDelegate#getMarshallString(java.lang.String)
//    */
//   public String getMarshallString(String exp)
//   {
//      return CLASS_NAME+".format("+exp+")";
//   }
   
	/** 
	 * @param clazz
	 * @param o
	 * @return the name of the class
	 * @see nuggets.delegate.ADelegate#marshall(java.lang.Class, java.lang.Object)
	 */
	public String marshall(Class clazz, Object o)
	{
		return format((Date)o);
	}

//   /** 
//    * @param exp
// * @return a call to the parse method
//    * @see nuggets.delegate.ASimpleDelegate#getUnmarshallString(String, java.lang.String)
//    */
//   public String getUnmarshallString(String className, String exp)
//   {
//      return CLASS_NAME+".parse("+exp+")";
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
//		try
//		{
//			return parse(value);
//		}
//		catch(Exception e)
//		{
//			throw new PersistenceException(e);
//		}
//	}
   
}

/* 
 * $Log$
 * Revision 1.10  2006/12/21 10:38:59  walczak
 * removed the unmarshall methods. not tested with reflection
 *
 * Revision 1.9  2006/12/20 22:55:56  walczak
 * Moved some classes represented as string to the reference representation.
 * Subclasses when serialized as a string loose their class identity with the old approach.
 *
 * Revision 1.8  2006/06/29 17:27:25  walczak
 * created a reflection delegate. alpha
 *
 *
 */