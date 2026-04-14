package com.onlinebanking;

import com.onlinebanking.config.ApplicationContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load(), 600, 400);
        
        String css = getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        stage.setTitle("Online Banking System");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        ApplicationContext.getInstance().shutdown();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
