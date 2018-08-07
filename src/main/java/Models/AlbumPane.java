package Models;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import java.io.IOException;

public class AlbumPane extends AnchorPane{
    @FXML
    public FlowPane jfxMasonryPane;
    public AlbumPane(){
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/fxml/albumPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
