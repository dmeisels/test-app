package com.dmm.calendar;
	
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialogs;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.dmm.calendar.model.CalendarEvent;
import com.dmm.calendar.model.EventList;


/**
 * Hebrew Calendar version 2.0
 * @author David Meisels Feb. 2014
 *
 */

public class Main extends Application {
	private final static int COLUMN_NUMBER = 7;
	private final static int ROW_NUMBER = 6;
	public final static int CAL_TYPE_GREGOREAN = 0;
	public final static int CAL_TYPE_HEBREW = 1;
	
	private int calendarType=0;
	private Stage primaryStage;
	public Stage getPrimaryStage() {return primaryStage;}
	public void setPrimaryStage(Stage primaryStage) {this.primaryStage = primaryStage;}

	public int getCalendarType() {	return calendarType;}
	public void setCalendarType(int calendarType) {this.calendarType = calendarType;}

	/**
	  * The data as an observable list of Events.
	  */
	private ObservableList<CalendarEvent> events = FXCollections.observableArrayList();
	public void setEventData(ObservableList<CalendarEvent> events) {this.events = events;}
	public ObservableList<CalendarEvent> getEventData() {return events;}

	private Calendar today;
	public Calendar getToday() {return today;}
	public void setToday(Calendar today) { this.today=today;}
	
	private CalendarController calendarController;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage=primaryStage;
		today = Calendar.getInstance();

		  // Try to load last opened event file
		File file = getEventFilePath();
		if (file != null) {
			loadEventDataFromFile(file);
		} else {
			CalendarEvent ce = new CalendarEvent(today, "Add events");
			events.add(ce);
		}
		
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/Calendar.fxml"));     
			Parent root = (Parent)fxmlLoader.load();
			
			primaryStage.getIcons().add(new Image("file:resources/images/Calendar-icon.png"));
			
