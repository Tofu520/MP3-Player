package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddPlaylistController {
	
	@FXML
	private Button addSongsButton;
	
	@FXML
	private Button confirmButton;
	
	@FXML
	private ListView <File> list;
	
	private int count;
	
	private ArrayList <String> songPaths = new ArrayList();
	
	public void addSongs() {
		FileChooser fc = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("mp3 files (*.mp3)", "*.mp3");
		String dir = System. getProperty("user.home");
		fc.setInitialDirectory(new File(dir));
		fc.getExtensionFilters().add(extFilter);
		List <File> selectedFiles = fc.showOpenMultipleDialog(null);
		
		if(selectedFiles != null) {
		for(int i=0; i<selectedFiles.size(); i++) {
				list.getItems().add(selectedFiles.get(i).getAbsoluteFile());
				count++;
		}
		
		}
	}
	
	
	public void  confirm() {	
		try {
			FileChooser fc = new FileChooser();
			String userDirectory = System.getProperty("user.home");
			fc.setInitialDirectory(new File(userDirectory));
			for (File currentFile: list.getItems()){
				if(!currentFile.getName().endsWith(".txt")){
					currentFile = new File(currentFile.getAbsolutePath());
					songPaths.add(currentFile.getAbsolutePath());
					
				}
				
			}
			
			File file = fc.showSaveDialog(new Stage());
			if (file!=null) {
			FileWriter fw = new FileWriter(file);
			for(String s:songPaths)
				fw.write(s + " \n");
			fw.close();
			}
			
			
		}
		catch (Exception error){
			error.printStackTrace();

		}
		
		
		
		
		
		
		
	}
	
	
}
