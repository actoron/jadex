package jadex.bdiv3.asmdex;

import jadex.bdiv3.asm.IFieldNode;

import org.ow2.asmdex.tree.FieldNode;


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
