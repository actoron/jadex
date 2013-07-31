package jadex.bdiv3.asmdex;

import jadex.bdiv3.asm.IInsnList;
import jadex.bdiv3.asm.IMethodNode;

import java.util.ArrayList;
import java.util.List;

import org.ow2.asmdex.tree.MethodNode;

public class MethodNodeWrapper implements IMethodNode
{

	public MethodNode methodNode;

	public MethodNodeWrapper(MethodNode mn)
	{
		this.methodNode = mn;
	}

	public static List<IMethodNode> wrapList(List<MethodNode> methods)
	{
		List<IMethodNode> result = null;
		if (methods != null)
		{
			result = new ArrayList<IMethodNode>(methods.size());
			for (MethodNode mn : methods)
			{
				result.add(MethodNodeWrapper.wrap(mn));
			}
		}
		return result;
	}

	private static IMethodNode wrap(MethodNode mn)
	{
		return new MethodNodeWrapper(mn);
	}

	@Override
	public IInsnList getInstructions()
	{
		return InsnListWrapper.wrap(methodNode.instructions);
	}

	@Override
	public String getName()
	{
		return methodNode.name;
	}

}
