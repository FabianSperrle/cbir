package feature;

import converter.GrayScaleConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.beans.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Created by fabian on 03.06.2016.
 */
public class EdgeHistogram implements FeatureDescriptor {
    private ConvolveOp verticalSobel;
    private ConvolveOp horizontalSobel;
    private Logger logger = LogManager.getLogger(EdgeHistogram.class);

    public EdgeHistogram() {
        float[] vertical = {1, 0, -1,
                2, 0, -2,
                1, 0, -1};
        Kernel verticalKernel = new Kernel(3, 3, vertical);
        this.verticalSobel = new ConvolveOp(verticalKernel, ConvolveOp.EDGE_ZERO_FILL, null);

        float[] horizontal = {1, 2, 1,
                0, 0, -0,
                -1, -2, -1};
        Kernel horizontalKernel = new Kernel(3, 3, horizontal);
        this.horizontalSobel = new ConvolveOp(horizontalKernel, ConvolveOp.EDGE_ZERO_FILL, null);
    }

    public List<BufferedImage> getHistogram(BufferedImage im) {
        BufferedImage gray = GrayScaleConverter.RGB2GrayByAveraging(im);

        BufferedImage vertResult = verticalSobel.filter(gray, null);
        BufferedImage horResult = horizontalSobel.filter(gray, null);

        BufferedImage resultImage = new BufferedImage(im.getWidth(), im.getHeight(), im.getType());
        int[] angles = new int[91];
        for (int x = 0; x < resultImage.getWidth(); x++) {
            for (int y = 0; y < resultImage.getHeight(); y++) {
                Color vert = new Color(vertResult.getRGB(x, y));
                Color hor = new Color(horResult.getRGB(x, y));

                int red = Math.min(255, (int) Math.round(Math.hypot(vert.getRed(), hor.getRed())));
                int green = Math.min(255, (int) Math.round(Math.hypot(vert.getGreen(), hor.getGreen())));
                int blue = Math.min(255, (int) Math.round(Math.hypot(vert.getBlue(), hor.getBlue())));

                resultImage.setRGB(x, y, new Color(red, green, blue).getRGB());

                final int v = (int) Math.round(Math.atan2(vert.getRed(), hor.getRed()) * 180.0 / Math.PI);
                angles[v]++;
            }
        }

        for (int i = 0; i < 91; i++) {
            int angle = angles[i];
            logger.warn("angle = " + angle);
        }

        return Arrays.asList(vertResult, horResult, resultImage);
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Gray Scale Image Comparison");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //BufferedImage img = ImageIO.read(new File("101_ObjectCategories/airplanes/image_0020.jpg"));
        BufferedImage img = ImageIO.read(new File("101_ObjectCategories/boy.jpg"));
        EdgeHistogram e = new EdgeHistogram();
        List<BufferedImage> images = e.getHistogram(img);
        frame.getContentPane().add(new JLabel(new ImageIcon(img)), BorderLayout.NORTH);
        frame.getContentPane().add(new JLabel(new ImageIcon(images.get(0))), BorderLayout.WEST);
        frame.getContentPane().add(new JLabel(new ImageIcon(images.get(1))), BorderLayout.EAST);
        frame.getContentPane().add(new JLabel(new ImageIcon(images.get(2))), BorderLayout.SOUTH);
        frame.pack();

        frame.setVisible(true);
    }
}
