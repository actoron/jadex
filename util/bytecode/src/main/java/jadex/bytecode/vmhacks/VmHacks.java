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
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

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
	/** Access to unsafe operations. */
	private static volatile Unsafe UNSAFE;
	
	/** Globally disable all VM Hacks. */
	public static boolean DISABLE = false;
	
	/** Disable all instrumentation-based Hacks. */
	public static boolean DISABLE_INSTRUMENTATION = true;
	
	/** Set to true to see debug infos during startup. */
	public static boolean DEBUG = false;
	
	/**
	 *  Provides access to unsafe operations.
	 *  @return The Unsafe object.
	 */
	public static final Unsafe get()
	{
		if (UNSAFE == null)
		{
			synchronized(VmHacks.class)
			{
				if (UNSAFE == null)
				{
					UNSAFE = new Unsafe();
					
					if (!DISABLE)
					{
						UNSAFE.init();
					}
					
					if (DEBUG)
						System.out.println(UNSAFE.toString());
				}
			}
		}
		return UNSAFE;
	}
	
	/**
     *  Access to unsafe operations.
     */
	public static final class Unsafe
	{
		/** Directory for temporary jar files. */
		protected static final File TEMP_JAR_DIR = new File(System.getProperty("java.io.tmpdir") + File.separator + ".jadex" + File.separator + "tmpjars");
		
		/** Set this to true to switch to fallback mode for invocation */
		private boolean asm = false;
		
		/** The native support if available. */
		private INativeHelper nativehelper = null;
		
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
		
		/** The instrumentation command queue. */
		private LinkedBlockingQueue<InstrumentationCommand> instrumentationcommandqueue;
		
		/** The instrumentation access if available. */
//		private Instrumentation instrumentation;
		
		/** Classloader class injection map. */
		private Map<Object[], Class<?>>  injectionclassstore;
		
		/** Classloader classes that have been enhanced with injections. */
		private Map<Class<?>, Unsafe> enhancedloaders = new WeakHashMap<Class<?>, Unsafe>();
		
		/**
		 *  Creates the Unsafe.
		 */
		Unsafe()
		{
		}
		
		// --------- Method for checking available capabilities. -----------------
		
		/**
		 *  Tests if ASM is available.
		 *  
		 *  @return True, if ASM is available.
		 */
		public boolean hasAsm()
		{
			return asm;
		}
		
		/**
		 *  Tests if native access is available.
		 *  
		 *  @return True, if native access is available.
		 */
		public boolean hasNative()
		{
			return nativehelper != null;
		}
		
		/**
		 *  Checks if instrumentation is available.
		 *  
		 *  @return True, if instrumentation is available.
		 */
		public boolean hasInstrumentation()
		{
			return instrumentationcommandqueue != null;
		}
		
		/**
		 *  Checks if redefineClassIndirect() is available.
		 *  
		 *  @return True, if indirect redefinition is available. 
		 */
		public boolean hasIndirectRedefinition()
		{
			return asm && instrumentationcommandqueue != null;
		}
		
		// --------- Methods providing functionality. -----------------
		
		/**
		 *  Attempts to change the user of the process to the given name.
		 *  If set to null, a list of default user accounts is tried.
		 *  
		 *  @param username The target user name, set to null for a list of default user account.
		 *  @return True, if successful, false if the attempt probably failed.
		 */
		public boolean tryChangeUser(String username)
		{
			boolean ret = false;
			if (hasNative())
			{
				if (username == null)
				{
					String[] defaccounts = new String[] { "jadex", "nobody", "www", "daemon" };
					for (int i = 0; i < defaccounts.length && !ret; ++i)
						ret = nativehelper.tryChangeUser(defaccounts[i]);
				}
				else
				{
					ret = nativehelper.tryChangeUser(username);
				}
			}
			
			return ret;
		}
		
		/**
		 *  Sets reflective object accessible without checks if native support is available.
		 *  
		 *  @param accobj The accessible object.
		 *  @param flag The flag value.
		 */
		public void setAccessible(AccessibleObject accobj, boolean flag)
		{
			if (hasNative() && nativehelper.canSetAccessible())
			{
				nativehelper.setAccessible(setaccessibleoverride.getName(), accobj, flag);
				
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
			if (hasNative())
			{
				return nativehelper.defineClass(name, b, loader);
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
					Method dc = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class, ProtectionDomain.class);
					setAccessible(dc, true);
					ret = (Class<?>) dc.invoke(loader, name, b, off, len, pd == null ? loader.getClass().getProtectionDomain() : pd);
				}
				catch (Exception e)
				{
				}
				return ret;
			}
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
				injectClassIntoStore(injectionclassstore, cl, clazz.getName(), ret);
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
			runInstrumentationCommand(new InstrumentationCommand()
			{
				public void run(Instrumentation instrumentation)
				{
					try
					{
						instrumentation.redefineClasses(def);
					}
					catch (Exception e)
					{
						SUtil.throwUnchecked(e);
					}
				}
			});
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
				runInstrumentationCommand(new InstrumentationCommand()
				{
					public void run(Instrumentation instrumentation)
					{
						try
						{
							instrumentation.appendToBootstrapClassLoaderSearch(jarfile);
						}
						catch (Exception e)
						{
							SUtil.throwUnchecked(e);
						}
					}
				});
			}
			catch (Exception e)
			{
				SUtil.throwUnchecked(e);
			}
		}
		
		/**
		 *  Debug message.
		 */
		public String toString()
		{
			String ret = getClass().getName(); 
			ret += " asm=" + asm;
			ret += " native=" + hasNative();
			ret += " javaunsafe=" + unsafeinstance;
			ret += " instrumentation=" + instrumentationcommandqueue;
			return ret;
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
		 *  Creates an extended class loader with additional privileges if available.
		 *  
		 *  @param parents ClassLoader parents.
		 *  @return The ClassLoader.
		 */
//		public IByteCodeClassLoader getExtendedByteCodeClassLoader(ClassLoader... parents)
//		{
//			IByteCodeClassLoader ret = null;
//			if (EXTENDED_BYTECODE_CLASSLOADER != null)
//			{
//				try
//				{
//					@SuppressWarnings("unchecked")
//					Constructor<IByteCodeClassLoader> c = (Constructor<IByteCodeClassLoader>) EXTENDED_BYTECODE_CLASSLOADER.getConstructor(ClassLoader[].class);
//					ret = c.newInstance(new Object[] { parents });
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//			ret = null;
//			return ret;
//		}
		
		/**
		 *  Initialization step after constructor to allow bootstrapping.
		 */
		protected void init()
		{
			asm = !SReflect.isAndroid();
			
			LoggerFilterStore.inject();
			
			try
			{
				nativehelper = new NativeHelper();
			}
			catch (Throwable t)
			{
			}
			
			try
			{
				unsafeclass = Class.forName("sun.misc.Unsafe");
			}
			catch (Exception e)
			{
			}
			
			if (unsafeclass != null)
				unsafeinstance = getSunUnsafe(unsafeclass);
			
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
			}
			catch (Exception e)
			{
				SUtil.throwUnchecked(e);
			}
			
			if (unsafeinstance != null)
			{
				defineclass = getSunUnsafeMethod("defineClass", String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class);
				putboolean = getSunUnsafeMethod("putBoolean", Object.class, long.class, boolean.class);
				objectFieldOffset = getSunUnsafeMethod("objectFieldOffset", Field.class);
			}
			
			if (setaccessibleoverride != null && objectFieldOffset != null)
				setaccessibleoverrideoffset = (Long) objectFieldOffset.invoke(unsafeinstance, setaccessibleoverride);
			
			startInstrumentationAgent();
		}
		
		/**
		 *  Creates instrumentation, if available.
		 */
		@SuppressWarnings("unchecked")
		private void startInstrumentationAgent()
		{
			if (!asm || DISABLE_INSTRUMENTATION)
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
		        if (hasNative())
		        {
//			        try
//					{
//						Class.forName("sun.instrument.InstrumentationImpl");
//						hasagent = nativehelper.startInstrumentationAgent(jar.getAbsolutePath());
//						if (DEBUG && hasagent)
//							System.out.println("Instrumentation agent loaded via internal API call.");
//					}
//					catch (Exception e1)
//					{
//					}
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
				        hasagent = true;
				        if (DEBUG)
							System.out.println("Instrumentation agent loaded via tools.jar.");
		        	}
		        	catch (Exception e1)
		        	{
		        	}
		        }
		        
		        if (hasagent)
		        	instrumentationcommandqueue = (LinkedBlockingQueue<InstrumentationCommand>) LoggerFilterStore.getStore().get(0);
		        
		        if (hasInstrumentation())
		        	injectionclassstore = (Map<Object[], Class<?>>) LoggerFilterStore.getStore().get(1);
		        else if (DEBUG)
		        	System.out.println("Instrumentation is unavailable.");
		        
		        jar.delete();
			}
			catch (Exception e)
			{
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
								InsnList nl = new InsnList();
								SASM.pushImmediate(nl, LoggerFilterStore.ID);
								nl.accept(ret);
								
								Method valueof = String.class.getMethod("valueOf", int.class);
								ret.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(String.class), valueof.getName(), Type.getMethodDescriptor(valueof), false);
								
								Method getlogger = Logger.class.getMethod("getLogger", String.class);
								ret.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Logger.class), getlogger.getName(), Type.getMethodDescriptor(getlogger), false);
								
								Method getfilter = Logger.class.getMethod("getFilter");
								ret.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Logger.class), getfilter.getName(), Type.getMethodDescriptor(getfilter), false);
								
								ret.visitTypeInsn(Opcodes.CHECKCAST, Type.getDescriptor(ArrayList.class));
								
								ret.visitInsn(Opcodes.ICONST_1);
								Method arrget = ArrayList.class.getMethod("get", int.class);
								ret.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(ArrayList.class), arrget.getName(), Type.getMethodDescriptor(arrget), false);
								
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
		private Object getSunUnsafe(Class<?> unsafeclazz)
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
		
		private IMethodInvoker getSunUnsafeMethod(String name, Class<?>... params)
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
		
		/** Run an instrumentation command */
		protected void runInstrumentationCommand(InstrumentationCommand command)
		{
			try
			{
				instrumentationcommandqueue.put(command);
			}
			catch (Exception e)
			{
				SUtil.throwUnchecked(e);
			}
			try
			{
				command.await(5000);
			}
			catch (TimeoutException e)
			{
				instrumentationcommandqueue = null;
				SUtil.throwUnchecked(e);
			}
		}
		
		/**
		 *  Creates an extended class loader class with additional privileges if available.
		 *  
		 *  @return The ClassLoader class.
		 */
