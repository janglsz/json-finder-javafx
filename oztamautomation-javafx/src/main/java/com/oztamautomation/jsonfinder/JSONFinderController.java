package com.oztamautomation.jsonfinder;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class JSONFinderController {
	
	// OBJECT DECLARATION
	
	@FXML private TextArea textArea = new TextArea();
	@FXML private Button buttonGetJSONLogs;
	
	// Create an instance of the main class
	private JSONFinderMain jsonFinderMain;
	
	/**Get the instance of the main class
	 * 
	 * @param jsonFinderMain
	 */
	public void setMain(JSONFinderMain jsonFinderMain) {
		
		// Get the instance of the main class object
		this.jsonFinderMain = jsonFinderMain;
	}
	
	@FXML
	private void getJSONData(ActionEvent event) {
		
		// TROUBLESHOOTING CODE
		// Print the text in the textArea to the console
		System.out.println("Text Area Text is: " + textArea.getText()); 
		
	}
	

	
}
