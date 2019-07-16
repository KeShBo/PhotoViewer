package cn.keshb.photoview.util;

import javafx.scene.image.Image;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Image帮助类，方便构造Image
 * @author keshb
 */
public class ImageHelper {

    public static Image get(String path) {
        return new Image(ImageHelper.class.getResourceAsStream(path));
    }

    public static Image get(String path, double width, double height) {
        return new Image(ImageHelper.class.getResourceAsStream(path), width, height, true, true);
    }

    public static Image get(File file) {
        try {
            return new Image(file.toURI().toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
