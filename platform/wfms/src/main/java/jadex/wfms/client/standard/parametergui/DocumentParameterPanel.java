package jadex.wfms.client.standard.parametergui;

import jadex.wfms.gui.images.SImage;
import jadex.wfms.guicomponents.SGuiHelper;
import jadex.wfms.parametertypes.Document;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class DocumentParameterPanel extends AbstractParameterPanel
{
	private static final String SAVE_BUTTON_TIP = "Save Document";
	
	private static final String ATTACH_BUTTON_TIP = "Attach Document";
	
	private static final String NO_DOCUMENT_TEXT = "(No Document)";
	
	private static final Set IMAGE_EXTENSIONS = new HashSet(Arrays.asList(new Object[] {"jpg", "jpeg", "gif", "png", "bmp", "tiff"}));
	
	private static final int MAX_IMAGE_PREVIEW_SIZE = 256;
	
	private Component docView;
	
	private Document document;
	
	private JPanel mainPanel;
	
	public DocumentParameterPanel(String parameterName, Document initialValue, Map metaProperties, boolean readOnly)
	{
		super(parameterName, readOnly);
		
		document = initialValue;
		
		mainPanel = new JPanel(new GridBagLayout());
		
		String borderTitle = (String) metaProperties.get("short_description");
		if (borderTitle == null)
			borderTitle = SGuiHelper.beautifyName(parameterName);
		TitledBorder border = new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED));
		border.setTitle(borderTitle);
		
		mainPanel.setBorder(border);
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.CENTER;
		add(mainPanel, g);
		
		setDocView();
		
		int x = 0;
		
		final JLabel fileLabel = new JLabel();
		if (document == null)
			fileLabel.setText(NO_DOCUMENT_TEXT);
		else
			fileLabel.setText(document.getFileName());
		fileLabel.setHorizontalAlignment(SwingConstants.CENTER);
		g = new GridBagConstraints();
		g.gridx = x++;
		g.gridy = 1;
		g.anchor = GridBagConstraints.WEST;
		mainPanel.add(fileLabel, g);
		
		JPanel filler = new JPanel();
		g.gridx = x++;
		g.gridy = 1;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.anchor = GridBagConstraints.CENTER;
		mainPanel.add(filler, g);
		
		if ((readOnly) || (initialValue != null))
		{
			JButton saveButton = new JButton();
			AbstractAction saveAction = new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					JFileChooser fc = new JFileChooser();
					fc.setSelectedFile(new File(document.getFileName()));
					if (fc.showSaveDialog(DocumentParameterPanel.this) == JFileChooser.APPROVE_OPTION)
					{
						File docFile = fc.getSelectedFile();
						try
						{
							(new FileOutputStream(docFile)).write(document.decodeContent());
						}
						catch (IOException e1)
						{
							JOptionPane.showMessageDialog(DocumentParameterPanel.this, "File write failed.");
						}
					}
				}
			};
			saveButton.setAction(saveAction);
			saveButton.setIcon(SImage.createImageIcon("disk_small.png"));
			saveButton.setToolTipText(SAVE_BUTTON_TIP);
			saveButton.setMargin(new Insets(1,1,1,1));
			g = new GridBagConstraints();
			g.gridx = x++;
			g.gridy = 1;
			g.fill = GridBagConstraints.NONE;
			g.insets = new Insets(5, 5, 5, 5);
			g.anchor = GridBagConstraints.EAST;
			mainPanel.add(saveButton, g);
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
			attachButton.setIcon(SImage.createImageIcon("paperclip_small.png"));
			attachButton.setMargin(new Insets(1,1,1,1));
			attachButton.setToolTipText(ATTACH_BUTTON_TIP);
			g = new GridBagConstraints();
			g.gridx = x;
			g.gridy = 1;
			g.fill = GridBagConstraints.NONE;
			g.insets = new Insets(0, 5, 0, 5);
			g.anchor = GridBagConstraints.EAST;
			mainPanel.add(attachButton, g);
		}
		
	}
	
	public boolean isParameterValueValid()
	{
		return (document != null);
	}
	
	public boolean requiresLabel()
	{
		return false;
	}
	
	public Object getParameterValue()
	{
		return document;
	}
	
	private void setDocView()
	{
		if (document != null)
		{
			if (docView != null)
				mainPanel.remove(docView);
			GridBagConstraints g = new GridBagConstraints();
			g.gridx = 0;
			g.gridy = 0;
			g.gridwidth = GridBagConstraints.REMAINDER;
			g.weightx = 1;
			g.insets = new Insets(5, 5, 5, 5);
			g.anchor = GridBagConstraints.CENTER;
			
			String fn = document.getFileName();
			
			if (IMAGE_EXTENSIONS.contains(fn.substring(fn.length() - 3)) ||
				IMAGE_EXTENSIONS.contains(fn.substring(fn.length() - 4)))
			{
				if (docView != null)
					remove(docView);
				JLabel imageLabel = new JLabel();
				
				BufferedImage tmpImage = null;
				try
				{
					tmpImage = ImageIO.read(new ByteArrayInputStream(document.decodeContent()));
				}
				catch (IOException e)
				{
				}
				if (tmpImage == null)
					return;
				Image image = tmpImage;
				if (Math.max(tmpImage.getWidth(), tmpImage.getHeight()) > MAX_IMAGE_PREVIEW_SIZE)
				{
					double factor = MAX_IMAGE_PREVIEW_SIZE;
					if (tmpImage.getWidth() > tmpImage.getHeight())
						factor /= tmpImage.getWidth();
					else
						factor /= tmpImage.getHeight();
					image = tmpImage.getScaledInstance((int) (tmpImage.getWidth() * factor), (int) (tmpImage.getHeight() * factor), Image.SCALE_SMOOTH); 
				}
				tmpImage = null;
				
				ImageIcon icon = new ImageIcon(image);
				imageLabel.setIcon(icon);
				docView = imageLabel;
				mainPanel.add(docView, g);
			}
		}
	}
}
