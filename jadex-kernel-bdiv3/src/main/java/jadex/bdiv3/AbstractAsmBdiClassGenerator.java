package jadex.bdiv3;

import jadex.bdiv3.asm.IAnnotationNode;
import jadex.bdiv3.asm.IClassNode;
import jadex.bdiv3.asm.IFieldNode;
import jadex.bdiv3.asm.IInnerClassNode;
import jadex.bdiv3.asm.IMethodNode;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


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
	protected void transformClassNode(IClassNode cn, final String iclname, final BDIModel model)
	{
		// Some transformations are only applied to the agent class and not its
		// inner classes.
		boolean agentclass = isAgentClass(cn);
		boolean	planclass	= isPlanClass(cn);
		// Check method for array store access of beliefs and replace with
		// static method call
		List<IMethodNode> mths = cn.getMethods();

		for(IMethodNode mn : mths)
		{
			transformArrayStores(mn, model, iclname);
		}

		if(agentclass)
		{
			// Check if there are dynamic beliefs
			// and enhance getter/setter beliefs by adding event call to setter
			List<String> tododyn = new ArrayList<String>();
			List<String> todoset = new ArrayList<String>();
			List<String> todoget = new ArrayList<String>();
			List<MBelief> mbels = model.getCapability().getBeliefs();
			for(MBelief mbel : mbels)
			{
				Collection<String> evs = mbel.getEvents();
				if(evs!=null && !evs.isEmpty() || mbel.isDynamic())
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

			IFieldNode initArgsField = nodehelper.createField(OpcodeHelper.ACC_PROTECTED, "__initargs", "Ljava/util/List;", new String[]{"Ljava/util/List<Ljadex/commons/Tuple3<Ljava/lang/Class<*>;[Ljava/lang/Class<*>;[Ljava/lang/Object;>;>;"}, null);
			cn.addField(initArgsField);		
			
			for(IMethodNode mn : mths)
			{
				if(isPlanMethod(mn))
				{
					int line = nodehelper.getLineNumberOfMethod(mn);
					if(line != -1) 
					{
						IMethodNode lineNumberMethod = nodehelper.createReturnConstantMethod("__getLineNumber"+mn.getName(), line);
						cn.addMethod(lineNumberMethod);
					}
				}
				
				// search constructor (should not have multiple ones)
				// and extract field assignments for dynamic beliefs
				// will be incarnated as new update methods
				if(mn.getName().equals("<init>"))
				{
					transformConstructor(cn, mn, model, tododyn);
				}
				else if(todoset.contains(mn.getName()))
				{
					String belname = mn.getName().substring(3); // property name = method name - get/set prefix
					belname = belname.substring(0,1).toLowerCase()+belname.substring(1);
					if(ophelper.isNative(mn.getAccess()))
					{
						replaceNativeSetter(iclname, mn, belname);
					} 
					else 
					{
						enhanceSetter(iclname, mn, belname);
					}
				}
				// Enhance native getter method
				else if(todoget.contains(mn.getName()))
				{
					if(ophelper.isNative(mn.getAccess()))
					{
						String belname = mn.getName().startsWith("is") ? mn.getName().substring(2) : mn.getName().substring(3);
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
				for(IMethodNode mn: mths)
				{
					if(mn.getName().equals("<init>"))
					{
						int line = nodehelper.getLineNumberOfMethod(mn);
						if(line != -1) 
						{
							IMethodNode lineNumberMethod = nodehelper.createReturnConstantMethod("__getLineNumber", line);
							cn.addMethod(lineNumberMethod);
						}
					}
					break;
				}
			}
		}

	}

	protected abstract void transformArrayStores(IMethodNode mn, BDIModel model, String iclname);

	/**
	 * @param cn the class which contains the constructor
	 * @param mn The Constructor method node
	 * @param model
	 * @param tododyn list of dynamic beliefs
	 */
	protected abstract void transformConstructor(IClassNode cn, IMethodNode mn, BDIModel model, List<String> tododyn);

	/**
	 * Replace native getter for abstract belief. 
	 * @param iclname
	 * @param nativeSetter
	 * @param belname
	 */
	protected abstract void replaceNativeGetter(String iclname, IMethodNode nativeGetter, String belname);

	/**
	 * Replace native setter for abstract belief. 
	 * @param iclname
	 * @param nativeSetter
	 * @param belname
	 */
	protected abstract void replaceNativeSetter(String iclname, IMethodNode nativeSetter, String belname);
	
	/**
	 * Enhance setter method with unobserve oldvalue at the beginning and event call at the end
	 * @param iclname
	 * @param setter
	 * @param belname
	 */
	protected abstract void enhanceSetter(String iclname, IMethodNode setter, String belname);


	// ----- Helper methods ------
	
	/**
	 * Check whether a given ClassNode is an Agent (or Capability) class.
	 * @param classNode
	 * @return true, if the given classNode is an Agent or Capability class.
	 */
	protected boolean isAgentClass(IClassNode classNode)
	{
		boolean result = false;
		List<IAnnotationNode> visibleAnnotations = classNode.getVisibleAnnotations();
		if(visibleAnnotations!=null)
		{
			for (IAnnotationNode an: visibleAnnotations)
			{
				if(isAgentOrCapa(an.getDescription()))
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
	protected boolean isPlanClass(IClassNode classNode) 
	{
		boolean result = false;
		List<IAnnotationNode> visibleAnnotations = classNode.getVisibleAnnotations();
		if(visibleAnnotations!=null)
		{
			for(IAnnotationNode an: visibleAnnotations)
			{
				if("Ljadex/bdiv3/annotation/Plan;".equals(an.getDescription()))
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
	protected boolean isPlanMethod(IMethodNode methodNode) 
	{
		boolean result = false;
		List<IAnnotationNode> visibleAnnotations = methodNode.getVisibleAnnotations();
		if(visibleAnnotations!=null)
		{
			for(IAnnotationNode an: visibleAnnotations)
			{
				if("Ljadex/bdiv3/annotation/Plan;".equals(an.getDescription()))
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
	private boolean isInnerClass(IClassNode parent, final String iclname)
	{
		boolean isinner = false;
		List<IInnerClassNode> innerClasses = parent.getInnerClasses();
		if(innerClasses!=null)
		{
			for(IInnerClassNode icn: innerClasses)
			{
				if(icn.getName().equals(iclname))
				{
					isinner = true;
					break;
				}
			}
		}
		return isinner;
	}

}
