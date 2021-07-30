package jadex.commons;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SAccess
{
    /** Enable approach "Unsafe". */
    public static boolean ENABLE_UNSAFE_SETACCESSIBLE = true;
    
    /** Enable approach "Native Access". */
    public static boolean ENABLE_NATIVE_SETACCESSIBLE = true;
    
    /** Enable approach "Instrumentation". */
    public static boolean ENABLE_INSTRUMENTATION_SETACCESSIBLE = true;
    
    /** Flag if class was inited. */
    private volatile static boolean inited = false;
    
    /** AccessibleObject override field offset. */
    private static Long setaccessibleoverrideoffset;
    
    // ========== Approach based on sun.misc.Unsafe ============
    
    /** The sun.misc.Unsafe object, if available. */
    private static Object unsafe;
    
    /** The Unsafe.getBoolean method. */
    private static MethodHandle getboolean;
    
    /** The Unsafe.putBoolean method. */
    private static MethodHandle putboolean;
    
    // ========== Approach based on JNI ============
    
    /** Helper for native functionality, optional **/
    private static Object nativehelper;
    
    /** NativeHelper.setAccessible() Method if available **/
    private static MethodHandle nativesetaccessible;
    
    // ========== Access handle unlocked by any method. =========
    
    /** Access to the setAccessible0 internal method, if available. */
    private static volatile MethodHandle setaccessible;
    
    public static final void setAccessible(AccessibleObject acc, boolean flag)
    {
        checkInit();
        
        if (setaccessible != null)
        {
            try
            {
                setaccessible.invoke(acc, flag);
                return;
            }
            catch (Throwable t)
            {
            }
        }
        
        if (setaccessibleoverrideoffset != null)
        {
            try
            {
                putboolean.invoke(unsafe, acc, setaccessibleoverrideoffset, flag);
                return;
            }
            catch (Throwable t)
            {
            }
        }
        
        if (nativesetaccessible != null)
        {
            try
            {
                nativesetaccessible.invoke(nativehelper, acc, flag);
            }
            catch (Throwable t)
            {
            }
        }
        
        acc.setAccessible(flag);
    }
    
    /**
     *  Checks if class was inited, initializes otherwise.
     */
    private static final void checkInit()
    {
        if (!inited)
        {
            synchronized (jadex.commons.SAccess.class)
            {
                if (!inited)
                {
                    if (ENABLE_UNSAFE_SETACCESSIBLE)
                        initUnsafe();
                    
                    if (ENABLE_NATIVE_SETACCESSIBLE)
                        initNative();
                    
                    if (ENABLE_INSTRUMENTATION_SETACCESSIBLE)
                        initInstrumentation();
                    
                    inited = true;
                }
            }
        }
    }
    
    /**
     *  Initializes access unlock approach "Unsafe"
     */
    private static final void initUnsafe()
    {
        Class<?> unsafeclass = null;
        try
        {
            unsafeclass = Class.forName("sun.misc.Unsafe");
        }
        catch (Exception e)
        {
        }
    
        if (unsafeclass != null)
        {
            try
            {
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
                        unsafe = instancefield.get(null);
                    }
                    catch (Exception e)
                    {
                    }
                }
            
                if (unsafe == null)
                {
                    // Okay, last chance, just instantiate a new instance...
                    Constructor<?> c = unsafeclass.getConstructor();
                    c.setAccessible(true);
                    unsafe = c.newInstance();
                }
            }
            catch (Exception e)
            {
            }
        }
    
        if (unsafe != null)
        {
            try
            {
                Method method = unsafeclass.getDeclaredMethod("getBoolean", Object.class, long.class);
                getboolean = MethodHandles.lookup().unreflect(method);
                method = unsafeclass.getDeclaredMethod("putBoolean", Object.class, long.class, boolean.class);
                putboolean = MethodHandles.lookup().unreflect(method);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        
            if (putboolean != null)
            {
                try
                {
                    Method testmethod = SAccess.class.getDeclaredMethod("checkInit");
                
                    int max = 20;
                
                    // Probe for field offset
                    for (int i = 0; i < max; ++i)
                    {
                        try
                        {
                            testmethod.setAccessible(false);
                            boolean before = (Boolean) getboolean.invoke(unsafe, testmethod, (long) i);
                            testmethod.setAccessible(true);
                            boolean after = (Boolean) getboolean.invoke(unsafe, testmethod, (long) i);
                        
                            if (!before && after)
                            {
                                setaccessibleoverrideoffset = (long) i;
                                
                                if (setaccessible == null)
                                {
                                    Method sa = AccessibleObject.class.getDeclaredMethod("setAccessible0", boolean.class);
                                    putboolean.invoke(unsafe, sa, setaccessibleoverrideoffset, true);
                                    setaccessible = MethodHandles.lookup().unreflect(sa);
                                }
                                
                                break;
                            }
                        }
                        catch (Throwable t)
                        {
                            t.printStackTrace();
                        }
                    }
                }
                catch (Exception e)
                {
                }
            }
        }
    }
    
    /**
     *  Initializes access unlock approach "Native Access"
     */
    private static final void initNative()
    {
        Class<?> nativehelperclass = null;
        try
        {
            nativehelperclass = Class.forName("jadex.nativetools.NativeHelper");
        }
        catch (Exception e)
        {
        }
    
        if (nativehelperclass != null)
        {
            try
            {
                nativehelper = nativehelperclass.getConstructor().newInstance();
            }
            catch (Exception e)
            {
            }
            
            if (nativehelper != null)
            {
                try
                {
                    Method method = nativehelperclass.getDeclaredMethod("setAccessible", AccessibleObject.class, boolean.class);
                    nativesetaccessible = MethodHandles.lookup().unreflect(method);
    
                    if (setaccessible == null)
                    {
                        Method sa = AccessibleObject.class.getDeclaredMethod("setAccessible0", boolean.class);
                        nativesetaccessible.invoke(sa, true);
                        setaccessible = MethodHandles.lookup().unreflect(sa);
                    }
                }
                catch (Throwable t)
                {
                }
            }
        }
    }
    
    /**
     *  Initialize instrumentation-based approach.
     */
    private static final void initInstrumentation()
    {
        if (setaccessible == null)
        {
            try
            {
                Class<?> instaccessclass = Class.forName("jadex.bytecode.access.InstAccess");
                Method method = instaccessclass.getDeclaredMethod("getAccessHandle");
                MethodHandle getaccesshandle = MethodHandles.lookup().unreflect(method);
                setaccessible = (MethodHandle) getaccesshandle.invoke();
            }
            catch (Throwable t)
            {
            }
        }
    }
}
