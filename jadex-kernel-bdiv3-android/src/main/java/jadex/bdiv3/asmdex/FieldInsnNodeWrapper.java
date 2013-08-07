package jadex.bdiv3.asmdex;

import jadex.bdiv3.asm.instructions.IFieldInsnNode;

import org.ow2.asmdex.tree.FieldInsnNode;


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
