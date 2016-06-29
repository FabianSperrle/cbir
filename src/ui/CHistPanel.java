package ui;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import feature.ColourHistogram;


public class CHistPanel extends FeaturePanel {
    private ColourHistogram cHist;
    private Map<String, List<List<double[]>>> colorHistograms;
    private JFormattedTextField binField;
    private JFormattedTextField cellField;
    
    public JFormattedTextField getBinField() {
		return binField;
	}

	public void setBinField(JFormattedTextField binField) {
		this.binField = binField;
	}

	public JFormattedTextField getCellField() {
		return cellField;
	}

	public void setCellField(JFormattedTextField cellField) {
		this.cellField = cellField;
	}

	public CHistPanel() {
    	super();
    	super.setLayout(new FlowLayout());
        super.setBorder(BorderFactory.createTitledBorder("Additional Parameters Features"));
        JLabel binLabel = new JLabel("Number of bins :");
        binField = new JFormattedTextField(NumberFormat.getNumberInstance());
        binField.setValue(new Integer(32));
        JLabel cellLabel = new JLabel("Number of cells :");
        cellField = new JFormattedTextField(NumberFormat.getNumberInstance());
        cellField.setValue(new Integer(1));
        super.add(binLabel);
        super.add(binField);
        super.add(cellLabel);
        super.add(cellField);
//        cHist = new ColourHistogram(32, 1);
//        try {
//            colorHistograms = createColorHistograms();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        
        binField.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
			    Object source = e.getSource();
			    Integer v = 0;
			    if (source == cellField) {
			        v = ((Number)cellField.getValue()).intValue();
			        if (v > 0 && v <= 256){
			        	cellField.setValue(new Integer(v));
			        	return;
			        }
			    } else if (source == binField){
			    	v = ((Number)binField.getValue()).intValue();
			    	if (v > 0 && v <= 256){
			    		binField.setValue(new Integer(v));
			        	return;
			        }
			    }
			    
			}
        });
        cellField.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
			    Object source = e.getSource();
			    Integer v = 0;
			    if (source == cellField) {
			        v = ((Number)cellField.getValue()).intValue();
			        if (v > 0 && v <= 256){
			        	cellField.setValue(new Integer(v));
			        	return;
			        }
			    } else if (source == binField){
			    	v = ((Number)binField.getValue()).intValue();
			    	if (v > 0 && v <= 256){
			    		binField.setValue(new Integer(v));
			        	return;
			        }
			    }
			}
        });
        
        cellField.setColumns(10);
        binField.setColumns(10);
		super.repaint();
    }
    public void updateCHistPanel(int bins, int cells){
    	cHist = new ColourHistogram(bins, cells);
        try {
            colorHistograms = createColorHistograms();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Map<String, List<List<double[]>>> getColorHistograms() {
        return colorHistograms;
    }

	@SuppressWarnings("unchecked")
    private Map<String, List<List<double[]>>> createColorHistograms() throws IOException {
		int bins = cHist.getBins();
		int cells = cHist.getCells();
        return Files.walk(Paths.get("101_ObjectCategories"))
                //paths.forEach(System.out::println);
                .parallel()
                .unordered()
                .filter(Files::isRegularFile)
                .filter(p -> noExceptionRead(p) != null)
				.collect(Collectors.toMap(
						path -> path.toAbsolutePath().toString(),
						path -> {
							String fileName = "cache/histogram/color/" + cells + "/" + bins + "/" + String.valueOf(Math.abs(path.toString().hashCode()));
							if (Files.exists(Paths.get(fileName))) {
								try {
									BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fileName));
									ObjectInputStream ois = new ObjectInputStream(fis);
									final List<List<double[]>> lists = (List<List<double[]>>) ois.readObject();
									fis.close();
									ois.close();
									return lists;
								} catch (IOException | ClassNotFoundException e) {
									e.printStackTrace();
								}
							}
							return cHist.getHistogram(noExceptionRead(path), path);
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
}
