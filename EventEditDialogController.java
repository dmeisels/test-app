package com.dmm.calendar;

import java.util.Calendar;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishDate;

import com.dmm.calendar.model.CalendarEvent;


/**
 * Dialog to edit details of a person.
 * 
 * @author Marco Jakob
 */
public class EventEditDialogController {

	@FXML	private TextField descriptionField;
	@FXML	private TextField yearField;
	@FXML	private ChoiceBox<String> monthField;
	@FXML	private ChoiceBox<String> dayField;
	@FXML	private TextField hYearField;
	@FXML	private ChoiceBox<String> hMonthField;
	@FXML	private ChoiceBox<String> hDayField;
	@FXML	private TextField typeField;
	@FXML	private ChoiceBox<String> recurrenceField;
	@FXML   private ToggleGroup typeToggleGroup;
	@FXML	private RadioButton btnTypeEnglish;
	@FXML	private RadioButton btnTypeHebrew;
	@FXML	private GridPane gridPane;
	
	
	private Stage dialogStage;
	private CalendarEvent event;
	private boolean okClicked = false;
	private final String[] monthList = {"January", "February","March","April","May","June","July","August","September","October","November","December"};
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		
		recurrenceField.getSelectionModel().select(0);
		
		yearField.textProperty().addListener(new ChangeListener<String>() {
            @Override 
            public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
            	if (newValue.length()==0) 
            		return;
            	
            	if (newValue.length()!=4) {
                	yearField.setText(oldValue);
                	return;
            	}
            	
                try {
                	Integer.parseInt(newValue);
                } catch (Exception e) {
                	yearField.setText(oldValue);
                	return;
                }
            }
        });
		monthField.getItems().clear();
		monthField.getItems().addAll(monthList);
		dayField.getItems().clear();
		for (int i=0; i<32; i++)
			dayField.getItems().add(String.format("%02d", i));

		hYearField.textProperty().addListener(new ChangeListener<String>() {
            @Override 
            public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {

            	if (newValue.length()==0) 
            		return;
            	
            	if (newValue.length()!=4) {
                	hYearField.setText(oldValue);
                	return;
            	}
            	
                try {
                	Integer.parseInt(newValue);
                } catch (Exception e) {
                	hYearField.setText(oldValue);
                	return;
                }
            }
        });
		
		hMonthField.getItems().clear();
		JewishDate jDate = new JewishDate();
		HebrewDateFormatter hdf = new HebrewDateFormatter();
		hdf.setHebrewFormat(false);
		for (int i=1; i<14; i++) {
			jDate.setJewishMonth(i);
			hMonthField.getItems().add(hdf.formatMonth(jDate));
		}
		hDayField.getItems().clear();
		for (int i=1; i<31; i++)
			hDayField.getItems().add(Integer.toString(i));

	}
	
	/**
	 * Sets the stage of this dialog.
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	/**
	 * Sets the person to be edited in the dialog.
	 * 
	 * @param person
	 */
	public void setEvent(CalendarEvent event) {
		this.event = event;
		
		if (event.getEventType()==CalendarEvent.ENGLISH_EVENT )
			btnTypeEnglish.setSelected(true);
		else
			btnTypeHebrew.setSelected(true);
		
		yearField.setText(Integer.toString(event.getDate().get(Calendar.YEAR)));
		monthField.getSelectionModel().select(event.getDate().get(Calendar.MONTH));
		int dom = event.getDate().get(Calendar.DAY_OF_MONTH);
		dayField.getSelectionModel().select(dom);
		System.out.println("Event="+event.getDisplayDate()+" dom="+dom+" index="+dayField.getSelectionModel().getSelectedIndex()+" value="+dayField.getSelectionModel().getSelectedItem());
		
		hYearField.setText(Integer.toString(event.getJewishDate().getJewishYear()));
		hMonthField.getSelectionModel().select(event.getJewishDate().getJewishMonth());
		hDayField.getSelectionModel().select(event.getJewishDate().getJewishDayOfMonth());
		
		descriptionField.setText(event.getDescription());
		recurrenceField.getSelectionModel().select(event.getRecurrence());
	}
	
	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}
	
	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			
			event.setDescription(descriptionField.getText());
			event.setEventType(btnTypeEnglish.isSelected() ? 
					CalendarEvent.ENGLISH_EVENT : CalendarEvent.JEWISH_EVENT );
			event.setRecurrence(recurrenceField.getSelectionModel().getSelectedIndex());
			
			if (btnTypeEnglish.isSelected()) {
				Calendar c = Calendar.getInstance();
			    c.set(Calendar.YEAR, Integer.parseInt(yearField.getText()));
			    c.set(Calendar.MONTH, monthField.getSelectionModel().getSelectedIndex());
			    c.set(Calendar.DAY_OF_MONTH, dayField.getSelectionModel().getSelectedIndex());
			    event.setDate(c);
			    event.setJewishDate(c.getTime());
			} else {
			    int hDate = hDayField.getSelectionModel().getSelectedIndex();
			    int hMonth = hMonthField.getSelectionModel().getSelectedIndex();
			    int hYear = Integer.parseInt(hYearField.getText());
			    
			    event.getJewishDate().setJewishDate(hYear, hMonth, hDate);
			    event.setDate(event.getJewishDate().getTime());
			}
			
			System.out.println("Event date is "+event.getDisplayDate()+" "+event.getJewishDisplayDate());
			okClicked = true;
			dialogStage.close();
		}
	}
	
	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
	
	/**
	 * Validates the user input in the text fields.
	 * 
	 * @return true if the input is valid
	 */
	private boolean isInputValid() {
		String errorMessage = "";

		if (descriptionField.getText() == null || descriptionField.getText().length() == 0) {
			errorMessage += "No valid description!\n"; 
		}
		
		if (errorMessage.length() == 0) {
			return true;
		} else {
			// Show the error message
			Dialogs.showErrorDialog(dialogStage, errorMessage,
					"Please correct invalid fields", "Invalid Fields");
			return false;
		}
	}
	
}
