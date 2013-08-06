package jadex.bdiv3;

import jadex.android.commons.JadexDexClassLoader;
import jadex.android.commons.Logger;
import jadex.bdiv3.android.DexLoader;
import jadex.bdiv3.android.LogClassWriter;
import jadex.bdiv3.android.MethodSignature;
import jadex.bdiv3.asmdex.ClassNodeWrapper;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.javaparser.javaccimpl.ConstantNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javassist.bytecode.Opcode;

import org.objectweb.asm.Type;
import org.ow2.asmdex.ApplicationReader;
import org.ow2.asmdex.ApplicationVisitor;
import org.ow2.asmdex.ApplicationWriter;
import org.ow2.asmdex.ClassVisitor;
import org.ow2.asmdex.MethodVisitor;
import org.ow2.asmdex.Opcodes;
import org.ow2.asmdex.tree.AbstractInsnNode;
import org.ow2.asmdex.tree.ApplicationNode;
import org.ow2.asmdex.tree.ArrayOperationInsnNode;
import org.ow2.asmdex.tree.ClassNode;
import org.ow2.asmdex.tree.FieldInsnNode;
import org.ow2.asmdex.tree.FieldNode;
import org.ow2.asmdex.tree.InsnList;
import org.ow2.asmdex.tree.InsnNode;
import org.ow2.asmdex.tree.IntInsnNode;
import org.ow2.asmdex.tree.LabelNode;
import org.ow2.asmdex.tree.MethodInsnNode;
import org.ow2.asmdex.tree.MethodNode;
import org.ow2.asmdex.tree.StringInsnNode;
import org.ow2.asmdex.tree.TypeInsnNode;
import org.ow2.asmdex.tree.VarInsnNode;
import org.ow2.asmdex.util.RegisterShiftMethodAdapter;

import android.util.Log;

public class AsmDexBdiClassGenerator extends AbstractAsmBdiClassGenerator
{
	protected static Method methoddc1;
	protected static Method methoddc2;
	public static File OUTPATH;

