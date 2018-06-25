/* NEXT STEPS:
 * 1. Implement a toggle button for staging and production
 * 
 * 
 * 
 * 
 * 
 */


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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class JSONFinderController {
	
	// OBJECT DECLARATION
	private JSONFinderMain jsonFinderMain;
	private WebDriver driver;
	private WebDriverWait wait;
	
	// GUI ELEMENTS
	@FXML 
	private TextArea textArea;
	@FXML 
	private Button buttonGetJSONLogs;
	@FXML
	private ToggleButton toggleBtnProduction;
	@FXML
	private ToggleButton toggletBtnStaging;
	@FXML
	private Label lblStatus;
	
	// CONSTRUCTOR
	public JSONFinderController() {
		
		// SET PROPERTIES		
		System.setProperty("webdriver.gecko.driver", JSONFinderMain.getFirefoxDriverFile());
	}
	
	/**Get the instance of the main class
	 * 
	 * @param jsonFinderMain The main class
	 */
	public void setMain(JSONFinderMain jsonFinderMain) {
		
		// Get the instance of the main class object
		this.jsonFinderMain = jsonFinderMain;
		

	}
	
	@FXML
	private void createJSONLog(ActionEvent event) {
		
		// Get and save the Node.js input from the textArea
		JSONFinderMain.setNodejsString(textArea.getText());
		
		// Separate the Node.js events into individual Strings and store in the nodejsEvents[] array	
		JSONFinderMain.setNodejsEvents(JSONFinderMain.getNodejsString().split("\n"));
		
		// TESTING CODE: Print the Node.js array to the console
		System.out.println("Printing NodejsEvents[] array contents....");
		for (String singleNodejsEvent: JSONFinderMain.getNodejsEvents()) {
			System.out.println(singleNodejsEvent);
		}
		
		// Check an environment is selected and set the baseURL
		checkEnvironmentAndSetBaseURL();
		
//		// Check an environment is selected
//		if (!(toggletBtnStaging.isSelected() || toggleBtnProduction.isSelected())) {
//			System.out.println("ERROR: You must select either the Staging or Production environment");
//			lblStatus.setText("STATUS: ERROR - You must select either the Staging or Production environment!");
//		}
//		
//		if (toggletBtnStaging.isSelected()) {
//			System.out.println("Staging button is selected...");
//			JSONFinderMain.setBaseURL("https://sdashboard.oztam.com.au/");
//			lblStatus.setText("STATUS: Creating JSON Log File...");
//		} 
//		
//		if (toggleBtnProduction.isSelected()) {
//			System.out.println("Production button is selected");
//			JSONFinderMain.setBaseURL("https://dashboard.oztam.com.au/");
//			lblStatus.setText("STATUS: Creating JSON Log File...");
//		}
		
//		if (toggletBtnStaging.isSelected()) {
//			JSONFinderMain.setBaseURL("https://dashboard.oztam.com.au/");
//		} else {
//			JSONFinderMain.setBaseURL("https://sdashboard.oztam.com.au/");
//		}
//		
//		System.out.println("Printing baseURL...");
//		System.out.println(JSONFinderMain.getBaseURL());
		
		
///////////// NEED TO REFACTOR THE CODE BELLOW ///////////////////////
		
//		// Selenium WebDriver object for Firefox
//		driver = new FirefoxDriver();
		
//		// Create the object that controls the max wait time
//		wait = new WebDriverWait(driver, JSONFinderMain.getWaitTime());
		
		// Launch the web page
//		driver.get(getBaseURL());
//		
//		// Enter the username and password
//		clickAndFillInputField(driver, wait, By.id("username"), getUsername());
//		clickAndFillInputField(driver, wait, By.id("password"), getPassword());
//		
//		// Find and click the Login in button
//		driver.findElement(By.className("login")).click();
//		
//		// Wait for the time range button to appear and click it
//		wait.until(ExpectedConditions.presenceOfElementLocated(By.className("fa-clock-o")));
//		driver.findElement(By.className("fa-clock-o")).click();
//		
//		// Wait for the "Last 12 hours" option to appear, then click it
//		wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Last 12 hours")));
//		driver.findElement(By.linkText("Last 12 hours")).click();
//		
//		// Create the file and add the header text
//		createFileHeader(file, fw, bw);
//		
//		// Iterate through each individual node.js event
//		for (String singleNodejsEvent : nodejsEvents) {
//			
//			// If we have a new sessionId, perform another sessionId search for new JSON data
//			hasSessionIdChanged(driver, wait, extractMatchingString(singleNodejsEvent, SESSION_ID_PATTERN));
//			
//			// Get the time stamp from the single Node.js event and save it
//			setCurrentTimestamp(extractMatchingString(singleNodejsEvent, TIMESTAMP_PATTERN));
//			
//			// Get the event type from the single Node.js event and save it
//			setCurrentEventType(extractMatchingString(singleNodejsEvent, EVENT_PATTERN));	
//			
//			// TROUBLESHOOTING CODE:
//			System.out.println("Getting JSON Log for SessionId: " + getCurrentSessionId() + ", Timestamp: " + getCurrentTimestamp() + ", AND Event Type: " + getCurrentEventType());
//			
//			// On each Table tab, loop through each timestamp and event type
//			for (int uniqueXpathNumber = 1; uniqueXpathNumber <= disclosureTriangles.size(); ++uniqueXpathNumber) {
//				
//					try {
//
//					// Index used to step through JSON property values, listed on the table tab. The timestamp index usually 
//					// starts at [23] and increases (e.g. /tr[23]/td[3]/div[@class='doc-viewer-value']/span). Hence,
//					// the assigned value below
//					setJsonPropertyIndex(23);
//					
//					// For storing the clicked JSON property value on table tab
//					String clickedText = "";
//					
//					// Starting at row [23], keep looking down the table for the timestamp text, until the timestamp is found
//					do {
//						
//						// Get the 'timestamp' text starting at row [23]
//						clickedText = driver.findElement(By.xpath("//*/tr[" 
//								+ (uniqueXpathNumber*2) + "]/td/doc-viewer/div[@class='doc-viewer']"
//								+ "/div[@class='doc-viewer-content']/render-directive/table[@class='table table-condensed']/tbody/tr[" 
//								+ jsonPropertyIndex + "]/td[3]/div[@class='doc-viewer-value']/span")).getText();
//						
//						// Extract the clicked text and save it
//						setExtractedTimestamp(extractMatchingString(clickedText, TIMESTAMP_PATTERN));
//						
//						// Increment the JSON property index for the next loop
//						setJsonPropertyIndex(getJsonPropertyIndex() + 1);
//						
//					} while (!Pattern.matches(TIMESTAMP_PATTERN, getExtractedTimestamp()));
//
//					
//					// Get the entire 'events' text from the JSON table and save it
//					setExtractedEventType(driver.findElement(By.xpath("//*/tr[" 
//							+ (uniqueXpathNumber*2) + "]/td/doc-viewer/div[@class='doc-viewer']/div[@class='doc-viewer-content']" 
//							+ "/render-directive/table[@class='table table-condensed']/tbody/tr[8]/td[3]/div[@class='doc-viewer-value']/span")).getText());
//					
//					// Extract the event type from the entire 'event' text and save it
//					setExtractedEventType(extractMatchingString(getExtractedEventType(), EVENT_PATTERN));
//					
//					// Check we have the right JSON data
//					if (Objects.equals(getExtractedTimestamp(), getCurrentTimestamp()) && Objects.equals(getExtractedEventType(), getCurrentEventType())) {
//						
//						// The extracted timestamp and event type match the Node.js timestamp and event type
//						
//						// TROUBLESHOOTING CODE
//						System.out.println("Extracted Timestamp: " + getExtractedTimestamp() + " AND Extracted Event Type: " + getExtractedEventType());
//						System.out.println("The Extracted Timestamp MATCHES the currentTimestamp !!!!!!!!!!!!!!!");
//						
//						// Open the corresponding JSON tab
//						clickElement(driver, wait, By.xpath("//*/tr[" + (uniqueXpathNumber*2) + "]/td/doc-viewer/div[@class='doc-viewer']/ul[@class='nav nav-tabs']/li[2]/a"));
//						
//						// Wait for the JSON text to appear
//						wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body[@id='kibana-body']/div[@class='content']/div[@class='app-wrapper']/div[@class='app-wrapper-panel']" 
//								+ "/div[@class='application tab-discover']/discover-app[@class='app-container']/div[@class='container-fluid']/div[@class='row'][2]/div[@class='discover-wrapper col-md-10']"
//								+ "/div[@class='discover-content']/div[@class='results']/div[@class='discover-table']/doc-table/div[@class='doc-table-container']/table[@class='kbn-table table']/tbody/tr[" 
//								+ (uniqueXpathNumber*2) 
//								+ "]/td/doc-viewer/div[@class='doc-viewer']/div[@class='doc-viewer-content']/render-directive/div[@id='json-ace']/div[@class='ace_scroller']/div[@class='ace_content']")));
//						
//						// Get the JSON text using the uniqueXpathNumber which is incremented by 2 as you move down each disclosure triangle
//						jsonStringFromClipboard = driver.findElement(By.xpath("/html/body[@id='kibana-body']/div[@class='content']/div[@class='app-wrapper']/div[@class='app-wrapper-panel']" 
//								+ "/div[@class='application tab-discover']/discover-app[@class='app-container']/div[@class='container-fluid']/div[@class='row'][2]/div[@class='discover-wrapper col-md-10']"
//								+ "/div[@class='discover-content']/div[@class='results']/div[@class='discover-table']/doc-table/div[@class='doc-table-container']/table[@class='kbn-table table']/tbody/tr[" 
//								+ (uniqueXpathNumber*2) 
//								+ "]/td/doc-viewer/div[@class='doc-viewer']/div[@class='doc-viewer-content']/render-directive/div[@id='json-ace']/div[@class='ace_scroller']/div[@class='ace_content']")).getText();
//						
//						
//						// Add the JSON text to the "JSON Logs.txt" tile
//						writeJSONDataToFile(jsonStringFromClipboard, file, fw, bw, getCurrentEventType());
//						
//						// We have a match. No need to iterate through the remaining disclosure triangles, exit the for loop
//						break;
//						
//					} // END if
//					
//				} catch (Exception e) {
//					// Catches exceptions caused if a disclosure triangle cannot be found
//					
//					System.out.println("Exception caught....\r\n" + e.toString());
//					
//				} // END catch
//				
//			} // END for
//			
//		} // END for
//		
//		// Kill the geckodriver.exe and quite Firefox
//		driver.quit();
				
	} // END createJSONLog
	
	
	/** Checks if the enviornment has been selected.
	 *  Sets the baseURL based on the environment
	 */
	private void checkEnvironmentAndSetBaseURL() {
		// Check an environment is selected
		if (!(toggletBtnStaging.isSelected() || toggleBtnProduction.isSelected())) {
			lblStatus.setText("STATUS: ERROR - You must select either the Staging or Production environment!");
			
			return;
		}
		
		if (toggleBtnProduction.isSelected()) {
			// TESTING CODE:
			System.out.println("Production button is selected");
			
			JSONFinderMain.setBaseURL("https://dashboard.oztam.com.au/");
			lblStatus.setText("STATUS: Creating JSON Log File...");
		}
		
		if (toggletBtnStaging.isSelected()) {
			
			// TESTING CODE:
			System.out.println("Staging button is selected...");
			
			JSONFinderMain.setBaseURL("https://sdashboard.oztam.com.au/");
			lblStatus.setText("STATUS: Creating JSON Log File...");
		} 
		
		// TESTING CODE:
		System.out.println("baseURL has been set to: " + JSONFinderMain.getBaseURL());
	}
	
	
	
//	/**
//     * Export a resource embedded into a Jar file to the local file path.
//     *
//     * @param resourceName ie.: "/geckodriver.exe"
//     * @return The path to the exported resource
//     * @throws Exception
//     */
//    static public String ExportResource(String resourceName) throws Exception {
//        InputStream stream = null;
//        OutputStream resStreamOut = null;
//        String jarFolder;
//        try {
//            stream = JSONFinderMain.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
//            if(stream == null) {
//                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
//            }
//
//            int readBytes;
//            byte[] buffer = new byte[4096];
//            jarFolder = new File(JSON_FINDER_FILE).getParentFile().getPath().replace('\\', '/');
//
//            String temp = JSONFinderMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
//            System.out.println(temp);
//            
//            resStreamOut = new FileOutputStream(jarFolder + resourceName);
//            while ((readBytes = stream.read(buffer)) > 0) {
//                resStreamOut.write(buffer, 0, readBytes);
//            }
//        } catch (Exception ex) {
//            throw ex;
//        } finally {
//            stream.close();
//            resStreamOut.close();
//        }
//
//        return jarFolder + resourceName;
//    }
//	
//	/**
//	 * This methods searches a String object for a matching regular expression. It returns 
//	 * the result if found, and an empty String "" if not.
//	 * 
//	 * @param searchString 		The String to search
//	 * @param regexPattern 		The String pattern to match
//	 * @return matchingString 	Returns a String that matches the regular expression. 
//	 * 							Returns an empty String (i.e. "") if nothing is found
//	 */
//	private static String extractMatchingString(String searchString, String regexPattern) {
//		// Stores the matching String
//		String matchingString = "";
//		// The pattern to match
//		Pattern pattern = Pattern.compile(regexPattern);		
//		// Stores the matching regular expression
//		Matcher matcher;		
//		
//		// Get the matching regular expression
//		matcher = pattern.matcher(searchString);
//		
//		// Check that we have a match
//		if (matcher.find()) {
//			// Save the matching String
//			matchingString = matcher.group(0);
//		}
//		else {
//			// Print an error message
//			System.out.println("WARNING: We do not have a matching String: " + searchString);
//		}
//		
//		// Return the matching String
//		return matchingString;
//		
//	} // END extractMatchingString
//	
//	
//	/**
//	 * This method waits for an element to appear, then clicks it
//	 * 
//	 * @param driver 			The Selenium Webdriver object
//	 * @param wait 				Object to control the max wait time before selenium webdriver performs an action
//	 * @param elementIdentifier The By object which identifies the web page element
//	 */
//	static void clickElement(WebDriver driver, WebDriverWait wait, By elementIdentifier) {
//		// Wait for the element to appear
//		wait.until(ExpectedConditions.presenceOfElementLocated(elementIdentifier));
//		// Click the element
//		driver.findElement(elementIdentifier).click();
//	}
//	
//	
//	/**
//	 * This method waits for an input field to appear, clicks it, then enters the provided text
//	 * 
//	 * @param driver			The Selenium Webdriver object
//	 * @param wait				Object to control the max wait time before selenium webdriver performs an action
//	 * @param elementIdentifier	The By object which identifies the web page element
//	 * @param textInput			The text to enter into the input field
//	 */
//	static void clickAndFillInputField(WebDriver driver, WebDriverWait wait, By elementIdentifier, String textInput) {
//		// Wait for the field to appear, click it, clear it and enter the inputText
//		wait.until(ExpectedConditions.presenceOfElementLocated(elementIdentifier));
//		driver.findElement(elementIdentifier).click();
//		driver.findElement(elementIdentifier).clear();
//		driver.findElement(elementIdentifier).sendKeys(textInput);
//	}
//	
//	
//	/**
//	 * This method creates the output file and writes the header information to the file
//	 * 
//	 * @param file	The file object
//	 * @param fw	The file writer
//	 * @param bw	BufferredWriter object that uses the fw object to write String's of text to the file
//	 */
//	static void createFileHeader(File file, FileWriter fw, BufferedWriter bw) {
//		
//		try {
//
//			// if file doesnt exists, then create it
//			if (!file.exists()) {
//				file.createNewFile();
//			}
//
//			// true = append file
//			fw = new FileWriter(file.getAbsoluteFile(), true);
//			bw = new BufferedWriter(fw);
//
//			bw.write("TEST - \r\n\r\n\r\n");
//			bw.write("RESULT - \r\n\r\n\r\n");
//			bw.write("ISSUES: \r\n\r\n\r\n");
//			bw.write("NODE.JS: \r\n");
//			
//
//			
//			for (String event : nodejsEvents) {
//				bw.write(event + "\r\n");
//			}
//			
//			
//			bw.write("\r\n\r\n\r\n");
//			bw.write("FIDDLER/DASHBOARD DATA: \r\n\r\n\r\n");
//
//			System.out.println("Header text has been added to the file");
//
//		} catch (IOException e) {
//
//			e.printStackTrace();
//
//		} finally {
//
//					try {
//
//						if (bw != null)
//							bw.close();
//
//						if (fw != null)
//							fw.close();
//
//					} catch (IOException ex) {
//						ex.printStackTrace();
//					} 
//		} // END finally
//	}
//	
//	
//	/**
//	 * This method check if the sessionId for the next Node.js event is different. If
//	 * so, it performs a new sessionId search, using the new sessionId
//	 * 
//	 * @param driver				Selenium WebDriver object
//	 * @param wait					WebDriverWait object
//	 * @param nextSessionId			The sessionId for the next Node.js event
//	 * @throws InterruptedException	To handle any exceptions caused by Thread.sleep(2000)
//	 */
//	static void hasSessionIdChanged(WebDriver driver, WebDriverWait wait, String nextSessionId) throws InterruptedException {
//		
//		// Check if the sessionId has changed
//		if (!nextSessionId.equals(currentSessionId)) {
//			
//			// Get the current sessionId
//			currentSessionId = extractMatchingString(nextSessionId, SESSION_ID_PATTERN);
//						
//			// Enter the sessionId into the search field
//			clickAndFillInputField(driver, wait, By.className("kuiLocalSearchInput"), "sessionId: " + currentSessionId);
//			
//			// Click the search button
//			clickElement(driver, wait, By.className("kuiLocalSearchButton"));
//			
//			// Wait for all disclosure triangles to appear
//			Thread.sleep(2000);	
//			
//			// Get the number of disclosure triangles on the page
//			disclosureTriangles = driver.findElements(By.xpath("//*/tr[@class='discover-table-row'][*]/td[1]/i[@class='fa discover-table-open-icon fa-caret-right']"));
//			System.out.println("disclosureTriangles: " + disclosureTriangles.size());
//			
//			// Wait until the first disclosure triangle appears
//			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*/tr[@class='discover-table-row'][1]/td[1]/i[@class='fa discover-table-open-icon fa-caret-right']")));
//			
////			// Wait for all other page elements to load. Otherwise the first disclosure triangle will close again when the last page element loads
////			Thread.sleep(2000);	
//			
//			// Expand all disclosure triangles on the web page
//			expandAllDisclosureTriangles(driver);
//			
//		} // END if
//
//	} // END hasSessionIdChanged()
//		
//
//	
//	/**
//	 * This method write the JSON data copied from the web page to the JSON log file
//	 * 
//	 * @param JSONString	The JSON String to write to the file
//	 * @param file			The file to write to
//	 * @param fw			The FileWriter object
//	 * @param bw			The BufferedWriter object
//	 * @param eventType		The type of Node.js event e.g. LOAD, BEGIN, etc.
//	 */
//	static void writeJSONDataToFile(String JSONString, File file, FileWriter fw, BufferedWriter bw, String eventType) {
//		
//		try {
//
//			// if file doesnt exists, then create it
//			if (!file.exists()) {
//				file.createNewFile();
//			}
//
//			// true = append file
//			fw = new FileWriter(file.getAbsoluteFile(), true);
//			bw = new BufferedWriter(fw);
//
//			// Write the current event type to the file
//			bw.write(eventType + ": \r\n");
//			
//			// Write the JSON String to the file
//			bw.write(JSONString);
//			bw.write("\r\n\r\n\r\n");
//
//			System.out.println("JSON Data has been added to " + JSON_LOG_FILE_NAME + "\r\n\r\n");
//
//		} catch (IOException e) {
//
//			e.printStackTrace();
//
//		} finally {
//
//					try {
//
//						if (bw != null)
//							bw.close();
//
//						if (fw != null)
//							fw.close();
//
//					} catch (IOException ex) {
//						ex.printStackTrace();
//					} 
//		} // END finally
//		
//		
//	} // END writeJSONDataToFile
//	
//	
//	/**
//	 * This method expands all disclosure triangles on the web page
//	 * 
//	 * @param driver	The Selenium Webdriver object
//	 */
//	private static void expandAllDisclosureTriangles(WebDriver driver) {
//		
//		// Open all of the disclosure triangles
//		for (int i = 1; i <= disclosureTriangles.size(); ++i) {
//			
//			try {				
//				
//				// Open all of the disclosure triangles
//				driver.findElement(By.xpath("//*/tr[@class='discover-table-row'][" + i + "]/td[1]/i[@class='fa discover-table-open-icon fa-caret-right']")).click();
//				
//			} catch (Exception e) {
//				
//				// Catches exceptions caused if a disclosure triangle cannot be found
//				
//			} // END catch
//			
//		} // END for
//	}
	
} // END JSONFinderController
