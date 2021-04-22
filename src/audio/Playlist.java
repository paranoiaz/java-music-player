package audio;

import java.io.File;
import java.util.ArrayList;


public class Playlist {
    private String playListName;
    private ArrayList<Song> songList;

    public Playlist(String playListName, String directory) {
        this.playListName = playListName;
        this.songList = new ArrayList<Song>();
        File songDirectory = new File(directory);
        File[] songFiles = songDirectory.listFiles();

        if (songFiles != null) {
            for (File songFile: songFiles) {
                this.songList.add(new Song(songFile));
            }
        }
    }

    public String getPlayListName() {
        return this.playListName;
    }

    public ArrayList<Song> getSongList() {
        return this.songList;
    }
}
