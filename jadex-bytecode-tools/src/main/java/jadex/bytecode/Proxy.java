package jadex.bytecode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;

/**
 *  Proxy class allows for generating proxy objects for
 *  interfaces and/or one class. Both sides are optional.
 *  
 *  Uses the InvocationHandler from standard Java interface
 *  proxy mechanism.
 */
public class Proxy
{
	public static final Set<String> OBJECTMETHODS = new HashSet<String>();
	
	static
	{
		OBJECTMETHODS.add("equals");
		OBJECTMETHODS.add("toString");
		OBJECTMETHODS.add("hashCode");
	}

	public static final AtomicInteger COUNTER = new AtomicInteger();
	
	public static final Map<Tuple2<ClassLoader, Set<Class<?>>>, Class<?>> CLASSCACHE = Collections.synchronizedMap(new WeakHashMap<Tuple2<ClassLoader,Set<Class<?>>>, Class<?>>());
	
	/**
     *  Get the invocation handler of a proxy.
     *  @param proxy
     *  @return The handler
     */
    public static InvocationHandler getInvocationHandler(Object proxy) 
    {
    	try
    	{
    		proxy.getClass().getField("isproxy");
    		Field f = proxy.getClass().getField("handler");
    		return (InvocationHandler)f.get(proxy);
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
		Set<Class<?>> def = SUtil.arrayToSet(ifaces);
		if(clazz!=null)
			def.add(clazz);
		loader = loader==null? Proxy.class.getClassLoader(): loader;

		// Try fetch from cache
		Tuple2<ClassLoader, Set<Class<?>>> key = new Tuple2<ClassLoader, Set<Class<?>>>(loader, def);
		Class<?> ret = CLASSCACHE.get(key);
		if(ret!=null)
		{
			try
			{
//				System.out.println("Cache hit: "+ret+" "+def);
				Constructor<?> c = ret.getConstructor(new Class[]{InvocationHandler.class});
				Object o = c.newInstance(handler);
				return o;
			}
			catch(Exception e)
			{
				SUtil.rethrowAsUnchecked(e);
			}
		}
		
		ClassNode cn = new ClassNode();
		try
		{
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
	//		ClassReader cr = new ClassReader(clazz.getName());
	//		TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out));
	//		TraceClassVisitor tcv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
			
			cn.version = Opcodes.V1_5;
			cn.access = Opcodes.ACC_PUBLIC;
			cn.name = (clazz!=null? Type.getInternalName(clazz)+"Proxy": "Proxy")+COUNTER.getAndIncrement();
			cn.superName = clazz!=null? Type.getInternalName(clazz): Type.getType(Object.class).getInternalName();
			cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "handler", "Ljava/lang/reflect/InvocationHandler;", null, null));
			cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "isproxy", Type.getType(Boolean.class).getDescriptor(), null, Boolean.TRUE));
			
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
			
			ClassNode cns = SASM.getClassNode(Object.class, loader);
	//		crs.accept(new TraceClassVisitor(cns, new PrintWriter(System.out)), 0);
	//		crs.accept(new TraceClassVisitor(cns, new ASMifier(), new PrintWriter(System.out)), 0);
			
			List<MethodNode> ms = new ArrayList<MethodNode>(cns.methods);
			
			for(MethodNode m: ms)
			{
				if(OBJECTMETHODS.contains(m.name))
				{
					MethodNode nmn = genrateInvocationCode(m, cn.name, null, loader);
					if(nmn!=null && !done.contains(nmn.name+nmn.desc))
					{
						cn.methods.add(nmn);
						done.add(nmn.name+nmn.desc);
	//						System.out.println(nmn.name+nmn.desc);
					}
	//					else if(nmn!=null)
	//					{
	//						System.out.println("OMITTED: "+nmn.name+nmn.desc);
	//					}
				}
			}
			
			if(clazz!=null)
			{
				cns = SASM.getClassNode(clazz, loader);
		//		crs.accept(new TraceClassVisitor(cns, new PrintWriter(System.out)), 0);
		//		crs.accept(new TraceClassVisitor(cns, new ASMifier(), new PrintWriter(System.out)), 0);
				
				ms = new ArrayList<MethodNode>(cns.methods);
				
				for(MethodNode m: ms)
				{
					MethodNode nmn = genrateInvocationCode(m, cn.name, null, loader);
					if(nmn!=null && !done.contains(nmn.name+nmn.desc))
					{
						cn.methods.add(nmn);
						done.add(nmn.name+nmn.desc);
//						System.out.println(nmn.name+nmn.desc);
					}
//					else if(nmn!=null)
//					{
//						System.out.println("OMITTED: "+nmn.name+nmn.desc);
//					}
				}
			}
			
			
			if(ifaces!=null)
			{				
				for(int i=0; i<allifs.length; i++)
				{
					ClassNode tcn = SASM.getClassNode(allifs[i], loader);
					List<MethodNode> tms = tcn.methods;
					for(MethodNode m: tms)
					{
						MethodNode nmn = genrateInvocationCode(m, cn.name, allifs[i], loader);
						if(nmn!=null && !done.contains(nmn.name+nmn.desc))
						{
							cn.methods.add(nmn);
							done.add(nmn.name+nmn.desc);
//							System.out.println(nmn.name+nmn.desc);
						}
//						else if(nmn!=null)
//						{
//							System.out.println("OMITTED: "+nmn.name+nmn.desc);
//						}
					}
				}
			}
			
			cn.accept(cw);
	//		cn.accept(tcv);
	//		cn.accept(new CheckClassAdapter(tcv));
