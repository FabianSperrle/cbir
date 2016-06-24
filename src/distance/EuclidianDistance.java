package distance;

/**
 * Created by fabian on 24.06.2016.
 */
public class EuclidianDistance {

    public static double getEuclidianDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] + b[i], 2);
        }

        return Math.sqrt(sum);
    }

    public static double getEuclidianDistance(int[] a, int[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] + b[i], 2);
        }

        return Math.sqrt(sum);
    }

    public static double getEuclidianDistance(int[][][] a, int[][][] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                for (int k = 0; k < a.length; k++) {
                    sum += Math.pow(a[i][j][k] + b[i][j][k], 2);
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
                    sum += Math.pow(a[i][j][k] + b[i][j][k], 2);
                }
            }
        }

        return Math.sqrt(sum);
    }
}
