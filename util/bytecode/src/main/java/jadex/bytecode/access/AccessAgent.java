package jadex.bytecode.access;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

public class AccessAgent implements ClassFileTransformer
{
    /**
     *  Name of the InstAccess class, must be compile time constant
     *  since InstAccess will not be directly available in agent jar.
     */
    private static final String INSTACCESS_CLASSNAME = "jadex.bytecode.access.InstAccess";
    
    /** The enhanceClass() method of InstAccess once found. */
    private static Method enhanceclass;
    
    /**
     *  Transforms a class using instrumentation.
     *
     *  @param loader The class loader.
     *  @param className The class name
     *  @param classBeingRedefined The class being redefined.
     *  @param protectionDomain Protection domain.
     *  @param classfileBuffer Original classfile.
     *  @return Transformed class.
     *  @throws IllegalClassFormatException
     */
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        byte[] ret = null;
        
        if (Method.class.getCanonicalName().equals(classBeingRedefined.getCanonicalName()))
        {
            try
            {
                ret = (byte[]) enhanceclass.invoke(null, classfileBuffer);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
       
        return ret;
    }
    
    /**
     *  Agent in premain mode (unimplemented).
     *  @param agentargs Agent arguments.
     *  @param inst Instrumentation.
     */
    public static void premain(String agentargs, final Instrumentation inst)
    {
        // not implemented, do later
    }
    
    public static void agentmain(String agentargs, final Instrumentation inst)
    {
        Class<?>[] classes = inst.getAllLoadedClasses();
        Class<?> instaccessclass = null;
        for (Class<?> clazz : classes)
        {
            if (INSTACCESS_CLASSNAME.equals(clazz.getCanonicalName()))
            {
                instaccessclass = clazz;
                break;
            }
        }
        
        if (instaccessclass != null)
        {
            try
            {
                enhanceclass = instaccessclass.getMethod("enhanceClass", byte[].class);
                
                AccessAgent tf = new AccessAgent();
                inst.addTransformer(tf, true);
                inst.retransformClasses(Method.class);
    
                //revert = true;
                //inst.retransformClasses(Method.class);
                
                inst.removeTransformer(tf);
            }
            catch (Exception e)
            {
            }
        }
    }
    
    /**
     *  Main for testing.
     *  @param args Arguments.
     */
    public static void main(String[] args)
    {
        System.out.println("params " + args.length);
        System.out.println(args[0]);
        
        try
        {
            Class<?> ph = Class.forName("java.lang.ProcessHandle");
            Method phc = ph.getMethod("current");
            Method php = ph.getMethod("pid");
            long pid = (Long) php.invoke(phc.invoke(null, new Object[0]), new Object[0]);
            
            Class<?> vmc = Class.forName("com.sun.tools.attach.VirtualMachine");
            Method vmca = vmc.getMethod("attach", String.class);
            Object vm = vmca.invoke(null, args[1]);
            
            Method vmcla = vmc.getMethod("loadAgent", String.class);
            vmcla.invoke(vm, args[0]);
            
            Method vmcd = vmc.getMethod("detach");
            vmcd.invoke(vm);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
