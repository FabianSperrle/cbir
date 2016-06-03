package feature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    public List<Integer[][][]> getHistogram(BufferedImage im) {
        logger.debug("Height: {} --- Width: {}", im.getHeight(), im.getWidth());
        int cellHeight = (int) Math.floor(im.getHeight() / this.cells);
        int cellWidth = (int) Math.floor(im.getWidth() / this.cells);
        logger.debug("CellHeight: {} --- CellWidth: {}", cellHeight, cellWidth);

        List<Integer[][][]> results = new LinkedList<>();

        for (int c = 0; c < this.cells; c++) {
            final int minHeight = c * cellHeight;
            logger.debug("minHeight = " + minHeight);
            final int maxHeight = minHeight + cellHeight;
            logger.debug("maxHeight = " + maxHeight);

            final int minWidth = c * cellWidth;
            logger.debug("minWidth = " + minWidth);
            final int maxWidth = minWidth + cellWidth;
            logger.debug("maxWidth = " + maxWidth);

            Integer[][][] result = new Integer[this.bins][this.bins][this.bins];
            for (int y = minHeight; y < maxHeight; y++) {
                for (int x = minWidth; x < maxWidth; x++) {
                    Color color = new Color(im.getRGB(x, y));
                    int red = color.getRed() / this.binWidth;
                    int blue = color.getGreen() / this.binWidth;
                    int green = color.getBlue() / this.binWidth;

                    logger.debug("Red {}, Green {}, Blue {}", red, green ,blue);

                    result[red][green][blue] = result[red][green][blue] == null ? 1 : result[red][green][blue] + 1;
                }
            }
            results.add(result);
        }

        return results;
    }
}
