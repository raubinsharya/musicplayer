package Models;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Main;

import java.io.File;
import java.io.IOException;

public class PlaylistController extends AnchorPane {
    @FXML
    private Button exitButton;
    @FXML
    private ImageView imageView;
    private Stage stage;
    public PlaylistController(){
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/fxml/createPlaylist.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void exit(){
        stage= (Stage) exitButton.getScene().getWindow();
        AnimationGenerator.applyScaleAnimation(this,300,1,0,null);
        AnimationGenerator.applyFadeAnimationOn(this, 400, 1.0f, 0f, event -> {
            Main.controller.setEffect(null);
            stage.close();
        });


    }
    public void chooseImage(){
        FileChooser fileChooser=new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Image file", "*.png","*.jpg");
        fileChooser.getExtensionFilters().add(extFilter);
        File file=fileChooser.showOpenDialog(Main.mainStage);
        if (file!=null)
            imageView.setImage(new Image(file.toURI().toString()));
    }


}
