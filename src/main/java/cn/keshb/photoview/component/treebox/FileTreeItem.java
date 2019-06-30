package cn.keshb.photoview.component.treebox;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

import java.io.File;

/**
 * @author keshb
 */
public class FileTreeItem extends TreeItem<String> {

    private File file;

    FileTreeItem(final File file) {
        super(file.getName(), FileTreeItemIcons.getIcon(file));
        this.file = file;
    }

    FileTreeItem(final File file, final Node icon) {
        super(file.getName(), icon);
        this.file = file;
    }

    public String getPath() {
        return file.getPath();
    }

    public File getFile() {
        return file;
    }

    public void updateFile(File file) {
        this.file = file;
        setValue(file.getName());
    }

    public boolean isDirFile() {
        return getFile().isDirectory();
    }

}
