package jadex.bdiv3.android;

import java.util.List;

import org.ow2.asmdex.AnnotationVisitor;
import org.ow2.asmdex.MethodVisitor;
import org.ow2.asmdex.Opcodes;
import org.ow2.asmdex.structureCommon.Label;

public class MyMethodVisitor extends MethodVisitor
{

	final private MethodInsManager ruleManager;
	private String desc;
	private String name;


	public MyMethodVisitor(int api, MethodInsManager ruleManager, MethodVisitor mv, String name, String desc)
	{
		super(api, mv);
		this.ruleManager = ruleManager;
		this.name = name;
		this.desc = desc;
	}



	@Override
	public void visitMethodInsn(int opcode, java.lang.String owner, java.lang.String name, java.lang.String desc, int[] arguments)
	{
		boolean isStatic;
		String signature;
//		switch (opcode)
//		{
//			case Opcodes.INSN_INVOKE_STATIC :
//			case Opcodes.INSN_INVOKE_STATIC_RANGE :
//				isStatic = true;
//				break;
//			default :
//				isStatic = false;
//		}
//		String logItName = ruleManager.log(owner, name, desc, isStatic);
//		if (logItName != null)
//		{
//			if (isStatic)
//				signature = "V" + MethodSignature.popType(desc);
//			else
//				signature = "V" + owner + MethodSignature.popType(desc);
//			int opcodeStatic = (opcode < 0x74) ? Opcodes.INSN_INVOKE_STATIC : Opcodes.INSN_INVOKE_STATIC_RANGE;
//			mv.visitMethodInsn(opcodeStatic, LogClassWriter.LOG_CLASSNAME, logItName, signature, arguments);
//		}
		
		mv.visitMethodInsn(opcode, owner, name, desc, arguments);
	}
	
	
	@Override
	public void visitCode()
	{
		super.visitCode();
	}

	@Override
	public void visitEnd()
	{
		super.visitEnd();
	}

	@Override
	public void visitStringInsn(int opcode, int destinationRegister, String string)
	{
		if ("existingMethod".equals(name)) {
			super.visitStringInsn(opcode, destinationRegister, "otherString");
		} else {
			super.visitStringInsn(opcode, destinationRegister, string);
		}
	}

	@Override
	public void visitInsn(int opcode)
	{
		super.visitInsn(opcode);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible)
	{
		// TODO Auto-generated method stub
		return super.visitAnnotation(desc, visible);
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault()
	{
		// TODO Auto-generated method stub
		return super.visitAnnotationDefault();
	}

	@Override
	public void visitArrayLengthInsn(int destinationRegister, int arrayReferenceBearing)
	{
		// TODO Auto-generated method stub
		super.visitArrayLengthInsn(destinationRegister, arrayReferenceBearing);
	}

	@Override
	public void visitArrayOperationInsn(int opcode, int valueRegister, int arrayRegister, int indexRegister)
	{
		// TODO Auto-generated method stub
		super.visitArrayOperationInsn(opcode, valueRegister, arrayRegister, indexRegister);
	}

	@Override
	public void visitAttribute(Object attr)
	{
		// TODO Auto-generated method stub
		super.visitAttribute(attr);
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc, int valueRegister, int objectRegister)
	{
		// TODO Auto-generated method stub
		super.visitFieldInsn(opcode, owner, name, desc, valueRegister, objectRegister);
	}

	@Override
	public void visitFillArrayDataInsn(int arrayReference, Object[] arrayData)
	{
		// TODO Auto-generated method stub
		super.visitFillArrayDataInsn(arrayReference, arrayData);
	}

	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack)
	{
		// TODO Auto-generated method stub
		super.visitFrame(type, nLocal, local, nStack, stack);
	}

	@Override
	public void visitIntInsn(int opcode, int register)
	{
		// TODO Auto-generated method stub
		super.visitIntInsn(opcode, register);
	}

	@Override
	public void visitJumpInsn(int opcode, Label label, int registerA, int registerB)
	{
		// TODO Auto-generated method stub
		super.visitJumpInsn(opcode, label, registerA, registerB);
	}

	@Override
	public void visitLabel(Label label)
	{
		// TODO Auto-generated method stub
		super.visitLabel(label);
	}

	@Override
	public void visitLineNumber(int line, Label start)
	{
		// TODO Auto-generated method stub
		super.visitLineNumber(line, start);
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
	{
		// TODO Auto-generated method stub
		super.visitLocalVariable(name, desc, signature, start, end, index);
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, List<Label> ends, List<Label> restarts,
			int index)
	{
		// TODO Auto-generated method stub
		super.visitLocalVariable(name, desc, signature, start, ends, restarts, index);
	}

	@Override
	public void visitLookupSwitchInsn(int register, Label dflt, int[] keys, Label[] labels)
	{
		// TODO Auto-generated method stub
		super.visitLookupSwitchInsn(register, dflt, keys, labels);
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals)
	{
		// TODO Auto-generated method stub
		super.visitMaxs(maxStack, maxLocals);
	}

	@Override
	public void visitMultiANewArrayInsn(String desc, int[] registers)
	{
		// TODO Auto-generated method stub
		super.visitMultiANewArrayInsn(desc, registers);
	}

	@Override
	public void visitOperationInsn(int opcode, int destinationRegister, int firstSourceRegister, int secondSourceRegister, int value)
	{
		// TODO Auto-generated method stub
		super.visitOperationInsn(opcode, destinationRegister, firstSourceRegister, secondSourceRegister, value);
	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible)
	{
		// TODO Auto-generated method stub
		return super.visitParameterAnnotation(parameter, desc, visible);
	}

	@Override
	public void visitParameters(String[] parameters)
	{
		// TODO Auto-generated method stub
		super.visitParameters(parameters);
	}

	@Override
	public void visitTableSwitchInsn(int register, int min, int max, Label dflt, Label[] labels)
	{
		// TODO Auto-generated method stub
		super.visitTableSwitchInsn(register, min, max, dflt, labels);
	}

	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type)
	{
		// TODO Auto-generated method stub
		super.visitTryCatchBlock(start, end, handler, type);
	}

	@Override
	public void visitTypeInsn(int opcode, int destinationRegister, int referenceBearingRegister, int sizeRegister, String type)
	{
		// TODO Auto-generated method stub
		super.visitTypeInsn(opcode, destinationRegister, referenceBearingRegister, sizeRegister, type);
	}

	@Override
	public void visitVarInsn(int opcode, int destinationRegister, int var)
	{
		// TODO Auto-generated method stub
		super.visitVarInsn(opcode, destinationRegister, var);
	}

	@Override
	public void visitVarInsn(int opcode, int destinationRegister, long var)
	{
		// TODO Auto-generated method stub
		super.visitVarInsn(opcode, destinationRegister, var);
	}
	
	

}