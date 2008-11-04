/*
 * DNugget.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 19, 2006.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets.delegate;

import nuggets.AttributeSetOperation;
import nuggets.IAssembler;
import nuggets.ICruncher;
import nuggets.IDelegate;
import nuggets.INugget;
import nuggets.IReader;
import nuggets.InstanceNotAvailableException;

/** DNugget 
 * @author walczak
 * @since  Jan 19, 2006
 */
public class DNugget extends ADelegate implements IDelegate
{

	/** 
	 * @param o
	 * @param mill
	 * @see nuggets.IDelegate#persist(java.lang.Object, nuggets.ICruncher)
	 */
	public void persist(Object o, ICruncher mill)
	{
		((INugget)o)._persist(mill);
	}

	/** 
	 * @param o
	 * @param attribute
	 * @param value
	 * @see nuggets.delegate.ADelegate#set(java.lang.Object, java.lang.String, java.lang.Object)
	 */
	public void set(Object o, String attribute, Object value)
	{
		((INugget)o)._set(attribute, value);
	}

	/** 
	 * @param object
	 * @param asm
	 * @throws Exception
	 * @see nuggets.delegate.ADelegate#assemble(java.lang.Object, nuggets.IAssembler)
	 */
	public void assemble(Object object, IAssembler asm) throws Exception
	{
		INugget nugget=(INugget)object;
		IReader reader=asm.getReader();
		// set all other attributes
		int count=reader.getAttributeCount();
		for(int i=0; i<count; i++) {
			String attribute = reader.getAttributeName(i);
			if (attribute!=null) {
				String sid = reader.getAttributeValue(i);
				if (reader.isReferenceAttribute(i)) {
					try {
						nugget._set(attribute, asm.getValue(sid));
					} catch(InstanceNotAvailableException inae)  {
						asm.delay(new AttributeSetOperation(this, object, attribute, sid));
					}
				} else {
					nugget._set(attribute, sid);
				}
			}
		}
	}
	
	
}


/* 
 * $Log$
 * Revision 1.6  2006/02/23 17:46:25  walczak
 * LF
 *
 * Revision 1.5  2006/02/17 12:48:54  walczak
 * yet even faster
 *
 * Revision 1.4  2006/02/16 17:41:08  walczak
 * no reference to strings in Maps but a direct inclusion.
 *
 * Revision 1.3  2006/02/15 10:42:31  walczak
 * removed StringBuffer from XMLReader,
 * removed "new" from XMLReadr,
 * made generated Delegates use set method,
 *
 * Revision 1.2  2006/02/14 17:39:05  walczak
 * new version of nuggets
 *
 * Revision 1.1  2006/01/20 18:11:01  walczak
 * ------------------------
 *
 */