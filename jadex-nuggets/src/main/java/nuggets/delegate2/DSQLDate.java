/*
 * DString.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 17, 2006.  
 * Last revision $Revision: 4686 $ by:
 * $Author: walczak $ on $Date: 2006-12-20 23:55:56 +0100 (Mi, 20 Dez 2006) $.
 */
package nuggets.delegate2;

import java.sql.Date;

import nuggets.IAssembler;
import nuggets.ICruncher;
import nuggets.delegate.DDate;


/** DString 
 * @author walczak
 * @since  Jan 17, 2006
 */
public class DSQLDate extends DDate
{
    /** 
     * @param clazz
     * @param asm
     * @return a new instance of this array
     * @throws Exception
     */
    public Object getInstance(Class clazz,IAssembler asm) throws Exception
   {
      return new Date(parse(asm.getAttributeValue("v")).getTime());
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
//  
//   /** 
//    * @param exp
// * @return a call to the parse method
//    * @see nuggets.delegate.ASimpleDelegate#getUnmarshallString(String, java.lang.String)
//    */
//   public String getUnmarshallString(String className, String exp)
//   {
//      return "new java.sql.Date("+CLASS_NAME+".parse("+exp+").getTime())";
//   }
}
