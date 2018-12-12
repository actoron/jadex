package jadex.javaparser.javaccimpl;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jadex.commons.IPropertyObject;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.javaparser.IMapAccess;


/**
 *  A node for a constructor or method invocation or field access.
 */
// Todo: Allow conversions between basic number types.
public class ReflectNode	extends ExpressionNode
{
	//-------- constants --------

	/** The constructor type. */
	public static final int	CONSTRUCTOR	= 1;

	/** The static method type. */
	public static final int	STATIC_METHOD	= 2;

	/** The static field type. */
	public static final int	STATIC_FIELD	= 3;

	/** The method type. */
	public static final int	METHOD	= 4;

	/** The field type. */
	public static final int	FIELD	= 5;

	//-------- attributes --------

	/** The reflect node type. */
	protected int type;

	//-------- precomputed values --------

	/** The clazz. */
	protected Class	clazz;

	/** The argument types (for constructors and methods). */
	protected transient Class[]	argtypes;

	/** The argument values (for constructors and methods). */
	protected transient Object[]	args;

	/** The possible constructors (for constructor nodes). */
	protected transient Constructor[]	constructors;

	/** The dynamically reloaded class (currently only for constructor nodes). */
	protected transient Class	reloadedclass;
	
	/** The possible methods (for static and nonstatic methods). */
	protected transient Method[]	methods;

	/** The field accessor method (for static and nonstatic fields). */
	protected transient Method	accessor;

	/** The field (for static and nonstatic fields). */
	protected transient Field	field;

	/** Flag indicating that this node is a candidate dynamic class reloading
	 *  (currently only supported for plan constructors). */
	protected boolean	reloadable;

	//-------- constructors --------

	/**
	 *  Create an expression node.
	 *  @param p	The parser.
	 *  @param id	The id.
	 */
	public ReflectNode(ParserImpl p, int id)
	{
		super(p, id);
	}

	//-------- attribute accessors --------

	/**
	 *  Set the constructor type.
	 *  @param type	The constrcutor type.
	 */
	public void	setType(int type)
	{
		this.type	= type;
	}

	/**
	 *  Get the constructor type.
	 *  @return The constructor type.
	 */
	public int	getType()
	{
		return this.type;
	}

	//-------- evaluation --------

