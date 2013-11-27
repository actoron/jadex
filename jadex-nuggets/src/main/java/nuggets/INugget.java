/*
 * INugget.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Dec 2, 2005.  
 * Last revision $Revision: 4002 $ by:
 * $Author: walczak $ on $Date: 2006-02-14 18:39:05 +0100 (Di, 14 Feb 2006) $.
 */
package nuggets;

import nuggets.delegate.DDate;


/** INugget - a simple interface for all beans that implement their own persistence methods
 * @author walczak
 * @since  Dec 2, 2005
 */
public interface INugget 
{
   
   /** This is the main persistance method. 
    * @param cruncher
    */
   void _persist(ICruncher cruncher);


	/** This method is called after, whenever there was a reference, which could not be resolved at
	 * the first pass of the parser.
	 * @param attribute
	 * @param value
	 */
	void _set(String attribute, Object value);
	
	
//	/** Helper 
//	 * @author walczak
//	 * @since  Jan 19, 2006
//	 */
//	public static class Helper {
//		
//		/** 
//		 * @param date
//		 * @return this date to string
//		 */
//		public static String toString(java.util.Date date) {
//			return DDate.format(date);
//		}
//		
//		/** 
//		 * @param string
//		 * @return the date from representation
//		 */
//		public static java.util.Date parseDate(Object string) {
//			try
//			{	if (string==null) return null;
//				return DDate.parse(string);
//			}
//			catch(Exception e)
//			{
//				throw new PersistenceException(e);
//			}
//		}	
//	}
}


/* 
 * $Log$
 * Revision 1.2  2006/02/14 17:39:05  walczak
 * new version of nuggets
 *
 * Revision 1.1  2006/01/20 18:11:02  walczak
 * ------------------------
 *
 * Revision 1.1  2006/01/18 13:59:47  walczak
 * Introduced the nuggets package.
 *
 */