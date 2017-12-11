package jadex.bytecode.fastinvocation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

import jadex.bytecode.ByteCodeClassLoader;
import jadex.bytecode.SASM;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.WeakKeyValueMap;

/**
 *  Factory for providing fast reflective access to methods.
 */
public class FastInvocation
{
	/** Set this to true to switch to fallback mode for non-public invocation */
	public static boolean FALLBACK_MODE = false;
	
	/** Cached invoker classes, the invoker class does not prevent GC (tested). */
	protected static volatile WeakHashMap<Method, Class<IMethodInvoker>> INVOKER_CLASSES =
		new WeakHashMap<Method, Class<IMethodInvoker>>();
	
	/** Cached accessor classes. */
	protected static volatile WeakHashMap<Class<?>, WeakKeyValueMap<Class<?>, Class<?>>> ACCESSOR_CLASSES =
			new WeakHashMap<Class<?>, WeakKeyValueMap<Class<?>, Class<?>>>();
	
	/** Cached extractor classes. */
	protected static volatile WeakHashMap<Class<?>, WeakKeyValueMap<Class<?>, Class<?>>> EXTRACTOR_CLASSES =
			new WeakHashMap<Class<?>, WeakKeyValueMap<Class<?>, Class<?>>>();
	
	/** Class name suffix counter. */ 
	public static AtomicLong NAME_SUFFIX_COUNTER = new AtomicLong();
	
	/**
	 *  Directly invokes a method based on the method name and arguments.
	 *  Method resolution is very basic and a performance penalty is incurred,
	 *  do not use if you plan on making repeated calls or need good method
	 *  resolution, use getInvoker() methods instead.
	 *  
	 *  @param obj Object on which the method is to be called.
	 *  @param methodname Name of the methods.
	 *  @param args Invocation arguments.
	 *  @return Return value of invocation.
	 */
	public static final Object invoke(Object obj, String methodname, Object... args)
	{
		return invoke(obj, null, methodname, args);
	}
	
	/**
	 *  Directly invokes a method based on the method name and arguments.
	 *  Method resolution is very basic and a performance penalty is incurred,
	 *  do not use if you plan on making repeated calls or need good method
	 *  resolution, use getInvoker() methods instead.
	 *  
	 *  @param obj Object on which the method is to be called.
	 *  @param clazz Class definition for static calls, can be null if obj is defined.
	 *  @param methodname Name of the method.
	 *  @param args Invocation arguments.
	 *  @return Return value of invocation.
	 */
	public static final Object invoke(Object obj, Class<?> clazz, String methodname, Object... args)
	{
		clazz = clazz == null ? obj.getClass() : clazz;
		Method[] methods = SReflect.getAllMethods(clazz, methodname);
		if (methods.length == 1)
			return newInvoker(methods[0]).invoke(obj, args);
		
		int argcount = args != null ? args.length : 0;
		for (int i = 0; i < methods.length; ++i)
		{
			if (methods[i].getParameterTypes().length == argcount)
				return newInvoker(methods[i]).invoke(obj, args);
		}
		throw new IllegalArgumentException("No unambiguous method + " + methodname + " found, try " + FastInvocation.class.getName() + "getInvoker() methods.");
	}
	
	/**
	 *  Creates a new invoker for a method.
	 * 
	 *  @param method The method.
	 *  @return Instantiated invoker.
	 */
	public static final IMethodInvoker newInvoker(Method method)
	{
		Class<?> ic = getInvokerClass(method);
		if (ic == null)
			return new FallBackInvoker(method);
		
		return newInvoker(ic);
	}
	
