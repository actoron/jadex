package jadex.editor.bpmn.editor.properties.template;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;


/**
 * A cell editor that presents a spinner.
 */
public class SpinnerCellEditor extends CellEditor
{
	protected Spinner			spinner;

	protected int				selection;
	
	private static final int	defaultStyle	= SWT.BORDER;

	public SpinnerCellEditor()
	{
		setStyle(defaultStyle);
	}

	public SpinnerCellEditor(Composite parent)
	{
		this(parent, defaultStyle, 0, Integer.MAX_VALUE);
	}

	public SpinnerCellEditor(Composite parent, int style, int min, int max)
	{
		super(parent, style);
				
//		System.out.println("min: "+min+" max: "+max);
		spinner.setMinimum(min);
		spinner.setMaximum(max);
//		spinner.setSelection(1);
		spinner.setIncrement(1);
		spinner.setPageIncrement(100);
	}

	protected Control createControl(Composite parent)
	{
		// cannot set spinner min/max values here, because is called in super!
		
		spinner = new Spinner(parent, getStyle());
		spinner.setFont(parent.getFont());
//		spinner.addKeyListener(new KeyAdapter()
//		{
//			// hook key pressed - see PR 14201
//			public void keyPressed(KeyEvent e)
//			{
//				keyReleaseOccured(e);
//			}
//		});

		spinner.addSelectionListener(new SelectionAdapter()
		{
			public void widgetDefaultSelected(SelectionEvent event)
			{
				applyEditorValueAndDeactivate();
			}

			public void widgetSelected(SelectionEvent event)
			{
				selection = spinner.getSelection();
			}
		});

		// comboBox.addTraverseListener(new TraverseListener() {
		// public void keyTraversed(TraverseEvent e) {
		// if (e.detail == SWT.TRAVERSE_ESCAPE
		// || e.detail == SWT.TRAVERSE_RETURN) {
		// e.doit = false;
		// }
		// }
		// });

		spinner.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				SpinnerCellEditor.this.focusLost();
			}
		});
		return spinner;
	}

	protected Object doGetValue()
	{
		return Integer.valueOf(selection);
	}

	protected void doSetFocus()
	{
		spinner.setFocus();
	}

	public LayoutData getLayoutData()
	{
		LayoutData layoutData = super.getLayoutData();
		if((spinner == null) || spinner.isDisposed())
		{
			layoutData.minimumWidth = 60;
		}
		else
		{
			// make the comboBox 10 characters wide
			GC gc = new GC(spinner);
			layoutData.minimumWidth = (gc.getFontMetrics()
					.getAverageCharWidth() * 10) + 10;
			gc.dispose();
		}
		return layoutData;
	}

	protected void doSetValue(Object value)
	{
		System.out.println("value: " + value);
		Assert.isTrue(spinner != null && (value instanceof Integer));
		spinner.setSelection(((Integer)value).intValue());
	}

	void applyEditorValueAndDeactivate()
	{
		// must set the selection before getting value
		selection = spinner.getSelection();
		Object newValue = doGetValue();
		markDirty();
		boolean isValid = isCorrect(newValue);
		setValueValid(isValid);

		// if (!isValid) {
		// // Only format if the 'index' is valid
		// if (items.length > 0 && selection >= 0 && selection < items.length) {
		// // try to insert the current value into the error message.
		// setErrorMessage(MessageFormat.format(getErrorMessage(),
		// new Object[] { items[selection] }));
		// } else {
		// // Since we don't have a valid index, assume we're using an
		// // 'edit'
		// // combo so format using its text value
		// setErrorMessage(MessageFormat.format(getErrorMessage(),
		// new Object[] { comboBox.getText() }));
		// }
		// }

		fireApplyEditorValue();
		deactivate();
	}

	protected void focusLost()
	{
		if(isActivated())
		{
			applyEditorValueAndDeactivate();
		}
	}

	protected void keyReleaseOccured(KeyEvent keyEvent)
	{
		if(keyEvent.character == '\u001b')
		{ // Escape character
			fireCancelEditor();
		}
		else if(keyEvent.character == '\t')
		{ // tab key
			applyEditorValueAndDeactivate();
		}
	}
}