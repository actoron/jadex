package jadex.bdiv3;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import jadex.commons.ByteClassLoader;
import jadex.commons.SReflect;

public class SASM
{
	protected static Method methoddc1;
    protected static Method methoddc2;

	static
	{
		try
		{
			AccessController.doPrivileged(new PrivilegedExceptionAction<Object>()
			{
				public Object run() throws Exception
				{
					Class<?> cl = Class.forName("java.lang.ClassLoader");
					methoddc1 = cl.getDeclaredMethod("defineClass", new Class[]{String.class, byte[].class, int.class, int.class});
					methoddc2 = cl.getDeclaredMethod("defineClass", new Class[]{String.class, byte[].class, int.class, int.class, ProtectionDomain.class});
					return null;
				}
			});
		}
		catch(PrivilegedActionException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Make a value to an object.
	 *  @param nl The instruction list.
	 *  @param type The value type.
	 */
	public static void makeObject(InsnList nl, Type type)
	{
		makeObject(nl, type, 1);
	}
	
	/**
	 *  Make a value to an object.
	 *  @param nl The instruction list.
	 *  @param type The value type.
	 *  @param pos The position of the value on the registers (default=1, 0 is this).
	 *  @return The updated position value.
	 */
	public static int makeObject(InsnList nl, Type arg, int pos)
	{
		if(arg.getClassName().equals("byte"))
		{
			nl.add(new VarInsnNode(Opcodes.ILOAD, pos++));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;"));
		}
		else if(arg.getClassName().equals("short"))
		{
			nl.add(new VarInsnNode(Opcodes.ILOAD, pos++));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;"));
		}
		else if(arg.getClassName().equals("int"))
		{
			nl.add(new VarInsnNode(Opcodes.ILOAD, pos++));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;"));
		}
		else if(arg.getClassName().equals("char"))
		{
			nl.add(new VarInsnNode(Opcodes.ILOAD, pos++));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;"));
		}
		else if(arg.getClassName().equals("boolean"))
		{
			nl.add(new VarInsnNode(Opcodes.ILOAD, pos++));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;"));
		}
		else if(arg.getClassName().equals("long"))
		{
			nl.add(new VarInsnNode(Opcodes.LLOAD, pos++));
			pos++;
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;"));
		}
		else if(arg.getClassName().equals("float"))
		{
			nl.add(new VarInsnNode(Opcodes.FLOAD, pos++));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;"));
		}
		else if(arg.getClassName().equals("double"))
		{
			nl.add(new VarInsnNode(Opcodes.DLOAD, pos++));
			pos++;
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;"));
		}
		else // Object
		{
			nl.add(new VarInsnNode(Opcodes.ALOAD, pos++));
		}
		
		return pos;
	}
	
	/**
	 *  Make a value a basic type.
	 *  @param nl The instruction list.
	 *  @param type The value type.
	 */
	public static void makeBasicType(InsnList nl, Type type)
	{
		if(type.getClassName().equals("byte"))
		{
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(Boolean.class)));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B"));
		}
		else if(type.getClassName().equals("short"))
		{
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(Short.class)));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S"));
		}
		else if(type.getClassName().equals("int"))
		{
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(Integer.class)));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I"));
		}
		else if(type.getClassName().equals("char"))
		{
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(Character.class)));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C"));
		}
		else if(type.getClassName().equals("boolean"))
		{
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(Boolean.class)));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z"));
		}
		else if(type.getClassName().equals("long"))
		{
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(Long.class)));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J"));
		}
		else if(type.getClassName().equals("float"))
		{
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(Float.class)));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F"));
		}
		else if(type.getClassName().equals("double"))
		{
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(Double.class)));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D"));
		}
