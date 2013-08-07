package jadex.bdiv3.asmdex;

import jadex.bdiv3.asm.IInnerClassNode;

import java.util.ArrayList;
import java.util.List;

import org.ow2.asmdex.tree.InnerClassNode;

public class InnerClassNodeWrapper implements IInnerClassNode
{

	private InnerClassNode innerClassNode;

	public InnerClassNodeWrapper(InnerClassNode innerClassNode)
	{
		this.innerClassNode = innerClassNode;
	}

	@Override
	public String getName()
	{
		return innerClassNode.name;
	}

	public static List<IInnerClassNode> wrapList(List<InnerClassNode> innerClasses)
	{
		List<IInnerClassNode> result = null;
		if (innerClasses != null) {
			result = new ArrayList<IInnerClassNode>();
			for (InnerClassNode innerClassNode : innerClasses)
			{
				result.add(wrap(innerClassNode));
			}
		}
		return result;
	}

	private static IInnerClassNode wrap(InnerClassNode innerClassNode)
	{
		return new InnerClassNodeWrapper(innerClassNode);
	}

}
