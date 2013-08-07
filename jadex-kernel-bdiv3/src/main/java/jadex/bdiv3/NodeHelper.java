package jadex.bdiv3;

import jadex.bdiv3.asm.AsmNodeHelper;
import jadex.bdiv3.asm.IFieldNode;
import jadex.bdiv3.asm.IInsnList;
import jadex.bdiv3.asm.IMethodNode;
import jadex.bdiv3.asm.instructions.IAbstractInsnNode;
import jadex.bdiv3.asm.instructions.ILineNumberNode;
import jadex.commons.SReflect;

public abstract class NodeHelper
{


	private static NodeHelper INSTANCE;

	public static NodeHelper getInstance()
	{
		if (INSTANCE == null) {
			if (SReflect.isAndroid()) {
				Class<?> clazz = SReflect.classForName0("jadex.bdiv3.asmdex.AsmDexNodeHelper", null);
				try
				{
					INSTANCE = (NodeHelper) clazz.newInstance();
				}
				catch (InstantiationException e)
				{
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			} else {
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
	public int getLineNumberOfMethod(IMethodNode mn)
	{
		int ln = -1;
		IInsnList l = mn.getInstructions();
		for(int i=0; i<l.size(); i++)
		{
			IAbstractInsnNode n = l.get(i);
			if(n instanceof ILineNumberNode)
			{
				ln = ((ILineNumberNode)n).getLine();
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
	public abstract IMethodNode createReturnConstantMethod(String methodName, int value);

	public abstract IFieldNode createField(int access, String name, String desc, String[] signature, Object initialValue);

	
}
