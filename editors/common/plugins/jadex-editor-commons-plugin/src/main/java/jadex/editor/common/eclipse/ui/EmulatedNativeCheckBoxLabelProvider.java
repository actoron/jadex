package jadex.editor.common.eclipse.ui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Copied from: 
 * http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.jface.snippets/Eclipse%20JFace%20Snippets/org/eclipse/jface/snippets/viewers/Snippet061FakedNativeCellEditor.java?view=markup
 */
public abstract class EmulatedNativeCheckBoxLabelProvider extends
		OwnerDrawLabelProvider
{
	
	
	private static final String CHECKED_KEY = "CHECKED";
	private static final String UNCHECK_KEY = "UNCHECKED";
	
	public EmulatedNativeCheckBoxLabelProvider(ColumnViewer viewer)
	{
		if (JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY) == null)
		{
			JFaceResources.getImageRegistry().put(UNCHECK_KEY,
					makeShot(viewer.getControl(), false));
			JFaceResources.getImageRegistry().put(CHECKED_KEY,
					makeShot(viewer.getControl(), true));
		}
	}

	private Image makeShot(Control control, boolean type)
	{
		// Hopefully no platform uses exactly this color because we'll make
		// it transparent in the image.
		//Color greenScreen = new Color(control.getDisplay(), 222, 223, 224);
		Color greenScreen = new Color(control.getDisplay(), 111, 112, 113);

		Shell shell = new Shell(control.getShell(), SWT.NO_TRIM);

		// otherwise we have a default gray color
		shell.setBackground(greenScreen);

		if (Util.isMac())
		{
			Button button2 = new Button(shell, SWT.CHECK);
			Point bsize = button2.computeSize(SWT.DEFAULT, SWT.DEFAULT);

			// otherwise an image is stretched by width
			bsize.x = Math.max(bsize.x - 1, bsize.y - 1);
			bsize.y = Math.max(bsize.x - 1, bsize.y - 1);
			button2.setSize(bsize);
			button2.setLocation(100, 100);
		}

		
		
		Button button = new Button(shell, SWT.CHECK);
		button.setBackground(greenScreen);
		button.setSelection(type);


		// otherwise an image is located in a corner
		button.setLocation(1, 1);
		Point bsize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		// otherwise an image is stretched by width
		bsize.x = Math.max(bsize.x - 1, bsize.y - 1);
		bsize.y = Math.max(bsize.x - 1, bsize.y - 1);
		button.setSize(bsize);

		shell.setSize(bsize);

		shell.open();

		GC gc = new GC(shell);
		Image image = new Image(control.getDisplay(), bsize.x, bsize.y);
		gc.copyArea(image, 0, 0);
		gc.dispose();
		shell.close();

		ImageData imageData = image.getImageData();
		imageData.transparentPixel = imageData.palette.getPixel(greenScreen
				.getRGB());

		Image img = new Image(control.getDisplay(), imageData);
		image.dispose();

		return img;
	}

	public Image getImage(Object element)
	{
		if (isChecked(element))
		{
			return JFaceResources.getImageRegistry().get(CHECKED_KEY);
		}
		else
		{
			return JFaceResources.getImageRegistry().get(UNCHECK_KEY);
		}
	}

	protected void measure(Event event, Object element)
	{
		event.height = getImage(element).getBounds().height;
	}

	protected void paint(Event event, Object element)
	{

		Image img = getImage(element);

		if (img != null)
		{
			Rectangle bounds;

			if (event.item instanceof TableItem)
			{
				bounds = ((TableItem) event.item).getBounds(event.index);
			}
			else
			{
				bounds = ((TreeItem) event.item).getBounds(event.index);
			}

			Rectangle imgBounds = img.getBounds();
			bounds.width /= 2;
			bounds.width -= imgBounds.width / 2;
			bounds.height /= 2;
			bounds.height -= imgBounds.height / 2;

			int x = bounds.width > 0 ? bounds.x + bounds.width : bounds.x;
			int y = bounds.height > 0 ? bounds.y + bounds.height : bounds.y;

			if (SWT.getPlatform().equals("carbon"))
			{
				event.gc.drawImage(img, x + 2, y - 1);
			}
			else
			{
				event.gc.drawImage(img, x, y - 1);
			}

		}
	}

	protected abstract boolean isChecked(Object element);
}
