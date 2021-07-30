package jadex.nativetools;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sun.jna.Function;
import com.sun.jna.JNIEnv;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 *  Helper class using JNA to provide native features.
 *
 */
@SuppressWarnings("unused")
public final class NativeHelper
{
	// ------ JNI ------
	
	/** Use JNI version 1.6. */
	private static final int JNI_VERSION_1_6 = 0x00010006;
	
	/** JNI function GetVersion(). */
	private static final int JNI_GETVERSION_OFFSET = 4 * Native.POINTER_SIZE;
	
	/** JNI function DefineClass(). */
	private static final int JNI_DEFINECLASS_OFFSET = 5 * Native.POINTER_SIZE;
	
	/** JNI function FindClass(). */
	private static final int JNI_FINDCLASS_OFFSET = 6 * Native.POINTER_SIZE;
	
	/** JNI function FromReflectedMethod(). */
	private static final int JNI_FROMREFLECTEDMETHOD_OFFSET = 7 * Native.POINTER_SIZE;
	
	/** JNI function ToReflectedField(). */
	private static final int JNI_TOREFLECTEDFIELD_OFFSET = 12 * Native.POINTER_SIZE;
	
	/** JNI function CallObjectMethod(). */
	private static final int JNI_CALLOBJECTMETHOD_OFFSET = 34 * Native.POINTER_SIZE;
	
	/** JNI function CallLongMethod(). */
	private static final int JNI_CALLLONGMETHOD_OFFSET = 52 * Native.POINTER_SIZE;
	
	/** JNI function GetFieldID(). */
	private static final int JNI_GETFIELDID_OFFSET = 94 * Native.POINTER_SIZE;
	
	/** JNI function SetBooleanField(). */
	private static final int JNI_SETBOOLEANFIELD_OFFSET = 105 * Native.POINTER_SIZE;
	
	/** JNI function GetJavaVM(). */
	private static final int JNI_GETJAVAVM_OFFSET = 219 * Native.POINTER_SIZE;
	
	// ------ JavaVM ------
	
	/** JavaVM function GetEnv(). */
	private final int JAVAVM_GETENV_OFFSET = 6 * Native.POINTER_SIZE;
	
	/** Pointer to the JavaVM. */
	private Pointer javavm;
	
	/** Pointer to the JNI function table. */
	private Pointer jnifunctiontable;
	
	/** Invocation options of JNI environment. */
	private Map<String, Object> envoptions;
	
	/** Field ID of AccessibleObject override field, if found. */
    private Pointer overridefieldid;
	
	/**
	 *  Create helper.
	 */
	public NativeHelper()
	{
		envoptions = new HashMap<String, Object>();
		envoptions.put(Library.OPTION_ALLOW_OBJECTS, Boolean.TRUE);
		
		javavm = getJavaVm();
		
		if (Pointer.nativeValue(javavm) == 0)
			throw new IllegalStateException("NativeHelper could not find Java VM.");
		
		PointerByReference envref = new PointerByReference();
		Function getenv = Function.getFunction(javavm.getPointer(0).getPointer(JAVAVM_GETENV_OFFSET));
		getenv.invokeInt(new Object[] { javavm, envref, JNI_VERSION_1_6 });
		Pointer env = envref.getPointer().getPointer(0);
		
		if (Pointer.nativeValue(env) == 0)
			throw new IllegalStateException("Unable to acquire JNI environment.");
		
		jnifunctiontable = env.getPointer(0);
		
		invokeJni(JNI_GETVERSION_OFFSET, int.class);
	}
	
	/**
	 *  Sets reflective object accessible without checks.
	 *  
	 *  @param accobj The accessible object.
	 *  @param flag The flag value.
	 */
	public void setAccessible(AccessibleObject accobj, boolean flag)
	{
		if (overridefieldid == null)
		{
			try
			{
					overridefieldid = invokeJni(JNI_GETFIELDID_OFFSET, Pointer.class, AccessibleObject.class, "override", "Z");
			}
			catch (Throwable e)
			{
					try
					{
							overridefieldid = invokeJni(JNI_GETFIELDID_OFFSET, Pointer.class, AccessibleObject.class, "flag", "Z");
					}
					catch (Throwable e1)
					{
						// Throw original exception, most likely candidate...
						throw new RuntimeException(e);
					}
			}
		}
		invokeJni(JNI_SETBOOLEANFIELD_OFFSET, void.class, accobj, overridefieldid, flag);
	}
	