	static
	{
		try
		{
			AccessController.doPrivileged(new PrivilegedExceptionAction<Object>()
			{
				public Object run() throws Exception
				{
					Class<?> cl = Class.forName("java.lang.ClassLoader");
					methoddc1 = cl.getDeclaredMethod("defineClass", new Class[]
					{String.class, byte[].class, int.class, int.class});
					methoddc2 = cl.getDeclaredMethod("defineClass", new Class[]
					{String.class, byte[].class, int.class, int.class, ProtectionDomain.class});
					return null;
				}
			});
		}
		catch (PrivilegedActionException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<?> generateBDIClass(String classname, BDIModel micromodel, ClassLoader cl)
	{
		return generateBDIClass(classname, micromodel, cl, new HashSet<String>());
	}

	/**
	 * Generate class. Generated class should be available in the given
	 * classLoader at the end of this method.
	 */
	public Class<?> generateBDIClass(final String clname, final BDIModel model, final ClassLoader cl, final Set<String> done)
	{
		Class<?> ret = null;

		final List<String> todo = new ArrayList<String>();
		done.add(clname);

		int api = Opcodes.ASM4;
		final String iclname = clname.replace(".", "/");
		final String iname = "L" + clname.replace('.', '/') + ";";

		// pattern to accept all inner classes
		final Pattern classPattern = Pattern.compile("L" + clname.replace('.', '/') + ";?\\$?.*");

		try
		{
			JadexDexClassLoader androidCl = (JadexDexClassLoader) SUtil.androidUtils().findJadexDexClassLoader(cl.getParent());
			// is = SUtil.getResource(APP_PATH, cl);
			String appPath = ((JadexDexClassLoader) androidCl).getDexPath();
			InputStream is = getFileInputStream(new File(appPath));
			ApplicationReader ar = new ApplicationReader(api, is);
			ApplicationWriter aw = new ApplicationWriter();
			ApplicationNode an = new ApplicationNode(api);
			
			final ArrayList<ClassNode> classes = new ArrayList<ClassNode>();
			
			ApplicationVisitor av = new ApplicationVisitor(api, an)
			{

				@Override
				public ClassVisitor visitClass(int access, String name, String[] signature, String superName, String[] interfaces)
				{
					if (classPattern.matcher(name).matches())
					{
						System.out.println("visit class: " + name);
						final ClassNode cn = new ClassNode(api, access, name, signature, superName, interfaces);
						classes.add(cn);
						final ClassVisitor superVisitor = super.visitClass(access, iname, signature, superName, interfaces);
						ClassVisitor cv = new ClassVisitor(api, cn)
						{

							@Override
							public MethodVisitor visitMethod(int access, String name, final String mDesc, final String[] signature, String[] exceptions)
							{
								MethodVisitor mv = new MethodVisitor(api, super.visitMethod(access, name, mDesc, signature, exceptions))
								{
									
									private int maxStackIndex;
									// stack will be shifted by 2
									public void visitMaxs(int maxStack, int maxLocals) {
										this.maxStackIndex = maxStack-1;
										super.visitMaxs(maxStack, maxLocals);
									};
									
									public void visitParameters(String[] parameters) {
										super.visitParameters(parameters);
									};
									
									@Override
									public void visitFieldInsn(int opcode, String owner, String name, String desc, int valueRegister,
											int objectRegister)
									{
										// objectRegister = reference to instance (ignored when static),
										// valueRegister = index of value to put!
										// Parameters are put in the last registers. So if we have 5 registers and 1 param = p0,
										// v0,v1,v2,v3 are free and v4 = p0
										
										// After increasing the registers by 2, we should have 2 unused local registers:
										String mydesc = mDesc;
										Type[] argumentTypes = Type.getArgumentTypes(mydesc);
										
										int occupiedRegisters = (argumentTypes != null ? argumentTypes.length : 0) +1 ;
										int v0 = 0;
										int v1 = 1;
										
										
										if (isInstancePutField(opcode) && model.getCapability().hasBelief(name)
												&& model.getCapability().getBelief(name).isFieldBelief())
										{
											//
											// possibly transform basic value
											if (SReflect.isBasicType(SReflect.findClass0(Type.getType(desc).getClassName(), null, cl)))
											{
												visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, "Ljadex/commons/SReflect;", "wrapValue",
														"Ljava/lang/Object;" + desc, new int[]
														{valueRegister});
												
												// valueRegister = wrapped primitive parameter
												visitIntInsn(Opcodes.INSN_MOVE_RESULT_OBJECT, valueRegister); // move result, replace given primitive value in register
												
//									 			visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, LogClassWriter.LOG_CLASSNAME, "log", "VLjava/lang/Object;", new int[]{valueRegister});
											}

											// v0 = __agent
											super.visitFieldInsn(Opcodes.INSN_IGET_OBJECT, iname, "__agent", Type.getDescriptor(BDIAgent.class), v0, objectRegister);
											
											// v1 = field name
											visitStringInsn(Opcodes.INSN_CONST_STRING, v1, name);
											
											// log everything:
											visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, LogClassWriter.LOG_CLASSNAME, "log", "VLjava/lang/Object;", new int[]{valueRegister});
											visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, LogClassWriter.LOG_CLASSNAME, "log", "VLjava/lang/Object;", new int[]{v0});
											visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, LogClassWriter.LOG_CLASSNAME, "log", "VLjava/lang/Object;", new int[]{v1});
											visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, LogClassWriter.LOG_CLASSNAME, "log", "VLjava/lang/Object;", new int[]{objectRegister});
											
											// objectRegister = reference to instance (ignored when static),
											// valueRegister = wrapped primitive parameter
											// v0 = __agent
											// v1 = field name
											
											super.visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, "Ljadex/bdiv3/BDIAgent;", "writeField", "VLjava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljadex/bdiv3/BDIAgent;", new int[]{valueRegister, v1, objectRegister, v0});
										}
										else
										{
											super.visitFieldInsn(opcode, owner, name, desc, valueRegister, objectRegister);
										}
									}

								};
								
								return new RegisterShiftMethodAdapter(api, mv, 2); // shifts all register access by 2
							}

							@Override
							public void visitInnerClass(String name, String outerName, String innerName, int access)
							{
								String icln = (name == null ? null : name.replace("/", "."));
								if (!done.contains(icln))
									todo.add(icln);
								super.visitInnerClass(name, outerName, innerName, access);// Opcodes.ACC_PUBLIC);
																							// does
																							// not
																							// work
							}

							@Override
							public void visitEnd()
							{
								visitField(Opcodes.ACC_PUBLIC, "__agent", Type.getDescriptor(BDIAgent.class), null, null);
								visitField(Opcodes.ACC_PUBLIC, "__globalname", Type.getDescriptor(String.class), null, null);
								super.visitEnd();
							}

						};

						return cv;
					}
					else
					{
						return null;
					}
				}

			};

