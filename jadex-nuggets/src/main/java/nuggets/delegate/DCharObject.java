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
public class DCharObject extends ASimpleDelegate
{

   /** 
    * @param clazz
    * @param asm 
    * @return the string stored in "v"
    * @throws Exception
    */
   public Object getInstance(Class clazz, IAssembler asm) throws Exception
   {
      return new Character(((String)asm.getAttributeValue("v")).charAt(0));
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
    * @param exp
 * @return unmarshal expressions for boolean and Boolean
    * @see nuggets.delegate.ASimpleDelegate#getUnmarshallString(String, java.lang.String)
    */
   public String getUnmarshallString(String className, String exp)
   {
      return "new Character(((String)"+exp+").charAt(0))";
   }
   
	/** 
	 * @param clazz
	 * @param value
	 * @return the boolean expression
	 * @see nuggets.delegate.ASimpleDelegate#unmarshall(java.lang.Class, java.lang.Object)
	 */
	public Object unmarshall(Class clazz, Object value)
	{
		return new Character(((String)value).charAt(0));
	}
}