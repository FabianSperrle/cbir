package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileSystemView;

import feature.ColourHistogram;
import feature.EdgeHistogram;
import feature.HaralickTexture;

import distance.EuclidianDistance;
import distance.QuadraticFormDistance;

public class MainPanel {

	private JFrame frame;
	/** Provides nice icons and names for files. */
	private FileSystemView fileSystemView;
	/** Used to open/edit/print files. */
	private Desktop desktop;

	private File selectedFile;
	private BufferedImage img;

	//The Feature Extraction Methods
	ColourHistogram cHist;
	EdgeHistogram eHist;
	HaralickTexture hFeature;

	PicturePanel panel;
	JLabel l;
	int bins;
	Map<String,Double> distI;
	int counter = 0;

	public MainPanel () throws IOException{
		fileSystemView = FileSystemView.getFileSystemView();

		frame = new JFrame("App");
		frame.setVisible(true);
		frame.setSize(new Dimension(700,700));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		bins = 128;
		cHist = new ColourHistogram(bins);

		JLabel l = new JLabel();


		//panel für FileChooser
		JButton openButton = new JButton("Open File");
		JPanel fileChoose = new JPanel();
		fileChoose.setLayout(new BorderLayout());
		fileChoose.setPreferredSize(new Dimension(100,100));
		fileChoose.add(openButton, BorderLayout.CENTER);
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				FileOpener opener = new FileOpener();
				selectedFile = opener.createFileUI(frame);
				img = null;
				try {
					img = ImageIO.read(selectedFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Image resizedImage = img.getScaledInstance(100, 100, 1);
				ImageIcon im = new ImageIcon(resizedImage);

				l.setIcon(im);
				l.setSize(im.getIconWidth(), im.getIconHeight());
				fileChoose.add(l, BorderLayout.EAST);
				fileChoose.revalidate();
				fileChoose.repaint();
				frame.revalidate();
				frame.repaint();

			}
		});
		frame.add(fileChoose, BorderLayout.NORTH);

		//Panel fuer auswahl von Methods
		JPanel methodsPanel = new JPanel();
		methodsPanel.setLayout(new FlowLayout());

		String[] listFeature = {"Color histogram","Global Edge histogram","Texture Haralick Features"};
		JComboBox comboBoxFeature = new JComboBox<String>();
		comboBoxFeature.setModel(new DefaultComboBoxModel(listFeature));
		comboBoxFeature.setMaximumSize( comboBoxFeature.getPreferredSize() );
		methodsPanel.add(comboBoxFeature);

		String[] listSimilarity = {"Color Euclidean Distance ","Quadratic Form Distance"};
		JComboBox comboBoxSimilarity = new JComboBox<String>();
		comboBoxSimilarity.setModel(new DefaultComboBoxModel(listSimilarity));
		comboBoxSimilarity.setMaximumSize( comboBoxSimilarity.getPreferredSize() );
		methodsPanel.add(comboBoxSimilarity);

		distI = new HashMap<String,Double>();
		
