package Models;

import java.io.Serializable;

public class MetaData implements Serializable{
    private String artist;
    private String album;
    private String title;
    private String duration;
    private String genre;
    private String fileLocation;

    public MetaData(String title, String artistName, String album, String genre, String duration,String fileLocation) {
        this.artist = artistName;
        this.album = album;
        this.title = title;
        this.duration = duration;
        this.genre = genre;
        this.fileLocation=fileLocation;
    }

    public String getDuration() {
        return duration;
    }
    public String getGenre(){
        return genre;
    }
    public String getTitle() { return title; }
    public String getArtist() {
        return artist;
    }
    public String getAlbum() {
        return album;
    }
    public String getFileLocation() {
        return fileLocation;
    }
}
