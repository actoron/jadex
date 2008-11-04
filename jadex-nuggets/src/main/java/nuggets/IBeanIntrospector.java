/*
 * IBeanIntrospector.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Mar 22, 2006.  
 * Last revision $Revision: 4130 $ by:
 * $Author: walczak $ on $Date: 2006-03-22 18:17:00 +0100 (Mi, 22 Mrz 2006) $.
 */
package nuggets;

import java.util.Map;

/** IBeanIntrospector 
 * @author walczak
 * @since  Mar 22, 2006
 */
public interface IBeanIntrospector
{

	/** 
	 * @param clazz
	 * @return an array of bean properties
	 */
	Map getBeanProperties(Class clazz);

}


/* 
 * $Log$
 * Revision 1.1  2006/03/22 17:17:00  walczak
 * added an reflective introspector
 *
 */