package Models;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


import java.io.IOException;


public class Album extends VBox {
    @FXML
    private ImageView imageView;
    @FXML
    private Text albumName,artistName;
    public Album(String albumName,String artistName){
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/fxml/album.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            this.albumName.setText(albumName);
            this.artistName.setText(artistName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
