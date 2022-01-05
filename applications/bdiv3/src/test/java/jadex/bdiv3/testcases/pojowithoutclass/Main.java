package jadex.bdiv3.testcases.pojowithoutclass;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bytecode.SASM;

public class Main 
{
	/**
	 *  Start a platform and the example.
	 * /
	public static void main(String[] args) throws Exception
	{
		// Used once to generate the ASM code for the HelloAgent.class construction
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		TraceClassVisitor tcv = new TraceClassVisitor(cw, new ASMifier(), new PrintWriter(System.out));
		ClassNode cn = SASM.getClassNode(HelloAgent.class, null);
		cn.accept(tcv);
	}*/
	
	/**
	 *  Test if a pojo agent without class (on disk) can be created.
	 */
	@Test
	public void testPojoWithoutClassAgent()
	{
		try
		{
			byte[] bytes = getClassByteCode();
		
			//Loader loader = new Loader(Main.class.getClassLoader());                                       
			Loader loader = new Loader(Main.class.getClassLoader());                                       
			loader.loadDataInBytes(bytes, "pojowithoutclass/HelloAgent.class");                                                                    

			Class<?> hacl = loader.loadClass("pojowithoutclass.HelloAgent");                                                                        
			Object pojo = hacl.getDeclaredConstructor().newInstance();

			IExternalAccess platform = Starter.createPlatform(PlatformConfigurationHandler.getDefault()).get();
			//System.out.println("started platform");
			IExternalAccess agent = platform.addComponent(pojo).get();
			Map<String, Object> res = agent.waitForTermination().get();
			//Map<String, Object> res = agent.getResultsAsync().get();
			System.out.println("results: "+res);
			//System.out.println("started pojo agent");
			
			if("executed".equals(res.get("result")))
				assertTrue(true);
			else
				assertTrue(false);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 *  Start a platform and the example.
	 */
	public static void main(String[] args) 
	{
		Main main = new Main();
		main.testPojoWithoutClassAgent();
	}
	
	public static class Loader extends ClassLoader 
	{                                                                                              
        private Map<String, byte[]> classes = new HashMap<>();                                                                 

        public Loader(ClassLoader parent) 
        {                                                                                            
        	super(parent);                                                                                                         
        }                                                                                                                              

        public void loadDataInBytes(byte[] byteData, String resourcesName) 
        {                                                           
        	classes.put(resourcesName, byteData);                                                                              
        }                                                                                                                              

        @Override                                                                                                                      
        protected Class<?> findClass(String className) throws ClassNotFoundException 
        {                                                 
        	String filePath = className.replaceAll("\\.", "/").concat(".class");                                                   
            byte[] extractedBytes = classes.get(filePath);                                                                     

            if(extractedBytes == null)                                                                                            
            	throw new ClassNotFoundException("Cannot find " + filePath + " in bytes");                                     
            return defineClass(className, extractedBytes, 0, extractedBytes.length);                                               
        }                                                                                                                              
	}         
	
	/**
	 *  This method generates the byte code of the HelloAgent.class.
	 *  When template HelloWorld.java is changed main() has to be rerun and the content
	 *  of this method has to be replaced manually.
	 */
	public static byte[] getClassByteCode() throws Exception 
	{
		ClassWriter classWriter = new ClassWriter(0);
		FieldVisitor fieldVisitor;
		RecordComponentVisitor recordComponentVisitor;
		MethodVisitor methodVisitor;
		AnnotationVisitor annotationVisitor0;

		classWriter.visit(Opcodes.V11, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, "pojowithoutclass/HelloAgent", null, "jadex/bdiv3/runtime/BDIAgent", null);

		classWriter.visitSource("HelloAgent.java", null);

		{
		annotationVisitor0 = classWriter.visitAnnotation("Ljadex/micro/annotation/Agent;", true);
		annotationVisitor0.visit("type", "bdi");
		annotationVisitor0.visitEnd();
		}
		{
		fieldVisitor = classWriter.visitField(Opcodes.ACC_PROTECTED, "result", "Ljava/lang/String;", null, null);
		{
		annotationVisitor0 = fieldVisitor.visitAnnotation("Ljadex/micro/annotation/AgentResult;", true);
		annotationVisitor0.visitEnd();
		}
		fieldVisitor.visitEnd();
		}
		{
		fieldVisitor = classWriter.visitField(Opcodes.ACC_PROTECTED, "agent", "Ljadex/bridge/IInternalAccess;", null, null);
		{
		annotationVisitor0 = fieldVisitor.visitAnnotation("Ljadex/micro/annotation/Agent;", true);
		annotationVisitor0.visitEnd();
		}
		fieldVisitor.visitEnd();
		}
		{
		methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
		methodVisitor.visitCode();
		Label label0 = new Label();
		methodVisitor.visitLabel(label0);
		methodVisitor.visitLineNumber(11, label0);
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "jadex/bdiv3/runtime/BDIAgent", "<init>", "()V", false);
		methodVisitor.visitInsn(Opcodes.RETURN);
		Label label1 = new Label();
		methodVisitor.visitLabel(label1);
		methodVisitor.visitLocalVariable("this", "Lpojowithoutclass/HelloAgent;", null, label0, label1, 0);
		methodVisitor.visitMaxs(1, 1);
		methodVisitor.visitEnd();
		}
		{
		methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "body", "()V", null, null);
		{
		annotationVisitor0 = methodVisitor.visitAnnotation("Ljadex/bridge/service/annotation/OnStart;", true);
		annotationVisitor0.visitEnd();
		}
		methodVisitor.visitCode();
		Label label0 = new Label();
		methodVisitor.visitLabel(label0);
		methodVisitor.visitLineNumber(25, label0);
		methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
		methodVisitor.visitInsn(Opcodes.DUP);
		methodVisitor.visitLdcInsn("hello body end: ");
		methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
		Label label1 = new Label();
		methodVisitor.visitLabel(label1);
		methodVisitor.visitLineNumber(26, label1);
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitLdcInsn("executed");
		methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, "pojowithoutclass/HelloAgent", "result", "Ljava/lang/String;");
		Label label2 = new Label();
		methodVisitor.visitLabel(label2);
		methodVisitor.visitLineNumber(27, label2);
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitFieldInsn(Opcodes.GETFIELD, "pojowithoutclass/HelloAgent", "agent", "Ljadex/bridge/IInternalAccess;");
		methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, "jadex/bridge/IInternalAccess", "killComponent", "()Ljadex/commons/future/IFuture;", true);
		methodVisitor.visitInsn(Opcodes.POP);
		Label label3 = new Label();
		methodVisitor.visitLabel(label3);
		methodVisitor.visitLineNumber(28, label3);
		methodVisitor.visitInsn(Opcodes.RETURN);
		Label label4 = new Label();
		methodVisitor.visitLabel(label4);
		methodVisitor.visitLocalVariable("this", "Lpojowithoutclass/HelloAgent;", null, label0, label4, 0);
		methodVisitor.visitMaxs(4, 1);
		methodVisitor.visitEnd();
		}
		classWriter.visitEnd();

		return classWriter.toByteArray();
		
		/*ClassWriter classWriter = new ClassWriter(0);
		FieldVisitor fieldVisitor;
		RecordComponentVisitor recordComponentVisitor;
		MethodVisitor methodVisitor;
		AnnotationVisitor annotationVisitor0;

		classWriter.visit(Opcodes.V11, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, "pojowithoutclass/HelloAgent", null, "jadex/bdiv3/runtime/BDIAgent", null);

		classWriter.visitSource("HelloAgent.java", null);

		{
			annotationVisitor0 = classWriter.visitAnnotation("Ljadex/micro/annotation/Agent;", true);
			annotationVisitor0.visit("type", "bdi");
			annotationVisitor0.visitEnd();
		}
		
		{
		methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
		methodVisitor.visitCode();
		Label label0 = new Label();
		methodVisitor.visitLabel(label0);
		methodVisitor.visitLineNumber(6, label0);
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "jadex/bdiv3/runtime/BDIAgent", "<init>", "()V", false);
		methodVisitor.visitInsn(Opcodes.RETURN);
		Label label1 = new Label();
		methodVisitor.visitLabel(label1);
		methodVisitor.visitLocalVariable("this", "Ljadex/bdiv3/testcases/pojowithoutclass/HelloAgent;", null, label0, label1, 0);
		methodVisitor.visitMaxs(1, 1);
		methodVisitor.visitEnd();
		}
		{
		methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "body", "()V", null, null);
		{
		annotationVisitor0 = methodVisitor.visitAnnotation("Ljadex/bridge/service/annotation/OnStart;", true);
		annotationVisitor0.visitEnd();
		}
		methodVisitor.visitCode();
		Label label0 = new Label();
		methodVisitor.visitLabel(label0);
		methodVisitor.visitLineNumber(14, label0);
		methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
		methodVisitor.visitInsn(Opcodes.DUP);
		methodVisitor.visitLdcInsn("body end: ");
		methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
		Label label1 = new Label();
		methodVisitor.visitLabel(label1);
		methodVisitor.visitLineNumber(15, label1);
		methodVisitor.visitInsn(Opcodes.RETURN);
		Label label2 = new Label();
		methodVisitor.visitLabel(label2);
		methodVisitor.visitLocalVariable("this", "Ljadex/bdiv3/testcases/pojowithoutclass/HelloAgent;", null, label0, label2, 0);
		methodVisitor.visitMaxs(4, 1);
		methodVisitor.visitEnd();
		}
		classWriter.visitEnd();

		return classWriter.toByteArray();*/
	}
}


