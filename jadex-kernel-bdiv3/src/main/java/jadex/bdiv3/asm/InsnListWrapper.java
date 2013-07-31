package jadex.bdiv3.asm;

import org.kohsuke.asm4.tree.InsnList;

public class InsnListWrapper implements IInsnList
{

	private InsnList instructions;

	public InsnListWrapper(InsnList instructions)
	{
		this.instructions = instructions;
	}

	public static IInsnList wrap(InsnList instructions)
	{
		return new InsnListWrapper(instructions);
	}

	@Override
	public IAbstractInsnNode[] toArray()
	{
		return AbstractInsnNodeWrapper.wrapArray(instructions.toArray());
	}

	@Override
	public void remove(IAbstractInsnNode n)
	{
		instructions.remove(((AbstractInsnNodeWrapper)n).insnNode);
	}

	@Override
	public void insert(IAbstractInsnNode previous, IInsnList newins)
	{
		instructions.insert(((AbstractInsnNodeWrapper)previous).insnNode, ((InsnListWrapper)newins).instructions);
	}

	@Override
	public void add(IAbstractInsnNode newins)
	{
		instructions.add(((AbstractInsnNodeWrapper)newins).insnNode);
	}

	@Override
	public int size()
	{
		return instructions.size();
	}

	@Override
	public IAbstractInsnNode get(int i)
	{
		return AbstractInsnNodeWrapper.wrap(instructions.get(i));
	}

}
