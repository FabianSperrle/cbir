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
	private ColourHistogram cHist;
	private EdgeHistogram eHist;
	private HaralickTexture hFeature;

	private PicturePanel panel;
	private JLabel l;
	private Map<String,Double> distI;
	private int counter = 0;

	public MainPanel () throws IOException{
		fileSystemView = FileSystemView.getFileSystemView();

		frame = new JFrame("App");
		frame.setVisible(true);
		frame.setSize(new Dimension(700,700));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		int bins = 32;
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
					List<List<double[]>> imgHist= cHist.getHistogram(img);

					cpanel.getColorHistograms().entrySet()
							 .parallelStream()
							 .forEach(entry -> computeQFD(entry, imgHist));

					updateThumbnails();
					break;
				case 1:
					eHist = new EdgeHistogram();
					int[] imgEHist = eHist.getHistogram(img);
					epanel.getEdgeHistograms().entrySet()
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
		listScroller.setPreferredSize(new Dimension(650,400));
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

	private void computeQFD(Entry<String, double[]> entry, double[] imgHist){
		System.out.println(counter);
		String key = entry.getKey();
		double[] hist = entry.getValue();


		distI.put(key, QuadraticFormDistance.getQuadricFormDistance(hist, imgHist));
		counter++;
	}

	private void computeQFD(Entry<String, List<List<double[]>>> entry, List<List<double[]>> imgHist){
		System.out.println(counter);
		String key = entry.getKey();
		List<List<double[]>> hist = entry.getValue();

		double distance = 0;
		// Iterate through cells
		for (int i = 0; i < hist.size(); i++) {
			List<double[]> cell = hist.get(i);
			for (int j = 0; j < cell.size(); j++) {
				double[] histogram1 = cell.get(j);
				double[] histogram2 = imgHist.get(i).get(j);
				distance += QuadraticFormDistance.getQuadricFormDistance(histogram1, histogram2);
			}
		}
		distance /= hist.size() * 3;
		
		distI.put(key, distance);
		counter++;
	}

	private void computeED(Entry<String, double[]> entry, double[] imgHist){
		System.out.println(counter);
		String key = entry.getKey();
		double[] hist = entry.getValue();
		
		distI.put(key, EuclidianDistance.getEuclidianDistance(hist, imgHist));
		counter++;
	}

	private void computeED(Entry<String, int[]> entry, int[] imgHist){
		System.out.println(counter);
		String key = entry.getKey();
		int[] hist = entry.getValue();

		distI.put(key, EuclidianDistance.getEuclidianDistance(hist, imgHist));
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


