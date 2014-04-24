package com.dmm.calendar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Dialogs.DialogResponse;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;

import com.dmm.calendar.model.CalendarEvent;
import com.dmm.calendar.model.Zman;
import com.dmm.calendar.model.Zmanim;

public class CalendarController {
	private final static String DATEPICKER_OTHERMONTH = "othermonth-cell";
	private final static String DATEPICKER_TODAY = "today-cell";
	private final static String DATEPICKER_MONTH = "month-cell";
	private String[] cellStyleList = {DATEPICKER_OTHERMONTH, DATEPICKER_TODAY, DATEPICKER_MONTH};
	
	private final static int FILTER_NONE=0;
	private final static int FILTER_MONTH=1;

	@FXML private Label monthYear;
	@FXML private GridPane grid;
	@FXML private TableView<Zman> zmanimTable;
	@FXML private TableColumn<Zman, String> zmanColumn;
	@FXML private TableColumn<Zman, String> descColumn;
	
	@FXML private TableView<CalendarEvent> eventTable;
	@FXML private TableColumn<CalendarEvent, String> dateColumn;
	@FXML private TableColumn<CalendarEvent, String> jDateColumn;
	@FXML private TableColumn<CalendarEvent, String> eventColumn;
	
	@FXML private CheckMenuItem showAll;
	
	
	// Observable list of zmanim for a given date
	private ObservableList<CalendarEvent> eventsFilter = FXCollections.observableArrayList();
	private int filter=0;
	
	// Reference to the main application
	private Main mainApp;
	private List<VBox> dayCells = new ArrayList<VBox>();
	private Calendar currDate;
	private JewishCalendar hayom;
	private HebrewDateFormatter hdf = new HebrewDateFormatter();
	
	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public CalendarController() {}


	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	public void initialize() {
		
		for (int row=2; row<8; row++) {
			for (int col=0; col<7; col++) {
				VBox cell = new VBox();
				cell.setId("day-cell");
				cell.setFillWidth(true);
				cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent me) {
				    	VBox vb = (VBox)me.getSource();
//						Integer row = GridPane.getRowIndex(vb);
//						Integer col = GridPane.getColumnIndex(vb);
//				    	System.out.println("Mouse pressed on row "+row+" column "+col+" Id: "+vb.getId());
//				    	System.out.println("Styles: "+vb.getStyleClass());
//				    	System.out.println("User data: "+vb.getUserData());
				    	zmanimTable.getItems().clear();
				    	zmanimTable.getItems().addAll(getZmanim((String)vb.getUserData()));
					}
				});
				dayCells.add(cell);
				grid.add(cell, col, row);
			}
		}
		
		// Auto resize columns
		zmanimTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//		eventTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		eventTable.setItems(eventsFilter);
		
		zmanColumn.setCellValueFactory(new PropertyValueFactory<Zman, String>("zman"));
		descColumn.setCellValueFactory(new PropertyValueFactory<Zman, String>("desc"));
		dateColumn.setCellValueFactory(new PropertyValueFactory<CalendarEvent, String>("displayDate"));
		jDateColumn.setCellValueFactory(new PropertyValueFactory<CalendarEvent, String>("jewishDate"));
		
		eventColumn.setCellValueFactory(new PropertyValueFactory<CalendarEvent, String>("description"));
		
		final Tooltip tooltip = new Tooltip();
		tooltip.setText("Click here to change to month/year");
		monthYear.setTooltip(tooltip);
		
		hdf.setHebrewFormat(true);
	}	

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
		currDate = mainApp.getToday();
		currDate.set(Calendar.DAY_OF_MONTH, 1);
		hayom = new JewishCalendar(currDate);
		setMonthYear();
		setDayCells();
		setEvents();
	}
	
	private void setMonthYear() {
		String s=(new SimpleDateFormat("MMMM, YYYY")).format(currDate.getTime());
		String t=reverseWords(hdf.formatHebrewNumber(hayom.getJewishYear()))+" "+
				reverseWords(hdf.formatMonth(hayom));
		
		monthYear.setText(s+"\n"+t);	
	}
	
	private void setDayCells() {
		int month=currDate.get(Calendar.MONTH);
		int year=currDate.get(Calendar.YEAR);
		
    	zmanimTable.getItems().clear();
    	zmanimTable.getItems().addAll(getZmanim(String.format("%tF", currDate)));
    	
		while(currDate.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY) {
			currDate.add(Calendar.DAY_OF_MONTH, -1);
			hayom.back();
		}
		
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j<7; j++) {
				int c = (i*7) +j;
				VBox cell = dayCells.get(c);
				cell.getChildren().clear();
				cell.getChildren().addAll(buildDayCell(currDate, hayom));
				cell.setUserData(String.format("%tF", currDate));
				cell.getStyleClass().removeAll(cellStyleList);
				if (isToday(currDate)) {
					cell.getStyleClass().add(DATEPICKER_TODAY);
			    	zmanimTable.getItems().clear();
			    	zmanimTable.getItems().addAll(getZmanim(String.format("%tF", currDate)));
				}
				else if (currDate.get(Calendar.MONTH)==month) 
					cell.getStyleClass().add(DATEPICKER_MONTH);
				else
					cell.getStyleClass().add(DATEPICKER_OTHERMONTH);
				currDate.add(Calendar.DAY_OF_MONTH, 1);
				hayom.forward();
			}
		}
		
		currDate.set(Calendar.YEAR, year);
		currDate.set(Calendar.MONTH, month);
		currDate.set(Calendar.DAY_OF_MONTH, 1);
