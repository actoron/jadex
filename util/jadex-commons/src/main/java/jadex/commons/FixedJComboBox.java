package jadex.commons;

import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

// 4133743
// Votes 	0
// Synopsis 	JComboBox.getSelectedIndex fails with duplicate entries
// Category 	java:classes_swing
// Reported Against 	1.1.5 , swing1.0.2
// Release Fixed 	
// State 	11-Closed, Not a Defect, bug
// Priority: 	4-Low
// Related Bugs 	4133804
// Submit Date 	29-APR-1998
// Description 	
//
// If a JCombobox contains two elements which have
// the same contents, getSelectedIndex() always returns
// the index of the first duplicate, even if the 
// second duplicate was chosen.

public class FixedJComboBox extends JComboBox
{
    public FixedJComboBox(ComboBoxModel aModel) 
    {
        super(aModel);
    }

    public FixedJComboBox(final Object items[]) 
    {
    	super(items);
    }

    public FixedJComboBox(Vector items) 
    {
    	super(items);
    }

    public FixedJComboBox() 
    {
    	super();
    }
    
    protected int iSelectedIndex = 0;

	public void setSelectedIndex(int anIndex) 
	{
        anIndex = anIndex<0 ? 0 : anIndex;
        int size = super.dataModel.getSize();

        if(anIndex>= size)
            throw new IllegalArgumentException();

        iSelectedIndex = anIndex;
        
        super.setSelectedItem(super.dataModel.getElementAt(anIndex));
    }

	public int getSelectedIndex()
	{
		return iSelectedIndex;
	}


}
