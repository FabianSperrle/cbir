package distance;

import java.util.Arrays;

/**
 * Created by fabian on 24.06.2016.
 */
public class EuclidianDistance {

    public static double getEuclidianDistance(double[] a, double[] b) {
        double[] weights = new double[a.length];
        Arrays.fill(weights, 1);
        return getEuclidianDistance(a, b, weights, a.length);
    }

    public static double getEuclidianDistance(double[] a, double[] b, double[] weights, int dim) {
        double sum = 0;
        for (int i = 0; i < dim; i++) {
            System.out.println("dim = " + dim);
            sum += weights[i] * Math.pow(a[i] - b[i], 2);
        }

        return Math.sqrt(sum);
    }

    public static double getEuclidianDistance(Integer[] a, Integer[] b) {
        double[] weights = new double[a.length];
        Arrays.fill(weights, 1);
        return getEuclidianDistance(a, b, weights);
    }

    public static double getEuclidianDistance(Integer[] a, Integer[] b, double[] weights) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += weights[i] * Math.pow(a[i] - b[i], 2);
        }

        return Math.sqrt(sum);
    }

    public static double getEuclidianDistance(Integer[][][] a, Integer[][][] b) {
        double[] weights = new double[a.length];
        Arrays.fill(weights, 1);
        return getEuclidianDistance(a, b, weights);
    }

    public static double getEuclidianDistance(Integer[][][] a, Integer[][][] b, double[] weights) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                for (int k = 0; k < a.length; k++) {
                    sum += weights[i] * Math.pow(a[i][j][k] - b[i][j][k], 2);
                }
            }
        }

        return Math.sqrt(sum);
    }

    public static double getEuclidianDistance(double[][][] a, double[][][] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                for (int k = 0; k < a.length; k++) {
                    sum += Math.pow(a[i][j][k] - b[i][j][k], 2);
                }
            }
        }

        return Math.sqrt(sum);
    }
}
