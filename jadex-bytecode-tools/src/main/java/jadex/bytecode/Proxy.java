package jadex.bytecode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import jadex.commons.SReflect;
import jadex.commons.SUtil;

/**
 *  Proxy class allows for generating proxy objects for
 *  interfaces and/or one class. Both sides are optional.
 *  
 *  Uses the InvocationHandler from standard Java interface
 *  proxy mechanism.
 */
public class Proxy
{
	/**
	 *  Generate a proxy for an existing class.
	 *  @param loader The class loader.
	 *  @param ifaces The interfaces (may contain one clazz).
	 *  @param handler The invocation handler.
	 *  @return The new proxy extending the clazz and implementing all interfaces.
	 */
	public static Object newProxyInstance(ClassLoader loader, final Class<?>[] ifaces, InvocationHandler handler) 
	{
		try
		{
			Class<?> clazz = null;
			List<Class<?>> ifs = new ArrayList<Class<?>>();
			for(Class<?> iface: ifaces)
			{
				if(!iface.isInterface())
				{
					clazz = iface;
				}
				else
				{
					ifs.add(iface);
				}
			}
			Class<?>[] ifsa = ifs.toArray(new Class[ifs.size()]);
			return newProxyInstance(loader, clazz, ifsa, handler);
		}
		catch(Exception e)
		{
			SUtil.rethrowAsUnchecked(e);
			return null;
		}
	}
	
	/**
	 *  Generate a proxy for an existing class.
	 *  @param loader The class loader.
	 *  @param clazz The clazz.
	 *  @param ifaces The interfaces.
	 *  @param handler The invocation handler.
	 *  @return The new proxy extending the clazz and implementing all interfaces.
	 */
	public static Object newProxyInstance(ClassLoader loader, final Class<?> clazz, final Class<?>[] ifaces, InvocationHandler handler) 
	{
		try
		{
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
	//		ClassReader cr = new ClassReader(clazz.getName());
	//		TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out));
	//		TraceClassVisitor tcv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
			
			ClassNode cn = new ClassNode();
			cn.version = Opcodes.V1_5;
			cn.access = Opcodes.ACC_PUBLIC;
			cn.name = clazz!=null? Type.getInternalName(clazz)+"Proxy": SUtil.createUniqueId("Proxy", 5);
			cn.superName = clazz!=null? Type.getInternalName(clazz): Type.getType(Object.class).getInternalName();
			cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "handler", "Ljava/lang/reflect/InvocationHandler;", null, null));
			
			Class<?>[] allifs = ifaces!=null? SReflect.getSuperInterfaces(ifaces): null;
			if(ifaces!=null)
			{
//				for(int i=0; i<allifs.length; i++)
//				{
//					cn.interfaces.add(Type.getInternalName(allifs[i]));
//				}
				for(int i=0; i<ifaces.length; i++)
				{
					cn.interfaces.add(Type.getInternalName(ifaces[i]));
				}
			}
			
			MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "(Ljava/lang/reflect/InvocationHandler;)V", null, null);
			
			mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, clazz!=null? Type.getInternalName(clazz): Type.getInternalName(Object.class), "<init>", "()V"));
			mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
			mn.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, cn.name, "handler", "Ljava/lang/reflect/InvocationHandler;"));
			mn.instructions.add(new InsnNode(Opcodes.RETURN));
			
			cn.methods.add(mn);
			
			// The done methods
			Set<String> done = new HashSet<String>();
			
			if(clazz!=null)
			{
				ClassNode cns = SASM.getClassNode(clazz);
		//		crs.accept(new TraceClassVisitor(cns, new PrintWriter(System.out)), 0);
		//		crs.accept(new TraceClassVisitor(cns, new ASMifier(), new PrintWriter(System.out)), 0);
				
				List<MethodNode> ms = new ArrayList<MethodNode>(cns.methods);
				
				for(MethodNode m: ms)
				{
					MethodNode nmn = genrateInvocationCode(m, cn.name, null);
					if(nmn!=null && !done.contains(nmn.desc))
					{
						cn.methods.add(nmn);
						done.add(nmn.desc);
					}
				}
			}
			
			
			if(ifaces!=null)
			{				
				for(int i=0; i<allifs.length; i++)
				{
					ClassNode tcn = SASM.getClassNode(allifs[i]);
					List<MethodNode> tms = tcn.methods;
					for(MethodNode m: tms)
					{
						MethodNode nmn = genrateInvocationCode(m, cn.name, allifs[i]);
						if(nmn!=null && !done.contains(nmn.desc))
						{
							cn.methods.add(nmn);
							done.add(nmn.desc);
						}
					}
				}
			}
			
			cn.accept(cw);
	//		cn.accept(tcv);
	//		cn.accept(new CheckClassAdapter(tcv));
	//		cn.accept(new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out)));
			
	//		ByteClassLoader bcl = new ByteClassLoader(loader!=null? loader: SASM.class.getClassLoader());
			Class<?> cl = SASM.toClass(cn.name.replace("/", "."), cw.toByteArray(), new URLClassLoader(new URL[0], SASM.class.getClassLoader()), null);
	//		Class<?> cl = bcl.loadClass(cn.name, cw.toByteArray(), true);
			Constructor<?> c = cl.getConstructor(new Class[]{InvocationHandler.class});
			Object o = c.newInstance(handler);
			
			return o;
		}
		catch(Exception e)
		{
			SUtil.rethrowAsUnchecked(e);
			return null;
		}
	}
	
	/**
	 *  Generate the code for delegating the call to the invocation handler.
	 *  @param m The methodnode.
	 *  @param classname The class name.
	 *  @param iface The interface (null means the class is owner of the method)
	 *  @return The new method node (or null).
	 */
	protected static MethodNode genrateInvocationCode(MethodNode m, String classname, Class<?> iface) throws Exception
	{
		MethodNode ret = null;
		
		if(!"<init>".equals(m.name))
		{
			// todo: exceptions
//			MethodNode nm = new MethodNode(m.access, m.name, m.desc, m.signature, null);
			ret = new MethodNode(Opcodes.ACC_PUBLIC, m.name, m.desc, m.signature, null);
			System.out.println(m.name+" "+m.desc+" "+m.signature);
			Type[] ptypes = Type.getArgumentTypes(ret.desc);
							
			// Object on which method is invoked (handler)
			
			ret.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			ret.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, classname, "handler", Type.getDescriptor(InvocationHandler.class)));
			
			// Arguments proxy, method, args
			
			// Proxy
			ret.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			
