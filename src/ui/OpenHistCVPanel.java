package ui;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OpenHistCVPanel extends FeaturePanel{

	 private Map<String, BufferedImage> openCVMatrices;
	 
	public Map<String, BufferedImage> getOpenCVMatrices() {
		return openCVMatrices;
	}
	public void setOpenCVMatrices(Map<String, BufferedImage> openCVMatrices) {
		this.openCVMatrices = openCVMatrices;
	}
	public OpenHistCVPanel() {
		super();
		try {
			openCVMatrices = createEdgeHistograms();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private Map<String, BufferedImage> createEdgeHistograms() throws IOException {
        return Files.walk(Paths.get("101_ObjectCategories"))
                //paths.forEach(System.out::println);
                .parallel()
                .unordered()
                .peek(p -> System.out.println("opencv p = " + p))
                .filter(Files::isRegularFile)
                .filter(p -> noExceptionRead(p) != null)
                .collect(Collectors.toMap(
                        path -> path.toAbsolutePath().toString(),
                        path -> {
                        	return noExceptionRead(path);

                        }
                ));
    }
    private BufferedImage noExceptionRead(Path p) {
        try {
            return ImageIO.read(p.toFile());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
	public static double compareHistograms(BufferedImage img1, BufferedImage img2, int CVtype){
		Mat hist0 = new Mat();
		Mat hist1 = new Mat();
		
		int hist_bins = 30;           //number of histogram bins
		int hist_range[]= {0,180};//histogram range
		MatOfFloat ranges = new MatOfFloat(0f, 256f);
		MatOfInt histSize = new MatOfInt(25);
		
		Imgproc.calcHist(Arrays.asList(toCv(img1)), new MatOfInt(0), new Mat(), hist0, histSize, ranges);
		Imgproc.calcHist(Arrays.asList(toCv(img2)), new MatOfInt(0), new Mat(), hist1, histSize, ranges);
		return Imgproc.compareHist(hist0, hist1, CVtype);
	}
	
	public static Mat toCv(BufferedImage img){
//		BufferedImage image = img;
//		int[] matPixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
//		
//		ByteBuffer bb = ByteBuffer.allocate(matPixels.length * 4);
//		IntBuffer ib = bb.asIntBuffer();
//		ib.put(matPixels);
//		
//		byte[] bvals = bb.array();
//
//		m.put(0,0, bvals);
		BufferedImage image = img;
		byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, data);
		return mat;
	}
	
}
