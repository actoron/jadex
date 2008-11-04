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
   public void persist(Object o, ICruncher mill)
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


/* 
 * $Log$
 * Revision 1.3  2006/12/20 22:55:56  walczak
 * Moved some classes represented as string to the reference representation.
 * Subclasses when serialized as a string loose their class identity with the old approach.
 *
 * Revision 1.2  2006/03/22 17:26:40  walczak
 * minor bugfix
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