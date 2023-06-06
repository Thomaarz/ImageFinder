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
import java.util.Scanner;

public class ImageFinder {

    @Getter
    private static List<String> imagesName;
    @Getter
    private static Map<Image, String> images;


    public static void main(String[] args) throws ImageException {

        imagesName = ImageUtil.loadDatabaseImagesName();
        images = ImageUtil.loadDatabaseImages(imagesName);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            try {
                Image image = ImageUtil.loadImage("database/" + input);
                ByteImage byteImage = new ByteImage(image);
                ImageUtil.displayImage(byteImage);

                List<Image> similarities = SimilarityUtil.searchSimilarities(byteImage, 5);
                similarities.forEach(ImageUtil::displayImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