//			 ar.accept(aa, new String[]{iname}, 0);
			ar.accept(av, null, 0); // visit all classes
			
			aw.visit();
			for (ClassNode classNode : classes)
			{
				transformClassNode(classNode, iclname, model);
				String[] signature = classNode.signature == null? null : classNode.signature.toArray(new String[classNode.signature.size()]);
				String[] interfaces = classNode.interfaces == null ? null :classNode.interfaces.toArray(new String[classNode.interfaces.size()]);
				System.out.println("write class: " + classNode.name);
				ClassVisitor visitClass = aw.visitClass(classNode.access, classNode.name, signature, classNode.superName, interfaces);
				classNode.accept(visitClass);
			}
			aw.visitEnd();

			byte[] dex = aw.toByteArray();

			// we need the android user loader as parent here, because class
			// dependencies for the agent could exist
			ClassLoader newCl = DexLoader.load(androidCl, dex, OUTPATH);

			for (ClassNode classNode : classes) {
				// load all generated classes, including inner classes
				String className = Type.getType(classNode.name).getClassName();
				Class<?> clazz = newCl.loadClass(className);
				androidCl.defineClass(className, clazz);
			}
			
			Class<?> generatedClass = newCl.loadClass(clname);
			ret = generatedClass;

//			if (androidCl != null)
//			{
//				androidCl.defineClass(clname, generatedClass);
//			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	private void transformClassNode(ClassNode cn, String iclname, BDIModel model)
	{
		// Some transformations are only applied to the agent class and not its
		// inner classes.
		boolean agentclass = isAgentClass(ClassNodeWrapper.wrap(cn));

//		final String iclname = iname.replace(".", "/");

		// Check method for array store access of beliefs and replace with
		// static method call
		MethodNode[] mths = cn.methods.toArray(new MethodNode[0]);
		
		// TODO arraystorage
		
		
		if(agentclass)
		{
			// Check if there are dynamic beliefs
			// and enhance getter/setter beliefs by adding event call to setter
			List<String> tododyn = new ArrayList<String>();
			List<String> todoset = new ArrayList<String>();
			List<String> todoget = new ArrayList<String>();
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
				
				if(!mbel.isFieldBelief())
				{
					todoget.add(mbel.getGetter().getName());
				}
			}
			
			cn.fields.add(new FieldNode(Opcodes.ACC_PROTECTED, "__initargs", "Ljava/util/List;", new String[]{"Ljava/util/List<Ljadex/commons/Tuple3<Ljava/lang/Class<*>;[Ljava/lang/Class<*>;[Ljava/lang/Object;>;>;"}, null));
			
			for(MethodNode constructorNode: mths)
			{
//				System.out.println(mn.name);
				
				// search constructor (should not have multiple ones) 
				// and extract field assignments for dynamic beliefs
				// will be incarnated as new update methods 
				if(constructorNode.name.equals("<init>"))
				{
					InsnList instructions = constructorNode.instructions;
					LabelNode begin = null;
					int superConstructorIndex = -1;
					
					for(int i=0; i<instructions.size(); i++)
					{
						AbstractInsnNode n = instructions.get(i);
						
						if(begin==null && n instanceof LabelNode)
						{
							begin = (LabelNode)n;
						}
						
						// find first constructor call
						if(Opcodes.INSN_INVOKE_DIRECT==n.getOpcode() && superConstructorIndex==-1)
						{
							superConstructorIndex = i;
							begin = null;
						}
						else if(n instanceof MethodInsnNode && ((MethodInsnNode)n).name.equals("writeField"))
						{
//							MethodInsnNode min = (MethodInsnNode)n;
//							
//							AbstractInsnNode start = min;
//							String name = null;
//							List<String> events = new ArrayList<String>();
//							// get name of method and events?
//							while(!start.equals(begin))
//							{
//								// find method name via last constant load
//								if (name == null) {
//									System.out.println(start);
//									// TODO: find method name !!
//								}
////								if(name==null && start instanceof LdcInsnNode)
////									name = (String)((LdcInsnNode)start).cst;
//								if(isInstanceGetField(start.getOpcode()))
//								{
//									String bn = ((FieldInsnNode)start).name;
//									if(model.getCapability().hasBelief(bn))
//									{
//										events.add(bn);
//									}
//								}
//								start = start.getPrevious();
//							}
							// start is the first labelNode before min (the 'writeField' Insn)
							// TODO: dynamic beliefs
//							if(tododyn.remove(name))
//							{
//								MBelief mbel = model.getCapability().getBelief(name);
//								mbel.getEvents().addAll(evs);
//								
//								MethodNode mnode = new MethodNode(Opcodes.ACC_PUBLIC, IBDIClassGenerator.DYNAMIC_BELIEF_UPDATEMETHOD_PREFIX
//									+SUtil.firstToUpperCase(name), Type.getMethodDescriptor(Type.VOID_TYPE), null, null);
//								
//								// First labels are cloned
//								AbstractInsnNode cur = start;
//								Map<LabelNode, LabelNode> labels = new HashMap<LabelNode, LabelNode>();
//								while(!cur.equals(min))
//								{
//									if(cur instanceof LabelNode)
//										labels.put((LabelNode)cur, new LabelNode(new Label()));
//									cur = cur.getNext();
//								}
//								// Then code is cloned
//								cur = start;
//								while(!cur.equals(min))
//								{
//									AbstractInsnNode clone = cur.clone(labels);
//									mnode.instructions.add(clone);
//									cur = cur.getNext();
//								}
//								mnode.instructions.add(cur.clone(labels));
//								mnode.visitInsn(Opcodes.RETURN);
//								
//								cn.methods.add(mnode);
//							}
							
							begin = null;
						}
					}
					
					// Move init code to separate method for being called after injections. 
					if(superConstructorIndex!=-1 && superConstructorIndex+1<instructions.size())
					{
						String name	= IBDIClassGenerator.INIT_EXPRESSIONS_METHOD_PREFIX+"_" + iclname.replace("/", "_").replace(".", "_");
						System.out.println("creating Init method: "+name);
						MethodNode initMethodNode = new MethodNode(Opcodes.ACC_PUBLIC, name, constructorNode.desc, constructorNode.signature, null);
						cn.methods.add(initMethodNode);
						initMethodNode.visitMaxs(constructorNode.maxStack, 0);
						
						constructorNode.visitMaxs(constructorNode.maxStack + 3, 0); // need 3 more registers, got 2 from shift before 
						int maxStackIndex = constructorNode.maxStack - 1;
						int p0 = maxStackIndex - (initMethodNode.signature == null ? 0 : initMethodNode.signature.length - 1); // this
						
						int v0 = 0; 
						int v1 = 1;
						int v2 = 2;
						int v3 = 3;
						int v4 = 4;
						
						// move all instructions except super call to new init method
						while(instructions.size()>superConstructorIndex+1)
						{
							AbstractInsnNode	node	= instructions.get(superConstructorIndex+1);
							if(isReturn(node.getOpcode()))
							{
								initMethodNode.visitInsn(node.getOpcode());
								break;
							}
							instructions.remove(node);
							initMethodNode.instructions.add(node);
						}
						
						// shift constructor call registers:
						MethodInsnNode superConstructorCall = (MethodInsnNode) instructions.get(superConstructorIndex);
						int[] arguments = superConstructorCall.arguments;
						int[] newArguments = new int[arguments.length];
						for (int i = 0; i < newArguments.length; i++)
						{
							newArguments[i] = arguments[i] + 3;
						}
						superConstructorCall.arguments = newArguments;
//						instructions.remove(superConstructorCall);
//						RegisterShiftMethodAdapter shift = new RegisterShiftMethodAdapter(1, constructorNode, 3); // shift by 3 registers
//						shift.visitMethodInsn(superConstructorCall.getOpcode(), superConstructorCall.owner, superConstructorCall.name, superConstructorCall.desc, superConstructorCall.arguments);
						// end shift
						
						// Add code to store all arguments that are needed for the new init method in a field.
						Type[]	args	= Type.getArgumentTypes(constructorNode.desc);
						InsnList	init	= new InsnList();
						
						// argtypes param
						
						init.add(new VarInsnNode(Opcodes.INSN_CONST, v0, args.length));
						// v0 = length of array
						String arrayDesc = Type.getDescriptor(Class[].class);
						init.add(new TypeInsnNode(Opcodes.INSN_NEW_ARRAY, v1, -1, v0, arrayDesc));
						// v1 = argtypes array
						for(int i=0; i<args.length; i++)
						{
							String descriptor = args[i].getDescriptor();
							
							init.add(new TypeInsnNode(Opcodes.INSN_CONST_CLASS, v4, -1, -1, descriptor));
							// v4 = class of parameter i
							init.add(new VarInsnNode(Opcodes.INSN_CONST, v3, i));
							// v3 = i
							// aput-object class(descriptor), array, i 
							init.add(new ArrayOperationInsnNode(Opcodes.INSN_APUT_OBJECT, v4, v1, v3));
						}
						
						// args param
						init.add(new TypeInsnNode(Opcodes.INSN_NEW_ARRAY, v2, -1, v0, Type.getDescriptor(Object[].class)));
						// v2 = args array
						
						for(int i=0; i<args.length; i++)
						{
							init.add(new VarInsnNode(Opcodes.INSN_CONST, v3, i));
							// v3 = i
							
							int valueRegister = p0 + 1 + i; // i-th parameter of constructor is in this register
							
							// aput-object class(descriptor), array, i 
							init.add(new ArrayOperationInsnNode(Opcodes.INSN_APUT_OBJECT, valueRegister, v2, v3));
						}
						
						// now we have 
						// v0 = length of args
						// v1 = argtypes array
						// v2 = args array
						
						init.add(new TypeInsnNode(Opcodes.INSN_CONST_CLASS, v0, -1, -1, "L"+iclname+";"));
						// v0 = class
						
						// p0 = this
						// v0 = class
						// v1 = argument types
						// v2 = argument values
						// Invoke method.
						init.add(new MethodInsnNode(Opcodes.INSN_INVOKE_STATIC, "Ljadex/bdiv3/BDIAgent;", "addInitArgs", "VLjava/lang/Object;Ljava/lang/Class;[Ljava/lang/Class;[Ljava/lang/Object;", new int[]{p0,v0,v1,v2}));
						instructions.insertBefore(instructions.get(superConstructorIndex+1), init);
					}
				} // constructor end
			}
		}
	}

	private InputStream getFileInputStream(File apkFile)
	{
		InputStream result = null;
		String desiredFile = "classes.dex";
		try
		{
			FileInputStream fin = new FileInputStream(apkFile);
			@SuppressWarnings("resource")
			// Resource is closed later
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null)
			{
				Logger.d("Unzipping " + ze.getName());

				if (!ze.isDirectory())
				{

					/**** Changes made below ****/
					if (ze.getName().toString().equals(desiredFile))
					{
						result = zin;
						break;
					}

				}

				zin.closeEntry();

			}
			// zin.close();
		}
		catch (Exception e)
		{
			Log.e("Decompress", "unzip", e);
		}

		return result;
	}

	protected boolean isInstancePutField(int opcode)
	{
		switch (opcode)
		{
			case Opcodes.INSN_IPUT :
			case Opcodes.INSN_IPUT_BOOLEAN :
			case Opcodes.INSN_IPUT_BYTE :
			case Opcodes.INSN_IPUT_CHAR :
			case Opcodes.INSN_IPUT_OBJECT :
			case Opcodes.INSN_IPUT_SHORT :
			case Opcodes.INSN_IPUT_WIDE :
				return true;
			default :
				return false;
		}
	}
	
	protected boolean isInstanceGetField(int opcode)
	{
		switch (opcode)
		{
			case Opcodes.INSN_IGET :
			case Opcodes.INSN_IGET_BOOLEAN :
			case Opcodes.INSN_IGET_BYTE :
			case Opcodes.INSN_IGET_CHAR :
			case Opcodes.INSN_IGET_OBJECT :
			case Opcodes.INSN_IGET_SHORT :
			case Opcodes.INSN_IGET_WIDE :
				return true;
			default :
				return false;
		}
	}
	
	protected boolean isReturn(int opcode)
	{
		switch (opcode)
		{
			case Opcodes.INSN_RETURN :
			case Opcodes.INSN_RETURN_OBJECT :
			case Opcodes.INSN_RETURN_VOID :
			case Opcodes.INSN_RETURN_WIDE :
				return true;
			default :
				return false;
		}
	}

	
}
