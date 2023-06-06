package fr.thomas.util;

import fr.thomas.ImageFinder;
import fr.thomas.exceptions.HistogramException;
import fr.unistra.pelican.ByteImage;
import fr.unistra.pelican.Image;

import java.util.*;

public class SimilarityUtil {

    public static List<Image> searchSimilarities(ByteImage from, int amount) {
        List<Image> images = new ArrayList<>();

        Map<Image, String> databaseImages = ImageFinder.getImages();
        Map<Image, Double> similarities = new HashMap<>();

        double[][] fromHistogram = HistogramUtil.getHistogramRGB(from);

        for (Map.Entry<Image, String> image : databaseImages.entrySet()) {
            ByteImage byteImage = new ByteImage(image.getKey());
            double[][] toHistogram = HistogramUtil.getHistogramRGB(byteImage);
            double distance = calculateEuclideanDistance(fromHistogram, toHistogram);
            similarities.put(image.getKey(), distance);
        }

        similarities = sortByValue(similarities);

        for (int i = 0; i < amount; i++) {
            try {
                Image similar = similarities.entrySet().iterator().next().getKey();
                similarities.remove(similar);
                String name = databaseImages.get(similar);
                images.add(similar);
                System.out.println(name);
            } catch (Exception e) {

            }
        }

        return images;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static double calculateEuclideanDistance(double[] histogram1, double[] histogram2) {
        double distance = 0.0;

        for (int i = 0; i < histogram1.length; i++) {
            distance += Math.pow(histogram1[i] - histogram2[i], 2);
        }

        distance = Math.sqrt(distance);

        return distance;
    }


    public static double calculateEuclideanDistance(double[][] histogram1, double[][] histogram2) {
        double distance = 0.0;

        if (histogram1.length != histogram2.length) {
            return distance;
        }

        for (int i = 0; i < histogram1.length; i++) {
            distance += calculateEuclideanDistance(histogram1[i], histogram2[i]);
        }

        return distance;
    }

    /**
     * Get the amount of pixels in a histograp
     * @param histogram: the histogram
     * @return the amount of pixels
     */
    public static int getPixelsAmount(double[] histogram) {
        int amount = 0;
        for (double i : histogram) {
            amount += i;
        }
        return amount;
    }
}
