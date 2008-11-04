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


import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import nuggets.IAssembler;
import nuggets.ICruncher;


/**
 * DString
 *
 * @author walczak
 * @since Jan 17, 2006
 */
public class DCalendar extends ADelegate
{

	/**
	 * @param clazz
	 * @param asm
	 * @return the string stored in "v"
	 * @throws Exception
	 */
	public Object getInstance(Class clazz, IAssembler asm) throws Exception
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone((String)asm.getAttributeValue("timezone")));
		//cal.setTimeInMillis(Long.parseLong((String)asm.get("millis"))); // does only work since 1.4.
		cal.setTime(new Date(Long.parseLong((String)asm.getAttributeValue("millis"))));
		return cal;
	}


	/**
	 * @param o
	 * @param mill
	 * @see ASimpleDelegate#persist(Object, nuggets.ICruncher)
	 */
	public void persist(Object o, ICruncher mill)
	{
		  mill.startConcept(o);
		mill.put("timezone", ((Calendar)o).getTimeZone().getID());
		//mill.put("millis", Long.toString(((Calendar)o).getTimeInMillis())); // does only work since 1.4.
		mill.put("millis", Long.toString(((Calendar)o).getTime().getTime()));

	}

}

/*

 * $Log$
 * Revision 1.5  2006/02/17 12:48:54  walczak
 * yet even faster
 *
 * Revision 1.4  2006/02/16 17:41:08  walczak
 * no reference to strings in Maps but a direct inclusion.
 *
 * Revision 1.3  2006/02/14 12:04:38  braubach
 * *** empty log message ***
 *
 * Revision 1.2  2006/01/20 18:11:02  walczak
 * ------------------------
 *
 * Revision 1.1  2006/01/18 13:59:47  walczak
 * Introduced the nuggets package.
 *

 */