package ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

import feature.ColourHistogram;
import feature.EdgeHistogram;

public class EHistPanel extends JPanel {
	private ArrayList<ArrayList<List<Integer>>> EHistList;
	private Map<String, ArrayList<Integer>> EHistListDict = new HashMap<String,ArrayList<Integer>>();
	int counter;
	
	EdgeHistogram eHist;
	
	public EHistPanel(){
		counter = 0;
		JButton compute = new JButton("Compute Colour Histograms!");
		eHist = new EdgeHistogram();
		EHistList = new ArrayList<ArrayList<List<Integer>>>();
		createEHistograms();
	}
	
	public ArrayList<ArrayList<List<Integer>>> getEHistList() {
		return EHistList;
	}

	public void setEHistList(ArrayList<ArrayList<List<Integer>>> eHistList) {
		EHistList = eHistList;
	}

	public ArrayList<ArrayList<List<Integer>>> createEHistograms(){
		
		try (Stream<Path> paths = Files.walk(Paths.get("../cbir/101_ObjectCategories/"))) {
		      //paths.forEach(System.out::println);
			paths.parallel()
            .filter((p) -> !p.toFile().isDirectory())
            .forEach(p -> computeHist(p));
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		
		return EHistList;
	}

	private void computeHist(Path p) {
		BufferedImage image  = null;
		try {
			image = ImageIO.read(p.toFile());
			System.out.println(counter);
			//EHistList.add(eHist.getHistogram(image));
			EHistListDict.put(p.getParent().toString() +"\\"+ p.getFileName().toString(),(ArrayList<Integer>) Arrays.stream( eHist.getHistogram(image) ).boxed().collect( Collectors.toList()));
			counter ++;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Map<String, ArrayList<Integer>> getEHistListDict() {
		return EHistListDict;
	}

	public void setEHistListDict(Map<String, ArrayList<Integer>> eHistListDict) {
		EHistListDict = eHistListDict;
	}


}
