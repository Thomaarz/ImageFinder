package fr.thomas.util;

import fr.thomas.exceptions.ImageException;
import fr.unistra.pelican.ByteImage;
import fr.unistra.pelican.Image;
import fr.unistra.pelican.algorithms.io.ImageLoader;
import fr.unistra.pelican.algorithms.visualisation.Viewer2D;

import java.io.File;
import java.util.*;

public class ImageUtil {

    public static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList("png", "jpg");

    /**
     * Convert a colored image to a binary image
     * @param image: the image
     * @param threshold: the color threshold
     * @return the new image to binary
     */
    public static ByteImage convertToBinary(Image image, int threshold, int color1, int color2) {
        int width = image.getXDim();
        int height = image.getYDim();

        ByteImage binaryImage = new ByteImage(width, height, 1, 1, 1);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int grayValue = image.getPixelXYBByte(x, y, 0);

                if (grayValue < threshold) {
                    binaryImage.setPixelXYBByte(x, y, 0, color1);
                } else {
                    binaryImage.setPixelXYBByte(x, y, 0, color2);
                }
            }
        }
        return binaryImage;
    }

    /**
     * Convert a colored image to a black and white image
     * @param image: the image
     * @param threshold: the color treshold
     * @return the new image to binary
     */
    public static ByteImage convertToBinary(Image image, int threshold) {
        return convertToBinary(image, threshold, 0, 255);
    }

    /**
     * Load an image
     * @param name: the image file name
     * @return the loaded image
     * @throws ImageException if image is not found
     */
    public static Image loadImage(String name) throws ImageException {
        try {
            String imagePath = ImageLoader.class.getClassLoader().getResource(name).getPath();
            return ImageLoader.exec(imagePath);
        } catch (Exception e) {
            throw new ImageException("Unable to find image: " + name);
        }
    }

    /**
     * Get every image file name contained in the 'database' folder
     * @return the list of images name
     * @throws ImageException if database folder doesn't exist
     */
    public static List<String> loadDatabaseImagesName() throws ImageException {
        List<String> images = new ArrayList<>();
        File folder = new File(ImageLoader.class.getClassLoader().getResource("database").getPath());

        if (!folder.isDirectory()) {
            throw new ImageException("Unable to load images, database folder doesn't found");
        }

        File[] files = folder.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                for (String extensions : SUPPORTED_EXTENSIONS) {
                    if (fileName.contains("." + extensions)) {
                        images.add(fileName);
                        break;
                    }
                }
            }
        }
        return images;
    }

    /**
     * Load Image objects
     * @param imagesName: the list of the images names
     * @return a list of Images object
     */
    public static Map<Image, String> loadDatabaseImages(List<String> imagesName) {
        Map<Image, String> images = new HashMap<>();

        for (int i = 0; i < imagesName.size(); i++) {
            String name = imagesName.get(i);
            try {
                Image image = ImageUtil.loadImage(name.contains("database/") ? name : "database/" + name);
                images.put(image, name);
                System.out.println("[LOAD] (" + (i + 1) + "/" + imagesName.size() + ") Load image: " + name);
            } catch (ImageException e) {
                System.out.println("[LOAD] (" + (i + 1) + "/" + imagesName.size() + ") [FAILED] Unable to load image for name: " + name);
            }
        }
        return images;
    }

    /**
     * Display an image
     * @param image: the image to display
     */
    public static void displayImage(ByteImage image) {
        Viewer2D.exec(image);
    }

    /**
     * Display an image
     * @param image: the image to display
     */
    public static void displayImage(Image image) {
        Viewer2D.exec(new ByteImage(image));
    }
}
