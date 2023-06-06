package fr.thomas.util;

import fr.thomas.exceptions.HistogramException;
import fr.unistra.pelican.ByteImage;
import fr.unistra.pelican.Image;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HistogramUtil {

    /**
     * Display a histogram from a table
     * @param histogram: the histogram table of an image
     * @param name: the displayed name of the frame
     */
    public static void displayHistogram(double[] histogram, String name) {
        JFreeChart freeChart = getFreeChart(histogram);
        XYPlot xyplot = freeChart.getXYPlot();

        xyplot.setBackgroundPaint(Color.lightGray);
        xyplot.setRangeGridlinePaint(Color.white);
        NumberAxis axis = (NumberAxis) xyplot.getDomainAxis();

        axis.setLowerMargin(0);
        axis.setUpperMargin(0);

        // Display the frame
        String frameName = name != null ? "Histogram: " + name : "Histogram";
        ChartFrame frame = new ChartFrame(frameName, freeChart);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Calculate a histogram from an image and display it
     * @param image: the image
     */
    public static void displayHistogram(ByteImage image) {
        displayHistogram(image, image.getName());
    }

    /**
     * Calculate a histogram from an image and display it
     * @param image: the image
     * @param name: the displayed name of the frame
     */
    public static void displayHistogram(ByteImage image, String name) {
        double[] histogram = getHistogram(image);
        displayHistogram(histogram, name);
    }

    /**
     * Display RGB histogram
     * @param histogram: the histogram to display
     */
    public static void displayHistogram(double[][] histogram) {
        for (int i = 0; i < histogram.length; i++) {
            displayHistogram(histogram[i], "Histogram [" + i + "]");
        }
    }

    /**
     * Save a histogram as a PNG file
     * @param histogram: the histogram to save
     * @param fileName: the histogram file name
     * @throws HistogramException when save failed
     */
    public static void saveHistogram(double[] histogram, String fileName) throws HistogramException {
        JFreeChart freeChart = getFreeChart(histogram);
        XYPlot xyplot = freeChart.getXYPlot();

        xyplot.setBackgroundPaint(Color.lightGray);
        xyplot.setRangeGridlinePaint(Color.white);
        NumberAxis axis = (NumberAxis) xyplot.getDomainAxis();

        axis.setLowerMargin(0);
        axis.setUpperMargin(0);

        try {
            String filePath = HistogramUtil.class.getClassLoader().getResource("saved_histogram").getPath();
            ChartUtilities.saveChartAsPNG(new File(filePath + "/" + fileName), freeChart, 900, 600);
        } catch (IOException e) {
            throw new HistogramException("Unable to save histogram with name: " + fileName);
        }
    }

    /**
     * Calculate a histogram of an image
     * @param image: the image
     * @return the calculated histogram as a table of int
     */
    public static double[] getHistogram(ByteImage image) {
        double[] histogram = new double[256];

        for (int y = 0; y < image.getYDim(); y++) {
            for (int x = 0; x < image.getXDim(); x++) {
                int grayValue = image.getPixelXYBByte(x, y, 0);
                histogram[grayValue]++;
            }
        }

        return histogram;
    }

    public static double[][] getHistogramRGB(ByteImage image) {
        double[][] histogram = new double[3][256];

        for (int y = 0; y < image.getYDim(); y++) {
            for (int x = 0; x < image.getXDim(); x++) {
                for (int rgb = 0; rgb < 3; rgb++) {
                    try {
                        int value = image.getPixelXYBByte(x, y, rgb);
                        histogram[rgb][value]++;
                    } catch (Exception e) {

                    }
                }
            }
        }
        return histogram;
    }

    /**
     * Reduce a histogram in multiple partitions
     * @param histogram: the histogram
     * @param partitions: the amount of partitions
     * @return the reduced histogram
     */
    public static double[] reduceHistogram(double[] histogram, int partitions) {
        if (partitions < 1) {
            return histogram;
        }

        double[] reduceHistogram = new double[partitions];
        int partitionSize = histogram.length / partitions;

        int index;
        for (int partition = 0; partition < partitions; partition++) {
            index = partition * partitionSize;

            for (int i = index; i < index + partitionSize; i++) {
                reduceHistogram[partition] += histogram[i];
            }
        }

        return reduceHistogram;
    }


    public static double[][] reduceHistogram(double[][] histogram, int partitions) {
        double[][] histo = new double[histogram.length][histogram[0].length];

        for (int i = 0; i < histogram.length; i++) {
            histo[i] = reduceHistogram(histogram[i], partitions);
        }

        return histo;
    }

    /**
     * Standardize a histogram with the frequency of each pixel
     * @param histogram: the histogram
     * @return the standardized histogram
     */
    public static double[] standardizeHistogram(double[] histogram) {
        double[] standardizedHistogram = new double[histogram.length];
        int pixelsAmount = SimilarityUtil.getPixelsAmount(histogram);

        for (int i = 0; i < histogram.length; i++) {
            standardizedHistogram[i] = histogram[i] / pixelsAmount;
        }
        return standardizedHistogram;
    }

    public static double[][] standardizeHistogram(double[][] histogram) {
        double[][] histo = new double[histogram.length][histogram[0].length];

        for (int i = 0; i < histogram.length; i++) {
            histo[i] = standardizeHistogram(histogram[i]);
        }
        return histo;
    }

    /**
     * Get the SeriesCollection from a histogram
     * @param histogram: the histogram
     * @return the SerieCollection object
     */
    private static XYSeriesCollection getSeriesCollection(double[] histogram) {
        XYSeries series = new XYSeries("Nombre de pixels");
        for(int i = 0; i < histogram.length; i++) {
            series.add(new Double(i), new Double(histogram[i]));
        }
        return new XYSeriesCollection(series);
    }

    /**
     * Get the FreeChart from a histogram
     * @param histogram: the histogram
     * @return the FreeChart object
     */
    private static JFreeChart getFreeChart(double[] histogram) {
        JFreeChart freeChart = ChartFactory.createXYBarChart(
                "Histogramme de l'image",
                "Niveaux de gris",
                false,
                "Nombre de pixels",
                getSeriesCollection(histogram),
                PlotOrientation.VERTICAL,
                true,
                false,
                false);
        freeChart.setBackgroundPaint(Color.white);
        return freeChart;
    }
}
