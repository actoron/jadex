package jadex.bdiv3;

import jadex.bdiv3.asm.IAnnotationNode;
import jadex.bdiv3.asm.IClassNode;
import jadex.bdiv3.model.BDIModel;

import java.util.List;

public abstract class AbstractAsmBdiClassGenerator implements IBDIClassGenerator
{

	@Override
	public abstract Class<?> generateBDIClass(String clname, BDIModel micromodel, ClassLoader cl);
	
	protected abstract boolean isInstancePutField(int opcode);
	
	protected abstract boolean isInstanceGetField(int opcode);
	
	protected abstract boolean isReturn(int opcode);
	
//	protected abstract void transformClassNode(IClassNode cn, final String clname, final BDIModel model);
	
//	protected abstract void transformArrayStores(IMethodNode[] mths, BDIModel model, String iclname);
	
	protected boolean isAgentClass(IClassNode classNode) {
		boolean result = false;
		List<IAnnotationNode> visibleAnnotations = classNode.getVisibleAnnotations();
		if(visibleAnnotations!=null)
		{
			for(IAnnotationNode an: visibleAnnotations)
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

}
