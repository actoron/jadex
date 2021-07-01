package jadex.bytecode.invocation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import jadex.bytecode.IByteCodeClassLoader;
import jadex.bytecode.SASM;
import jadex.bytecode.vmhacks.VmHacks;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.collection.WeakKeyValueMap;

/**
 *  Factory for providing fast reflective access to methods.
 */
public class SInvocation
{
	/** Class name suffix counter. */ 
	public static AtomicLong NAME_SUFFIX_COUNTER = new AtomicLong();
	
	/** 
	 *  Flag if default / protected access via ASM is available.
	 *  Cannot be final due to bootstrapping. 
	 */
	public static boolean DEFAULT_ACCESS = false;
	
	/** 
	 *  Flag if private access via ASM is available.
	 *  Cannot be final due to bootstrapping.
	 */
	public static boolean PRIVATE_ACCESS = false;
	
	/** Cached invoker classes, the invoker class does not prevent GC (tested). */
	protected static volatile WeakHashMap<Method, Class<IMethodInvoker>> INVOKER_CLASSES =
		new WeakHashMap<Method, Class<IMethodInvoker>>();
	
	/** Cached accessor classes. */
	protected static volatile WeakHashMap<Class<?>, WeakKeyValueMap<Class<?>, Class<?>>> ACCESSOR_CLASSES =
			new WeakHashMap<Class<?>, WeakKeyValueMap<Class<?>, Class<?>>>();
	
	/** Cached extractor classes. */
	protected static volatile WeakHashMap<Class<?>, WeakKeyValueMap<Class<?>, Class<?>>> EXTRACTOR_CLASSES =
			new WeakHashMap<Class<?>, WeakKeyValueMap<Class<?>, Class<?>>>();
	
