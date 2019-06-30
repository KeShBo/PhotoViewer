package cn.keshb.photoview.util;

import java.io.File;

/**
 * 文件路径工具类
 */
public class FileUtil {

    public static String getNameWithoutExtension(String filename) {
        int index = filename.lastIndexOf(".");
        return index > -1 ? filename.substring(0, index) : null;
    }

    public static String getExtension(File file) {
        return getExtension(file.getName());
    }

    public static String getExtension(String name) {
        if (name == null) {
            return null;
        }
        int index = name.lastIndexOf(".");
        return index > -1 ? name.substring(index + 1) : null;
    }

    public static String getFilename(String path) {
        if (path == null) {
            return null;
        }

        path = trimSeparator(path);
        int index = path.lastIndexOf(File.separator);
        return index > -1 ? path.substring(index + 1) : null;
    }

    public static String getParentPath(String path) {
        if (path == null) {
            return null;
        }

        path = trimSeparator(path);
        int index = path.lastIndexOf(File.separator);
        return index > -1 ? path.substring(0, index) : null;

    }

    private static String trimSeparator(String path) {
        while (path.charAt(path.length() - 1) == File.separator.charAt(0)) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public static void main(String[] args) {
        String s = "G:\\KuGou\\Temp\\";
        System.out.println(new File(s).getPath());
    }
}
