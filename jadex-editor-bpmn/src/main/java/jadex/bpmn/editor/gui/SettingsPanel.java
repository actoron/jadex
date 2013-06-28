package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.BpmnEditor;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;

public class SettingsPanel extends JPanel
{
	/** Maximum search depth for developer project search. */
	protected static final int MAX_DEV_SEARCH_DEPTH = 3; 
	
	/** The library entry table. */
	protected JTable libentrytable;
	
	/** The new library entries. */
	protected List<File> libentries;
	
	/** Look and feel box. */
	protected JComboBox<String> lookandfeelbox;
	
	/** Smooth zoom box. */
	protected JCheckBox szbox;
	
	/** Sequence edge enabled box. */
	protected JCheckBox sebox;
	
	/** Direct sequence edge auto box. */
	protected JCheckBox dsbox;
	
	/** Data edge enabled box. */
	protected JCheckBox debox;
	
	/** Name/Type data edge box. */
	protected JCheckBox ntbox;
	
	/** The settings */
	protected Settings settings;
	
	public SettingsPanel(Settings settings)
	{
		super(new GridBagLayout());
		this.libentries = new ArrayList<File>();
		if (settings.getLibraryEntries() != null)
		{
			libentries.addAll(Arrays.asList(settings.getLibraryEntries()));
		}
		
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
		
		final AbstractAction changeaction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				SettingsPanel.this.firePropertyChange(OptionDialog.OPTIONS_CHANGED_PROPERTY, null, null);
			}
		};
		
		libentrytable = new JTable(new ClassPathTableModel());
		
		JButton pathbutton = new JButton(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				File last = libentries.size() > 0? libentries.get(libentries.size() - 1) : null;
				BetterFileChooser fc = new BetterFileChooser(last);
				fc.setDialogType(BetterFileChooser.OPEN_DIALOG);
				fc.setFileSelectionMode(BetterFileChooser.FILES_AND_DIRECTORIES);
				FileFilter filter = new FileNameExtensionFilter("BPMN model file", "bpmn2");
				fc.addChoosableFileFilter(filter);
				fc.setFileFilter(filter);
				
				int state = fc.showOpenDialog(SettingsPanel.this);
				
				if (state == BetterFileChooser.APPROVE_OPTION)
				{
					File path = fc.getSelectedFile();
					if (!libentries.contains(path))
					{
						int pos = libentries.size();
						libentries.add(path);
						((ClassPathTableModel) libentrytable.getModel()).fireTableRowsInserted(pos, pos);
					}
					changeaction.actionPerformed(e);
				}
			}
		});
		pathbutton.setToolTipText("Add Path");
		Icon[] icons = ImageProvider.getInstance().generateGenericFlatImageIconSet(32, ImageProvider.EMPTY_FRAME_TYPE, "folder", Color.BLACK);
		pathbutton.setIcon(icons[0]);
		pathbutton.setPressedIcon(icons[1]);
		pathbutton.setRolloverIcon(icons[2]);
		pathbutton.setContentAreaFilled(false);
		pathbutton.setBorder(new EmptyBorder(0, 0, 0, 0));
		pathbutton.setMargin(new Insets(0, 0, 0, 0));
		
		JButton projectbutton = new JButton(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				File last = libentries.size() > 0? libentries.get(libentries.size() - 1) : null;
				BetterFileChooser fc = new BetterFileChooser(last);
				fc.setDialogType(BetterFileChooser.OPEN_DIALOG);
				fc.setFileSelectionMode(BetterFileChooser.DIRECTORIES_ONLY);
				
				int state = fc.showOpenDialog(SettingsPanel.this);
				
				if (state == BetterFileChooser.APPROVE_OPTION)
				{
					File libhome = fc.getSelectedFile();
					
					File libdir = new File(libhome.getAbsolutePath() + File.separator + "lib");
					if (!libdir.exists() || !libdir.isDirectory())
					{
						libdir = libhome;
					}
					
					Set<File> entries = new HashSet<File>();
					File[] files = libdir.listFiles();
					if (files != null)
					{
						for (File file : files)
						{
							if (file.getAbsolutePath().endsWith(".jar"))
							{
								entries.add(file);
							}
						}
					}
					
					int depth = MAX_DEV_SEARCH_DEPTH;
					List<File> libdirs = new ArrayList<File>();
					libdirs.add(libdir);
					while (entries.isEmpty() && depth > 0 && libdirs.size() > 0)
					{
						--depth;
						for (File ldir : libdirs)
						{
							searchDevMode(entries, ldir);
						}
						
						if (entries.isEmpty() && depth > 0)
						{
							List<File> newlibdirs = new ArrayList<File>();
							for (File ldir : libdirs)
							{
								File[] dirfiles = ldir.listFiles();
								for (File dirfile : dirfiles)
								{
									if (dirfile.isDirectory())
									{
										newlibdirs.add(dirfile);
									}
								}
							}
							libdirs = newlibdirs;
						}
					}
					
					if (entries.size() > 0)
					{
						entries.removeAll(libentries);
						int start = libentries.size();
						libentries.addAll(entries);
						if (start < libentries.size())
						{
							((ClassPathTableModel) libentrytable.getModel()).fireTableRowsInserted(start, libentries.size() - 1);
							changeaction.actionPerformed(e);
						}
					}
					else
					{
						JOptionPane.showMessageDialog(getParent(),
							    "No classpath was found in this project folder.",
							    "Project classpaths not found",
							    JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		});
		icons = ImageProvider.getInstance().generateGenericFlatImageIconSet(32, ImageProvider.EMPTY_FRAME_TYPE, "user-home", Color.BLACK);
		projectbutton.setToolTipText("Add Project");
		projectbutton.setIcon(icons[0]);
		projectbutton.setPressedIcon(icons[1]);
		projectbutton.setRolloverIcon(icons[2]);
		projectbutton.setContentAreaFilled(false);
		projectbutton.setBorder(new EmptyBorder(0, 0, 0, 0));
		projectbutton.setMargin(new Insets(0, 0, 0, 0));
		
		JButton removebutton = new JButton(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] ind = libentrytable.getSelectedRows();
				Arrays.sort(ind);
				
				for (int i = ind.length - 1; i >= 0; --i)
				{
					libentries.remove(ind[i]);
					((ClassPathTableModel) libentrytable.getModel()).fireTableRowsDeleted(ind[i], ind[i]);
				}
				
				if (ind.length > 0)
				{
					changeaction.actionPerformed(e);
				}
			}
		});
		icons = ImageProvider.getInstance().generateGenericFlatImageIconSet(32, ImageProvider.EMPTY_FRAME_TYPE, "folder-forbidden", Color.BLACK);
		removebutton.setToolTipText("Remove Paths");
		removebutton.setIcon(icons[0]);
		removebutton.setPressedIcon(icons[1]);
		removebutton.setRolloverIcon(icons[2]);
		removebutton.setContentAreaFilled(false);
		removebutton.setBorder(new EmptyBorder(0, 0, 0, 0));
		removebutton.setMargin(new Insets(0, 0, 0, 0));
		
		JButton clearbutton = new JButton(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				libentries.clear();
				((ClassPathTableModel) libentrytable.getModel()).fireTableStructureChanged();
				changeaction.actionPerformed(e);
			}
		});
		icons = ImageProvider.getInstance().generateGenericFlatImageIconSet(32, ImageProvider.EMPTY_FRAME_TYPE, "user-trash", Color.BLACK);
		clearbutton.setToolTipText("Clear");
		clearbutton.setIcon(icons[0]);
		clearbutton.setPressedIcon(icons[1]);
		clearbutton.setRolloverIcon(icons[2]);
		clearbutton.setContentAreaFilled(false);
		clearbutton.setBorder(new EmptyBorder(0, 0, 0, 0));
		clearbutton.setMargin(new Insets(0, 0, 0, 0));
		
		JPanel cppanel = new JPanel(new GridBagLayout());
		cppanel.setBorder(new TitledBorder("Class Path Settings"));
		tabpane.addTab("Class Path", cppanel);
		
		g = new GridBagConstraints();
		g.gridheight = 5;
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		cppanel.add(new JScrollPane(libentrytable), g);
		
		g = new GridBagConstraints();
		g.gridx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.insets = GuiConstants.DEFAULT_BUTTON_INSETS;
		cppanel.add(pathbutton, g);
		
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.insets = GuiConstants.DEFAULT_BUTTON_INSETS;
		cppanel.add(projectbutton, g);
		
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 2;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.insets = GuiConstants.DEFAULT_BUTTON_INSETS;
		cppanel.add(removebutton, g);
		
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 3;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.insets = GuiConstants.DEFAULT_BUTTON_INSETS;
		cppanel.add(clearbutton, g);
		
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 4;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.VERTICAL;
		cppanel.add(new JPanel(), g);
		
		lookandfeelbox = new JComboBox<String>(BpmnEditor.LOOK_AND_FEELS.keySet().toArray(new String[0]));
		lookandfeelbox.setEditable(false);
		lookandfeelbox.setSelectedItem(settings.getLfName());
		lookandfeelbox.addActionListener(changeaction);
		g = new GridBagConstraints();
		g.anchor = GridBagConstraints.WEST;
		generalpanel.add(lookandfeelbox, g);
		
		szbox = new JCheckBox(changeaction);
		szbox.setText("Smooth Zoom");
		szbox.setSelected(settings.isSmoothZoom());
		g = new GridBagConstraints();
		g.anchor = GridBagConstraints.WEST;
		g.gridy = 1;
		generalpanel.add(szbox, g);
		
		g = new GridBagConstraints();
		g.gridwidth = 2;
		g.gridy = 1;
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		generalpanel.add(new JPanel(), g);
		
