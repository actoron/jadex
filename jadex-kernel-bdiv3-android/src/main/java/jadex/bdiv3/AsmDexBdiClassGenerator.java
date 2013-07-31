package jadex.bdiv3;

import jadex.android.commons.JadexDexClassLoader;
import jadex.android.commons.Logger;
import jadex.bdiv3.android.DexLoader;
import jadex.bdiv3.model.BDIModel;
import jadex.commons.SReflect;
import jadex.commons.SUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.Type;
import org.ow2.asmdex.ApplicationReader;
import org.ow2.asmdex.ApplicationVisitor;
import org.ow2.asmdex.ApplicationWriter;
import org.ow2.asmdex.ClassVisitor;
import org.ow2.asmdex.ClassWriter;
import org.ow2.asmdex.FieldVisitor;
import org.ow2.asmdex.MethodVisitor;
import org.ow2.asmdex.Opcodes;
import org.ow2.asmdex.tree.ApplicationNode;
import org.ow2.asmdex.tree.ClassNode;
import org.ow2.asmdex.tree.MethodNode;

import android.util.Log;

public class AsmDexBdiClassGenerator implements IBDIClassGenerator
{
	protected static Method methoddc1;
	protected static Method methoddc2;
	public static File OUTPATH;

	static
	{
		try
		{
			AccessController.doPrivileged(new PrivilegedExceptionAction<Object>()
			{
				public Object run() throws Exception
				{
					Class<?> cl = Class.forName("java.lang.ClassLoader");
					methoddc1 = cl.getDeclaredMethod("defineClass", new Class[]
					{String.class, byte[].class, int.class, int.class});
					methoddc2 = cl.getDeclaredMethod("defineClass", new Class[]
					{String.class, byte[].class, int.class, int.class, ProtectionDomain.class});
					return null;
				}
			});
		}
		catch (PrivilegedActionException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<?> generateBDIClass(String classname, BDIModel micromodel, ClassLoader cl)
	{
		return generateBDIClass(classname, micromodel, cl, new HashSet<String>());
	}

	/**
	 * Generate class. Generated class should be available in the given
	 * classLoader at the end of this method.
	 */
	public Class<?> generateBDIClass(final String clname, final BDIModel model, final ClassLoader cl, final Set<String> done)
	{
		Class<?> ret = null;

		final List<String> todo = new ArrayList<String>();
		done.add(clname);

		int api = Opcodes.ASM4;

		final String iname = "L" + clname.replace('.', '/') + ";";

		// pattern to accept all inner classes
		final Pattern classPattern = Pattern.compile("L" + clname.replace('.', '/') + ";?\\$?.*");

		try
		{
			JadexDexClassLoader androidCl = (JadexDexClassLoader) SUtil.androidUtils().findJadexDexClassLoader(cl.getParent());
			// is = SUtil.getResource(APP_PATH, cl);
			String appPath = ((JadexDexClassLoader) androidCl).getDexPath();
			InputStream is = getFileInputStream(new File(appPath));
			ApplicationReader ar = new ApplicationReader(api, is);
			ApplicationWriter aw = new ApplicationWriter();
			ApplicationNode an = new ApplicationNode(api);
			
			final ArrayList<ClassNode> classes = new ArrayList<ClassNode>();
			
			ApplicationVisitor av = new ApplicationVisitor(api, an)
			{

				@Override
				public ClassVisitor visitClass(int access, String name, String[] signature, String superName, String[] interfaces)
				{
					if (classPattern.matcher(name).matches())
					{
						System.out.println("visit class: " + name);
						final ClassNode cn = new ClassNode(api, access, name, signature, superName, interfaces);
						classes.add(cn);
						final ClassVisitor superVisitor = super.visitClass(access, iname, signature, superName, interfaces);
						ClassVisitor cv = new ClassVisitor(api, cn)
						{

							@Override
							public MethodVisitor visitMethod(int access, String name, String desc, String[] signature, String[] exceptions)
							{
								return new MethodVisitor(api, super.visitMethod(access, name, desc, signature, exceptions))
								{

									@Override
									public void visitFieldInsn(int opcode, String owner, String name, String desc, int valueRegister,
											int objectRegister)
									{
										// objectRegister = reference to
										// instance (ignored when static),
										// valueRegister = index of value to
										// put!
										if (isInstancePut(opcode) && model.getCapability().hasBelief(name)
												&& model.getCapability().getBelief(name).isFieldBelief())
										{
											//
											// possibly transform basic value
											if (SReflect.isBasicType(SReflect.findClass0(Type.getType(desc).getClassName(), null, cl)))
											{
												visitMethodInsn(Opcodes.INSN_INVOKE_STATIC, "Ljadex/commons/SReflect;", "wrapValue",
														"Ljava/lang/Object;" + desc, new int[]
														{valueRegister});
											}

											super.visitFieldInsn(opcode, owner, name, desc, valueRegister, objectRegister);
											//
											// visitInsn(Opcodes.SWAP);
											//
											// // fetch bdi agent value from
											// field
											//
											// // this pop aload is necessary in
											// inner
											// // classes!
											// visitInsn(Opcodes.POP);
											// visitVarInsn(Opcodes.ALOAD, 0);
											// super.visitFieldInsn(Opcodes.GETFIELD,
											// iclname, "__agent",
											// Type.getDescriptor(BDIAgent.class));
											// // add field name
											// visitLdcInsn(name);
											// visitInsn(Opcodes.SWAP);
											// // add this
											// visitVarInsn(Opcodes.ALOAD, 0);
											// visitInsn(Opcodes.SWAP);
											//
											// // invoke method
											// visitMethodInsn(Opcodes.INVOKESTATIC,
											// "jadex/bdiv3/BDIAgent",
											// "writeField",
											// "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Ljadex/bdiv3/BDIAgent;)V");
										}
										else
										{
											super.visitFieldInsn(opcode, owner, name, desc, valueRegister, objectRegister);
										}
									}

								};
							}

							@Override
							public void visitInnerClass(String name, String outerName, String innerName, int access)
							{
								// System.out.println("vic: " + name + " " +
								// outerName + " " + innerName + " " + access);
								String icln = (name == null ? null : name.replace("/", "."));
								if (!done.contains(icln))
									todo.add(icln);
								super.visitInnerClass(name, outerName, innerName, access);// Opcodes.ACC_PUBLIC);
																							// does
																							// not
																							// work
							}

							@Override
							public void visitEnd()
							{
								visitField(Opcodes.ACC_PUBLIC, "__agent", Type.getDescriptor(BDIAgent.class), null, null);
								visitField(Opcodes.ACC_PUBLIC, "__globalname", Type.getDescriptor(String.class), null, null);
								super.visitEnd();
							}

						};

						return cv;
					}
					else
					{
						return null;
					}
				}

			};

//			 ar.accept(aa, new String[]{iname}, 0);
			ar.accept(av, null, 0); // visit all classes
			
			aw.visit();
			for (ClassNode classNode : classes)
			{
				transformClassNode(classNode, iname, model);
				String[] signature = classNode.signature == null? null : classNode.signature.toArray(new String[classNode.signature.size()]);
				String[] interfaces = classNode.interfaces == null ? null :classNode.interfaces.toArray(new String[classNode.interfaces.size()]);
				System.out.println("write class: " + classNode.name);
				ClassVisitor visitClass = aw.visitClass(classNode.access, classNode.name, signature, classNode.superName, interfaces);
				classNode.accept(visitClass);
			}
			aw.visitEnd();

			byte[] dex = aw.toByteArray();

			// we need the android user loader as parent here, because class
			// dependencies for the agent could exist
			ClassLoader newCl = DexLoader.load(androidCl, dex, OUTPATH);

			Class<?> generatedClass = newCl.loadClass(clname);
			ret = generatedClass;

			if (androidCl != null)
			{
				androidCl.defineClass(clname, generatedClass);
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	private void transformClassNode(ClassNode cn, String iname, BDIModel model)
	{
		// Some transformations are only applied to the agent class and not its
		// inner classes.
		boolean agentclass = false;
		if (cn.visibleAnnotations != null)
		{
			for (org.ow2.asmdex.tree.AnnotationNode an : cn.visibleAnnotations)
			{
				if ("Ljadex/micro/annotation/Agent;".equals(an.desc) || "Ljadex/bdiv3/annotation/Capability;".equals(an.desc))
				{
					agentclass = true;
					break;
				}
			}
		}

//		final String iclname = iname.replace(".", "/");

		// Check method for array store access of beliefs and replace with
		// static method call
		MethodNode[] mths = cn.methods.toArray(new MethodNode[0]);
	}

	private InputStream getFileInputStream(File apkFile)
	{
		InputStream result = null;
		String desiredFile = "classes.dex";
		try
		{
			FileInputStream fin = new FileInputStream(apkFile);
			@SuppressWarnings("resource")
			// Resource is closed later
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null)
			{
				Logger.d("Unzipping " + ze.getName());

				if (!ze.isDirectory())
				{

					/**** Changes made below ****/
					if (ze.getName().toString().equals(desiredFile))
					{
						result = zin;
						break;
					}

				}

				zin.closeEntry();

			}
			// zin.close();
		}
		catch (Exception e)
		{
			Log.e("Decompress", "unzip", e);
		}

		return result;
	}

	private static boolean isInstancePut(int opcode)
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
}
