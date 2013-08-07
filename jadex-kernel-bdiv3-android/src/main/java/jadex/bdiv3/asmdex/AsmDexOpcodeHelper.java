package jadex.bdiv3.asmdex;

import jadex.bdiv3.OpcodeHelper;

import org.ow2.asmdex.Opcodes;
import org.ow2.asmdex.tree.AbstractInsnNode;

public class AsmDexOpcodeHelper extends OpcodeHelper
{

	@Override
	public boolean isPutField(int opcode)
	{
		switch (opcode)
		{
			case Opcodes.INSN_IPUT :
			case Opcodes.INSN_IPUT_BOOLEAN :
			case Opcodes.INSN_IPUT_BYTE :
			case Opcodes.INSN_IPUT_CHAR :
			case Opcodes.INSN_IPUT_OBJECT :
			case Opcodes.INSN_IPUT_SHORT :
			case Opcodes.INSN_IPUT_WIDE :
				return true;
			default :
				return false;
		}
	}

	@Override
	public boolean isGetField(int opcode)
	{
		switch (opcode)
		{
			case Opcodes.INSN_IGET :
			case Opcodes.INSN_IGET_BOOLEAN :
			case Opcodes.INSN_IGET_BYTE :
			case Opcodes.INSN_IGET_CHAR :
			case Opcodes.INSN_IGET_OBJECT :
			case Opcodes.INSN_IGET_SHORT :
			case Opcodes.INSN_IGET_WIDE :
				return true;
			default :
				return false;
		}
	}

	@Override
	public boolean isReturn(int opcode)
	{
		switch (opcode)
		{
			case Opcodes.INSN_RETURN :
			case Opcodes.INSN_RETURN_OBJECT :
			case Opcodes.INSN_RETURN_VOID :
			case Opcodes.INSN_RETURN_WIDE :
				return true;
			default :
				return false;
		}
	}

	@Override
	public boolean isNative(int access)
	{
		return ((Opcodes.ACC_NATIVE&access) != 0);
	}
	
	@Override
	public boolean isLoadConstant(int opcode)
	{
		switch (opcode)
		{
			case Opcodes.INSN_CONST:
			case Opcodes.INSN_CONST_16:
			case Opcodes.INSN_CONST_4:
			case Opcodes.INSN_CONST_CLASS:
			case Opcodes.INSN_CONST_HIGH16:
			case Opcodes.INSN_CONST_STRING:
			case Opcodes.INSN_CONST_STRING_JUMBO:
			case Opcodes.INSN_CONST_WIDE:
			case Opcodes.INSN_CONST_WIDE_16:
			case Opcodes.INSN_CONST_WIDE_32:
			case Opcodes.INSN_CONST_WIDE_HIGH16:
				return true;
			default:
				return false;
		}
	}

}
