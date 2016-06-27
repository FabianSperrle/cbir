package feature;

import converter.GrayScaleConverter;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import utils.Function2;
import utils.Tuple;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class HaralickTexture {
    private double[][][] p = new double[4][256][256];
    private double[][] px = new double[4][256];
    private double[][] py = new double[4][256];
    private double[][] px_plus_y = new double[4][512];
    private double[][] px_minus_y = new double[4][256];

    private final double EPSILON = 0.00000000001;
    
    public double[] getFeatures(BufferedImage im, Path p) {
        final double[] texture = getFeatures(im);
        String fileName = "cache/feature/haralick/" + String.valueOf(Math.abs(p.toAbsolutePath().toString().hashCode()));
        List<String> values = new LinkedList<>();
        for (double count : texture) {
            values.add(String.valueOf(count));
        }
        try {
            Files.write(Paths.get(fileName), values, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return texture;
    }

    public double[] getFeatures(BufferedImage img) {
        generateGrayLevelCoOccurrenceMatrix(img);

        double[] features = new double[24];
        List<Tuple<Double, Double>> results = new LinkedList<>();

        results.add(getAngularSecondMoment());
        results.add(getContrast());
        results.add(getCorrelation());
        results.add(getSoSVariance());
        results.add(getInverseDifferenceMoment());
        results.add(getSumAverage());
        results.add(getSumVariance());
        results.add(getSumEntropy());
        results.add(getEntropy());
        results.add(getDifferenceVariance());
        results.add(getDifferenceEntropy());

        for (int i = 0; i < results.size(); i++) {
            final Tuple<Double, Double> resultTuple = results.get(i);
            features[2 * i] = resultTuple.getLeft();
            features[2 * i + 1] = resultTuple.getRight();
        }

        return features;
    }

    private Tuple<Double, Double> getAngularSecondMoment() {
        List<Double> results = new LinkedList<>();

        for (int i = 0; i < 4; i++) {
            double result = 0;
            for (int j = 0; j < 256; j++) {
                for (int k = 0; k < 256; k++) {
                    result += Math.pow(p[i][j][k], 2);
                }
            }
            results.add(result);
        }
        return finalizeResult(results);
    }

    private Tuple<Double, Double> getContrast() {
        List<Double> results = new LinkedList<>();

        for (int i = 0; i < 4; i++) {
            double result = 0;
            for (int n = 0; n < 256; n++) {
                double res = 0;
                for (int j = 0; j < 256; j++) {
                    for (int k = 0; k < 256; k++) {
                        if (Math.abs(j - k) == n) {
                            res += p[i][j][k];
                        }
                    }
                }
                result += Math.pow(n, 2) * res;
            }
            results.add(result);
        }
        return finalizeResult(results);
    }

    private Tuple<Double, Double> getCorrelation() {
        List<Double> results = new LinkedList<>();

        for (int i = 0; i < 4; i++) {
            SummaryStatistics px_stats = new SummaryStatistics();
            SummaryStatistics py_stats = new SummaryStatistics();

            for (int j = 0; j < 256; j++) {
                px_stats.addValue(px[i][j]);
                py_stats.addValue(px[i][j]);
            }

            double means = px_stats.getMean() * py_stats.getMean();
            double stds = px_stats.getStandardDeviation() * py_stats.getStandardDeviation();

            double result = 0;
            for (int j = 0; j < 256; j++) {
                for (int k = 0; k < 256; k++) {
                    result += ((j * k) * p[i][j][k] - means) / (stds);
                }
            }
            results.add(result);
        }

        return finalizeResult(results);
    }

    private Tuple<Double, Double> getSoSVariance() {
        List<Double> results = new LinkedList<>();

        for (int i = 0; i < 4; i++) {
            SummaryStatistics p_stats = new SummaryStatistics();
            for (int j = 0; j < 256; j++) {
                for (int k = 0; k < 256; k++) {
                    p_stats.addValue(p[i][j][k]);
                }
            }
            double p_mean = p_stats.getMean();

            double result = 0;
            for (int j = 0; j < 256; j++) {
                double square = (j - p_mean) * (j - p_mean);
                for (int k = 0; k < 256; k++) {
                    result += square * p[i][j][k];
                }
            }
            results.add(result);
        }

        return finalizeResult(results);
    }

    private Tuple<Double, Double> getInverseDifferenceMoment() {
        List<Double> results = new LinkedList<>();

        for (int i = 0; i < 4; i++) {
            double result = 0;
            for (int j = 0; j < 256; j++) {
                for (int k = 0; k < 256; k++) {
                    result += p[i][j][k] / (1 + Math.pow(j - k, 2));
                }
            }
            results.add(result);
        }

        return finalizeResult(results);
    }

    private Tuple<Double, Double> getSumAverage() {
        List<Double> results = new LinkedList<>();

        for (int i = 0; i < 4; i++) {
            double result = 0;
            for (int k = 0; k < 256 * 2 - 1; k++) {
                result += k * px_plus_y[i][k];
            }
            results.add(result);
        }

        return finalizeResult(results);
    }

    private Tuple<Double, Double> getSumVariance() {
        List<Double> results = new LinkedList<>();

        for (int i = 0; i < 4; i++) {
            double result = 0;
            double sumEntropy = 0;
            for (int k = 0; k < 256 * 2 - 1; k++) {
                sumEntropy += k * px_plus_y[i][k] * Math.log(px_plus_y[i][k] + EPSILON);
            }
            sumEntropy *= -1;

            for (int k = 0; k < 256 * 2 - 1; k++) {
                result += Math.pow(k - sumEntropy, 2) * px_plus_y[i][k];
            }
            results.add(result);
        }

        return finalizeResult(results);
    }

    private Tuple<Double, Double> getSumEntropy() {
        List<Double> results = new LinkedList<>();

        for (int i = 0; i < 4; i++) {
            double result = 0;
            for (int k = 0; k < 256 * 2 - 1; k++) {
                result += k * px_plus_y[i][k] * Math.log(px_plus_y[i][k] + EPSILON);
            }
            results.add(result * (-1));
        }

        return finalizeResult(results);
    }

    private Tuple<Double, Double> getEntropy() {
        List<Double> results = new LinkedList<>();

        for (int i = 0; i < 4; i++) {
            double result = 0;

            for (int j = 0; j < 256; j++) {
                for (int k = 0; k < 256; k++) {
                    result += p[i][j][k] * Math.log(p[i][j][k] + EPSILON);
                }
            }
            results.add(result * (-1));
        }

        return finalizeResult(results);
    }

    private Tuple<Double, Double> getDifferenceVariance() {
        List<Double> results = new LinkedList<>();

        for (int i = 0; i < 4; i++) {
            SummaryStatistics p_stats = new SummaryStatistics();

            for (int j = 0; j < 256; j++) {
                p_stats.addValue(px_minus_y[i][j]);
            }

            results.add(p_stats.getVariance());
        }

        return finalizeResult(results);
    }

    private Tuple<Double, Double> getDifferenceEntropy() {
        List<Double> results = new LinkedList<>();

        for (int i = 0; i < 4; i++) {
            double result = 0;
            for (int k = 0; k < 256; k++) {
                result += k * px_minus_y[i][k] * Math.log(px_minus_y[i][k] + EPSILON);
            }
            results.add(result * (-1));
        }

        return finalizeResult(results);
    }


    private Tuple<Double, Double> finalizeResult(List<Double> results) {
        double sum = 0;
        double min = Integer.MAX_VALUE;
        double max = Integer.MIN_VALUE;
        for (Double result : results) {
            sum += result;
            if (result > max) {
                max = result;
            } else if (result < min) {
                min = result;
            }
        }
        double mean = sum / 4;
        double range = max - min;

        return new Tuple<>(mean, range);
    }

    private double[][][] generateGrayLevelCoOccurrenceMatrix(BufferedImage img) {
        BufferedImage gray = img;
        if (img.getType() != BufferedImage.TYPE_BYTE_GRAY) {
            gray = GrayScaleConverter.RGB2GrayByBT601(img);
        }

        List<Function2<Integer, Integer, Tuple<Integer, Integer>>> directions = new LinkedList<>();
        // west <-> east
        Function2<Integer, Integer, Tuple<Integer, Integer>> we = (x, y) -> new Tuple<>(x + 1, y);
        // north <-> south
        Function2<Integer, Integer, Tuple<Integer, Integer>> ns = (x, y) -> new Tuple<>(x, y + 1);
        // north-east <-> south-west
        Function2<Integer, Integer, Tuple<Integer, Integer>> nesw = (x, y) -> new Tuple<>(x + 1, y + 1);
        // north-west <-> south-east
        Function2<Integer, Integer, Tuple<Integer, Integer>> nwse = (x, y) -> new Tuple<>(x + 1, y - 1);

        directions.add(we);
        directions.add(ns);
        directions.add(nesw);
        directions.add(nwse);

        for (int i = 0; i < directions.size(); i++) {
            Function2<Integer, Integer, Tuple<Integer, Integer>> function2 = directions.get(i);
            int count = 0;

            for (int x = 0; x < gray.getWidth(); x++) {
                for (int y = 0; y < gray.getHeight(); y++) {
                    // Gray scale image, so we can get any of the three color components
                    final int level = new Color(gray.getRGB(x, y)).getRed();
                    // Get the neighbor coordinates w.r.t. the current co-occurrence matrix
                    final Tuple<Integer, Integer> neighbor = function2.apply(x, y);
                    try {
                        final int neighborLevel = new Color(gray.getRGB(neighbor.getLeft(), neighbor.getRight())).getRed();

                        p[i][level][neighborLevel] += 2;
                        count += 2;
                        if (level != neighborLevel) {
                            p[i][neighborLevel][level] += 2;
                            count += 2;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        // it's fine...
                    }
                }
            }

            // Normalize the values
            for (int x = 0; x < 256; x++) {
                for (int y = 0; y < 256; y++) {
                    p[i][x][y] = p[i][x][y] / count;
                }
            }

            for (int x = 0; x < 256; x++) {
                double sum = 0;
                for (int y = 0; y < 256; y++) {
                    sum += p[i][x][y];
                }
                px[i][x] = sum;
            }

            for (int y = 0; y < 256; y++) {
                double sum = 0;
                for (int x = 0; x < 256; x++) {
                    sum += p[i][x][y];
                }
                py[i][y] = sum;
            }

            for (int k = 0; k < 256 * 2 - 1; k++) {
                for (int x = 0; x < 256; x++) {
                    for (int y = 0; y < 256; y++) {
                        if (x + y == k) {
                            px_plus_y[i][k] += p[i][x][y];
                        }
                        if (Math.abs(x - y) == k) {
                            px_minus_y[i][k] += p[i][x][y];
                        }
                    }
                }
            }
        }

        return p;
    }
}
