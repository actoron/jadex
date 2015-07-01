package jadex.bdiv3;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.rules.eca.EventType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;


/**
 * 
 */
public abstract class AbstractAsmBdiClassGenerator implements IBDIClassGenerator
{
	protected OpcodeHelper ophelper = OpcodeHelper.getInstance();
	
	protected NodeHelper nodehelper = NodeHelper.getInstance();
	
	public abstract List<Class<?>> generateBDIClass(String clname, BDIModel micromodel, ClassLoader cl);

	/**
	 * 
	 * @param cn
	 * @param iclname
	 * @param model
	 */
	protected void transformClassNode(ClassNode cn, final String iclname, final BDIModel model)
	{
		// Some transformations are only applied to the agent class and not its
		// inner classes.
		boolean agentclass = isAgentClass(cn);
		boolean	planclass	= isPlanClass(cn);
		// Check method for array store access of beliefs and replace with
		// static method call
		MethodNode[] mths = (MethodNode[])cn.methods.toArray(new MethodNode[0]);

		for(MethodNode mn : mths)
		{
			transformArrayStores(mn, model, iclname);
		}

		if(agentclass)
		{
			List<String> ifaces = cn.interfaces;
			for(String name: ifaces)
			{
				// Auto-implement abstract methods from IBDIAgent and subinterfaces.
				if(name.indexOf(Type.getInternalName(IBDIAgent.class))!=-1)
				{
//					cn.interfaces.add(Type.getInternalName(INonUserAccess.class));
					// Fetch all methods.
					List<Class<?>> allcz = SUtil.arrayToList(SReflect.getSuperInterfaces(new Class[]{IBDIAgent.class}));
					allcz.add(IBDIAgent.class);
//					allcz.add(INonUserAccess.class);
					Set<Method> allms = new HashSet<Method>();
					for(Class<?> tmp: allcz)
					{
						Method[] mets = tmp.getDeclaredMethods();
						for(Method m: mets)
						{
							allms.add(m);
						}
					}
					
					// Implement all methods
					for(Method m: allms)
					{
						MethodNode mnode = new MethodNode(Opcodes.ACC_PUBLIC, m.getName(), Type.getMethodDescriptor(m), null, null);
						Type ret = Type.getReturnType(mnode.desc);
						InsnList nl = new InsnList();
						
						// Fetch agent object to invoke method on.
						nl.add(new VarInsnNode(Opcodes.ALOAD, 0));
						nl.add(new FieldInsnNode(Opcodes.GETFIELD, iclname, "__agent", "Ljadex/bridge/IInternalAccess;"));
						if(m.getDeclaringClass().equals(IBDIAgentFeature.class))
						{
							nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "jadex/bdiv3/features/impl/BDIAgentFeature", "getBDIAgentFeature", "(Ljadex/bridge/IInternalAccess;)Ljadex/bdiv3/features/IBDIAgentFeature;", false));
						}
						
						// Push parameters to stack
						Class<?>[] ptypes = m.getParameterTypes();
						int cnt = 1;
						for(int i=0; i<ptypes.length; i++)
						{
							if(ptypes[i].equals(boolean.class) || ptypes[i].equals(byte.class) || ptypes[i].equals(int.class) || ptypes[i].equals(short.class))
							{
								nl.add(new VarInsnNode(Opcodes.ILOAD, i+cnt));
							}
							else if(ptypes[i].equals(long.class))
							{
								nl.add(new VarInsnNode(Opcodes.LLOAD, i+cnt++));
							}
							else if(ptypes[i].equals(float.class))
							{
								nl.add(new VarInsnNode(Opcodes.FLOAD, i+cnt));
							}
							else if(ptypes[i].equals(double.class))
							{
								nl.add(new VarInsnNode(Opcodes.DLOAD, i+cnt++));
							}
							else
							{
								nl.add(new VarInsnNode(Opcodes.ALOAD, i+cnt));
							}
//							nl.add(new InsnNode(Opcodes.SWAP));
						}
						
						// Invoke method.
						if(m.getDeclaringClass().equals(IBDIAgentFeature.class))
						{
							nl.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "jadex/bdiv3/features/IBDIAgentFeature", mnode.name, mnode.desc, true));
						}
//						else if(m.getDeclaringClass().equals(INonUserAccess.class))
//						{
//							nl.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "jadex/bridge/INonUserAccess", mnode.name, mnode.desc, true));
//						}
						else
						{
							nl.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "jadex/bridge/IInternalAccess", mnode.name, mnode.desc, true));
						}
						
						// Return result.
						Class<?> rett = m.getReturnType();
						if(ret!=null && !rett.equals(void.class) && !rett.equals(Void.class))
						{
							if(rett.equals(boolean.class) || rett.equals(byte.class) || rett.equals(int.class) || rett.equals(short.class))
							{
								nl.add(new InsnNode(Opcodes.IRETURN));
							}
							else if(rett.equals(long.class))
							{
								nl.add(new InsnNode(Opcodes.LRETURN));
							}
							else if(rett.equals(float.class))
							{
								nl.add(new InsnNode(Opcodes.FRETURN));
							}
							else if(rett.equals(double.class))
							{
								nl.add(new InsnNode(Opcodes.DRETURN));
							}
							else
							{
								String t = ret.toString().length()>1? ret.getInternalName(): ret.toString();
								nl.add(new TypeInsnNode(Opcodes.CHECKCAST, t));
								nl.add(new InsnNode(Opcodes.ARETURN));
							}
						}
						else
						{
							nl.add(new InsnNode(Opcodes.RETURN));
						}
						mnode.instructions = nl;
						cn.methods.add(mnode);
					}
					break;
				}
			}
			
			// Check if there are dynamic beliefs
			// and enhance getter/setter beliefs by adding event call to setter
			List<String> tododyn = new ArrayList<String>();
			List<String> todoset = new ArrayList<String>();
			List<String> todoget = new ArrayList<String>();
			List<MBelief> mbels = model.getCapability().getBeliefs();
			for(MBelief mbel : mbels)
			{
				Collection<String> evs = mbel.getBeliefEvents();
				Collection<EventType> rawevs = mbel.getRawEvents();
				if((evs!=null && !evs.isEmpty()) || (rawevs!=null && !rawevs.isEmpty()) || mbel.isDynamic())
				{
					tododyn.add(mbel.getName());
				}

				if(!mbel.isFieldBelief())
				{
					if(mbel.getSetter()!=null)
						todoset.add(mbel.getSetter().getName());
					if(mbel.getGetter()!=null)
						todoget.add(mbel.getGetter().getName());
				}
			}

			FieldNode initArgsField = nodehelper.createField(OpcodeHelper.ACC_PROTECTED, "__initargs", "Ljava/util/List;", new String[]{"Ljava/util/List<Ljadex/commons/Tuple3<Ljava/lang/Class<*>;[Ljava/lang/Class<*>;[Ljava/lang/Object;>;>;"}, null);
			cn.fields.add(initArgsField);		
			
			for(MethodNode mn : mths)
			{
				if(isPlanMethod(mn))
				{
					int line = nodehelper.getLineNumberOfMethod(mn);
					if(line != -1) 
					{
						MethodNode lineNumberMethod = nodehelper.createReturnConstantMethod("__getLineNumber"+mn.name, line);
						cn.methods.add(lineNumberMethod);
					}
				}
				
				// search constructor (should not have multiple ones)
				// and extract field assignments for dynamic beliefs
				// will be incarnated as new update methods
				if(mn.name.equals("<init>"))
				{
					transformConstructor(cn, mn, model, tododyn);
				}
				else if(todoset.contains(mn.name))
				{
					String belname = mn.name.substring(3); // property name = method name - get/set prefix
					belname = belname.substring(0,1).toLowerCase()+belname.substring(1);
					if(ophelper.isNative(mn.access))
					{
						replaceNativeSetter(iclname, mn, belname);
					} 
					else 
					{
						enhanceSetter(iclname, mn, belname);
					}
				}
				// Enhance native getter method
				else if(todoget.contains(mn.name))
				{
					if(ophelper.isNative(mn.access))
					{
						String belname = mn.name.startsWith("is") ? mn.name.substring(2) : mn.name.substring(3);
						belname = belname.substring(0,1).toLowerCase()+belname.substring(1);
						
						replaceNativeGetter(iclname, mn, belname);
					}
				}
			}
		}
		
		if(planclass)
		{
			if(isInnerClass(cn, iclname))
			{
				for(MethodNode mn: mths)
				{
					if(mn.name.equals("<init>"))
					{
						int line = nodehelper.getLineNumberOfMethod(mn);
						if(line != -1) 
						{
							MethodNode lineNumberMethod = nodehelper.createReturnConstantMethod("__getLineNumber", line);
							cn.methods.add(lineNumberMethod);
						}
					}
					break;
				}
			}
		}

	}

	protected abstract void transformArrayStores(MethodNode mn, BDIModel model, String iclname);

	/**
	 * @param cn the class which contains the constructor
	 * @param mn The Constructor method node
	 * @param model
	 * @param tododyn list of dynamic beliefs
	 */
	protected abstract void transformConstructor(ClassNode cn, MethodNode mn, BDIModel model, List<String> tododyn);

	/**
	 * Replace native getter for abstract belief. 
	 * @param iclname
	 * @param nativeSetter
	 * @param belname
	 */
	protected abstract void replaceNativeGetter(String iclname, MethodNode nativeGetter, String belname);

	/**
	 * Replace native setter for abstract belief. 
	 * @param iclname
	 * @param nativeSetter
	 * @param belname
	 */
	protected abstract void replaceNativeSetter(String iclname, MethodNode nativeSetter, String belname);
	
	/**
	 * Enhance setter method with unobserve oldvalue at the beginning and event call at the end
	 * @param iclname
	 * @param setter
	 * @param belname
	 */
	protected abstract void enhanceSetter(String iclname, MethodNode setter, String belname);


	// ----- Helper methods ------
	
	
	/**
	 * Check whether a given ClassNode is an Agent (or Capability) class.
	 * @param classNode
	 * @return true, if the given classNode is an Agent or Capability class.
	 */
	protected boolean isAgentClass(ClassNode classNode)
	{
		boolean result = false;
		List<AnnotationNode> visibleAnnotations = classNode.visibleAnnotations;
		if(visibleAnnotations!=null)
		{
			for(AnnotationNode an: visibleAnnotations)
			{
				if(isAgentOrCapa(an.desc))
				{
					result	= true;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * Check whether a given Annotation marks an Agent or Capability.
	 * @param annotationDescription description of the annotation
	 * @return true, if the given annotationDescription marks an Agent or Capability class.
	 */
	protected boolean isAgentOrCapa(String annotationDescription)
	{
		return (annotationDescription.indexOf("Ljadex/micro/annotation/Agent;")!=-1
	    	|| annotationDescription.indexOf("Ljadex/bdiv3/annotation/Capability;")!=-1);
	}
	
	/**
	 * Check whether a given Annotation marks a goal.
	 * @param annotationDescription description of the annotation
	 * @return true, if the given annotationDescription marks a goal.
	 */
	protected boolean isGoal(String annotationDescription)
	{
		return annotationDescription.indexOf("Ljadex/bdiv3/annotation/Goal;")!=-1;
	}
	
	/**
	 * Check whether a given Annotation marks a plan.
	 * @param annotationDescription description of the annotation
	 * @return true, if the given annotationDescription marks a plan.
	 */
	protected boolean isPlan(String annotationDescription)
	{
		return annotationDescription.indexOf("Ljadex/bdiv3/annotation/Plan;")!=-1;
	}
	
	/**
	 * 
	 * @param classNode
	 * @return
	 */
	protected boolean isPlanClass(ClassNode classNode) 
	{
		boolean result = false;
		List<AnnotationNode> visibleAnnotations = classNode.visibleAnnotations;
		if(visibleAnnotations!=null)
		{
			for(AnnotationNode an: visibleAnnotations)
			{
				if("Ljadex/bdiv3/annotation/Plan;".equals(an.desc))
				{
					result	= true;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param methodNode
	 * @return
	 */
	protected boolean isPlanMethod(MethodNode methodNode) 
	{
		boolean result = false;
		List<AnnotationNode> visibleAnnotations = methodNode.visibleAnnotations;
		if(visibleAnnotations!=null)
		{
			for(AnnotationNode an: visibleAnnotations)
			{
				if("Ljadex/bdiv3/annotation/Plan;".equals(an.desc))
				{
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param parent
	 * @param iclname
	 * @return
	 */
	private boolean isInnerClass(ClassNode parent, final String iclname)
	{
		boolean isinner = false;
		List<InnerClassNode> innerClasses = parent.innerClasses;
		if(innerClasses!=null)
		{
			for(InnerClassNode icn: innerClasses)
			{
				if(icn.name.equals(iclname))
				{
					isinner = true;
					break;
				}
			}
		}
		return isinner;
	}
	
	/**
	 * Returns whether a class is already enhanced.
	 * @param clazz
	 * @return true, if already enhanced, else false.
	 */
	public static boolean isEnhanced(Class<?> clazz)
	{
		boolean isEnhanced = false;
		try {
//			Field field = clazz.getField("__agent");
			Field field = clazz.getField("__globalname");
			isEnhanced = true;
		} catch (NoSuchFieldException ex) {
		}
		return isEnhanced;
	}
	
	/**
	 *  Check if a bdi agent class was enhanced.
	 *  @throws RuntimeException if was not enhanced.
	 */
	public static void checkEnhanced(Class<?> clazz)
	{
		// check if agentclass is bytecode enhanced
		try
		{
			clazz.getField("__agent");
		}
		catch(Exception e)
		{
			if (SReflect.isAndroid()) {
				throw new RuntimeException("BDI agent class was not bytecode enhanced: " + clazz.getName() + ". On Android, this is done during build time by the jadex-android-maven-plugin. Be sure it is included in your pom.xml!");
			} else {
				throw new RuntimeException("BDI agent class was not bytecode enhanced: " + clazz.getName() + " This may happen if the class is accessed directly in application code before loadModel() was called.");
			}
		}
	}

}
