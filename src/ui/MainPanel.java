package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;

import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import distance.CosineDistance;
import feature.ColourHistogram;
import feature.EdgeHistogram;
import feature.HaralickTexture;

import distance.EuclidianDistance;
import distance.QuadraticFormDistance;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


public class MainPanel {

	private JFrame frame;

	private File selectedFile;
	private BufferedImage img;

	//The Feature Extraction Methods
	private ColourHistogram cHist;
	private EdgeHistogram eHist;
	private HaralickTexture hFeature;

	private PicturePanel panel;
	private FeaturePanel panelFeature;
	private FeaturePanel panelDistance;
	private Map<String,Double> distI;
	private int counter = 0;

	private final String[] listSimilarityColorHist = {"Color Euclidean Distance","Quadratic Form Distance", "Cosine Similarity"};
	private final String[] listSimilarityGlobalEdgeHist = {"Color Euclidean Distance", "Cosine Similarity"};
	private final String[] listSimilarityHaralick = {"Color Euclidean Distance", "Cosine Similarity"};
	private final String[] listSimilarityOpenCVHist = {"Chi-Square","Correlation", "Intersection", "Bhattacharyya distance"};

	public MainPanel () throws IOException{

		frame = new JFrame("App");
		frame.setVisible(true);
		frame.setSize(new Dimension(1920,1080));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		JLabel l = new JLabel();


		//panel für FileChooser
		JButton openButton = new JButton("Open File");
		JPanel fileChoose = new JPanel();
		fileChoose.setLayout(new BorderLayout());
		//fileChoose.setPreferredSize(new Dimension(500,600));
		fileChoose.add(openButton, BorderLayout.NORTH);
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
				fileChoose.repaint();
				fileChoose.revalidate();
				frame.repaint();

			}
		});
		frame.add(fileChoose, BorderLayout.NORTH);

		//Panel fuer auswahl von Methods
		JPanel methodsPanel = new JPanel();
		methodsPanel.setPreferredSize(new Dimension(200,200));
		//methodsPanel.setLayout(new FlowLayout());
		methodsPanel.setLayout(new BoxLayout(methodsPanel, BoxLayout.Y_AXIS));
		methodsPanel.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));		
		String[] listFeature = {"Color histogram","Global Edge histogram","Texture Haralick Features","OpenCV Histogram"};
		JComboBox<String> comboBoxFeature = new JComboBox<String>();
		comboBoxFeature.setModel(new DefaultComboBoxModel<String>(listFeature));
		comboBoxFeature.setMaximumSize( comboBoxFeature.getPreferredSize() );
		comboBoxFeature.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboBoxFeature.setSelectedIndex(1);
		methodsPanel.add(comboBoxFeature);
		methodsPanel.add(Box.createRigidArea(new Dimension(0, 10)));	

		JComboBox<String> comboBoxSimilarity = new JComboBox<String>();
		comboBoxSimilarity.setModel(new DefaultComboBoxModel<String>(listSimilarityColorHist));
		comboBoxSimilarity.setMaximumSize( comboBoxSimilarity.getPreferredSize() );
		comboBoxSimilarity.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboBoxSimilarity.setModel(new DefaultComboBoxModel<String>(listSimilarityGlobalEdgeHist));
		methodsPanel.add(comboBoxSimilarity);
		methodsPanel.add(Box.createRigidArea(new Dimension(0, 10)));	

		JPanel addPanel = new JPanel();
		addPanel.setLayout(new GridLayout(2, 1));
		addPanel.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));	
		fileChoose.add(addPanel, BorderLayout.CENTER);

		CHistPanel cpanel = new CHistPanel();
		EHistPanel epanel = new EHistPanel();
		HTPanel htpanel = new HTPanel();
		OpenHistCVPanel cvpanel = new OpenHistCVPanel();
		panelFeature = new FeaturePanel();
		panelFeature.setLayout(new CardLayout());
		addPanel.add(panelFeature,Box.createRigidArea(new Dimension(0, 10)));
		panelFeature.add(cpanel,"c");
		panelFeature.add(epanel,"e");
		CardLayout cl = (CardLayout)(panelFeature.getLayout());
		cl.show(panelFeature, "e");
		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				int state = itemEvent.getStateChange();
				System.out.println((state == ItemEvent.SELECTED) ? "Selected" : "Deselected");
				System.out.println("Item: " + itemEvent.getItem());
				ItemSelectable is = itemEvent.getItemSelectable();
				switch ((String) is.getSelectedObjects()[0]) {
				case "Color histogram": 
					comboBoxSimilarity.setModel(new DefaultComboBoxModel<String>(listSimilarityColorHist));
					cl.show(panelFeature, "c");
					break;
				case "Global Edge histogram":
					comboBoxSimilarity.setModel(new DefaultComboBoxModel<String>(listSimilarityGlobalEdgeHist));
					cl.show(panelFeature, "e");
					break;
				case "Texture Haralick Features":
					comboBoxSimilarity.setModel(new DefaultComboBoxModel<String>(listSimilarityHaralick));
					cl.show(panelFeature, "e");
					break;
				case "OpenCV Histogram":
					comboBoxSimilarity.setModel(new DefaultComboBoxModel<String>(listSimilarityOpenCVHist));
					cl.show(panelFeature, "e");
					break;
				default:
					break;
				}
			}
		};
		comboBoxFeature.addItemListener(itemListener);

		QFDPanel distancePanel = new QFDPanel();
		panelDistance = new FeaturePanel();
		panelDistance.setLayout(new CardLayout());
		addPanel.add(panelDistance,Box.createRigidArea(new Dimension(0, 10)));
		panelDistance.add(distancePanel,"c");
		panelDistance.add(new JPanel(),"e");
		CardLayout cl2 = (CardLayout)(panelDistance.getLayout());
		cl2.show(panelDistance, "e");

		ItemListener itemListener2 = new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				int state = itemEvent.getStateChange();
				System.out.println((state == ItemEvent.SELECTED) ? "Selected" : "Deselected");
				System.out.println("Item: " + itemEvent.getItem());
				ItemSelectable is = itemEvent.getItemSelectable();
				switch ((String) is.getSelectedObjects()[0]) {
				case "Color Euclidean Distance": 
				case "Chi-Square":
				case "Intersection":
				case "Correlation":
				case "Cosine Similarity":
				case "Bhattacharyya distance":
					cl2.show(panelDistance, "e");
					break;
				case "Quadratic Form Distance":
					cl2.show(panelDistance, "c");
					break;
				default:
					break;
				}
			}
		};
		comboBoxSimilarity.addItemListener(itemListener2);
		distI = new HashMap<String,Double>();

		JButton compute = new JButton("Find similar images!");
		compute.setAlignmentX(Component.LEFT_ALIGNMENT);

		compute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (img!= null){
					distI = new HashMap<String,Double>();
					counter = 0;
					switch (comboBoxFeature.getSelectedIndex()) {
					case 0:

						cHist = new ColourHistogram(((Number)cpanel.getBinField().getValue()).intValue(), ((Number)cpanel.getCellField().getValue()).intValue());
						cpanel.updateCHistPanel(((Number)cpanel.getBinField().getValue()).intValue(), ((Number)cpanel.getCellField().getValue()).intValue());
						List<List<double[]>> imgHist= cHist.getHistogram(img);
						Map<String, List<List<double[]>>> syncMap = Collections.synchronizedMap(cpanel.getColorHistograms());
						switch (comboBoxSimilarity.getSelectedIndex()) {
						case 0:
							syncMap.entrySet()
							.parallelStream()
							.forEach(entry -> computeED(entry, imgHist));
							updateThumbnails();
							break;
						case 1:
							int ev = Integer.parseInt(distancePanel.getEvField().getText());
							syncMap.entrySet()
							.parallelStream()
							.forEach(entry -> computeQFD(entry, imgHist,ev));
							updateThumbnails();
							break;
						default:
							break;
						}

						break;
					case 1:
						Map<String, int[]> syncMap1 = Collections.synchronizedMap(epanel.getEdgeHistograms());
						eHist = new EdgeHistogram();
						int[] imgEHist = eHist.getHistogram(img);

						switch (comboBoxSimilarity.getSelectedIndex()) {
						case 0:
							epanel.getEdgeHistograms().entrySet()
							.parallelStream()
							.forEach(entry -> computeED(entry, imgEHist));
							break;
						case 1:
							epanel.getEdgeHistograms().entrySet()
							.parallelStream()
							.forEach(entry -> computeCosineDistance(entry, imgEHist));
							updateThumbnails();
							break;
						default:
							break;
						}

						syncMap1.entrySet()
						.parallelStream()
						.forEach(entry -> computeED(entry, imgEHist));
						updateThumbnails();
						break;
					case 2:
						hFeature = new HaralickTexture();
						double[] imghfeature = hFeature.getFeatures(img);
						Map<String, double[]> syncMap11 = Collections.synchronizedMap(htpanel.getHTextures());
						switch (comboBoxSimilarity.getSelectedIndex()) {
						case 0:
							syncMap11.entrySet()
							.parallelStream()
							.forEach(entry -> computeHF(entry, imghfeature));
							updateThumbnails();
							break;
						case 1:

							syncMap11.entrySet()
							.parallelStream()
							.forEach(entry -> computeCosineDistance(entry, imghfeature));
							updateThumbnails();
							break;
						default:
							break;
						}

						break;
					case 3:
						Map<String, Mat> syncMap111 = Collections.synchronizedMap(cvpanel.getOpenCVMatrices());
						switch (comboBoxSimilarity.getSelectedIndex()) {
						case 0:
							syncMap111.entrySet()
							.parallelStream()
							.forEach(entry -> computeOpenCVHist(entry, OpenHistCVPanel.toCv(img),Imgproc.CV_COMP_CHISQR)
									);
							updateThumbnails();
							break;
						case 1:
							syncMap111.entrySet()
							.parallelStream()
							.forEach(entry -> computeOpenCVHist(entry, OpenHistCVPanel.toCv(img),Imgproc.CV_COMP_CORREL)
									);
							updateThumbnailsMax();
							break;
						case 2:
							syncMap111.entrySet()
							.parallelStream()
							.forEach(entry -> computeOpenCVHist(entry, OpenHistCVPanel.toCv(img),Imgproc.CV_COMP_INTERSECT)
									);
							updateThumbnailsMax();
							break;
						case 3:
							syncMap111.entrySet()
							.parallelStream()
							.forEach(entry -> computeOpenCVHist(entry, OpenHistCVPanel.toCv(img),Imgproc.CV_COMP_BHATTACHARYYA)
									);
							updateThumbnails();
							break;
						default:
							break;
						}
						break;
					case 4:

						break;

					default:
						break;
					}

				}
			}
		});
		methodsPanel.add(compute);

		fileChoose.add(methodsPanel,BorderLayout.WEST);
		fileChoose.setMinimumSize(fileChoose.getPreferredSize());

		//panel für results
		panel = new PicturePanel();
		JScrollPane listScroller = new JScrollPane(panel);
		listScroller.setPreferredSize(new Dimension(650,400));
		frame.add(listScroller,BorderLayout.CENTER);
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

	static <K,V extends Comparable<? super V>>SortedSet<Map.Entry<K,V>> entriesSortedByValuesDesc(Map<K,V> map) {
		SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
				new Comparator<Map.Entry<K,V>>() {
					@Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
						int res = e2.getValue().compareTo(e1.getValue());
						return res != 0 ? res : 1;
					}
				}
				);
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}



	private void computeHF(Entry<String, double[]> entry, double[] imgHist){
		//System.out.println(counter);
		String key = entry.getKey();
		double[] hist = entry.getValue();

		distI.put(key, EuclidianDistance.getEuclidianDistance(hist, imgHist));
		//counter++;
	}

	private void computeCosineDistance(Entry<String, double[]> entry, double[] imgHist){
		String key = entry.getKey();
		double[] hist = entry.getValue();

		distI.put(key, CosineDistance.cosineDistance(hist, imgHist));
	}

	private void computeCosineDistance(Entry<String, int[]> entry, int[] imgHist){
		String key = entry.getKey();
		int[] hist = entry.getValue();

		distI.put(key, CosineDistance.cosineDistance(hist, imgHist));
	}
	private void computeQFD(Entry<String, double[]> entry, double[] imgHist, int ev){
		//System.out.println(counter);
		String key = entry.getKey();
		double[] hist = entry.getValue();


		distI.put(key, QuadraticFormDistance.getQuadricFormDistance(hist, imgHist, ev));
		//counter++;
	}

	private void computeQFD(Entry<String, List<List<double[]>>> entry, List<List<double[]>> imgHist, int ev){
		//System.out.println(counter);
		String key = entry.getKey();
		List<List<double[]>> hist = entry.getValue();

		double distance = 0;
		// Iterate through cells
		for (int i = 0; i < hist.size(); i++) {
			List<double[]> cell = hist.get(i);
			for (int j = 0; j < cell.size(); j++) {
				double[] histogram1 = cell.get(j);
				double[] histogram2 = imgHist.get(i).get(j);
				distance += QuadraticFormDistance.getQuadricFormDistance(histogram1, histogram2, ev);
			}
		}
		distance /= hist.size() * 3;

		distI.put(key, distance);
		//counter++;
	}
	private void computeOpenCVHist(Entry<String, Mat> entry, Mat img, int type){
		//System.out.println(counter);
		String key = entry.getKey();

		distI.put(key, OpenHistCVPanel.compareHistograms(img,entry.getValue(),type));
		entry.getValue().release();
		//counter++;
	}

	private void computeED(Entry<String, List<List<double[]>>> entry, List<List<double[]>> imgHist){
		//System.out.println(counter);
		String key = entry.getKey();
		List<List<double[]>> hist = entry.getValue();

		double distance = 0;
		// Iterate through cells
		for (int i = 0; i < hist.size(); i++) {
			List<double[]> cell = hist.get(i);
			for (int j = 0; j < cell.size(); j++) {
				double[] histogram1 = cell.get(j);
				double[] histogram2 = imgHist.get(i).get(j);
				distance += EuclidianDistance.getEuclidianDistance(histogram1, histogram2);
			}
		}
		distance /= hist.size() * 3;

		distI.put(key, distance);
		//counter++;
	}
	private void computeED(Entry<String, double[]> entry, double[] imgHist){
		//System.out.println(counter);
		String key = entry.getKey();
		double[] hist = entry.getValue();

		distI.put(key, EuclidianDistance.getEuclidianDistance(hist, imgHist));
		//counter++;
	}

	private void computeED(Entry<String, int[]> entry, int[] imgHist){
		//System.out.println(counter);
		String key = entry.getKey();
		int[] hist = entry.getValue();

		distI.put(key, EuclidianDistance.getEuclidianDistance(hist, imgHist));
		//counter++;
	}

	private void updateThumbnails(){
		distI.remove(selectedFile.getParent().toString() +File.separator+ selectedFile.getName().toString());
		Double min = Collections.min(distI.values());
		System.out.println(min);
		System.out.println(selectedFile.getParent().toString() +"\\"+ selectedFile.getName().toString());
		SortedSet<Map.Entry<String,Double>> sorted = entriesSortedByValues(distI);

		panel.setImgList(new ArrayList<String>());
		int x = 0;
		for(Entry<String, Double> ent : sorted) {
			if(x < 20) {
				System.out.println(ent.getKey());
				panel.addImage(ent.getKey());
				x++;
			}
		}
		frame.pack();
		panel.showThumbnail();
		panel.revalidate();
		panel.repaint();
	}
	private void updateThumbnailsMax(){
		distI.remove(selectedFile.getParent().toString() +File.separator+ selectedFile.getName().toString());
		Double min = Collections.min(distI.values());
		System.out.println(min);
		System.out.println(selectedFile.getParent().toString() +"\\"+ selectedFile.getName().toString());
		SortedSet<Map.Entry<String,Double>> sorted = entriesSortedByValuesDesc(distI);

		panel.setImgList(new ArrayList<String>());
		int x = 0;
		for(Entry<String, Double> ent : sorted) {
			if(x < 20) {
				System.out.println(ent.getKey());
				panel.addImage(ent.getKey());
				x++;
			}
		}
		frame.pack();
		panel.showThumbnail();
		panel.revalidate();
		panel.repaint();
	}

}


