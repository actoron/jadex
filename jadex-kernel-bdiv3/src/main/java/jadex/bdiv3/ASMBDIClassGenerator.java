package jadex.bdiv3;

import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import jadex.bdiv3.exceptions.JadexBDIGenerationException;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MGoal;
import jadex.bridge.IInternalAccess;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.micro.MicroClassReader.DummyClassLoader;


/**
 * 
 */
public class ASMBDIClassGenerator extends AbstractAsmBdiClassGenerator
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
	public List<Class<?>> generateBDIClass(String clname, BDIModel model, ClassLoader dummycl) throws JadexBDIGenerationException {
		return generateBDIClass(clname, model, dummycl, new HashMap<String, ClassNode>());
	}
	
	/**
	 *  Generate class.
	 */
	public List<Class<?>> generateBDIClass(final String clname, final BDIModel model, 
		ClassLoader dummycl, final Map<String, ClassNode> done) throws JadexBDIGenerationException {
		List<Class<?>> ret = new ArrayList<Class<?>>();
		final ClassLoader cl = ((DummyClassLoader)dummycl).getOriginal();
		
//		System.out.println("Generating with cl: "+cl+" "+clname);
		
		final List<String> todo = new ArrayList<String>();
		
		try
		{
	//		String clname = cma.getName()+BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST;
			
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			final ClassNode cn = new ClassNode();
			
			done.put(clname, cn);
			
//			TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out))
//			TraceClassVisitor tcv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
//			CheckClassAdapter cc = new CheckClassAdapter(cw);
			
			final String iclname = clname.replace(".", "/");
			
			ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cn)
			{
				boolean isagentorcapa = false;
				boolean isgoal = false;
//				boolean isplan = false;
//				Set<String> fields = new HashSet<String>();
				
				public void visit(int version, int access, String name,
					String signature, String superName, String[] interfaces)
				{
					// Make class non-abstract if agent implements IBDIAgent interface
					boolean implbdiagent = false;
					if(interfaces!=null)
					{
						for(String iface: interfaces)
						{
							if(iface.indexOf(Type.getInternalName(IBDIAgent.class))!=-1)
							{
								implbdiagent = true;
								break;
							}
						}
					}
					if(implbdiagent && name.endsWith(BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST))
					{
						// erase abstract modifier
//						access = ~Opcodes.ACC_ABSTRACT & access;
						access = access-Opcodes.ACC_ABSTRACT;
					}
					super.visit(version, access, name, null, superName, interfaces);
				}
				
//				public FieldVisitor visitField(int access, String name,
//						String desc, String signature, Object value)
//				{
//					fields.add(name);
//					return super.visitField(access, name, desc, signature, value);
//				}
				
			    public AnnotationVisitor visitAnnotation(String desc, boolean visible) 
			    {
			    	if(visible)
			    	{
			    		if(isAgentOrCapa(desc))
			    		{
			    			isagentorcapa = true;
			    		}
			    		else if(isGoal(desc))
			    		{
			    			isgoal = true;
			    		}
//			    		else if(isPlan(desc))
//			    		{
//			    			isplan = true;
//			    		}
			    	}
			    	return super.visitAnnotation(desc, visible);
			    }

			    public MethodVisitor visitMethod(int access, final String methodname, String desc, String signature, String[] exceptions)
				{
//					if(clname.indexOf("PlanPrecondition")!=-1)
//						System.out.println(desc+" "+methodname);
					
					return new MethodVisitor(api, super.visitMethod(access, methodname, desc, signature, exceptions))
					{
						public void visitFieldInsn(int opcode, String owner, String name, String desc)
						{
							boolean enh = false;
							if(ophelper.isPutField(opcode))
							{
								// if is a putfield and is belief and not is in init (__agent field is not available)
								// either is itself agent/capa or is not field of non-agent
								if(model.getCapability().hasBelief(name) 
									&& model.getCapability().getBelief(name).isFieldBelief()
									&& (isagentorcapa || !owner.equals(iclname)))
								{
									// possibly transform basic value
									if(SReflect.isBasicType(SReflect.findClass0(Type.getType(desc).getClassName(), null, cl)))
										visitMethodInsn(Opcodes.INVOKESTATIC, "jadex/commons/SReflect", "wrapValue", "("+desc+")Ljava/lang/Object;");
									
									visitInsn(Opcodes.SWAP);
									
									// fetch bdi agent value from field
	
									// this pop aload is necessary in inner classes!
									if(isagentorcapa)
									{
										visitInsn(Opcodes.POP);
										visitVarInsn(Opcodes.ALOAD, 0);
										super.visitFieldInsn(Opcodes.GETFIELD, iclname, AGENT_FIELD_NAME, Type.getDescriptor(IInternalAccess.class));
									}
									else
									{
										visitInsn(Opcodes.POP);
										visitInsn(Opcodes.ACONST_NULL);
									}
									
									// add field name	
									visitLdcInsn(name);
									visitInsn(Opcodes.SWAP);
									// add this
									visitVarInsn(Opcodes.ALOAD, 0);
									visitInsn(Opcodes.SWAP);
									
									visitMethodInsn(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "writeField", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljadex/bridge/IInternalAccess;)V");
									enh = true;
								}
								else if(isgoal)
								{
									MGoal mgoal = model.getCapability().getGoal(clname);
									if(mgoal!=null && mgoal.hasParameter(name))
									{
										// possibly transform basic value
										if(SReflect.isBasicType(SReflect.findClass0(Type.getType(desc).getClassName(), null, cl)))
											visitMethodInsn(Opcodes.INVOKESTATIC, "jadex/commons/SReflect", "wrapValue", "("+desc+")Ljava/lang/Object;");
										
										visitInsn(Opcodes.SWAP);
										
										// fetch bdi agent value from field
										visitInsn(Opcodes.POP);
										visitInsn(Opcodes.ACONST_NULL);
										
										// add field name	
										visitLdcInsn(name);
										visitInsn(Opcodes.SWAP);
										// add this
										visitVarInsn(Opcodes.ALOAD, 0);
										visitInsn(Opcodes.SWAP);
										
										visitMethodInsn(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "writeParameterField", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljadex/bridge/IInternalAccess;)V");
										enh = true;
									}
								}
//								else if(isplan)
//								{
//									// todo? currently have no parameter support
//								}
							}
							
							if(!enh)
								super.visitFieldInsn(opcode, owner, name, desc);
						}
						
//						public void visitInsn(int opcode)
//						{
//							if(Opcodes.AASTORE==opcode)
//							{
//								// on stack: arrayref, index, value 
//								visitVarInsn(Opcodes.ALOAD, 0);
//								super.visitFieldInsn(Opcodes.GETFIELD, iclname, AGENT_FIELD_NAME, Type.getDescriptor(BDIAgent.class));
//
//								// invoke method
//								visitMethodInsn(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "writeArrayField", "(Ljava/lang/Object;ILjava/lang/Object;Ljadex/bridge/IInternalAccess;)V");
//							}
//							else
//							{
//								super.visitInsn(opcode);
//							}
////							super.visitInsn(opcode);
//						}
					};
				}
				
				public void visitInnerClass(String name, String outerName, String innerName, int access)
				{
					// Exclude non-relevant inner classes (that do not belong to the application code)
					if(iclname!=null && (outerName!=null && iclname.startsWith(outerName)) 
						|| (outerName==null && innerName==null)) // case in anonymous inner classes
					{
//						System.out.println("vic: "+name+" "+outerName+" "+innerName+" "+access);
						String icln = name.replace("/", ".");
						if(!done.containsKey(icln))
							todo.add(icln);
					}
//					else
//					{
//						System.out.println("skipping class enhancement of: "+innerName);
//					}
					super.visitInnerClass(name, outerName, innerName, access);//Opcodes.ACC_PUBLIC); does not work
				}
				
				public void visitEnd()
				{
					if(isagentorcapa)
						visitField(Opcodes.ACC_PUBLIC, AGENT_FIELD_NAME, Type.getDescriptor(IInternalAccess.class), null, null);
					visitField(Opcodes.ACC_PUBLIC, GLOBALNAME_FIELD_NAME, Type.getDescriptor(String.class), null, null);
					super.visitEnd();
				}
			};
			
			InputStream is = null;
			try
			{
				String fname = clname.replace('.', '/') + ".class";
				is = SUtil.getResource(fname, cl);
				ClassReader cr = new ClassReader(is);

//				TraceClassVisitor tcv2 = new TraceClassVisitor(cv, new PrintWriter(System.out));
//				TraceClassVisitor tcv3 = new TraceClassVisitor(null, new PrintWriter(System.out));
//				cr.accept(tcv2, 0);
				cr.accept(cv, 0);
				transformClassNode(cn, iclname, model, dummycl, done);
				cn.accept(cw);
				byte[] data = cw.toByteArray();
				
//				CheckClassAdapter.verify(new ClassReader(data), true, new PrintWriter(System.out));
				
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
				
//				System.out.println("toClass: "+clname+" "+found);
				Class<?> loadedClass = toClass(clname, data, found, null);
				if(loadedClass != null) 
				{
					// if it's null, we were not allowed to generate this class
					// e.g. java.util.Map.Entry "subclasses" (in bdiv3.tutorial.c1.TranslationBDI)
					ret.add(loadedClass);
				}
				
//				if(ret.getName().indexOf("$")!=-1)
//				{
//					try
//					{
//						Method m = ret.getMethod("__getLineNumber", new Class[0]);
//						Object o = m.invoke(null, new Object[0]);
//						System.out.println("Line is: "+ret.getName()+" "+o);
//					}
//					catch(Exception e)
//					{
//					}
//				}
			}
			catch(Exception e)
			{
				throw new JadexBDIGenerationException("Could not generate BDI Class: " + clname,e);
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
			
			for(String icl: todo)
			{
				List<Class<?>> classes = generateBDIClass(icl, model, dummycl, done);
				ret.addAll(classes);
			}
		}
		catch(Throwable e)
		{
			throw new JadexBDIGenerationException("Error generating BDI class:" + clname, e);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected void replaceNativeGetter(String iclname, MethodNode mn, String belname)
	{
		Type	ret	= Type.getReturnType(mn.desc);

		mn.access = mn.access - Opcodes.ACC_NATIVE;
		InsnList nl = new InsnList();
		nl.add(new VarInsnNode(Opcodes.ALOAD, 0));
		nl.add(new FieldInsnNode(Opcodes.GETFIELD, iclname, AGENT_FIELD_NAME, "Ljadex/bridge/IInternalAccess;"));
		nl.add(new VarInsnNode(Opcodes.ALOAD, 0));
		nl.add(new FieldInsnNode(Opcodes.GETFIELD, iclname, GLOBALNAME_FIELD_NAME, "Ljava/lang/String;"));
		nl.add(new LdcInsnNode(belname));
		
		if(ret.getClassName().equals("byte"))
		{
			nl.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;"));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "getAbstractBeliefValue", "(Ljadex/bridge/IInternalAccess;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;"));
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Number"));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I"));
			nl.add(new InsnNode(Opcodes.I2B));
			nl.add(new InsnNode(Opcodes.IRETURN));
		}
		else if(ret.getClassName().equals("short"))
		{
			nl.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;"));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "getAbstractBeliefValue", "(Ljadex/bridge/IInternalAccess;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;"));
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Number"));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I"));
			nl.add(new InsnNode(Opcodes.I2S));
			nl.add(new InsnNode(Opcodes.IRETURN));
		}
		else if(ret.getClassName().equals("int"))
		{
			nl.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;"));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "getAbstractBeliefValue", "(Ljadex/bridge/IInternalAccess;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;"));
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Number"));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I"));
			nl.add(new InsnNode(Opcodes.IRETURN));
		}
		else if(ret.getClassName().equals("char"))
		{
			nl.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;"));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "getAbstractBeliefValue", "(Ljadex/bridge/IInternalAccess;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;"));
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Character"));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C"));
			nl.add(new InsnNode(Opcodes.IRETURN));
		}
		else if(ret.getClassName().equals("boolean"))
		{
			nl.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;"));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "getAbstractBeliefValue", "(Ljadex/bridge/IInternalAccess;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;"));
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Boolean"));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z"));
			nl.add(new InsnNode(Opcodes.IRETURN));
		}
		else if(ret.getClassName().equals("long"))
		{
			nl.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;"));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "getAbstractBeliefValue", "(Ljadex/bridge/IInternalAccess;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;"));
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Number"));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "longValue", "()J"));
			nl.add(new InsnNode(Opcodes.LRETURN));							
		}
		else if(ret.getClassName().equals("float"))
		{
			nl.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;"));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "getAbstractBeliefValue", "(Ljadex/bridge/IInternalAccess;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;"));
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Number"));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "floatValue", "()F"));
			nl.add(new InsnNode(Opcodes.FRETURN));							
		}
		else if(ret.getClassName().equals("double"))
		{
			nl.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;"));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "getAbstractBeliefValue", "(Ljadex/bridge/IInternalAccess;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;"));
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Number"));
			nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D"));
			nl.add(new InsnNode(Opcodes.DRETURN));
		}
		else // Object
		{
			nl.add(new LdcInsnNode(ret));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "getAbstractBeliefValue", "(Ljadex/bridge/IInternalAccess;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;"));
			nl.add(new TypeInsnNode(Opcodes.CHECKCAST, ret.getInternalName()));
			nl.add(new InsnNode(Opcodes.ARETURN));
		}
		
		mn.instructions = nl;
	}
	
	/**
	 * 
	 */
	protected void makeObject(InsnList nl, Type arg)
	{
		if(arg.getClassName().equals("byte"))
		{
			nl.add(new VarInsnNode(Opcodes.ILOAD, 1));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;"));
		}
		else if(arg.getClassName().equals("short"))
		{
			nl.add(new VarInsnNode(Opcodes.ILOAD, 1));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;"));
		}
		else if(arg.getClassName().equals("int"))
		{
			nl.add(new VarInsnNode(Opcodes.ILOAD, 1));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;"));
		}
		else if(arg.getClassName().equals("char"))
		{
			nl.add(new VarInsnNode(Opcodes.ILOAD, 1));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;"));
		}
		else if(arg.getClassName().equals("boolean"))
		{
			nl.add(new VarInsnNode(Opcodes.ILOAD, 1));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;"));
		}
		else if(arg.getClassName().equals("long"))
		{
			nl.add(new VarInsnNode(Opcodes.LLOAD, 1));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;"));
		}
		else if(arg.getClassName().equals("float"))
		{
			nl.add(new VarInsnNode(Opcodes.FLOAD, 1));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;"));
		}
		else if(arg.getClassName().equals("double"))
		{
			nl.add(new VarInsnNode(Opcodes.DLOAD, 1));
			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;"));
		}
		else // Object
		{
			nl.add(new VarInsnNode(Opcodes.ALOAD, 1));
		}
	}
	
	/**
	 * 
	 */
	protected void replaceNativeSetter(String iclname, MethodNode mn, String belname) 
	{
		Type arg = Type.getArgumentTypes(mn.desc)[0];

		mn.access = mn.access - Opcodes.ACC_NATIVE;
		InsnList nl = new InsnList();
		nl.add(new VarInsnNode(Opcodes.ALOAD, 0));
		nl.add(new FieldInsnNode(Opcodes.GETFIELD, iclname, AGENT_FIELD_NAME, "Ljadex/bridge/IInternalAccess;"));
		nl.add(new VarInsnNode(Opcodes.ALOAD, 0));
		nl.add(new FieldInsnNode(Opcodes.GETFIELD, iclname, GLOBALNAME_FIELD_NAME, "Ljava/lang/String;"));
		nl.add(new LdcInsnNode(belname));
		
		makeObject(nl, arg);
		
		nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "setAbstractBeliefValue", "(Ljadex/bridge/IInternalAccess;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V"));
		nl.add(new InsnNode(Opcodes.RETURN));
		
		mn.instructions = nl;
	}

	/**
	 * 
	 */
	protected void enhanceSetter(String iclname, MethodNode mn, String belname)
	{
//		System.out.println("method acc: "+mn.getName()+" "+mn.getAccess());
		
		Type[] args = Type.getArgumentTypes(mn.desc);
		
		InsnList l = mn.instructions;
		
//		System.out.println("icl: "+iclname);
				
		InsnList nl = new InsnList();
		nl.add(new VarInsnNode(Opcodes.ALOAD, 0)); // loads the object
		nl.add(new FieldInsnNode(Opcodes.GETFIELD, iclname, AGENT_FIELD_NAME, Type.getDescriptor(IInternalAccess.class)));
		nl.add(new LdcInsnNode(belname));
		nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "unobserveValue", 
//			"(Ljava/lang/String;)V"));
			"(Ljadex/bridge/IInternalAccess;Ljava/lang/String;)V"));
		l.insertBefore(l.getFirst(), nl);
		
		nl = new InsnList();
