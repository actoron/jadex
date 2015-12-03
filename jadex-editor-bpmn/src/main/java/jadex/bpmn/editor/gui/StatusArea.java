package jadex.bpmn.editor.gui;

import java.awt.BorderLayout;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jadex.bpmn.editor.BpmnEditor;

/**
 *  Area for status messages.
 *
 */
public class StatusArea extends JPanel
{
	public StatusArea()
	{
		setLayout(new BorderLayout());
		final JTextArea logarea = new JTextArea();
		JScrollPane scrollpane = new JScrollPane(logarea);
		add(scrollpane, BorderLayout.CENTER);
		
		Logger.getLogger(BpmnEditor.APP_NAME).addHandler(new Handler()
		{
			public void publish(LogRecord record)
			{
				logarea.append("[" + record.getLevel().getName() + "]: " + record.getMessage() + "\n");
			}
			
			public void flush()
			{
			}
			
			public void close() throws SecurityException
			{
			}
		});
	}
}
