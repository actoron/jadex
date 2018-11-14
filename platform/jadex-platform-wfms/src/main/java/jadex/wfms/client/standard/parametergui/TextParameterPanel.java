package jadex.wfms.client.standard.parametergui;

import jadex.wfms.guicomponents.SGuiHelper;
import jadex.wfms.parametertypes.Text;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class TextParameterPanel extends AbstractParameterPanel
{
	private JEditorPane editorPane;
	
	private Text parameterValue;
	
	public TextParameterPanel(String parameterName, Text initialValue, Map metaProperties, boolean readOnly)
	{
		super(parameterName, readOnly);
		
		parameterValue = initialValue;
		if (parameterValue == null)
			parameterValue = new Text();
		
		if (((!readOnly) && (!Boolean.FALSE.equals(metaProperties.get("border")))) ||
			((readOnly) && (Boolean.TRUE.equals(metaProperties.get("border")))))
		{
			String borderTitle = (String) metaProperties.get("short_description");
			if (borderTitle == null)
				borderTitle = SGuiHelper.beautifyName(parameterName);
			setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), borderTitle));
		}
		
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.weightx = 1;
		
		String type;
		if (readOnly)
			type = "text/html";
		else
			type = "text/plain";
		
		editorPane = new JEditorPane(type, parameterValue.getText())
		{
			public Dimension getPreferredSize()
			{
				Dimension d = super.getPreferredSize();
				if (d.getHeight() < 72)
					d.setSize(d.getWidth(), 72);
				return d;
			}
		};
		editorPane.setEditable(!readOnly);
		add(editorPane, g);
	}
	
	public boolean isParameterValueValid()
	{
		return true;
	}
	
	public boolean requiresLabel()
	{
		return false;
	}
	
	public Object getParameterValue()
	{
		parameterValue.setText(editorPane.getText());
		return parameterValue;
	}
}
