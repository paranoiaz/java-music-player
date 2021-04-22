package audio;

import java.io.File;


public class Song {
    private File songFile;
    private String songName;

    public Song(File songFile) {
        this.songFile = songFile;
        this.songName = songFile.getName();
    }

    public File getSongFile() {
        return this.songFile;
    }

    public String getSongName() {
        return this.songName.split("\\.")[0];
    }
}
