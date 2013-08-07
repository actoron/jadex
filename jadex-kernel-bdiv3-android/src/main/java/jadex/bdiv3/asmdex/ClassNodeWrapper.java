package jadex.bdiv3.asmdex;

import jadex.bdiv3.asm.IAnnotationNode;
import jadex.bdiv3.asm.IClassNode;
import jadex.bdiv3.asm.IFieldNode;
import jadex.bdiv3.asm.IInnerClassNode;
import jadex.bdiv3.asm.IMethodNode;

import java.util.List;

import org.ow2.asmdex.tree.AnnotationNode;
import org.ow2.asmdex.tree.ClassNode;
import org.ow2.asmdex.tree.MethodNode;

public class ClassNodeWrapper implements IClassNode
{
	private ClassNode classNode;

	public ClassNodeWrapper(ClassNode classNode)
	{
		this.classNode = classNode;
	}

	@Override
	public List<IAnnotationNode> getVisibleAnnotations()
	{
		List<AnnotationNode> visibleAnnotations = classNode.visibleAnnotations;
		return AnnotationNodeWrapper.wrapList(visibleAnnotations);
	}

	@Override
	public List<IMethodNode> getMethods()
	{
		List<MethodNode> methods = classNode.methods;
		return MethodNodeWrapper.wrapList(methods);
	}
	
	public static IClassNode wrap(ClassNode cn)
	{
		return new ClassNodeWrapper(cn);
	}

	@Override
	public void addMethod(IMethodNode mnode)
	{
		classNode.methods.add(((MethodNodeWrapper)mnode).methodNode);
	}

	@Override
	public void addField(IFieldNode fieldNode)
	{
		classNode.fields.add(((FieldNodeWrapper)fieldNode).fieldNode);
	}

	@Override
	public String getName()
	{
		return classNode.name;
	}

	@Override
	public List<IInnerClassNode> getInnerClasses()
	{
		return InnerClassNodeWrapper.wrapList(classNode.innerClasses);
	}
}
