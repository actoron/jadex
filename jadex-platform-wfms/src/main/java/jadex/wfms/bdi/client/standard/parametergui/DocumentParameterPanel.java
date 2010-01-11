package jadex.wfms.bdi.client.standard.parametergui;

import jadex.wfms.parametertypes.Document;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public class DocumentParameterPanel extends AbstractParameterPanel
{
	private static final String SAVE_BUTTON_TIP = "Save Document";
	
	private static final String ATTACH_BUTTON_TIP = "Attach Document";
	
	private static final String NO_DOCUMENT_TEXT = "(No Document)";
	
	private static final Set IMAGE_EXTENSIONS = new HashSet(Arrays.asList(new Object[] {"jpg", "jpeg", "gif", "png", "bmp", "tiff"}));
	
	private Component docView;
	
	private Document document;
	
	public DocumentParameterPanel(String parameterName, Document initialValue, boolean readOnly)
	{
		super(parameterName, readOnly);
		
		document = initialValue;
		
		setDocView();
		
		final JLabel fileLabel = new JLabel();
		if (document == null)
			fileLabel.setText(NO_DOCUMENT_TEXT);
		else
			fileLabel.setText(document.getFileName());
		fileLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 1;
		//g.gridwidth = 2;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.WEST;
		add(fileLabel, g);
		
		int x = 1;
		
		if ((readOnly) || (initialValue != null))
		{
			JButton saveButton = new JButton();
			AbstractAction saveAction = new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					JFileChooser fc = new JFileChooser();
					if (fc.showSaveDialog(DocumentParameterPanel.this) == JFileChooser.APPROVE_OPTION)
					{
						File docFile = fc.getSelectedFile();
						try
						{
							(new FileOutputStream(docFile)).write(document.retrieveContent());
						}
						catch (IOException e1)
						{
							JOptionPane.showMessageDialog(DocumentParameterPanel.this, "File write failed.");
						}
					}
				}
			};
			saveButton.setAction(saveAction);
			saveButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource(getClass().getPackage().getName().replaceFirst("parametergui", "images").replaceAll("\\.", "/") + "/disk_small.png")));
			saveButton.setToolTipText(SAVE_BUTTON_TIP);
			saveButton.setMargin(new Insets(1,1,1,1));
			g = new GridBagConstraints();
			g.gridx = x++;
			g.gridy = 1;
			g.fill = GridBagConstraints.NONE;
			g.insets = new Insets(5, 5, 5, 5);
			g.anchor = GridBagConstraints.WEST;
			add(saveButton, g);
		}
		
		if (!readOnly)
		{
			JButton attachButton = new JButton();
			attachButton.setAction(new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					JFileChooser fc = new JFileChooser();
					if (fc.showOpenDialog(DocumentParameterPanel.this) == JFileChooser.APPROVE_OPTION)
					{
						File docFile = fc.getSelectedFile();
						try
						{
							document = new Document(docFile);
							fileLabel.setText(document.getFileName());
							setDocView();
						}
						catch (IOException e1)
						{
							JOptionPane.showMessageDialog(DocumentParameterPanel.this, "Document read failed.");
						}
					}
				}
			});
			attachButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource(getClass().getPackage().getName().replaceFirst("parametergui", "images").replaceAll("\\.", "/") + "/paperclip_small.png")));
			attachButton.setMargin(new Insets(1,1,1,1));
			attachButton.setToolTipText(ATTACH_BUTTON_TIP);
			g = new GridBagConstraints();
			g.gridx = x;
			g.gridy = 1;
			g.fill = GridBagConstraints.NONE;
			g.insets = new Insets(0, 5, 0, 5);
			g.anchor = GridBagConstraints.WEST;
			add(attachButton, g);
		}
		
	}
	
	public boolean isParameterValueValid()
	{
		return (document != null);
	}
	
	public boolean requiresLabel()
	{
		return true;
	}
	
	public Object getParameterValue()
	{
		return document;
	}
	
	private void setDocView()
	{
		if (document != null)
		{
			GridBagConstraints g = new GridBagConstraints();
			g.gridx = 0;
			g.gridy = 0;
			g.gridwidth = GridBagConstraints.REMAINDER;
			g.weightx = 1;
			g.anchor = GridBagConstraints.CENTER;
			
			String fn = document.getFileName();
			if (IMAGE_EXTENSIONS.contains(fn.substring(fn.length() - 3)) ||
				IMAGE_EXTENSIONS.contains(fn.substring(fn.length() - 4)))
			{
				if (docView != null)
					remove(docView);
				JLabel imageLabel = new JLabel();
				ImageIcon icon = new ImageIcon(document.retrieveContent());
				imageLabel.setIcon(icon);
				docView = imageLabel;
				add(docView, g);
			}
		}
	}
}
