package jadex.bdiv3.asmdex;

import jadex.bdiv3.asm.instructions.IMethodInsnNode;

import org.ow2.asmdex.tree.MethodInsnNode;

public class MethodInsnNodeWrapper extends AbstractInsnNodeWrapper implements IMethodInsnNode
{

	private MethodInsnNode methodInsnNode;

	public MethodInsnNodeWrapper(MethodInsnNode insnNode)
	{
		super(insnNode);
		this.methodInsnNode = insnNode;
	}
	
	@Override
	public String getName()
	{
		return methodInsnNode.name;
	}

	@Override
	public int[] getArguments()
	{
		return methodInsnNode.arguments;
	}

	@Override
	public void setArguments(int[] newArguments)
	{
		methodInsnNode.arguments = newArguments;
	}



}
