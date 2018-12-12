package jadex.bdiv3.examples.alarmclock;

import java.text.SimpleDateFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

/**
 *  A spinner for presenting and inputing time values.
 */
public class TimeSpinner extends JSpinner
{
	//-------- attributes --------

	/** The textfield. */
	JFormattedTextField tf;

	//-------- constructors --------

	/**
	 *  Create a new time spinner with default format.
	 */
	public TimeSpinner()
	{
		this("HH:mm:ss");
	}

	/**
	 *  Create a new time spinner.
	 *  @param format
	 */
	public TimeSpinner(String format)
	{
		SpinnerDateModel model = new SpinnerDateModel();
		setModel(model);
		DateEditor editor = new DateEditor(this, format);
		setEditor(editor);
		tf = ((DateEditor)this.getEditor()).getTextField();
		tf.setEditable(true);
		DefaultFormatterFactory factory = (DefaultFormatterFactory)tf.getFormatterFactory();
		DateFormatter formatter = (DateFormatter)factory.getDefaultFormatter();
		formatter.setAllowsInvalid(false);
	}

	//-------- methods --------

	/**
	 *  Set the format of the spinner.
	 *  @param format The format string.
	 */
	public void setFormat(String format)
	{
		((DateFormatter)tf.getFormatter()).setFormat(new SimpleDateFormat(format));
		tf.setValue(tf.getValue()); // Force a repaint.
	}
}