	/**
	 *  Precompute the set of matching constructors if possible.
	 */
	public void precompile()
	{
		// Check number of children.
		if(	(type==CONSTRUCTOR || type==STATIC_METHOD || type==METHOD)
			&& !(jjtGetNumChildren()==2)
		||	(type==STATIC_FIELD || type==FIELD)
			&& !(jjtGetNumChildren()==1))
		{
			throw new ParseException("Wrong number of child nodes: "+this);
		}
		else if(type!=CONSTRUCTOR && type!=STATIC_METHOD && type!=METHOD
			&& type!=STATIC_FIELD && type!=FIELD)
		{
			throw new ParseException("Unknown node type "+type+": "+this);
		}

		// Get child nodes.
		int	child=0;
		ExpressionNode	type_or_value	=  (ExpressionNode)jjtGetChild(child++);
		ExpressionNode	argsnode	= jjtGetNumChildren()==child ? null
			: (ExpressionNode)jjtGetChild(child);

		// Determine class and reference value for nonstatic members.
		if(type==METHOD || type==FIELD)
		{
			clazz	= type_or_value.getStaticType()!=null ? type_or_value.getStaticType() : null;
		}

		// Determine class for static members.
		else if(type_or_value!=null && type_or_value.isConstant())
		{
			try
			{
				clazz	= (Class)type_or_value.getValue(null);
			}
			catch(Exception e)
			{
			}
		}

		if(clazz!=null && (clazz.getModifiers() & Modifier.PUBLIC)==0)
		{
			throw new ParseException("Cannot access member of nonpublic class: "+clazz);
		}
		
		// Precompute argument types and values.
		if(type==CONSTRUCTOR || type==STATIC_METHOD || type==METHOD)
		{
			this.argtypes	= new Class[argsnode.jjtGetNumChildren()];
			this.args	= new Object[argtypes.length];
			for(int i=0; i<argtypes.length; i++)
			{
				ExpressionNode	node	= (ExpressionNode)argsnode.jjtGetChild(i);
				argtypes[i]	= node.getStaticType()!=null ? node.getStaticType() : null;
				if(node.isConstant())
				{
					try
					{
						args[i]	= node.getValue(null);
					}
					catch(Exception e)
					{
					}
				}
			}

			// Find available constructors
			if(type==CONSTRUCTOR && clazz!=null)
			{
				this.setStaticType(clazz);
				this.constructors	= findConstructors(clazz, argtypes);
				if(constructors.length==0)
				{
					throw new ParseException("No constructor found for: "+clazz
						+SUtil.arrayToString(argtypes));
				}
				
//				// Set node to reloadable if this is a plan constructor.
//				if(SReflect.isSupertype(AbstractPlan.class, clazz))
//				{
//					this.reloadable	= true;
//				}
			}

			// Find available methods
			else if((type==STATIC_METHOD || type==METHOD) && clazz!=null)
			{
				this.methods	= findMethods(clazz, argtypes);
				if(methods.length==0)
				{
					throw new ParseException("No "+getText()+" method found for: "+clazz
						+SUtil.arrayToString(argtypes));
				}
				else
				{					
					// Determine return type, if unique.
					Class	retype	= null;
					for(int i=0; i<methods.length; i++)
					{
						if(i==0)
						{
							retype	= methods[i].getReturnType();
						}
						else if(retype!=methods[i].getReturnType())
						{
							retype	= null;
						}
					}
					if(retype!=null)
						setStaticType(SReflect.getWrappedType(retype));
				}
			}
		}

		// Find field
		else if((type==STATIC_FIELD || type==FIELD) && clazz!=null)
		{
			// Find field. Handle ".class" specially.
			if(type==STATIC_FIELD && getText().equals("class"))
			{
				this.setStaticType(Class.class);
				this.setConstantValue(clazz);
				this.setConstant(true);
			}
			// Handle ".length" of arrays specially (Java Bug???).
			else if(type==FIELD && clazz.isArray() && getText().equals("length"))
			{
				this.setStaticType(Integer.class);
				if(type_or_value.isConstant())
				{
					try
					{
						Object	array	= type_or_value.getValue(null);
						this.setConstantValue(Integer.valueOf(Array.getLength(array)));
						this.setConstant(true);
					}
					catch(Exception e)
					{
					}
				}
			}
			else
			{
				try
				{
					this.field	= SReflect.getCachedField(clazz, getText());
					//this.field	= clazz.getField(getText());
					this.setStaticType(SReflect.getWrappedType(field.getType()));

					// Check if static modifier matches.
					if(type==STATIC_FIELD &&!Modifier.isStatic(field.getModifiers()))
					{
						throw new ParseException("Static reference to nonstatic field :"+this);
					}

					// For final fields precompute value.
					if(Modifier.isFinal(field.getModifiers()))
					{
						try
						{
							if(Modifier.isStatic(field.getModifiers()))
							{
								this.setConstantValue(field.get(null));
								this.setConstant(true);
							}
							else if(type_or_value.isConstant())
							{
								try
								{
									Object value	= type_or_value.getValue(null);
									if(value!=null)
									{
										this.setConstantValue(field.get(value));
										this.setConstant(true);
									}
									else
									{
										throw new ParseException("Cannot reference nonstatic field of null value: "+this);
									}
								}
								catch(ParseException e)
								{
									throw e;
								}
								catch(Exception e)
								{
								}
							}
						}
						catch(IllegalAccessException e)
						{
							throw new ParseException("Nonpublic field cannot be accessed: "+this);
						}
					}
				}
				catch(NoSuchFieldException e)
				{
//					// Try bean helper.
//					Class type = BeanHelper.getPropertyClass(clazz, getText());
//
//					if(type!=null && type_or_value.isConstant())
//					{
//						Object value	= type_or_value.getValue(null);
//						if(value!=null)
//						{
//							try
//							{
//								this.setConstantValue(BeanHelper.getPropertyValue(value, getText()));
//								this.setConstant(true);
//							}
//							catch(IntrospectionException ie)
//							{
//								throwParseException(ie);
//							}
//						}
//						else
//						{
//							throw new ParseException("Cannot reference nonstatic field of null value: "+this);
//						}
//					}

					// Try map accessor.
					boolean	found	= false;
					if(accessor==null && SReflect.isSupertype(Map.class, clazz))
					{
						try
						{
							accessor	= clazz.getMethod("get", new Class[]{Object.class});
							args	= new Object[]{getText()};
							found	= true;
						}
						catch(NoSuchMethodException e2)
						{
						}
						catch(SecurityException e2)
						{
						}
					}

					// If not found, throw original exception.
					if(!found)
					{
						throwParseException(e);
					}
//					else
//					{
//						this.setStaticType(SReflect.getWrappedType(type));
//					}
				}
			}
		}
	}


