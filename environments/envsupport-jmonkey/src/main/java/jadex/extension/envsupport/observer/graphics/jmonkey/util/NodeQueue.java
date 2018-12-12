package jadex.extension.envsupport.observer.graphics.jmonkey.util;

import java.util.LinkedList;
import java.util.Queue;

import com.jme3.scene.Node;

/**
 * NodeQueue. We use it to reduce the EffectNodes in the Scene.
 * 
 * So there can be ony as many Effects the same time as the Maxsize.
 * 
 * @author 7willuwe: Philip Willuweit
 */
public class NodeQueue 
{
	private int maxsize = 1;
	private Queue<Node> stack = new LinkedList<Node>();
	
	public NodeQueue(int maxsize)
	{
		super();
		this.maxsize = maxsize;
	}
	public Node push(Node node)
	{

		stack.add(node);
		if(stack.size()>maxsize)
		{
			return stack.poll();
		}
		else
		{
			return null;
		}
		
		
	}
	
}
