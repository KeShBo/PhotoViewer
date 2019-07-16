package cn.keshb.photoview.component.treebox;

import cn.keshb.photoview.util.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author keshb
 */
public class FileTreeFilter implements FileFilter {

    private Set<String> set = new HashSet<>();

    FileTreeFilter() {
        set.addAll(Arrays.asList("jpg", "png", "gif", "bmp"));
    }

    @Override
    public boolean accept(File file) {
        return file.isDirectory() || set.contains(FileUtil.getExtension(file));
    }

    public void addExtension(String extension) {
        set.add(extension);
    }

    public void removeExtension(String extension) {
        set.remove(extension);
    }
}