package jadex.bdiv3.asm.instructions;

public interface IMethodInsnNode extends IAbstractInsnNode
{
	int[] getArguments();
	void setArguments(int[] newArguments);
	
	String getName();
}
