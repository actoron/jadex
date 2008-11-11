/*
 * IDelegatGenerator.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jun 29, 2006.  
 * Last revision $Revision: 4401 $ by:
 * $Author: walczak $ on $Date: 2006-06-29 19:27:25 +0200 (Do, 29 Jun 2006) $.
 */
package nuggets;

import java.util.Map;

/** IDelegatGenerator 
 * @author walczak
 * @since  Jun 29, 2006
 */
public interface IDelegateGenerator
{

	/** 
	 * @param clazz
	 * @param props
	 * @return a delegate for this class
	 */
	IDelegate generateDelegate(Class clazz, Map props, ClassLoader classloader);

}