	/**
	 *  Evaluate the expression in the given state
	 *  with respect to given parameters.
	 * @param params	The parameters (string, value pairs), if any.
	 *  @return	The value of the term.
	 */
	public Object	getValue(IValueFetcher fetcher)	//throws Exception
	{
		// Return constant value if available.
		if(isConstant())
			return getConstantValue();

		// Get child nodes.
		int	child=0;
		ExpressionNode	type_or_value	= (ExpressionNode)jjtGetChild(child++);
		ExpressionNode	argsnode	= jjtGetNumChildren()==child ? null
			: (ExpressionNode)jjtGetChild(child);
		Object	value	= null;

		if(type==CONSTRUCTOR || type==STATIC_METHOD || type==METHOD)
		{
			// Instantiate arguments (make copy of precomputed values).
			Object[]	args	= new Object[argsnode.jjtGetNumChildren()];
			if(this.args!=null)
				System.arraycopy(this.args, 0, args, 0, args.length);
			for(int i=0; i<args.length; i++)
			{
				if(args[i]==null)
				{
					args[i]	= ((ExpressionNode)argsnode.jjtGetChild(i)).getValue(fetcher);
				}
			}

			// Determine argument types (make copy of precomputed types).
			Class[]	argtypes	= new Class[argsnode.jjtGetNumChildren()];
			if(this.argtypes!=null)
				System.arraycopy(this.argtypes, 0, argtypes, 0, argtypes.length);
			for(int i=0; i<argtypes.length; i++)
			{
				if(argtypes[i]==null && args[i]!=null)
				{
					argtypes[i]	= args[i].getClass();
				}
			}

			// Handle constructor nodes.
			if(type==CONSTRUCTOR)
			{
				try
				{
					value	= invokeConstructor((Class)type_or_value
						.getValue(fetcher), argtypes, args);
				}
				catch(Exception e)
				{
//					e.printStackTrace();
					if(e instanceof RuntimeException)
					{
						throw (RuntimeException)e;
					}
					else
					{
						throw new RuntimeException(e);
					}
				}
			}

			// Handle method nodes.
			else if(type==METHOD || type==STATIC_METHOD)
			{
				Object	ref	= null;
				if(type==STATIC_METHOD && clazz==null)
				{
					clazz	= (Class)type_or_value.getValue(fetcher);
					// todo: find public superclass.
				}
				else
				{
					ref	= type_or_value.getValue(fetcher);
					if(ref==null)
					{
						throw new NullPointerException("Cannot invoke nonstatic method on null value: "+this);
					}
					else if(clazz==null)
					{
						clazz	= ref.getClass();
						// todo: find public superclass.
					}
				}
				try
				{
					value	= invokeMethod(ref, clazz, argtypes, args);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw SUtil.throwUnchecked(e);
				}
			}
		}
		else if(type==FIELD || type==STATIC_FIELD)
		{
			// Get object to get the field from (if any).
			Object	ref	= type==STATIC_FIELD ? null
				: type_or_value.getValue(fetcher);

			// Handle ".class" specially.
			if(type==STATIC_FIELD && getText().equals("class"))
			{
				value	= type_or_value.getValue(fetcher);
			}

			// Handle ".length" of arrays specially (Java Bug???).
			else if(type==FIELD && ref!=null && ref.getClass().isArray() && getText().equals("length"))
			{
				value	= Integer.valueOf(Array.getLength(ref));
 			}
			
			// Handle IPropertyObject
			else if(type==FIELD && ref instanceof IPropertyObject && ((IPropertyObject)ref).hasProperty(getText()))
			{
				value = ((IPropertyObject)ref).getProperty(getText());
			}

			// Handle normal fields.
			else
			{
				// Determine class.
				if(type!=STATIC_FIELD && ref==null)
				{
					throw new NullPointerException("Cannot reference nonstatic field of null value: "+this+", "+type_or_value.getValue(fetcher));
				}
				else if(clazz==null)
				{
					clazz	= type==STATIC_FIELD ? (Class)type_or_value.getValue(fetcher) : ref.getClass();
					// todo: find public superclass.
				}

				try
				{
					value	= accessField(ref, clazz, fetcher);
				}
				catch(Exception e)
				{
					throw new RuntimeException(""+this, e);
				}
			}
		}

		return value;
	}

