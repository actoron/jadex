package jadex.bytecode.access;

import jadex.commons.MethodInfo;
import jadex.commons.SUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/** Gain access using instrumentation. */
public class InstAccess
{
    /** Directory for temporary jar files. */
    protected static final File TEMP_JAR_DIR = new File(System.getProperty("java.io.tmpdir") + File.separator + ".jadex" + File.separator + "tmpjars");
    
    /** Access to the setAccessible0 internal method, once available. */
    private static volatile MethodHandle setaccessible;
    
    /** Original class to restore functionality once done. */
    private static byte[] preservedclass;
    
    /**
     *  Acquire MethodHandle to unchecked Method AccessibleObject.setAccessible0.
     *
     *  @return MethodHandle on success, null otherwise.
     */
    public static final MethodHandle getAccessHandle()
    {
        if (setaccessible == null)
        {
            synchronized (InstAccess.class)
            {
                if (setaccessible == null)
                {
                    boolean started = false;
                    try
                    {
                        Method sa = AccessibleObject.class.getDeclaredMethod("setAccessible0", boolean.class);
    
                        startAgent();
                        started = true;
                        
                        // This should now work.
                        sa.setAccessible(true);
                        
                        setaccessible = MethodHandles.lookup().unreflect(sa);
                    }
                    catch (Throwable e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        // Restore class
                        if (started)
                            startAgent();
                    }
                }
            }
        }
        return setaccessible;
    }
    
    /**
     *  Starts the instrumentation agent.
     */
    private static final void startAgent()
    {
        File jar = null;
        try
        {
            Manifest man = new Manifest();
            Attributes attrs = man.getMainAttributes();
            attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
            attrs.put(new Attributes.Name("Premain-Class"), AccessAgent.class.getCanonicalName());
            attrs.put(new Attributes.Name("Agent-Class"), AccessAgent.class.getCanonicalName());
            attrs.put(new Attributes.Name("Main-Class"), AccessAgent.class.getCanonicalName());
            attrs.put(new Attributes.Name("Can-Redefine-Classes"), "true");
            attrs.put(new Attributes.Name("Can-Retransform-Classes"), "true");
    
            InputStream is = AccessAgent.class.getResourceAsStream(AccessAgent.class.getSimpleName() + ".class");
            jar = createTempJar(AccessAgent.class.getCanonicalName(), is, man);
            SUtil.close(is);
    
            try
            {
                String pid = Long.toString(ProcessHandle.current().pid());
                Process p = null;
                try
                {
                    p = Runtime.getRuntime().exec(new String[]{"javaw", "-jar", jar.getAbsolutePath(), jar.getAbsolutePath(), pid});
                }
                catch (IOException e)
                {
                    p = Runtime.getRuntime().exec(new String[] { "java", "-jar", jar.getAbsolutePath(),  jar.getAbsolutePath(), pid });
                }
                
                if (p != null)
                    p.waitFor();
            }
            catch (Exception e1)
            {
            }
        }
        catch (Exception e)
        {
        }
    }
    
    /**
     *  Method used by the instrumentation agent to enhance the target class.
     *
     *  @param input Original class bytecode.
     *  @return Enhanced class bytecode.
     */
    public static final byte[] enhanceClass(byte[] input)
    {
        if (preservedclass != null)
        {
            byte[] ret = preservedclass;
            preservedclass = null;
            return ret;
        }
        
        byte[] ret = null;
        ClassReader cr = new ClassReader(input);
        ClassNode cn = new ClassNode(Opcodes.ASM9);
        cr.accept(cn, 0);
    
        if (cn.name.equals(Method.class.getCanonicalName().replace(".", "/")))
        {
    
            String methoddesc = Type.getMethodDescriptor(Type.getType(void.class), Type.getType(boolean.class));
    
            MethodNode sanode = null;
            for (MethodNode mn : cn.methods)
            {
                if ("setAccessible".equals(mn.name) && methoddesc.equals(mn.desc))
                {
                    sanode = mn;
                    break;
                }
            }
    
            if (sanode != null)
            {
                InsnList list = sanode.instructions;
                list.clear();
                list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                list.add(new VarInsnNode(Opcodes.ILOAD, 1));
                list.add(new FieldInsnNode(Opcodes.PUTFIELD, Type.getInternalName(AccessibleObject.class), "override", Type.getDescriptor(boolean.class)));
                list.add(new InsnNode(Opcodes.RETURN));
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                cn.accept(cw);
        
                ret = cw.toByteArray();
                preservedclass = input;
            }
        }
        
        return ret;
    }
    
    /**
     *  Creates a temporary jar-file for the agent.
     *  @param classname Name of class.
     *  @param classcontent The bytecode content.
     *  @param man The jar manifest.
     *  @return Reference to jar-file.
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
            
            os = new JarOutputStream(new FileOutputStream(jar), man);
            String clname = classname.replace('.', '/') + ".class";
            JarEntry e = new JarEntry(clname);
            os.putNextEntry(e);
            SUtil.copyStream(classcontent, os);
            os.closeEntry();
            jar.deleteOnExit();
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
     *  Main class for testing.
     *  @param args Arguments.
     */
    public static void main(String[] args)
    {
        try
        {
            Method sa = AccessibleObject.class.getDeclaredMethod("setAccessible0", boolean.class);
            //System.out.println("Before: " + sa.canAccess(sa));
            getAccessHandle().invoke(sa, true);
            //System.out.println("sa0: " + sa);
            //sa.invoke(f, true);
            //Field or = AccessibleObject.class.getDeclaredField("override");
            //System.out.println("ORF " + or);
            //or.set(f, true);
            //System.out.println("After: " + sa.canAccess(sa));
        } catch (Throwable e)
        {
            e.printStackTrace();
        }
        
        SUtil.sleep(3000);
    }
}
