package jadex.bdiv3;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

import jadex.bdiv3.asm.AsmNodeHelper;
import jadex.commons.SReflect;

/**
 * 
 */
public abstract class NodeHelper
{
	private static NodeHelper INSTANCE;

	public static NodeHelper getInstance()
	{
		if(INSTANCE == null) 
		{
			if(SReflect.isAndroid()) 
			{
				throw new Error("OpcodeHelper.getInstance() is not implemented for Android.");
			} 
			else 
			{
				INSTANCE = new AsmNodeHelper();
			}
		}
		return INSTANCE;
	}
	
	/**
	 * Returns the line number of the first LineNumberNode in the instructions
	 * of the given MethodNode, or -1, if none found.
	 * @param mn The MethodNode to look into
	 * @return the line number or -1, if none found
	 */
	public int getLineNumberOfMethod(MethodNode mn)
	{
		int ln = -1;
		InsnList l = mn.instructions;
		for(int i=0; i<l.size(); i++)
		{
			AbstractInsnNode n = l.get(i);
			if(n instanceof LineNumberNode)
			{
				ln = ((LineNumberNode)n).line;
				break;
			}
		}
		return ln;
	}

	/**
	 * Creates a Method that always returns the given value.
	 * @param methodName
	 * @param value
	 * @return the new method
	 */
	public abstract MethodNode createReturnConstantMethod(String methodName, int value);

	public abstract FieldNode createField(int access, String name, String desc, String[] signature, Object initialValue);
}
