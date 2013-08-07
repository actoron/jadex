package jadex.bdiv3.asm.instructions;

import org.kohsuke.asm4.tree.LabelNode;

public class LabelNodeWrapper extends AbstractInsnNodeWrapper implements ILabelNode
{

	LabelNode labelNode;

	public LabelNodeWrapper(LabelNode insnNode)
	{
		super(insnNode);
		this.labelNode = insnNode;
	}


}
