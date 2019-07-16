package cn.keshb.photoview.component.treebox;

import cn.keshb.photoview.component.BaseContextMenu;
import cn.keshb.photoview.component.SearchField;
import cn.keshb.photoview.util.FileUtil;
import cn.keshb.photoview.util.ImageHelper;
import cn.keshb.photoview.util.TaskThreadPool;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


/**
 * @author keshb
 */
public class TreeBox extends VBox implements Initializable {

    @FXML
    private ToolBar toolBar;
    @FXML
    private SearchField searchField;
    @FXML
    private TreeView<String> treeView;

    private FileFilter fileFilter = new FileTreeFilter();

    private File rootFile;

    public TreeBox() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/component/TreeBox/tree-box.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 绑定控件的宽
        toolBar.prefWidthProperty().bind(this.widthProperty());
        searchField.prefWidthProperty().bind(this.widthProperty());
        treeView.prefHeightProperty().bind(this.heightProperty()
                .subtract(searchField.heightProperty())
                .subtract(toolBar.heightProperty()));

    }

    public void setContextMenu(final ContextMenu contextMenu) {
        treeView.setContextMenu(contextMenu);
    }

    public void setDir(File file) {
        this.rootFile = file;
        FileTreeItem rootItem = new FileTreeItem(file);
        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);

        TaskThreadPool.execute(new FileTreeLoader());
    }

    public ToolBar getToolBar() {
        return toolBar;
    }

    public SearchField getSearchField() {
        return searchField;
    }

    public TreeView getTreeView() {
        return treeView;
    }

    public abstract class TreeBoxContextMenu extends BaseContextMenu {

        private final MenuItem showAll = generateMenuItem("Show All", null, null);

        protected TreeBoxContextMenu() {
            super();
            setOnShowing(event -> {
                File file = selectedFile();
                ObservableList<MenuItem> items = getItems();
                if (file.isDirectory() && !items.contains(showAll)) {
                    items.add(showAll);
                } else if (!file.isDirectory()) {
                    items.remove(showAll);
                }
            });

            showAll.setOnAction(event -> {
                FileTreeItem item = (FileTreeItem) treeView.getSelectionModel().getSelectedItem();
                if (item.isDirFile()) {
                    showAll();
                }
            });
        }

        /**
         * show all菜单的点击事件
         */
        protected abstract void showAll();

        @Override
        protected void rename() {
            FileTreeItem item = (FileTreeItem) treeView.getSelectionModel().getSelectedItem();
            File file = item.getFile();
            String extension = FileUtil.getExtension(file);

            TextInputDialog dialog = new TextInputDialog(FileUtil.getNameWithoutExtension(file.getName()));
            Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(ImageHelper.get("/ui/main/image/icon_window.png"));
            dialog.setHeaderText(null);
            dialog.setGraphic(null);
            dialog.setTitle("Rename");
            dialog.getDialogPane().getStylesheets().add(
                    getClass().getResource("/ui/main/css/dialog.css").toExternalForm());
            dialog.getDialogPane().getStyleClass().add("my-text-input-dialog");
            ((GridPane) dialog.getDialogPane().getContent()).setHgap(0);

            dialog.showAndWait().ifPresent(name -> {
                File dest = new File(file.getParent() + File.separator + name + "." + extension);
                if (file.renameTo(dest)) {
                    item.updateFile(dest);
                }
            });
        }

        @Override
        protected File selectedFile() {
            return ((FileTreeItem) treeView.getSelectionModel().getSelectedItem()).getFile();
        }
    }


    final class FileTreeLoader extends Task {

        @Override
        protected Object call() {
            List<FileTreeItem> curItems = new ArrayList<>();
            curItems.add((FileTreeItem) treeView.getRoot());

            while (curItems.size() > 0) {
                // 当前目录层级所有文件夹的子文件
                List<FileTreeItem> allChildren = new ArrayList<>();
                for (FileTreeItem item : curItems) {
                    File curFile = new File(item.getPath());
                    if (curFile.isDirectory()) {
                        // 当前文件夹的子文件
                        List<FileTreeItem> curChildren = new ArrayList<>();

                        for (File childFile : listSortedName(curFile)) {
                            Node icon = FileTreeItemIcons.getIcon(childFile);
                            FileTreeItem child = new FileTreeItem(childFile, icon);
                            curChildren.add(child);
                        }

                        Platform.runLater(() -> {
                            item.setExpanded(true);
                            for (FileTreeItem child : curChildren) {
                                item.getChildren().add(child);
                            }
                        });
                        allChildren.addAll(curChildren);
                    }
                }
                curItems = allChildren;
            }
            return null;
        }


        private File[] listSortedName(File curFile) {
            File[] children = curFile.listFiles(fileFilter);
            if (children != null) {
                int length = children.length;
                File[] result = new File[length];

                int cursor = 0;
                File child;
                for (int i = 0; i < length; i++) {
                    child = children[i];
                    if (child != null && child.isDirectory()) {
                        result[cursor++] = child;
                        children[i] = null;
                    }
                }

                for (File f : children) {
                    if (f != null) {
                        result[cursor++] = f;
                    }
                }
                return result;
            }
            return new File[0];
        }
    }
}