//		else // Object
//		{
//		}
	}
	
	/**
	 *  Make a suitable return statement.
	 *  @param nl The instruction list.
	 *  @param type The value type.
	 */
	public static void makeReturn(InsnList nl, Type type)
	{
		if(type.getClassName().equals("byte"))
		{
			nl.add(new InsnNode(Opcodes.IRETURN));
		}
		else if(type.getClassName().equals("short"))
		{
			nl.add(new InsnNode(Opcodes.IRETURN));
		}
		else if(type.getClassName().equals("int"))
		{
			nl.add(new InsnNode(Opcodes.IRETURN));
		}
		else if(type.getClassName().equals("char"))
		{
			nl.add(new InsnNode(Opcodes.IRETURN));
		}
		else if(type.getClassName().equals("boolean"))
		{
//			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(boolean.class)));
			nl.add(new InsnNode(Opcodes.IRETURN));
		}
		else if(type.getClassName().equals("long"))
		{
			nl.add(new InsnNode(Opcodes.LRETURN));
		}
		else if(type.getClassName().equals("float"))
		{
			nl.add(new InsnNode(Opcodes.FRETURN));
		}
		else if(type.getClassName().equals("double"))
		{
			nl.add(new InsnNode(Opcodes.DRETURN));
		}
		else if(Type.VOID_TYPE.equals(type))
		{
			nl.add(new InsnNode(Opcodes.RETURN));
		}
		else 
		{
			nl.add(new InsnNode(Opcodes.ARETURN));
		}
	}
	
	/**
	 *  Generate a proxy for an existing class.
	 */
	public static Object newProxyInstance(ClassLoader loader, final Class<?> clazz, InvocationHandler handler) throws Exception
	{
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//		ClassReader cr = new ClassReader(clazz.getName());
//		TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out));
//		TraceClassVisitor tcv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
		
		ClassNode cn = new ClassNode();
		cn.version = Opcodes.V1_5;
		cn.access = Opcodes.ACC_PUBLIC;
		cn.name = Type.getInternalName(clazz)+"Proxy";
		cn.superName = Type.getInternalName(clazz);
		cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "handler", "Ljava/lang/reflect/InvocationHandler;", null, null));
		
		MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "(Ljava/lang/reflect/InvocationHandler;)V", null, null);
		
		mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Type.getInternalName(clazz), "<init>", "()V"));
		mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		mn.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, cn.name, "handler", "Ljava/lang/reflect/InvocationHandler;"));
		mn.instructions.add(new InsnNode(Opcodes.RETURN));
		
		cn.methods.add(mn);
		
		ClassNode cns = new ClassNode();
		ClassReader crs = new ClassReader(clazz.getName());
		crs.accept(cns, 0);
//		crs.accept(new TraceClassVisitor(cns, new PrintWriter(System.out)), 0);
//		crs.accept(new TraceClassVisitor(cns, new ASMifier(), new PrintWriter(System.out)), 0);
		
		List<MethodNode> ms = cns.methods;
		for(MethodNode m: ms)
		{
			if(!"<init>".equals(m.name))
			{
				// todo: exceptions
				MethodNode nm = new MethodNode(m.access, m.name, m.desc, m.signature, null);
				Type[] ptypes = Type.getArgumentTypes(nm.desc);
								
				// Object on which method is invoked (handler)
				
				nm.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nm.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, cn.name, "handler", Type.getDescriptor(InvocationHandler.class)));
				
				// Arguments proxy, method, args
				
				// Proxy
				nm.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				
//				nm.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
//				nm.instructions.add(new LdcInsnNode("0ppppppp"));
//				nm.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
				
				// Method
				nm.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nm.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false));
				nm.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getSuperclass", "()Ljava/lang/Class;", false));
				nm.instructions.add(new LdcInsnNode(m.name));
				nm.instructions.add(new LdcInsnNode(ptypes.length));
				nm.instructions.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Class"));				
				for(int i=0; i<ptypes.length; i++)
				{
					nm.instructions.add(new InsnNode(Opcodes.DUP));
					nm.instructions.add(new LdcInsnNode(i));
					Class<?> cl = SReflect.findClass(ptypes[i].getClassName(), null, null);
					
					if(cl.isPrimitive())
					{
						// Special hack to get primitive class
//						nm.instructions.add(new LdcInsnNode(Type.getType(SReflect.getWrappedType(cl))));
						nm.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, Type.getInternalName(SReflect.getWrappedType(cl)), "TYPE", "Ljava/lang/Class;"));
					}
					else
					{
						nm.instructions.add(new LdcInsnNode(ptypes[i]));
					}
					nm.instructions.add(new InsnNode(Opcodes.AASTORE));
				}
				nm.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false));

				// Arguments
				nm.instructions.add(new LdcInsnNode(ptypes.length));
				nm.instructions.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"));
				int pos = 1;
				for(int i=0; i<ptypes.length; i++)
				{
					nm.instructions.add(new InsnNode(Opcodes.DUP));
					nm.instructions.add(new LdcInsnNode(i));
//					nm.instructions.add(new VarInsnNode(Opcodes.ALOAD, i+1));
					pos = SASM.makeObject(nm.instructions, ptypes[i], pos);
					nm.instructions.add(new InsnNode(Opcodes.AASTORE));
				}
				
