package jadex.wfms.gui.images;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class SImage
{
	public static final String IMAGE_PATH = SImage.class.getPackage().getName().replaceAll("\\.", "/") + "/";
	
	public static ImageIcon createImageIcon(String imagename)
	{
		try
		{
			BufferedImage image = ImageIO.read(SImage.class.getResource("/" + SImage.IMAGE_PATH + imagename));
			return new ImageIcon(image);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
