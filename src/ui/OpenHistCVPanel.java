package ui;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
		
		//MatOfFloat ranges = new MatOfFloat(0f, 255f);
		//MatOfInt histSize = new MatOfInt(32);
		/// Convert to HSV
	      Imgproc.cvtColor(toCv(img1), hist0, Imgproc.COLOR_BGR2HSV);
	      Imgproc.cvtColor(toCv(img2), hist1, Imgproc.COLOR_BGR2HSV);

	      /// Using 50 bins for hue and 60 for saturation
	      int hBins = 50;
	      int sBins = 60;
	      MatOfInt histSize = new MatOfInt( hBins,  sBins);

	      // hue varies from 0 to 179, saturation from 0 to 255
	      MatOfFloat ranges =  new MatOfFloat( 0f,180f,0f,256f );

	      // we compute the histogram from the 0-th and 1-st channels
	      MatOfInt channels = new MatOfInt(0, 1);
	      
	      Mat histRef = new Mat();
	      Mat histSource = new Mat();

	      ArrayList<Mat> histImages=new ArrayList<Mat>();
	      histImages.add(hist0);
	      Imgproc.calcHist(histImages,
	              channels,
	              new Mat(),
	              histRef,
	              histSize,
	              ranges,
	              false);
	      Core.normalize(histRef,
	              histRef,
	              0,
	              1,
	              Core.NORM_MINMAX,
	              -1,
	              new Mat());

	      histImages=new ArrayList<Mat>();
	      histImages.add(hist1);
	      Imgproc.calcHist(histImages,
	              channels,
	              new Mat(),
	              histSource,
	              histSize,
	              ranges,
	              false);
	      Core.normalize(histSource,
	              histSource,
	              0,
	              1,
	              Core.NORM_MINMAX,
	              -1,
	              new Mat());


		
		//Imgproc.calcHist(Arrays.asList(toCv(img1)), new MatOfInt(0), new Mat(), hist0, histSize, ranges);
		//Imgproc.calcHist(Arrays.asList(toCv(img2)), new MatOfInt(0), new Mat(), hist1, histSize, ranges);
		return Imgproc.compareHist(histRef, histSource, CVtype);
	}
	
	public static Mat toCv(BufferedImage img){

		BufferedImage image = img;
		byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, data);
		return mat;
	}
	
}
