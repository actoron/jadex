package jadex.bdiv3.asmdex;

import jadex.bdiv3.asm.instructions.ILabelNode;

import org.ow2.asmdex.tree.LabelNode;

public class LabelNodeWrapper extends AbstractInsnNodeWrapper implements ILabelNode
{

	LabelNode labelNode;

	public LabelNodeWrapper(LabelNode insnNode)
	{
		super(insnNode);
		this.labelNode = insnNode;
	}


}