//		
		
	}
	
	private List<Node> buildDayCell(Calendar date, JewishCalendar hdate) {
		List<Node> nodes = new ArrayList<Node>();
		Zmanim z = new Zmanim(date);
		String t = reverseWords(hdf.formatMonth(hdate))+" "+
				reverseWords(hdf.formatHebrewNumber(hdate.getJewishDayOfMonth()));
		
/*
 *   First row contains two Labels within HBoxes
 *   one on the left, and one on the right		
 */
		HBox hb1 = new HBox();
		Label leftSide = new Label(String.valueOf(date.get(Calendar.DAY_OF_MONTH)));
		hb1.getChildren().addAll(leftSide);
		nodes.add(hb1);

		HBox hb2 = new HBox();
		Label lbl2 = new Label();
		switch (date.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.FRIDAY:
			lbl2.setText("Candles "+z.getCandlesAsString());
			lbl2.setTextFill(Color.RED);
			break;
		case Calendar.SATURDAY:
			lbl2.setText(reverseWords(z.getParsha()));
			lbl2.setAlignment(Pos.CENTER_RIGHT);
			hb2.setAlignment(Pos.CENTER_RIGHT);
			break;
		default:
			lbl2.setText(" ");
			break;
		}
		hb2.getChildren().add(lbl2);
		nodes.add(hb2);
		
		HBox hb3 = new HBox();
		Label lbl3 = new Label();
		if (hdate.isYomTov() || hdate.isTaanis()) {
			lbl3.setText(reverseWords(hdf.formatYomTov(hdate)));
			lbl3.setTextFill(Color.RED);
			lbl3.setAlignment(Pos.CENTER_RIGHT);
			hb3.setAlignment(Pos.CENTER_RIGHT);
		} else if (hdate.isErevYomTov()) {
			lbl3.setText("Candles "+z.getCandlesAsString());
		} else {
			lbl3.setText(" ");
		}
		hb3.getChildren().add(lbl3);
		nodes.add(hb3);
		
		HBox hb4 = new HBox();
		Label lbl4 = new Label();
		lbl4.setText(t);
		lbl4.setAlignment(Pos.CENTER_RIGHT);
		hb4.setAlignment(Pos.CENTER_RIGHT);
		
		hb4.getChildren().add(lbl4);
		nodes.add(hb4);

		return nodes;
	}
	
	/**
	 * @param calendar is to be compared 
	 * @return true if the compared calendar is today
	 */
	private boolean isToday(Calendar calendar){
		Calendar today = Calendar.getInstance();
		return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && 
				today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
				today.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	private String reverseWords(String sentence) {
		StringBuilder sb = new StringBuilder(sentence);
		return sb.reverse().toString();
	}
	
	/**
	 * @param calendar  
	 * @return list of events for this month
	 */
	private void setEvents() {
		System.out.println("Loading Events...");
		eventsFilter.clear();
		eventsFilter.addAll(mainApp.getEventData());

	      // Listen for changes in master data.
	      // Whenever the master data changes we must also update the filtered data.
		mainApp.getEventData().addListener(new ListChangeListener<CalendarEvent>() {
	          @Override
	          public void onChanged(ListChangeListener.Change<? extends CalendarEvent> change) {
	              updateFilteredData();
	          }
	      });
	  		
		return;
	}
	
	private void updateFilteredData() {
		eventsFilter.clear();
          
		for (CalendarEvent event : mainApp.getEventData()) {
	          if (matchesFilter(event)) {
	        	  eventsFilter.add(event);
	          }
	      }
	      
	      // Must re-sort table after items changed
	      reapplyTableSortOrder();
		
	}

	  /**
	  * Returns true if the Event matches the current filter.
	  * 
	  * @param CalendarEvent
	  * @return
	  */
	  private boolean matchesFilter(CalendarEvent event) {
	      if (filter == CalendarController.FILTER_NONE) {
	          // No filter --> Add all.
	          return true;
	      }
	      
		if (event.isYearlyEvent()) {
			if (event.isJewishEvent()) {
				if (event.getJewishDate().getJewishMonth() == hayom.getJewishMonth())
					return true;
			} else {
				if (event.getDate().get(Calendar.MONTH) == currDate.get(Calendar.MONTH))
					return true;
			}
		}
	      
	      return false; // Does not match
	  }
	  
	  private void reapplyTableSortOrder() {
	      ArrayList<TableColumn<CalendarEvent, ?>> sortOrder = new ArrayList<>(eventTable.getSortOrder());
	      eventTable.getSortOrder().clear();
	      eventTable.getSortOrder().addAll(sortOrder);
	  }
	  
	/**
	 * @param calendar  
	 * @return list of zmanim for this date
	 */
	private List<Zman> getZmanim(String date) {
		Calendar cal = Calendar.getInstance();
		try {
			Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
			cal.setTime(d);
		} catch (Exception e) {return null;}
		
		// Get Zmanim for today
		List<Zman> l = new ArrayList<Zman>();
		Zmanim z = new Zmanim(cal);
		JewishCalendar jcal = z.getJcal();
		l.add(new Zman(String.format("%tl:%<tM", z.getDawn()), reverseWords(Zmanim.dawnTitle)));
		l.add(new Zman(String.format("%tl:%<tM", z.getSunrise()), reverseWords(Zmanim.sunriseTitle)));
		l.add(new Zman(String.format("%tl:%<tM", z.getLatestShema()), reverseWords(Zmanim.latestShemaTitle)));
		l.add(new Zman(String.format("%tl:%<tM", z.getLatestShachris()), reverseWords(Zmanim.latestShachrisTitle)));
		l.add(new Zman(String.format("%tl:%<tM", z.getMidDay()), reverseWords(Zmanim.midDayTitle)));
		l.add(new Zman(String.format("%tl:%<tM", z.getPlagMincha()), reverseWords(Zmanim.plagMinchaTitle)));
		l.add(new Zman(String.format("%tl:%<tM", z.getSunset()), reverseWords(Zmanim.sunsetTitle)));
		l.add(new Zman(String.format("%tl:%<tM", z.getTzais()), reverseWords(Zmanim.tzaisTitle)));
		l.add(new Zman(String.format("%tl:%<tM", z.getTzais72()), reverseWords(Zmanim.tzaisTitle72)));
		
		if (jcal.isRoshChodesh()) 
			l.add(new Zman(String.format("Hours %02d Min %02d Chalakim %02d",
					z.getJcal().getMoladHours(), z.getJcal().getMoladMinutes(), z.getJcal().getMoladChalakim()),
					"Molad"));
		
		if (jcal.getDayOfWeek()==6|| jcal.isErevYomTov())
			l.add(new Zman(String.format("%tl:%<tM", z.getCandles()), "Candle lighting"));
		
		return l;
	}

  /**
   * Called when the user clicks Load Events menu item.
   */
	@FXML
	private void handleLoadEvents() {
		System.out.println("Display Load Events dialog");
		FileChooser fileChooser = new FileChooser();
		File file = mainApp.getEventFilePath();

		if (file!=null) {
			fileChooser.setInitialDirectory(file);
		} else {
			//Set to user directory or go to default if cannot access
			String userDirectoryString = System.getProperty("user.home");
			File userDirectory = new File(userDirectoryString);
			if(!userDirectory.canRead()) {
			    userDirectory = new File("c:/");
			}
			fileChooser.setInitialDirectory(userDirectory);
		}
		// Set extension filter
		FileChooser.ExtensionFilter extFilter = 
				new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		
		file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		
		if (file != null) {
			mainApp.loadEventDataFromFile(file);
		}
	}
		
  /**
   * Called when the user clicks Add Event menu item.
   */
	@FXML
	private void handleAddEvent() {
		System.out.println("Display Add Event dialog");
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, currDate.get(Calendar.YEAR));
		c.set(Calendar.MONTH, currDate.get(Calendar.MONTH));
		c.set(Calendar.DAY_OF_MONTH, 1);
		CalendarEvent tempEvent = new CalendarEvent(c, "");
		boolean okClicked = mainApp.showEventEditDialog(tempEvent);
		if (okClicked) {
			mainApp.getEventData().add(tempEvent);
			mainApp.saveEvents();
			updateFilteredData();
//			eventTable.getItems().add(tempEvent);
//			setEvents();
		}
		
	}
	
	@FXML
	private void handleEditEvent() {
		//javafx.scene.control.TableColumn$4@19a9ea0
		CalendarEvent event = (CalendarEvent)eventTable.getSelectionModel().getSelectedItem();
		int ex = eventTable.getSelectionModel().getSelectedIndex();
		int idx = mainApp.getEventData().indexOf(event);
		if (event != null) {
			boolean okClicked = mainApp.showEventEditDialog(event);
			if (okClicked) {
				if (idx!= -1) {
					mainApp.getEventData().remove(idx);
					mainApp.getEventData().add(event);
					mainApp.saveEvents();
					updateFilteredData();
//					eventTable.getItems().set(ex, event);
//					setEvents();
				}
			}
		} else {
			Dialogs.showErrorDialog(null, "No Event selected",
					"Please select an Event to edit", "Invalid selection");

		}
	}
	
  /**
   * Called when the user clicks Delete Event menu item.
   */
	@FXML
	private void handleDeleteEvent() {
		CalendarEvent event = (CalendarEvent)eventTable.getSelectionModel().getSelectedItem();
		int ex = eventTable.getSelectionModel().getSelectedIndex();
		if (event != null) {
			String msg = "Are you sure you want to delete \n"+
					event.getDisplayDate()+" / "+event.getJewishDate()+"\n"+event.getDescription();
			DialogResponse resp = Dialogs.showConfirmDialog(null, msg, "Delete Event");
			if (resp == DialogResponse.YES) {
				mainApp.getEventData().remove(event);
				mainApp.saveEvents();
				updateFilteredData();
//				eventTable.getItems().remove(ex);
//				setEvents();
			}
		} else {
			// Show the error message
			Dialogs.showErrorDialog(null, "No Event selected",
					"Please select an Event to delete", "Invalid selection");
			
		}
	}
	

	@FXML
	private void onMonthYearClicked(MouseEvent me) {
		boolean ok = mainApp.showChangeDateDialog(currDate);
		
		if (ok) {
			currDate.set(Calendar.DAY_OF_MONTH, 1);
			hayom = new JewishCalendar(currDate);
			setMonthYear();
			setDayCells();
			updateFilteredData();
//			setEvents();
		  }
	}
	
	@FXML
	private void showAllEvents() {
		filter = showAll.isSelected() ? CalendarController.FILTER_NONE : CalendarController.FILTER_MONTH;
		updateFilteredData();

		System.out.println("Filter "+ 
				(filter==CalendarController.FILTER_NONE ? "OFF" : "ON"));
	}	
	
	@FXML
	private void showSettings(MouseEvent me) {
	}	
}
