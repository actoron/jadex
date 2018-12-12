package jadex.commons.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.JTextField;
import javax.swing.text.Document;

import jadex.commons.IValidator;

/**
 *  The validator text field allows to check the input and automatically
 *  underlines an invalid input with a red line (like an eclipse error).
 */
public class JValidatorTextField extends JTextField
{
	//-------- attributes --------
	
	/** The validator. */
	protected IValidator validator;
	
	//-------- attributes --------
	
	/**
     * Constructs a new <code>TextField</code>.  A default model is created,
     * the initial string is <code>null</code>,
     * and the number of columns is set to 0.
     */
    public JValidatorTextField() 
    {
        super();
    }
    
    /**
     * Constructs a new <code>TextField</code>.  A default model is created,
     * the initial string is <code>null</code>,
     * and the number of columns is set to 0.
     */
    public JValidatorTextField(IValidator validator) 
    {
        super();
        setValidator(validator);
    }

    /**
     * Constructs a new <code>TextField</code> initialized with the
     * specified text. A default model is created and the number of
     * columns is 0.
     *
     * @param text the text to be displayed, or <code>null</code>
     */
    public JValidatorTextField(String text, IValidator validator) 
    {
        super(text);
        setValidator(validator);
    }
    
    /**
     * Constructs a new <code>TextField</code> initialized with the
     * specified text. A default model is created and the number of
     * columns is 0.
     *
     * @param text the text to be displayed, or <code>null</code>
     */
    public JValidatorTextField(String text) 
    {
        super(text);
    }

    /**
     * Constructs a new empty <code>TextField</code> with the specified
     * number of columns.
     * A default model is created and the initial string is set to
     * <code>null</code>.
     *
     * @param columns  the number of columns to use to calculate 
     *   the preferred width; if columns is set to zero, the
     *   preferred width will be whatever naturally results from
     *   the component implementation
     */ 
    public JValidatorTextField(int columns) 
    {
        super(columns);
    }

    /**
     * Constructs a new <code>TextField</code> initialized with the
     * specified text and columns.  A default model is created.
     *
     * @param text the text to be displayed, or <code>null</code>
     * @param columns  the number of columns to use to calculate 
     *   the preferred width; if columns is set to zero, the
     *   preferred width will be whatever naturally results from
     *   the component implementation
     */
    public JValidatorTextField(String text, int columns) 
    {
        super(text, columns);
    }

    /**
     * Constructs a new <code>JTextField</code> that uses the given text
     * storage model and the given number of columns.
     * This is the constructor through which the other constructors feed.
     * If the document is <code>null</code>, a default model is created.
     *
     * @param doc  the text storage to use; if this is <code>null</code>,
     *		a default will be provided by calling the
     *		<code>createDefaultModel</code> method
     * @param text  the initial string to display, or <code>null</code>
     * @param columns  the number of columns to use to calculate 
     *   the preferred width >= 0; if <code>columns</code>
     *   is set to zero, the preferred width will be whatever
     *   naturally results from the component implementation
     * @exception IllegalArgumentException if <code>columns</code> < 0
     */
    public JValidatorTextField(Document doc, String text, int columns) 
    {
       super(doc, text, columns);
    }
	
    //-------- methods --------
    
    /**
     *  Set the validator.
     *  @param validator The validator.
     */
    public void setValidator(IValidator validator)
    {
    	this.validator = validator;
    }
    
    /**
     *  Test if the input is valid with regards to the text.
     *  @return True, if valid.
     */
    protected boolean isInputValid()
    {
    	return validator==null? true: validator.isValid(getText());
    }
    
    /**
     *  Paint the textfield and optionally add a red error indicator.
     */
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if(!isInputValid())
		{
			// Determine draw area.
			Rectangle	bounds	= getBounds();
			Insets	insets	= getInsets();
			bounds.x	= insets.left;
			bounds.y	= insets.top;
			bounds.width	-= insets.left + insets.right;
			bounds.height	-= insets.top + insets.bottom;

			Rectangle2D	textbounds	= g.getFontMetrics().getStringBounds(getText(), g);
			int width = (int)textbounds.getWidth();
			
			g.setColor(Color.red);
			
			int y = bounds.y + bounds.height;
			int x;
			int wx;
			
			boolean ltr = getComponentOrientation().isLeftToRight();
			int align = getHorizontalAlignment();
			if(align==JTextField.LEFT || (ltr && align==JTextField.LEADING) 
				|| (!ltr && align==JTextField.TRAILING))
			{
				x = bounds.x;
				//System.out.println("left:"+x);
			}
			else if(getHorizontalAlignment()==JTextField.RIGHT 
				|| (!ltr && align==JTextField.LEADING) || (ltr && align==JTextField.TRAILING))
			{
				x = Math.max(bounds.x, bounds.x + bounds.width - width);
				//System.out.println("right:"+x);
			}
			else if(getHorizontalAlignment()==JTextField.CENTER)
			{
				x = Math.max(bounds.x, bounds.x + (bounds.width - width)/2);
//				System.out.println("center:"+x+", bounds.width:"+bounds.width+", width:"+textbounds.getWidth());
			}
			else
			{
				x	= bounds.x;
			}
			
			wx = Math.min(x + width, bounds.x + bounds.width);
			
//			g.setColor(Color.BLACK);
//			g.drawRect(bounds.x, bounds.y, bounds.width-1, bounds.height-1);
//			g.setColor(Color.RED);
//			g.drawRect(x, bounds.y, wx-x-1, bounds.height-1);

			while(x+2<=wx)
			{
				g.drawLine(x, y, x+2, y-2);
				x+=2;
				if(x+2<=wx)
				{
					g.drawLine(x, y-2, x+2, y);
					x+=2;
				}
			}
		}
	}
	
	/**
	 *  Main for testing.
	 * /
	public static void main(String[] args)
	{
		IValidator vf = new IValidator()
		{
			public boolean isValid()
			{
				return false;
			}
		};
		
		JValidatorTextField v1 = new JValidatorTextField("huhuuhuhuhuhuuhhuhhuuu", vf);
		JValidatorTextField v2 = new JValidatorTextField("huhuuhuhuhuhuuhhuhhuuu", vf);
		v2.setHorizontalAlignment(JTextField.CENTER);
		JValidatorTextField v3 = new JValidatorTextField("huhuuhuhuhuhuuhhuhhuuu", vf);
		v3.setHorizontalAlignment(JTextField.RIGHT);
		JPanel p = new JPanel(new GridLayout(3,1));
		p.add(v1);
		p.add(v2);
		p.add(v3);
		JFrame f = new JFrame();
		f.getContentPane().add("Center", p);
		f.pack();
		f.setVisible(true);
	}*/
}