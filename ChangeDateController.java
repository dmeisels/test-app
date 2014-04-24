package com.dmm.calendar;

import java.util.Calendar;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChangeDateController {

	@FXML private TextField year;
	@FXML private ChoiceBox<String> month;
	
	private Stage dialogStage;
	public Calendar getCalendar() {return calendar;}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
		year.setText(Integer.toString(calendar.get(Calendar.YEAR)));
		month.getSelectionModel().select((calendar.get(Calendar.MONTH)));
	}

	private Calendar calendar;
	private boolean okClicked = false;
	
	private final String[] monthList = {"January", "February","March","April","May","June","July","August","September","October","November","December"};
	
	/**
	  * Initializes the controller class. This method is automatically called
	  * after the fxml file has been loaded.
	  */
	  @FXML
	  private void initialize() {
			month.getItems().clear();
			month.getItems().addAll(monthList);
			
			month.getSelectionModel().selectedIndexProperty().addListener(
				new ChangeListener<Number>() {
					public void changed(ObservableValue<? extends Number> ov,
							Number oldValue, Number newValue) 
					{
						calendar.set(Calendar.MONTH, newValue.intValue());
				    }
				});
			
			year.textProperty().addListener(new ChangeListener<String>() {
	            @Override 
	            public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
	            	int y=calendar.get(Calendar.YEAR);

	            	if (newValue.length()==0) 
	            		return;
	            	
	                try {
	                	y= Integer.parseInt(newValue);
	                } catch (Exception e) {
	                	year.setText(oldValue);
	                	return;
	                }
	                
	            	if (newValue.length()>4) {
	                	year.setText(oldValue);
	                	return;
	            	}
	            	
	            	if (newValue.length()==4) {
	                    if (y<calendar.getMinimum(Calendar.YEAR) || y>calendar.getMaximum(Calendar.YEAR)) {
	                    	year.setText(oldValue);
	                    	return;
	                    }
	                    
		   				calendar.set(Calendar.YEAR, y);
	            	}
	            }
	        });
	  }
	  
	  /**
	  * Sets the stage of this dialog.
	  * @param dialogStage
	  */
	  public void setDialogStage(Stage dialogStage) {
	      this.dialogStage = dialogStage;
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
           okClicked = true;
           dialogStage.close();
	   }
	   
	   /**
	   * Called when the user clicks cancel.
	   */
	   @FXML
	   private void handleCancel() {
	       dialogStage.close();
	   }
	  	  
}