		JButton compute = new JButton("Find similar images!");
		CHistPanel cpanel = new CHistPanel(bins);
		EHistPanel epanel = new EHistPanel();
		compute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				switch (comboBoxFeature.getSelectedIndex()) {
				case 0:
					List<List<Integer>> imgHist= cHist.getHistogram(img);

					cpanel.getCHistListDict().entrySet()
							 .parallelStream()
							 .forEach(entry -> computeQFD(entry, imgHist));

					updateThumbnails();
					break;
				case 1:
					eHist = new EdgeHistogram();
					ArrayList<Integer> imgEHist = (ArrayList<Integer>) Arrays.stream( eHist.getHistogram(img) ).boxed().collect( Collectors.toList());
					epanel.getEHistListDict().entrySet()
					 .parallelStream()
					 .forEach(entry -> computeED(entry, imgEHist));
					updateThumbnails();
					break;
				case 2:
					hFeature = new HaralickTexture();
					hFeature.getFeatures(img);
					break;

				default:
					break;
				}
			}
		});
		methodsPanel.add(compute);

		fileChoose.add(methodsPanel,BorderLayout.SOUTH);
		fileChoose.setMaximumSize(fileChoose.getPreferredSize());

		//panel für results
		panel = new PicturePanel();
		JScrollPane listScroller = new JScrollPane(panel);
		listScroller.setPreferredSize(new Dimension(800,800));
		frame.add(listScroller,BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);

	}
	static <K,V extends Comparable<? super V>>SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
		SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
				new Comparator<Map.Entry<K,V>>() {
					@Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
						int res = e1.getValue().compareTo(e2.getValue());
						return res != 0 ? res : 1;
					}
				}
				);
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}
	private void computeQFD(Entry<String, ArrayList<List<Integer>>> entry, List<List<Integer>> imgHist){
		System.out.println(counter);
		String key = entry.getKey();
		ArrayList<List<Integer>> hist = entry.getValue();
		Double[] rgb = new Double[3];
		
		List<Integer> red = imgHist.get(0);
		List<Integer> green = imgHist.get(1);
		List<Integer> blue = imgHist.get(2);

		List<Integer> red2 = hist.get(0);
		List<Integer> green2 = hist.get(1);
		List<Integer> blue2 = hist.get(2);
		
		double[] redArray = Arrays.stream(red.stream().mapToInt(i -> i).toArray()).asDoubleStream().toArray();
		double[] redArray2 = Arrays.stream(red2.stream().mapToInt(d -> d).toArray()).asDoubleStream().toArray();
		double[] greenArray = Arrays.stream(green.stream().mapToInt(d -> d).toArray()).asDoubleStream().toArray();
		double[] greenArray2 = Arrays.stream(green2.stream().mapToInt(d -> d).toArray()).asDoubleStream().toArray();
		double[] blueArray = Arrays.stream(blue.stream().mapToInt(d -> d).toArray()).asDoubleStream().toArray();
		double[] blueArray2 = Arrays.stream(blue2.stream().mapToInt(d -> d).toArray()).asDoubleStream().toArray();
		
		rgb[0] = new Double (QuadraticFormDistance.getQuadricFormDistance(redArray, redArray2,4));
		rgb[1] = new Double (QuadraticFormDistance.getQuadricFormDistance(greenArray, greenArray2,4));
		rgb[2] = new Double (QuadraticFormDistance.getQuadricFormDistance(blueArray, blueArray2,4));
		
		Arrays.sort(rgb);
		distI.put(key, rgb[rgb.length - 1]);
		counter++;
	}
	private void computeQFD(Entry<String, ArrayList<Integer>> entry, ArrayList<Integer> imgHist){
		System.out.println(counter);
		String key = entry.getKey();
		ArrayList<Integer> hist = entry.getValue();
		
		double[] array = Arrays.stream(imgHist.stream().mapToInt(i -> i).toArray()).asDoubleStream().toArray();
		double[] array2 = Arrays.stream(hist.stream().mapToInt(i -> i).toArray()).asDoubleStream().toArray();
		
		distI.put(key, QuadraticFormDistance.getQuadricFormDistance(array, array2));
		counter++;
	}
	private void computeED(Entry<String, ArrayList<Integer>> entry, ArrayList<Integer> imgHist){
		System.out.println(counter);
		String key = entry.getKey();
		ArrayList<Integer> hist = entry.getValue();
		
		double[] array = Arrays.stream(imgHist.stream().mapToInt(i -> i).toArray()).asDoubleStream().toArray();
		double[] array2 = Arrays.stream(hist.stream().mapToInt(i -> i).toArray()).asDoubleStream().toArray();
		
		distI.put(key, new Double(EuclidianDistance.getEuclidianDistance(array, array2)));
		counter++;
	}
	private void updateThumbnails(){
		distI.remove(selectedFile.getParent().toString() +"\\"+ selectedFile.getName().toString());
		Double min = Collections.min(distI.values());
		System.out.println(min);
		System.out.println(selectedFile.getParent().toString() +"\\"+ selectedFile.getName().toString());
		SortedSet<Map.Entry<String,Double>> sorted = entriesSortedByValues(distI);

		panel.setImgList(new ArrayList<String>());
		int x = 0;
		for(Entry<String, Double> ent : sorted) {
			if(x <= 20) {
				System.out.println(ent.getKey());
				panel.addImage(ent.getKey());
				x++;
			}
		}
		panel.showThumbnail();
		panel.repaint();
	}

}


