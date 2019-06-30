package cn.keshb.photoview.component;

import cn.keshb.photoview.util.ImageHelper;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCombination;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

/**
 * @author keshb
 */
public abstract class BaseContextMenu extends ContextMenu {

    protected final MenuItem copy = generateMenuItem("Copy", "icon_copy.png", "CTRL+C");
    protected final MenuItem copyName = generateMenuItem("Copy Name", null, "CTRL+SHIFT+C");
    protected final MenuItem rename = generateMenuItem("Rename", null, "CTRL+R");
    protected final MenuItem showInExplorer = generateMenuItem("Show in Explorer", null, null);

    public BaseContextMenu() {
        super();
        initEventHandler();
    }

    protected MenuItem generateMenuItem(String text, String iconPath, String accelerator) {
        MenuItem item = new MenuItem(text);
        if (iconPath != null) {
            item.setGraphic(new ImageView(ImageHelper.get("/ui/main/image/" + iconPath)));
        }
        if (accelerator != null) {
            item.setAccelerator(KeyCombination.valueOf(accelerator));
        }
        getItems().add(item);
        return item;
    }

    private void initEventHandler() {
        showInExplorer.setOnAction(event -> {
            String cmd = "explorer /e,/select," + selectedFile().getPath();
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        copy.setOnAction(event -> {
            ClipboardContent content = new ClipboardContent();
            File file = selectedFile();
            if (file.isDirectory()) {
                content.putFiles(Collections.singletonList(file));
            } else {
                try {
                    content.putImage(new Image(new FileInputStream(file)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            Clipboard.getSystemClipboard().setContent(content);
        });

        copyName.setOnAction(event -> {
            Transferable name = new StringSelection(selectedFile().getName());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(name, null);
        });

        rename.setOnAction(event -> rename());

    }

    /**
     * 重命名操作，留给子类实现
     */
    protected abstract void rename();

    protected abstract File selectedFile();
}
