package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller implements Initializable{
	@FXML
	private Pane panel;
	
	@FXML
	
	private Label songTitle,volume;
	
	@FXML
	private Button playButton,pauseButton,resetButton,previousButton, nextButton;
	
	@FXML
	private ComboBox<String> playlists;
	
	@FXML
	private Slider volumeSlider;
	@FXML
	private ProgressBar songProgressBar;
	
	@FXML
	private Button shuffleButton;
	
	@FXML
	private CheckBox LoopCheckBox;
	
	private File directory;
	
	private Media media;
	
	private MediaPlayer mediaPlayer;
	
	private File[] files;
	
	private ArrayList<File> songs;
	
	private int songIndex=0;
	
	private boolean playing;
	
	private Timer timer;
	
	private TimerTask task;
	
	private boolean repeat;
	@FXML
	private Slider progressSlider;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		volumeSlider.valueProperty().addListener((ChangeListener<? super Number>) new ChangeListener <Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				mediaPlayer.setVolume(volumeSlider.getValue()*0.01);
				
			}
			
		});
		
	
	}
	
	public void repeatSong() {
		if (LoopCheckBox.isSelected())
			repeat=true;
		else
			repeat=false;
			
	}
	
	public void choosePlaylist() {
		
		try {
		songs = new ArrayList <File>();
		FileChooser fc = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("txt files (*.txt)", "*.txt");
		String dir = System. getProperty("user.home");
		fc.setInitialDirectory(new File(dir));
		fc.getExtensionFilters().add(extFilter);
		File result = fc.showOpenDialog(null);
		
		
		if(result!=null) {
	
			directory = result;
		
		
		int count =0;
		
		Scanner s1 = new Scanner(directory);
		while(s1.hasNextLine()) {
		    String line = s1.nextLine();
			count++;
			}
		
		s1.close();
		
		
		Scanner s2 = new Scanner(directory);
		
		File [] temp = new File [count];
		for(int i=0; i<count; i++) 
			temp[i] = new File(s2.nextLine());
		s2.close();
		
		files = temp;
		if(files!=null) {
			for(File f:files) {
				songs.add(f);
			}
		}
		
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer = null;
	    }
		
		songIndex =0; 
		
		media = new Media(songs.get(songIndex).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		
		songTitle.setText(songs.get(songIndex).getName());

		
		
		}
		}
		
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
	}
	
	public void addPlaylist() {
		try {
		FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("AddPlaylistScreen.fxml"));
		Parent root = (Parent) fxmlloader.load();
		Stage stage = new Stage();
		stage.setTitle("Add Playlist");
		stage.setScene(new Scene(root));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.show();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	public void playSong(){
		
		if(mediaPlayer!=null) {
		startTimer();
		mediaPlayer.setVolume(volumeSlider.getValue()*0.01);
		mediaPlayer.play();
		if(!repeat) {
		mediaPlayer.setOnEndOfMedia(() -> {
		      nextSong();
		 });
		}
		else
			mediaPlayer.setOnEndOfMedia(() -> {
			      resetSong();
			 });
		}
		
	}
	
	public void previousSong() {
		if(mediaPlayer!=null) {
		if(songIndex>0) {
			songIndex--;
			pauseSong();
			
			if(playing) {
				cancelTimer();
				
			}
			
			media = new Media(songs.get(songIndex).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			songTitle.setText(songs.get(songIndex).getName());
			playSong();
		}
		else {
			songIndex = songs.size()-1;
			pauseSong();
			
			if(playing) {
				cancelTimer();
			}
			
			media = new Media(songs.get(songIndex).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			songTitle.setText(songs.get(songIndex).getName());
			playSong();
		}
		}
	}
	
	public void pauseSong() {
		if(mediaPlayer!=null && playing) {
		cancelTimer();
		mediaPlayer.pause();
		}
	}
	
	public void nextSong() {
		if(mediaPlayer!=null) {
		if (songIndex<songs.size()-1) {
			songIndex++;
		mediaPlayer.stop();
		
		if(playing) {
			cancelTimer();
		}
		
		media = new Media(songs.get(songIndex).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		songTitle.setText(songs.get(songIndex).getName());
		
		playSong();
		}
		else {
			songIndex=0;
			mediaPlayer.stop();
			
			if(playing) {
				cancelTimer();
			}
			media = new Media(songs.get(songIndex).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			songTitle.setText(songs.get(songIndex).getName());
			playSong();
		}
		}
	}
	
	public void shuffle() {
		if(mediaPlayer!=null) {
		Collections.shuffle(songs);
		}
	}
	
	public void resetSong() {
		if(mediaPlayer!=null) {
		songProgressBar.setProgress(0);
		mediaPlayer.seek(Duration.seconds(0));
		}
	}
	
	public void startTimer() {
		timer = new Timer();
		task = new TimerTask() {
			
			public void run() {
				playing =true;
				double currentTime = mediaPlayer.getCurrentTime().toSeconds();
				double duration = media.getDuration().toSeconds();
				
				songProgressBar.setProgress(currentTime/duration);
				progressSlider.setMax(duration);
				progressSlider.setValue(currentTime);
				progressSlider.valueProperty().addListener( new ChangeListener <Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
						songProgressBar.setProgress(progressSlider.getValue()/duration);
					}
				
				});
				
				if(currentTime/duration == 1.0) {
					cancelTimer();
					}
			}
			
		};
		
		timer.scheduleAtFixedRate(task, 0, 1000);
		
	}
	
	public void cancelTimer() {
		playing = false;
		timer.cancel();
	}
	

}