	/**
     * Define a class in any ClassLoader.
     */
	public Class<?> defineClass(String name, byte[] b, ClassLoader loader)
	{
		name = name!=null ? name.replace('.', '/') : null;
		Class<?> ret = (Class<?>) invokeJni(JNI_DEFINECLASS_OFFSET, Object.class, name, loader, b, b.length);
		return ret;
	}
	
	/**
	 *  Find a class in system classloader.
	 *  @param name Fully-qualified name of class.
	 *  @return The class.
	 */
	public Class<?> findClass(String name)
	{
		name = name!=null ? name.replace('.', '/') : null;
		Class<?> ret = (Class<?>) invokeJni(JNI_FINDCLASS_OFFSET, Object.class, name);
		return ret;
	}
	
	/**
	 *  Returns the field of a class, overriding security checks.
	 *
	 *  @param clazz The class.
	 *  @param fieldname The field name.
	 *  @param type The field type in VM notation.
	 *  @param isstatic If the field is static or not.
	 *  @return The field.
	 */
	public Field getField(Class<?> clazz, String fieldname, String type, boolean isstatic)
	{
		Pointer fieldid = invokeJni(JNI_GETFIELDID_OFFSET, Pointer.class, clazz, fieldname, type);
		Field ret = (Field) invokeJni(JNI_TOREFLECTEDFIELD_OFFSET, Object.class, clazz, fieldid, isstatic);
		return ret;
	}
	
	/**
	 *  Attempts to change the user of the process to the given name.
	 *  
	 *  @param username The target user name.
	 *  @return True, if successful, false if the attempt probably failed.
	 */
	public boolean tryChangeUser(String username)
	{
		boolean ret = false;
		try
		{
			username = username == null ? "nobody" : username;
			int[] uidgid = getUidGid(username);
			if (uidgid == null)
				return false;
			
			boolean succeeded = true;
			int maxtries = 3;
			int res = -1;
			Function fun = NativeLibrary.getProcess().getFunction("setregid");
			for (int i = 0; i < maxtries; ++i)
			{
				res = fun.invokeInt(new Object[] { uidgid[1], uidgid[1] });
				if (res == 0)
					break;
			}
			
			succeeded &= res == 0;
			
			fun = NativeLibrary.getProcess().getFunction("setreuid");
			for (int i = 0; i < maxtries; ++i)
			{
				res = fun.invokeInt(new Object[] { uidgid[0], uidgid[0] });
				if (res == 0)
					break;
			}
			
			succeeded &= res == 0;
			
			ret = succeeded;
		}
		catch (Throwable e)
		{
		}
		return ret;
	}
	
	/**
	 *  Tests if the JVM is running as root/admin.
	 *  
	 *  @return True, if running as root.
	 */
	public boolean isRootAdmin()
	{
		boolean ret = false;
		try
		{
			ret = geteUid() == 0;
		}
		catch (Throwable e)
		{
		}
		return ret;
	}
	
