package jadex.bdiv3.asm;


public interface IInsnList
{

	IAbstractInsnNode[] toArray();

	void remove(IAbstractInsnNode n);

	void insert(IAbstractInsnNode previous, IInsnList newins);

	void add(IAbstractInsnNode methodInsnNode);

	int size();

	IAbstractInsnNode get(int i);

}