	/**
	 *  Create a string representation of this node and its subnodes.
	 *  @return A string representation of this node.
	 */
	public String toPlainString()
	{
		if(type==CONSTRUCTOR)
			return "new " + jjtGetChild(0).toPlainString() + jjtGetChild(1).toPlainString();
		else if(type==METHOD || type==STATIC_METHOD)
			return jjtGetChild(0).toPlainString() + "." + getText() + jjtGetChild(1).toPlainString();
		else //if(type==FIELD || type==STATIC_FIELD)
			return jjtGetChild(0).toPlainString() + "." + getText();
	}

	//-------- helper methods --------

	/**
	 *  Find all matching constructors of a given class.
	 *  @param clazz	The class.
	 *  @param argtypes	The argument types.
	 *  @return	The matched constructors.
	 */
	protected Constructor[]	findConstructors(Class clazz, Class[] argtypes)
	{
		// Find matching signatures from available options.
		Constructor[] cs	= clazz.getConstructors();
		Class[][]	paramtypes	= new Class[cs.length][];
		for(int i=0; i<cs.length; i++)
		{
			paramtypes[i]	= cs[i].getParameterTypes();
		}
		int[]	matches	= SReflect.matchArgumentTypes(argtypes, paramtypes);

		// Store matched constructors.
		Constructor[]	constructors	= new Constructor[matches.length];
		for(int i=0; i<matches.length; i++)
		{
			constructors[i]	= cs[matches[i]];
		}

		return constructors;
	}

	/**
	 *  Find all matching methods of a given class.
	 *  @param clazz	The class.
	 *  @param argtypes	The argument types.
	 *  @return	The matched methods.
	 */
	protected Method[]	findMethods(Class clazz, Class[] argtypes)
	{
		// Find named methods.
		Method[] ms	= SReflect.getMethods(clazz, getText());
		ArrayList ames	= new ArrayList();
		for(int i=0; i<ms.length; i++)
		{
			// Matches static modifier.
			if(type!=STATIC_METHOD || Modifier.isStatic(ms[i].getModifiers()))
			{
				ames.add(ms[i]);
			}
		}
		ms	= (Method[])ames.toArray(new Method[ames.size()]);

		// Find matching signatures from available options.
		Class[][]	paramtypes	= new Class[ms.length][];
		for(int i=0; i<ms.length; i++)
		{
			paramtypes[i]	= ms[i].getParameterTypes();
		}
		int[]	matches	= SReflect.matchArgumentTypes(argtypes, paramtypes);

		// Return matched methods.
		Method[]	methods	= new Method[matches.length];
		for(int i=0; i<matches.length; i++)
		{
			methods[i]	= ms[matches[i]];
		}
		return methods;
	}

