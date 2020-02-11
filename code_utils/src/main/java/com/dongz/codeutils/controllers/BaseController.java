package com.dongz.codeutils.controllers;

import com.dongz.codeutils.entitys.db.DataBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author dong
 * @date 2020/2/12 00:52
 * @desc
 */
public abstract class BaseController {
    protected static final String STEP1 = "/ui/stepFirst.fxml";
    protected static final String STEP2 = "/ui/step2.fxml";

    protected static boolean isConnection = false;
    protected static DataBase db;

    protected void reload() {

    }

    protected void changeStep(Button btn,String step) throws IOException {
        Stage secondStage = (Stage) btn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(step));
        Parent load = fxmlLoader.load();
        BaseController controller = fxmlLoader.getController();
        controller.reload();
        Scene scene = new Scene(load);
        secondStage.setScene(scene);
    }

    protected void alert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