	/**
	 *  Method for starting an instrumentation agent.
	 *  
	 *  @param jarfile The path to the jar file of the agent.
	 *  @return True, on successful start.
	 */
	public boolean startInstrumentationAgent(String jarfile)
	{
		boolean ret = false;
		try
		{
			/*long pid = ProcessHandle.current().pid();
			VirtualMachine vm = VirtualMachine.attach(Long.toString(pid));
			vm.loadAgent(jarfile);
			vm.detach();*/
			
			/*System.loadLibrary("instrument");
			Function fun = NativeLibrary.getInstance("instrument").getFunction("Agent_OnAttach");
			//ret = fun.invokeInt(new Object[] { javavm, jarfile, Pointer.NULL }) == 0;
			ret = fun.invokeInt(new Object[] { getJavaVm(), jarfile, Pointer.NULL }) == 0;*/
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
//	public Pointer getMethodId(Method method)
//	{
//		return invokeEnv(JNI_FROMREFLECTEDMETHOD_OFFSET, Pointer.class, method);
//	}
	
//	public Object invokeMethod(Object obj, Pointer methodid, Method method, Object... params)
//	{
////		Pointer methodid = invokeEnv(JNI_FROMREFLECTEDMETHOD_OFFSET, Pointer.class, method);
//		
//		return invokeEnv(JNI_CALLLONGMETHOD_OFFSET, long.class, obj, methodid);
//	}
	
	/**
	 *  Invokes function on the JNI environment.
	 * @param offset
	 * @param rettype
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T invokeJni(long offset, Class<T> rettype, Object... params)
	{
		Object[] extparams = null;
		if (params == null)
		{
			extparams = new Object[] { JNIEnv.CURRENT };
		}
		else
		{
			extparams = new Object[params.length + 1];
			extparams[0] = JNIEnv.CURRENT;
			System.arraycopy(params, 0, extparams, 1, params.length);
		}
		
		Function jnifun = Function.getFunction(jnifunctiontable.getPointer(offset));
		return (T) jnifun.invoke(rettype, extparams, envoptions);
	}
	
	/**
	 *  Retrieves the current Java VM.
	 *  
	 *  @return Pointer to the current Java VM struct.
	 */
	private Pointer getJavaVm()
	{
		PointerByReference vmref = new PointerByReference();
		IntByReference num = new IntByReference();
		Function getcreatedjavavms = NativeLibrary.getProcess().getFunction("JNI_GetCreatedJavaVMs");
		getcreatedjavavms.invoke(new Object[] { vmref, 1, num });
		return vmref.getPointer().getPointer(0);
	}
	
	/**
	 *  Gets the effective user ID for the Java VM.
	 *  
	 *  @return Effective user ID of the running Java VM.
	 */
	private int geteUid()
	{
		Function geteuid = NativeLibrary.getProcess().getFunction("geteuid");
		return geteuid.invokeInt(new Object[0]);
	}
	
	/**
	 *  Gets the UID and main GID for a user name.
	 *  
	 *  @param username The user name.
	 *  @return The UID and GID.
	 */
	private int[] getUidGid(String username)
	{
		Pointer structbuf = malloc(8192);
		int charbufsize = 65536;
		Pointer charbuf = malloc(charbufsize);
		
		Function getpwnamer = NativeLibrary.getProcess().getFunction("getpwnam_r");
		PointerByReference passwdref = new PointerByReference();
		getpwnamer.invokeInt(new Object[] { username, structbuf, charbuf, charbufsize, passwdref });
		
		int[] ret = null;
		Pointer structp = passwdref.getPointer().getPointer(0);
		if (structp != null)
		{
			ret = new int[2];
			long off = 2*Native.POINTER_SIZE;
			ret[0] = structp.getInt(off);
			off += 4;
			ret[1] = structp.getInt(off);
		}
		
		free(structbuf);
		free(charbuf);
		
		return ret;
	}
	
	/**
	 *  Allocates memory using malloc and returns pointer.
	 *  
	 *  @param size Memory size to allocate.
	 *  @return Pointer to memory.
	 */
	private Pointer malloc(long size)
	{
		return new Pointer(Native.malloc(size));
	}
	
	/**
	 *  Frees memory.
	 *  
	 *  @param ptr Pointer to memory.
	 */
	private void free(Pointer ptr)
	{
		Native.free(Pointer.nativeValue(ptr));
	}
	
	/**
	 *  Native method to get the current Java VM.
	 */
//	private native final int JNI_GetCreatedJavaVMs(PointerByReference vmref, int len, IntByReference num);
	
	/** Test main */
	public static final void main(String[] args) throws Exception
	{
		NativeHelper n = new NativeHelper();
//		System.out.println(n.getVm());
		System.out.println(n.geteUid());
		System.out.println(n.getUidGid("nobody")[0] + " " + n.getUidGid("nobody")[1]);
		n.tryChangeUser(null);
		System.out.println(n.geteUid());
		try
		{
			Thread.sleep(30000);
		}
		catch (Exception e)
		{
		}
	}
}
