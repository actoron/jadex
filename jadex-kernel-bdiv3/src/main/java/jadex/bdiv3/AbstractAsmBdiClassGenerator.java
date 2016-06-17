package jadex.bdiv3;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MCondition;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bridge.ClassInfo;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.MultiCollection;
import jadex.javaparser.javaccimpl.ParameterNode;
import jadex.rules.eca.EventType;


/**
 * ASM Generator base class
 */
public abstract class AbstractAsmBdiClassGenerator implements IBDIClassGenerator
{
	protected OpcodeHelper ophelper = OpcodeHelper.getInstance();
	
	protected NodeHelper nodehelper = NodeHelper.getInstance();
	
	public abstract List<Class<?>> generateBDIClass(String clname, BDIModel micromodel, ClassLoader cl);

	/**
	 *  Store which beliefs are accessed in a method.
	 */
	public static class MethodBeliefs
	{
		protected MethodNode methodNode;
		protected Set<String> beliefs;

		/**
		 *  Create a new method beliefs.
		 * @param methodNode
		 * @param beliefs
		 */
		public MethodBeliefs(MethodNode methodNode, Set<String> beliefs)
		{
			this.methodNode = methodNode;
			this.beliefs = beliefs;
		}

		/**
		 *  Get the methodNode.
		 *  @return The methodNode
		 */
		public MethodNode getMethodNode()
		{
			return methodNode;
		}
		
		/**
		 *  Set the methodNode.
		 *  @param methodNode The methodNode to set
		 */
		public void setMethodNode(MethodNode methodNode)
		{
			this.methodNode = methodNode;
		}
		
		/**
		 *  Get the beliefs.
		 *  @return The beliefs
		 */
		public Set<String> getBeliefs()
		{
			return beliefs;
		}
		
		/**
		 *  Set the beliefs.
		 *  @param beliefs The beliefs to set
		 */
		public void setBeliefs(Set<String> beliefs)
		{
			this.beliefs = beliefs;
		}
	}
	
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

		MultiCollection<String, MethodBeliefs> methodbeliefs = new MultiCollection<String, MethodBeliefs>();
		for(MethodNode mn : mths)
		{
			transformArrayStores(mn, model, iclname);
			Set<String> bels = findBeliefs(cn, mn, model);
			if(bels.size()>0)
				methodbeliefs.add(mn.name, new MethodBeliefs(mn, bels));
		}
		
		List<MGoal> mgoals = model.getCapability().getGoals();
		for(MGoal mgoal : mgoals)
		{
			for(Map.Entry<String, List<MCondition>> entry: mgoal.getConditions().entrySet())
			{
				List<MCondition> conds = entry.getValue();
				for(MCondition cond: conds)
				{
					// nothing declared?
					if(cond.getEvents().size()==0)
					{
						MethodInfo mi = cond.getMethodTarget();
						Collection<MethodBeliefs> mbs = methodbeliefs.get(mi.getName());
						
						if(mbs!=null)
						{
							MethodBeliefs mb = null;
							if(mbs.size()>1)
							{
								ClassInfo[] ptypes = cond.getMethodTarget().getParameterTypeInfos();
								for(MethodBeliefs tmp: mbs)
								{
									List<ParameterNode> ps = tmp.getMethodNode().parameters;
									int psize = ps==null? 0: ps.size();
									if(psize==ptypes.length)
									{
										System.out.println("hhh");
									}
								}
							}
							else if(mbs.size()==1)
							{
								mb =  mbs.iterator().next();
							}
	
							if(mb!=null)
							{
								for(String belname: mb.getBeliefs())
								{
									//BDIAgentFeature.addBeliefEvents(model.getCapability(), events, ev, cl);
									cond.addEvent(new EventType(new String[]{ChangeEvent.BELIEFCHANGED, belname})); // the whole value was changed
									cond.addEvent(new EventType(new String[]{ChangeEvent.FACTCHANGED, belname})); // property change of a value
								
									// todo: how to check if multi belief
									cond.addEvent(new EventType(new String[]{ChangeEvent.FACTADDED, belname}));
									cond.addEvent(new EventType(new String[]{ChangeEvent.FACTREMOVED, belname}));
								}
							}
							System.out.println("Added belief dependency of condition: "+entry.getKey()+" "+cond.getName()+" "+cond.getEvents());
						}
					}
				}
			}
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
						nl.add(new FieldInsnNode(Opcodes.GETFIELD, iclname, IBDIClassGenerator.AGENT_FIELD_NAME, "Ljadex/bridge/IInternalAccess;"));
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

	/**
	 *  Find the beliefs used in a method.
	 */
	protected abstract Set<String> findBeliefs(ClassNode cn, MethodNode mn, BDIModel model);

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
//			Field field = clazz.getField(AGENT_FIELD_NAME);
			Field field = clazz.getField(GLOBALNAME_FIELD_NAME);
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
			clazz.getField(AGENT_FIELD_NAME);
		}
		catch(Exception e)
		{
			if (SReflect.isAndroid()) {
				throw new RuntimeException("BDI agent class was not bytecode enhanced: " + clazz.getName() + ". On Android, this is done during build time by the jadex-gradle plugin. Be sure it is included in your build.gradle as explained in the jadex-android documentation!");
			} else {
				throw new RuntimeException("BDI agent class was not bytecode enhanced: " + clazz.getName() + " This may happen if the class is accessed directly in application code before loadModel() was called.");
			}
		}
	}

}