			// Show the days in a ROW_NUMBER by COLUMN_NUMBER matrix.
			// The matrix constitutes of DayCell class which extends Label class and holds date information.
/*			
			dayCells = new DayCell[COLUMN_NUMBER * ROW_NUMBER];
			int index = 0;
			for (int i = 0; i < ROW_NUMBER; i++) {
				HBox row = new HBox();
				for (int j = 0; j < COLUMN_NUMBER; j++) {
					DayCell cell = createCell(0, 0, 0);
					row.getChildren().add(cell);
					cell.setId("day-cell");
					dayCells[index++] = cell; 
				}
				root.getChildren().add(row);
			}
*/			
			// Give the controller access to the main app
			calendarController = fxmlLoader.getController();
			calendarController.setMainApp(this);
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("view/calendar.css").toExternalForm());

	        primaryStage.setTitle("Calendar");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the current event data to the specified file.
	 * 
	 * @param file
	 */
	public void saveEventDataToFile(File file) {

		 // create JAXB context and initializing Marshaller
		
	  // Convert ObservableList to a normal ArrayList
	  EventList eventList = new EventList(new ArrayList<CalendarEvent>(events));

	  try {
		// Writing to XML file
		  JAXBContext jaxbContext = JAXBContext.newInstance(EventList.class);
		  Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		  jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		  jaxbMarshaller.marshal(eventList, file);
		  jaxbMarshaller.marshal(eventList, System.out);

		  setEventFilePath(file);
	  } catch (Exception e) { // catches ANY exception
	    Dialogs.showErrorDialog(primaryStage,
	        "Could not save data to file:\n" + file.getPath(), "Could not save data", "Error", e);
	  }
	}
	
	public boolean loadEventDataFromFile(File file) {
		
		// create JAXB context and initializing Marshaller
		
		  try {
			  JAXBContext jaxbContext = JAXBContext.newInstance(EventList.class);
			  Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			  
			  EventList eventList = (EventList) jaxbUnmarshaller.unmarshal(file);
			  
			  events.clear();
			  events.addAll(eventList.getEventList());

			  setEventFilePath(file);
			  return true;
		  } catch (Exception e) { // catches ANY exception
		    Dialogs.showErrorDialog(primaryStage,
		        "Could not load data from file:\n" + file.getPath(), "Could not load data", "Error", e);
		    return false;
		  }
	}
	
	/**
	 * Returns the person file preference, i.e. the file that was last opened.
	 * The preference is read from the OS specific registry. If no such
	 * preference can be found, null is returned.
	 * 
	 * @return
	 */
	public File getEventFilePath() {
		Preferences prefs = Preferences.userNodeForPackage(Main.class);
		String filePath = prefs.get("filePath", null);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}
	
	
	/**
	 * Sets the file path of the currently loaded file.
	 * The path is persisted in the OS specific registry.
	 * 
	 * @param file the file or null to remove the path
	 */
	public void setEventFilePath(File file) {
		Preferences prefs = Preferences.userNodeForPackage(Main.class);
		if (file != null) {
			prefs.put("filePath", file.getPath());
			
			// Update the stage title
			primaryStage.setTitle("Calendar - " + file.getName());
		} else {
			prefs.remove("filePath");
			
			// Update the stage title
			primaryStage.setTitle("Calendar");
		}
	}
	
	public void saveEvents() {
		File file = getEventFilePath();
		if (file == null) {
			FileChooser fileChooser = new FileChooser();
			// Set extension filter
			FileChooser.ExtensionFilter extFilter = 
					new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
			fileChooser.getExtensionFilters().add(extFilter);

			// Show save file dialog
			
			file = fileChooser.showOpenDialog(primaryStage);
		}
		
		if (file != null) {
			saveEventDataToFile(file);
		}

	}
	
	/**
	 * Opens a dialog to edit details for the specified event. If the user
	 * clicks OK, the changes are saved into the provided event object and
	 * true is returned.
	 * 
	 * @param event the event object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 */
	public boolean showEventEditDialog(CalendarEvent event) {
	  try {
	    // Load the fxml file and create a new stage for the popup
	    FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/EventEditDialog.fxml"));
	    AnchorPane page = (AnchorPane) loader.load();
	    Stage dialogStage = new Stage();
	    dialogStage.setTitle("Edit Event");
	    dialogStage.initModality(Modality.WINDOW_MODAL);
	    dialogStage.initOwner(primaryStage);
	    Scene scene = new Scene(page);
	    dialogStage.setScene(scene);

	    // Set the event into the controller
	    EventEditDialogController controller = loader.getController();
	    controller.setDialogStage(dialogStage);
	    controller.setEvent(event);

	    // Show the dialog and wait until the user closes it
	    dialogStage.showAndWait();

	    return controller.isOkClicked();

	  } catch (IOException e) {
	    // Exception gets thrown if the fxml file could not be loaded
	    e.printStackTrace();
	    return false;
	  }
	}	
	
	
	/**
	 * Sets the file path of the currently loaded file.
	 * The path is persisted in the OS specific registry.
	 * 
	 * @param file the file or null to remove the path
	 */
	public void setPreferences() {
		Preferences prefs = Preferences.userNodeForPackage(Main.class);
		prefs.put("filePath", "hcal");
	}
	
	/**
	 * Opens a dialog to change the current month and year.
	 * 
	 * @param calendar, the current working date
	 * @return true if the user clicked OK, false otherwise.
	 */
	public boolean showChangeDateDialog(Calendar calendar) {
	  try {
	    // Load the fxml file and create a new stage for the popup
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("view/ChangeDateDialog.fxml"));
	    AnchorPane page = (AnchorPane) loader.load();
	    Stage dialogStage = new Stage();
	    dialogStage.setTitle("Change Date");
	    dialogStage.initModality(Modality.WINDOW_MODAL);
	    dialogStage.initOwner(primaryStage);
	    Scene scene = new Scene(page);
	    dialogStage.setScene(scene);

	    // Set the person into the controller
	    ChangeDateController controller = loader.getController();
	    controller.setDialogStage(dialogStage);
	    controller.setCalendar(calendar);

	    // Show the dialog and wait until the user closes it
	    dialogStage.showAndWait();

	    return controller.isOkClicked();

	  } catch (IOException e) {
	    // Exception gets thrown if the fxml file could not be loaded
	    e.printStackTrace();
	    return false;
	  }
	}	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
}
