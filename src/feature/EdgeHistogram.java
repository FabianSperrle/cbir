package feature;

import converter.GrayScaleConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Convolution;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by fabian on 03.06.2016.
 */
public class EdgeHistogram implements FeatureDescriptor {
    private double[] verticalSobel;
    private double[] horizontalSobel;
    private Logger logger = LogManager.getLogger(EdgeHistogram.class);

    public EdgeHistogram() {
        this.verticalSobel = new double[]{1, 0, -1,
                2, 0, -2,
                1, 0, -1};
        this.horizontalSobel = new double[]{1, 2, 1,
                0, 0, -0,
                -1, -2, -1};
    }

    public int[] getHistogram(BufferedImage im, Path p) {
        final int[] histogram = getHistogram(im);
        String fileName = "cache/histogram/edges/" + String.valueOf(Math.abs(p.toString().hashCode()));
        List<String> values = new LinkedList<>();
        for (int count : histogram) {
            values.add(String.valueOf(count));
        }
        try {
            Files.write(Paths.get(fileName), values, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return histogram;
    }

    public int[] getHistogram(BufferedImage im) {
        BufferedImage gray = GrayScaleConverter.RGB2GrayByAveraging(im);

        double[][] resultX = Convolution.convolve(gray, verticalSobel, 3, 3);
        double[][] resultY = Convolution.convolve(gray, horizontalSobel, 3, 3);

        BufferedImage resultImage = new BufferedImage(resultY.length, resultY[0].length, BufferedImage.TYPE_BYTE_BINARY);
        int[] angles = new int[360];
        for (int x = 0; x < resultY.length; x++) {
            for (int y = 0; y < resultY[0].length; y++) {

                // calculate the magnitude
                double val = Math.hypot(resultX[x][y], resultY[x][y]);

                // don't forget the thresholding (the threshold is arbitrary).
                if (val > 200) {
                    resultImage.setRGB(x, y, Color.white.getRGB());
                }
                int v = (int) Math.round(Math.atan2(resultX[x][y], resultY[x][y]) * 180.0 / Math.PI) + 180;
                if (v == 360) v = 0;

                angles[v]++;
            }
        }

        return angles;
    }
}
