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

/** DString 
 * @author walczak
 * @since  Jan 17, 2006
 */
public class DChar extends ASimpleDelegate
{
   /** 
    * @param exp
 * @return unmarshal expressions for char
    * @see nuggets.delegate.ASimpleDelegate#getUnmarshallString(String, java.lang.String)
    */
   public String getUnmarshallString(String className, String exp)
   {
      return "((String)"+exp+").charAt(0)";
   }
   
	/** 
	 * @param clazz
	 * @param value
	 * @return the boolean expression
	 * @see nuggets.delegate.ASimpleDelegate#unmarshall(java.lang.Class, java.lang.Object)
	 */
	public Object unmarshall(Class clazz, Object value)
	{
		return Character.valueOf(((String)value).charAt(0));
	}
}
