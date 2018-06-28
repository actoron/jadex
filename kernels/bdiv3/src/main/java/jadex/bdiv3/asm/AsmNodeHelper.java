package jadex.bdiv3.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import jadex.bdiv3.NodeHelper;

/**
 *  Helper for creating nodes.
 */
public class AsmNodeHelper extends NodeHelper
{
	/**
	 *  Create const method node helper.
	 */
	public MethodNode createReturnConstantMethod(String name, int value)
	{
		MethodNode mnode = new MethodNode(Opcodes.ACC_PUBLIC+Opcodes.ACC_STATIC, 
			name, Type.getMethodDescriptor(Type.INT_TYPE), null, null);
				
		mnode.visitIntInsn(Opcodes.SIPUSH, value);
		mnode.visitInsn(Opcodes.IRETURN);
			
		return mnode;
	}
	
	/**
	 *  Create field node helper.
	 */
	public FieldNode createField(int access, String name, String desc, String[] signature, Object initialValue)
	{
		StringBuilder sig = new StringBuilder();
		for(String string : signature)
		{
			sig.append(string);
		}
		
		FieldNode fieldNode = new FieldNode(access, name, desc, sig.toString(), initialValue);
		return fieldNode;
	}
}
