package ui;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import feature.ColourHistogram;
import feature.HaralickTexture;

public class HTPanel extends FeaturePanel {
	private HaralickTexture hTexture;
    private Map<String, double[]> hTextures;
    
    
	public HTPanel() {
    	super();
    	super.setLayout(new FlowLayout());
    	hTexture = new HaralickTexture();
        try {
        	hTextures = createHaralickTextures();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
		super.repaint();
    }
    
    public Map<String, double[]> getHTextures() {
        return hTextures;
    }

    private Map<String, double[]> createHaralickTextures() throws IOException {
        return Files.walk(Paths.get("101_ObjectCategories"))
                //paths.forEach(System.out::println);
                .parallel()
                .unordered()
                .peek(p -> System.out.println("ht p = " + p))
                .filter(Files::isRegularFile)
                .filter(p -> noExceptionRead(p) != null)
                .collect(Collectors.toMap(
                        path -> path.toAbsolutePath().toString(),
                        path -> {
                            String fileName = "cache/feature/haralick/" + String.valueOf(Math.abs(path.toAbsolutePath().toString().hashCode()));
                            if (Files.exists(Paths.get(fileName))) {
                                try {
                                    final List<String> strings = Files.readAllLines(Paths.get(fileName));
                                    double[] features = new double[24];
                                    for (int i = 0; i < 24; i++) {
                                        features[i] = Double.valueOf(strings.get(i));
                                    }
                                    return features;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            return hTexture.getFeatures(noExceptionRead(path), path);
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