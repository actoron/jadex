package jadex.bdiv3.asm.instructions;

import org.kohsuke.asm4.tree.FieldInsnNode;

public class FieldInsnNodeWrapper extends AbstractInsnNodeWrapper implements IFieldInsnNode
{

	private FieldInsnNode fieldInsnNode;

	public FieldInsnNodeWrapper(FieldInsnNode insnNode)
	{
		super(insnNode);
		this.fieldInsnNode = insnNode;
	}

	@Override
	public String getName()
	{
		return fieldInsnNode.name;
	}
}
