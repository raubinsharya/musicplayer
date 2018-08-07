package sample;

import Models.AnimationGenerator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class Main extends Application {
    public static Stage mainStage;
    public static Controller controller=new Controller();
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("DY Music player");
        primaryStage.getIcons().add(new Image("/images/spotify.png"));
        primaryStage.setScene(new Scene(controller));
        primaryStage.setMinHeight(1000);
        primaryStage.setMinWidth(1600);
        mainStage=primaryStage;
       /*
        Parent parent= FXMLLoader.load(getClass().getResource("/fxml/loading.fxml"));
        primaryStage.setScene(new Scene(parent));
        FadeTransition ft = new FadeTransition(Duration.millis(3000), parent);
        ft.setFromValue(0.3);
        ft.setToValue(1.0);
        ft.setCycleCount(1);
        ft.setOnFinished(event -> primaryStage.setScene(new Scene(controller)));
        ft.play();
        primaryStage.setMaximized(true);*/
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            try{
            Properties properties=new Properties();
            properties.setProperty("volume"       ,String.valueOf(controller.getVolume()));
            properties.setProperty("lastPlayed"   ,controller.getCurrentSong());
            properties.setProperty("currentTime"  ,controller.songLastPlayedStatus());
            properties.setProperty("totalTime"    ,controller.songTotalPlayedStatus());

            FileOutputStream fileOutputStream=new FileOutputStream(new File("media.config"));
            properties.store(fileOutputStream,"DY Configuration");
            fileOutputStream.close();
            } catch (Exception e) {
                Platform.exit();
                System.exit(0);
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
