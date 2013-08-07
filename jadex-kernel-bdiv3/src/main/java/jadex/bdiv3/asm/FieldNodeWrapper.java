package jadex.bdiv3.asm;

import org.kohsuke.asm4.tree.FieldNode;

public class FieldNodeWrapper implements IFieldNode
{

	public FieldNode fieldNode;

	public FieldNodeWrapper(FieldNode fieldNode)
	{
		this.fieldNode = fieldNode;
	}

	public static IFieldNode wrap(FieldNode fieldNode)
	{
		return new FieldNodeWrapper(fieldNode);
	}

}
