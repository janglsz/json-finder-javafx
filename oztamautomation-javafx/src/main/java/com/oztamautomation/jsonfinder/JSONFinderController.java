package com.oztamautomation.jsonfinder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;

public class JSONFinderController {
	
	// OBJECT DECLARATION
	private Main main;
	private WebDriver driver;
	private WebDriverWait wait;
	
	// GUI ELEMENTS
	/** The Close Menu Item */
	@FXML
	private MenuItem menuItemClose;
	/** Input TextArea from the Node.js input */
	@FXML 
	private TextArea textArea;
	/** Button Controls */
	@FXML 
	private Button buttonGetJSONLogs;
	@FXML
	private ToggleButton toggleBtnProduction;
	@FXML
	private ToggleButton toggletBtnStaging;
	/** Provides the user the status of the application */
	@FXML
	private Label lblStatus;
	
	// CONSTRUCTOR
	public JSONFinderController() {
		
		// SET PROPERTIES		
		System.setProperty("webdriver.gecko.driver", Main.getFirefoxDriverFile());
	}
	
	/** This method gets the instance of the main class and WebDriver objects. It also copies
	 *  the geckodriver.exe file to the desktop
	 * 
	 * @param jsonFinderMain The main class
	 * @throws Exception 
	 */
	public void setMain(Main main, WebDriver driver, WebDriverWait wait) throws Exception {
		
		// Get the instance of the main class object
		this.main = main;
		this.driver = driver;
		this.wait = wait;
		
		// Copy geckdriver.exe to the local desktop
		ExportResource("/geckodriver.exe");
		
	}
	
