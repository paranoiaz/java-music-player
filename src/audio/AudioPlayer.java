package audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.util.ArrayList;


public class AudioPlayer {
    public MediaPlayer mediaPlayer;
    public Playlist playlist;
    public ArrayList<Media> songList;
    public boolean clickedState = false;
    public boolean repeatState = false;
    public boolean shuffleState = false;
    public boolean muteState = false;
    public double currentVolume = 0.5;
    public int songCounter = 0;

    public AudioPlayer() {
        this.songList = new ArrayList<Media>();
        // provide a valid path to the directory with audio files
        this.playlist = new Playlist("", "");

        for (Song song: playlist.getSongList()) {
            this.songList.add(new Media(song.getSongFile().toURI().toString()));
        }

        this.mediaPlayer = new MediaPlayer(this.songList.get(songCounter));
    }

    public boolean playSong() {
        if (!this.clickedState) {
            this.clickedState = true;
            this.mediaPlayer.play();
        }
        else {
            this.clickedState = false;
            this.mediaPlayer.pause();
        }
        return this.clickedState;
    }

    public void repeatSong() {
        this.repeatState = !this.repeatState;
    }

    public void shuffleSong() {
        this.shuffleState = !this.shuffleState;
    }

    public void muteSong() {
        this.muteState = !this.muteState;
    }

    public Duration getSongTimestamp() {
        return this.mediaPlayer.getCurrentTime();
    }

    public Duration getSongDuration() {
        return this.mediaPlayer.getTotalDuration();
    }
}
