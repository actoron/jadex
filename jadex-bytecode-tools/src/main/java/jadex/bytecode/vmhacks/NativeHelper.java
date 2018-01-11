package jadex.bytecode.vmhacks;

import java.lang.reflect.AccessibleObject;
import java.util.HashMap;
import java.util.Map;

import com.sun.jna.Function;
import com.sun.jna.Library;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 *  Helper class using JNA to provide native features.
 *
 */
public final class NativeHelper implements INativeHelper
{
	/** JNI function GetVersion(). */
	private static final int JNIENV_GETVERSION_OFFSET = 4 * Pointer.SIZE;
	
	/** JNI function DefineClass(). */
	private static final int JNIENV_DEFINECLASS_OFFSET = 5 * Pointer.SIZE;
	
	/** JNI function FromReflectedMethod(). */
	private static final int JNIENV_FROMREFLECTEDMETHOD_OFFSET = 7 * Pointer.SIZE;
	
	/** JNI function CallObjectMethod(). */
	@SuppressWarnings("unused")
	private static final int JNIENV_CALLOBJECTMETHOD_OFFSET = 34 * Pointer.SIZE;
	
	/** JNI function CallLongMethod(). */
	@SuppressWarnings("unused")
	private static final int JNIENV_CALLLONGMETHOD_OFFSET = 52 * Pointer.SIZE;
	
	/** JNI function GetFieldID(). */
	private static final int JNIENV_GETFIELDID_OFFSET = 94 * Pointer.SIZE;
	
	/** JNI function SetBooleanField(). */
	private static final int JNIENV_SETBOOLEANFIELD_OFFSET = 105 * Pointer.SIZE;
	
	/** JNI function GetJavaVM(). */
//	private static final int JNIENV_GETJAVAVM_OFFSET = 219 * Pointer.SIZE;
	
	/** JavaVM function GetEnv(). */
	private static final int JAVAVM_GETENV_OFFSET = 6 * Pointer.SIZE;
	
	/** User JNI version 1.6 */
	private static final int JNI_VERSION_1_6 = 0x00010006;
	
	/** Pointer to the JavaVM. */
	private Pointer javavm;
	
	/** Field ID of AccessibleObject override field, if found. */
	private Pointer overridefieldid;
	
	/** Invocation options of JNI environment. */
	private Map<String, Object> envoptions = new HashMap<String, Object>();
	
	/**
	 *  Create helper.
	 */
	protected NativeHelper()
	{
		envoptions.put(Library.OPTION_ALLOW_OBJECTS, Boolean.TRUE);
		
		javavm = getJavaVm();
		
		if (Pointer.nativeValue(javavm) == 0)
			throw new IllegalStateException("NativeHelper could not find Java VM.");
		
		invokeEnv(JNIENV_GETVERSION_OFFSET, int.class);
		
		try
		{
			overridefieldid = invokeEnv(JNIENV_GETFIELDID_OFFSET, Pointer.class, AccessibleObject.class, "override", "Z");
		}
		catch (Throwable e)
		{
			try
			{
				overridefieldid = invokeEnv(JNIENV_GETFIELDID_OFFSET, Pointer.class, AccessibleObject.class, "flag", "Z");
			}
			catch (Throwable e1)
			{
			}
		}
	}
	
	/**
	 *  Sets reflective object accessible without checks.
	 *  
	 *  @param accobj The accessible object.
	 *  @param flag The flag value.
	 */
	public void setAccessible(String flagname, AccessibleObject accobj, boolean flag)
	{
		invokeEnv(JNIENV_SETBOOLEANFIELD_OFFSET, void.class, accobj, overridefieldid, flag);
	}
	
	/**
	 *  Tests if the setAccessible() method can be used.
	 *  @return True, if method can be used.
	 */
	public boolean canSetAccessible()
	{
		return overridefieldid != null;
	}
	
	/**
	 *  Gets a pointer to the VM.
	 *  
	 *  @return Pointer to VM.
	 */
	public long getVm()
	{
		return Pointer.nativeValue(javavm);
	}
	
	/**
     * Define a class in any ClassLoader.
     */
	public Class<?> defineClass(String name, byte[] b, ClassLoader loader)
	{
		Class<?> ret = (Class<?>) invokeEnv(JNIENV_DEFINECLASS_OFFSET, Object.class, name, loader, b, b.length);
		return ret;
	}
	
//	public Pointer getMethodId(Method method)
//	{
//		return invokeEnv(JNIENV_FROMREFLECTEDMETHOD_OFFSET, Pointer.class, method);
//	}
	
//	public Object invokeMethod(Object obj, Pointer methodid, Method method, Object... params)
//	{
////		Pointer methodid = invokeEnv(JNIENV_FROMREFLECTEDMETHOD_OFFSET, Pointer.class, method);
//		
//		return invokeEnv(JNIENV_CALLLONGMETHOD_OFFSET, long.class, obj, methodid);
//	}
	
	/**
	 *  Invokes function on the JNI environment.
	 * @param offset
	 * @param rettype
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T invokeEnv(long offset, Class<T> rettype, Object... params)
	{
		Function getenv = getFunctionFromTable(javavm, JAVAVM_GETENV_OFFSET);
		PointerByReference envref = new PointerByReference();
		getenv.invokeInt(new Object[] { javavm, envref, JNI_VERSION_1_6 });
		Pointer env = envref.getPointer().getPointer(0);
		
		if (Pointer.nativeValue(env) == 0)
			throw new IllegalStateException("Unable to acquire JNI environment.");
		
		Object[] extparams = null;
		if (params == null)
		{
			extparams = new Object[] { env };
		}
		else
		{
			extparams = new Object[params.length + 1];
			extparams[0] = env;
			System.arraycopy(params, 0, extparams, 1, params.length);
		}
		
		return (T) getFunctionFromTable(env, offset).invoke(rettype, extparams, envoptions);
	}
	
	/**
	 *  Gets function from a function table provided by JavaVM or JNIENV.
	 *  
	 *  @param p The pointer to JavaVM or JNIENV.
	 *  @param offset Function table offset.
	 *  @return The function.
	 */
	private Function getFunctionFromTable(Pointer p, long offset)
	{
		Pointer functiontable = p.getPointer(0);
		Pointer functionp = functiontable.getPointer(offset);
		return Function.getFunction(functionp);
	}
	
	private Pointer getJavaVm()
	{
		PointerByReference vmref = new PointerByReference();
		IntByReference num = new IntByReference();
		Function getcreatedjavavms = NativeLibrary.getProcess().getFunction("JNI_GetCreatedJavaVMs");
		getcreatedjavavms.invoke(new Object[] { vmref, 1, num });
		return vmref.getPointer().getPointer(0);
	}
	
	/**
	 *  Native method to get the current Java VM.
	 */
//	private native final int JNI_GetCreatedJavaVMs(PointerByReference vmref, int len, IntByReference num);
	
	/** Test main */
	public static void main(String[] args) throws Exception
	{
		final NativeHelper n = new NativeHelper();
		System.out.println(n.getVm());
	}
}
