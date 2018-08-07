package sample;

import Models.*;
import com.jfoenix.controls.JFXSlider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.textfield.CustomTextField;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

public class Controller extends BorderPane implements Initializable{
    @FXML
    private CustomTextField searchBox;

    @FXML
    private TableView<MetaData> tableView;

    @FXML
    private TableColumn<MetaData, Button> playButton;
    @FXML
    private TableColumn<MetaData, String> title;

    @FXML
    private TableColumn<MetaData, String> artist;

    @FXML
    private TableColumn<MetaData, String> album;

    @FXML
    private TableColumn<MetaData, String> genre;

    @FXML
    private TableColumn<MetaData, Button> menuButton;

    @FXML
    private TableColumn<MetaData, String> duration;
    @FXML
    private TableColumn<MetaData, Integer> id;

    @FXML
    private JFXSlider progressBar,volumeSlider;
    @FXML
    private ImageView play_pause_button,albumCover,repeatImage,shuffleImage;
    @FXML
    private Text currentTime,totalTime,songTitle,artistName;

    @FXML
    private BorderPane borderPane;

    private ObservableList<MetaData> observableList= FXCollections.observableArrayList();
    private List<MetaData> songsData=new ArrayList<>();
    private Media media;
    private MediaPlayer mediaPlayer;
    private boolean isChanging=true,repeat,shuffle;
    private Random random=new Random();
    private int tableViewIndex;
    private PlaylistController playlistController;

    public Controller(){
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/sample.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        searchBox.setLeft(new ImageView("images/search.png"));
        readSongFromFile();
        loadConfig();
        initializeSearchOption();

      /*  final Provider provider = Provider.getCurrentProvider(true);
        Runtime.getRuntime().addShutdownHook(new Thread("shutdown-hook") {
            @Override
            public void run() {
                provider.reset();
                provider.stop();
            }
        });
        provider.register(MediaKey.MEDIA_PLAY_PAUSE, hotKey -> playPause());
        provider.register(MediaKey.MEDIA_NEXT_TRACK, hotKey -> playNextSong());
        provider.register(MediaKey.MEDIA_PREV_TRACK, hotKey -> playPreviousSong());*/
    }

