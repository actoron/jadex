package jadex.simulation.analysis.common.superClasses.service.view.session.subprocess;

import java.awt.Dimension;
import jadex.commons.beans.PropertyVetoException;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class ATaskInternalFrame extends JInternalFrame
{
	static int openFrameCount = 0;
	static final int xOffset = 30, yOffset = 30;

	public ATaskInternalFrame(String name, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable)
	{
	    super(name,resizable, closable, maximizable, iconifiable);
//	    setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);   
	    pack();
	    setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
	    openFrameCount++;
	    setVisible(true);
	}

}
