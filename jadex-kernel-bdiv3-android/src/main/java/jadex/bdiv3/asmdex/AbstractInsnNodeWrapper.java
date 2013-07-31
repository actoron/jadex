package jadex.bdiv3.asmdex;

import jadex.bdiv3.asm.IAbstractInsnNode;

import org.ow2.asmdex.tree.AbstractInsnNode;

public class AbstractInsnNodeWrapper implements IAbstractInsnNode
{

	public AbstractInsnNode insnNode;

	public AbstractInsnNodeWrapper(AbstractInsnNode insnNode)
	{
		this.insnNode = insnNode;
	}
	
	@Override
	public int getOpcode()
	{
		return insnNode.getOpcode();
	}

	@Override
	public IAbstractInsnNode getPrevious()
	{
		return AbstractInsnNodeWrapper.wrap(insnNode.getPrevious());
	}
	
	@Override
	public IAbstractInsnNode getNext()
	{
		return AbstractInsnNodeWrapper.wrap(insnNode.getNext());
	}

	public static IAbstractInsnNode wrap(AbstractInsnNode insnNode)
	{
		return new AbstractInsnNodeWrapper(insnNode);
	}

	public static IAbstractInsnNode[] wrapArray(AbstractInsnNode[] array)
	{
		IAbstractInsnNode[] result = null;
		if (array != null) {
			result = new IAbstractInsnNode[array.length];
			for (int i = 0; i < array.length; i++)
			{
				result[i] = wrap(array[i]);
			}
		}
		return result;
	}

}
