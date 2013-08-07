package jadex.bdiv3.asmdex;

import jadex.bdiv3.NodeHelper;
import jadex.bdiv3.asm.IFieldNode;
import jadex.bdiv3.asm.IMethodNode;

import org.objectweb.asm.Type;
import org.ow2.asmdex.Opcodes;
import org.ow2.asmdex.tree.FieldNode;
import org.ow2.asmdex.tree.MethodNode;

public class AsmDexNodeHelper extends NodeHelper
{

	@Override
	public IMethodNode createReturnConstantMethod(String methodName, int value)
	{
		int v0 = 0;
		MethodNode mn = new MethodNode(Opcodes.ACC_PUBLIC+Opcodes.ACC_STATIC, methodName, Type.getMethodDescriptor(Type.INT_TYPE), null, null);
		mn.visitMaxs(1, 0);
		mn.visitVarInsn(Opcodes.INSN_CONST, v0, value);
		mn.visitIntInsn(Opcodes.INSN_RETURN, v0);
		return MethodNodeWrapper.wrap(mn);
	}

	@Override
	public IFieldNode createField(int access, String name, String desc, String[] signature, Object initialValue)
	{
		FieldNode fieldNode = new FieldNode(access, name, desc, signature, initialValue);
		return FieldNodeWrapper.wrap(fieldNode);
	}

}
