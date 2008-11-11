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

import java.net.InetAddress;
import java.net.UnknownHostException;

import nuggets.IAssembler;
import nuggets.ICruncher;
import nuggets.PersistenceException;


/** DString 
 * @author walczak
 * @since  Jan 17, 2006
 */
public class DInetAddress extends ASimpleDelegate
{

	/** 
	 * @param clazz
	 * @param asm
	 * @return a new instance of this array
	 * @throws Exception
	 */
	public Object getInstance(Class clazz,IAssembler asm) throws Exception
   {
      return InetAddress.getByName((String)asm.getAttributeValue("v"));
   }

   /** 
    * @param o
    * @param mill
    * @see nuggets.delegate.ASimpleDelegate#persist(java.lang.Object, nuggets.ICruncher)
    */
   public void persist(Object o, ICruncher mill, ClassLoader classloader)
   {
		  mill.startConcept(o);
      mill.put("v", ((InetAddress)o).getHostAddress());
   }

   /** 
    * @param exp
 * @return "InetAddress.getByName((String)"+exp+")";
    * @see nuggets.delegate.ASimpleDelegate#getUnmarshallString(String, java.lang.String)
    */
   public String getUnmarshallString(String className, String exp)
   {
      return "java.net.InetAddress.getByName((String)"+exp+")";
   }

   /** 
    * @param exp
 * @return exp+".getHostAddress()";
    * @see nuggets.delegate.ASimpleDelegate#getMarshallString(java.lang.String)
    */
   public String getMarshallString(String exp)
   {
      return exp+".getHostAddress()";
   }
   
	/** 
	 * @param clazz
	 * @param o
	 * @return the name of the class
	 * @see nuggets.delegate.ADelegate#marshall(java.lang.Class, java.lang.Object)
	 */
	public String marshall(Class clazz, Object o)
	{
		return ((InetAddress)o).getHostName();
	}
     
	/** 
	 * @param clazz
	 * @param value
	 * @return the boolean expression
	 * @see nuggets.delegate.ASimpleDelegate#unmarshall(java.lang.Class, java.lang.Object)
	 */
	public Object unmarshall(Class clazz, Object value)
	{
		try
		{
			return InetAddress.getByName((String)value);
		}
		catch(UnknownHostException e)
		{
			throw new PersistenceException(e);
		}
	}
   
}


/* 
 * $Log$
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
 * Revision 1.2  2006/01/20 18:11:02  walczak
 * ------------------------
 *
 * Revision 1.1  2006/01/18 13:59:47  walczak
 * Introduced the nuggets package.
 *
 */