package jadex.bdiv3.asmdex;

import jadex.bdiv3.asm.instructions.ILineNumberNode;

import org.ow2.asmdex.tree.LineNumberNode;


public class LineNumberNodeWrapper extends AbstractInsnNodeWrapper implements ILineNumberNode
{

	private LineNumberNode lineNumberNode;

	public LineNumberNodeWrapper(LineNumberNode insnNode)
	{
		super(insnNode);
		this.lineNumberNode = insnNode;
	}

	@Override
	public int getLine()
	{
		return lineNumberNode.line;
	}


}
