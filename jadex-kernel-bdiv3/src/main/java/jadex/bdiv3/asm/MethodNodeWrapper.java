package jadex.bdiv3.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kohsuke.asm4.tree.MethodNode;

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
			result = Collections.unmodifiableList(result);
		}
		return result;
	}

	public static IMethodNode wrap(MethodNode mn)
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

	@Override
	public String getDesc()
	{
		return methodNode.desc;
	}

	@Override
	public String getSignature()
	{
		return methodNode.signature;
	}

	@Override
	public void setInstructions(IInsnList nl)
	{
		methodNode.instructions = ((InsnListWrapper)nl).instructions;
	}

	@Override
	public int getAccess()
	{
		return methodNode.access;
	}

	@Override
	public void setAccess(int access)
	{
		methodNode.access = access;
	}

	@Override
	public int getMaxStack()
	{
		return methodNode.maxStack;
	}

	@Override
	public void setMaxStack(int maxStack)
	{
		methodNode.maxStack = maxStack;
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals)
	{
		methodNode.visitMaxs(maxStack, maxLocals);
	}

	@Override
	public void visitInsn(int opcode)
	{
		methodNode.visitInsn(opcode);
	}

	@Override
	public List<IAnnotationNode> getVisibleAnnotations()
	{
		return AnnotationNodeWrapper.wrapList(methodNode.visibleAnnotations);
	}

}
