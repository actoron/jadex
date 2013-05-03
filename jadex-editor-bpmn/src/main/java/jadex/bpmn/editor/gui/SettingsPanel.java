package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.gui.propertypanels.DocumentAdapter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;

public class SettingsPanel extends JPanel
{
	/** The library path field. */
	protected JTextField libpathfield;
	
	/** The settings */
	protected Settings settings;
	
	public SettingsPanel(Settings settings)
	{
		super(new GridBagLayout());
		
		JTabbedPane tabpane = new JTabbedPane();
		GridBagConstraints g = new GridBagConstraints();
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		add(tabpane, g);
		
		JPanel generalpanel = new JPanel(new GridBagLayout());
		generalpanel.setBorder(new TitledBorder("General Settings"));
		tabpane.addTab("General", generalpanel);
		
		this.settings = settings;
		
		JLabel label = new JLabel("Jadex Home");
		libpathfield = new JTextField();
		if (settings.getLibraryHome() != null)
		{
			libpathfield.setText(settings.getLibraryHome().getPath());
		}
		libpathfield.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				SettingsPanel.this.firePropertyChange(OptionDialog.OPTIONS_CHANGED_PROPERTY, null, null);
			}
		});
		
		JButton button = new JButton(new AbstractAction("...")
		{
			public void actionPerformed(ActionEvent e)
			{
				File oldhome = new File(libpathfield.getText());
				BetterFileChooser fc = new BetterFileChooser(oldhome);
				fc.setDialogType(BetterFileChooser.OPEN_DIALOG);
				fc.setFileSelectionMode(BetterFileChooser.DIRECTORIES_ONLY);
				
				int state = fc.showOpenDialog(SettingsPanel.this);
				
				if (state == BetterFileChooser.APPROVE_OPTION)
				{
					libpathfield.setText(fc.getSelectedFile().getPath());
				}
			}
		});
		
		g = new GridBagConstraints();
		generalpanel.add(label, g);
		
		g = new GridBagConstraints();
		g.gridx = 1;
		g.weightx = 1.0;
		g.fill = GridBagConstraints.HORIZONTAL;
		generalpanel.add(libpathfield, g);
		
		g = new GridBagConstraints();
		g.gridx = 2;
		generalpanel.add(button, g);
		
		g = new GridBagConstraints();
		g.gridy = 1;
		g.gridwidth = GridBagConstraints.REMAINDER;
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		generalpanel.add(new JPanel(), g);
	}
	
	/**
	 *  Applies the settings.
	 */
	public void applySettings()
	{
		settings.setLibraryHome(new File(libpathfield.getText()));
	}
}
