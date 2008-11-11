/*
 * DMap.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Dec 5, 2005.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets.delegate;

import java.util.Iterator;
import java.util.Properties;

import nuggets.IAssembler;
import nuggets.ICruncher;
import nuggets.IReader;


/** DMap 
 * @author walczak
 * @since  Dec 5, 2005
 */
public class DProperties extends  ADelegate {

   /** 
    * @param o
    * @param mill
    * @see nuggets.delegate.ASimpleDelegate#persist(java.lang.Object, nuggets.ICruncher)
    */
   public void persist(Object o, ICruncher mill, ClassLoader classloader)
   {
		  mill.startConcept(o);
        final Properties props = (Properties) o;
        final Iterator it = props.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            mill.put(key, props.getProperty(key));
        }
   }

	/** 
	 * @param clazz
	 * @param asm
	 * @return a new instance of this array
	 * @throws Exception
	 */
	public Object getInstance(Class clazz,IAssembler asm) throws Exception
   {
      return new Properties();
   }



	/** 
	 * @param object
	 * @param asm
	 * @throws Exception
	 * @see nuggets.delegate.ADelegate#assemble(java.lang.Object, nuggets.IAssembler)
	 */
	public void assemble(Object object, IAssembler asm) throws Exception
	{

		Properties props=(Properties)object;
		IReader reader=asm.getReader();
		// set all other attributes
		int count=reader.getAttributeCount();
		for(int i=0; i<count; i++) {
			String attribute = reader.getAttributeName(i);
			if (attribute!=null) {
					props.setProperty(attribute, reader.getAttributeValue(i));
			}
		}
	}
	
	
   
	
}
