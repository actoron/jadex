package jadex.android.controlcenter.componentViewer;

import jadex.base.gui.asynctree.ITreeNode;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.TextView;

public class BreadCrumbView extends TextView
{

	public BreadCrumbView(Context context)
	{
		super(context);
		
		setText("");
		setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		setTextColor(ColorStateList.valueOf(Color.GRAY));
	}

	public void setCurrentTreeNode(ITreeNode currentNode)
	{
		ITreeNode parent = currentNode;
		List<ITreeNode> path = new ArrayList<ITreeNode>();
		
		while (parent != null) {
			path.add(parent);
			parent = parent.getParent();
		}

		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (int i = path.size()-1; i > -1; i--)
		{
			sb.append(sep);
			sb.append(path.get(i));
			sep = " > ";
		}
		setText(sb.toString());
	}

}
