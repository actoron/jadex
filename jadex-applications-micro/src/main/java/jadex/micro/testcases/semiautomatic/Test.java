package jadex.micro.testcases.semiautomatic;

import static org.jcodec.common.NIOUtils.writableFileChannel;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.encode.RateControl;
import org.jcodec.codecs.raw.V210Encoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.AWTUtil;
import org.jcodec.scale.RgbToYuv420;

import com.github.sarxos.webcam.Webcam;

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
		BufferedImage img = webcam.getImage();
		
		 // Create MP4 muxer
	    MP4Muxer muxer = new MP4Muxer(writableFileChannel(new File("out.ts")));
		FramesMP4MuxerTrack videoTrack = muxer.addVideoTrack("v210", new Size(img.getWidth(), img.getHeight()), "jcodec", 24000);
		
		imageToMP4(img, videoTrack);
		
		 // Write MP4 header and finalize recording
	    muxer.writeHeader();
	}
	
	public static void imageToMP4(BufferedImage bi, FramesMP4MuxerTrack track) throws Exception
	{
	    // A transform to convert RGB to YUV colorspace
	    RgbToYuv420 transform = new RgbToYuv420(0, 0);

	    // A JCodec native picture that would hold source image in YUV colorspace
	    Picture toEncode = Picture.create(bi.getWidth(), bi.getHeight(), ColorSpace.YUV420);

	    // Perform conversion
	    transform.transform(AWTUtil.fromBufferedImage(bi), toEncode);

	    
	    // Add a video track
//	    CompressedTrack outTrack = muxer.addTrackForCompressed(TrackType.VIDEO, 25);

	    // Create H.264 encoder
	    H264Encoder encoder = new H264Encoder();

	    // Allocate a buffer that would hold an encoded frame
	    ByteBuffer _out = ByteBuffer.allocate(bi.getWidth() * bi.getHeight() * 6);

	    // Allocate storage for SPS/PPS, they need to be stored separately in a special place of MP4 file
	    List<ByteBuffer> spsList = new ArrayList<ByteBuffer>();
	    List<ByteBuffer> ppsList = new ArrayList<ByteBuffer>();

	    // Encode image into H.264 frame, the result is stored in '_out' buffer
	    ByteBuffer result = encoder.encodeFrame(_out, toEncode);

	    // Based on the frame above form correct MP4 packet
	    H264Utils.encodeMOVPacket(result, spsList, ppsList);

	    // Add packet to video track
	    track.addFrame(new MP4Packet(result, 0, 25, 1, 0, true, null, 0, 0));

	    // Push saved SPS/PPS to a special storage in MP4
	    track.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList));
	}
}