//		private static final Class<?> createExtendedClassLoaderClass()
//		{
//			Class<?> ret = null;
//			
//			if (get().hasAsm() && get().hasInstrumentation())
//			{
//				try
//				{
//					// Future improvement, disable for now....
//					if (!HAS_EXTENDED_BYTECODE_CLASSLOADER)
//						return null;
//					
//					// Get out prybar into the base classloader...
//					ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
//					Class<?> superclass = ByteCodeClassLoader.class.getClassLoader().loadClass("sun.reflect.DelegatingClassLoader");
//					System.out.println(superclass.getDeclaredConstructors().length);
//					String superclassname = superclass.getPackage().getName() + ".PublicDelegatingClassLoader";
//					String internalname = superclassname.replace('.', '/');
//					cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, internalname, null, Type.getType(superclass).getInternalName(), new String[] { });
//					MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.getType(void.class), Type.getType(ClassLoader.class)), null, null);
//					mv.visitCode();
//					mv.visitVarInsn(Opcodes.ALOAD, 0);
//					mv.visitVarInsn(Opcodes.ALOAD, 1);
//					mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(superclass), "<init>", Type.getConstructorDescriptor(superclass.getDeclaredConstructor(ClassLoader.class)), false);
//					mv.visitInsn(Opcodes.RETURN);
//					mv.visitMaxs(0, 0);
//					mv.visitEnd();
//					cw.visitEnd();
//					byte[] code = cw.toByteArray();
//					get().appendToBootstrapClassLoaderSearch(superclassname, new ByteArrayInputStream(code));
//					superclass = ClassLoader.getSystemClassLoader().loadClass(superclassname);
//					final Constructor<?> supercon = superclass.getDeclaredConstructor(ClassLoader.class);
//					
//					// Now make something nice based on our prybar
//					internalname = (VmHacks.class.getPackage().getName() + "." + ByteCodeClassLoader.class.getSimpleName() + "Extended").replace('.', '/');
//					InputStream is = ByteCodeClassLoader.class.getClassLoader().getResourceAsStream(ByteCodeClassLoader.class.getCanonicalName().replace('.', '/') + ".class");
//					ClassReader cr = new ClassReader(is);
//					final SimpleRemapper remapper = new SimpleRemapper(Type.getInternalName(ByteCodeClassLoader.class), internalname);
//					cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
//					ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, new ClassRemapper(cw, remapper))
//					{
//						private boolean noheader = true;
//						
//						public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
//						{
//							if (noheader)
//							{
//								cv.visit(version, access, name, signature, superName, interfaces);
//								noheader = false;
//							}
//						}
//						
//						public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
//						{
//							MethodVisitor ret = cv.visitMethod(access, name, desc, signature, exceptions);
//							if ("<init>".equals(name))
//							{
////								MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.getType(void.class), Type.getType(ClassLoader.class)), null, null);
////								mv.visitCode();
////								mv.visitVarInsn(Opcodes.ALOAD, 0);
////								mv.visitVarInsn(Opcodes.ALOAD, 1);
////								Class<?> superclass = supercon.getDeclaringClass();
////								mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(superclass), "<init>", Type.getConstructorDescriptor(supercon), false);
////								mv.visitInsn(Opcodes.RETURN);
////								mv.visitMaxs(0, 0);
////								mv.visitEnd();
//								ret = new MethodVisitor(Opcodes.ASM5, ret)
//								{
//									public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
//									{
//										if (opcode == Opcodes.INVOKESPECIAL)
//										{
//											mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(supercon.getDeclaringClass()), "<init>", Type.getConstructorDescriptor(supercon), false);
//										}
//										else
//										{
//											mv.visitMethodInsn(opcode, owner, name, desc, itf);
//										}
//									};
//								};
//							}
//							ret = new MethodRemapper(ret, remapper);
//							return ret;
//						}
//					};
//					
//					cv.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, internalname, null, Type.getType(superclass).getInternalName(), new String[] { Type.getType(IByteCodeClassLoader.class).getInternalName() });
//					cr.accept(cv, 0);
//					cv.visitEnd();
//					ClassLoader basecl = IByteCodeClassLoader.class.getClassLoader();
//					basecl = basecl == null ? ClassLoader.getSystemClassLoader() : basecl;
//					ByteCodeClassLoader bcl = new ByteCodeClassLoader(basecl);
//					code = cw.toByteArray();
//					ret = bcl.doDefineClassInParent(null, code, 0, code.length, superclass.getProtectionDomain());
//					System.out.println("superrr:  " + ret.getSuperclass());
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//			return ret;
//		}
		
		/**
		 *  Tests if the extended classloader is available and functional.
		 *  @return True, if available.
		 */
