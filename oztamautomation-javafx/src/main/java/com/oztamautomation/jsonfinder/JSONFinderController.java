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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;

public class JSONFinderController {
	
	// OBJECT DECLARATION
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
		this.driver = driver;
		this.wait = wait;
		
		// Copy geckodriver.exe to the local desktop for Windows
		exportGeckoDriver("/geckodriver.exe");
		
		// Export geckodriver to the local desktop for Mac
		exportGeckoDriver("/geckodriver");
	}
	
	/**
	 * This method handles the closing of the application from the File menu
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
	 * This method shows a dialog containing Help Information
	 * 
	 * @param event	Click event for Help > About
	 */
	@FXML
	public void showAboutDialog(ActionEvent event) {
		
		// Set the type of alert
		Alert alert = new Alert(AlertType.INFORMATION);
		
		// Set the dialog width
		alert.getDialogPane().setMinWidth(425);
		
		// Set the dialog title
		alert.setTitle("About JSON Finder");
		
		// Set the dialog heading
		alert.setHeaderText("HELP INFORMATION");
		
		// Set the dialog content text
		alert.setContentText("SYSTEM REQUIREMENTS:\r\n" + 
				"1. Java (version 1.8) must be installed\r\n" + 
				"2. Firefox must be installed\r\n" + 
				"\r\n" + 
				"DESCRIPTION:\r\n" + 
				"JSON Finder will read each line of Node.js input and find the corresponding \r\n" + 
				"JSON data using the Node.js timestamp and event type (LOAD, BEGIN, etc.) \r\n" + 
				"\r\n" + 
				"Using Selenium WebDriver, the JSON data is extracted from either: \r\n" + 
				"1. Production: https://dashboard.oztam.com.au\r\n" + 
				"2. Staging: https://sdashboard.oztam.com.au \r\n" + 
				"\r\n" + 
				"The application copies the Firefox driver (geckodriver.exe) onto the desktop." + 
				"The JSON data is then written to a \"JSON Log File.txt\", on the users " + 
				"desktop. \r\n" + 
				"\r\n" + 
				"TROUBLESHOOTING:\r\n" + 
				"If the application is prematurely quit, geckdriver.exe may need to be manually " + 
				"quit from Windows Task Manager to successfully run the appliation again." + 
				"\r\n\r\n" + 
				"Created by Kevin Jang.");
		
		// Show the About Dialog
		alert.show();
	}
	
	
	/**
	 *  This method Handles the "Get JSON Logs" button click event in the following manner: 
	 *  1. It gets the Node.js input from the TextArea 
	 *  2. It extracts the Node.js time stamp and event type from the Node.js input 
	 *  3. It uses the extracted time stamp and event type with Selenium WebDriver to find the related JSON data on the OzTAM dashboard website 
	 *  4. It extracts the related JSON data
	 *  5. It creates a text file on the desktop and saves the extracted JSON data to the file
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
		
		// Check an environment button is selected and set the related baseURL
		if (!isEnvironmentSet()) {
			// Exit the method if no enironment is selected
			return;
		}
		
		// Launch Firefox
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
		
		// Wait until the "Discover" navigation menu link appears, then click it
		wait.until(ExpectedConditions.presenceOfElementLocated(By.className("global-nav-link__title")));
		driver.findElement(By.className("global-nav-link__title")).click();
				
		// Wait for and then click the drop-down control containing the "oztam_meter_index*" filter
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//i[@class='caret pull-right']")));
		driver.findElement(By.xpath("//i[@class='caret pull-right']")).click();
		
		// Wait for and then click "oztam_meter_index*" drop down item in the drop down list
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(), 'oztam_meter_index*')]")));
		driver.findElement(By.xpath("//div[contains(text(), 'oztam_meter_index*')]")).click();
		
		// Wait for the time range button to appear and click it
		Thread.sleep(1000);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//pretty-duration[contains(text(), 'Last 15 minutes')]")));
		driver.findElement(By.xpath("//pretty-duration[contains(text(), 'Last 15 minutes')]")).click();
		
		// Wait for the "Last 12 hours" link to appear, then click it
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
			
			// TROUBLESHOOTING CODE:
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
    static public void exportGeckoDriver(String resourceName) throws Exception {
        
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
	 *  Checks if the environment has been selected.
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
			
			// Extract and set the sessionId
			Main.setCurrentSessionId(extractMatchingString(nextSessionId, Main.getSessionIdPattern()));
						
			// Enter the sessionId into the search field
			clickAndFillInputField(driver, wait, By.className("kuiLocalSearchInput"), "sessionId: \"" + Main.getCurrentSessionId() + "\"");
			
			// Press the keyboard [Return] key to apply the sessionId filter
			driver.findElement(By.className("kuiLocalSearchInput")).sendKeys(Keys.RETURN);
			
			// Reload the page - This is to overcome a bug in selenium 3.13.0 where the search button is successfully clicked but the resulting page does not load.
			// Using Thread.sleep and explicit and implicit wait does not overcome this bug
			// Implementing methods that wait for AJAX, JavaScript, JQuery and Angular to load also do to resolve the issue
			driver.get("https://dashboard.oztam.com.au");
			driver.get("https://dashboard.oztam.com.au/app/kibana#/discover?_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now-12h,mode:quick,to:now))&_a=(columns:!(_source),index:'oztam_meter_index*',interval:auto,query:(query_string:(analyze_wildcard:!t,query:'sessionId:%20%22"
					+ Main.getCurrentSessionId() 
					+ "%22')),sort:!(createdAt,desc))");			
			
			// Click the search button
			clickElement(driver, wait, By.className("kuiLocalSearchButton"));			
			
			// Wait for all disclosure triangles to appear
			Thread.sleep(2000);	
			
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
	
	
//	/**
//	 * This method waits for JQuery and JavaScript to finish loading
//	 * 
//	 * @return
//	 */
//	public static boolean waitForJSandJQueryToLoad(WebDriverWait wait) {
//
//	    // wait for jQuery to load
//	    ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
//	      @Override
//	      public Boolean apply(WebDriver driver) {
//	        try {
//	          return ((Long)((JavascriptExecutor)driver).executeScript("return jQuery.active") == 0);
//	        }
//	        catch (Exception e) {
//	          // no jQuery present
//	          return true;
//	        }
//	      }
//	    };
//
//	    // wait for Javascript to load
//	    ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
//	      @Override
//	      public Boolean apply(WebDriver driver) {
//	        return ((JavascriptExecutor)driver).executeScript("return document.readyState").toString().equals("complete");
//	      }
//	    };
//
//	  return wait.until(jQueryLoad) && wait.until(jsLoad);
//	}

} // END JSONFinderController
