package jadex.bdiv3.android;

import org.ow2.asmdex.ApplicationVisitor;
import org.ow2.asmdex.ClassVisitor;


public class MyApplicationVisitor extends ApplicationVisitor
{
	final LogClassWriter logClassWriter;
	final MethodInsManager ruleManager;
//	private ClassVisitor dummyCA;

	public MyApplicationVisitor(int api, MethodInsManager rm, ApplicationVisitor av)
	{
		super(api, av);
		ruleManager = rm;
		logClassWriter = new LogClassWriter(rm, av);
		
//		dummyCA = new ClassVisitor(api)
//		{
//			
//		};
	}

	@Override
	public ClassVisitor visitClass(int access, String name, String[] signature, String superName, String[] interfaces)
	{
		System.out.println(name);
		ClassVisitor cv = av.visitClass(access, name, signature, superName, interfaces);
		MyClassVisitor ca = new MyClassVisitor(api, ruleManager, cv);
		return ca;
	}
	@Override
	public void visitEnd()
	{
		logClassWriter.addLogClass();
		av.visitEnd();
	}
}
