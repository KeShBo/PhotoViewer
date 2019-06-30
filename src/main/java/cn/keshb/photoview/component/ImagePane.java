package cn.keshb.photoview.component;

import cn.keshb.photoview.component.treebox.FileTreeItem;
import cn.keshb.photoview.util.ImageHelper;
import cn.keshb.photoview.util.NumericUtil;
import cn.keshb.photoview.util.TaskThreadPool;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author keshb
 */
public class ImagePane extends BorderPane implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private StackPane infoPane;
    @FXML
    private Button backButton;
    @FXML
    private Label infoLabel;
    @FXML
    private ScrollPane viewContainer;
    @FXML
    private BorderPane viewPane;
    @FXML
    private ImageView imageView;
    @FXML
    private Button leftArrowButton;
    @FXML
    private Button rightArrowButton;
    @FXML
    private ToolBar toolBar;

    private FlowViewsPane flowViewsPane = new FlowViewsPane();

    private ImageContextMenu contextMenu = new ImageContextMenu();

    private FileTreeItem displayItem;

    private static final double RATE = 1.1;

    public ImagePane() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/component/ImagePane/image-pane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUrl(String url) {
        Image image = new Image(url);
        infoLabel.setText(url + " (" + (int) image.getWidth() + "x" + (int) image.getWidth() + "像素)");
        setImage(image);
    }


    /**
     * 显示文件夹图片
     *
     * @param item item
     */
    public void showImages(FileTreeItem item) {
        // 当前显示单张图片，改为文件夹图片显示面板
        if (!isDirShowing()) {
            setCenter(flowViewsPane);
        }

        // 如果已经有图片则清空
        if (!flowViewsPane.isEmpty()) {
            flowViewsPane.clear();
        }

        // 执行Task，普通线程执行读取操作，UI线程执行图片渲染操作
        TaskThreadPool.executeAsTask(() -> flowViewsPane.showImages(item));
    }

    /**
     * 加载图片文件并设置图片信息
     */
    public void showImage(FileTreeItem item) {
        if (item != null) {
            // 当前显示文件夹图片，改回单张图片显示面板
            if (isDirShowing()) {
                setCenter(stackPane);
            }

            this.displayItem = item;
            File file = item.getFile();

            Image image = null;
            try {
                image = new Image(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (image != null) {
                setImageInfo(file.getName(), file.length(), image.getWidth(), image.getHeight());
                setImage(image);
            }
        }
    }

    private boolean isDirShowing() {
        return getCenter() == flowViewsPane;
    }

    private boolean isNetImage() {
        return imageView.getImage().getUrl() != null;
    }

    /**
     * 设置图片
     */
    private void setImage(Image image) {
        if (image != null) {
            double width = image.getWidth();
            double height = image.getHeight();

            // 上下左右空40像素
            double paneFitWidth = viewContainer.getPrefWidth() - 40;
            double paneFitHeight = viewContainer.getPrefHeight() - 20;

            // 图片比规定尺寸小就原图显示,否则按比例缩小到规定大小
            if (width < paneFitWidth && height < paneFitHeight) {
                imageView.setFitHeight(height);
                imageView.setFitWidth(width);
                imageView.setPreserveRatio(false);
            } else {
                imageView.setFitWidth(paneFitWidth);
                imageView.setFitHeight(paneFitHeight);
                imageView.setPreserveRatio(true);
            }
            // 复位居中
            resetPosition();
            imageView.setImage(image);
        }
    }

    /**
     * 图片复位到正中间
     */
    private void resetPosition() {
        imageView.setTranslateX(0);
        imageView.setTranslateY(0);
    }

    /**
     * 显示图片信息
     */
    private void setImageInfo(final String imageName, final long size, final double width, final double height) {
        final long kbLimit = 1024;
        final long mbLimit = 1048576;
        final long gbLimit = 1073741824;
        String sizeStr;
        if (size >= gbLimit) {
            sizeStr = NumericUtil.retain((double) size / gbLimit, 2) + "GB";
        } else if (size >= mbLimit) {
            sizeStr = NumericUtil.retain((double) size / mbLimit, 2) + "MB";
        } else if (size >= kbLimit) {
            sizeStr = NumericUtil.retain((double) size / kbLimit, 2) + "KB";
        } else {
            sizeStr = size + "KB";
        }
        infoLabel.setText(imageName + " (" + sizeStr + " , " + (int) width + "x" + (int) height + "像素)");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 设置上下张按钮位置
        StackPane.setAlignment(leftArrowButton, Pos.CENTER_LEFT);
        StackPane.setMargin(leftArrowButton, new Insets(0, 0, 0, 20));
        StackPane.setAlignment(rightArrowButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(rightArrowButton, new Insets(0, 20, 0, 0));
        // 设置图片信息Label居中
        StackPane.setAlignment(infoLabel, Pos.CENTER);
        StackPane.setAlignment(backButton, Pos.CENTER_LEFT);

        // 返回按钮，返回文件夹图片面板
        backButton.setOnAction(event -> {
            setCenter(flowViewsPane);
            flowViewsPane.updateLabelInfo();
            backButton.setVisible(false);
        });

        // 绑定大小
        stackPane.prefWidthProperty().bind(this.widthProperty());
        stackPane.prefHeightProperty().bind(this.heightProperty());
        viewPane.prefHeightProperty().bind(viewContainer.prefViewportHeightProperty());
        viewPane.prefWidthProperty().bind(viewContainer.prefViewportWidthProperty());
        flowViewsPane.prefWidthProperty().bind(stackPane.widthProperty());
        flowViewsPane.prefHeightProperty().bind(stackPane.heightProperty());

        // 滚轮滚动放大缩小图片
        viewPane.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            double w;
            double h;
            if (deltaY > 0) {
                w = imageView.getFitWidth() * RATE;
                h = imageView.getFitHeight() * RATE;
            } else {
                w = imageView.getFitWidth() / RATE;
                h = imageView.getFitHeight() / RATE;
            }
            imageView.setFitWidth(w);
            imageView.setFitHeight(h);
        });

        initViewContainer();
        initArrowButton();
    }

    private void initViewContainer() {
        // 屏蔽滚动事件
        viewContainer.addEventFilter(ScrollEvent.ANY, event -> event.copyFor(viewPane, viewPane));

        // 子节点超出则会被隐藏
        Rectangle rect = new Rectangle();
        rect.widthProperty().bind(viewContainer.prefWidthProperty());
        rect.heightProperty().bind(viewContainer.prefHeightProperty());
        viewContainer.setClip(rect);

        // 绑定大小
        viewContainer.prefHeightProperty().bind(stackPane.heightProperty());
        viewContainer.prefWidthProperty().bind(stackPane.widthProperty());
        viewContainer.prefViewportWidthProperty().bind(stackPane.widthProperty());
        viewContainer.prefViewportHeightProperty().bind(stackPane.heightProperty());

        // 上|下张按钮出现|隐藏
        viewContainer.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            // image有url表示显示的是网络图片，不显示左右按钮
            if (imageView.getImage() != null && !isNetImage()) {
                double width = viewContainer.getPrefWidth();
                double height = viewContainer.getPrefHeight();
                double leftX = width / 5;
                double rightX = width - leftX;
                double cursorX = event.getX();
                double cursorY = event.getY();

                // 鼠标离开viewPane
                if (cursorX == 0 || cursorX == width - 1 || cursorY == height - 1) {
                    leftArrowButton.setVisible(false);
                    rightArrowButton.setVisible(false);
                    // 鼠标在左边按钮区
                } else if (cursorX < leftX) {
                    leftArrowButton.setVisible(true);
                    // 鼠标在右边按钮区
                } else if (cursorX > rightX) {
                    rightArrowButton.setVisible(true);
                    // 鼠标在viewPane中间区域
                } else {
                    leftArrowButton.setVisible(false);
                    rightArrowButton.setVisible(false);
                }
            }
        });

        // 拉伸窗口时图片一起适配大小
        viewContainer.prefHeightProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                Image img = imageView.getImage();
                if (img != null && img.getHeight() >= newValue.doubleValue()) {
                    this.setImage(imageView.getImage());
                }
            }
        }));
        viewContainer.prefWidthProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                Image img = imageView.getImage();
                if (img != null && img.getWidth() >= newValue.doubleValue()) {
                    this.setImage(imageView.getImage());
                }
            }
        }));

        // 菜单事件
        viewContainer.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                contextMenu.show(viewContainer, event.getScreenX(), event.getScreenY());
            } else if (event.getButton().equals(MouseButton.PRIMARY)) {
                contextMenu.hide();
            }
        });

        Point startPoint = new Point();
        imageView.setOnMousePressed(event -> startPoint.setLocation(event.getX(), event.getY()));
        imageView.setOnMouseDragged(event -> {
            double x = event.getX() - startPoint.getX() + imageView.getTranslateX();
            double y = event.getY() - startPoint.getY() + imageView.getTranslateY();
            imageView.setTranslateX(x);
            imageView.setTranslateY(y);
        });
    }

    private void initArrowButton() {
        EventHandler<ActionEvent> handler = new ArrowButtonHandler();
        leftArrowButton.addEventHandler(ActionEvent.ACTION, handler);
        rightArrowButton.addEventHandler(ActionEvent.ACTION, handler);
    }

    /**
     * 文件夹图片流式面板
     */
    final class FlowViewsPane extends ScrollPane {

        private static final int VIEW_HEIGHT = 100;
        private static final int VIEW_WIDTH = 100;
        private static final String DEFAULT_STYLE_CLASS = "flow-views-pane";

        private final FlowPane flowPane = new FlowPane();

        private final Map<ImageView, FileTreeItem> itemMap = new HashMap<>(16);

        FlowViewsPane() {
            super();
            flowPane.setHgap(5);
            flowPane.setVgap(5);
            flowPane.prefWidthProperty().bind(this.prefViewportWidthProperty().subtract(35));
            flowPane.getStyleClass().add("flow-pane");
            FlowPane.setMargin(flowPane, new Insets(10));

            this.setCache(true);
            this.prefViewportWidthProperty().bind(this.widthProperty());
            this.getStyleClass().add(DEFAULT_STYLE_CLASS);
            this.setContent(flowPane);
            this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
            this.setHbarPolicy(ScrollBarPolicy.NEVER);
        }

        /**
         * 显示文件夹图片，Show All菜单项事件
         * 因为方法会被放到Task线程池中执行，所有UI操作需要用Platform执行
         * @param item 文件夹item
         */
        void showImages(FileTreeItem item) {
            if (item.isDirFile()) {
                ObservableList<TreeItem<String>> children = item.getChildren();

                Platform.runLater(() -> updateLabelInfo(children.size()));

                // 过滤掉文件夹然后遍历
                children.filtered(p -> !((FileTreeItem) p).isDirFile())
                        .forEach(p -> {
                            FileTreeItem i = (FileTreeItem) p;
                            ImageView view = generateView(i.getFile());
                            itemMap.put(view, i);
                            Platform.runLater(() -> flowPane.getChildren().add(view));
                        });
            }
        }

        void updateLabelInfo() {
            updateLabelInfo(flowPane.getChildren().size());
        }

        void updateLabelInfo(int n) {
            infoLabel.setText(n + "张图片");
        }

        /**
         * 清空图片和TreeItem和ImageView的映射
         */
        void clear() {
            flowPane.getChildren().clear();
            itemMap.clear();
        }

        boolean isEmpty() {
            return flowPane.getChildren().isEmpty();
        }


        private ImageView generateView(File file) {
            Image image = ImageHelper.get(file);
            ImageView view = new ImageView();
            if (image == null || image.getWidth() > VIEW_WIDTH || image.getHeight() > VIEW_HEIGHT) {
                view.setFitWidth(VIEW_WIDTH);
                view.setFitHeight(VIEW_HEIGHT);
                view.setPreserveRatio(true);
            }
            view.setImage(image);
            view.setOnMouseClicked(event -> {
                EventTarget target = event.getTarget();
                if (target instanceof ImageView) {
                    backButton.setVisible(true);
                    showImage(itemMap.get(target));
                }
            });
            return view;
        }

    }

    final class ArrowButtonHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Object target = event.getTarget();
            FileTreeItem item;
            if (target.equals(leftArrowButton)) {
                item = (FileTreeItem) displayItem.previousSibling();
            } else if (target.equals(rightArrowButton)) {
                item = (FileTreeItem) displayItem.nextSibling();
            } else {
                return;
            }

            if (item != null) {
                showImage(item);
            }
        }
    }

    class ImageContextMenu extends BaseContextMenu {

        ImageContextMenu() {
            super();
            getItems().remove(2);
            this.setAutoHide(true);
        }

        @Override
        protected void rename() {

        }

        @Override
        protected File selectedFile() {
            return displayItem.getFile();
        }
    }

}
