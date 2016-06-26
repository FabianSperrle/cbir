package ui;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import feature.ColourHistogram;


public class CHistPanel extends FeaturePanel {
    private ColourHistogram cHist;
    private Map<String, List<List<double[]>>> colorHistograms;

    public CHistPanel(int bins) {
    	super.setLayout(new FlowLayout());
        super.setBorder(BorderFactory.createTitledBorder("Additional Parameters Features"));
        JLabel binLabel = new JLabel("Number of bins :");
        JTextField binField = new JTextField("32");
        JLabel cellLabel = new JLabel("Number of cells :");
        JTextField cellField = new JTextField("1");
        super.add(binLabel);
        super.add(binField);
        super.add(cellLabel);
        super.add(cellField);
        cHist = new ColourHistogram(bins);
        try {
            colorHistograms = createColorHistograms();
        } catch (IOException e) {
            e.printStackTrace();
        }
		super.repaint();
    }

    public Map<String, List<List<double[]>>> getColorHistograms() {
        return colorHistograms;
    }

    private Map<String, List<List<double[]>>> createColorHistograms() throws IOException {
        return Files.walk(Paths.get("101_ObjectCategories"))
                //paths.forEach(System.out::println);
                .parallel()
                .unordered()
                .peek(p -> System.out.println("p = " + p))
                .filter(Files::isRegularFile)
                .filter(p -> noExceptionRead(p) != null)
                .collect(Collectors.toMap(
                        path -> path.toAbsolutePath().toString(),
                        path -> cHist.getHistogram(noExceptionRead(path))
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
