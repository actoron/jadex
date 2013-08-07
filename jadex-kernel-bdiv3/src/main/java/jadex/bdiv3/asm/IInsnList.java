package jadex.bdiv3.asm;

import jadex.bdiv3.asm.instructions.IAbstractInsnNode;




public interface IInsnList extends Iterable<IAbstractInsnNode>
{

//	IAbstractInsnNode[] toArray();
	int size();

	void remove(IAbstractInsnNode n);

	void insert(IAbstractInsnNode previous, IInsnList newins);

	void add(IAbstractInsnNode methodInsnNode);


	IAbstractInsnNode get(int i);

	IAbstractInsnNode getFirst();

	IAbstractInsnNode getLast();

	void insertBefore(IAbstractInsnNode first, IInsnList nl);

}
