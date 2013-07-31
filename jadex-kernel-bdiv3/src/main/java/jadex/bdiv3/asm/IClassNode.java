package jadex.bdiv3.asm;

import java.util.List;

public interface IClassNode
{
	public List<IMethodNode> getMethods();

	public List<IAnnotationNode> getVisibleAnnotations();

	public void addMethod(IMethodNode mnode);
}
