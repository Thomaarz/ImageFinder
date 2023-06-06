package fr.thomas;

import fr.thomas.util.HistogramUtil;
import fr.thomas.exceptions.ImageException;
import fr.thomas.util.ImageUtil;
import fr.thomas.util.SimilarityUtil;
import fr.unistra.pelican.ByteImage;
import fr.unistra.pelican.Image;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class ImageFinder {

    @Getter
    private static List<String> imagesName;
    @Getter
    private static Map<Image, String> images;


    public static void main(String[] args) throws ImageException {

        imagesName = ImageUtil.loadDatabaseImagesName();
        images = ImageUtil.loadDatabaseImages(imagesName);

        Image image = ImageUtil.loadImage("database/196.jpg");

        ByteImage byteImage = new ByteImage(image);
        ImageUtil.displayImage(byteImage);

        double[] histogram = HistogramUtil.getHistogram(byteImage);
        HistogramUtil.displayHistogram(histogram, "Histogram");

        List<Image> similarities = SimilarityUtil.searchSimilarities(byteImage, 10);
        similarities.forEach(ImageUtil::displayImage);
    }
}
