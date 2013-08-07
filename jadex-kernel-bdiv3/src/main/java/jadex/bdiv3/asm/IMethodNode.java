package jadex.bdiv3.asm;

import java.util.List;


public interface IMethodNode
{
	String getName();
	String getDesc();
	
	/**
	 * Will be a String in ASM, but an Array<String> in ASMDEX
	 * @return
	 */
	Object getSignature();

	IInsnList getInstructions();
	void setInstructions(IInsnList nl);
	
	int getAccess();
	void setAccess(int access);
	
	int getMaxStack();
	void setMaxStack(int maxStack);
	
	void visitMaxs(int maxStack, int maxLocals);
	void visitInsn(int opcode);
	
	List<IAnnotationNode> getVisibleAnnotations();

}
