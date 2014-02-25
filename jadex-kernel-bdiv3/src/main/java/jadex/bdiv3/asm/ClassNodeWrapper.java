package jadex.bdiv3.asm;

import java.util.List;

import org.kohsuke.asm4.tree.ClassNode;

public class ClassNodeWrapper implements IClassNode
{
	private ClassNode classNode;

	public ClassNodeWrapper(ClassNode classNode)
	{
		this.classNode = classNode;
	}

	public List<IAnnotationNode> getVisibleAnnotations()
	{
		return AnnotationNodeWrapper.wrapList(classNode.visibleAnnotations);
	}

	public List<IMethodNode> getMethods()
	{
		return MethodNodeWrapper.wrapList(classNode.methods);
	}
	
	public List<String> getInterfaces()
	{
		return classNode.interfaces;
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
		classNode.fields.add(((FieldNodeWrapper) fieldNode).fieldNode);
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
