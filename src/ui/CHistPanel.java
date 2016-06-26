package ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

import feature.ColourHistogram;


public class CHistPanel extends JPanel {
	 	File folder;
	    File[] listOfFiles;
		private ArrayList<ArrayList<List<Integer>>> CHistList;
		private Map<String,ArrayList<List<Integer>>> CHistListDict = new HashMap<String,ArrayList<List<Integer>>>();
		int counter;
		
		ColourHistogram cHist;
		
		public CHistPanel(int bins){
			counter = 0;
			JButton compute = new JButton("Compute Colour Histograms!");
			cHist = new ColourHistogram(bins);
			CHistList = new ArrayList<ArrayList<List<Integer>>>();
			createHCHistograms();
		}
		
		public ArrayList<ArrayList<List<Integer>>> getCHistList() {
			return CHistList;
		}

		public void setCHistList(ArrayList<ArrayList<List<Integer>>> cHistList) {
			CHistList = cHistList;
		}

		public ArrayList<ArrayList<List<Integer>>> createHCHistograms(){
			
			folder = new File("F:/Eclipse/workspace/cbir/101_ObjectCategories/");
			listOfFiles= folder.listFiles();
			try (Stream<Path> paths = Files.walk(Paths.get("F:/Eclipse/workspace/cbir/101_ObjectCategories/"))) {
			      //paths.forEach(System.out::println);
				paths.parallel()
                .filter((p) -> !p.toFile().isDirectory())
                .forEach(p -> computeHist(p));
			    } catch (IOException e) {
			      e.printStackTrace();
			    }
			
			return CHistList;
		}

		private void computeHist(Path p) {
			BufferedImage image  = null;
			try {
				image = ImageIO.read(p.toFile());
				//System.out.println(counter+ "of "+listOfFiles.length);
				CHistList.add(cHist.getHistogram(image));
				CHistListDict.put(p.getParent().toString() +"\\"+ p.getFileName().toString(), cHist.getHistogram(image));
				counter ++;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public Map<String, ArrayList<List<Integer>>> getCHistListDict() {
			return CHistListDict;
		}

		public void setCHistListDict(Map<String, ArrayList<List<Integer>>> cHistListDict) {
			CHistListDict = cHistListDict;
		}

}
