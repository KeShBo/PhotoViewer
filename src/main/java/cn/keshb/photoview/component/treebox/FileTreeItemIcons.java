package cn.keshb.photoview.component.treebox;

import cn.keshb.photoview.util.FileUtil;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FileTreeItemIcons {

    private final static Map<String, Image> ICON_MAP = new HashMap<>();

    static {
        final String iconFilePrefix = "icon-";
        final String iconFileSuffix = ".png";

        File dir = null;
        try {
            dir = new File(FileTreeItemIcons.class.getResource("/component/TreeBox").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (dir != null && dir.isDirectory()) {
            File[] files = dir.listFiles((dir1, name) -> name.startsWith(iconFilePrefix) && name.endsWith(iconFileSuffix));
            if (files != null) {
                for (File image : files) {
                    String name = image.getName();
                    try {
                        URL imageUrl = image.toURI().toURL();
                        ICON_MAP.put(name.substring(iconFilePrefix.length(), name.length() - 4), new Image(imageUrl.toString()));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static ImageView getIcon(File file) {
        String type = file.isDirectory() ? "folder" : FileUtil.getExtension(file);
        Optional<Image> image = Optional.ofNullable(ICON_MAP.get(type));
        return new ImageView(image.orElse(ICON_MAP.get("image")));
    }
}