	/**
	 * This method handles closing of the application from the menu and close button
	 * 
	 * @param event	The File > Close event
	 */
	@FXML
	public void exitApplication(ActionEvent event) {
		
		// Close the application
		Platform.exit();
		
		//  Quit geckodriver.exe from running in Task Manager
		driver.quit();
	}
	
	
	/**
	 *  This method Handles the "Get JSON Logs" button click event. It gets the Node.js input 
	 *  from the TextArea. Uses the Node.js timestamps and event types with Selenium
	 *  WebDriver to extract the related JSON data, then exports the data to a text file on
	 *  the desktop.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@FXML
	private void createJSONLog(ActionEvent event) throws InterruptedException {
		
		// Get and save the Node.js input from the textArea
		Main.setNodejsString(textArea.getText());
		
		// Separate the Node.js events into individual Strings and store in the nodejsEvents[] array	
		Main.setNodejsEvents(Main.getNodejsString().split("\n"));
		
		// TESTING CODE: Print the Node.js array to the console
		System.out.println("Printing NodejsEvents[] array contents....");
		for (String singleNodejsEvent: Main.getNodejsEvents()) {
			System.out.println(singleNodejsEvent);
		}
		
		// Check an environment is selected and set the baseURL
		if (!isEnvironmentSet()) {
			// Exit the method if no enironment is selected
			return;
		}
		
		// Selenium WebDriver object for Firefox
		driver = new FirefoxDriver();
		
		// Create the object that controls the max wait time
		wait = new WebDriverWait(driver, Main.getWaitTime());
		
		// Launch the web page
		driver.get(Main.getBaseURL());
		
		
		// Enter the username and password
		clickAndFillInputField(driver, wait, By.id("username"), Main.getUsername());
		clickAndFillInputField(driver, wait, By.id("password"), Main.getPassword());
		
		// Find and click the Login in button
		driver.findElement(By.className("login")).click();
		
		
		// Wait for the time range button to appear and click it
		wait.until(ExpectedConditions.presenceOfElementLocated(By.className("fa-clock-o")));
		driver.findElement(By.className("fa-clock-o")).click();
		
		// Wait for the "Last 12 hours" option to appear, then click it
		wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Last 12 hours")));
		driver.findElement(By.linkText("Last 12 hours")).click();
		
		// Create the "JSON Logs.txt" file and add the header text
		createFileHeader(Main.getFile(), Main.getFw(), Main.getBw());
		
		
		// Iterate through each individual node.js event
		for (String singleNodejsEvent : Main.getNodejsEvents()) {
			
			// If we have a new sessionId, perform another sessionId search for new JSON data
			hasSessionIdChanged(driver, wait, extractMatchingString(singleNodejsEvent, Main.getSessionIdPattern()));
			
			// Get the time stamp from the single Node.js event and save it
			Main.setCurrentTimestamp(extractMatchingString(singleNodejsEvent, Main.getTimestampPattern()));
			
			// Get the event type from the single Node.js event and save it
			Main.setCurrentEventType(extractMatchingString(singleNodejsEvent, Main.getEventPattern()));	
			
			// TESTING CODE:
			System.out.println("Getting JSON Log for SessionId: " + Main.getCurrentSessionId() + ", Timestamp: " + Main.getCurrentTimestamp() + ", AND Event Type: " + Main.getCurrentEventType());
			
			// On each Table tab, loop through each timestamp and event type
			for (int uniqueXpathNumber = 1; uniqueXpathNumber <= Main.getDisclosureTriangles().size(); ++uniqueXpathNumber) {
				
					try {

					// Index used to step through JSON property values, listed on the table tab. The timestamp index usually 
					// starts at [23] and increases (e.g. /tr[23]/td[3]/div[@class='doc-viewer-value']/span). Hence,
					// the assigned value below
					Main.setJsonPropertyIndex(23);
					
					// For storing the clicked JSON property value on table tab
					String clickedText = "";
					
					// Starting at row [23], keep looking down the table for the timestamp text, until the timestamp is found
					do {
						
						// Get the 'timestamp' text starting at row [23]
						clickedText = driver.findElement(By.xpath("//*/tr[" 
								+ (uniqueXpathNumber*2) + "]/td/doc-viewer/div[@class='doc-viewer']"
								+ "/div[@class='doc-viewer-content']/render-directive/table[@class='table table-condensed']/tbody/tr[" 
								+ Main.getJsonPropertyIndex() + "]/td[3]/div[@class='doc-viewer-value']/span")).getText();
						
						// Extract the clicked text and save it
						Main.setExtractedTimestamp(extractMatchingString(clickedText, Main.getTimestampPattern()));
						
						// Increment the JSON property index for the next loop
						Main.setJsonPropertyIndex(Main.getJsonPropertyIndex() + 1);
						
					} while (!Pattern.matches(Main.getTimestampPattern(), Main.getExtractedTimestamp()));

					
					// Get the entire 'events' text from the JSON table and save it
					Main.setExtractedEventType(driver.findElement(By.xpath("//*/tr[" 
							+ (uniqueXpathNumber*2) + "]/td/doc-viewer/div[@class='doc-viewer']/div[@class='doc-viewer-content']" 
							+ "/render-directive/table[@class='table table-condensed']/tbody/tr[8]/td[3]/div[@class='doc-viewer-value']/span")).getText());
					
					// Extract the event type from the entire 'event' text and save it
					Main.setExtractedEventType(extractMatchingString(Main.getExtractedEventType(), Main.getEventPattern()));
					
					// Check we have the right JSON data
					if (Objects.equals(Main.getExtractedTimestamp(), Main.getCurrentTimestamp()) && Objects.equals(Main.getExtractedEventType(), Main.getCurrentEventType())) {
						
						// The extracted timestamp and event type match the Node.js timestamp and event type
						
						// TESTING CODE:
						System.out.println("Extracted Timestamp: " + Main.getExtractedTimestamp() + " AND Extracted Event Type: " + Main.getExtractedEventType());
						System.out.println("The Extracted Timestamp MATCHES the currentTimestamp !!!!!!!!!!!!!!!");
						
						// Open the corresponding JSON tab
						clickElement(driver, wait, By.xpath("//*/tr[" + (uniqueXpathNumber*2) + "]/td/doc-viewer/div[@class='doc-viewer']/ul[@class='nav nav-tabs']/li[2]/a"));
						
						// Wait for the JSON text to appear
						wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body[@id='kibana-body']/div[@class='content']/div[@class='app-wrapper']/div[@class='app-wrapper-panel']" 
								+ "/div[@class='application tab-discover']/discover-app[@class='app-container']/div[@class='container-fluid']/div[@class='row'][2]/div[@class='discover-wrapper col-md-10']"
								+ "/div[@class='discover-content']/div[@class='results']/div[@class='discover-table']/doc-table/div[@class='doc-table-container']/table[@class='kbn-table table']/tbody/tr[" 
								+ (uniqueXpathNumber*2) 
								+ "]/td/doc-viewer/div[@class='doc-viewer']/div[@class='doc-viewer-content']/render-directive/div[@id='json-ace']/div[@class='ace_scroller']/div[@class='ace_content']")));
						
						// Get the JSON text using the uniqueXpathNumber which is incremented by 2 as you move down each disclosure triangle
						Main.setExtractedJsonString(driver.findElement(By.xpath("/html/body[@id='kibana-body']/div[@class='content']/div[@class='app-wrapper']/div[@class='app-wrapper-panel']" 
								+ "/div[@class='application tab-discover']/discover-app[@class='app-container']/div[@class='container-fluid']/div[@class='row'][2]/div[@class='discover-wrapper col-md-10']"
								+ "/div[@class='discover-content']/div[@class='results']/div[@class='discover-table']/doc-table/div[@class='doc-table-container']/table[@class='kbn-table table']/tbody/tr[" 
								+ (uniqueXpathNumber*2) 
								+ "]/td/doc-viewer/div[@class='doc-viewer']/div[@class='doc-viewer-content']/render-directive/div[@id='json-ace']/div[@class='ace_scroller']/div[@class='ace_content']")).getText());
						
						
						// Add the JSON text to the "JSON Logs.txt" tile
						writeJSONDataToFile(Main.getExtractedJsonString(), Main.getFile(), Main.getFw(), Main.getBw(), Main.getCurrentEventType());
						
						// We have a match. No need to iterate through the remaining disclosure triangles, exit the for loop
						break;
						
					} // END if
					
				} catch (Exception e) {
					// Catches exceptions caused if a disclosure triangle cannot be found
					
					System.out.println("Exception caught....\r\n" + e.toString());
					
				} // END catch
				
			} // END for
			
		} // END for
		
		// Kill the geckodriver.exe and quite Firefox
		driver.quit();
		
		// Change the status label
		lblStatus.setText("STATUS: COMPLETE. \"JSON Logs.txt\" has been created on the desktop");
				
	} // END createJSONLog
	
	
	/**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param 	resourceName ie.: "/geckodriver.exe"
     * @return 	The path to the exported resource
     * @throws 	Exception
     */
    static public void ExportResource(String resourceName) throws Exception {
        
    	InputStream streamIn = null;
        OutputStream streamOut = null;
        
        // Stores the file path of the runnable jar for this application
        String jarFolder;
        
        try {
        	// Set the input stream file path
            streamIn = Main.class.getResourceAsStream(resourceName); //note that each / is a directory down in the "jar tree" been the jar the root of the tree
            
            // Verify the input stream could be set
            if(streamIn == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            // Counter for reading the number of bytes in the file
            int readBytes;
            // Array to store all bytes in the file
            byte[] buffer = new byte[4096];
            
            // Get the file path of the runnable jar for this application
            jarFolder = new File(Main.getJsonFinderFile()).getParentFile().getPath().replace('\\', '/');

            // TESTING CODE: Prints the 
            String temp = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            System.out.println(temp);
            
            // Set the output stream file path
            streamOut = new FileOutputStream(jarFolder + resourceName);
            
            // Copy the file from the input stream to the output stream
            while ((readBytes = streamIn.read(buffer)) > 0) {
                streamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            streamIn.close();
            streamOut.close();
        }
    }
	
	
	/** 
	 *  Checks if the enviornment has been selected.
	 *  Sets the baseURL based on the environment
	 * 
	 * @return 	false 	If an environment has not been selected
	 * 			true 	If the baseURL has been set	
	 */
	private boolean isEnvironmentSet() {
		// Check an environment is selected
		if (!(toggletBtnStaging.isSelected() || toggleBtnProduction.isSelected())) {
			lblStatus.setText("STATUS: ERROR - You must select either the Staging or Production environment!");
			
			return false;
		}
		
		if (toggleBtnProduction.isSelected()) {
			// TESTING CODE:
			System.out.println("Production button is selected");
			
			Main.setBaseURL("https://dashboard.oztam.com.au/");
			lblStatus.setText("STATUS: Creating JSON Log File...");
		}
		
		if (toggletBtnStaging.isSelected()) {
			
			// TESTING CODE:
			System.out.println("Staging button is selected...");
			
			Main.setBaseURL("https://sdashboard.oztam.com.au/");
			lblStatus.setText("STATUS: Creating JSON Log File...");
		} 
		
		// TESTING CODE:
		System.out.println("baseURL has been set to: " + Main.getBaseURL());
		
		return true;
	}
	
	
	/**
	 * This method waits for an input field to appear, clicks it, then enters the provided text
	 * 
	 * @param driver			The Selenium Webdriver object
	 * @param wait				Object to control the max wait time before selenium webdriver performs an action
	 * @param elementIdentifier	The By object which identifies the web page element
	 * @param textInput			The text to enter into the input field
	 */
	static void clickAndFillInputField(WebDriver driver, WebDriverWait wait, By elementIdentifier, String textInput) {
		// Wait for the field to appear, click it, clear it and enter the inputText
		wait.until(ExpectedConditions.presenceOfElementLocated(elementIdentifier));
		driver.findElement(elementIdentifier).click();
		driver.findElement(elementIdentifier).clear();
		driver.findElement(elementIdentifier).sendKeys(textInput);
	}

	
	/**
	 * This method creates the output file and writes the header information to the file
	 * 
	 * @param file	The file object
	 * @param fw	The file writer
	 * @param bw	BufferredWriter object that uses the fw object to write String's of text to the file
	 */
	static void createFileHeader(File file, FileWriter fw, BufferedWriter bw) {
		
		try {

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// true = append file
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);

			bw.write("TEST - \r\n\r\n\r\n");
			bw.write("RESULT - \r\n\r\n\r\n");
			bw.write("ISSUES: \r\n\r\n\r\n");
			bw.write("NODE.JS: \r\n");
			
			for (String event : Main.getNodejsEvents()) {
				bw.write(event + "\r\n");
			}
			
			
			bw.write("\r\n\r\n\r\n");
			bw.write("FIDDLER/DASHBOARD DATA: \r\n\r\n\r\n");

			System.out.println("Header text has been added to the file");

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

					try {
						// Close the streams
						if (bw != null)
							bw.close();
						if (fw != null)
							fw.close();

					} catch (IOException ex) {
						ex.printStackTrace();
					} 
		} // END finally
	}
	
	/**
	 * This methods searches a String object for a matching regular expression. It returns 
	 * the result if found, and an empty String "" if not.
	 * 
	 * @param searchString 		The String to search
	 * @param regexPattern 		The String pattern to match
	 * @return matchingString 	Returns a String that matches the regular expression. 
	 * 							Returns an empty String (i.e. "") if nothing is found
	 */
	private static String extractMatchingString(String searchString, String regexPattern) {
		
		// Stores the matching String
		String matchingString = "";
		
		// The pattern to match
		Pattern pattern = Pattern.compile(regexPattern);		
		
		// Stores the matching regular expression
		Matcher matcher;		
		
		// Get the matching regular expression
		matcher = pattern.matcher(searchString);
		
		// Check that we have a match
		if (matcher.find()) {
			// Save the matching String
			matchingString = matcher.group(0);
		}
		else {
			// Print an error message
			System.out.println("WARNING: We do not have a matching String: " + searchString);
		}
		
		// Return the matching String
		return matchingString;
		
	} // END extractMatchingString
	
	
	/**
	 * This method check if the sessionId for the next Node.js event is different. If
	 * so, it performs a new sessionId search, using the new sessionId
	 * 
	 * @param driver				Selenium WebDriver object
	 * @param wait					WebDriverWait object
	 * @param nextSessionId			The sessionId for the next Node.js event
	 * @throws InterruptedException	To handle any exceptions caused by Thread.sleep(2000)
	 */
	static void hasSessionIdChanged(WebDriver driver, WebDriverWait wait, String nextSessionId) throws InterruptedException {
		
		// Check if the sessionId has changed
		if (!nextSessionId.equals(Main.getCurrentSessionId())) {
			
			// Get the current sessionId
			Main.setCurrentSessionId(extractMatchingString(nextSessionId, Main.getSessionIdPattern()));
						
			// Enter the sessionId into the search field
			clickAndFillInputField(driver, wait, By.className("kuiLocalSearchInput"), "sessionId: " + Main.getCurrentSessionId());
			
			// Click the search button
			clickElement(driver, wait, By.className("kuiLocalSearchButton"));
			
			// Wait for all disclosure triangles to appear
			Thread.sleep(500);	
			
			// Get the number of disclosure triangles on the page
			Main.setDisclosureTriangles(driver.findElements(By.xpath("//*/tr[@class='discover-table-row'][*]/td[1]/i[@class='fa discover-table-open-icon fa-caret-right']")));
			System.out.println("disclosureTriangles: " + Main.getDisclosureTriangles().size());
			
			// Wait until the first disclosure triangle appears
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*/tr[@class='discover-table-row'][1]/td[1]/i[@class='fa discover-table-open-icon fa-caret-right']")));
			
			// Expand all disclosure triangles on the web page
			expandAllDisclosureTriangles(driver);
			
		} // END if

	} // END hasSessionIdChanged()
	
	
	/**
	 * This method waits for an element to appear, then clicks it
	 * 
	 * @param driver 			The Selenium Webdriver object
	 * @param wait 				Object to control the max wait time before selenium webdriver performs an action
	 * @param elementIdentifier The By object which identifies the web page element
	 */
	static void clickElement(WebDriver driver, WebDriverWait wait, By elementIdentifier) {
		// Wait for the element to appear
		wait.until(ExpectedConditions.presenceOfElementLocated(elementIdentifier));
		// Click the element
		driver.findElement(elementIdentifier).click();
	}
	
	
	/**
	 * This method expands all disclosure triangles on the web page
	 * 
	 * @param driver	The Selenium Webdriver object
	 */
	private static void expandAllDisclosureTriangles(WebDriver driver) {
		
		// Open all of the disclosure triangles
		for (int i = 1; i <= Main.getDisclosureTriangles().size(); ++i) {
			
			try {				
				
				// Open all of the disclosure triangles
				driver.findElement(By.xpath("//*/tr[@class='discover-table-row'][" + i + "]/td[1]/i[@class='fa discover-table-open-icon fa-caret-right']")).click();
				
			} catch (Exception e) {
				
				// Catches exceptions caused if a disclosure triangle cannot be found
				
			} // END catch
			
		} // END for
	}
	
	
	/**
	 * This method write the JSON data copied from the web page to the JSON log file
	 * 
	 * @param JSONString	The JSON String to write to the file
	 * @param file			The file to write to
	 * @param fw			The FileWriter object
	 * @param bw			The BufferedWriter object
	 * @param eventType		The type of Node.js event e.g. LOAD, BEGIN, etc.
	 */
	static void writeJSONDataToFile(String JSONString, File file, FileWriter fw, BufferedWriter bw, String eventType) {
		
		try {

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// true = append file
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);

			// Write the current event type to the file
			bw.write(eventType + ": \r\n");
			
			// Write the JSON String to the file
			bw.write(JSONString);
			bw.write("\r\n\r\n\r\n");

			System.out.println("JSON Data has been added to " + Main.getJsonLogFileName() + "\r\n\r\n");

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

					try {
						// Close the streams
						if (bw != null)
							bw.close();

						if (fw != null)
							fw.close();

					} catch (IOException ex) {
						ex.printStackTrace();
					} 
		} // END finally
		
		
	} // END writeJSONDataToFile
	
	
} // END JSONFinderController