//			nm.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
//			nm.instructions.add(new LdcInsnNode("0ppppppp"));
//			nm.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
			
			// Method
			ret.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			if(iface==null)
			{
				ret.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false));
				ret.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getSuperclass", "()Ljava/lang/Class;", false));
			}
			else
			{
//				ret.instructions.add(new LdcInsnNode(Type.getType(iface)));
				ret.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false));
			}
			ret.instructions.add(new LdcInsnNode(m.name));
			ret.instructions.add(new LdcInsnNode(ptypes.length));
			ret.instructions.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Class"));				
			for(int i=0; i<ptypes.length; i++)
			{
				ret.instructions.add(new InsnNode(Opcodes.DUP));
				ret.instructions.add(new LdcInsnNode(i));
				Class<?> cl = SReflect.findClass(ptypes[i].getClassName(), null, null);
				
				if(cl.isPrimitive())
				{
					// Special hack to get primitive class
//					nm.instructions.add(new LdcInsnNode(Type.getType(SReflect.getWrappedType(cl))));
					ret.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, Type.getInternalName(SReflect.getWrappedType(cl)), "TYPE", "Ljava/lang/Class;"));
				}
				else
				{
					ret.instructions.add(new LdcInsnNode(ptypes[i]));
				}
				ret.instructions.add(new InsnNode(Opcodes.AASTORE));
			}
			ret.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false));

			// Arguments
			ret.instructions.add(new LdcInsnNode(ptypes.length));
			ret.instructions.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"));
			int pos = 1;
			for(int i=0; i<ptypes.length; i++)
			{
				ret.instructions.add(new InsnNode(Opcodes.DUP));
				ret.instructions.add(new LdcInsnNode(i));
//				nm.instructions.add(new VarInsnNode(Opcodes.ALOAD, i+1));
				pos = SASM.makeObject(ret.instructions, ptypes[i], pos);
				ret.instructions.add(new InsnNode(Opcodes.AASTORE));
			}
			
//			nm.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
//			nm.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
//			nm.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
			
			ret.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/lang/reflect/InvocationHandler", "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", true));
			
			Type rettype = Type.getReturnType(m.desc);
			if(Type.VOID_TYPE.equals(rettype))
				ret.instructions.add(new InsnNode(Opcodes.POP));
			
			SASM.makeBasicType(ret.instructions, rettype);
			SASM.makeReturn(ret.instructions, rettype);
			
//			cn.methods.add(ret);
		}
		
		return ret;
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args) throws Exception
	{
//		final MyTestClass mtc = new MyTestClass();
//		MyTestClass proxy = (MyTestClass)newProxyInstance(null, MyTestClass.class, 
//			new Class<?>[]{ActionListener.class},
//			new InvocationHandler()
//		{
//			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
//			{
//				System.out.println("Handler called: "+proxy+" "+method+" "+args);
//				try
//				{
//					return method.invoke(mtc, args);
//				}
//				catch(Exception e)
//				{
//					System.out.println("Could not delegate to original object: "+e.getMessage());
//					return null;
//				}
//			}
//		});
//		
//		System.out.println("o: "+proxy+" "+proxy.getClass().getField("handler").get(proxy));
//		
//		try
//		{
//			proxy.call2("hallo", 12);
//			System.out.println(proxy.add(1, 2));
//			((ActionListener)proxy).actionPerformed(new ActionEvent(new Object(), 1, null));
//		}
//		catch(Throwable t)
//		{
//			t.printStackTrace();
//		}
		
		
		ActionListener proxy2 = (ActionListener)newProxyInstance(null, null, 
			new Class<?>[]{ActionListener.class},
			new InvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				System.out.println("Handler called: "+proxy+" "+method+" "+args);
				return null;
			}
		});
		
		try
		{
			proxy2.actionPerformed(new ActionEvent(new Object(), 1, null));
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
}