	/**
	 *  Instantiate a new method invoker from the invoker class.
	 *  
	 *  @param invokerclass The invoker class.
	 *  @return Instantiated invoker.
	 */
	public static final IMethodInvoker newInvoker(Class<?> invokerclass)
	{
		try
		{
			Constructor<?> c = invokerclass.getConstructor((Class[]) null);
			return (IMethodInvoker) c.newInstance((Object[]) null);
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
	}
	
	/**
	 *  Gets an invoker class.
	 *  
	 *  @param clazz The class used to map methods.
	 *  @param methodidmap The method ID map to store the mapping.
	 *  @return The generated invoker.
	 */
	public static final Class<IMethodInvoker> getInvokerClass(Method method)
	{
		Class<IMethodInvoker> ic = INVOKER_CLASSES.get(method);
		
		if (ic == null)
		{
			synchronized(NAME_SUFFIX_COUNTER)
			{
				ic = INVOKER_CLASSES.get(method);
				if (ic == null)
				{
					ClassLoader cl = method.getDeclaringClass().getClassLoader();
					
					ByteCodeClassLoader bcl = SASM.getByteCodeClassLoader(cl);
					
					ic = createInvokerClass(bcl, method);
					
					if (ic != null)
					{
						WeakHashMap<Method, Class<IMethodInvoker>> newgenclasses = new WeakHashMap<Method, Class<IMethodInvoker>>(INVOKER_CLASSES);
						newgenclasses.put(method, ic);
						INVOKER_CLASSES = newgenclasses;
					}
				}
			}
			
		}
		
		return ic;
	}
	
	/**
	 *  Creates the invoker class.
	 *  
	 *  @param cl ClassLoader to use for generated class.
	 *  @param clazz The class used to map methods.
	 *  @return The generated invoker.
	 */
	@SuppressWarnings("unchecked")
	public static final Class<IMethodInvoker> createInvokerClass(ByteCodeClassLoader cl, Method method)
	{
		Class<IMethodInvoker> ret = null;
		try
		{
			Class<?> clazz = method.getDeclaringClass();
			
			// Check ClassLoader validity
			if (!clazz.equals(cl.loadClass(clazz.getCanonicalName())))
				throw new IllegalArgumentException("Code generation classloader " + cl + " does not have access to class " + clazz + " defined in method " + method.getName());
			
			boolean notpublic = (method.getModifiers() & Modifier.PUBLIC) == 0;
			boolean isstatic = (method.getModifiers() & Modifier.STATIC) != 0;
			
			String classname = FastInvocation.class.getPackage().getName() + ".invokers.MethodInvoker_" + method.getName() + "_" + NAME_SUFFIX_COUNTER.incrementAndGet();
			
			ClassWriter cw = createClass(classname, notpublic, IMethodInvoker.class);
			if (cw == null)
				return null;
			
			// Implement the invoke method.
			Method invmethod = IMethodInvoker.class.getMethod("invoke", new Class<?>[] { Object.class, Object[].class });
			MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, invmethod.getName(), Type.getMethodDescriptor(invmethod), null, null);
	        mv.visitCode();
//			System.out.println("" + ": " + method.getName());
			
	        // If not static, load the object, cast to right type, ready the parameters, then invoke.
			if (!isstatic)
			{
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(clazz));
			}
			
			// Prepare method parameters
			readyParameters(mv, method.getParameterTypes());
			
			// Invoke static if static method.
			if (isstatic)
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(method.getDeclaringClass()), method.getName(), Type.getMethodDescriptor(method), false);
			else
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(method.getDeclaringClass()), method.getName(), Type.getMethodDescriptor(method), false);
			
			// Wrap primitive return value.
			if (SReflect.isBasicType(method.getReturnType()) && !void.class.equals(method.getReturnType()))
			{
				Class<?> wt = SReflect.getWrappedType(method.getReturnType());
				Method wm = wt.getMethod("valueOf", method.getReturnType());
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(wt), wm.getName(), Type.getMethodDescriptor(wm), false);
			}
			
			// If return type is void, return null
			if (void.class.equals(method.getReturnType()))
				mv.visitInsn(Opcodes.ACONST_NULL);
			
			// Return
			mv.visitInsn(Opcodes.ARETURN);
			
			mv.visitMaxs(0, 0);
			mv.visitEnd();
			
			cw.visitEnd();
			byte[] classcode = cw.toByteArray();
			ret = (Class<IMethodInvoker>) cl.defineClass(classcode);
		}
		catch (Exception e)
		{
			SUtil.throwUnchecked(e);
		}
		return ret;
	}
	
	/**
	 *  Creates a new accessor from an accessor class.
	 * 
	 *  @param accessorclass The accessor class.
	 *  @param delegate The delegation object / accessor target.
	 *  @return Instantiated accessor.
	 */
	public static final Object newAccessor(Class<?> accessorclass, Object delegate)
	{
		Object ret = null;
		try
		{
			Constructor<?> c = accessorclass.getConstructor((Class[]) null);
			ret = c.newInstance((Object[]) null);
			Field f = accessorclass.getDeclaredField("delegate");
			f.set(ret, delegate);
		}
		catch (Exception e)
		{
			SUtil.throwUnchecked(e);
		}
		return ret;
	}
	
	/**
	 *  Gets class for an interface-based accessor.
	 *  
	 *  @param iface The accessor interface.
	 *  @param clazz The target class.
	 *  @return The accessor class.
	 */
	@SuppressWarnings("unchecked")
	public static final <T> Class<T> getAccessorClass(Class<T> iface, Class<?> clazz)
	{
		Class<?> ac = null;
		WeakKeyValueMap<Class<?>, Class<?>> map = ACCESSOR_CLASSES.get(clazz);
		if (map != null)
			ac = map.get(iface);
		
		if (ac == null)
		{
			synchronized(NAME_SUFFIX_COUNTER)
			{
				map = ACCESSOR_CLASSES.get(clazz);
				if (map != null)
					ac = map.get(iface);
				
				if (ac == null)
				{
					if (map == null)
						map = new WeakKeyValueMap<Class<?>, Class<?>>();
					
					ClassLoader cl = clazz.getClassLoader();
					
					ByteCodeClassLoader bcl = SASM.getByteCodeClassLoader(cl);
					
					ac = createAccessorClass(bcl, iface, clazz);
					
					if (ac != null)
					{
						map.put(iface, ac);
						WeakHashMap<Class<?>, WeakKeyValueMap<Class<?>, Class<?>>> newgenclasses =
								new WeakHashMap<Class<?>, WeakKeyValueMap<Class<?>, Class<?>>>(ACCESSOR_CLASSES);
						newgenclasses.put(clazz, map);
						ACCESSOR_CLASSES = newgenclasses;
					}
				}
			}
		}
		
		return (Class<T>) ac;
	}
	
	/**
	 *  Generates an accessor class based on an interface.
	 *  Methods between the interface and the target class
	 *  are matched.
	 *  
	 *  @param cl The ClassLoader used to load the generated byte code.
	 *  @param iface The accessor interface.
	 *  @param clazz The target class of the accessor.
	 *  @return The generated class.
	 */
	public static final <T> Class<T> createAccessorClass(ByteCodeClassLoader cl, Class<T> iface, Class<?> clazz)
	{
		if (iface == null || !iface.isInterface())
			throw new IllegalArgumentException("Class is not an interface: " + iface);
		
		Method[] ifacemethods = SReflect.getAllMethods(iface);
		
		boolean privileged = false;
		Method[] targets = new Method[ifacemethods.length];
		for (int i = 0; i < ifacemethods.length; ++i)
		{
			Method[] cms = SReflect.getAllMethods(clazz, ifacemethods[i].getName());
			
			Class<?>[][] paramtypes = new Class<?>[cms.length][];
			for (int j = 0; j < cms.length; ++j)
				paramtypes[j] = cms[j].getParameterTypes();
			
			int[] match = SReflect.matchArgumentTypes(ifacemethods[i].getParameterTypes(), paramtypes);
			
			if (match == null || match.length == 0)
				throw new IllegalArgumentException("No match found for interface method " + ifacemethods[i]);
			
			targets[i] = cms[match[0]];
			privileged |= (targets[i].getModifiers() & Modifier.PUBLIC) == 0;
		}
		
		String classname = FastInvocation.class.getPackage().getName() + ".accessors.ClassAccessor_" + clazz.getName() + "_" + NAME_SUFFIX_COUNTER.incrementAndGet();
		String internalname = classname.replace('.', '/');
		ClassWriter cw = createClass(classname, privileged, iface);
		
		cw.visitField(Opcodes.ACC_PUBLIC, "delegate", Type.getDescriptor(clazz), null, null);
		
		for (int i = 0; i < ifacemethods.length; ++i)
		{
			Method invmethod = ifacemethods[i];
			MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, invmethod.getName(), Type.getMethodDescriptor(invmethod), null, null);
	        mv.visitCode();
	        
	        if ((invmethod.getModifiers() & Modifier.STATIC) == 0)
	        {
	        	mv.visitVarInsn(Opcodes.ALOAD, 0);
	        	mv.visitFieldInsn(Opcodes.GETFIELD, internalname, "delegate", Type.getDescriptor(clazz));
	        }
	        
	        int aload = 0;
	        Class<?>[] paramtypes = targets[i].getParameterTypes();
	        for (int j = 0; j < paramtypes.length; ++j)
	        {
	        	if (byte.class.equals(paramtypes[j]) ||
	        		short.class.equals(paramtypes[j]) ||
	        		char.class.equals(paramtypes[j]) ||
	        		boolean.class.equals(paramtypes[j]) ||
	        		int.class.equals(paramtypes[j]))
	        	{
	        		mv.visitVarInsn(Opcodes.ILOAD, ++aload);
	        	}
	        	else if (long.class.equals(paramtypes[j]))
	        	{
	        		mv.visitVarInsn(Opcodes.LLOAD, ++aload);
	        		++aload;
	        	}
	        	else if (float.class.equals(paramtypes[j]))
	        	{
	        		mv.visitVarInsn(Opcodes.FLOAD, ++aload);
	        	}
	        	else if (double.class.equals(paramtypes[j]))
	        	{
	        		mv.visitVarInsn(Opcodes.DLOAD, ++aload);
	        		++aload;
	        	}
	        	else
	        	{
	        		mv.visitVarInsn(Opcodes.ALOAD, ++aload);
	        	}
	        }
	        
			if ((targets[i].getModifiers() & Modifier.STATIC) != 0)
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(targets[i].getDeclaringClass()), targets[i].getName(), Type.getMethodDescriptor(targets[i]), false);
			else
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(targets[i].getDeclaringClass()), targets[i].getName(), Type.getMethodDescriptor(targets[i]), false);
			
			
			InsnList insn = new InsnList();
			SASM.makeReturn(insn, Type.getType(invmethod.getReturnType()));
			insn.accept(mv);
	        
	        mv.visitMaxs(0, 0);
	        mv.visitEnd();
		}
		
		try
		{
			cw.visitEnd();
			byte[] classcode = cw.toByteArray();
			@SuppressWarnings("unchecked")
			Class<T> genclass = (Class<T>) cl.defineClass(classcode);
			
			return genclass;
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
	}
	
	/**
	 *  Creates extractor class.
	 *  
	 *  @param cl ClassLoader to use for generated class.
	 *  @param clazz The class used to map methods.
	 *  @return The generated invoker.
	 */
	public static final <T> Class<IExtractor<T>> createExtractorClass(ByteCodeClassLoader cl, Class<T> clazz, String[] propnames, Method[] accessormethods)
	{
		if (propnames.length != accessormethods.length)
			throw new IllegalArgumentException("Number of properties and methods must match.");
		
		Class<IExtractor<T>> ret = null;
		
		boolean privileged = false;
		for (int i = 0; i < accessormethods.length; ++i)
			privileged |= (accessormethods[i].getModifiers() & Modifier.PUBLIC) == 0;
		
		String classname = FastInvocation.class.getPackage().getName() + ".accessors.ClassAccessor_" + clazz.getName() + "_" + NAME_SUFFIX_COUNTER.incrementAndGet();
		String internalname = classname.replace('.', '/');
		ClassWriter cw = createClass(classname, privileged, IExtractor.class);
		
		cw.visitField(Opcodes.ACC_PROTECTED | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC, "PROPERTYNAMES", Type.getDescriptor(String[].class), null, null);
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<clinit>", Type.getMethodDescriptor(Type.getType(void.class), new Type[0]), null, null);
		InsnList nl = new InsnList();
		SASM.pushImmediate(nl, propnames.length);
		nl.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getDescriptor(String.class)));
		for (int i = 0; i < propnames.length; ++i)
		{
			nl.add(new InsnNode(Opcodes.DUP));
			SASM.pushImmediate(nl, i);
			nl.add(new LdcInsnNode(propnames[i]));
			nl.add(new InsnNode(Opcodes.AASTORE));
		}
		nl.add(new InsnNode(Opcodes.PUTSTATIC));
		nl.accept(mv);
		mv.visitEnd();
		
		try
		{
			Method exmethod = IExtractor.class.getMethod("extract", new Class<?>[] { Object.class });
			mv = cw.visitMethod(Opcodes.ACC_PUBLIC, exmethod.getName(), Type.getMethodDescriptor(exmethod), null, null);
	        mv.visitCode();
	        
	        mv.visitTypeInsn(Opcodes.NEW, Type.getInternalName(Tuple2.class));
			mv.visitInsn(Opcodes.DUP);
			
			mv.visitFieldInsn(Opcodes.GETFIELD, internalname, "PROPERTYNAMES", Type.getDescriptor(String[].class));
			
			mv.visitTypeInsn(Opcodes.ANEWARRAY, Type.getDescriptor(Object.class));
			nl = new InsnList();
			for (int i = 0; i < propnames.length; ++i)
			{
				nl.add(new InsnNode(Opcodes.DUP));
				SASM.pushImmediate(nl, i);
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(accessormethods[i].getDeclaringClass()), accessormethods[i].getName(), Type.getMethodDescriptor(accessormethods[i]), false);
				nl.add(new InsnNode(Opcodes.AASTORE));
			}
			
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Tuple2.class), "<init>", Type.getConstructorDescriptor(Tuple2.class.getConstructor(Object.class, Object.class)), false);
			mv.visitInsn(Opcodes.ARETURN);
		}
		catch (Exception e)
		{
			SUtil.throwUnchecked(e);
		}
		
		return ret;
	}
	
	/**
	 *  Creates the initial setup for a new class in ASM.
	 *  
	 *  @param classname Fully-qualified name of the class.
	 *  @param privileged If the class should be "privileged" to allow
	 *  				  access to non-publics.
	 *  @return Preinitialized class writer.
	 */
	protected static final ClassWriter createClass(String classname, boolean privileged, Class<?>... interfaces)
	{
		Class<?> superclass = Object.class;
		if (privileged)
		{
			if (FALLBACK_MODE)
				return null;
			
			try
			{
				Class<?> reffacclass = Class.forName("sun.reflect.ReflectionFactory");
				superclass = Class.forName("sun.reflect.MagicAccessorImpl", true, reffacclass.getClassLoader());
			}
			catch (Exception e)
			{
				return null;
			}
		}
		
		String[] internalifaces = interfaces != null ? new String[interfaces.length] : new String[0];
		for (int i = 0; i < internalifaces.length; ++i)
			internalifaces[i] = Type.getType(interfaces[i]).getInternalName();
		
		// Create class implementing the handler.
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		
		cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, classname.replace('.', '/'), null, Type.getType(superclass).getInternalName(), internalifaces);
		
		// Create empty constructor for our invoker handler.
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.getType(void.class), new Type[0]), null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		try
		{
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", Type.getConstructorDescriptor(Object.class.getConstructor((Class[])null)), false);
		}
		catch (Exception e)
		{SUtil.throwUnchecked(e);
		}
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		
		return cw;
	}
	
	/**
	 *  Readies the parameters for delegated method invocation.
	 *  Converts primitives as appropriate.
	 *  
	 *  @param mv The MethodVisitor being used.
	 *  @param parameters The parameters of the method.
	 */
	protected static final void readyParameters(MethodVisitor mv, Class<?>[] parameters)
	{
		for (int i = 0; i < parameters.length; ++i)
		{
			// Load reference of argument array
			mv.visitVarInsn(Opcodes.ALOAD, 2);
			
			// Select index of parameter in array.
			InsnList nl = new InsnList();
			SASM.pushImmediate(nl, i);
			nl.accept(mv);
			
			// Load parameter onto stack
			mv.visitInsn(Opcodes.AALOAD);
			
			// If the actual parameter is a primitive, we need to do some conversion...
			if (SReflect.isBasicType(parameters[i]))
			{
				Class<?> cc = null;
				Method cm = null;
				try
				{
					if (parameters[i].equals(boolean.class))
					{
						cc = Boolean.class;
						cm = cc.getMethod("booleanValue", new Class<?>[0]);
					}
					else if (parameters[i].equals(int.class))
					{
						cc = Integer.class;
						cm = cc.getMethod("intValue", new Class<?>[0]);
					}
					else if (parameters[i].equals(double.class))
					{
						cc = Double.class;
						cm = cc.getMethod("doubleValue", new Class<?>[0]);
					}
					else if (parameters[i].equals(float.class))
					{
						cc = Float.class;
						cm = cc.getMethod("floatValue", new Class<?>[0]);
					}
					else if (parameters[i].equals(long.class))
					{
						cc = Long.class;
						cm = cc.getMethod("longValue", new Class<?>[0]);
					}
					else if (parameters[i].equals(short.class))
					{
						cc = Short.class;
						cm = cc.getMethod("shortValue", new Class<?>[0]);
					}
					else if (parameters[i].equals(byte.class))
					{
						cc = Byte.class;
						cm = cc.getMethod("byteValue", new Class<?>[0]);
					}
					else if (parameters[i].equals(char.class))
					{
						cc = Character.class;
						cm = cc.getMethod("charValue", new Class<?>[0]);
					}
				}
				catch (Exception e)
				{
					SUtil.throwUnchecked(e);
				}
				
				// Convert wrapped parameter to primitive.
				mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(cc));
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(cc), cm.getName(), Type.getMethodDescriptor(cm), false);
			}
			else if (!Object.class.equals(parameters[i]))
			{
				// Cast parameter since argument array is type Object[]
				mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(parameters[i]));
			}
		}
	}
	
	/**
	 *  Fallback invoker using reflection in case a byte-engineered variant is not available.
	 *
	 */
	protected static class FallBackInvoker implements IMethodInvoker
	{
		/** The method. */
		protected Method method;
		
		/**
		 *  Creates the invoker.
		 *  
		 *  @param method Method to invoke.
		 */
		public FallBackInvoker(Method method)
		{
//			System.err.println("WARNING FALLBACK MODE ENABLED");
			method.setAccessible(true);
			this.method = method;
		}
		
		
		/**
		 *  Invokes a method on an object.
		 *  
		 *  @param object The object
		 *  @param methodid The ID of the method.
		 *  @param args The method arguments.
		 *  @return The result, null if void.
		 */
		public Object invoke(Object object, Object... args)
		{
			try
			{
				return method.invoke(object, args);
			}
			catch (Exception e)
			{
				throw SUtil.throwUnchecked(e);
			}
		}
	}
}
