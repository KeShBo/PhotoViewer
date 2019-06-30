package cn.keshb.photoview;

import cn.keshb.photoview.component.ImagePane;
import cn.keshb.photoview.component.treebox.FileTreeItem;
import cn.keshb.photoview.component.treebox.TreeBox;
import cn.keshb.photoview.util.ImageHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author keshb
 */
public class Controller implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private TreeBox treeBox;
    @FXML
    private MenuItem menuOpen;

    private ImagePane imagePane = new ImagePane();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imagePane.prefHeightProperty().bind(borderPane.heightProperty());
        imagePane.prefWidthProperty().bind(borderPane.widthProperty().subtract(treeBox.widthProperty()));
        borderPane.setCenter(imagePane);

        menuOpen.setGraphic(new ImageView(ImageHelper.get("/ui/main/image/icon_open_folder.png")));

        initTreeBox();
    }

    private void initTreeBox() {
        treeBox.getTreeView().setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                FileTreeItem item = ((FileTreeItem) treeBox.getTreeView().getSelectionModel().getSelectedItem());
                if (item != null && !item.isDirFile()) {
                    imagePane.showImage(item);
                }
            }
        });

        final ContextMenu contextMenu = treeBox.new TreeBoxContextMenu() {
            @Override
            protected void showAll() {
                FileTreeItem item = (FileTreeItem) treeBox.getTreeView().getSelectionModel().getSelectedItem();
                if (item.isDirFile()) {
                    imagePane.showImages(item);
                }
            }
        };
        contextMenu.setOnShown(event -> contextMenu.requestFocus());
        treeBox.setContextMenu(contextMenu);
    }


    public void openFile(final ActionEvent event) {
        final DirectoryChooser chooser = new DirectoryChooser();
        File file = chooser.showDialog(borderPane.getScene().getWindow());
        if (file != null) {
            treeBox.setDir(file);
            borderPane.setCenter(imagePane);
        }
    }

    public void openUrl(final ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setTitle("Open URL...");
        dialog.setGraphic(null);
        dialog.showAndWait().ifPresent(url -> imagePane.setUrl(url));
    }

    public void exit(ActionEvent event) {
        Stage stage = (Stage) borderPane.getScene().getWindow();
        // Platform.setImplicitExit()属性默认true，当所有窗口关闭会结束程序
        stage.close();
    }
}
