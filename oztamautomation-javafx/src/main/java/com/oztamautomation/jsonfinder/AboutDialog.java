package com.oztamautomation.jsonfinder;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AboutDialog {
	
	public static void showAboutDialog() {
		
		try {
			// Create a FXML file loader object from the JSONFinderView.fxml file
			FXMLLoader fxmlFileLoader = new FXMLLoader(Main.class.getResource("AboutDialog.fxml"));
			
			// Create the main GUI anchor pane and load the FXML objects onto it
			AnchorPane anchorPane = fxmlFileLoader.load();
			
			// Create an instance of the Controller from the FXML controller
			JSONFinderController jsonFinderController = fxmlFileLoader.getController();
			
			// Create the main container for all contents in the Scene graph
			Scene scene = new Scene(anchorPane,480,450);
			
			// NEXT STEPS:
			// 1. Apply the same MVC classes/files/code to the AboutDialog
			
//			// Create the Stage for the About dialog
//			Stage stageAboutDialog = new Stage();
//			
//			// Give the About dialog modal control when launched
//			stageAboutDialog.initModality(Modality.APPLICATION_MODAL);
//			
//			// Set the title
//			stageAboutDialog.setTitle("About Dialog");

			
		} catch (Exception e) {
			
		}
		
		
		
	
	}

}
