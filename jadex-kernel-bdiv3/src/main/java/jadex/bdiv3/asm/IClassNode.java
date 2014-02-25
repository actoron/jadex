package jadex.bdiv3.asm;

import java.util.List;

public interface IClassNode
{
	public List<IMethodNode> getMethods();

	public List<IAnnotationNode> getVisibleAnnotations();

	public void addMethod(IMethodNode mnode);

	public void addField(IFieldNode fieldNode);
	
	public List<String> getInterfaces();

	/**
	 * Returns the name (including path) of the Class in internal
	 * representation (with '/').
	 * In ASM, this returns the class name only, while in
	 * ASMDEX, this returns the class name prefixed with 'L' and suffixed with ';'.
	 * @return Name of the Class
	 */
	public String getName();

	public List<IInnerClassNode> getInnerClasses();
}
