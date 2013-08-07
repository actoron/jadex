package jadex.bdiv3.asm;

import jadex.bdiv3.asm.instructions.AbstractInsnNodeWrapper;
import jadex.bdiv3.asm.instructions.IAbstractInsnNode;

import java.util.Iterator;
import java.util.ListIterator;

import org.kohsuke.asm4.tree.AbstractInsnNode;
import org.kohsuke.asm4.tree.InsnList;

public class InsnListWrapper implements IInsnList
{

	public InsnList instructions;

	public InsnListWrapper(InsnList instructions)
	{
		this.instructions = instructions;
	}

	public static IInsnList wrap(InsnList instructions)
	{
		return new InsnListWrapper(instructions);
	}

//	@Override
//	public IAbstractInsnNode[] toArray()
//	{
//		return AbstractInsnNodeWrapper.wrapArray(instructions.toArray());
//	}

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

	@Override
	public Iterator<IAbstractInsnNode> iterator()
	{
		return new Iterator<IAbstractInsnNode>()
		{
			private ListIterator<AbstractInsnNode> it = instructions.iterator();
			@Override
			public boolean hasNext()
			{
				return it.hasNext();
			}

			@Override
			public IAbstractInsnNode next()
			{
				return AbstractInsnNodeWrapper.wrap(it.next());
			}

			@Override
			public void remove()
			{
				it.remove();
			}
		};
	}

	@Override
	public IAbstractInsnNode getFirst()
	{
		return AbstractInsnNodeWrapper.wrap(instructions.getFirst());
	}

	@Override
	public IAbstractInsnNode getLast()
	{
		return AbstractInsnNodeWrapper.wrap(instructions.getLast());
	}

	@Override
	public void insertBefore(IAbstractInsnNode first, IInsnList nl)
	{
		instructions.insertBefore(((AbstractInsnNodeWrapper)first).insnNode, ((InsnListWrapper)nl).instructions);
	}

}
