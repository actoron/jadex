package jadex.bdiv3.asm.instructions;

import org.kohsuke.asm4.tree.LdcInsnNode;

public class LdcInsnNodeWrapper extends AbstractInsnNodeWrapper implements ILdcInsnNode
{

	private LdcInsnNode ldcNode;

	public LdcInsnNodeWrapper(LdcInsnNode insnNode)
	{
		super(insnNode);
		this.ldcNode = insnNode;
	}

	@Override
	public Object getCst()
	{
		return ldcNode.cst;
	}
}
