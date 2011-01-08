/*
 * JaninoGenerator.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jun 29, 2006.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets;

import java.io.StringReader;
import java.util.Map;

import nuggets.delegate.AGeneratedDelegate;

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.Scanner;


/** JaninoGenerator 
 * @author walczak
 * @since  Jun 29, 2006
 */
public class JaninoGenerator implements IDelegateGenerator
{
	static final String				ICRUNCHER_INTERFACE		= ICruncher.class.getName();

	static final String				IASSEMBLER_INTERFACE	= IAssembler.class.getName();

	/** 
	 * Constructor for JaninoGenerator.
	 */
	public JaninoGenerator()
	{
		System.out.println("Using JaninoGenerator for generating delegates.");
	}
	
	/** 
	 * @param clazz
	 * @param props
	 * @return a compiled delegate
	 * @see nuggets.IDelegateGenerator#generateDelegate(java.lang.Class, java.util.Map)
	 */
	public IDelegate generateDelegate(Class clazz, Map props, ClassLoader classloader)
	{
		
		String classbody = createDelegateClassBody(clazz, props);
//		System.out.println("BeanAnalyzer: generating delegate for class "+clazz.getName() +"\n"+classbody);
//		System.out.println("cl: "+Thread.currentThread().getContextClassLoader());
		try
		{
			return (IDelegate)ClassBodyEvaluator.createFastClassBodyEvaluator(new Scanner(
				"<generated>", new StringReader(classbody)), "persistence.Delegate"
				+ clazz.getName(), AGeneratedDelegate.class, new Class[]{IDelegate.class}, classloader);
		}
		catch(Exception e)
		{
			throw new PersistenceException(e);
		}
	}
	
	/** 
	 * @param clazz
	 * @return the class body definition for janino compiler
	 */
	private static String createDelegateClassBody(Class clazz, Map props)
	{
		try
		{
			String clazzname = clazz.getName();
			String classbody = "";

			// calculate perfect hash
			int size = props.size();
			String[] keys = (String[])props.keySet().toArray(new String[size]);

			String persist_body = "";
			String reference_body = "";
			int ref_counter = 0;
			for(int k = 0; k < size; k++)
			{
				BeanProperty bp = (BeanProperty)props.get(keys[k]);
				Class getter_type = bp.getGetterType();
				IDelegate getter_del = PersistenceHelper.getDefaultDelegate(getter_type);
				if(getter_del.isSimple())
				{
					if(getter_type.isPrimitive())
					{
						persist_body += "   c.put(\"" + bp.getName() + "\","
								+ getter_del.getMarshallString("x." + bp.getGetterName() + "()")
								+ ");\n";
					}
					else
					{
						persist_body += "   {" + getClassName(getter_type) + " t = x."
								+ bp.getGetterName() + "(); " + "if (t!=null) c.put(\""
								+ bp.getName() + "\"," + getter_del.getMarshallString("t")
								+ ");}\n";
					}
				}
				else
				{ // all primitive classes can be represented as a string
					ref_counter++;
					reference_body += "   int id" + ref_counter + " = c.declare(x."
							+ bp.getGetterName() + "()"+", classloader"+");\n";

					persist_body += "   if (id" + ref_counter + "!=0) c.put(\"" + bp.getName()
							+ "\", id" + ref_counter + ");\n";
				}
			}


			// ---------------------- persist -------------------------------------
			classbody += "\npublic void persist(Object o, " + ICRUNCHER_INTERFACE
					+ " c"+", ClassLoader classloader"+") throws Exception {\n" + "   " + clazzname + " x=(" + clazzname + ")o;\n"
					+ reference_body + "   c.startConcept(o);\n" + persist_body + "}\n";

			//----------------------- put ---------------------------
			classbody += "\npublic void set(Object o, String attribute, Object value) throws Exception {\n   "
					+ clazzname + " x=(" + clazzname + ")o;\n";

			if(size > 1)
			{
				// choose the hash method
				classbody += "   switch(hash(attribute)) {\n";
				PerfectHash ph = new PerfectHash(keys);

				keys = ph.getKeys();

				// write the switch case construct
				for(int k = 0; k < keys.length; k++)
				{
					String name = keys[k];
					if(name != null)
					{
						BeanProperty bp = (BeanProperty)props.get(name);
						classbody += "   case " + k + ":";
						Class param_clazz = bp.getSetterType();
						IDelegate del = PersistenceHelper.getDefaultDelegate(param_clazz);
						classbody += " x." + bp.getSetterName() + "("
								+ del.getUnmarshallString(getClassName(param_clazz), "value")
								+ "); return; \n";

					}
				}

				classbody += "}}\n";

				// perfect hash method 
				classbody += ph.getHashMethodString();

			}
			else if(size == 1)
			{
				// simple method
				BeanProperty bp = (BeanProperty)props.get(keys[0]);
				Class param_clazz = bp.getSetterType();
				IDelegate del = PersistenceHelper.getDefaultDelegate(param_clazz);
				classbody += "   x." + bp.getSetterName() + "("
						+ del.getUnmarshallString(getClassName(param_clazz), "value") + ");\n}\n";
			}
			else
			{
				classbody += "}\n";
			}


			//			----------------------- getInstance -------------------------------         
			classbody += "\npublic Object getInstance(Class clazz, " + IASSEMBLER_INTERFACE
					+ " asm) throws Exception {\n" + "   return new " + clazzname + "();\n}\n";

			System.out.println("class: "+classbody);
			
			return classbody;

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}


	/** Respects arrays
	 * @param ret
	 * @return the name of this class
	 */
	private static String getClassName(Class ret)
	{
		if(ret.isArray()) return getClassName(ret.getComponentType()) + "[]";
		return ret.getName();
	}

}
