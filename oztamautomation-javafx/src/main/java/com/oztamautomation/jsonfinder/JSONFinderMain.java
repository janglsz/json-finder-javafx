package com.oztamautomation.jsonfinder;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;


public class JSONFinderMain extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
//			// Create the main anchor pane from the FXML file
//			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("JSONFinderView.fxml"));
			
			// Create a FXML file loader object from the JSONFinderView.fxml file
			FXMLLoader fxmlFileLoader = new FXMLLoader(JSONFinderMain.class.getResource("JSONFinderView.fxml"));
			// Create the main GUI anchor pane and load the FXML objects onto it
			AnchorPane anchorPane = fxmlFileLoader.load();

			// Create an instance of the Controller from the FXML controller
			JSONFinderController jsonFinderController = fxmlFileLoader.getController();
			jsonFinderController.setMain(this);
			
			// Create the main container for all contents in the Scene graph
			Scene scene = new Scene(anchorPane,1300,750);
			
			// Apply the CSS
			scene.getStylesheets().add(getClass().getResource("JSONFinder.css").toExternalForm());
			
			// Specify the scene to be used on the Stage
			primaryStage.setScene(scene);
			
			// Show the GUI
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