//		private static final boolean hasExtendedClassLoader()
//		{
//			boolean ret = false;
//			if (EXTENDED_BYTECODE_CLASSLOADER != null)
//			{
//				IByteCodeClassLoader testcl = VmHacks.get().getExtendedByteCodeClassLoader(VmHacks.class.getClassLoader());
//				ret = testcl != null;
////				System.out.println("Has extended bytecode classloader!");
//			}
//			else
//			{
//				ret = false;
//			}
//			return ret;
//		}
		
		/**
		 *  Creates a temporary .jar.
		 */
		private static File createTempJar(String classname, InputStream classcontent, Manifest man)
		{
			if(!TEMP_JAR_DIR.exists())
				TEMP_JAR_DIR.mkdirs();
			
			man = man == null ? new Manifest() : man;
			JarOutputStream os = null;
	        
	        File jar = null;
	        try
	        {
	        	jar = File.createTempFile("jadextmp", ".jar");
	        	jar = new File(TEMP_JAR_DIR, SUtil.createPlainRandomId("tmpjar", 32)+".jar");
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
	}
	
	/**
	 *  Trampoline function for injection into the class redefinition store.
	 *  This allows the stack trace to come from VmHacks instead of VmHacks$Unsafe,
	 *  avoiding potential inner class naming inconsistencies.
	 *  
	 *  @param classstore The class store.
	 *  @param cl The targeted classloader.
	 *  @param classname Name of the class.
	 *  @param clazz The class.
	 */
	protected static final void injectClassIntoStore(Map<Object[], Class<?>> classstore, ClassLoader cl, String classname, Class<?> clazz)
	{
		classstore.put(new Object[] { cl, clazz.getName() }, clazz);
	}
	
	/**
	 *  Instrumentation command issued to the instrumentation agent.
	 *
	 */
	protected static abstract class InstrumentationCommand
	{
		/** The semaphore. */
		protected Semaphore sem = new Semaphore(0);
		
		/** Execute the command. */
		public final void execute(Instrumentation instrumentation)
		{
			try
			{
				run(instrumentation);
			}
			catch (Exception e)
			{
			}
			sem.release();
		}
		
		/** Custom command code. */
		public abstract void run(Instrumentation instrumentation);
		
		/** Wait for command to finish. */
		public void await()
		{
			try
			{
				sem.acquire();
				sem.release();
			}
			catch (InterruptedException e)
			{
			}
		}
		
		/** Wait for command to finish. */
		public void await(long timeout) throws TimeoutException
		{
			try
			{
				boolean acquired = sem.tryAcquire(timeout, TimeUnit.MILLISECONDS);
				if (acquired)
					sem.release();
				else
					throw new TimeoutException("Instrumentation command did not finish in time.");
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
