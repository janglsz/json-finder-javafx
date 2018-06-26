/**
 * <h1>JSON Finder</h1>
 * JSON Finder is a program that accepts a String of Node.js events, uses Selenium WebDriver to extract the related JSON data 
 * from https://dashboard.oztam.com.au/ and then saves the JSON data to a text file on the desktop
 * 
 * @author Kevin Jang
 * @version 2.0
 * @since 2018-06-26
 */
package com.oztamautomation.jsonfinder;
	
// IMPORTS
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;



public class Main extends Application {
	
	//--CONSTANTS-----------------------------------------------------------------------------------------------------------------------------------------------/

	/** Regular expression for Timestmap */
	private static final String TIMESTAMP_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]";
	
	/** Regular expression for sessionId */
	private static final String SESSION_ID_PATTERN = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
	
	/** Regular expression for Event type */
	private static final String EVENT_PATTERN = "LOAD|BEGIN|PROGRESS|AD_BEGIN|AD_COMPLETE|COMPLETE";	
	
	/** Max wait time (in seconds) for a page element to load */
	private static final int WAIT_TIME = 5;

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
	private static String extractedJsonString;
	
	
	
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
	
	public static String getJsonLogFileName() {
		return JSON_LOG_FILE_NAME;
	}
	
	public static String getJsonFinderFile() {
		return JSON_FINDER_FILE;
	}
	
	public static String getExtractedJsonString() {
		return extractedJsonString;
	}
	public static void setExtractedJsonString(String extractedJsonString) {
		Main.extractedJsonString = extractedJsonString;
	}
	
	//----------------------------------------------------------------------------------------------------------------------------------------------------------/	
	
	/**
	 * This method creates the Stage and initiates the JSONFinderController
	 */
	@Override
	public void start(Stage primaryStage) {
		
		try {			
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
			
			primaryStage.setTitle("JSON Finder" );
			
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
	 * <h1>JSON Finder</h1>
	 * JSON Finder is a program that accepts a String of Node.js events, extracts the related Node.js data 
	 * from https://dashboard.oztam.com.au/ and then saves the JSON data to a text file.
	 * 
	 * @author Kevin Jang
	 * @version 1.0
	 * @since 2018-04-30
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
		
	} // END main
	
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
		
	} // END Constructor 
	
	
	/**
	 * This method is run when the JavaFX Stage is closed
	 */
	@Override 
	public void stop() {
		System.out.println("Stage is closing");
	}
	
	
} // END Class Main
