package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.gui.propertypanels.DocumentAdapter;
import jadex.bridge.ClassInfo;
import jadex.commons.SReflect;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;

public class SettingsPanel extends JPanel
{
	/** The library path field. */
	protected JTextField libpathfield;
	
	/** Smooth zoom box. */
	protected JCheckBox szbox;
	
	/** Data edge enabled box. */
	protected JCheckBox debox;
	
	/** Name/Type data edge box. */
	protected JCheckBox ntbox;
	
	/** The global cache. */
	protected GlobalCache globalcache;
	
	/** The settings */
	protected Settings settings;
	
	public SettingsPanel(GlobalCache globalcache, Settings settings)
	{
		super(new GridBagLayout());
		this.globalcache = globalcache;
		
		JTabbedPane tabpane = new JTabbedPane();
		tabpane.setBorder(new EmptyBorder(10, 5, 10, 5));
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
		g.insets = new Insets(0, 5, 0, 10);
		generalpanel.add(label, g);
		
		g = new GridBagConstraints();
		g.gridx = 1;
		g.weightx = 1.0;
		g.fill = GridBagConstraints.HORIZONTAL;
		generalpanel.add(libpathfield, g);
		
		g = new GridBagConstraints();
		g.gridx = 2;
		g.insets = new Insets(0, 10, 0, 5);
		generalpanel.add(button, g);
		
		AbstractAction changeaction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				SettingsPanel.this.firePropertyChange(OptionDialog.OPTIONS_CHANGED_PROPERTY, null, null);
			}
		};
		
		szbox = new JCheckBox(changeaction);
		szbox.setText("Smooth Zoom");
		szbox.setSelected(settings.isSmoothZoom());
		g = new GridBagConstraints();
		g.gridy = 1;
		generalpanel.add(szbox, g);
		
		g = new GridBagConstraints();
		g.gridy = 2;
		g.gridwidth = GridBagConstraints.REMAINDER;
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		generalpanel.add(new JPanel(), g);
		
		JPanel dataedgepanel = new JPanel(new GridBagLayout());
		dataedgepanel.setBorder(new TitledBorder("Data Edge Settings"));
		tabpane.addTab("Data Edge Settings", dataedgepanel);
		
		debox = new JCheckBox(changeaction);
		debox.setText("Enable data edges");
		debox.setToolTipText("Enable data edges.");
		debox.setSelected(settings.isDataEdges());
		g = new GridBagConstraints();
		g.anchor = GridBagConstraints.WEST;
		dataedgepanel.add(debox, g);
		
		ntbox = new JCheckBox(changeaction);
		ntbox.setText("Generate data edge for matching name and type");
		ntbox.setToolTipText("Generate data edge if following task has parameter of matching name and type.");
		ntbox.setSelected(settings.isNameTypeDataAutoConnect());
		g = new GridBagConstraints();
		g.gridy = 1;
		g.anchor = GridBagConstraints.WEST;
		dataedgepanel.add(ntbox, g);
		
		g = new GridBagConstraints();
		g.gridy = 2;
		g.gridwidth = GridBagConstraints.REMAINDER;
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		dataedgepanel.add(new JPanel(), g);
	}
	
	/**
	 *  Applies the settings.
	 */
	public void applySettings()
	{
		String pf = libpathfield.getText();
		pf = pf != null && pf.length() == 0 ? null : pf;
		if ((pf != null && settings.getLibraryHome() == null) ||
			(pf == null && settings.getLibraryHome() != null) ||
			(pf != null && !libpathfield.getText().equals(settings.getLibraryHome().getPath())))
		{
			if (pf == null)
			{
				settings.setLibraryHome(null);
			}
			else
			{
				settings.setLibraryHome(new File(pf));
			}
			Comparator<ClassInfo> comp = new Comparator<ClassInfo>()
			{
				public int compare(ClassInfo o1, ClassInfo o2)
				{
					String str1 = SReflect.getUnqualifiedTypeName(o1.toString());
					String str2 = SReflect.getUnqualifiedTypeName(o2.toString());
					return str1.compareTo(str2);
				}
			};
			Set<ClassInfo>[] tmp = GlobalCache.scanForClasses(settings.getHomeClassLoader());
			globalcache.getGlobalTaskClasses().addAll(tmp[0]);
			globalcache.getGlobalInterfaces().addAll(tmp[1]);
			Collections.sort(globalcache.getGlobalTaskClasses(), comp);
			Collections.sort(globalcache.getGlobalInterfaces(), comp);
			settings.setGlobalTaskClasses(globalcache.getGlobalTaskClasses());
			settings.setGlobalInterfaces(globalcache.getGlobalInterfaces());
			settings.setGlobalAllClasses(globalcache.getGlobalAllClasses());
		}
		settings.setSmoothZoom(szbox.isSelected());
		settings.setNameTypeDataAutoConnect(ntbox.isSelected());
		settings.setDataEdges(debox.isSelected());
	}
}
