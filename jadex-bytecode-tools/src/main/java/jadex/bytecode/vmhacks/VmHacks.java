package jadex.bytecode.vmhacks;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.MethodRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.InsnList;

import jadex.bytecode.ByteCodeClassLoader;
import jadex.bytecode.IByteCodeClassLoader;
import jadex.bytecode.SASM;
import jadex.bytecode.invocation.IMethodInvoker;
import jadex.bytecode.invocation.SInvocation;
import jadex.commons.SReflect;
import jadex.commons.SUtil;

/**
 *  Class providing various means of getting around VM restrictions.
 *
 */
public class VmHacks
{
	/** Set this to true to switch to fallback mode for invocation */
	public static boolean NO_ASM = false;
	
	/** Flag if native support is available. */
	public static final boolean HAS_NATIVE;
	
	/** Flag if the Extended ByteCodeClassLoader is available. */
	public static final boolean HAS_EXTENDED_BYTECODE_CLASSLOADER;
	
	/** 
	 *  Flag if default / protected access via ASM is available.
	 *  Cannot be final due to bootstrapping. 
	 */
	public static boolean DEFAULT_ACCESS = true;
	
	/** 
	 *  Flag if private access via ASM is available.
	 *  Cannot be final due to bootstrapping.
	 */
	public static boolean PRIVATE_ACCESS = true;
	
	/** Access to unsafe operations. */
	private static final Unsafe UNSAFE;
	
	/** Class of the Extended ByteCodeClassLoader if available. */
	private static final Class<?> EXTENDED_BYTECODE_CLASSLOADER;
	
	static
	{
		// Test if native support is available...
		
		HAS_NATIVE = hasNative();
		
		// No ASM bytecode on Android...
		if (SReflect.isAndroid())
			NO_ASM = true;
		
		UNSAFE = new Unsafe();
		UNSAFE.init();
		
		UNSAFE.startInstrumentationAgent();
		
		EXTENDED_BYTECODE_CLASSLOADER = createExtendedClassLoaderClass();
		HAS_EXTENDED_BYTECODE_CLASSLOADER = hasExtendedClassLoader();
		
		AccessTestClass testobj = new AccessTestClass();
		DEFAULT_ACCESS = hasMethodAccess("defaultTest", testobj);
		PRIVATE_ACCESS = hasMethodAccess("privateTest", testobj);
	}
	
	/**
	 *  Provides access to unsafe operations.
	 *  @return The Unsafe object.
	 */
	public static final Unsafe getUnsafe()
	{
		return UNSAFE;
	}
	
