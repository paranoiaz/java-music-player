import audio.AudioPlayer;
import audio.Song;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Random;


public class GUI extends Application {
    private AudioPlayer audioPlayer;
    private Stage window;
    private Scene mainScene;
    private BorderPane rootPane;
    private VBox centerPane;
    private HBox bottomPane;
    private Pane leftPane;
    private Slider durationSlider;
    private Label currentTimeText;
    private Label totalTimeText;
    private Label songNameText;
    private Label playlistNameText;
    private Button playButton;
    private Button audioButton;
    private Slider volumeSlider;
    private ListView<String> songListView;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.window = primaryStage;
        this.rootPane = new BorderPane();
        this.mainScene = new Scene(this.rootPane, 850, 600);
        this.bottomPane = new HBox();
        this.centerPane = new VBox();
        this.leftPane = new Pane();

        this.window.setTitle("Music Player developed by github.com/paranoiaz");
        this.window.setScene(this.mainScene);

        this.rootPane.setId("root_pane");
        this.mainScene.getStylesheets().add(String.valueOf(this.getClass().getResource("/css/styles.css")));

        this.centerPane.setId("center_pane");
        this.rootPane.setCenter(this.centerPane);

        this.bottomPane.setId("bottom_pane");
        this.bottomPane.setMinHeight(75);
        this.bottomPane.setMaxHeight(75);
        this.bottomPane.setSpacing(10);
        this.rootPane.setBottom(this.bottomPane);

        this.leftPane.setId("left_pane");
        this.leftPane.setMinHeight(150);
        this.leftPane.setMaxHeight(150);
        this.leftPane.setMinWidth(150);
        this.leftPane.setMaxWidth(150);
        this.rootPane.setLeft(this.leftPane);

        this.audioPlayer = new AudioPlayer();
        this.renderPlaySkipButtons();
        this.setupSlider();
        this.renderRepeatShuffleButtons();
        this.setupAudioButton();
        this.setupVolumeSlider();
        this.setupSongListView();
        this.setupCurrentlyPlaying();

