package jadex.bytecode.vmhacks;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

import jadex.bytecode.IByteCodeClassLoader;
import jadex.bytecode.SASM;
import jadex.bytecode.fastinvocation.IMethodInvoker;
import jadex.bytecode.fastinvocation.SInvocation;
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
	
	/** Flag if default / protected access via ASM is available. */
	public static boolean DEFAULT_ACCESS = true;
	
	/** Flag if private access via ASM is available. */
	public static boolean PRIVATE_ACCESS = true;
	
	/** Access to unsafe operations. */
	private static final Unsafe UNSAFE;
	
	/** Class of the Extended ByteCodeClassLoader if available. */
	private static final Class<?> EXTENDED_BYTECODE_CLASSLOADER;
	
	static
	{
		// Test if native support is available...
		boolean hasnative = false;
		try
		{
			new NativeHelper();
			hasnative = true;
		}
		catch (Throwable t)
		{
		}
		HAS_NATIVE = hasnative;
		
		// No ASM bytecode on Android...
		if (SReflect.isAndroid())
			NO_ASM = true;
		
		UNSAFE = new Unsafe();
		UNSAFE.init();
		
		EXTENDED_BYTECODE_CLASSLOADER = createExtendedClassLoaderClass();
		if (EXTENDED_BYTECODE_CLASSLOADER != null)
		{
			IByteCodeClassLoader testcl = getExtendedByteCodeClassLoader(VmHacks.class.getClassLoader());
			HAS_EXTENDED_BYTECODE_CLASSLOADER = testcl != null;
			System.out.println("Has extended bytecode classloader!");
		}
		else
		{
			HAS_EXTENDED_BYTECODE_CLASSLOADER = false;
		}
		
		AccessTestClass atcobj = new AccessTestClass();
		IByteCodeClassLoader dummycl = SASM.createByteCodeClassLoader(AccessTestClass.class.getClassLoader());
		Method cicm = null;
		try
		{
			cicm = SInvocation.class.getDeclaredMethod("createInvokerClass", new Class<?>[] { IByteCodeClassLoader.class, Method.class });
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		cicm.setAccessible(true);
		boolean acc = false;
		try
		{
			Method m = AccessTestClass.class.getDeclaredMethod("defaultTest", (Class<?>[]) null);
			Class<?> invclass = (Class<?>) cicm.invoke(null, dummycl, m);
			if (invclass != null)
			{
				Constructor<?> c = invclass.getConstructor((Class[]) null);
				IMethodInvoker inv = (IMethodInvoker) c.newInstance((Object[]) null);
				inv.invoke(atcobj, (Object[]) null);
				acc = true;
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		DEFAULT_ACCESS = acc;
		
		dummycl = SASM.createByteCodeClassLoader(AccessTestClass.class.getClassLoader());
		acc = false;
		try
		{
			Method m = AccessTestClass.class.getDeclaredMethod("privateTest", (Class<?>[]) null);
			Class<?> invclass = (Class<?>) cicm.invoke(null, dummycl, m);
			if (invclass != null)
			{
				Constructor<?> c = invclass.getConstructor((Class[]) null);
				IMethodInvoker inv = (IMethodInvoker) c.newInstance((Object[]) null);
				inv.invoke(atcobj, (Object[]) null);
				acc = true;
			}
		}
		catch (Throwable t)
		{
		}
		PRIVATE_ACCESS = acc;
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
			}
		}
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
		if (!NO_ASM)
		{
			try
			{
//				InputStream is = ByteCodeClassLoader.class.getClassLoader().getResourceAsStream(ByteCodeClassLoader.class.getCanonicalName().replace('.', '/') + ".class");
//				ClassReader cr = new ClassReader(is);
//				ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
//				Class<?> superclass = ByteCodeClassLoader.class.getClassLoader().loadClass("sun.reflect.DelegatingClassLoader");
//				String internalname = (superclass.getPackage().getName() + "." + ByteCodeClassLoader.class.getSimpleName() + "Extended").replace('.', '/');
//				cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, internalname, null, Type.getType(superclass).getInternalName(), new String[] { Type.getType(IByteCodeClassLoader.class).getInternalName() });
//				cw.visitEnd();
//				ByteCodeClassLoader bcl = new ByteCodeClassLoader(ClassLoader.getSystemClassLoader());
//				byte[] code = cw.toByteArray();
//				ret = bcl.doDefineClassInParent(null, code, 0, code.length, superclass.getProtectionDomain());
			}
			catch (Exception e)
			{
			}
		}
		return ret;
	}
	
	/**
     *  Access to unsafe operations.
     */
	public static class Unsafe
	{
		/** Instance, if available. */
		private Object instance = null;
		
		/** The defineClass method. */
		private IMethodInvoker defineclass;
		
		/** The putBoolean method. */
		private IMethodInvoker putboolean;
		
		/** setAccessible() override field. */
		private Field setaccessibleoverride;
		
		/** setAccessible() override field offset. */
		private Long setaccessibleoverrideoffset;
		
		/**
		 *  Creates the Unsafe.
		 */
		private Unsafe()
		{
		}
		
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
				return (Class<?>) defineclass.invoke(instance, name, b, off, len, loader, pd == null ? loader.getClass().getProtectionDomain() : pd);
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
		 *  Initialization step after constructor to allow bootstrapping.
		 */
		protected void init()
		{
			if (!HAS_NATIVE)
			{
				Class<?> unsafeclass = null;
				try
				{
					unsafeclass = Class.forName("sun.misc.Unsafe");
					
					Field instancefield = null;
					try
					{
						// Field name in regular Java, normally...
						instancefield = unsafeclass.getDeclaredField("theUnsafe");
					}
					catch (Exception e)
					{
					}
					
					if (instancefield == null)
					{
						try
						{
							// Field name in Android, normally...
							instancefield = unsafeclass.getDeclaredField("THE_ONE");
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
							instance = instancefield.get(null);
						}
						catch (Exception e)
						{
						}
					}
					
					if (instance == null)
					{
						// Okay, last chance, just instantiate a new instance...
						Constructor<?> c = unsafeclass.getConstructor();
						c.setAccessible(true);
						instance = c.newInstance();
					}
					
					Method method = null;
					method = unsafeclass.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class);
					IByteCodeClassLoader bcl = SASM.createByteCodeClassLoader(method.getDeclaringClass().getClassLoader(), SASM.class.getClassLoader());
					defineclass = SInvocation.newInvoker(method, bcl);
					
					Method objectfieldoffset = unsafeclass.getDeclaredMethod("objectFieldOffset", Field.class);
					
					method = unsafeclass.getDeclaredMethod("putBoolean", Object.class, long.class, boolean.class);
//					bcl = new ByteCodeClassLoader(method.getDeclaringClass().getClassLoader(), SASM.class.getClassLoader());
					putboolean = SInvocation.newInvoker(method, bcl);
					
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
					
					if (setaccessibleoverride != null)
					{
						try
						{
							setaccessibleoverrideoffset = (Long) objectfieldoffset.invoke(null, setaccessibleoverride);
						}
						catch (Exception e)
						{
						}
					}
				}
				catch (Exception e)
				{
					SUtil.throwUnchecked(e);
				}
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
