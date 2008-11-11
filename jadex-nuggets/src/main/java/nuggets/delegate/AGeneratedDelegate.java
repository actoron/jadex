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
import nuggets.IDelegate;
import nuggets.IReader;
import nuggets.InstanceNotAvailableException;

/** DNugget 
 * @author walczak
 * @since  Jan 19, 2006
 */
public class AGeneratedDelegate extends ADelegate implements IDelegate
{

	/** 
	 * @param object
	 * @param asm
	 * @throws Exception
	 * @see nuggets.delegate.ADelegate#assemble(java.lang.Object, nuggets.IAssembler)
	 */
	public void assemble(Object object, IAssembler asm) throws Exception
	{
		IReader reader=asm.getReader();
		// set all other attributes
		int count=reader.getAttributeCount();
		for(int i=0; i<count; i++) {
			String attribute = reader.getAttributeName(i);
			if (attribute!=null) {
				String sid = reader.getAttributeValue(i);
				if (reader.isReferenceAttribute(i)) {
					try {
						set(object, attribute, asm.getValue(sid));
					} catch(InstanceNotAvailableException inae)  {
						asm.delay(new AttributeSetOperation(this, object, attribute, sid));
					}
				} else {
					set(object, attribute, sid);
				}
			}
		}
	}
	
	
}