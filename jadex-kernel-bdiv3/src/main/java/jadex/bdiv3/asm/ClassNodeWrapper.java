package jadex.bdiv3.asm;

import java.util.List;

import org.kohsuke.asm4.tree.AnnotationNode;
import org.kohsuke.asm4.tree.ClassNode;
import org.kohsuke.asm4.tree.MethodNode;

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
}
