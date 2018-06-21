package jadex.bdiv3;

import jadex.bdiv3.asm.AsmOpcodeHelper;
import jadex.commons.SReflect;

public abstract class OpcodeHelper
{
	private static OpcodeHelper INSTANCE;
	
	public final static int ACC_PUBLIC = 0x1;
	public final static int ACC_PRIVATE = 0x2;
	public final static int ACC_PROTECTED = 0x4;
	public final static int ACC_STATIC = 0x8;

	public static OpcodeHelper getInstance()
	{
		if(INSTANCE == null) 
		{
			if(SReflect.isAndroid()) 
			{
				throw new Error("OpcodeHelper.getInstance() is not implemented for Android.");
			} 
			else 
			{
				INSTANCE = new AsmOpcodeHelper();
			}
		}
		return INSTANCE;
	}

	public abstract boolean isPutField(int opcode);

	public abstract boolean isGetField(int opcode);

	public abstract boolean isReturn(int opcode);
	
	public abstract boolean isNative(int access);
	
	public abstract boolean isLoadConstant(int opcode);
	
}
