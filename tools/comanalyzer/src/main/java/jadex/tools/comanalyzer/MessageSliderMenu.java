package jadex.tools.comanalyzer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jadex.commons.SUtil;


/**
 * A popup control for "sliding" thru the recorded messages. Its used by the
 * tooltabs for replaying.
 */
public class MessageSliderMenu extends TitlePopupMenu implements ChangeListener
{

	/** The tool who is using it. */
	private ToolTab tool;

	/** controls */
	private JLabel label;

	private JSlider slider;

	// -------- constructor --------

	/**
	 * Constructor for the message slider menu.
	 * 
	 * @param title The title of the control.
	 * @param tool The tool.
	 */
	public MessageSliderMenu(String title, ToolTab tool)
	{
		super(title);
		this.tool = tool;

		// dont show the slider if there are no messages
		if(tool.plugin.getMessageList().size() == 0)
		{
			JLabel text = new JLabel("No messages recorded yet");
			text.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
			this.setLayout(new BorderLayout());
			this.add(text, BorderLayout.CENTER);
			return;
		}

		JPanel slider_panel = new JPanel(new GridLayout(2, 0));
		slider = new JSlider(JSlider.HORIZONTAL);
		slider.setMinimum(0);
		slider.setMaximum(tool.plugin.getMessageList().size());
		slider.setValue(tool.messagelist.size());
		slider.addChangeListener(this);

		label = new JLabel();
		String display = "Message " + tool.messagelist.size() + " / " + tool.plugin.getMessageList().size();
		label.setText(display);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		slider_panel.add(slider);
		slider_panel.add(label);

		this.setLayout(new BorderLayout());
		this.add(slider_panel, BorderLayout.CENTER);

	}

	// -------- ChangeListener interface --------

	/**
	 * This method is called when the value of the slidere changes. Depending on
	 * the value it either retrieves the nessessary messages from the plugin or
	 * removes messages from the tool representation.
	 */
	public void stateChanged(ChangeEvent e)
	{

		String display = "Message " + tool.messagelist.size() + " / " + tool.plugin.getMessageList().size();
		label.setText(display);

		// add all agents to the tool if there are non
		if(tool.componentlist.size() == 0)
		{
			tool.componentlist.addAll(SUtil.arrayToList(tool.getPlugin().getAgents()));
			tool.componentsChanged((Component[])tool.componentlist.toArray(new Component[tool.componentlist.size()]));
		}

		List messages = tool.plugin.getMessageList().getList();
		if((tool.messagelist.size() - slider.getValue() < 0))
		{
			List list = messages.subList(tool.messagelist.size(), slider.getValue());
			tool.messagelist.addAll(list);
			tool.messagesChanged((Message[])list.toArray(new Message[list.size()]));
		}
		
		if((tool.messagelist.size() - slider.getValue() > 0))
		{
			List list = messages.subList(slider.getValue(), tool.messagelist.size());
			// messagelist.removeAll(list);
			tool.messagesRemoved((Message[])list.toArray(new Message[list.size()]));

		}
	}
}