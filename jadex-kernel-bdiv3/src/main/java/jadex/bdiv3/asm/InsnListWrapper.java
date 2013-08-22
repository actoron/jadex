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

	public void remove(IAbstractInsnNode n)
	{
		instructions.remove(((AbstractInsnNodeWrapper)n).insnNode);
	}

	public void insert(IAbstractInsnNode previous, IInsnList newins)
	{
		instructions.insert(((AbstractInsnNodeWrapper)previous).insnNode, ((InsnListWrapper)newins).instructions);
	}

	public void add(IAbstractInsnNode newins)
	{
		instructions.add(((AbstractInsnNodeWrapper)newins).insnNode);
	}

	public int size()
	{
		return instructions.size();
	}

	public IAbstractInsnNode get(int i)
	{
		return AbstractInsnNodeWrapper.wrap(instructions.get(i));
	}

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

	public IAbstractInsnNode getFirst()
	{
		return AbstractInsnNodeWrapper.wrap(instructions.getFirst());
	}

	public IAbstractInsnNode getLast()
	{
		return AbstractInsnNodeWrapper.wrap(instructions.getLast());
	}

	public void insertBefore(IAbstractInsnNode first, IInsnList nl)
	{
		instructions.insertBefore(((AbstractInsnNodeWrapper)first).insnNode, ((InsnListWrapper)nl).instructions);
	}

}
