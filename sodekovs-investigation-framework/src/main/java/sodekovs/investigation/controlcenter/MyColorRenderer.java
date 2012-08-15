package sodekovs.investigation.controlcenter;


import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class MyColorRenderer extends DefaultTableCellRenderer 
{ 
	  @Override 
	  public void setValue( Object value ) 
	  { 			  
	    if ( (value instanceof Integer) || (value instanceof Long)) 
	    { 
//	      setForeground( (Long) value % 2 == 0 ? Color.BLUE : Color.GRAY ); 
	    	setForeground(Color.BLUE);	 
	      setText( value.toString() );	
	    } 
	    else{ 
	      super.setValue( value ); 
	    setForeground(Color.RED);
	    }
	  } 
	}