	/**
	 *  Find and invoke a constructor.
	 *  @param clazz	The class to instantiate.
	 *  @param argtypes	The actual argument types.
	 *  @param args	The actual argument values.
	 *  @return	The instantiated object.
	 * @throws Exception 
	 */
	protected Object	invokeConstructor(Class clazz, Class[] argtypes, Object[] args) throws Exception
	{
		// Reload, when class is reloadable.
//		if(reloadable && Configuration.getConfiguration().isJavaCCPlanReloading())
//		{
//			// Remember reloaded class for next access (otherwise original class would be used next time).
//			// Hack!!! Should be remembered by class loader?
//			// Should update type node ???
//			reloadedclass	= DynamicURLClassLoader.loadModifiedClassWithInstance(
//				reloadedclass!=null ? reloadedclass : clazz);
//
//			// On change, remove constructor cache.
//			if(reloadedclass!=clazz)
//			{
//				clazz	= reloadedclass;
//				constructors	= null;
//			}
//		}
		
		// Find matching signature from available options.
		Constructor	con	= null;
		if(constructors==null)
		{
			Constructor[]	constructors	= findConstructors(clazz, argtypes);
			if(constructors.length>0)
			{
				con	= constructors[0];
			}
			else
			{
				throw new ParseException("No constructor found for: "+clazz
					+SUtil.arrayToString(argtypes));
			}
		}
		else
		{
			// Match precomputed options against actual argument types.
			Class[][]	paramtypes	= new Class[constructors.length][];
			for(int i=0; i<constructors.length; i++)
			{
				paramtypes[i]	= constructors[i].getParameterTypes();
			}
			int[]	matches	= SReflect.matchArgumentTypes(argtypes, paramtypes);

			if(matches.length>0)
			{
				con	= constructors[matches[0]];
			}
			else
			{
				throw new RuntimeException("No constructor found for "+clazz
					+SUtil.arrayToString(argtypes));
			}
		}

		// Try to invoke constructor.
		Object	ret	= null;
		try
		{
			ret	= con.newInstance(SReflect.fillArguments(args, con.getParameterTypes()));
		}
		catch(InvocationTargetException e)
		{
			if(e.getTargetException() instanceof Exception)
				throw (Exception)e.getTargetException();
			else
				throw e;
		}
//		catch(IllegalArgumentException e)
//		{
//			e.printStackTrace();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		return ret;
	}

	/**
	 *  Find and invoke a method.
	 *  @param ref	The object on which to invoke (may be null for static methods).
	 *  @param clazz	The class to instantiate.
	 *  @param argtypes	The actual argument types.
	 *  @param args	The actual argument values.
	 *  @return	The return value.
	 * @throws Exception 
	 */
	protected Object	invokeMethod(Object ref, Class clazz,
		Class[] argtypes, Object[] args) throws Exception
	{
		// Find matching signature from available options.
		Method	method	= null;
		if(methods==null)
		{
			Method[]	methods	= findMethods(clazz, argtypes);
			if(methods.length>0)
			{
				method	= methods[0];
			}
			else
			{
				throw new ParseException("No method found for term "+this+": " + clazz
					+ " " + getText() + SUtil.arrayToString(argtypes));
			}
		}
		else
		{
			// Match precomputed options against actual argument types.
			Class[][]	paramtypes	= new Class[methods.length][];
			for(int i=0; i<methods.length; i++)
			{
				paramtypes[i]	= methods[i].getParameterTypes();
			}
			int[]	matches	= SReflect.matchArgumentTypes(argtypes, paramtypes);

			if(matches.length>0)
			{
				method	= methods[matches[0]];
			}
			else
			{
				throw new ParseException("No method found for: " + clazz
					+ " " + getText() + SUtil.arrayToString(argtypes));
			}
		}

		Method	invmeth	= getMethodForMethod(method);
		if(invmeth==null)
		{
			throw new ParseException("Method '"+method.getName()+"' declared on nonpublic class.");
		}
		
		// Try to invoke method.
		Object	ret	= null;
		try
		{
			ret	= invmeth.invoke(ref, SReflect.fillArguments(args, invmeth.getParameterTypes()));
		}
		catch(InvocationTargetException e)
		{
			if(e.getTargetException() instanceof Exception)
				throw (Exception)e.getTargetException();
			else
				throw e;
		}
		return ret;
	}

