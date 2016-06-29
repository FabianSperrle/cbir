package ui;

import feature.EdgeHistogram;

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


public class EHistPanel extends FeaturePanel {
    private EdgeHistogram eHist;
    private Map<String, int[]> edgeHistograms;

    public EHistPanel() {
    	super();
    	super.setName("EHist");
        eHist = new EdgeHistogram();
        try {
            edgeHistograms = createEdgeHistograms();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, int[]> getEdgeHistograms() {
        return edgeHistograms;
    }

    private Map<String, int[]> createEdgeHistograms() throws IOException {
        return Files.walk(Paths.get("101_ObjectCategories"))
                //paths.forEach(System.out::println);
                .parallel()
                .unordered()
                .filter(Files::isRegularFile)
                .filter(p -> noExceptionRead(p) != null)
                .collect(Collectors.toMap(
                        path -> path.toAbsolutePath().toString(),
                        path -> {
                            String fileName = "cache/histogram/edges/" + String.valueOf(Math.abs(path.toString().hashCode()));
                            if (Files.exists(Paths.get(fileName))) {
                                try {
                                    final List<String> strings = Files.readAllLines(Paths.get(fileName));
                                    int[] hist = new int[360];
                                    for (int i = 0; i < 360; i++) {
                                        hist[i] = Integer.valueOf(strings.get(i));
                                    }
                                    return hist;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            return eHist.getHistogram(noExceptionRead(path), path);
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
