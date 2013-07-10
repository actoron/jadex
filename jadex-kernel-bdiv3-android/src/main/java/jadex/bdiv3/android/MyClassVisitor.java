package jadex.bdiv3.android;

import org.ow2.asmdex.ClassVisitor;
import org.ow2.asmdex.MethodVisitor;

public class MyClassVisitor extends ClassVisitor
{
	final private MethodInsManager ruleManager;

	public MyClassVisitor(int api, MethodInsManager ruleManager, ClassVisitor cv)
	{
		super(api, cv);
		this.ruleManager = ruleManager;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String[] signature, String[] exceptions)
	{
		MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
		MyMethodVisitor ma = new MyMethodVisitor(api, ruleManager, mv, name, desc);
		return ma;
	}

	@Override
	public void visit(int version, int access, String name, String[] signature, String superName, String[] interfaces)
	{
		super.visit(version, access, name, signature, superName, interfaces);
		// visitMethod(Opcodes.ACC_PUBLIC, "hello", "ILjava/lang/String;", new
		// String[]{"void"}, null);
	}

}
