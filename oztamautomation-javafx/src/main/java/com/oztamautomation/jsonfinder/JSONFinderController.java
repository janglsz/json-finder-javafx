package com.oztamautomation.jsonfinder;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class JSONFinderController {
	
	// Views
	@FXML private TextArea textArea;
	@FXML private Button buttonGetJSONLogs;
	
	// Create an instance of the main class
	private JSONFinderMain jsonFinderMain;
	
	public void setMain(JSONFinderMain jsonFinderMain) {
		
		// Get the instance of the main class object
		this.jsonFinderMain = jsonFinderMain;
	}
	
	public void handleButton(ActionEvent event) {
		

	}
	
}