    private void initializeSearchOption() {
        tableView.setPlaceholder(new Label("Add songs to display here"));
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount()==2)
                playNewSong(tableViewIndex=tableView.getSelectionModel().getSelectedIndex());
        });
        FilteredList<MetaData> filteredList=new FilteredList<>(observableList,e ->true);
        searchBox.setOnKeyReleased(event -> {
            searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredList.setPredicate((Predicate<? super MetaData>)metaData->{
                    if (newValue==null || newValue.isEmpty())
                        return true;
                    if (metaData.getTitle().toLowerCase().contains(newValue.toLowerCase()))
                        return true;
                    else  if (metaData.getAlbum().toLowerCase().contains(newValue.toLowerCase()))
                        return true;
                    else  if (metaData.getArtist().toLowerCase().contains(newValue.toLowerCase()))
                        return true;
                    return false;
                });
            });
            SortedList<MetaData> sortedList=new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sortedList);
        });

    }

    public void chooseFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(Main.mainStage);
        if (file != null)
            listSong(file.listFiles());
        else System.out.println("No folder selected");

    }
    private void loadConfig() {
        try {
            File file=new File("media.config");
            if (!file.exists()){
                file.createNewFile();
                return;
            }
            FileReader fileReader=new FileReader(file);
            Properties properties=new Properties();
            properties.load(fileReader);
            playNewSong(tableViewIndex=Integer.parseInt(properties.getProperty("lastPlayed")));
            playPause();
            progressBar.setValue((Double.parseDouble(properties.getProperty("currentTime"))/Double.parseDouble(properties.getProperty("totalTime"))*100));
            totalTime.setText(formatTime((int) Double.parseDouble(properties.getProperty("totalTime"))));
            currentTime.setText(formatTime((int) Double.parseDouble(properties.getProperty("currentTime"))));
            volumeSlider.setValue(Double.parseDouble(properties.getProperty("volume"))*100);
            mediaPlayer.setOnReady(()-> mediaPlayer.seek(Duration.seconds(Double.parseDouble(properties.getProperty("currentTime")))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showPlaylistDialog(){
        if (playlistController==null)
            playlistController=new PlaylistController();
        Dialog dialog=new Dialog();
        dialog.getDialogPane().setContent(playlistController);
        dialog.initOwner(Main.mainStage);
        dialog.initStyle(StageStyle.UNDECORATED);
       // AnimationGenerator.applyTranslateAnimationOn(playlistController,1000,-120,0);
        AnimationGenerator.applyScaleAnimation(playlistController,500,0,1,null);
        AnimationGenerator.applyFadeAnimationOn(playlistController,700,0f,1.0f,null);
        if (!dialog.isShowing()){
            dialog.show();
            Main.controller.setEffect(HelperClass.boxBlur);
        }
    }

    private void playNewSong(int selectedIndex) {
         if (shuffle && !repeat)
             tableViewIndex=random.nextInt(observableList.size());
         if (mediaPlayer!=null){
             mediaPlayer.stop();
             mediaPlayer.dispose();
             mediaPlayer=null;
         }
         tableView.getSelectionModel().select(selectedIndex);
         media=new Media(new File(observableList.get(selectedIndex).getFileLocation()).toURI().toASCIIString());
         mediaPlayer=new MediaPlayer(media);
         mediaPlayer.play();
         mediaPlayer.setOnEndOfMedia(() -> {
                 if (repeat)
                     playNewSong(tableViewIndex);
                 else playNewSong(++tableViewIndex);
            });
         play_pause_button.setImage(new Image("images/pause-button-circle.png"));
         artistName.setText(observableList.get(selectedIndex).getArtist());
         artistName.maxWidth(10);
         songTitle.maxWidth(10);
         songTitle.setText(observableList.get(selectedIndex).getTitle());
         mediaPlayer.setOnReady(() -> {
                Main.mainStage.setTitle(observableList.get(selectedIndex).getTitle());
                if (media.getMetadata().get("image") == null){
                    albumCover.setImage(new Image("images/spotify.png"));
                }else albumCover.setImage((Image) media.getMetadata().get("image"));
                });
            publishProgressBar();
            try{mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty().divide(100));
            }catch (Exception ex){System.out.println(ex.getMessage());}
            progressBar.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
                isChanging=false;
                        if (oldValue){
                            mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(progressBar.getValue() / 100.0));
                            isChanging=true;
                        }
            });

    }

    private void readSongFromFile() {
        try {
            ObjectInputStream objectInputStream=new ObjectInputStream(new FileInputStream("mp3data.txt"));
            songsData= (List<MetaData>) objectInputStream.readObject();
            copySongsDataToObservableList();
            loadSongsToTable();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadSongsToTable() {
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        artist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        album.setCellValueFactory(new PropertyValueFactory<>("album"));
        genre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        duration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        tableView.setItems(observableList);
    }

    private void copySongsDataToObservableList() {
        for (int i=0;i<songsData.size();i++){
            observableList.add(songsData.get(i));
            }
    }

    private void listSong(File[] listFile) {
        Thread thread=new Thread(()->{
            for (File songFile : listFile) {
                if (songFile.isDirectory()) {
                    listSong(songFile.listFiles());
                }
                if (songFile.getName().endsWith(".mp3"))
                    addSong(songFile);
                saveSongData();
            } });thread.start();

    }

    private void saveSongData() {
        try {
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(new FileOutputStream("mp3data.txt"));
            objectOutputStream.writeObject(songsData);
            objectOutputStream.close();
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addSong(File songFile) {
        String title,album,artist,genre,length;
        try {
            AudioFile audioFile= AudioFileIO.read(songFile);
            Tag tag=audioFile.getTag();
            if (tag.getFirst(FieldKey.TITLE)==null || tag.getFirst(FieldKey.TITLE).trim().isEmpty())
                title=songFile.getName().replace(".mp3","").trim();
            else title=tag.getFirst(FieldKey.TITLE).trim();
            if (tag.getFirst(FieldKey.ALBUM_ARTIST)==null || tag.getFirst(FieldKey.ALBUM_ARTIST).trim().isEmpty())
                artist="UNKNOWN";
            else artist=tag.getFirst(FieldKey.ALBUM_ARTIST).trim();
            if (tag.getFirst(FieldKey.GENRE)==null || tag.getFirst(FieldKey.ALBUM_ARTIST).trim().isEmpty())
                genre="UNKNOWN";
            else genre=tag.getFirst(FieldKey.GENRE);
            if (tag.getFirst(FieldKey.ALBUM)==null || tag.getFirst(FieldKey.ALBUM).trim().isEmpty())
                album="UNKNOWN";
            else album=tag.getFirst(FieldKey.ALBUM);
            length=formatTime(audioFile.getAudioHeader().getTrackLength());
            MetaData metaData=new MetaData(title,artist,album,genre,length,songFile.getAbsolutePath().toString());
            songsData.add(metaData);
            observableList.add(metaData);
            } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void loadAlbum(){
        AlbumPane albumPane=new AlbumPane();
        borderPane.setCenter(albumPane);
        for (int i=0;i<100;i++){
            //albumPane.jfxMasonryPane.getChildren().add(new Album("Sweety ki tity ki sonu",i+"dhhdhd"));
        }
    }
    public void playPause(){
        if (mediaPlayer!=null && mediaPlayer.getStatus()==MediaPlayer.Status.PAUSED || mediaPlayer.getStatus()==MediaPlayer.Status.READY ){
             mediaPlayer.play();
             play_pause_button.setImage(new Image("images/pause-button-circle.png"));
         }else if (mediaPlayer!=null && mediaPlayer.getStatus()==MediaPlayer.Status.PLAYING || mediaPlayer.getStatus()==MediaPlayer.Status.UNKNOWN){
             mediaPlayer.pause();
             play_pause_button.setImage(new Image("images/play.png"));
         }
    }
    public void playNextSong(){
        playNewSong(++tableViewIndex);
    }
    public void playPreviousSong(){
        playNewSong(--tableViewIndex);
    }
    public void repeatSong(){
        if (repeat){
            repeat=false;
            repeatImage.setImage(new Image("images/repeat.png"));
        }
        else {repeat=true;
             repeatImage.setImage(new Image("images/repeat_green.png"));
        }
    }
    public void shuffleSong(){
        if (shuffle){
            shuffle=false;
            shuffleImage.setImage(new Image("images/shuffle-arrows.png")); }else {
            shuffle=true;
            shuffleImage.setImage(new Image("images/shuffle_green.png"));
        }

    }
    private void publishProgressBar(){
        mediaPlayer.currentTimeProperty().addListener(observable -> {
            if (isChanging)
                progressBar.setValue((mediaPlayer.getCurrentTime().toMillis()/mediaPlayer.getTotalDuration().toMillis())*100);
            totalTime.setText((formatTime((int)mediaPlayer.getTotalDuration().toSeconds())));
            currentTime.setText(formatTime((int)mediaPlayer.getCurrentTime().toSeconds()));
            });
    }
    private static String formatTime(int elapsed) {
        int intElapsed = elapsed;
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;
        if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    public String getVolume(){
          return String.valueOf(volumeSlider.getValue()/100.00);
        }
    public String getCurrentSong(){
       return String.valueOf(tableViewIndex);
    }
    public String songLastPlayedStatus(){
        return String.valueOf(mediaPlayer.getCurrentTime().toSeconds());
        }
    public String songTotalPlayedStatus(){
        return String.valueOf(mediaPlayer.getTotalDuration().toSeconds());
    }
}