	// Ensure VmHacks is initialized.
	static
	{
		VmHacks.get();
		enableEnhancedAccess();
	}
	
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
		throw new IllegalArgumentException("No unambiguous method + " + methodname + " found, try " + SInvocation.class.getName() + "getInvoker() methods.");
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
	 *  Creates a new invoker for a method.
	 * 
	 *  @param method The method.
	 *  @param cl ClassLoader to use.
	 *  @return Instantiated invoker.
	 */
	public static final IMethodInvoker newInvoker(Method method, IByteCodeClassLoader cl)
	{
		Class<?> ic = createInvokerClass(cl, method);
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
	protected static final IMethodInvoker newInvoker(Class<?> invokerclass)
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
			if (INVOKER_CLASSES.containsKey(method))
				return null;
			
			synchronized(NAME_SUFFIX_COUNTER)
			{
				ic = INVOKER_CLASSES.get(method);
				if (ic == null)
				{
					ClassLoader cl = method.getDeclaringClass().getClassLoader();
					
					IByteCodeClassLoader bcl = SASM.getByteCodeClassLoader(cl);
					
					ic = createInvokerClass(bcl, method);
					
					WeakHashMap<Method, Class<IMethodInvoker>> newgenclasses = new WeakHashMap<Method, Class<IMethodInvoker>>(INVOKER_CLASSES);
					newgenclasses.put(method, ic);
					INVOKER_CLASSES = newgenclasses;
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
	protected static final Class<IMethodInvoker> createInvokerClass(IByteCodeClassLoader cl, Method method)
	{
		Class<IMethodInvoker> ret = null;
		try
		{
			Class<?> clazz = method.getDeclaringClass();
			
			// Check ClassLoader validity
			if (!clazz.equals(cl.loadClass(clazz.getName())))
				throw new IllegalArgumentException("Code generation classloader " + cl + " does not have access to class " + clazz + " defined in method " + method.getName());
			
//			boolean notpublic = (method.getModifiers() & Modifier.PUBLIC) == 0;
			boolean isstatic = (method.getModifiers() & Modifier.STATIC) != 0;
			
			String classname = "MethodInvoker_" + method.getName() + "_" + NAME_SUFFIX_COUNTER.incrementAndGet();
			int accesslevel = determineAccessLevel(Opcodes.ACC_PUBLIC, method.getModifiers());
			ExtendedClassWriter cw = createClass(clazz, classname, accesslevel, IMethodInvoker.class);
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
			prepareParameters(mv, method.getParameterTypes());
			
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
//			System.out.println("INV CL: " + cl);
			if (cw.requiresParentLoader())
				ret = (Class<IMethodInvoker>) cl.doDefineClassInParent(null, classcode, 0, classcode.length, clazz.getProtectionDomain());
			else	
				ret = (Class<IMethodInvoker>) cl.doDefineClass(classcode);
//			ret = (Class<IMethodInvoker>) SASM.UNSAFE.defineClass(null, classcode, 0, classcode.length, ClassLoader.getSystemClassLoader(), null);
//			ret = (Class<IMethodInvoker>) SASM.NATIVE_HELPER.defineClass(null, classcode, ClassLoader.getSystemClassLoader());
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
	public static final <T> T newAccessor(Class<T> iface, Class<?> targetclass, Object delegate)
	{
		Class<?> accessorclass = getAccessorClass(iface, targetclass);
		T ret = null;
		if (accessorclass != null)
		{
			try
			{
				@SuppressWarnings("unchecked")
				Constructor<T> c = (Constructor<T>) accessorclass.getConstructor((Class[]) null);
				ret = c.newInstance((Object[]) null);
				Field f = accessorclass.getDeclaredField("delegate");
				f.set(ret, delegate);
			}
			catch (Exception e)
			{
				SUtil.throwUnchecked(e);
			}
		}
		else
		{
			ret = createFallbackAccessor(iface, targetclass, delegate);
		}
		return ret;
	}
	
	/**
	 *  Gets class for an interface-based accessor.
	 *  
	 *  @param iface The accessor interface.
	 *  @param targetclazz The target class.
	 *  @return The accessor class.
	 */
	@SuppressWarnings("unchecked")
	public static final <T> Class<T> getAccessorClass(Class<T> iface, Class<?> targetclazz)
	{
		Class<?> ac = null;
		WeakKeyValueMap<Class<?>, Class<?>> map = ACCESSOR_CLASSES.get(targetclazz);
		if (map != null)
			ac = map.get(iface);
		
		if (ac == null && (map == null || !map.containsKey(iface)))
		{
			synchronized(NAME_SUFFIX_COUNTER)
			{
				map = ACCESSOR_CLASSES.get(targetclazz);
				if (map != null)
					ac = map.get(iface);
				
				if (ac == null)
				{
					if (map == null)
						map = new WeakKeyValueMap<Class<?>, Class<?>>();
					
					ClassLoader cl = targetclazz.getClassLoader();
					
					IByteCodeClassLoader bcl = SASM.getByteCodeClassLoader(cl);
					
					ac = createAccessorClass(bcl, iface, targetclazz);
					
					map.put(iface, ac);
					WeakHashMap<Class<?>, WeakKeyValueMap<Class<?>, Class<?>>> newgenclasses =
							new WeakHashMap<Class<?>, WeakKeyValueMap<Class<?>, Class<?>>>(ACCESSOR_CLASSES);
					newgenclasses.put(targetclazz, map);
					ACCESSOR_CLASSES = newgenclasses;
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
	@SuppressWarnings("unchecked")
	public static final <T> Class<T> createAccessorClass(IByteCodeClassLoader cl, Class<T> iface, Class<?> clazz)
	{
		if (iface == null || !iface.isInterface())
			throw new IllegalArgumentException("Class is not an interface: " + iface);
		
		Method[] ifacemethods = SReflect.getAllMethods(iface);
		
		int accesslevel = Opcodes.ACC_PUBLIC;
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
			accesslevel = determineAccessLevel(accesslevel, targets[i].getModifiers());
		}
		
		String classname = SInvocation.class.getPackage().getName() + ".accessors.ClassAccessor_" + clazz.getName() + "_" + NAME_SUFFIX_COUNTER.incrementAndGet();
		ExtendedClassWriter cw = createClass(clazz, classname, accesslevel, iface);
		if (cw == null)
			return null;
		
		String internalname = cw.getInternalName();
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
			Class<T> genclass = null;
			if (cw.requiresParentLoader())
			{
				genclass = (Class<T>) cl.doDefineClassInParent(null, classcode, 0, classcode.length, clazz.getProtectionDomain());
			}
			else
			{
				genclass = (Class<T>) cl.doDefineClass(classcode);
			}
			
			return genclass;
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
	}
	
	public static final IExtractor newExtractor(Class<IExtractor> extractorclass)
	{
		try
		{
			System.out.println(extractorclass);
			Constructor<IExtractor> c = extractorclass.getConstructor((Class[]) null);
			return c.newInstance((Object[]) null);
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
	@SuppressWarnings("unchecked")
	public static final Class<IExtractor> createExtractorClass(IByteCodeClassLoader cl, Class<?> clazz, String[] propnames, Member[] accessormember)
	{
		if (propnames.length != accessormember.length)
			throw new IllegalArgumentException("Number of properties and methods must match.");
		
		int accesslevel = Opcodes.ACC_PUBLIC;
		for (int i = 0; i < accessormember.length; ++i)
			accesslevel = determineAccessLevel(accesslevel, accessormember[i].getModifiers());
		
		String classnamesuffix = "ClassAccessor_" + clazz.getName() + "_" + NAME_SUFFIX_COUNTER.incrementAndGet();
		ExtendedClassWriter cw = createClass(clazz, classnamesuffix, accesslevel, IExtractor.class);
		String internalname = cw.getInternalName();
		
		cw.visitField(Opcodes.ACC_PROTECTED | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC, "PROPERTYNAMES", Type.getDescriptor(String[].class), null, null);
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<clinit>", Type.getMethodDescriptor(Type.getType(void.class), new Type[0]), null, null);
		InsnList nl = new InsnList();
		SASM.pushImmediate(nl, propnames.length);
		nl.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getInternalName(String.class)));
		for (int i = 0; i < propnames.length; ++i)
		{
			nl.add(new InsnNode(Opcodes.DUP));
			SASM.pushImmediate(nl, i);
			nl.add(new LdcInsnNode(propnames[i]));
			nl.add(new InsnNode(Opcodes.AASTORE));
		}
		nl.add(new FieldInsnNode(Opcodes.PUTSTATIC, internalname, "PROPERTYNAMES", Type.getDescriptor(String[].class)));
		nl.add(new InsnNode(Opcodes.RETURN));
		nl.accept(mv);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		
		try
		{
			Method exmethod = IExtractor.class.getMethod("extract", new Class<?>[] { Object.class });
			mv = cw.visitMethod(Opcodes.ACC_PUBLIC, exmethod.getName(), Type.getMethodDescriptor(exmethod), null, null);
	        mv.visitCode();
	        
	        mv.visitTypeInsn(Opcodes.NEW, Type.getInternalName(Tuple2.class));
			mv.visitInsn(Opcodes.DUP);
			
			mv.visitFieldInsn(Opcodes.GETSTATIC, internalname, "PROPERTYNAMES", Type.getDescriptor(String[].class));
			
			nl = new InsnList();
			SASM.pushImmediate(nl, propnames.length);
			nl.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getInternalName(Object.class)));
			for (int i = 0; i < propnames.length; ++i)
			{
				nl.add(new InsnNode(Opcodes.DUP));
				SASM.pushImmediate(nl, i);
				if (accessormember[i] instanceof Method)
				{
					Method accessormethod = (Method) accessormember[i];
					nl.add(new VarInsnNode(Opcodes.ALOAD, 1));
					nl.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(clazz)));
					nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type.getInternalName(accessormethod.getDeclaringClass()), accessormethod.getName(), Type.getMethodDescriptor(accessormethod), false));
				}
				else if (accessormember[i] instanceof Field)
				{
					Field accessorfield = (Field) accessormember[i];
					nl.add(new FieldInsnNode(Opcodes.GETFIELD, Type.getInternalName(accessorfield.getDeclaringClass()), accessorfield.getName(), Type.getDescriptor(accessorfield.getType())));
				}
				else
				{
					throw new IllegalArgumentException("Illegal accessor member: " + accessormember[i]);
				}
				nl.add(new InsnNode(Opcodes.AASTORE));
			}
			nl.accept(mv);
			
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Tuple2.class), "<init>", Type.getConstructorDescriptor(Tuple2.class.getConstructor(Object.class, Object.class)), false);
			mv.visitInsn(Opcodes.ARETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		catch (Exception e)
		{
			SUtil.throwUnchecked(e);
		}
		
		try
		{
			cw.visitEnd();
			byte[] classcode = cw.toByteArray();
			Class<IExtractor> genclass = null;
			if (cw.requiresParentLoader())
			{
				genclass = (Class<IExtractor>) cl.doDefineClassInParent(null, classcode, 0, classcode.length, clazz.getProtectionDomain());
			}
			else
			{
				genclass = (Class<IExtractor>) cl.doDefineClass(classcode);
			}
			
			return genclass;
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
	}
	
	/**
	 *  Creates the initial setup for a new class in ASM.
	 *  
	 *  @param classname Simple name of the class.
	 *  @param privileged If the class should be "privileged" to allow
	 *  				  access to non-publics.
	 *  @return Preinitialized class writer.
	 */
	protected static final ExtendedClassWriter createClass(Class<?> targetclass, String classname, int accesslevel, Class<?>... interfaces)
	{
		if (!VmHacks.get().hasAsm())
			return null;
		
		Class<?> superclass = Object.class;
		String genpackage = SInvocation.class.getPackage().getName() + ".generated";
//		accesslevel = Opcodes.ACC_PUBLIC;
		boolean needsparentcl = false;
		if (accesslevel == Opcodes.ACC_PRIVATE || (accesslevel != Opcodes.ACC_PUBLIC && PRIVATE_ACCESS))
		{
			if (!PRIVATE_ACCESS)
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
		else if (accesslevel != Opcodes.ACC_PUBLIC)
		{
			if (!DEFAULT_ACCESS)
				return null;
			
			// At least protected, inject into the package...
			genpackage = targetclass.getPackage().getName();
			//additional suffix to avoid clash
			classname += "_" + Math.abs(SUtil.FAST_RANDOM.nextLong());
			needsparentcl = true;
		}
		
		String[] internalifaces = interfaces != null ? new String[interfaces.length] : new String[0];
		for (int i = 0; i < internalifaces.length; ++i)
			internalifaces[i] = Type.getType(interfaces[i]).getInternalName();
		
		// Create class implementing the handler.
		final String internalname = (genpackage + "." + classname).replace('.', '/');
		ExtendedClassWriter cw = new ExtendedClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES, internalname, needsparentcl);
		
		cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, internalname, null, Type.getType(superclass).getInternalName(), internalifaces);
		
		// Create empty constructor for our invoker handler.
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.getType(void.class), new Type[0]), null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		try
		{
			mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", Type.getConstructorDescriptor(Object.class.getConstructor((Class[])null)), false);
		}
		catch (Exception e)
		{
			return null;
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
	protected static final void prepareParameters(MethodVisitor mv, Class<?>[] parameters)
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
	 *  Determines the necessary access level based on the current access level.
	 *   
	 *  @param currentlevel The current level.
	 *  @param modifiers Modifiers of the target
	 *  @return Needed access level.
	 */
	protected static final int determineAccessLevel(int currentlevel, int modifiers)
	{
		if ((modifiers & Modifier.PUBLIC) == 0)
		{
			if ((modifiers & Modifier.PRIVATE) != 0)
				currentlevel = Opcodes.ACC_PRIVATE;
			else if ((modifiers & Modifier.PROTECTED) != 0 && currentlevel == Opcodes.ACC_PUBLIC)
				currentlevel = Opcodes.ACC_PROTECTED;
			else if (currentlevel == Opcodes.ACC_PUBLIC)
				currentlevel = 0;
		}
		return currentlevel;
	}
	
	/**
	 *  Implements an accessor based on a dynamic proxy.
	 *  
	 *  @param cl ClassLoader to use.
	 *  @param iface The interface to implement.
	 *  @param clazz The target class.
	 *  @param obj The target class or null if all static.
	 *  @return Accessor.
	 */
	protected static final <T> T createFallbackAccessor(final Class<T> iface, final Class<?> clazz, final Object obj)
	{
		InvocationHandler handler = new InvocationHandler()
		{
			protected Map<Method, IMethodInvoker> invocationmap;
			{
				invocationmap = new HashMap<Method, IMethodInvoker>();
				try
				{
					Method[] methods = iface.getMethods();
					for (Method method : methods)
					{
						Class<?> cclazz = clazz;
						Method cmethod = null;
						while (cmethod == null && cclazz != null)
						{
							cmethod = cclazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
							cclazz = cclazz.getSuperclass();
						}
						SReflect.getMethod(clazz, method.getName(), method.getParameterTypes());
						invocationmap.put(method, newInvoker(cmethod));
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				IMethodInvoker invoker = invocationmap.get(method);
				return invoker.invoke(obj, args);
			}
		};
		
		ClassLoader cl = clazz.getClassLoader();
		@SuppressWarnings("unchecked")
		T ret = (T) Proxy.newProxyInstance(cl, new Class<?>[] { iface }, handler);
		return ret;
	}
	
	/**
	 *  Tries to enable enhanced direct access.
	 */
	protected static final void enableEnhancedAccess()
	{
		AccessTestClass testobj = new AccessTestClass();
		DEFAULT_ACCESS = true;
		DEFAULT_ACCESS = hasMethodAccess("defaultTest", testobj);
		PRIVATE_ACCESS = true;
		PRIVATE_ACCESS = hasMethodAccess("privateTest", testobj);
	}
	
	/**
	 *  Tests if access to a method using the invocation API is possible.
	 *  
	 *  @param testmethod Name of the test method.
	 *  @param testobject Test object with method.
	 *  @return True, if accessible.
	 */
	private static final boolean hasMethodAccess(String testmethod, Object testobject)
	{
		IByteCodeClassLoader dummycl = SASM.createByteCodeClassLoader(null, AccessTestClass.class.getClassLoader());
		Method cicm = null;
		try
		{
			cicm = SInvocation.class.getDeclaredMethod("createInvokerClass", new Class<?>[] { IByteCodeClassLoader.class, Method.class });
		}
		catch (Exception e)
		{
			return false;
		}
		VmHacks.get().setAccessible(cicm, true);
		boolean acc = false;
		try
		{
			Method m = testobject.getClass().getDeclaredMethod(testmethod, (Class<?>[]) null);
			Class<?> invclass = (Class<?>) cicm.invoke(null, dummycl, m);
			if (invclass != null)
			{
				Constructor<?> c = invclass.getConstructor((Class[]) null);
				IMethodInvoker inv = (IMethodInvoker) c.newInstance((Object[]) null);
				inv.invoke(testobject, (Object[]) null);
				acc = true;
			}
		}
		catch (Throwable t)
		{
			return false;
		}
		return acc;
	}
	
	/**
	 *  Class writer with some meta information.
	 *
	 */
	protected static class ExtendedClassWriter extends ClassWriter
	{
		/** Class internal name. */
		protected String internalname;
		
		/** Flag whether the resulting class requires the class loader parent. */
		protected boolean requiresparentloader;
		
		/**
		 *  Creates the writer.
		 *  
		 *  @param flags ClassWriter flags.
		 *  @param internalname Class internal name.
		 *  @param requiresparentloader Flag whether the resulting class requires the class loader parent.
		 */
		public ExtendedClassWriter(int flags, String internalname, boolean requiresparentloader)
		{
			super(flags);
			this.internalname = internalname;
			this.requiresparentloader = requiresparentloader;
		}
		
		/**
		 *  Gets the internal name.
		 * @return The internal name.
		 */
		public String getInternalName()
		{
			return internalname;
		}
		
		/**
		 *  Returns flag whether the resulting class requires the class loader parent
		 *  
		 *  @return Flag whether the resulting class requires the class loader parent
		 */
		public boolean requiresParentLoader()
		{
			return requiresparentloader;
		}
	}
	
	protected static class SortingInjectorWrapper implements IInjector
	{
		/**
		 *  Injects properties into a bean.
		 *  
		 *  @param object The target bean object.
		 *  @param properties The bean properties, names followed by values,
		 *  				  size must be even.
		 */
		public void inject(Object object, Object... properties)
		{
			if (properties.length < 50)
			{
				for (int i = 0; i < properties.length; i = i + 2)
				{
					
				}
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
			VmHacks.get().setAccessible(method, true);
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
	
	/**
	 *  Class used to test access level via ASM.
	 *
	 */
	public static class AccessTestClass
	{
		/**
		 *  Used to test default access privileges.
		 */
		protected Object defaultTest()
		{
			return privateTest();
		}
		
		/**
		 *  Used to test private access privileges.
		 */
		private Object privateTest()
		{
			return null;
		}
	}
}