//		g = new GridBagConstraints();
//		g.gridy = 5;
//		g.gridwidth = GridBagConstraints.REMAINDER;
//		g.weightx = 1.0;
//		g.weighty = 1.0;
//		g.fill = GridBagConstraints.BOTH;
//		generalpanel.add(new JPanel(), g);
		
		JPanel edgepanel = new JPanel(new GridBagLayout());
		edgepanel.setBorder(new TitledBorder("Edge Settings"));
		tabpane.addTab("Data Edge Settings", edgepanel);
		
		sebox = new JCheckBox(changeaction);
		sebox.setText("Enable sequence edges");
		sebox.setToolTipText("Enable sequence edges.");
		sebox.setSelected(settings.isSequenceEdges());
		g = new GridBagConstraints();
		g.anchor = GridBagConstraints.WEST;
		edgepanel.add(sebox, g);
		
		dsbox = new JCheckBox(changeaction);
		dsbox.setText("Generate missing sequence edge on data connections");
		dsbox.setToolTipText("Generate missing sequence edge on data connections.");
		dsbox.setSelected(settings.isDirectSequenceAutoConnect());
		g = new GridBagConstraints();
		g.gridy = 1;
		g.anchor = GridBagConstraints.WEST;
		edgepanel.add(dsbox, g);
		
		debox = new JCheckBox(changeaction);
		debox.setText("Enable data edges");
		debox.setToolTipText("Enable data edges.");
		debox.setSelected(settings.isDataEdges());
		g = new GridBagConstraints();
		g.gridy = 2;
		g.anchor = GridBagConstraints.WEST;
		edgepanel.add(debox, g);
		
		ntbox = new JCheckBox(changeaction);
		ntbox.setText("Generate data edge for matching name and type");
		ntbox.setToolTipText("Generate data edge if following task has parameter of matching name and type.");
		ntbox.setSelected(settings.isNameTypeDataAutoConnect());
		g = new GridBagConstraints();
		g.gridy = 3;
		g.anchor = GridBagConstraints.WEST;
		edgepanel.add(ntbox, g);
		
		g = new GridBagConstraints();
		g.gridy = 4;
		g.gridwidth = GridBagConstraints.REMAINDER;
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		edgepanel.add(new JPanel(), g);
	}
	
	/**
	 *  Applies the settings.
	 */
	public boolean applySettings()
	{
		boolean ret = false;
//		String pf = libpathfield.getText();
//		pf = pf != null && pf.length() == 0 ? null : pf;
//		if ((pf != null && settings.getLibraryHome() == null) ||
//			(pf == null && settings.getLibraryHome() != null) ||
//			(pf != null && !libpathfield.getText().equals(settings.getLibraryHome().getPath())))
		Set<File> orig = new HashSet<File>();
		Set<File> cand = new HashSet<File>(libentries);
		if (settings.getLibraryEntries() != null)
		{
			orig.addAll(Arrays.asList(settings.getLibraryEntries()));
		}
		
		if (!cand.equals(orig))
		{
			settings.setLibraryEntries(libentries);
			settings.scanForClasses();
			ret = true;
		}
		settings.setSmoothZoom(szbox.isSelected());
		settings.setDirectSequenceAutoConnect(dsbox.isSelected());
		settings.setSequenceEdges(sebox.isSelected());
		settings.setNameTypeDataAutoConnect(ntbox.isSelected());
		settings.setDataEdges(debox.isSelected());
		
		if (lookandfeelbox.getSelectedItem() != null &&
			!settings.getLfName().equals(lookandfeelbox.getSelectedItem()) &&
			BpmnEditor.LOOK_AND_FEELS.containsKey(lookandfeelbox.getSelectedItem()))
		{
			try
			{
				Object odf = UIManager.getDefaults().get("defaultFont");
				UIManager.setLookAndFeel(BpmnEditor.LOOK_AND_FEELS.get(lookandfeelbox.getSelectedItem()).getClassName());
				if (UIManager.getDefaults().get("defaultFont") == null)
				{
					// Swing bug?
					UIManager.getDefaults().put("defaultFont", odf);
				}
				Container c = getParent();
				while (!(c instanceof BpmnEditorWindow))
				{
					c = c.getParent();
				}
				SwingUtilities.updateComponentTreeUI(c);
				settings.setLfName((String) lookandfeelbox.getSelectedItem());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected void searchDevMode(Set<File> entries, File libdir)
	{
		// Attempt developer-mode search.
		File[] dirs = libdir.listFiles();
		for (File dir : dirs)
		{
			if (dir.isDirectory())
			{
				File targetdir = new File(dir.getAbsolutePath() + File.separator + "target" + File.separator + "classes");
				if (targetdir.exists() && targetdir.isDirectory())
				{
					entries.add(targetdir);
				}
			}
		}
	}
	
	/**
	 *
	 */
	protected class ClassPathTableModel extends AbstractTableModel
	{
		/**
		 *  Gets the column name.
		 *  
		 *  @return The column name.
		 */
		public String getColumnName(int column)
		{
			return "Class Path";
		}
		
		public int getRowCount()
		{
			return libentries.size();
		}
		
		public int getColumnCount()
		{
			return 1;
		}
		
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return false;
		}
		
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return libentries.get(rowIndex).getPath();
		}
	}
}