//		nl.add(new VarInsnNode(Opcodes.ALOAD, 1)); // loads the argument (=parameter0) does not work for other types than object
		if(args.length>0)
		{
			makeObject(nl, args[0]);
		}
		else
		{
			nl.add(new InsnNode(Opcodes.ACONST_NULL));
		}
		
		nl.add(new InsnNode(Opcodes.ACONST_NULL)); // oldvalue ?
		nl.add(new InsnNode(Opcodes.ACONST_NULL)); // no index/key
		
		nl.add(new VarInsnNode(Opcodes.ALOAD, 0)); // loads the agent object
		nl.add(new FieldInsnNode(Opcodes.GETFIELD, iclname, AGENT_FIELD_NAME, Type.getDescriptor(IInternalAccess.class)));
		
		nl.add(new LdcInsnNode(belname));
		
//		nl.add(new LdcInsnNode(mbel));
		nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "createChangeEvent", 
//			"(Ljava/lang/Object;Ljadex/bridge/IInternalAccess;Ljava/lang/String;)V"));
			"(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljadex/bridge/IInternalAccess;Ljava/lang/String;)V"));
//			nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bridge/IInternalAccess", "createEvent", 
//				"()V"));

		// Find return and insert call before that
		AbstractInsnNode n;
		for(n = l.getLast(); n.getOpcode()!=Opcodes.RETURN; n = n.getPrevious())
		{
		}
		l.insertBefore(n, nl);
	}

	/**
	 * 
	 */
	protected void transformConstructor(ClassNode cn, MethodNode mn, BDIModel model, List<String> tododyn, Map<String, ClassNode> others)
	{
		InsnList l = mn.instructions;
		LabelNode begin = null;
		int foundcon = -1;
		
		for(int i=0; i<l.size(); i++)
		{
			AbstractInsnNode n = l.get(i);
			
			if(begin==null && n instanceof LabelNode)
			{
				begin = (LabelNode)n;
			}
			
			// find first constructor call
			if(Opcodes.INVOKESPECIAL==n.getOpcode() && foundcon==-1)
			{
				foundcon = i;
				begin = null;
			}
			else if(n instanceof MethodInsnNode && ((MethodInsnNode)n).name.equals("writeField"))
			{
				MethodInsnNode min = (MethodInsnNode)n;
				
//				System.out.println("found writeField node: "+min.name+" "+min.getOpcode());
				AbstractInsnNode start = min;
				String name = null;
				List<String> evs = new ArrayList<String>(); 
				while(!start.equals(begin))
				{
					// find method name via last constant load
					if(name==null && start instanceof LdcInsnNode)
						name = (String)((LdcInsnNode)start).cst;
					if(start.getOpcode()==Opcodes.GETFIELD)
					{
						String bn = ((FieldInsnNode)start).name;
						if(model.getCapability().hasBelief(bn))
						{
							evs.add(bn);
						}
					}
					start = start.getPrevious();
				}
				
				// todo: use findBeliefs?!
//				Set<String> bels = findBeliefs(cn, mn, model, others);
				
				// Create new update method for dynamic belief
				if(tododyn.remove(name))
				{
					MBelief mbel = model.getCapability().getBelief(name);
					Set<String>	bevs = new LinkedHashSet<String>(mbel.getBeliefEvents());
					bevs.addAll(evs);
					mbel.setBeliefEvents(bevs);
					
					MethodNode mnode = new MethodNode(Opcodes.ACC_PUBLIC, IBDIClassGenerator.DYNAMIC_BELIEF_UPDATEMETHOD_PREFIX
						+SUtil.firstToUpperCase(name), Type.getMethodDescriptor(Type.VOID_TYPE), null, null);
					
					// First labels are cloned 
					AbstractInsnNode cur = start;
					Map<LabelNode, LabelNode> labels = new HashMap<LabelNode, LabelNode>();
					while(!cur.equals(min))
					{
						if(cur instanceof LabelNode)
							labels.put((LabelNode)cur, new LabelNode(new Label()));
						cur = cur.getNext();
					}
					// Then code is cloned and uses cloned labels
					cur = start;
					while(!cur.equals(min))
					{
						AbstractInsnNode clone = cur.clone(labels);
						mnode.instructions.add(clone);
						cur = cur.getNext();
					}
					mnode.instructions.add(cur.clone(labels));
					mnode.visitInsn(Opcodes.RETURN);
					
					cn.methods.add(mnode);
				}
				
				begin = null;
			}
		}
		
		// Move init code to separate method for being called after injections. 
		if(foundcon!=-1 && foundcon+1<l.size())
		{
			String iclname = cn.name; // in ASM, this is without 'L' and ';'
			String name	= IBDIClassGenerator.INIT_EXPRESSIONS_METHOD_PREFIX+"_"+iclname.replace("/", "_").replace(".", "_");
//			System.out.println("Init method: "+name);
			MethodNode mnode = new MethodNode(Opcodes.ACC_PUBLIC, name, mn.desc, (String)mn.signature, null);
			MethodNode wrappedMNode = mnode;
			cn.methods.add(wrappedMNode);

			while(l.size()>foundcon+1)
			{
				AbstractInsnNode	node	= l.get(foundcon+1);
				if(ophelper.isReturn(node.getOpcode()))
				{
					break;
				}
				l.remove(node);
				wrappedMNode.instructions.add(node);
			}						
			mnode.visitInsn(Opcodes.RETURN);
			
			// Add code to store arguments in field.
			Type[]	args	= Type.getArgumentTypes(mn.desc);
			InsnList	init	= new InsnList();

			// obj param
			init.add(new VarInsnNode(Opcodes.ALOAD, 0));
			
			// clazz param
			init.add(new LdcInsnNode(Type.getType("L"+iclname+";")));
			
			// argtypes param
			init.add(new LdcInsnNode(args.length));
			init.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Class"));
			for(int i=0; i<args.length; i++)
			{
				init.add(new InsnNode(Opcodes.DUP));
				init.add(new LdcInsnNode(i));
				init.add(new LdcInsnNode(args[i]));
				init.add(new InsnNode(Opcodes.AASTORE));
			}
			
			// args param
			init.add( new LdcInsnNode(args.length));
			init.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Object"));
			for(int i=0; i<args.length; i++)
			{
				init.add(new InsnNode(Opcodes.DUP));
				init.add(new LdcInsnNode(i));
				init.add(new VarInsnNode(Opcodes.ALOAD, i+1));	// 0==this, 1==arg0, ...
				init.add(new InsnNode(Opcodes.AASTORE));
			}
			
			// Invoke method.
			init.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "addInitArgs", "(Ljava/lang/Object;Ljava/lang/Class;[Ljava/lang/Class;[Ljava/lang/Object;)V"));
			
			l.insertBefore(l.get(foundcon+1), init);
		}
	}

	/**
	 *  Transform array store instructions for beliefs.
	 */
	protected void transformArrayStores(MethodNode mn, BDIModel model, String iclname)//, MGoal mgoal)
	{
		InsnList ins = mn.instructions;
		LabelNode lab = null;
		List<String> belnames = new ArrayList<String>();
		List<String> paramnames = new ArrayList<String>();
		MGoal mgoal = model.getCapability().getGoal(iclname.replaceAll("/", "."));

		for(int i=0; i<ins.size(); i++)
		{
			AbstractInsnNode n = ins.get(i);
			if(lab==null && n instanceof LabelNode)
			{
				lab = (LabelNode)n;
				belnames.clear();
				paramnames.clear();
			}
			
			if(n.getOpcode()==Opcodes.GETFIELD)
			{
				String fn = ((FieldInsnNode)n).name;
				if(model.getCapability().hasBelief(fn) && model.getCapability().getBelief(fn).isArrayBelief())
				{
					belnames.add(fn);
				}
				else if(mgoal!=null && mgoal.hasParameter(fn) && mgoal.getParameter(fn).isArray())
				{
					paramnames.add(fn);
				}
			}
			
			if(!belnames.isEmpty() || !paramnames.isEmpty())
			{
				InsnList newins = null;
				
				if(Opcodes.IASTORE==n.getOpcode() || Opcodes.BASTORE==n.getOpcode()) // for int, byte and boolean :-((	
				{
					newins = new InsnList();
					newins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/commons/SReflect", "wrapValue", "(I)Ljava/lang/Object;"));
				}
				else if(Opcodes.LASTORE==n.getOpcode())
				{
					newins = new InsnList();
					newins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/commons/SReflect", "wrapValue", "(J)Ljava/lang/Object;"));
				}
				else if(Opcodes.FASTORE==n.getOpcode())
				{
					newins = new InsnList();
					newins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/commons/SReflect", "wrapValue", "(F)Ljava/lang/Object;"));
				}
				else if(Opcodes.DASTORE==n.getOpcode())
				{
					newins = new InsnList();
					newins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/commons/SReflect", "wrapValue", "(D)Ljava/lang/Object;"));
				}
				else if(Opcodes.CASTORE==n.getOpcode())
				{
					newins = new InsnList();
					newins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/commons/SReflect", "wrapValue", "(C)Ljava/lang/Object;"));
				}
				else if(Opcodes.SASTORE==n.getOpcode())
				{
					newins = new InsnList();
					newins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/commons/SReflect", "wrapValue", "(S)Ljava/lang/Object;"));
				}
				else if(Opcodes.AASTORE==n.getOpcode())
				{
					newins = new InsnList();
				}
				
				if(newins!=null)
				{
//					// on stack: arrayref, index, value 
//					System.out.println("found: "+belnames+" "+paramnames);
					
					if(!belnames.isEmpty())
					{
						String belname = belnames.get(0);
						
						newins.add(new VarInsnNode(Opcodes.ALOAD, 0));
	//					newins.add(new FieldInsnNode(Opcodes.GETFIELD, iclname, AGENT_FIELD_NAME, Type.getDescriptor(BDIAgent.class)));
						newins.add(new LdcInsnNode(belname));
	//					newins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "writeArrayField", 
	//						"(Ljava/lang/Object;ILjava/lang/Object;Ljadex/bridge/IInternalAccess;Ljava/lang/String;)V"));
						newins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "writeArrayField", 
							"(Ljava/lang/Object;ILjava/lang/Object;Ljava/lang/Object;Ljava/lang/String;)V"));
						
						ins.insert(n.getPrevious(), newins);
						ins.remove(n); // remove old Xastore
					}
					else if(!paramnames.isEmpty())
					{
						String paramname = paramnames.get(0);
						
						newins.add(new VarInsnNode(Opcodes.ALOAD, 0));
						newins.add(new LdcInsnNode(paramname));
						newins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "writeArrayParameterField", 
							"(Ljava/lang/Object;ILjava/lang/Object;Ljava/lang/Object;Ljava/lang/String;)V"));
						
						ins.insert(n.getPrevious(), newins);
						ins.remove(n); // remove old Xastore
					}
					
					lab = null;
					belnames.clear();
					paramnames.clear();
				}
			}
		}
	}
	
	/**
	 *  Transform byte Array into Class and define it in classloader.
	 *  @return the loaded class or <code>null</code>, if the class is not valid, such as Map.entry "inner Classes".
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
	 *  Find beliefs accessed in methods.
	 */
	protected Set<String> findBeliefs(ClassNode cn, MethodNode mn, BDIModel model, Map<String, ClassNode> others)
	{
		Set<String> ret = new HashSet<String>();
		
		InsnList l = mn.instructions;
		String refob = null;
		
		for(int i=0; i<l.size(); i++)
		{
			AbstractInsnNode node = l.get(i);
			
			// Find direct field accesses
			if(node instanceof FieldInsnNode)
			{
				FieldInsnNode fnode = (FieldInsnNode)node;
				//if(fnode.getOpcode()==Opcodes.GETFIELD)
				if(model.getCapability().hasBelief(fnode.name))
				{
					ret.add(fnode.name);
				}
				else if(fnode.name.startsWith("this$"))
				{
					refob = fnode.name;
				}
			}
			// Find getter accesses
			else if(node instanceof MethodInsnNode && ((MethodInsnNode)node).name.startsWith("get"))
			{
				MethodInsnNode gnode = (MethodInsnNode)node;
				String name = gnode.name.substring(3);
				String bname = model.getCapability().hasBeliefIgnoreCase(name);
				if(bname!=null)
					ret.add(bname);
			}
			// Find boolean getter accesses
			else if(node instanceof MethodInsnNode && ((MethodInsnNode)node).name.startsWith("is"))
			{
				MethodInsnNode gnode = (MethodInsnNode)node;
				String name = gnode.name.substring(2);
				String bname = model.getCapability().hasBeliefIgnoreCase(name);
				if(bname!=null)
					ret.add(bname);
			}
			else if(node instanceof MethodInsnNode && ((MethodInsnNode)node).name.startsWith("access$"))
			{
//				System.out.println("found access: "+((MethodInsnNode)node).name);
				// found synthetic access to private field of e.g. outer class 
				
				// find the type of the field on which the access$ is performed
				List<FieldNode> fns = cn.fields;
				for(FieldNode fn: fns)
				{
					if(fn.name.equals(refob))
					{
						Type t = Type.getType(fn.desc);

						// search the class (node) this$ refers to 
						ClassNode ocl = others.get(t.getClassName());
						if(ocl!=null)
						{
							// search the access$ method on that class
							List<MethodNode> mnodes = ocl.methods;
							for(MethodNode mnode: mnodes)
							{
								// add dependencies of that access$ method
								if(mnode.name.equals(((MethodInsnNode)node).name))
								{
									ret.addAll(findBeliefs(ocl, mnode, model, others));
									break;
								}
							}
						}
						break;
					}
				}
			}
		}
		
//		System.out.println("Found belief accesses: "+cn.name+" "+ret+" in "+mn.name);
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static void main(String[] args) throws Exception
	{
//		System.out.println(int.class.getName());
		
//		Method m = SReflect.getMethods(BDIAgent.class, "writeArrayField")[0];
//		Method[] ms = SReflect.getMethods(SReflect.class, "wrapValue");
//		for(Method m: ms)
//		{
//			System.out.println(m.toString()+" "+Type.getMethodDescriptor(m));
//		}
				
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//		TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out));
		TraceClassVisitor tcv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
//		CheckClassAdapter cc = new CheckClassAdapter(tcv);
		
//		final String classname = "lars/Lars";
//		final String supername = "jadex/bdiv3/MyTestClass";
		
//		final ASMifier asm = new ASMifier();
		
		ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, tcv)
		{
//			public void visit(int version, int access, String name,
//				String signature, String superName, String[] interfaces)
//			{
//				super.visit(version, access, classname, null, superName, interfaces);
//			}
		
			public MethodVisitor visitMethod(int access, final String methodname, String desc, String signature, String[] exceptions)
			{
				return new MethodVisitor(api, super.visitMethod(access, methodname, desc, signature, exceptions))
				{
//					public void visitFieldInsn(int opcode, String owner, String name, String desc)
//					{
//						super.visitFieldInsn(opcode, owner, methodname, desc);
//					}
					public void visitInsn(int opcode)
					{
						super.visitInsn(opcode);
					}
				};
				
//				return super.visitMethod(access, methodname, desc, signature, exceptions);
//				
//				System.out.println("visit method: "+methodname);
				
//				if("<init>".equals(methodname))
//				{
//					return new TraceMethodVisitor(super.visitMethod(access, methodname, desc, signature, exceptions), asm);
//				}
//				else
//				{
//					return super.visitMethod(access, methodname, desc, signature, exceptions);
//				}
			}
		};
		
		ClassReader cr = new ClassReader("jadex.bdiv3.MyTestClass");
		cr.accept(cv, 0);
//		ClassNode cn = new ClassNode();
//		cr.accept(cn, 0);
//		
//		String prefix = "__update";
//		Set<String> todo = new HashSet<String>();
//		todo.add("testfield");
//		todo.add("testfield2");
//		todo.add("testfield3");
//		
//		MethodNode[] mths = cn.methods.toArray(new MethodNode[0]);
//		for(MethodNode mn: mths)
//		{
//			System.out.println(mn.name);
//			if(mn.name.equals("<init>"))
//			{
//				InsnList l = cn.methods.get(0).instructions;
//				for(int i=0; i<l.size() && !todo.isEmpty(); i++)
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
//						if(Opcodes.PUTFIELD==fn.getOpcode() && todo.contains(fn.name))
//						{
//							todo.remove(fn.name);
//							System.out.println("found putfield node: "+fn.name+" "+fn.getOpcode());
//							AbstractInsnNode start = fn;
//							while(!(start instanceof LabelNode))
//							{
//								start = start.getPrevious();
//							}
//
//							MethodNode mnode = new MethodNode(mn.access, prefix+SUtil.firstToUpperCase(fn.name), mn.desc, mn.signature, null);
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
//							mnode.instructions.add(start.clone(labels));
//							mnode.visitInsn(Opcodes.RETURN);
//							
//							cn.methods.add(mnode);
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
//		cw.accept(tcv);
//		byte[] data = cw.toByteArray();
		
//		ByteClassLoader bcl = new ByteClassLoader(ASMBDIClassGenerator.class.getClassLoader());
		
//		Class<?> cl = toClass("jadex.bdiv3.MyTestClass", data, new URLClassLoader(new URL[0], ASMBDIClassGenerator.class.getClassLoader()), null);
////		Class<?> cl = bcl.loadClass("lars.Lars", cw.toByteArray(), true);
//		Object o = cl.newInstance();
////		System.out.println("o: "+o);
////		Object v = cl.getMethod("getVal", new Class[0]).invoke(o, new Object[0]);
//		String mn = prefix+SUtil.firstToUpperCase("testfield");
//		Object v = cl.getMethod(mn, new Class[0]).invoke(o, new Object[0]);
//		cl.getMethod(mn, new Class[0]).invoke(o, new Object[0]);
//		cl.getMethod(mn, new Class[0]).invoke(o, new Object[0]);
//		System.out.println("res: "+cl.getDeclaredField("testfield").get(o));
		
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
