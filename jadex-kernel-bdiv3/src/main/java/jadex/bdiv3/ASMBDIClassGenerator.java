package jadex.bdiv3;

import jadex.bdiv3.model.BDIModel;
import jadex.commons.ByteClassLoader;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * 
 */
public class ASMBDIClassGenerator implements IBDIClassGenerator
{
	/**
	 *  Generate class.
	 */
	public Class<?> generateBDIClass(Class<?> cma, final BDIModel micromodel, ClassLoader cl)
	{
		Class<?> ret = null;
		
		String clname = cma.getName()+BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST;
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static void main(String[] args) throws Exception
	{
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		TraceClassVisitor tcv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
//		CheckClassAdapter cc = new CheckClassAdapter(tcv);
		
		ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, tcv)
		{
			public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces)
			{
				System.out.println(name+" extends "+superName);
				super.visit(version, access, "lars/Lars", null, "jadex/bdiv3/MyTestClass", interfaces);
			}
			
//			public FieldVisitor visitField(int access, String name,
//				String desc, String signature, Object value)
//			{
////				System.out.println(desc+" "+name);
////				return null;
//				
//				return super.visitField(access, name, desc, signature, value);
//			}
			
			public MethodVisitor visitMethod(int access, String name,
				String desc, String signature, String[] exceptions)
			{
				System.out.println(desc+" "+name);
				return new FieldAccessAdapter(Opcodes.ASM4, super.visitMethod(access, name, desc, signature, exceptions));
			}
			
			public void visitEnd()
			{
				FieldVisitor fv = visitField(Opcodes.ACC_PUBLIC, "__agent", Type.getDescriptor(String.class), null, null);
				super.visitEnd();
			}
		};
		
		ClassReader cr = new ClassReader("jadex.bdiv3.MyTestClass");
		cr.accept(cv, 0);
		
		ByteClassLoader bcl = new ByteClassLoader(ASMBDIClassGenerator.class.getClassLoader());
		Class<?> cl = bcl.loadClass("lars.Lars", cw.toByteArray(), true);
//		System.out.println(cl);
		cl.newInstance();
//		Method m = cl.getMethod("main", new Class[]{String[].class});
//		m.setAccessible(true);
//		m.invoke(null, new Object[]{new String[0]});
	}
}

/**
 * 
 */
class FieldAccessAdapter extends MethodVisitor implements Opcodes 
{ 
	/**
	 * 
	 */
	public FieldAccessAdapter(int api, MethodVisitor mv)
	{
		super(api, mv);
	}

	/**
	 * 
	 */
	public void visitFieldInsn(int opcode, String owner, String name, String desc)
	{
		// is already on stack (object + value)
//		mv.visitVarInsn(ALOAD, 0);
//		mv.visitIntInsn(BIPUSH, 25);
		
		if(Opcodes.PUTFIELD==opcode)
		{
			System.out.println("vis: "+opcode+" "+owner+" "+name+" "+desc);
			
			visitMethodInsn(INVOKESTATIC, "jadex/commons/SReflect", "wrapValue", "("+desc+")Ljava/lang/Object;");
			visitLdcInsn(name);
			visitVarInsn(ALOAD, 0);
			visitMethodInsn(INVOKEVIRTUAL, "jadex/bdiv3/MyTestClass", "writeField", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V");
//			visitMethodInsn(INVOKEVIRTUAL, "jadex/bdiv3/MyTestClass", "writeField2", "()V");
		}
		else
		{
			super.visitFieldInsn(opcode, owner, name, desc);
		}
	}
}
