package com.oztamautomation.jsonfinder;
	
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.TextArea;


public class Main extends Application {
	
	//--CONSTANTS-----------------------------------------------------------------------------------------------------------------------------------------------/

	/** Regular expression for Timestmap */
	private static final String TIMESTAMP_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]";
	
	/** Regular expression for sessionId */
	private static final String SESSION_ID_PATTERN = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
	
	/** Regular expression for Event type */
	private static final String EVENT_PATTERN = "LOAD|BEGIN|PROGRESS|AD_BEGIN|AD_COMPLETE|COMPLETE";	
	
	/** Max wait time (in seconds) for a page element to load */
	private static final int WAIT_TIME = 120;

	/** The location and filename of the JSON Log text file to be created */
	private static final String JSON_LOG_FILE_NAME = System.getProperty("user.home") + "\\Desktop\\JSON Logs.txt";
	
	/** The JSON Finder file path - Note that the file extention has not been added to "JSON Finder" as the runnable jar will be converted to an exe file */
	private static final String JSON_FINDER_FILE = System.getProperty("user.home") + "\\Desktop\\JSON Finder";
	
	/** The Firefox driver file path */
	private static final String FIREFOX_DRIVER_FILE = System.getProperty("user.home") + "\\Desktop\\geckodriver.exe";
	
	
	// System.getProperty("user.home") + "\\Desktop\\JSON Finder.jar"
	
	
	//--VARIABLE DECLARATION------------------------------------------------------------------------------------------------------------------------------------/	

	/** Index for stepping down the JSON table */
	private static int jsonPropertyIndex;
	
	
	//--OBJECT DECLARATION-------------------------------------------------------------------------------------------------------------------------------------/
	
	/** The application window */
	private JFrame frmJsonFinder;
	
	/** The selenium webdriver base URL */
	private static String baseURL;
	
	/** The login username */
	private static String username;
	
	/** The login password */
	private static String password;
	
	/** The Selenium WebDriver object for Firefox */
	private static WebDriver driver;

	/** Controls the max wait time */
	private static WebDriverWait wait;

	/** Stores the Node.js input from the console */
	private static String nodejsString;
	
	/** Array that holds the individual Node.js event Strings */
	private static String[] nodejsEvents;

	/** Holds the current timestamp */
	private static String currentTimestamp;
	
	/** Holds the current event type */
	private static String currentEventType;

	/** The extracted timestamp String */
	private static String extractedTimestamp;

	/** The extracted event type String	 */
	private static String extractedEventType;
	
	/** Holds the current sessionId */
	private static String currentSessionId;
	
	/**  Allows the writing of streams of characters */
	private static FileWriter fw;
	
	/** Writes text to a character output stream */
	private static BufferedWriter bw;
		
	/** Create the file object */
	private static File file;
	
	/** Stores all the  disclosure triangles WebElement objects on the web page */
	private static List<WebElement> disclosureTriangles;
	
	/** Stores the JSON String copied from the operating system Clipboard */
	private static String jsonStringFromClipboard;
	
	
	
	//--GETTERS AND SETTERS-----------------------------------------------------------------------------------------------------------------------------------/
	
	
	public static WebDriver getDriver() {
		return driver;
	}

	public static void setDriver(WebDriver driver) {
		Main.driver = driver;
	}
	
	public static String getFirefoxDriverFile() {
		return FIREFOX_DRIVER_FILE;
	}
	
	public static int getWaitTime() {
		return WAIT_TIME;
	}
	
	public static String getBaseURL() {
		return baseURL;
	}
	public static void setBaseURL(String baseURL) {	
		Main.baseURL = baseURL;
	}
	
	public static String getUsername() {
		return username;
	}
	public static void setUsername(String username) {
		Main.username = username;
	}
	
	public static String getPassword() {
		return password;
	}
	public static void setPassword(String password) {
		Main.password = password;
	}
	
	public static File getFile() {
		return file;
	}
	public static void setFile(File file) {
		Main.file = file;
	}
	
	public static FileWriter getFw() {
		return fw;
	}

	public static void setFw(FileWriter fw) {
		Main.fw = fw;
	}

	public static BufferedWriter getBw() {
		return bw;
	}

	public static void setBw(BufferedWriter bw) {
		Main.bw = bw;
	}
	
	public static String getTimestampPattern() {
		return TIMESTAMP_PATTERN;
	}

	public static String getSessionIdPattern() {
		return SESSION_ID_PATTERN;
	}

	public static String getEventPattern() {
		return EVENT_PATTERN;
	}

	public static int getJsonPropertyIndex() {
		return jsonPropertyIndex;
	}
	public static void setJsonPropertyIndex(int jsonPropertyIndex) {
		Main.jsonPropertyIndex = jsonPropertyIndex;
	}
	
	public static String getNodejsString() {
		return nodejsString;
	}
	public static void setNodejsString(String nodejsString) {
		Main.nodejsString = nodejsString;
	}
	
	public static String[] getNodejsEvents() {
		return nodejsEvents;
	}
	public static void setNodejsEvents(String[] nodejsEvents) {
		Main.nodejsEvents = nodejsEvents;
	}
	
	public static List<WebElement> getDisclosureTriangles() {
		return disclosureTriangles;
	}

	public static void setDisclosureTriangles(List<WebElement> disclosureTriangles) {
		Main.disclosureTriangles = disclosureTriangles;
	}
	
	public static String getCurrentTimestamp() {
		return currentTimestamp;
	}
	public static void setCurrentTimestamp(String currentTimestamp) {
		Main.currentTimestamp = currentTimestamp;
	}
	
	public static String getCurrentEventType() {
		return currentEventType;
	}
	public static void setCurrentEventType(String currentEventType) {
		Main.currentEventType = currentEventType;
	}

	public static String getExtractedTimestamp() {
		return extractedTimestamp;
	}
	public static void setExtractedTimestamp(String extractedTimestamp) {
		Main.extractedTimestamp = extractedTimestamp;
	}
	
	public static String getExtractedEventType() {
		return extractedEventType;
	}
	public static void setExtractedEventType(String extractedEventType) {
		Main.extractedEventType = extractedEventType;
	}

	public static String getCurrentSessionId() {
		return currentSessionId;
	}
	public static void setCurrentSessionId(String currentSessionId) {
		Main.currentSessionId = currentSessionId;
	}
	
	public static String getJsonStringFromClipboard() {
		return jsonStringFromClipboard;
	}
	public static void setJsonStringFromClipboard(String jsonStringFromClipboard) {
		Main.jsonStringFromClipboard = jsonStringFromClipboard;
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
//			// Create the main anchor pane from the FXML file
//			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("JSONFinderView.fxml"));
			
			// Create a FXML file loader object from the JSONFinderView.fxml file
			FXMLLoader fxmlFileLoader = new FXMLLoader(Main.class.getResource("JSONFinderView.fxml"));
			
			// Create the main GUI anchor pane and load the FXML objects onto it
			AnchorPane anchorPane = fxmlFileLoader.load();

			// Create an instance of the Controller from the FXML controller
			JSONFinderController jsonFinderController = fxmlFileLoader.getController();
			jsonFinderController.setMain(this, driver, wait);
			
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
	
	
	//--MAIN METHOD---------------------------------------------------------------------------------------------------------------------------------------------/	
	
	/**
	 * This is the main method.
	 * 
	 * @param args Unused
	 * @return Nothing
	 * @throws Exception 
	 * @throws IOException When Selenium Webdriver cannot locate an element
	 * @throws InterruptedException To handle potential Thread.sleep() exceptions
	 */
	public static void main(String[] args) {
		launch(args);
		
		
	}
	
	//--CONSTRUCTOR-------------------------------------------------------------------------------------------------------------------------------------------/	
	
	/**
	 * This is the Constructor -  Creates an instance of the application
	 */
	public Main() {
		
		// INITIALISE VARIABLES
		setUsername("");
		setPassword("");
		setCurrentSessionId("");
		
		// INITIALISE OBJECTS
		bw = null;
		fw = null;
		file = new File(JSON_LOG_FILE_NAME);
				
//		// SET PROPERTIES		
//		System.setProperty("webdriver.gecko.driver", FIREFOX_DRIVER_FILE);
		
	}
	

	//--METHOD DECLARATION & DEFINITION------------------------------------------------------------------------------------------------------------------------/
	
	
//	/**
//	 * This method uses the Node.js input's timestamp and event type (e.g. LOAD, BEGIN, etc) to find the corresponding
//	 * JSON log in dashboard.oztam.com.au
//	 * 
//	 * @param event					For handling the button click event
//	 * @param textAreaNodejsInput	The TextArea when the Node.js data is entered
//	 * @throws InterruptedException	Exception handling when a thread is interrupted
//	 */
//	private static void createJSONLog(ActionEvent event, JTextArea textAreaNodejsInput) throws InterruptedException {
//						
//				// Get the Node.js input from the console
////				nodejsString = getNodejsInput();
//				setNodejsString(textAreaNodejsInput.getText());
//				
//				// Separate the Node.js events into individual Strings			
//				nodejsEvents = getNodejsString().split("\n");
//				
//				// Selenium WebDriver object for Firefox
//				driver = new FirefoxDriver();
//				
//				// Create the object that controls the max wait time
//				wait = new WebDriverWait(driver, WAIT_TIME);
//				
//				// Launch the web page
//				driver.get(getBaseURL());
//				
//				// Enter the username and password
//				clickAndFillInputField(driver, wait, By.id("username"), getUsername());
//				clickAndFillInputField(driver, wait, By.id("password"), getPassword());
//				
//				// Find and click the Login in button
//				driver.findElement(By.className("login")).click();
//				
//				// Wait for the time range button to appear and click it
//				wait.until(ExpectedConditions.presenceOfElementLocated(By.className("fa-clock-o")));
//				driver.findElement(By.className("fa-clock-o")).click();
//				
//				// Wait for the "Last 12 hours" option to appear, then click it
//				wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Last 12 hours")));
//				driver.findElement(By.linkText("Last 12 hours")).click();
//				
//				// Create the file and add the header text
//				createFileHeader(file, fw, bw);
//				
//				// Iterate through each individual node.js event
//				for (String singleNodejsEvent : nodejsEvents) {
//					
//					// If we have a new sessionId, perform another sessionId search for new JSON data
//					hasSessionIdChanged(driver, wait, extractMatchingString(singleNodejsEvent, SESSION_ID_PATTERN));
//					
//					// Get the time stamp from the single Node.js event and save it
//					setCurrentTimestamp(extractMatchingString(singleNodejsEvent, TIMESTAMP_PATTERN));
//					
//					// Get the event type from the single Node.js event and save it
//					setCurrentEventType(extractMatchingString(singleNodejsEvent, EVENT_PATTERN));	
//					
//					// TROUBLESHOOTING CODE:
//					System.out.println("Getting JSON Log for SessionId: " + getCurrentSessionId() + ", Timestamp: " + getCurrentTimestamp() + ", AND Event Type: " + getCurrentEventType());
//					
//					// On each Table tab, loop through each timestamp and event type
//					for (int uniqueXpathNumber = 1; uniqueXpathNumber <= disclosureTriangles.size(); ++uniqueXpathNumber) {
//						
//							try {
//
//							// Index used to step through JSON property values, listed on the table tab. The timestamp index usually 
//							// starts at [23] and increases (e.g. /tr[23]/td[3]/div[@class='doc-viewer-value']/span). Hence,
//							// the assigned value below
//							setJsonPropertyIndex(23);
//							
//							// For storing the clicked JSON property value on table tab
//							String clickedText = "";
//							
//							// Starting at row [23], keep looking down the table for the timestamp text, until the timestamp is found
//							do {
//								
//								// Get the 'timestamp' text starting at row [23]
//								clickedText = driver.findElement(By.xpath("//*/tr[" 
//										+ (uniqueXpathNumber*2) + "]/td/doc-viewer/div[@class='doc-viewer']"
//										+ "/div[@class='doc-viewer-content']/render-directive/table[@class='table table-condensed']/tbody/tr[" 
//										+ jsonPropertyIndex + "]/td[3]/div[@class='doc-viewer-value']/span")).getText();
//								
//								// Extract the clicked text and save it
//								setExtractedTimestamp(extractMatchingString(clickedText, TIMESTAMP_PATTERN));
//								
//								// Increment the JSON property index for the next loop
//								setJsonPropertyIndex(getJsonPropertyIndex() + 1);
//								
//							} while (!Pattern.matches(TIMESTAMP_PATTERN, getExtractedTimestamp()));
//
//							
//							// Get the entire 'events' text from the JSON table and save it
//							setExtractedEventType(driver.findElement(By.xpath("//*/tr[" 
//									+ (uniqueXpathNumber*2) + "]/td/doc-viewer/div[@class='doc-viewer']/div[@class='doc-viewer-content']" 
//									+ "/render-directive/table[@class='table table-condensed']/tbody/tr[8]/td[3]/div[@class='doc-viewer-value']/span")).getText());
//							
//							// Extract the event type from the entire 'event' text and save it
//							setExtractedEventType(extractMatchingString(getExtractedEventType(), EVENT_PATTERN));
//							
//							// Check we have the right JSON data
//							if (Objects.equals(getExtractedTimestamp(), getCurrentTimestamp()) && Objects.equals(getExtractedEventType(), getCurrentEventType())) {
//								
//								// The extracted timestamp and event type match the Node.js timestamp and event type
//								
//								// TROUBLESHOOTING CODE
//								System.out.println("Extracted Timestamp: " + getExtractedTimestamp() + " AND Extracted Event Type: " + getExtractedEventType());
//								System.out.println("The Extracted Timestamp MATCHES the currentTimestamp !!!!!!!!!!!!!!!");
//								
//								// Open the corresponding JSON tab
//								clickElement(driver, wait, By.xpath("//*/tr[" + (uniqueXpathNumber*2) + "]/td/doc-viewer/div[@class='doc-viewer']/ul[@class='nav nav-tabs']/li[2]/a"));
//								
//								// Wait for the JSON text to appear
//								wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body[@id='kibana-body']/div[@class='content']/div[@class='app-wrapper']/div[@class='app-wrapper-panel']" 
//										+ "/div[@class='application tab-discover']/discover-app[@class='app-container']/div[@class='container-fluid']/div[@class='row'][2]/div[@class='discover-wrapper col-md-10']"
//										+ "/div[@class='discover-content']/div[@class='results']/div[@class='discover-table']/doc-table/div[@class='doc-table-container']/table[@class='kbn-table table']/tbody/tr[" 
//										+ (uniqueXpathNumber*2) 
//										+ "]/td/doc-viewer/div[@class='doc-viewer']/div[@class='doc-viewer-content']/render-directive/div[@id='json-ace']/div[@class='ace_scroller']/div[@class='ace_content']")));
//								
//								// Get the JSON text using the uniqueXpathNumber which is incremented by 2 as you move down each disclosure triangle
//								jsonStringFromClipboard = driver.findElement(By.xpath("/html/body[@id='kibana-body']/div[@class='content']/div[@class='app-wrapper']/div[@class='app-wrapper-panel']" 
//										+ "/div[@class='application tab-discover']/discover-app[@class='app-container']/div[@class='container-fluid']/div[@class='row'][2]/div[@class='discover-wrapper col-md-10']"
//										+ "/div[@class='discover-content']/div[@class='results']/div[@class='discover-table']/doc-table/div[@class='doc-table-container']/table[@class='kbn-table table']/tbody/tr[" 
//										+ (uniqueXpathNumber*2) 
//										+ "]/td/doc-viewer/div[@class='doc-viewer']/div[@class='doc-viewer-content']/render-directive/div[@id='json-ace']/div[@class='ace_scroller']/div[@class='ace_content']")).getText();
//								
//								
//								// Add the JSON text to the "JSON Logs.txt" tile
//								writeJSONDataToFile(jsonStringFromClipboard, file, fw, bw, getCurrentEventType());
//								
//								// We have a match. No need to iterate through the remaining disclosure triangles, exit the for loop
//								break;
//								
//							} // END if
//							
//						} catch (Exception e) {
//							// Catches exceptions caused if a disclosure triangle cannot be found
//							
//							System.out.println("Exception caught....\r\n" + e.toString());
//							
//						} // END catch
//						
//					} // END for
//					
//				} // END for
//				
//				// Kill the geckodriver.exe and quite Firefox
//				driver.quit();
//					
//	} // END createJSONLog

	
	
} // END CLASS
