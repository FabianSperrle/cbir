package distance;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import utils.HumanPerceptionSimilarityMatrix;

import java.util.Arrays;

/**
 * Created by fabian on 24.06.2016.
 */
public class QuadraticFormDistance {

    public static double getQuadricFormDistance(double[] p, double[] q) {
        return getQuadricFormDistance(p, q, p.length, Integer.MAX_VALUE);
    }

    public static double getQuadricFormDistance(double[] p, double[] q, int dim) {
        return getQuadricFormDistance(p, q, p.length, dim);
    }

    public static double getQuadricFormDistance(double[] p, double[] q, int bins, int dim) {
        final Array2DRowRealMatrix similarityMatrix= new Array2DRowRealMatrix(HumanPerceptionSimilarityMatrix.createHumanPerceptionSimilarityMatrix(bins));
        final EigenDecomposition evd = new EigenDecomposition(similarityMatrix);
        final RealMatrix v = evd.getV();
        final double[] eigenvalues = evd.getRealEigenvalues();

        p = v.preMultiply(p);
        q = v.preMultiply(q);

        return EuclidianDistance.getEuclidianDistance(p, q, eigenvalues, Math.min(dim, p.length));
    }

    public static void main(String[] args) {
        double[] a = {1,2,3,4,5,6,7,8,9};
        double[] b = {2,2,3,4,5,6,7,8,9};
        final double quadricFormDistance = getQuadricFormDistance(a, b, a.length, 5);
        System.out.println("quadricFormDistance = " + quadricFormDistance);
    }
}
