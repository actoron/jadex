package jadex.bdiv3;

import jadex.bdiv3.model.BDIModel;
import jadex.commons.ByteClassLoader;
import jadex.commons.SReflect;
import jadex.commons.SUtil;

import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.kohsuke.asm4.ClassReader;
import org.kohsuke.asm4.ClassVisitor;
import org.kohsuke.asm4.ClassWriter;
import org.kohsuke.asm4.MethodVisitor;
import org.kohsuke.asm4.Opcodes;
import org.kohsuke.asm4.Type;
import org.kohsuke.asm4.tree.ClassNode;
import org.kohsuke.asm4.util.CheckClassAdapter;
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
	public Class<?> generateBDIClass(final String clname, final BDIModel model, final ClassLoader cl)
	{
		Class<?> ret = null;
		
//		System.out.println("Generating with cl: "+cl+" "+clname);
		
		final List<String> iclasses = new ArrayList<String>();
		
		try
		{
	//		String clname = cma.getName()+BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST;
			
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//			TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out));
//			TraceClassVisitor tcv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
//			CheckClassAdapter cc = new CheckClassAdapter(cw);
			
	//		final String classname = "lars/Lars";
	//		final String supername = "jadex/bdiv3/MyTestClass";
			
			final String iclname = clname.replace(".", "/");
			
			ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw)
			{
	//			public void visit(int version, int access, String name,
	//				String signature, String superName, String[] interfaces)
	//			{
	//				super.visit(version, access, name, null, superName, interfaces);
	//			}
				
				public MethodVisitor visitMethod(int access, final String methodname, String desc, String signature, String[] exceptions)
				{
//					System.out.println(desc+" "+methodname);
					
					return new MethodVisitor(api, super.visitMethod(access, methodname, desc, signature, exceptions))
					{
						public void visitFieldInsn(int opcode, String owner, String name, String desc)
						{
							// if is a putfield and is belief and not is in init (__agent field is not available)
							if(Opcodes.PUTFIELD==opcode && model.getCapability().hasBelief(name))// && !"<init>".equals(methodname))
							{
//								visitInsn(Opcodes.POP);
//								visitInsn(Opcodes.POP);
								
								// stack before putfield is object,value ()
//								System.out.println("method: "+methodname+" "+name);

								// write init values also immediately to allow dependencies
								if("<init>".equals(methodname))
								{
									super.visitFieldInsn(opcode, owner, name, desc);
									visitVarInsn(Opcodes.ALOAD, 0);
									visitInsn(Opcodes.DUP);
									super.visitFieldInsn(Opcodes.GETFIELD, owner, name, desc);
								}
								
								// is already on stack (object + value)
	//							mv.visitVarInsn(ALOAD, 0);
	//							mv.visitIntInsn(BIPUSH, 25);
								
//								System.out.println("vis: "+opcode+" "+owner+" "+name+" "+desc);
//								System.out.println(Type.getType(desc).getClassName());
																
								// possibly transform basic value
								if(SReflect.isBasicType(SReflect.findClass0(Type.getType(desc).getClassName(), null, cl)))
									visitMethodInsn(Opcodes.INVOKESTATIC, "jadex/commons/SReflect", "wrapValue", "("+desc+")Ljava/lang/Object;");
								
								visitInsn(Opcodes.SWAP);
//								visitInsn(Opcodes.POP);
								
								// fetch bdi agent value from field
//								visitVarInsn(Opcodes.ALOAD, 0);
								super.visitFieldInsn(Opcodes.GETFIELD, iclname, "__agent", Type.getDescriptor(BDIAgent.class));
//								visitInsn(Opcodes.SWAP);
								
								// add field name	
								visitLdcInsn(name);
								visitInsn(Opcodes.SWAP);
								// add this
								visitVarInsn(Opcodes.ALOAD, 0);
								visitInsn(Opcodes.SWAP);
								
								// invoke method
//								String bdin = Type.getDescriptor(BDIAgent.class);
//								System.out.println("bdin: "+bdin);
//								visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(BDIAgent.class), "writeField", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V");
//								visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(BDIAgent.class), "writeField", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;"+bdin+")V");
								visitMethodInsn(Opcodes.INVOKESTATIC, "jadex/bdiv3/BDIAgent", "writeField", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljadex/bdiv3/BDIAgent;)V");
//								visitMethodInsn(Opcodes.INVOKESTATIC, "jadex/bdiv3/BDIAgent", "writeField", "()V");
							}
							else
							{
								super.visitFieldInsn(opcode, owner, name, desc);
							}
						}
					};
				}
				
				public void visitInnerClass(String name, String outerName, String innerName, int access)
				{
//					System.out.println("vic: "+name+" "+outerName+" "+innerName+" "+access);
					iclasses.add(name);
					super.visitInnerClass(name, outerName, innerName, access);//Opcodes.ACC_PUBLIC); does not work
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
				String fname = clname.replace('.', '/') + ".class";
				is = SUtil.getResource(fname, cl);
				ClassReader cr = new ClassReader(is);
				cr.accept(cv, 0);
				byte[] data = cw.toByteArray();
				
				// Find correct cloader for injecting the class.
				// Probes to load class without loading class.
				
				List<ClassLoader> pas = new LinkedList<ClassLoader>();
				ClassLoader tmp = cl;
				while(tmp!=null)
				{
					pas.add(0, tmp);
					tmp = tmp.getParent();
				}
				
				ClassLoader found = null;
				for(ClassLoader tmpcl: pas)
				{
					if(tmpcl.getResource(fname)!=null)
					{
						found = tmpcl;
						break;
					}
				}
				
				ret = toClass(clname, data, found, null);
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
			catch(InvocationTargetException e)
			{
				if(e.getTargetException() instanceof LinkageError)
				{
					// when same class was already loaded via other filename :-(
					ret = SReflect.findClass(name, null, loader);
				}
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
	
	/**
	 * 
	 */
	public static void main(String[] args) throws Exception
	{
//		System.out.println(int.class.getName());
		
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out));
//		TraceClassVisitor tcv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
		CheckClassAdapter cc = new CheckClassAdapter(tcv);
		
		final String classname = "lars/Lars";
//		final String supername = "jadex/bdiv3/MyTestClass";
		
//		final ASMifier asm = new ASMifier();
		
		ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, tcv)
		{
			public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces)
			{
				super.visit(version, access, classname, null, superName, interfaces);
			}
			
//			public MethodVisitor visitMethod(int access, final String methodname, String desc, String signature, String[] exceptions)
//			{
//				return super.visitMethod(access, methodname, desc, signature, exceptions);
//				
//				System.out.println("visit method: "+methodname);
//				
//				if("<init>".equals(methodname))
//				{
//					return new TraceMethodVisitor(super.visitMethod(access, methodname, desc, signature, exceptions), asm);
//				}
//				else
//				{
//					return super.visitMethod(access, methodname, desc, signature, exceptions);
//				}
//			}
		};
		
		ClassReader cr = new ClassReader("jadex.bdiv3.MyTestClass");
		cr.accept(cv, 0);
//		ClassNode cn = new ClassNode();
//		cr.accept(cn, 0);
		
//		MethodNode[] mths = cn.methods.toArray(new MethodNode[0]);
//		for(MethodNode mn: mths)
//		{
//			System.out.println(mn.name);
//			if(mn.name.equals("<init>"))
//			{
//				InsnList l = cn.methods.get(0).instructions;
//				for(int i=0; i<l.size(); i++)
//				{
//					AbstractInsnNode n = l.get(i);
//					if(n instanceof LabelNode)
//					{
//						LabelNode ln = (LabelNode)n;
//						System.out.println(ln.getLabel());
//					}
//					else if(n instanceof FieldInsnNode)
//					{
//						FieldInsnNode fn = (FieldInsnNode)n;
//						
//						if(Opcodes.PUTFIELD==fn.getOpcode())
//						{
//							System.out.println("found putfield node: "+fn.name+" "+fn.getOpcode());
//							AbstractInsnNode start = fn;
//							while(!(start instanceof LabelNode))
//							{
//								start = start.getPrevious();
//							}
//
//							MethodNode mnode = new MethodNode(mn.access, "set"+fn.name, mn.desc, mn.signature, null);
//							
//							Map<LabelNode, LabelNode> labels = new HashMap<LabelNode, LabelNode>();
//							while(!start.equals(fn))
//							{
//								AbstractInsnNode clone;
//								if(start instanceof LabelNode)
//								{
//									clone = new LabelNode(new Label());
//									labels.put((LabelNode)start, (LabelNode)clone);
//								}
//								else
//								{
//									clone = start.clone(labels);
//								}
//								mnode.instructions.add(clone);
//								start = start.getNext();
//							}
//							mnode.instructions.add(start);
//							
//							cn.methods.add(mnode);
//							break;
//						}
//					}
//					else
//					{
//						System.out.println(n);
//					}
//				}
//			}
//		}
		
//		cn.name = classname;
		
//		System.out.println("cn: "+cn);
		
//		System.out.println(asm.getText());
		
//		ClassWriter cw = new ClassWriter(0);
//		cn.accept(cc);
//		byte[] b = cw.toByteArray();
		
		ByteClassLoader bcl = new ByteClassLoader(ASMBDIClassGenerator.class.getClassLoader());
		Class<?> cl = bcl.loadClass("lars.Lars", cw.toByteArray(), true);
		Object o = cl.newInstance();
		Object v = cl.getMethod("getVal", new Class[0]).invoke(o, new Object[0]);
		
		System.out.println("res: "+o+" "+v);
		
//		System.out.println(SUtil.arrayToString(cl.getDeclaredMethods()));
		
		
//		System.out.println(cl);
//		Constructor<?> c = cl.getConstructor(new Class[0]);
//		c.setAccessible(true);
//		c.newInstance(new Object[0]);
//		Method m = cl.getMethod("main", new Class[]{String[].class});
//		m.setAccessible(true);
//		m.invoke(null, new Object[]{new String[0]});
	}
}
