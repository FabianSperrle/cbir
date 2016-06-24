package utils;

import java.awt.Color;
import java.util.Arrays;

public class HumanPerceptionSimilarityMatrix {

	/**
 * Creates the similarity for one channel (or the gray values) of an image
	 * for the specified number of bins. The similarity matrix should be
	 * correspond more to the human perception. To achieve that the RGB colors
	 * are converted to the L*ab color space. Usage example:
	 * {@code createHumanPerceptionSimilarityMatrix{8}}
	 * 
	 * @param numBins
	 *            the number of bins, min. 1 and max. 256
	 * @return a weighted similarity matrix. the weights are in the range of [0,
	 *         1]. 1 means that the colors are similar. weights lower than 1
	 *         indicate the dissimilarity of these colors in the L*ab space.
	 */
	public static double[][] createHumanPerceptionSimilarityMatrix(int numBins) {

		if (numBins < 1 || numBins > 256) {
			throw new IllegalArgumentException("numBins outside of range [1, 256]");
		}

		// first calculate the bin range
		float binrange = 255 / (float) numBins;

		// create a color lookup table.
		// for each color bin, calculate the average, convert that color to L*ab
		double[][] colorLookupTable = new double[numBins][3];
		for (int i = 0; i < numBins; i++) {
			float lower_bin_bound = i * binrange;
			float upper_bin_bound = (i + 1) * binrange;
			float average_bin_value = (lower_bin_bound + (upper_bin_bound - lower_bin_bound)) / 255f;

			colorLookupTable[i] = rgbToLab(new Color(average_bin_value, average_bin_value, average_bin_value));
		}

		// calculate the maximum distance in the lab room
		// min and max values taken from http://stackoverflow.com/a/19099064
		double maxdistance = LabDistance(new double[] { 0, -86.185, -107.863 }, new double[] { 100, 98.254, 94.482 });

		double[][] similarityMatrix = new double[numBins][numBins];
		for (int i = 0; i < numBins; i++) {
			for (int j = 0; j < numBins; j++) {
				similarityMatrix[i][j] = 1 - LabDistance(colorLookupTable[i], colorLookupTable[j]) / maxdistance;
			}
		}

		return similarityMatrix;
	}

	/**
	 * Converts a RGB color to L*ab.
	 * 
	 * 
	 * @param c
	 *            a color in the RGB color space
	 * @return a double array containing the L*ab color values
	 */
	private static double[] rgbToLab(Color c) {

		// first we have to convert from RGB to XYZ color space.
		double var_R = (c.getRed() / 255d); // R from 0 to 255
		double var_G = (c.getGreen() / 255d); // G from 0 to 255
		double var_B = (c.getBlue() / 255d); // B from 0 to 255

		if (var_R > 0.04045) {
			var_R = Math.pow(((var_R + 0.055) / 1.055), 2.4);
		} else {
			var_R = var_R / 12.92;
		}

		if (var_G > 0.04045) {
			var_G = Math.pow(((var_G + 0.055) / 1.055), 2.4);
		} else {
			var_G = var_G / 12.92;
		}

		if (var_B > 0.04045) {
			var_B = Math.pow(((var_B + 0.055) / 1.055), 2.4);
		} else {
			var_B = var_B / 12.92;
		}

		var_R = var_R * 100;
		var_G = var_G * 100;
		var_B = var_B * 100;

		// Observer. = 2�, Illuminant = D65
		double X = var_R * 0.4124 + var_G * 0.3576 + var_B * 0.1805;
		double Y = var_R * 0.2126 + var_G * 0.7152 + var_B * 0.0722;
		double Z = var_R * 0.0193 + var_G * 0.1192 + var_B * 0.9505;

		// then convert from RGB to L*ab
		double var_X = X / 95.047; // ref_X = 95.047 Observer= 2�, Illuminant=
									// D65
		double var_Y = Y / 100.000; // ref_Y = 100.000
		double var_Z = Z / 108.883; // ref_Z = 108.883

		if (var_X > 0.008856) {
			var_X = Math.pow(var_X, 1d / 3d);
		} else {
			var_X = (7.787 * var_X) + (16 / 116);
		}
		if (var_Y > 0.008856) {
			var_Y = Math.pow(var_Y, 1d / 3d);
		} else {
			var_Y = (7.787 * var_Y) + (16 / 116);
		}
		if (var_Z > 0.008856) {
			var_Z = Math.pow(var_Z, 1d / 3d);
		} else {
			var_Z = (7.787 * var_Z) + (16 / 116);
		}

		double CIE_L = (116 * var_Y) - 16;
		double CIE_a = 500 * (var_X - var_Y);
		double CIE_b = 200 * (var_Y - var_Z);

		return new double[] { CIE_L, CIE_a, CIE_b };
	}

	/**
	 * Calculates the color difference according to the CIE76 standard.
	 * 
	 * @see https://en.wikipedia.org/wiki/Color_difference#CIE76
	 * 
	 * @param lab1
	 *            one color in L*ab color space as a double array
	 * @param lab2
	 *            another color in L*ab color space as a double array
	 * 
	 * @return the difference between the colors
	 */
	private static double LabDistance(double[] lab1, double[] lab2) {
		return Math
				.sqrt(Math.pow(lab1[0] - lab2[0], 2) + Math.pow(lab1[1] - lab2[1], 2) + Math.pow(lab1[2] - lab2[2], 2));
	}

}