        this.window.setResizable(false);
        this.window.show();
    }

    private void renderRepeatShuffleButtons() {
        Button repeatButton = new Button();
        Button shuffleButton = new Button();

        repeatButton.setId("repeat_off");
        shuffleButton.setId("shuffle_off");

        repeatButton.getStylesheets().add(String.valueOf(this.getClass().getResource("/css/styles.css")));
        repeatButton.setMinSize(40, 40);
        repeatButton.setMaxSize(40, 40);
        repeatButton.setTranslateY(17.5);
        repeatButton.setTranslateX(25);

        shuffleButton.getStylesheets().add(String.valueOf(this.getClass().getResource("/css/styles.css")));
        shuffleButton.setMinSize(40, 40);
        shuffleButton.setMaxSize(40, 40);
        shuffleButton.setTranslateY(17.5);
        shuffleButton.setTranslateX(25);

        repeatButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                audioPlayer.repeatSong();
                if (audioPlayer.repeatState) {
                    repeatButton.setId("repeat_on");
                }
                else {
                    repeatButton.setId("repeat_off");
                }
            }
        });

        shuffleButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                audioPlayer.shuffleSong();
                if (audioPlayer.shuffleState) {
                    shuffleButton.setId("shuffle_on");
                }
                else {
                    shuffleButton.setId("shuffle_off");
                }
            }
        });

        this.bottomPane.getChildren().add(6, repeatButton);
        this.bottomPane.getChildren().add(7, shuffleButton);
    }

    private void renderPlaySkipButtons() {
        // arraylist used to avoid repetition
        ArrayList<Button> buttonList = new ArrayList<Button>();
        this.playButton = new Button();
        Button skipBackwardButton = new Button();
        Button skipForwardButton = new Button();

        buttonList.add(skipBackwardButton);
        // order matters in the foreach loop
        buttonList.add(this.playButton);
        buttonList.add(skipForwardButton);

        this.playButton.setId("play");
        skipBackwardButton.setId("skip_backward");
        skipForwardButton.setId("skip_forward");

        int indexCounter = 0;
        for (Button button: buttonList) {
            button.getStylesheets().add(String.valueOf(this.getClass().getResource("/css/styles.css")));
            button.setMinSize(40, 40);
            button.setMaxSize(40, 40);
            button.setTranslateY(17.5);
            button.setTranslateX(25);
            this.bottomPane.getChildren().add(indexCounter, button);
            indexCounter++;
        }

        this.playButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (audioPlayer.playSong()) {
                    playButton.setId("pause");
                }
                else {
                    playButton.setId("play");
                }
            }
        });

        this.addSkipButtonListener(skipBackwardButton);
        this.addSkipButtonListener(skipForwardButton);
    }

    private void addSkipButtonListener(Button skipButton) {
        skipButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (skipButton.getId().equalsIgnoreCase("skip_backward")) {
                    if (audioPlayer.getSongTimestamp().toSeconds() > 3) {
                        audioPlayer.mediaPlayer.seek(Duration.seconds(0));
                    }
                    else if (audioPlayer.songCounter > 0 && audioPlayer.songCounter < audioPlayer.songList.size()) {
                        changeCurrentSong(false);
                    }
                }

                if (skipButton.getId().equalsIgnoreCase("skip_forward")) {
                    if (audioPlayer.songCounter >= 0 && audioPlayer.songCounter < audioPlayer.songList.size() - 1) {
                        changeCurrentSong(true);
                    }
                }
            }
        });
    }

    private void refreshSlider() {
        this.bottomPane.getChildren().remove(durationSlider);
        this.bottomPane.getChildren().remove(this.currentTimeText);
        this.bottomPane.getChildren().remove(this.totalTimeText);
        this.setupSlider();
    }

    private void setupSlider() {
        this.durationSlider = new Slider();
        this.durationSlider.setId("duration_slider");

        this.durationSlider.setTranslateY(30);
        this.durationSlider.setTranslateX(25);
        this.durationSlider.setMinWidth(300);
        this.durationSlider.setMaxWidth(300);
        this.durationSlider.setMin(0);
        this.durationSlider.setMax(100);
        this.durationSlider.setValue(0);

        this.currentTimeText = new Label("0:00");
        this.totalTimeText = new Label("0:00");
        this.currentTimeText.setId("time_text");
        this.totalTimeText.setId("time_text");
        this.currentTimeText.setTranslateX(30);
        this.totalTimeText.setTranslateX(20);
        this.currentTimeText.setTranslateY(25);
        this.totalTimeText.setTranslateY(25);


        audioPlayer.mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration t1) {
                double currentTime = audioPlayer.getSongTimestamp().toSeconds();
                double totalTime = audioPlayer.getSongDuration().toSeconds();
                durationSlider.setValue((currentTime / totalTime) * 100);

                double currentMinutes = audioPlayer.getSongTimestamp().toSeconds() / 60;
                double currentSeconds = audioPlayer.getSongTimestamp().toSeconds() % 60;
                currentTimeText.setText(String.format("%d:%02d", (int) currentMinutes, (int) currentSeconds));

                double totalMinutes = audioPlayer.getSongDuration().toSeconds() / 60;
                double totalSeconds = audioPlayer.getSongDuration().toSeconds() % 60;
                totalTimeText.setText(String.format("%d:%02d", (int) totalMinutes, (int) totalSeconds));
                // idk if 0.999 value is correct
                if (currentTime / totalTime >= 0.999) {
                    if (audioPlayer.repeatState) {
                        audioPlayer.mediaPlayer.seek(Duration.seconds(0));
                    }
                    else if (audioPlayer.songCounter >= 0 && audioPlayer.songCounter < audioPlayer.songList.size() - 1) {
                        if (audioPlayer.shuffleState) {
                            Random randomGenerator = new Random();
                            int newSongCounter = audioPlayer.songCounter;
                            while (newSongCounter == audioPlayer.songCounter) {
                                newSongCounter = randomGenerator.nextInt(audioPlayer.songList.size());
                            }
                            audioPlayer.songCounter = newSongCounter;
                        }
                        else {
                            audioPlayer.songCounter++;
                        }
                        audioPlayer.mediaPlayer.stop();
                        audioPlayer.mediaPlayer = new MediaPlayer(audioPlayer.songList.get(audioPlayer.songCounter));
                        songListView.getSelectionModel().select(audioPlayer.songCounter);
                        songNameText.setText(audioPlayer.playlist.getSongList().get(audioPlayer.songCounter).getSongName());
                        if (audioPlayer.muteState) {
                            audioPlayer.mediaPlayer.setVolume(0);
                            volumeSlider.setValue(0);
                        }
                        else {
                            audioPlayer.mediaPlayer.setVolume(audioPlayer.currentVolume);
                            volumeSlider.setValue(audioPlayer.currentVolume);
                        }
                        refreshSlider();
                        if (!audioPlayer.clickedState) {
                            audioPlayer.playSong();
                            playButton.setId("pause");
                        }
                        else {
                            audioPlayer.mediaPlayer.play();
                        }
                    }
                }
            }
        });

        this.durationSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newSliderValue) {
                double newTime = newSliderValue.doubleValue() / 100 * audioPlayer.getSongDuration().toSeconds();
                if (newTime > audioPlayer.getSongTimestamp().toSeconds() + 1 || newTime < audioPlayer.getSongTimestamp().toSeconds() - 1) {
                    audioPlayer.mediaPlayer.pause();
                    audioPlayer.mediaPlayer.seek(Duration.seconds(newTime));
                }
            }
        });

        this.durationSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!durationSlider.isPressed() && audioPlayer.clickedState) {
                    audioPlayer.mediaPlayer.play();
                }
            }
        });

        this.bottomPane.getChildren().add(3, this.currentTimeText);
        this.bottomPane.getChildren().add(4, this.durationSlider);
        this.bottomPane.getChildren().add(5, this.totalTimeText);
    }

    private void setupAudioButton() {
        this.audioButton = new Button();
        this.audioButton.setId("audio_on");

        this.audioButton.getStylesheets().add(String.valueOf(this.getClass().getResource("/css/styles.css")));
        this.audioButton.setMinSize(30, 30);
        this.audioButton.setMaxSize(30, 30);
        this.audioButton.setTranslateY(22.5);
        this.audioButton.setTranslateX(30);

        this.audioButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                audioPlayer.muteSong();
                toggleAudioButton(audioPlayer.muteState);
                if (audioPlayer.muteState) {
                    audioPlayer.mediaPlayer.setVolume(0);
                    volumeSlider.setValue(0);
                }
                else {
                    audioPlayer.mediaPlayer.setVolume(audioPlayer.currentVolume);
                    volumeSlider.setValue(audioPlayer.currentVolume);
                }
            }
        });

        this.bottomPane.getChildren().add(8,  this.audioButton);
    }

    private void toggleAudioButton(boolean state) {
        if (!state) {
            audioButton.setId("audio_on");
        }
        else {
            audioButton.setId("audio_off");
        }
    }

    private void setupVolumeSlider() {
        this.volumeSlider = new Slider();
        this.volumeSlider.setId("volume_slider");

        this.volumeSlider.setTranslateY(30);
        this.volumeSlider.setTranslateX(20);
        this.volumeSlider.setMinWidth(100);
        this.volumeSlider.setMaxWidth(100);
        this.volumeSlider.setMin(0);
        this.volumeSlider.setMax(1);
        this.volumeSlider.setValue(0.5);

        this.volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newVolume) {
                audioPlayer.mediaPlayer.setVolume(newVolume.doubleValue());
                if (volumeSlider.getValue() == 0) {
                    audioPlayer.muteState = true;
                    toggleAudioButton(audioPlayer.muteState);
                    audioPlayer.mediaPlayer.setVolume(0);
                }
                else {
                    audioPlayer.muteState = false;
                    toggleAudioButton(audioPlayer.muteState);
                    audioPlayer.currentVolume = newVolume.doubleValue();
                    audioPlayer.mediaPlayer.setVolume(audioPlayer.currentVolume);
                }
            }
        });

        this.bottomPane.getChildren().add(9, this.volumeSlider);
    }

    private void setupSongListView() {
        this.songListView = new ListView<String>();

        for (Song song: this.audioPlayer.playlist.getSongList()) {
            this.songListView.getItems().add(song.getSongName());
        }

        this.songListView.setMinWidth(500);
        this.songListView.setMaxWidth(500);
        this.songListView.setMinHeight(525);
        this.songListView.setMaxHeight(525);
        this.songListView.setTranslateX(20);
        this.songListView.setTranslateY(20);

        this.songListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() >= 2) {
                    int songIndex = songListView.getSelectionModel().getSelectedIndex();
                    if (songIndex < 0 || songIndex >= audioPlayer.playlist.getSongList().size()) {
                        return;
                    }

                    audioPlayer.songCounter = songIndex;
                    audioPlayer.mediaPlayer.stop();
                    audioPlayer.mediaPlayer = new MediaPlayer(audioPlayer.songList.get(audioPlayer.songCounter));
                    songListView.getSelectionModel().select(audioPlayer.songCounter);
                    songNameText.setText(audioPlayer.playlist.getSongList().get(audioPlayer.songCounter).getSongName());
                    if (audioPlayer.muteState) {
                        audioPlayer.mediaPlayer.setVolume(0);
                        volumeSlider.setValue(0);
                    }
                    else {
                        audioPlayer.mediaPlayer.setVolume(audioPlayer.currentVolume);
                        volumeSlider.setValue(audioPlayer.currentVolume);
                    }
                    refreshSlider();
                    if (!audioPlayer.clickedState) {
                        audioPlayer.playSong();
                        playButton.setId("pause");
                    }
                    else {
                        audioPlayer.mediaPlayer.play();
                    }
                }
            }
        });

        songListView.getSelectionModel().select(audioPlayer.songCounter);
        this.centerPane.getChildren().add(0, this.songListView);
    }

    private void setupCurrentlyPlaying() {
        this.playlistNameText = new Label(audioPlayer.playlist.getPlayListName());
        this.playlistNameText.setId("playlist_name");
        this.playlistNameText.setMinSize(135, 30);
        this.playlistNameText.setMaxSize(135, 30);
        this.playlistNameText.setTranslateX(10);
        this.playlistNameText.setTranslateY(10);

        this.songNameText = new Label(audioPlayer.playlist.getSongList().get(audioPlayer.songCounter).getSongName());
        this.songNameText.setId("song_name");
        this.songNameText.setMinSize(135, 30);
        this.songNameText.setMaxSize(135, 30);
        this.songNameText.setTranslateX(10);
        this.songNameText.setTranslateY(110);

        Label nowPlaying = new Label("Now playing:");
        nowPlaying.setId("now_playing");
        nowPlaying.setMinSize(135, 30);
        nowPlaying.setMaxSize(135, 30);
        nowPlaying.setTranslateX(10);
        nowPlaying.setTranslateY(90);

        Line lineLeft = new Line();
        lineLeft.setId("line_left");
        lineLeft.setStartX(150);
        lineLeft.setStartY(0);
        lineLeft.setEndX(150);
        lineLeft.setEndY(525);

        Line lineRight = new Line();
        lineRight.setId("line_right");
        lineRight.setStartX(690);
        lineRight.setStartY(0);
        lineRight.setEndX(690);
        lineRight.setEndY(525);

        this.rootPane.getChildren().add(lineLeft);
        this.rootPane.getChildren().add(lineRight);

        this.leftPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (leftPane.getOpacity() > 0.5) {
                    leftPane.setId("left_pane_clicked");
                }
                else {
                    leftPane.setId("left_pane");
                }
            }
        });

        this.leftPane.setTranslateY(375);
        this.leftPane.getChildren().add(0, this.playlistNameText);
        this.leftPane.getChildren().add(1, nowPlaying);
        this.leftPane.getChildren().add(2, this.songNameText);
    }

    // true is an increment, false is a decrement
    private void changeCurrentSong(boolean positiveOrNegative) {
        if (this.audioPlayer.shuffleState) {
            Random randomIntegerGenerator = new Random();
            int newSongCounter = this.audioPlayer.songCounter;

            // generate random song counter that is not equal to current
            while (newSongCounter == this.audioPlayer.songCounter) {
                newSongCounter = randomIntegerGenerator.nextInt(this.audioPlayer.songList.size());
            }

            this.audioPlayer.songCounter = newSongCounter;
        }
        else {
            if (positiveOrNegative) {
                this.audioPlayer.songCounter++;
            }
            else {
                this.audioPlayer.songCounter--;
            }
        }

        // change current song by initializing a new object
        this.audioPlayer.mediaPlayer.stop();
        this.audioPlayer.mediaPlayer = new MediaPlayer(this.audioPlayer.songList.get(this.audioPlayer.songCounter));
        this.songListView.getSelectionModel().select(this.audioPlayer.songCounter);
        this.songNameText.setText(this.audioPlayer.playlist.getSongList().get(this.audioPlayer.songCounter).getSongName());

        if (this.audioPlayer.muteState) {
            this.audioPlayer.mediaPlayer.setVolume(0);
            this.volumeSlider.setValue(0);
        }
        else {
            this.audioPlayer.mediaPlayer.setVolume(this.audioPlayer.currentVolume);
            this.volumeSlider.setValue(this.audioPlayer.currentVolume);
        }

        if (!this.audioPlayer.clickedState) {
            this.audioPlayer.playSong();
            this.playButton.setId("pause");
        }
        else {
            this.audioPlayer.mediaPlayer.play();
        }

        this.refreshSlider();
    }
}
