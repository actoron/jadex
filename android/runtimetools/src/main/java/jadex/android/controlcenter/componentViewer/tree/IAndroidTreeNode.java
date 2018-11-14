package jadex.android.controlcenter.componentViewer.tree;

import java.util.List;

import jadex.android.controlcenter.SubActivity;
import jadex.android.controlcenter.componentViewer.properties.PropertyItem;
import jadex.base.gui.asynctree.ITreeNode;

public interface IAndroidTreeNode extends ITreeNode
{
	public Class<? extends SubActivity> getPropertiesActivityClass();
	
	public PropertyItem[] getProperties();
}
