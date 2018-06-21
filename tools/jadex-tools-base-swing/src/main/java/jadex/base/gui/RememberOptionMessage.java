package jadex.base.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *  Option message with checkbox.
 */
public class RememberOptionMessage extends JPanel 
{
	/** The remember checkbox. */
	protected JCheckBox remember;
	
	/**
	 *  Create a new option message.
	 */
    public RememberOptionMessage(String msg) 
    {
    	this(msg, false);
    }
	
	/**
	 *  Create a new option message.
	 */
    public RememberOptionMessage(String msg, boolean remember) 
    {
        super(new BorderLayout());
        JLabel l = new JLabel("<html>"+msg+"</html>");
        //System.out.println(l.getPreferredSize());
        
        Dimension d = l.getPreferredSize();
        int w = d.width;
        int h = d.height;
        l.setPreferredSize(new Dimension(250, (int)(h*w/250*1.6)));
        l.setMinimumSize(new Dimension(250, (int)(h*w/250*1.6)));
        //System.out.println(w+" "+h+" "+l.getPreferredSize());
        
        add(l, BorderLayout.NORTH);
        //add(new JMultilineLabel(msg, 40), BorderLayout.NORTH);
        this.remember = new JCheckBox("Remember this decision", remember);
        add(this.remember, BorderLayout.CENTER);
     }
    
    /**
     *  Get the remember state.
     *  @return The remember state.
     */
    public boolean isRemember()
    {
    	return remember.isSelected();
    }
}