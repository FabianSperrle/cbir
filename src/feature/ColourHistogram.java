package feature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by fabian on 01.06.2016.
 */
public class ColourHistogram implements FeatureDescriptor {
    Logger logger = LogManager.getLogger(ColourHistogram.class);

    /** Number of colour bins */
    private int bins;
    private int binWidth;
    /** Number of cells. Leads to cell^2 sub-histograms. */
    private int cells;

    public ColourHistogram(int bins, int cells) {
        this.bins = bins;
        this.binWidth = 256 / bins;
        this.cells = cells;
    }
     public ColourHistogram(int bins) {
         this.bins = bins;
         this.binWidth = 256 / bins;
         this.cells = 1;
     }

    public List<List<double[]>> getHistogram(BufferedImage im) {
        int cellHeight = (int) Math.floor(im.getHeight() / this.cells);
        int cellWidth = (int) Math.floor(im.getWidth() / this.cells);

        List<List<double[]>> results = new LinkedList<>();
        for (int c = 0; c < this.cells; c++) {
            List<double[]> hist = new LinkedList<>();
            final int minHeight = c * cellHeight;
            final int maxHeight = minHeight + cellHeight;

            final int minWidth = c * cellWidth;
            final int maxWidth = minWidth + cellWidth;
            double[] redHist = new double[this.bins];
            double[] greenHist = new double[this.bins];
            double[] blueHist = new double[this.bins];
            for (int y = minHeight; y < maxHeight; y++) {
                for (int x = minWidth; x < maxWidth; x++) {
                    Color color = new Color(im.getRGB(x, y));
                    int red = color.getRed() / this.binWidth;
                    int blue = color.getGreen() / this.binWidth;
                    int green = color.getBlue() / this.binWidth;

                    redHist[red]++;
                    greenHist[green]++;
                    blueHist[blue]++;

                    logger.debug("Red {}, Green {}, Blue {}", red, green ,blue);
                }
            }
            hist.add(redHist);
            hist.add(greenHist);
            hist.add(blueHist);
            results.add(hist);
        }

        return results;
    }
}
