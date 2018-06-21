package jadex.bdiv3.asm;

import org.objectweb.asm.Opcodes;

import jadex.bdiv3.OpcodeHelper;

/**
 *  Helper for creating opcodes.
 */
public class AsmOpcodeHelper extends OpcodeHelper
{
	public boolean isPutField(int opcode)
	{
		return Opcodes.PUTFIELD == opcode;
	}
	
	public boolean isGetField(int opcode)
	{
		return Opcodes.GETFIELD == opcode;
	}

	public boolean isReturn(int opcode)
	{
		return Opcodes.RETURN == opcode;
	}

	public boolean isNative(int access)
	{
		return ((access&Opcodes.ACC_NATIVE)!=0);
	}

	public boolean isLoadConstant(int opcode)
	{
		return Opcodes.LDC == opcode;
	}
}