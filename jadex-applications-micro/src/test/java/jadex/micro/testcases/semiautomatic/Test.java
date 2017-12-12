package jadex.micro.testcases.semiautomatic;

import java.io.File;

import org.jcodec.api.SequenceEncoder;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

/**
 * 
 */
public class Test
{
//	public static void main(String[] args)
//	{
//		Webcam webcam = Webcam.getDefault();
//		if(webcam!=null) 
//		{
//			System.out.println("Webcam: " + webcam.getName());
//
//			webcam.setViewSize(WebcamResolution.VGA.getSize());
//
//			WebcamPanel panel = new WebcamPanel(webcam, false);
//			panel.setFPS(10);
//
//			JFrame f = new JFrame();
//			f.add(panel, BorderLayout.CENTER);
//
//			if(webcam.isOpen()) 
//				webcam.close();
//			webcam.open();
//			panel.start();
//			
//			f.setLocation(SGUI.calculateMiddlePosition(f));
//			f.pack();
//			f.setVisible(true);
//		} 
//		else 
//		{
//			System.out.println("No webcam detected");
//		}
//	}
	
//	public static void main(String[] args) throws Throwable
//	{
//		File file = new File("output.ts");
//
//		IMediaWriter writer = ToolFactory.makeWriter(file.getName());
//		Dimension size = WebcamResolution.QVGA.getSize();
//
//		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);
//
//		Webcam webcam = Webcam.getDefault();
//		webcam.setViewSize(size);
//		webcam.open();
//
//		long start = System.currentTimeMillis();
//
//		for(int i = 0; i < 50; i++)
//		{
//			System.out.println("Capture frame " + i);
//
//			BufferedImage image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
//			IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
//
//			IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
//			frame.setKeyFrame(i == 0);
//			frame.setQuality(0);
//
//			writer.encodeVideo(0, frame);
//
//			// 10 FPS
//			Thread.sleep(100);
//		}
//
//		writer.close();
//
//		System.out.println("Video recorded in file: " + file.getAbsolutePath());
//	}
	
	public static void main(String[] args) throws Exception
	{
		Webcam webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		if(webcam.isOpen()) 
			webcam.close();
		webcam.open();
		
		SequenceEncoder enc = new SequenceEncoder(new File("out.ts"));
		
		for(int i=0; i<100; i++)
		{
			enc.encodeImage(webcam.getImage());
			try
			{
				Thread.sleep(100);
			}
			catch(Exception e)
			{
			}
		}
		enc.finish();
	}
}
