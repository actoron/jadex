/*
 * ReflectionGenerator.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jun 29, 2006.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets;

import java.util.Collection;
import java.util.Map;

import nuggets.delegate.AGeneratedDelegate;

/** ReflectionGenerator 
 * @author walczak
 * @since  Jun 29, 2006
 */
public class ReflectionGenerator implements IDelegateGenerator
{

	/** 
	 * Constructor for ReflectionGenerator.
	 */
	public ReflectionGenerator() {
		System.out.println("Using ReflectionGenerator for generating delegates.");
	}
	
	/** 
	 * @param clazz
	 * @param props
	 * @return a reflection delegate
	 * @see nuggets.IDelegateGenerator#generateDelegate(java.lang.Class, java.util.Map)
	 */
	public IDelegate generateDelegate(Class clazz, Map props, ClassLoader classloader)
	{
		return new ReflectionDelegate(clazz, props);
	}
	
	static class ReflectionDelegate extends AGeneratedDelegate {
		private final Map   props;
		private final BeanProperty[] bp;

		private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
		
		ReflectionDelegate(Class clazz, Map props) {
			this.props = props;
			Collection values=props.values();
			bp = (BeanProperty[])values.toArray(new BeanProperty[values.size()]);
		}
				
		/** 
		 * @param o
		 * @param cruncher
		 * @see nuggets.IDelegate#persist(java.lang.Object, nuggets.ICruncher)
		 */
		public void persist(Object o, ICruncher cruncher, ClassLoader classloader) 
		{
			persist_recursive(o, cruncher, bp.length, classloader);
		}

		/** 
		 * @param o
		 * @param cruncher
		 * @param i 
		 */
		private void persist_recursive(Object o, ICruncher c, int i, ClassLoader classloader)
		{
			if (i==0) {
				c.startConcept(o);
				return;
			} // else
			IDelegate del = PersistenceHelper.getDefaultDelegate(bp[--i].getType());
			if(del.isSimple())
			{
				persist_recursive(o, c, i, classloader);
				try
				{
					if(bp[i].getType().isPrimitive())
					{
		
							c.put(bp[i].getName(), del.marshall(bp[i].getType(), bp[i].getGetter().invoke(o, EMPTY_OBJECT_ARRAY)));
					}
					else
					{
						Object val= bp[i].getGetter().invoke(o, EMPTY_OBJECT_ARRAY);
						if (val!=null) c.put(bp[i].getName(), del.marshall(bp[i].getType(), val));
					}
				} catch(Exception e) {
					throw new PersistenceException(e);
				}
			}
			else
			{ 
				int id=0;
				try
				{
					id = c.declare(bp[i].getGetter().invoke(o, EMPTY_OBJECT_ARRAY), classloader);
				} catch(Exception e) {
					throw new PersistenceException(e);
				}
				persist_recursive(o, c, i, classloader);
				if (id!=0) c.put(bp[i].getName(), id);
			}
		}

		/** 
		 * @param object
		 * @param attribute
		 * @param value
		 * @throws Exception
		 * @see nuggets.IDelegate#set(java.lang.Object, java.lang.String, java.lang.Object)
		 */
		public void set(Object object, String attribute, Object value) throws Exception
		{
			BeanProperty bp=(BeanProperty)props.get(attribute);
			if (bp==null) throw new PersistenceException("No such attribute: " + attribute); 
			
			Class param_clazz = bp.getSetterType();
			IDelegate del = PersistenceHelper.getDefaultDelegate(param_clazz);
			bp.getSetter().invoke(object, new Object[] {del.unmarshall(param_clazz, value)});
		}


		
	}
	
	
	
}


/* 
 * $Log$
 * Revision 1.3  2006/07/14 10:29:23  walczak
 * sysout
 *
 * Revision 1.2  2006/07/03 09:11:11  walczak
 * created a reflection delegate. beta
 *
 * Revision 1.1  2006/06/29 17:27:25  walczak
 * created a reflection delegate. alpha
 *
 */