	/**
	 *  Access a field.
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchFieldException 
	 */
	protected Object	accessField(Object ref, Class clazz, IValueFetcher fetcher) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException
	{
		boolean	fetched	= false;
		Object val = null;
		
//		// When nothing precomputed, try fetcher first.
//		if(field==null && accessor==null)
//		{
//			try
//			{
//				val	= fetcher.fetchValue(getText(), ref);
//				fetched	= true;
//			}
//			catch(Exception e)
//			{
////				e.printStackTrace();
//			}
//		}

		// Find field if not precomputed.
		Field	field0	= this.field;
		NoSuchFieldException	nsfe	= null;
		if(!fetched && field0==null)
		{
			try
			{
				field0	= SReflect.getCachedField(clazz, getText());

				// Check if static modifier matches.
				if(type==STATIC_FIELD &&!Modifier.isStatic(field0.getModifiers()))
				{
					throw new RuntimeException("Static reference to nonstatic field :"+this);
				}
			}
			catch(NoSuchFieldException e)
			{
				nsfe	= e;
			}
		}
		
		// Find map accessor if not precomputed.
		Method	accessor0	= this.accessor;
		Object[]	args0	= this.args;
		if(!fetched && field0==null && accessor0==null && 
			(SReflect.isSupertype(Map.class, clazz) || SReflect.isSupertype(IMapAccess.class, clazz)))
		{
			accessor0	= SReflect.getMethod(clazz, "get", new Class[]{Object.class});
			if(accessor0==null)
				throw nsfe;
			args0	= new Object[]{getText()};
		}

		// Bean access no longer supported: use getXyz() instead of .xyz (why?)
		// Try bean property
//		else
//		{
//			try
//			{
//				val = BeanHelper.getPropertyValue(ref, getText());
//			}
//			catch(Exception e)
//			{
//				// Throw original NoSuchFieldException.
//				throwEvaluationException(e);
//			}
//		}


		// Try to find accessor method.
		/*if(field0==null && accessor0==null)
		{
			try
			{
				String	name	= "get" + getText().substring(0,1).toUpperCase()
					+ ((getText().length()>1) ? getText().substring(1) : "");
				accessor0	= clazz.getMethod(name, new Class[0]);
				args0	= new Object[0];
			}
			catch(NoSuchMethodException e){}
			catch(SecurityException e){}
		}*/


		// Read field.
		if(!fetched)
		{
			try
			{
				if(accessor0==null)
				{
					val	= field0.get(ref);
				}
				else
				{
					val	= accessor0.invoke(ref, args0);
				}
			}
	//		catch(IllegalAccessException e)
	//		{
	//			StringWriter	sw = new StringWriter();
	//			e.printStackTrace(new PrintWriter(sw));
	//			throw new RuntimeException(""+sw);
	//		}
	//		catch(IllegalArgumentException e)
	//		{
	//			StringWriter	sw = new StringWriter();
	//			e.printStackTrace(new PrintWriter(sw));
	//			throw new RuntimeException(""+sw);
	//		}
			catch(InvocationTargetException e)
			{
				if(e.getTargetException() instanceof RuntimeException)
				{
					throw (RuntimeException)e.getTargetException();
				}
				else
				{
					throw new RuntimeException(e);
				}
			}
		}

		return val;
	}
	
	/**
	 *  Find method declared in public class for a given method. 
	 */
	protected Method	getMethodForMethod(Method method)
	{
		Class	clazz	= method.getDeclaringClass();
		if((clazz.getModifiers() & Modifier.PUBLIC)==0)
		{
			List	classes	= new ArrayList();
			if(clazz.getSuperclass()!=null)
				classes.add(clazz.getSuperclass());
			classes.addAll(Arrays.asList(clazz.getInterfaces()));
			Method	meth	= null;
			while(meth==null && classes.size()>0)
			{
				Class	testclass	= (Class)classes.remove(0);
				try
				{
					if((testclass.getModifiers() & Modifier.PUBLIC)!=0)
					{
						meth	= testclass.getMethod(method.getName(), method.getParameterTypes());
					}
				}
				catch(Exception e)
				{
				}
				
				if(meth==null)
				{
					if(testclass.getSuperclass()!=null)
						classes.add(testclass.getSuperclass());
					classes.addAll(Arrays.asList(testclass.getInterfaces()));
				}
			}
			
			method	= meth;
		}
		return method;
	}

	/**
	 *  Test if two nodes are equal.
	 */
	public boolean	equals(Object o)
	{
		return super.equals(o) && type==((ReflectNode)o).type
			&& SUtil.equals(getText(), ((ReflectNode)o).getText());
	}
	
	/**
	 *  Get the hash code for the node.
	 */
	public int hashCode()
	{
		int	ret	= super.hashCode();
		ret	= ret*31 + type;
		ret	= ret*31 + (getText()!=null ? getText().hashCode() : 1);
		return ret;
	}

	//-------- deserialization handling --------

	/**
	 *  After deserialization do a precompile, because
	 *  the reflect objects (method, constructor etc.)
	 *  created in the precompile process are not serializable.
	 * /
	private void readObject(java.io.ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		// Read this object from stream.
		in.defaultReadObject();

		// Precompile node to initialize reflection objects.
		precompile();
	}*/
}

