package converter;

import utils.Function3;

import java.awt.image.BufferedImage;

/**
 * Created by fabian on 03.06.2016.
 */
public class GrayScaleConverter {

    /**
     * Calculate a gray scale version of the input {@link BufferedImage} by
     * averaging the values of the red, green and blue channel.
     *
     * @param img the input {@link BufferedImage}.
     * @return the input {@link BufferedImage} converted to a gray scale
     * {@link BufferedImage}.
     */
    public static BufferedImage RGB2GrayByAveraging(BufferedImage img) {
        // Simple average function
        Function3<Integer, Integer, Integer, Integer> f = (red, green, blue) ->
                Math.round((red + green + blue) / 3);
        return RBG2Gray(img, f);
    }

    /**
     * Calculate a gray scale version of the input {@link BufferedImage} by
     * using the following equation called "Luma": Gray = (Red * 0.299 + Green *
     * 0.587 + Blue * 0.114)
     *
     * @param img the input {@link BufferedImage}.
     * @return the input {@link BufferedImage} converted to a gray scale
     * {@link BufferedImage}.
     */
    public static BufferedImage RGB2GrayByBT601(BufferedImage img) {
        // Luma function
        Function3<Integer, Integer, Integer, Integer> f = (red, green, blue) ->
                (int) Math.round(0.299 * red + 0.587 * green + 0.114 * blue);
        return RBG2Gray(img, f);
    }

    /**
     * Calculate a gray scale version of the input {@link BufferedImage} by
     * applying the function f to the red, green and blue values.
     *
     * @param img the input {@link BufferedImage}.
     * @return the input {@link BufferedImage} converted to a gray scale
     * {@link BufferedImage}.
     */
    private static BufferedImage RBG2Gray(BufferedImage img, Function3<Integer, Integer, Integer, Integer> f) {
        // Create new image with same dimensions
        // Otherwise we would override the input image
        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        // Iterate over all pixels
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                // Get the different values by bit-shifting the RGB value
                int rgb = img.getRGB(x, y);
                int alpha = rgb >> 24 & 0xff;
                int red = rgb >> 16 & 0xff;
                int green = rgb >> 8 & 0xff;
                int blue = rgb & 0xff;

                // Apply the respective transformation function
                int newRGB = f.apply(red, green, blue);

                // Recompose the rgb value by bit-shifting
                rgb = alpha << 24 | newRGB << 16 | newRGB << 8 | newRGB;

                // Set the color of the new, empty image
                newImg.setRGB(x, y, rgb);
            }
        }
        return newImg;
    }
}