//				nm.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
//				nm.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
//				nm.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
				
				nm.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/lang/reflect/InvocationHandler", "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", true));
				
				Type rettype = Type.getReturnType(m.desc);
				if(Type.VOID_TYPE.equals(rettype))
					nm.instructions.add(new InsnNode(Opcodes.POP));
				
				SASM.makeBasicType(nm.instructions, rettype);
				SASM.makeReturn(nm.instructions, rettype);
				
				cn.methods.add(nm);
			}
		}
		
		cn.accept(cw);
//		cn.accept(tcv);
//		cn.accept(new CheckClassAdapter(tcv));
//		cn.accept(new TraceClassVisitor(tcv, new ASMifier(), new PrintWriter(System.out)));
		
//		ByteClassLoader bcl = new ByteClassLoader(loader!=null? loader: SASM.class.getClassLoader());
		Class<?> cl = toClass(cn.name.replace("/", "."), cw.toByteArray(), new URLClassLoader(new URL[0], ASMBDIClassGenerator.class.getClassLoader()), null);
//		Class<?> cl = bcl.loadClass(cn.name, cw.toByteArray(), true);
		Constructor<?> c = cl.getConstructor(new Class[]{InvocationHandler.class});
		Object o = c.newInstance(handler);
		
		return o;
	}
	
	/**
	 *  Transform byte Array into Class and define it in classloader.
	 *  @return the loaded class or <code>null</code>, if the class is not valid, such as Map.entry "inner Classes".
	 */
	public static Class<?> toClass(String name, byte[] data, ClassLoader loader, ProtectionDomain domain)
	{
		Class<?> ret = null;
		
		try
		{
			Method method;
			Object[] args;
			if(domain == null)
			{
				method = methoddc1;
				args = new Object[]{name, data, Integer.valueOf(0), Integer.valueOf(data.length)};
			}
			else
			{
				method = methoddc2;
				args = new Object[]{name, data, Integer.valueOf(0), Integer.valueOf(data.length), domain};
			}

			method.setAccessible(true);
			try
			{
				ret = (Class<?>)method.invoke(loader, args);
			}
			catch(InvocationTargetException e)
			{
				if(e.getTargetException() instanceof LinkageError)
				{
//					e.printStackTrace();					
					// when same class was already loaded via other filename wrong cache miss:-(
//					ret = SReflect.findClass(name, null, loader);
					ret = Class.forName(name, true, loader);
				}
				else
				{
					throw e.getTargetException();
				}
			}
			finally
			{
				method.setAccessible(false);
			}
		}
		catch(Throwable e)
		{
			if(e instanceof Error)
			{
				throw (Error)e;
			}
			else if(e instanceof RuntimeException)
			{
				throw (RuntimeException)e;
			}
			else
			{
				throw new RuntimeException(e);
			}
		}
		
		return ret;
	}

	/**
	 *  Main for testing.
	 */
	public static void main(String[] args) throws Exception
	{
		final MyTestClass mtc = new MyTestClass();
		MyTestClass proxy = (MyTestClass)newProxyInstance(null, MyTestClass.class, new InvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				System.out.println("Handler called: "+proxy+" "+method+" "+args);
				return method.invoke(mtc, args);
			}
		});
		
		System.out.println("o: "+proxy+" "+proxy.getClass().getField("handler").get(proxy));
		
		try
		{
			proxy.call2("hallo", 12);
			System.out.println(proxy.add(1, 2));
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
}