//			cn.accept(new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out)));
			
	//		ByteClassLoader bcl = new ByteClassLoader(loader!=null? loader: SASM.class.getClassLoader());
			Class<?> cl = SASM.toClass(cn.name.replace("/", "."), cw.toByteArray(), new URLClassLoader(new URL[0], loader), null);
	//		Class<?> cl = bcl.loadClass(cn.name, cw.toByteArray(), true);
			Constructor<?> c = cl.getConstructor(new Class[]{InvocationHandler.class});
			Object o = c.newInstance(handler);
		
			CLASSCACHE.put(new Tuple2<ClassLoader, Set<Class<?>>>(loader, def), cl);
			return o;
		}
		catch(Throwable t)
		{
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			//		TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out));
			TraceClassVisitor tcv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
			cn.accept(tcv);
			
			SUtil.rethrowAsUnchecked(t);
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
	protected static MethodNode genrateInvocationCode(MethodNode m, String classname, Class<?> iface, ClassLoader loader) throws Exception
	{
		MethodNode ret = null;
		
		if(!"<init>".equals(m.name) && !"<clinit>".equals(m.name))
		{
			// todo: exceptions
//			MethodNode nm = new MethodNode(m.access, m.name, m.desc, m.signature, null);
			ret = new MethodNode(Opcodes.ACC_PUBLIC, m.name, m.desc, m.signature, null);
//			System.out.println(m.name+" "+m.desc+" "+m.signature);
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
			if(iface==null)
			{
				ret.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				ret.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false));
				ret.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getSuperclass", "()Ljava/lang/Class;", false));
			}
			else
			{
				ret.instructions.add(new LdcInsnNode(Type.getType(iface)));
//				ret.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false));
			}
			ret.instructions.add(new LdcInsnNode(m.name));
			ret.instructions.add(new LdcInsnNode(ptypes.length));
			ret.instructions.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Class"));				
			for(int i=0; i<ptypes.length; i++)
			{
				ret.instructions.add(new InsnNode(Opcodes.DUP));
				ret.instructions.add(new LdcInsnNode(i));
				Class<?> cl = SReflect.findClass(ptypes[i].getClassName(), null, loader);
				
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
			
//			if(m.name.indexOf("getComponentFeatures")!=-1)
//			{
//				ret.instructions.add(new VarInsnNode(Opcodes.ASTORE, 1));
//				ret.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
//				ret.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
//				ret.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false));
////				ret.instructions.add(new LdcInsnNode(rettype.getInternalName()));
////				ret.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false));
//				ret.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
//				ret.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
//			}
			
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
