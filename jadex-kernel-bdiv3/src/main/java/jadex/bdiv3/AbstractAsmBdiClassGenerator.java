package jadex.bdiv3;

import jadex.bdiv3.asm.IAnnotationNode;
import jadex.bdiv3.asm.IClassNode;
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
	public abstract Class<?> generateBDIClass(String clname, BDIModel micromodel, ClassLoader cl);

	protected void transformClassNode(IClassNode cn, final String iclname, final BDIModel model)
	{
		// Some transformations are only applied to the agent class and not its
		// inner classes.
		boolean agentclass = isAgentClass(cn);
		boolean	planclass	= isPlanClass(cn);
		// Check method for array store access of beliefs and replace with
		// static method call
		List<IMethodNode> mths = cn.getMethods();

		for (IMethodNode mn : mths)
		{
			transformArrayStores(mn, model, iclname);
		}

		if (agentclass)
		{
			// Check if there are dynamic beliefs
			// and enhance getter/setter beliefs by adding event call to setter
			List<String> tododyn = new ArrayList<String>();
			List<String> todoset = new ArrayList<String>();
			List<String> todoget = new ArrayList<String>();
			List<MBelief> mbels = model.getCapability().getBeliefs();
			for (MBelief mbel : mbels)
			{
				Collection<String> evs = mbel.getEvents();
				if (evs != null && !evs.isEmpty() || mbel.isDynamic())
				{
					tododyn.add(mbel.getName());
				}

				if (!mbel.isFieldBelief())
				{
					todoset.add(mbel.getSetter().getName());
				}

				if (!mbel.isFieldBelief())
				{
					todoget.add(mbel.getGetter().getName());
				}
			}

			addInitArgsField(cn);

			for (IMethodNode mn : mths)
			{
				if(isPlanMethod(mn))
				{
					transformPlanMethod(cn, mn);
				}
				
				// search constructor (should not have multiple ones)
				// and extract field assignments for dynamic beliefs
				// will be incarnated as new update methods
				if (mn.getName().equals("<init>"))
				{
					transformConstructor(cn, mn, model, tododyn);
				}
				else if (todoset.contains(mn.getName()))
				{
					transformSetter(iclname, mn);
				}
				// Enhance native getter method
				else if (todoget.contains(mn.getName()))
				{
					transformGetter(iclname, mn);
				}
			}
		}
		
		if(planclass)
		{
			boolean isinner = false;
			List<IInnerClassNode> innerClasses = cn.getInnerClasses();
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
			
			if(isinner)
			{
				for(IMethodNode mn: mths)
				{
					if(mn.getName().equals("<init>"))
					{
						transformInnerPlanConstructor(cn, mn);
					}
					break;
				}
			}
		}

	}

	protected abstract void transformPlanMethod(IClassNode cn, IMethodNode mn);

	protected abstract void transformInnerPlanConstructor(IClassNode cn, IMethodNode mn);

	protected abstract void addInitArgsField(IClassNode cn);

	protected abstract void transformArrayStores(IMethodNode mn, BDIModel model, String iclname);

	/**
	 * @param cn the class which contains the constructor
	 * @param mn The Constructor method node
	 * @param model
	 * @param tododyn list of dynamic beliefs
	 */
	protected abstract void transformConstructor(IClassNode cn, IMethodNode mn, BDIModel model, List<String> tododyn);

	protected abstract void transformGetter(String iclname, IMethodNode mn);

	protected abstract void transformSetter(String iclname, IMethodNode mn);


	// ----- Helper methods ------
	
	protected abstract boolean isInstancePutField(int opcode);
	
	protected abstract boolean isInstanceGetField(int opcode);
	
	protected abstract boolean isReturn(int opcode);
	
	protected boolean isAgentClass(IClassNode classNode)
	{
		boolean result = false;
		List<IAnnotationNode> visibleAnnotations = classNode.getVisibleAnnotations();
		if(visibleAnnotations!=null)
		{
			for (IAnnotationNode an: visibleAnnotations)
			{
				if("Ljadex/micro/annotation/Agent;".equals(an.getDescription())
						|| "Ljadex/bdiv3/annotation/Capability;".equals(an.getDescription()))
				{
					result	= true;
					break;
				}
			}
		}
		return result;
	}
	
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

}
