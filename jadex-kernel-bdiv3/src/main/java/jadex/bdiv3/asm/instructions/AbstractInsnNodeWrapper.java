package jadex.bdiv3.asm.instructions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.kohsuke.asm4.tree.AbstractInsnNode;
import org.kohsuke.asm4.tree.FieldInsnNode;
import org.kohsuke.asm4.tree.LabelNode;
import org.kohsuke.asm4.tree.LdcInsnNode;
import org.kohsuke.asm4.tree.LineNumberNode;
import org.kohsuke.asm4.tree.MethodInsnNode;


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
		AbstractInsnNode previous = insnNode.getPrevious();
		return previous == null ? null : AbstractInsnNodeWrapper.wrap(previous);
	}
	
	@Override
	public IAbstractInsnNode getNext()
	{
		return AbstractInsnNodeWrapper.wrap(insnNode.getNext());
	}

	public static IAbstractInsnNode wrap(AbstractInsnNode insnNode)
	{
		int type = insnNode.getType();
		IAbstractInsnNode result = null;
		
		switch (type)
		{
			case AbstractInsnNode.FIELD_INSN:
				result = new FieldInsnNodeWrapper((FieldInsnNode)insnNode);
				break;
			case AbstractInsnNode.LABEL:
				result = new LabelNodeWrapper((LabelNode) insnNode);
				break;
			case AbstractInsnNode.METHOD_INSN:
				result = new MethodInsnNodeWrapper((MethodInsnNode) insnNode);
				break;
			case AbstractInsnNode.LDC_INSN:
				result = new LdcInsnNodeWrapper((LdcInsnNode) insnNode);
				break;
			case AbstractInsnNode.LINE:
				result = new LineNumberNodeWrapper((LineNumberNode) insnNode);
				break;
			default :
				result = new AbstractInsnNodeWrapper(insnNode);
				break;
		}
		
		return result;
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

	@Override
	public IAbstractInsnNode clone(Map<ILabelNode, ILabelNode> labels)
	{
		Map<LabelNode, LabelNode> nativeMap = new HashMap<LabelNode, LabelNode>();
		Set<Entry<ILabelNode,ILabelNode>> entrySet = labels.entrySet();
		for (Entry<ILabelNode, ILabelNode> entry : entrySet)
		{
			LabelNode key = ((LabelNodeWrapper) entry.getKey()).labelNode;
			LabelNode value = ((LabelNodeWrapper) entry.getValue()).labelNode;
			nativeMap.put(key, value);
		}
		
		AbstractInsnNode clone = insnNode.clone(nativeMap);
		return AbstractInsnNodeWrapper.wrap(clone);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof AbstractInsnNodeWrapper) {
			return insnNode.equals(((AbstractInsnNodeWrapper) obj).insnNode);
		} else {
			return insnNode.equals(obj);
		}
	}

	@Override
	public int hashCode()
	{
		return insnNode.hashCode();
	}
}
