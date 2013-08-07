package jadex.bdiv3.asm.instructions;

import org.kohsuke.asm4.tree.LineNumberNode;

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
