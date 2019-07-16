package cn.keshb.photoview;

import cn.keshb.photoview.util.ImageHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author keshb
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ui/main/view.fxml"));
        primaryStage.setTitle("PhotoView");
        primaryStage.getIcons().add(ImageHelper.get("/ui/main/image/icon_window.png"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        root.setOnMouseClicked(event -> System.out.println(event.getTarget()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
