package jadex.bdiv3.asmdex;

import jadex.bdiv3.asm.IAnnotationNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ow2.asmdex.tree.AnnotationNode;

public class AnnotationNodeWrapper implements IAnnotationNode
{

	private AnnotationNode annotationNode;

	public AnnotationNodeWrapper(AnnotationNode an)
	{
		this.annotationNode = an;
	}

	@Override
	public String getDescription()
	{
		return annotationNode.desc;
	}

	public static List<IAnnotationNode> wrapList(List<AnnotationNode> ans)
	{
		List<IAnnotationNode> result = null;
		if (ans != null)
		{
			result = new ArrayList<IAnnotationNode>(ans.size());

			for (AnnotationNode annotationNode : ans)
			{
				result.add(new AnnotationNodeWrapper(annotationNode));
			}
			
			result = Collections.unmodifiableList(result);
		}
		return result;
	}

}
