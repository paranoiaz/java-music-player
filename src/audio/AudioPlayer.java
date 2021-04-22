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
    public int songCounter = 0;
    public double currentVolume = 0.5;

    public AudioPlayer() {
        this.songList = new ArrayList<Media>();
        // provide a valid path to the directory with audio files
        this.playlist = new Playlist("Music Playlist", "src/song");

        for (Song song: playlist.getSongList()) {
            this.songList.add(new Media(song.getSongFile().toURI().toString()));
        }

        this.mediaPlayer = new MediaPlayer(this.songList.get(songCounter));
    }

    public Duration getSongTimestamp() {
        return this.mediaPlayer.getCurrentTime();
    }

    public Duration getSongDuration() {
        return this.mediaPlayer.getTotalDuration();
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

    public boolean repeatSong() {
        this.repeatState = !this.repeatState;
        return this.repeatState;
    }

    public boolean shuffleSong() {
        this.shuffleState = !this.shuffleState;
        return this.shuffleState;
    }

    public boolean muteSong() {
        this.muteState = !this.muteState;
        return this.muteState;
    }
}
