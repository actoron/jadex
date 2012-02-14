package sodekovs.mapscollision;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

//import de.agent.goku.Joystick;

/**
 * 
 * @author wolf.posdorfer
 *
 */
public class Main
{

//	public static Joystick _joystick = null;

	public static void main(String[] args) throws Exception
	{
		createLargePicture();
//		jadex.base.Starter.main(args);
	}

	/**
	 * Scales the 100x100 map.png to a 1000x1000
	 */
	private static void createLargePicture()
	{
		try
		{
			File infile = new File("./src/main/java/sodekovs/mapscollision/Untitled.png");
			BufferedImage im = ImageIO.read(infile);
			ImageIcon imageIcon = new ImageIcon(im.getScaledInstance(1000, 1000,
					BufferedImage.SCALE_FAST));

			Image image = imageIcon.getImage();
			String name = infile.getName().split("\\.")[0] + "2."
					+ infile.getName().split("\\.")[1];

			File f = new File("./src/main/java/sodekovs/mapscollision/" + name);

			RenderedImage rendered = null;
			if (image instanceof RenderedImage)
			{
				rendered = (RenderedImage) image;
			}
			else
			{
				BufferedImage buffered = new BufferedImage(imageIcon.getIconWidth(),
						imageIcon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g = buffered.createGraphics();
				g.drawImage(image, 0, 0, null);
				g.dispose();
				rendered = buffered;
			}
			ImageIO.write(rendered, "png", f);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

}
