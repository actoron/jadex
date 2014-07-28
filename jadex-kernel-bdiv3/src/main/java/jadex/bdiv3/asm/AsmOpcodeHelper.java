package jadex.bdiv3.asm;

import jadex.bdiv3.OpcodeHelper;

import org.objectweb.asm.Opcodes;

public class AsmOpcodeHelper extends OpcodeHelper
{

	@Override
	public boolean isPutField(int opcode)
	{
		return Opcodes.PUTFIELD == opcode;
	}

	@Override
	public boolean isGetField(int opcode)
	{
		return Opcodes.GETFIELD == opcode;
	}

	@Override
	public boolean isReturn(int opcode)
	{
		return Opcodes.RETURN == opcode;
	}

	@Override
	public boolean isNative(int access)
	{
		return ((access&Opcodes.ACC_NATIVE)!=0);
	}

	@Override
	public boolean isLoadConstant(int opcode)
	{
		return Opcodes.LDC == opcode;
	}
	
	
}