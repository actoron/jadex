package jadex.bdiv3.asm;

import org.kohsuke.asm4.tree.InsnList;

public interface IMethodNode
{

	IInsnList getInstructions();

	String getName();

}
