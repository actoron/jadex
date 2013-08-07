package jadex.bdiv3.asm.instructions;

import java.util.Map;


public interface IAbstractInsnNode
{

	int getOpcode();

	IAbstractInsnNode getPrevious();

	IAbstractInsnNode getNext();

	IAbstractInsnNode clone(Map<ILabelNode, ILabelNode> labels);

}
