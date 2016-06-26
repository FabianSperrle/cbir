package feature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
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

    public ArrayList<List<Integer>> getHistogram(BufferedImage im) {
    //public  List<Integer[][][]> getHistogram(BufferedImage im) {
        logger.debug("Height: {} --- Width: {}", im.getHeight(), im.getWidth());
        int cellHeight = (int) Math.floor(im.getHeight() / this.cells);
        int cellWidth = (int) Math.floor(im.getWidth() / this.cells);
        logger.debug("CellHeight: {} --- CellWidth: {}", cellHeight, cellWidth);

        //List<Integer[][][]> results = new ArrayList<>();
        ArrayList<List<Integer>> results = new ArrayList<List<Integer>>();
        for (int c = 0; c < this.cells; c++) {
            final int minHeight = c * cellHeight;
            logger.debug("minHeight = " + minHeight);
            final int maxHeight = minHeight + cellHeight;
            logger.debug("maxHeight = " + maxHeight);

            final int minWidth = c * cellWidth;
            logger.debug("minWidth = " + minWidth);
            final int maxWidth = minWidth + cellWidth;
            logger.debug("maxWidth = " + maxWidth);
            List<Integer> redHist = new ArrayList<Integer>(Collections.nCopies(this.bins, 0));;
            List<Integer> greenHist = new ArrayList<Integer>(Collections.nCopies(this.bins, 0));;
            List<Integer> blueHist = new ArrayList<Integer>(Collections.nCopies(this.bins, 0));;
            //Integer[][][] result = new Integer[this.bins][this.bins][this.bins];
            for (int y = minHeight; y < maxHeight; y++) {
                for (int x = minWidth; x < maxWidth; x++) {
                    Color color = new Color(im.getRGB(x, y));
                    int red = color.getRed() / this.binWidth;
                    int blue = color.getGreen() / this.binWidth;
                    int green = color.getBlue() / this.binWidth;

                    logger.debug("Red {}, Green {}, Blue {}", red, green ,blue);
                    //System.out.println(red+ " "+green+ " " +blue);
                    //result[red][green][blue] = result[red][green][blue] == null ? 1 : result[red][green][blue] + 1;
                    redHist.set(red, blueHist.get(red)+1);
                    greenHist.set(green, blueHist.get(green)+1);
                    blueHist.set(blue, blueHist.get(blue)+1);
                }
            }
            results.add(redHist);
            results.add(greenHist);
            results.add(blueHist);
        }

        return results;
    }
}
