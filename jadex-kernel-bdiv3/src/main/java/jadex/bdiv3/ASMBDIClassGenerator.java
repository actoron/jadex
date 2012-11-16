package jadex.bdiv3;

import jadex.bdiv3.model.BDIModel;
import jadex.commons.ByteClassLoader;
import jadex.commons.SReflect;
import jadex.commons.SUtil;

import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;

import org.kohsuke.asm4.ClassReader;
import org.kohsuke.asm4.ClassVisitor;
import org.kohsuke.asm4.ClassWriter;
import org.kohsuke.asm4.MethodVisitor;
import org.kohsuke.asm4.Opcodes;
import org.kohsuke.asm4.Type;
import org.kohsuke.asm4.util.ASMifier;
import org.kohsuke.asm4.util.TraceClassVisitor;

/**
 * 
 */
public class ASMBDIClassGenerator implements IBDIClassGenerator
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
	 *  Generate class.
	 */
	public Class<?> generateBDIClass(final String clname, final BDIModel model, ClassLoader cl)
	{
		Class<?> ret = null;
		try
		{
	//		String clname = cma.getName()+BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST;
			
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out));
//			TraceClassVisitor tcv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
	//		CheckClassAdapter cc = new CheckClassAdapter(tcv);
			
	//		final String classname = "lars/Lars";
	//		final String supername = "jadex/bdiv3/MyTestClass";
			
			final String iclname = clname.replace(".", "/");
			
			ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, tcv)
			{
	//			public void visit(int version, int access, String name,
	//				String signature, String superName, String[] interfaces)
	//			{
	//				super.visit(version, access, name, null, superName, interfaces);
	//			}
				
				public MethodVisitor visitMethod(int access, final String methodname, String desc, String signature, String[] exceptions)
				{
					System.out.println(desc+" "+methodname);
					
					return new MethodVisitor(api, super.visitMethod(access, methodname, desc, signature, exceptions))
					{
						public void visitFieldInsn(int opcode, String owner, String name, String desc)
						{
							if(Opcodes.PUTFIELD==opcode && model.getCapability().hasBelief(name))
							{
								// is already on stack (object + value)
	//							mv.visitVarInsn(ALOAD, 0);
	//							mv.visitIntInsn(BIPUSH, 25);
								
								System.out.println("vis: "+opcode+" "+owner+" "+name+" "+desc);
								
								// possibly transform basic value
//								if(SReflect.isBasicType(Type.getType(desc).getClass()))
									visitMethodInsn(Opcodes.INVOKESTATIC, "jadex/commons/SReflect", "wrapValue", "("+desc+")Ljava/lang/Object;");
								
								// fetch bdi agent value from field
								visitVarInsn(Opcodes.ALOAD, 0);
								super.visitFieldInsn(Opcodes.GETFIELD, iclname, "__agent", Type.getDescriptor(BDIAgent.class));
								visitInsn(Opcodes.SWAP);
								
								// add field name	
								visitLdcInsn(name);
								
								// add this
								visitVarInsn(Opcodes.ALOAD, 0);
								
								// invoke method
								visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(BDIAgent.class), "writeField", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V");
							}
							else
							{
								super.visitFieldInsn(opcode, owner, name, desc);
							}
						}
					};
				}
				
				public void visitEnd()
				{
					visitField(Opcodes.ACC_PUBLIC, "__agent", Type.getDescriptor(BDIAgent.class), null, null);
					super.visitEnd();
				}
			};
			
			InputStream is = null;
			try
			{
				is = SUtil.getResource(clname.replace('.', '/') + ".class", cl);
				ClassReader cr = new ClassReader(is);
				cr.accept(cv, 0);
				byte[] data = cw.toByteArray();
				ret = toClass(clname, data, cl, null);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if(is!=null)
						is.close();
				}
				catch(Exception e)
				{
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public Class<?> toClass(String name, byte[] data, ClassLoader loader, ProtectionDomain domain)
	{
		Class<?> ret = null;
		
		try
		{
			Method method;
			Object[] args;
			if(domain == null)
			{
				method = methoddc1;
				args = new Object[]{name, data, new Integer(0), new Integer(data.length)};
			}
			else
			{
				method = methoddc2;
				args = new Object[]{name, data, new Integer(0), new Integer(data.length), domain};
			}

			method.setAccessible(true);
			try
			{
				ret = (Class<?>)method.invoke(loader, args);
			}
			finally
			{
				method.setAccessible(false);
			}
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return ret;
	}
	
//	/**
//	 * 
//	 */
	public static void main(String[] args) throws Exception
	{
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//		TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out));
		TraceClassVisitor tcv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
//		CheckClassAdapter cc = new CheckClassAdapter(tcv);
		
		final String classname = "lars/Lars";
		final String supername = "jadex/bdiv3/MyTestClass";
		
		ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, tcv)
		{
			public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces)
			{
				super.visit(version, access, classname, null, supername, interfaces);
			}
			
			public MethodVisitor visitMethod(int access, final String methodname, String desc, String signature, String[] exceptions)
			{
				System.out.println(desc+" "+methodname);
				
				if("<init>".equals(methodname))
				{
					return new MethodVisitor(api, super.visitMethod(access, methodname, desc, signature, exceptions))
					{
						public void visitCode()
						{
							visitVarInsn(Opcodes.ALOAD, 0);
							visitMethodInsn(Opcodes.INVOKESPECIAL, supername, "<init>", "()V");
							visitInsn(Opcodes.RETURN);
							visitMaxs(1, 1);
							visitEnd();
						}
					};
				}
				else
				{
					return new MethodVisitor(api, super.visitMethod(access, methodname, desc, signature, exceptions))
					{
	//					protected boolean si = true;
	//					public void visitMethodInsn(int opcode, String owner, String name, String desc)
	//					{
	//						// replace only first init call (to superconstruktor)
	//						if(si && "<init>".equals(methodname) && Opcodes.INVOKESPECIAL==opcode)
	//						{
	//							si = false;
	//							super.visitMethodInsn(Opcodes.INVOKESPECIAL, supername, name, desc);
	//						}
	//						else 
	//						{
	//							super.visitMethodInsn(opcode, owner, name, desc);
	//						}
	//					}
						
						public void visitFieldInsn(int opcode, String owner, String name, String desc)
						{
							if(Opcodes.PUTFIELD==opcode)
							{
								// is already on stack (object + value)
	//							mv.visitVarInsn(ALOAD, 0);
	//							mv.visitIntInsn(BIPUSH, 25);
								
								System.out.println("vis: "+opcode+" "+owner+" "+name+" "+desc);
								
								if(SReflect.isBasicType(Type.getType(desc).getClass()))
									visitMethodInsn(Opcodes.INVOKESTATIC, "jadex/commons/SReflect", "wrapValue", "("+desc+")Ljava/lang/Object;");
								visitLdcInsn(name);
								visitVarInsn(Opcodes.ALOAD, 0);
								visitMethodInsn(Opcodes.INVOKEVIRTUAL, "jadex/bdiv3/MyTestClass", "writeField", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V");
							}
							else
							{
								super.visitFieldInsn(opcode, owner, name, desc);
							}
						}
					};
				}
			}
			
//			public void visitEnd()
//			{
//				FieldVisitor fv = visitField(Opcodes.ACC_PUBLIC, "__agent", Type.getDescriptor(String.class), null, null);
//				super.visitEnd();
//			}
		};
		
		ClassReader cr = new ClassReader("jadex.bdiv3.MyTestClass");
		cr.accept(cv, 0);
		
		ByteClassLoader bcl = new ByteClassLoader(ASMBDIClassGenerator.class.getClassLoader());
		Class<?> cl = bcl.loadClass("lars.Lars", cw.toByteArray(), true);
		cl.newInstance();
		
//		System.out.println(cl);
//		Constructor<?> c = cl.getConstructor(new Class[0]);
//		c.setAccessible(true);
//		c.newInstance(new Object[0]);
//		Method m = cl.getMethod("main", new Class[]{String[].class});
//		m.setAccessible(true);
//		m.invoke(null, new Object[]{new String[0]});
	}
}
