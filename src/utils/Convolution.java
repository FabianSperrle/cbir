package utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Convolution {
    /**
     * Calculates the 2d convolution of the image for the given kernel. Example
     * usage: {@code convolve(image, new double[] { -1, -2, -1, 0, 0, 0, 1, 2, 1
     * }, 3, 3}
     *
     * @param bimg         a GRAYSCALE image (red, green and blue must have the same
     *                     value)
     * @param kernel       a kernel
     * @param kernelwidth  the width of the kernel, must be an odd number larger or equal
     *                     to 3.
     * @param kernelheight the height of the kernel, must be an odd number larger or
     *                     equal to 3.
     * @return
     * @throws IllegalArgumentException if kernel is smaller than 3x3 or if a kernel dimension has
     *                                  even number of elements
     */
    public static double[][] convolve(final BufferedImage bimg, final double[] kernel, final int kernelwidth,
                                      final int kernelheight) {

        // convolve kernel must be at least 3x3
        if (kernelwidth < 3 || kernelheight < 3) {
            throw new IllegalArgumentException("convolve kernel must be at least 3x3");
        }

        // convole kernel must have odd number of elements
        if (kernelwidth % 2 == 0 || kernelheight % 2 == 0) {
            throw new IllegalArgumentException("convole kernel must have odd number of elements");
        }

        // convole kernel must have odd number of elements
        if (kernel.length != kernelwidth * kernelheight) {
            throw new IllegalArgumentException("number of elements in kernel isn't equal to given kernel dimension");
        }

        final int kernelOffsetX = Math.floorDiv(kernelwidth, 2);
        final int kernelOffsetY = Math.floorDiv(kernelheight, 2);

        final double[][] result = new double[bimg.getWidth()][bimg.getHeight()];

        for (int y = 0; y < bimg.getHeight(); y++) {
            for (int x = 0; x < bimg.getWidth(); x++) {

                // if pixel is outside of image boundary then result = 0;
                if (y - kernelOffsetY < 0 || y + kernelOffsetY >= bimg.getHeight() || x - kernelOffsetX < 0
                        || x + kernelOffsetX >= bimg.getWidth()) {
                    result[x][y] = 0;
                } else {

                    double sum = 0;

                    for (int kernel_y = 0; kernel_y < kernelheight; kernel_y++) {
                        for (int kernel_x = 0; kernel_x < kernelwidth; kernel_x++) {

                            // calculate position in image
                            int x_ = x + kernel_x - kernelOffsetX;
                            int y_ = y + kernel_y - kernelOffsetY;

                            // get color value
                            Color c = new Color(bimg.getRGB(x_, y_));
                            double value = (c.getRed() + c.getGreen() + c.getBlue()) / 3d;

                            // multiply by kernel value
                            value *= kernel[kernel_x + kernel_y * kernelheight];

                            sum += value;
                        }
                    }

                    result[x][y] = sum;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) throws IOException {

        // kernel for x and y direction
        double sobelx[] = {1, 0, -1, 2, 0, -2, 1, 0, -1};
        double sobely[] = {1, 2, 1, 0, 0, 0, -1, -2, -1};

        // test image
        BufferedImage img = ImageIO
                .read(new URL("http://de.mathworks.com/help/examples/coder_product/coderdemo_edge_detection_01.png"));

        double[][] resultX = convolve(img, sobelx, 3, 3);
        double[][] resultY = convolve(img, sobely, 3, 3);

        BufferedImage edgeImg = new BufferedImage(resultY.length, resultY[0].length, BufferedImage.TYPE_BYTE_BINARY);
        for (int x = 0; x < resultY.length; x++) {
            for (int y = 0; y < resultY[0].length; y++) {

                // calculate the magnitude
                double val = Math.hypot(resultX[x][y], resultY[x][y]);

                // don't forget the thresholding (the threshold is arbitrary).
                if (val > 200) {
                    edgeImg.setRGB(x, y, Color.white.getRGB());
                }
            }
        }

        JFrame jf = new JFrame("Edge Detection");
        jf.add(new JLabel(new ImageIcon(edgeImg)));
        jf.pack();
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
