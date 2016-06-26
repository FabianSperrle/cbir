package ui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

import feature.ColourHistogram;


public class CHistPanel extends JPanel {
    private ColourHistogram cHist;
    private Map<String, List<List<double[]>>> colorHistograms;

    public CHistPanel(int bins) {
        JButton compute = new JButton("Compute Colour Histograms!");
        cHist = new ColourHistogram(bins);
        try {
            colorHistograms = createColorHistograms();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
