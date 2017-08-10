package jadex.bytecode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import jadex.commons.SUtil;

/**
 *  Static ASM helper methods.
 */
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
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, type.getInternalName()));
			nl.add(new InsnNode(Opcodes.ARETURN));
		}
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
	 *  Get a class node for a class.
	 *  @param clazz The clazz. 
	 *  @return The class node.
	 */
	public static ClassNode getClassNode(Class<?> clazz, ClassLoader classloader)
	{
		ClassNode cns = null;
		
		try
		{
			cns = new ClassNode();
			ClassReader crs = new ClassReader(SUtil.getResource(clazz.getName().replace(".", "/")+".class", classloader));
			crs.accept(cns, 0);
			return cns;
		}
		catch(Exception e)
		{
			SUtil.rethrowAsUnchecked(e);
		}
		
		return cns;
	}
}
