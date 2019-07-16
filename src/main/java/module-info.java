module cn.keshb.photoview {

    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;
    requires javafx.swing;
//    requires org.apache.commons.lang3;

    opens cn.keshb.photoview to javafx.graphics,javafx.fxml;
    opens cn.keshb.photoview.component to javafx.fxml;
    opens cn.keshb.photoview.component.treebox to javafx.fxml;
}