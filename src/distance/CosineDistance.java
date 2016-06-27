package distance;

import java.util.Arrays;

/**
 * Created by fabian on 27.06.2016.
 */
public class CosineDistance {

    public static double cosineDistance(double[] vector1, double[] vector2) {
        System.out.println("Arrays.toString(vector1) = " + Arrays.toString(vector1));
        System.out.println("Arrays.toString(vector1) = " + Arrays.toString(vector2));
        double dot = 0;
        double e1 = 0;
        double e2 = 0;
        for (int i = 0; i < vector1.length; i++) {
            dot += vector1[i] * vector2[i];
            e1 += Math.pow(vector1[i], 2);
            e2 += Math.pow(vector2[i], 2);
        }
        e1 = Math.sqrt(e1);
        e2 = Math.sqrt(e2);

        dot = dot / (e1 * e2);
        if (Double.isNaN(dot)) {
            throw new RuntimeException(Arrays.toString(vector1) + " " + Arrays.toString(vector2));
        }

        System.out.println("1-dot = " + (1 - dot) + " --- " + dot);

        return 1 - dot;
    }
}
