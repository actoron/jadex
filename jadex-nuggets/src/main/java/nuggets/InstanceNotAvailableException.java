/*
 * InstanceNotAvailableException.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Feb 16, 2006.  
 * Last revision $Revision: 4064 $ by:
 * $Author: walczak $ on $Date: 2006-02-23 18:48:47 +0100 (Do, 23 Feb 2006) $.
 */
package nuggets;

/** InstanceNotAvailableException 
 * @author walczak
 * @since  Feb 16, 2006
 */
public class InstanceNotAvailableException extends PersistenceException
{

	/** 
	 * Constructor for InstanceNotAvailableException.
	 */
	public InstanceNotAvailableException()
	{
		super();
	}

	/** 
	 * Constructor for InstanceNotAvailableException.
	 * @param message
	 */
	public InstanceNotAvailableException(String message)
	{
		super(message);
	}

	/** 
	 * Constructor for InstanceNotAvailableException.
	 * @param cause
	 */
	public InstanceNotAvailableException(Throwable cause)
	{
		super(cause);
	}

}


/* 
 * $Log$
 * Revision 1.2  2006/02/23 17:46:25  walczak
 * LF
 *
 * Revision 1.1  2006/02/16 17:41:08  walczak
 * no reference to strings in Maps but a direct inclusion.
 *
 */