package cn.keshb.photoview.component;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SearchField extends HBox implements Initializable {

    @FXML
    private TextField textField;
    @FXML
    private Button button;

    public SearchField() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/component/SearchField/search-field.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnTextAction(EventHandler<ActionEvent> value) {
        textField.setOnAction(value);
    }

    public <T extends Event> void addTextEventHandler(final EventType<T> eventType,
                                                      final EventHandler<? super T> eventHandler) {
        textField.addEventHandler(eventType, eventHandler);
    }

    public TextField getTextField() {
        return textField;
    }

    public Button getButton() {
        return button;
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String value) {
        textField.setText(value);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textField.setContextMenu(new ContextMenu());
        textField.prefWidthProperty().bind(this.widthProperty().subtract(button.prefWidthProperty()));
        textField.focusedProperty().addListener(focus -> {
            ReadOnlyBooleanProperty property = (ReadOnlyBooleanProperty) focus;
            if (property.getValue().equals(true)) {
                SearchField.this.setStyle("-fx-border-color: rgba(136, 138, 140, 0.75)");
            } else {
                SearchField.this.setStyle("-fx-border-color: rgba(136, 138, 140, 0.3)");
            }
        });
    }
}