	/**
	 *  Creates an extended class loader with additional privileges if available.
	 *  
	 *  @param parents ClassLoader parents.
	 *  @return The ClassLoader.
	 */
	public static final IByteCodeClassLoader getExtendedByteCodeClassLoader(ClassLoader... parents)
	{
		IByteCodeClassLoader ret = null;
		if (EXTENDED_BYTECODE_CLASSLOADER != null)
		{
			try
			{
				@SuppressWarnings("unchecked")
				Constructor<IByteCodeClassLoader> c = (Constructor<IByteCodeClassLoader>) EXTENDED_BYTECODE_CLASSLOADER.getConstructor(ClassLoader[].class);
				ret = c.newInstance(new Object[] { parents });
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		ret = null;
		return ret;
	}
	
	/**
	 *  Creates an extended class loader class with additional privileges if available.
	 *  
	 *  @return The ClassLoader class.
	 */
	private static final Class<?> createExtendedClassLoaderClass()
	{
		Class<?> ret = null;
		
		if (!NO_ASM && getUnsafe().hasInstrumentation())
		{
			try
			{
				// Future improvement, disable for now....
				if (!HAS_EXTENDED_BYTECODE_CLASSLOADER)
					return null;
				
				// Get out prybar into the base classloader...
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
				Class<?> superclass = ByteCodeClassLoader.class.getClassLoader().loadClass("sun.reflect.DelegatingClassLoader");
				System.out.println(superclass.getDeclaredConstructors().length);
				String superclassname = superclass.getPackage().getName() + ".PublicDelegatingClassLoader";
				String internalname = superclassname.replace('.', '/');
				cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, internalname, null, Type.getType(superclass).getInternalName(), new String[] { });
				MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.getType(void.class), Type.getType(ClassLoader.class)), null, null);
				mv.visitCode();
				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitVarInsn(Opcodes.ALOAD, 1);
				mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(superclass), "<init>", Type.getConstructorDescriptor(superclass.getDeclaredConstructor(ClassLoader.class)), false);
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(0, 0);
				mv.visitEnd();
				cw.visitEnd();
				byte[] code = cw.toByteArray();
				getUnsafe().appendToBootstrapClassLoaderSearch(superclassname, new ByteArrayInputStream(code));
				superclass = ClassLoader.getSystemClassLoader().loadClass(superclassname);
				final Constructor<?> supercon = superclass.getDeclaredConstructor(ClassLoader.class);
				
				// Now make something nice based on our prybar
				internalname = (VmHacks.class.getPackage().getName() + "." + ByteCodeClassLoader.class.getSimpleName() + "Extended").replace('.', '/');
				InputStream is = ByteCodeClassLoader.class.getClassLoader().getResourceAsStream(ByteCodeClassLoader.class.getCanonicalName().replace('.', '/') + ".class");
				ClassReader cr = new ClassReader(is);
				final SimpleRemapper remapper = new SimpleRemapper(Type.getInternalName(ByteCodeClassLoader.class), internalname);
				cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
				ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, new ClassRemapper(cw, remapper))
				{
					private boolean noheader = true;
					
					public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
					{
						if (noheader)
						{
							cv.visit(version, access, name, signature, superName, interfaces);
							noheader = false;
						}
					}
					
					public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
					{
						MethodVisitor ret = cv.visitMethod(access, name, desc, signature, exceptions);
						if ("<init>".equals(name))
						{
//							MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.getType(void.class), Type.getType(ClassLoader.class)), null, null);
//							mv.visitCode();
//							mv.visitVarInsn(Opcodes.ALOAD, 0);
//							mv.visitVarInsn(Opcodes.ALOAD, 1);
//							Class<?> superclass = supercon.getDeclaringClass();
//							mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(superclass), "<init>", Type.getConstructorDescriptor(supercon), false);
//							mv.visitInsn(Opcodes.RETURN);
//							mv.visitMaxs(0, 0);
//							mv.visitEnd();
							ret = new MethodVisitor(Opcodes.ASM5, ret)
							{
								public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
								{
									if (opcode == Opcodes.INVOKESPECIAL)
									{
										mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(supercon.getDeclaringClass()), "<init>", Type.getConstructorDescriptor(supercon), false);
									}
									else
									{
										mv.visitMethodInsn(opcode, owner, name, desc, itf);
									}
								};
							};
						}
						ret = new MethodRemapper(ret, remapper);
						return ret;
					}
				};
				
				cv.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, internalname, null, Type.getType(superclass).getInternalName(), new String[] { Type.getType(IByteCodeClassLoader.class).getInternalName() });
				cr.accept(cv, 0);
				cv.visitEnd();
				ClassLoader basecl = IByteCodeClassLoader.class.getClassLoader();
				basecl = basecl == null ? ClassLoader.getSystemClassLoader() : basecl;
				ByteCodeClassLoader bcl = new ByteCodeClassLoader(basecl);
				code = cw.toByteArray();
				ret = bcl.doDefineClassInParent(null, code, 0, code.length, superclass.getProtectionDomain());
				System.out.println("superrr:  " + ret.getSuperclass());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	/**
	 *  Tests if the extended classloader is available and functional.
	 *  @return True, if available.
	 */
	private static final boolean hasExtendedClassLoader()
	{
		boolean ret = false;
		if (EXTENDED_BYTECODE_CLASSLOADER != null)
		{
			IByteCodeClassLoader testcl = getExtendedByteCodeClassLoader(VmHacks.class.getClassLoader());
			ret = testcl != null;
//			System.out.println("Has extended bytecode classloader!");
		}
		else
		{
			ret = false;
		}
		return ret;
	}
	
	/**
	 *  Creates a temporary .jar.
	 */
	private static File createTempJar(String classname, InputStream classcontent, Manifest man)
	{
		man = man == null ? new Manifest() : man;
		JarOutputStream os = null;
        
        File jar = null;
        try
        {
        	jar = File.createTempFile("jadextmp", ".jar");
            jar.deleteOnExit();
            os = new JarOutputStream(new FileOutputStream(jar), man);
            String clname = classname.replace('.', '/') + ".class";
            JarEntry e = new JarEntry(clname);
            os.putNextEntry(e);
            SUtil.copyStream(classcontent, os);
            os.closeEntry();
        }
        catch (Exception e)
        {
        }
        finally
        {
            if (os != null)
            	SUtil.close(os);
        }
        return jar;
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
		cicm.setAccessible(true);
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
	 *  Tests if native access is available.
	 *  
	 *  @return True, if native access is available.
	 */
	private static final boolean hasNative()
	{
		boolean ret = false;
		try
		{
			new NativeHelper();
			ret = true;
		}
		catch (Throwable t)
		{
		}
		return ret;
	}
	
	/**
     *  Access to unsafe operations.
     */
	public static final class Unsafe
	{
		/** sun.misc.Unsafe if available. */
		private Class<?> unsafeclass;
		
		/** sun.misc.Unsafe instance, if available. */
		private Object unsafeinstance = null;
		
		// Start sun.misc.Unsafe methods.
		
		/** The defineClass method. */
		private IMethodInvoker defineclass;
		
		/** The putBoolean method. */
		private IMethodInvoker putboolean;
		
		/** The putBoolean method. */
		private IMethodInvoker objectFieldOffset;
		
		// End sun.misc.Unsafe methods.
		
		/** setAccessible() override field. */
		private Field setaccessibleoverride;
		
		/** setAccessible() override field offset. */
		private Long setaccessibleoverrideoffset;
		
		/** The instrumentation access if available. */
		private Instrumentation instrumentation;
		
		/** Classloader class injection map. */
		private Map<Object[], Class<?>>  injectionclassstore;
		
		/** Name of the class store. */
		private String classstoreclassname;
		
		/** Classloader classes that have been enhanced with injections. */
		private Map<Class<?>, Unsafe> enhancedloaders = new WeakHashMap<Class<?>, Unsafe>();
		
		/** Synchronization lock for the instrumentation agent. */
		private Semaphore instagentlock = new Semaphore(0);
		
		/**
		 *  Creates the Unsafe.
		 */
		protected Unsafe()
		{
		}
		
//		public void markAsVerified(Class<?> clazz)
//		{
//			
//			sun.misc.Unsafe u = null;
//			Object obj = null;
//			try
//			{
//				Class<?> unsafeclass = Class.forName("sun.misc.Unsafe");
//				Field instancefield = unsafeclass.getDeclaredField("theUnsafe");
//				instancefield.setAccessible(true);
//				u = (sun.misc.Unsafe) instancefield.get(null);
//				obj = u.allocateInstance(clazz);
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//			long klass = (u.getInt(obj, 8L) & 0xFFFFFFFFL) << 3;
//			int miscflags = (int) (u.getByte(klass+354) & 0xFF);
//			System.out.println("initstate " + clazz.getName() + ": " + miscflags);
////			miscflags |= 1 << 2;
////			u.putChar(klass+252, (char) 16384);
////			u.putChar(klass+252, (char)miscflags);
//			System.out.println("initstate2: " + miscflags);
//		}
		
		/**
		 *  Sets reflective object accessible without checks if native support is available.
		 *  
		 *  @param accobj The accessible object.
		 *  @param flag The flag value.
		 */
		public void setAccessible(AccessibleObject accobj, boolean flag)
		{
			if (HAS_NATIVE && setaccessibleoverride != null)
			{
				NativeHelper.setAccessible(setaccessibleoverride.getName(), accobj, flag);
				
			}
			else if (putboolean != null && setaccessibleoverrideoffset != null)
			{
				putboolean.invoke(null, accobj, setaccessibleoverrideoffset, flag);
			}
			else if (setaccessibleoverride != null)
			{
				try
				{
					if (!setaccessibleoverride.isAccessible())
						setaccessibleoverride.setAccessible(true);
				
					setaccessibleoverride.set(accobj, true);
				}
				catch (Exception e)
				{
					accobj.setAccessible(flag);
				}
			}
			else
			{
				accobj.setAccessible(flag);
			}
		}
		
		/**
	     *  Access to sun.misc.Unsafe or equivalent.
	     */
		public Class<?> defineClass(String name, byte[] b, int off, int len, ClassLoader loader, ProtectionDomain pd)
	    {
			if (HAS_NATIVE)
			{
				return NativeHelper.defineClass(name, b, loader);
			}
			else if (defineclass != null)
			{
				return (Class<?>) defineclass.invoke(unsafeinstance, name, b, off, len, loader, pd == null ? loader.getClass().getProtectionDomain() : pd);
			}
			else
			{
				Class<?> ret = null;
				try
				{
					Method dc = ClassLoader.class.getMethod("defineClass", String.class, byte[].class, int.class, int.class, ProtectionDomain.class);
					setAccessible(dc, true);
					ret = (Class<?>) dc.invoke(loader, name, b, off, len, loader, pd == null ? loader.getClass().getProtectionDomain() : pd);
				}
				catch (Exception e)
				{
				}
				return ret;
			}
	    }
		
		/**
		 *  Checks if instrumentation is available.
		 *  
		 *  @return True, if instrumentation is available.
		 */
		public boolean hasInstrumentation()
		{
			return instrumentation != null;
		}
		
		/**
		 *  Checks if redefineClassIndirect() is available.
		 *  
		 *  @return True, if indirect redefinition is available. 
		 */
		public boolean hasIndirectRedefinition()
		{
			return !NO_ASM && instrumentation != null;
		}
		
		/**
		 *  Redefine class byte code. Check HAS_INSTRUMENTATION before use.
		 *  Uses indirect route via classloader enhancement, more likely to work.
		 * 
		 *  @param clazz Class to be redefined.
		 *  @param bytecode The new byte code.
		 *  @return Redefined class.
		 */
		public Class<?> redefineClassIndirect(final Class<?> clazz, final byte[] bytecode)
		{
			ClassLoader cl = clazz.getClassLoader();
			Class<?> ret = clazz;
			try
			{
				enhanceClassLoader(cl);
				IByteCodeClassLoader bcl = SASM.createByteCodeClassLoader(cl);
				ret = bcl.doDefineClass(bytecode);
				injectionclassstore.put(new Object[] { cl, clazz.getName() }, ret);
			}
			catch (Exception e)
			{
			}
			return ret;
		}
		
		/**
		 *  Redefine class byte code. Check HAS_INSTRUMENTATION before use.
		 * 
		 *  @param clazz Class to be redefined.
		 *  @param bytecode The new byte code.
		 */
		public void redefineClass(final Class<?> clazz, final byte[] bytecode)
		{
			ClassDefinition def = new ClassDefinition(clazz, bytecode);
			try
			{
				instrumentation.redefineClasses(def);
			}
			catch (Exception e)
			{
				SUtil.throwUnchecked(e);
			}
		}
		
		/**
		 *  Appends a new class to the bootstrap classloader.
		 *  
		 *  @param classname The class name.
		 *  @param classcontent The bytecode.
		 */
		public void appendToBootstrapClassLoaderSearch(String classname, byte[] classcontent)
		{
			appendToBootstrapClassLoaderSearch(classname, new ByteArrayInputStream(classcontent));
		}
		
		/**
		 *  Appends a new class to the bootstrap classloader.
		 *  
		 *  @param classname The class name.
		 *  @param classcontent The bytecode.
		 */
		public void appendToBootstrapClassLoaderSearch(String classname, InputStream classcontent)
		{
			try
			{
				File file = createTempJar(classname, classcontent, null);
				JarFile jarfile = new JarFile(file);
				instrumentation.appendToBootstrapClassLoaderSearch(jarfile);
			}
			catch (Exception e)
			{
				SUtil.throwUnchecked(e);
			}
		}
		
		/**
		 *  Creates instrumentation, if available.
		 */
		@SuppressWarnings("unchecked")
		protected void startInstrumentationAgent()
		{
			if (NO_ASM)
				return;
			
			File jar = null;
			try
			{
				Manifest man = new Manifest();
		        Attributes attrs = man.getMainAttributes();
		        attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
		        attrs.put(new Attributes.Name("Premain-Class"), VmHacksAgent.class.getName());
		        attrs.put(new Attributes.Name("Agent-Class"), VmHacksAgent.class.getName());
		        attrs.put(new Attributes.Name("Can-Redefine-Classes"), "true");
		        attrs.put(new Attributes.Name("Can-Retransform-Classes"), "true");
		        
		        InputStream is = VmHacksAgent.class.getResourceAsStream(VmHacksAgent.class.getSimpleName() + ".class");
		        jar = createTempJar(VmHacksAgent.class.getName(), is, man);
		        SUtil.close(is);
		        
		        boolean hasagent = false;
		        if (HAS_NATIVE)
		        {
			        try
					{
						Class.forName("sun.instrument.InstrumentationImpl");
						InstrumentStarter.startAgent(jar.getAbsolutePath());
						hasagent = true;
					}
					catch (Exception e1)
					{
					}
		        }
		        
		        if (!hasagent)
		        {
		        	try
		        	{
				        String javahome = System.getProperty("java.home");
						File toolsjar = new File(javahome + File.separator + "lib" + File.separator + "tools.jar");
						if (!toolsjar.exists())
						{
							toolsjar = new File(javahome + File.separator + ".." + File.separator + "lib" + File.separator + "tools.jar");
						}
						
						ClassLoader toolsloader = VmHacks.class.getClassLoader();
						if (toolsjar.exists())
						{
							toolsloader = new URLClassLoader(new URL[] { toolsjar.toURI().toURL() });
						}
				        Class<?> vmclass = toolsloader.loadClass("com.sun.tools.attach.VirtualMachine");
				        
				        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
				        
				        Method attach = vmclass.getDeclaredMethod("attach", String.class);
				        Object vm = attach.invoke(null, pid);
				        Method loadagent = vmclass.getDeclaredMethod("loadAgent", String.class);
				        loadagent.invoke(vm, jar.getAbsolutePath());	        
		        	}
		        	catch (Exception e1)
		        	{
		        	}
		        }
		        
		        if (hasagent)
		        	instagentlock.tryAcquire(300, TimeUnit.MILLISECONDS);
		        
		        if (hasInstrumentation())
		        {
			        // Inject the class storage.
			        classstoreclassname = VmHacks.class.getPackage().getName() + "." + "ClassStore";
			        try
					{
				        is = null;
				        is = VmHacks.class.getClassLoader().getResourceAsStream(classstoreclassname.replace('.', '/') + ".class");
						appendToBootstrapClassLoaderSearch(classstoreclassname, is);
					
						Class<?> storeclass = Class.forName(classstoreclassname);
						Field clinj = storeclass.getField("STORE");
						injectionclassstore = (Map<Object[], Class<?>>) clinj.get(null);
					}
					catch (Exception e)
					{
					}
		        }
			}
			catch (Exception e)
			{
			}
		}
		
		/**
		 *  Sets the instrumentation, called by VmHacksAgent.
		 *  
		 *  @param inst The instrumentation. 
		 */
		protected void setInstrumentation(Instrumentation inst)
		{
			instrumentation = inst;
			instagentlock.release();
		}
		
		/**
		 *  Initialization step after constructor to allow bootstrapping.
		 */
		protected void init()
		{
			try
			{
				unsafeclass = Class.forName("sun.misc.Unsafe");
			}
			catch (Exception e)
			{
			}
			
			if (unsafeclass != null)
				unsafeinstance = getUnsafe(unsafeclass);
			
			if (unsafeinstance != null)
			{
				defineclass = getUnsafeMethod("defineClass", String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class);
				putboolean = getUnsafeMethod("putBoolean", Object.class, long.class, boolean.class);
				objectFieldOffset = getUnsafeMethod("objectFieldOffset", Field.class);
			}
			
			try
			{
				// setAccessible override flag
				try
				{
					setaccessibleoverride = AccessibleObject.class.getDeclaredField("override");
				}
				catch (Exception e)
				{
					try
					{
						// Sometimes called flag?
						setaccessibleoverride = AccessibleObject.class.getDeclaredField("flag");
					}
					catch (Exception e1)
					{
					}
				}
				
				if (setaccessibleoverride != null && objectFieldOffset != null)
					setaccessibleoverrideoffset = (Long) objectFieldOffset.invoke(unsafeinstance, setaccessibleoverride);
			}
			catch (Exception e)
			{
				SUtil.throwUnchecked(e);
			}
			
			
		}
		
		/**
		 *  Enhance a classloader to allow injections.
		 *  @param cl The classloader.
		 */
		private void enhanceClassLoader(ClassLoader cl)
		{
			synchronized(enhancedloaders)
			{
				if (enhancedloaders.containsKey(cl.getClass()))
					return;
				
				Class<?> clclazz = cl.getClass();
				Method m = null;
				while (m == null)
				{
					try
					{
						m = clclazz.getDeclaredMethod("loadClass", String.class, boolean.class);
					}
					catch (Exception e)
					{
						clclazz = clclazz.getSuperclass();
						if (Object.class.equals(clclazz))
							SUtil.throwUnchecked(e);
					}
				}
				
				if (enhancedloaders.containsKey(clclazz))
				{
					enhancedloaders.put(clclazz, Unsafe.this);
					return;
				}
				
				InputStream is = cl.getResourceAsStream(clclazz.getName().replace('.', '/') + ".class");
				ClassReader cr = null;
				
				try
				{
					cr = new ClassReader(is);
				}
				catch (Exception e)
				{
				}
				
				final Method loadclass = m; 
				
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
				ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw)
				{
					public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
					{
						
						MethodVisitor ret = cv.visitMethod(access, name, desc, signature, exceptions);
						if (loadclass.getName().equals(name) && "(Ljava/lang/String;Z)Ljava/lang/Class<*>;".equals(signature))
						{
							ret.visitCode();
							ret = new MethodVisitor(Opcodes.ASM5, ret)
							{
								public void visitCode()
								{
								};
								
								public void visitMaxs(int maxStack, int maxLocals)
								{
									mv.visitMaxs(0, 0);
								};
							};
							
							try
							{
								Class<?> storeclass = Class.forName(classstoreclassname);
								Field clinj = storeclass.getField("STORE");
								ret.visitFieldInsn(Opcodes.GETSTATIC, Type.getInternalName(clinj.getDeclaringClass()), clinj.getName(), Type.getDescriptor(clinj.getType()));
								
								ret.visitTypeInsn(Opcodes.CHECKCAST, Type.getDescriptor(Map.class));
								
								ret.visitInsn(Opcodes.ICONST_2);
								
								ret.visitTypeInsn(Opcodes.ANEWARRAY, Type.getInternalName(Object.class));
								ret.visitInsn(Opcodes.DUP);
								ret.visitInsn(Opcodes.ICONST_0);
								ret.visitVarInsn(Opcodes.ALOAD, 0);
								ret.visitInsn(Opcodes.AASTORE);
								ret.visitInsn(Opcodes.DUP);
								ret.visitInsn(Opcodes.ICONST_1);
								ret.visitVarInsn(Opcodes.ALOAD, 1);
								ret.visitInsn(Opcodes.AASTORE);
								
								Method get = Map.class.getMethod("get", Object.class);
								ret.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.getInternalName(Map.class), get.getName(), Type.getMethodDescriptor(get), true);
								
//								ret.visitVarInsn(Opcodes.ALOAD, 0);
//								
//								ret.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.getInternalName(Map.class), get.getName(), Type.getMethodDescriptor(get), true);
//								
//								ret.visitInsn(Opcodes.DUP);
//								ret.visitJumpInsn(Opcodes.IFNULL, cont);
//								
//								ret.visitTypeInsn(Opcodes.CHECKCAST, Type.getDescriptor(Map.class));
//								
//								ret.visitVarInsn(Opcodes.ALOAD, 1);
//								ret.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.getInternalName(Map.class), get.getName(), Type.getMethodDescriptor(get), true);
//								
								Label cont = new Label();
								ret.visitInsn(Opcodes.DUP);
								ret.visitJumpInsn(Opcodes.IFNULL, cont);
								
								ret.visitInsn(Opcodes.ARETURN);
								ret.visitLabel(cont);
							}
							catch (Exception e)
							{
							}
						}
						return ret;
					}
				};
				
				try
				{
					cr.accept(cv, 0);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				byte[] newcl = cw.toByteArray();
				
				redefineClass(clclazz, newcl);
				enhancedloaders.put(clclazz, Unsafe.this);
			}
		}
		
		/**
		 *  Gets the unsafe instance from the class.
		 *  
		 *  @param unsafeclazz sun.misc.Unsafe if available.
		 *  @return Instance of the class.
		 */
		private Object getUnsafe(Class<?> unsafeclazz)
		{
			Object ret = null;
			try
			{
				Field instancefield = null;
				try
				{
					// Field name in regular Java, normally...
					instancefield = unsafeclazz.getDeclaredField("theUnsafe");
				}
				catch (Exception e)
				{
				}
				
				if (instancefield == null)
				{
					try
					{
						// Field name in Android, normally...
						instancefield = unsafeclazz.getDeclaredField("THE_ONE");
					}
					catch (Exception e)
					{
					}
				}
				
				
				if (instancefield != null)
				{
					// attempt to acquire the singleton instance.
					try
					{
						instancefield.setAccessible(true);
						ret = instancefield.get(null);
					}
					catch (Exception e)
					{
					}
				}
				
				if (ret == null)
				{
					// Okay, last chance, just instantiate a new instance...
					Constructor<?> c = unsafeclazz.getConstructor();
					c.setAccessible(true);
					ret = c.newInstance();
				}
			}
			catch (Exception e)
			{
			}
			
			return ret;
		}
		
		private IMethodInvoker getUnsafeMethod(String name, Class<?>... params)
		{
			IMethodInvoker ret = null;
			try
			{
				Method method = null;
				method = unsafeclass.getDeclaredMethod(name, params);
				IByteCodeClassLoader bcl = SASM.createByteCodeClassLoader(method.getDeclaringClass().getClassLoader(), SASM.class.getClassLoader());
				ret = SInvocation.newInvoker(method, bcl);
			}
			catch (Exception e)
			{
			}
			return ret;
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
