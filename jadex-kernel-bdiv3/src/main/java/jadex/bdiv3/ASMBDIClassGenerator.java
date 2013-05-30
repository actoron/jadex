package jadex.bdiv3;

import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kohsuke.asm4.ClassReader;
import org.kohsuke.asm4.ClassVisitor;
import org.kohsuke.asm4.ClassWriter;
import org.kohsuke.asm4.Label;
import org.kohsuke.asm4.MethodVisitor;
import org.kohsuke.asm4.Opcodes;
import org.kohsuke.asm4.Type;
import org.kohsuke.asm4.tree.AbstractInsnNode;
import org.kohsuke.asm4.tree.AnnotationNode;
import org.kohsuke.asm4.tree.ClassNode;
import org.kohsuke.asm4.tree.FieldInsnNode;
import org.kohsuke.asm4.tree.InsnList;
import org.kohsuke.asm4.tree.InsnNode;
import org.kohsuke.asm4.tree.LabelNode;
import org.kohsuke.asm4.tree.LdcInsnNode;
import org.kohsuke.asm4.tree.MethodInsnNode;
import org.kohsuke.asm4.tree.MethodNode;
import org.kohsuke.asm4.tree.VarInsnNode;
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
		return generateBDIClass(clname, model, cl, new HashSet<String>());
	}
	
	/**
	 *  Generate class.
	 */
	public Class<?> generateBDIClass(final String clname, final BDIModel model, 
		final ClassLoader cl, final Set<String> done)
	{
		Class<?> ret = null;
		
//		System.out.println("Generating with cl: "+cl+" "+clname);
		
		final List<String> todo = new ArrayList<String>();
		done.add(clname);
		
		try
		{
	//		String clname = cma.getName()+BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST;
			
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			ClassNode cn = new ClassNode();
//			TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out))
//			TraceClassVisitor tcv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
//			CheckClassAdapter cc = new CheckClassAdapter(cw);
			
			final String iclname = clname.replace(".", "/");
			
			ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cn)
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
							if(Opcodes.PUTFIELD==opcode && model.getCapability().hasBelief(name) 
								&& model.getCapability().getBelief(name).isFieldBelief())
							{
//								visitInsn(Opcodes.POP);
//								visitInsn(Opcodes.POP);
								
								// stack before putfield is object,value ()
//								System.out.println("method: "+methodname+" "+name);

								// must do both in constructor
								// a) write init values also immediately to allow dependencies
								// b) invoke writefield (agent=null) to create rule events later
//								if("<init>".equals(methodname))
//								{
//									super.visitFieldInsn(opcode, owner, name, desc);
//									visitLabel(new Label());
//									visitVarInsn(Opcodes.ALOAD, 0);
//									visitInsn(Opcodes.DUP);
//									super.visitFieldInsn(Opcodes.GETFIELD, owner, name, desc);
//								}
								// is already on stack (object + value)
	//							mv.visitVarInsn(ALOAD, 0);
	//							mv.visitIntInsn(BIPUSH, 25);
								
//								System.out.println("vis: "+opcode+" "+owner+" "+name+" "+desc);
//								System.out.println(Type.getType(desc).getClassName());
																
								// possibly transform basic value
								if(SReflect.isBasicType(SReflect.findClass0(Type.getType(desc).getClassName(), null, cl)))
									visitMethodInsn(Opcodes.INVOKESTATIC, "jadex/commons/SReflect", "wrapValue", "("+desc+")Ljava/lang/Object;");
								
								visitInsn(Opcodes.SWAP);
								
								// fetch bdi agent value from field

								// this pop aload is necessary in inner classes!
								visitInsn(Opcodes.POP);
								visitVarInsn(Opcodes.ALOAD, 0);
								super.visitFieldInsn(Opcodes.GETFIELD, iclname, "__agent", Type.getDescriptor(BDIAgent.class));
//								super.visitFieldInsn(Opcodes.GETFIELD, iclname, "__agent", "L"+iclname+";");
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
						
//						public void visitInsn(int opcode)
//						{
//							if(Opcodes.AASTORE==opcode)
//							{
//								// on stack: arrayref, index, value 
//								visitVarInsn(Opcodes.ALOAD, 0);
//								super.visitFieldInsn(Opcodes.GETFIELD, iclname, "__agent", Type.getDescriptor(BDIAgent.class));
//
//								// invoke method
//								visitMethodInsn(Opcodes.INVOKESTATIC, "jadex/bdiv3/BDIAgent", "writeArrayField", "(Ljava/lang/Object;ILjava/lang/Object;Ljadex/bdiv3/BDIAgent;)V");
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
//					System.out.println("vic: "+name+" "+outerName+" "+innerName+" "+access);
					String icln = name.replace("/", ".");
					if(!done.contains(icln))
						todo.add(icln);
					super.visitInnerClass(name, outerName, innerName, access);//Opcodes.ACC_PUBLIC); does not work
				}
				
				public void visitEnd()
				{
					visitField(Opcodes.ACC_PUBLIC, "__agent", Type.getDescriptor(BDIAgent.class), null, null);
					visitField(Opcodes.ACC_PUBLIC, "__globalname", Type.getDescriptor(String.class), null, null);
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
				transformClassNode(cn, iclname, model);
				cn.accept(cw);
				byte[] data = cw.toByteArray();
				
//				CheckClassAdapter.verify(new ClassReader(data), false, new PrintWriter(System.out));
				
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
			
			for(String icl: todo)
			{
				generateBDIClass(icl, model, cl, done);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected void transformClassNode(ClassNode cn, final String clname, final BDIModel model)
	{
		// Some transformations are only applied to the agent class and not its inner classes.
		boolean	agentclass	= false;
		if(cn.visibleAnnotations!=null)
		{
			for(AnnotationNode an: cn.visibleAnnotations)
			{
				if("Ljadex/micro/annotation/Agent;".equals(an.desc)
					|| "Ljadex/bdiv3/annotation/Capability;".equals(an.desc))
				{
					agentclass	= true;
					break;
				}
			}
		}
		
		final String iclname = clname.replace(".", "/");
				
		// Check method for array store access of beliefs and replace with static method call
		MethodNode[] mths = cn.methods.toArray(new MethodNode[0]);
		for(MethodNode mn: mths)
		{
//			System.out.println(mn.name);
			
			InsnList ins = mn.instructions;
			LabelNode lab = null;
			List<String> belnames = new ArrayList<String>();
			
			AbstractInsnNode[] nodes = ins.toArray();
			for(AbstractInsnNode n: nodes)
			{
				if(lab==null && n instanceof LabelNode)
				{
					lab = (LabelNode)n;
					belnames.clear();
				}
				
				if(n.getOpcode()==Opcodes.GETFIELD)
				{
					String bn = ((FieldInsnNode)n).name;
					if(model.getCapability().hasBelief(bn))
					{
						belnames.add(bn);
					}
				}
				
				if(!belnames.isEmpty())
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
	//					System.out.println("found: "+belnames);
						String belname = belnames.get(0);
						
						newins.add(new VarInsnNode(Opcodes.ALOAD, 0));
						newins.add(new FieldInsnNode(Opcodes.GETFIELD, iclname, "__agent", Type.getDescriptor(BDIAgent.class)));
						newins.add(new LdcInsnNode(belname));
						newins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/BDIAgent", "writeArrayField", 
							"(Ljava/lang/Object;ILjava/lang/Object;Ljadex/bdiv3/BDIAgent;Ljava/lang/String;)V"));
						
						ins.insert(n.getPrevious(), newins);
						ins.remove(n); // remove old Xastore
						
						lab = null;
						belnames.clear();
					}
				}
			}
		}
		
		// Check if there are dynamic beliefs
		// and enhance getter/setter beliefs by adding event call to setter
		List<String> tododyn = new ArrayList<String>();
		List<String> todoset = new ArrayList<String>();
		List<MBelief> mbels = model.getCapability().getBeliefs();
		for(MBelief mbel: mbels)
		{
			Collection<String> evs = mbel.getEvents();
			if(evs!=null && !evs.isEmpty() || mbel.isDynamic())
			{
				tododyn.add(mbel.getName());
			}
			
			if(!mbel.isFieldBelief())
			{
				todoset.add(mbel.getSetter().getName());
			}
		}
		
		if(agentclass)
		{
//			MethodNode[] mths = cn.methods.toArray(new MethodNode[0]);
			for(MethodNode mn: mths)
			{
//				System.out.println(mn.name);
				
				// search constructor (should not have multiple ones) 
				// and extract field assignments for dynamic beliefs
				// will be incarnated as new update methods 
				if(mn.name.equals("<init>"))
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
							
//							System.out.println("found writeField node: "+min.name+" "+min.getOpcode());
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
							
							if(tododyn.remove(name))
							{
								MBelief mbel = model.getCapability().getBelief(name);
								mbel.getEvents().addAll(evs);
								
								MethodNode mnode = new MethodNode(mn.access, IBDIClassGenerator.DYNAMIC_BELIEF_UPDATEMETHOD_PREFIX
									+SUtil.firstToUpperCase(name), mn.desc, mn.signature, null);
								
								// First labels are cloned
								AbstractInsnNode cur = start;
								Map<LabelNode, LabelNode> labels = new HashMap<LabelNode, LabelNode>();
								while(!cur.equals(min))
								{
									if(cur instanceof LabelNode)
										labels.put((LabelNode)cur, new LabelNode(new Label()));
									cur = cur.getNext();
								}
								// Then code is cloned
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
						String name	= IBDIClassGenerator.INIT_EXPRESSIONS_METHOD_PREFIX+"_"+clname.replace("/", "_").replace(".", "_");
//						System.out.println("Init method: "+name);
						MethodNode mnode = new MethodNode(mn.access, name, mn.desc, mn.signature, null);
						cn.methods.add(mnode);

						while(l.size()>foundcon+1)
						{
							AbstractInsnNode	node	= l.get(foundcon+1);
							if(node.getOpcode()==Opcodes.RETURN)
							{
								break;
							}
							l.remove(node);
							mnode.instructions.add(node);
						}						
						mnode.visitInsn(Opcodes.RETURN);
					}
				}
				// Enhance setter method with unobserve oldvalue at the beginning and event call at the end
				else if(todoset.contains(mn.name))
				{
					if((mn.access&Opcodes.ACC_NATIVE)!=0)
					{
						// todo: native methods
						
						mn.access = mn.access-Opcodes.ACC_NATIVE;
						InsnList nl = new InsnList();
						
					    nl.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream"));
					    nl.add(new LdcInsnNode("Test"));    
					    nl.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
						nl.add(new InsnNode(Opcodes.RETURN));
						
						mn.instructions = nl;
					}
					else
					{
						String belname = mn.name.substring(3);
						belname = belname.substring(0,1).toLowerCase()+belname.substring(1);
						
						InsnList l = mn.instructions;
						
	//					System.out.println("icl: "+iclname);
						
						InsnList nl = new InsnList();
						nl.add(new VarInsnNode(Opcodes.ALOAD, 0)); // loads the object
						nl.add(new FieldInsnNode(Opcodes.GETFIELD, iclname, "__agent", Type.getDescriptor(BDIAgent.class)));
						nl.add(new LdcInsnNode(belname));
						nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/BDIAgent", "unobserveValue", 
	//						"(Ljava/lang/String;)V"));
							"(Ljadex/bdiv3/BDIAgent;Ljava/lang/String;)V"));
						l.insertBefore(l.getFirst(), nl);
						
						nl = new InsnList();
						nl.add(new VarInsnNode(Opcodes.ALOAD, 1)); // loads the argument (=parameter0)
						nl.add(new VarInsnNode(Opcodes.ALOAD, 0)); // loads the object
						nl.add(new FieldInsnNode(Opcodes.GETFIELD, iclname, "__agent", Type.getDescriptor(BDIAgent.class)));
						nl.add(new LdcInsnNode(belname));
						nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/BDIAgent", "createEvent", 
							"(Ljava/lang/Object;Ljadex/bdiv3/BDIAgent;Ljava/lang/String;)V"));
	//					nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/BDIAgent", "createEvent", 
	//						"()V"));
	
						// Find return and insert call before that
						AbstractInsnNode n;
						for(n = l.getLast(); n.getOpcode()!=Opcodes.RETURN; n = n.getPrevious())
						{
						}
						l.insertBefore(n, nl);
					}
				}
			}
		}
	}
	
	/**
	 * 
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
					// when same class was already loaded via other filename wrong cache miss:-(
//					ret = SReflect.findClass(name, null, loader);
					ret = Class.forName(name, true, loader);
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
		
//		Method m = SReflect.getMethods(BDIAgent.class, "writeArrayField")[0];
		Method[] ms = SReflect.getMethods(SReflect.class, "wrapValue");
		for(Method m: ms)
		{
			System.out.println(m.toString()+" "+Type.getMethodDescriptor(m));
		}
				
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out));
//		TraceClassVisitor tcv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
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
