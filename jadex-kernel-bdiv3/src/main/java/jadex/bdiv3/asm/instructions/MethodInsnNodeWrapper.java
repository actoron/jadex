package jadex.bdiv3.asm.instructions;


import org.kohsuke.asm4.tree.MethodInsnNode;

import jadex.bdiv3.asm.instructions.IMethodInsnNode;

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
		throw new NoSuchMethodError("getArguments() not available in ASM.");
	}

	@Override
	public void setArguments(int[] newArguments)
	{
		throw new NoSuchMethodError("setArguments() not available in ASM.");
	}
}
