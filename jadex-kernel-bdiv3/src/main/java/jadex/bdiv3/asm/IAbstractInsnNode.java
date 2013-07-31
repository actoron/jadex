package jadex.bdiv3.asm;

public interface IAbstractInsnNode
{

	int getOpcode();

	IAbstractInsnNode getPrevious();

	IAbstractInsnNode getNext();

}
