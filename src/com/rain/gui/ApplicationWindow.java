package com.rain.gui;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.rain.card.Card;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class ApplicationWindow extends Application{
	
//private fields	
	private final double WINDOW_MIN_WIDTH = 1200;
	private final double WINDOW_MIN_HEIGHT = 800;
	private TableView<Card> dataTable;
	private TextField search;

//constructor
	public ApplicationWindow() {
		
	}

//class start point	
	public void begin(String[] args){
		launch(args);
	}
	
	@Override
	public void start(Stage rootStage) throws Exception {
		AnchorPane componentWindow = new AnchorPane();
		VBox componentLayout = new VBox();
		BorderPane tableDisplay = new BorderPane();
		search = new TextField();
		
		//set main window size
		componentWindow.setMinHeight(WINDOW_MIN_HEIGHT);
		componentWindow.setMinWidth(WINDOW_MIN_WIDTH);
		
		//align tabledisplay
		tableDisplay.setRight(setupDataTable());
		VBox.setMargin(tableDisplay, new Insets(10,10,10,10));
        componentLayout.getChildren().addAll(tableDisplay);
        
        //add componentLayout to Window
        componentWindow.getChildren().addAll(componentLayout);
		
		//Create the scene and add the parent container to it
        Scene scene = new Scene(componentWindow, WINDOW_MIN_WIDTH, WINDOW_MIN_HEIGHT);
	      
	    //Add the Scene to the Stage
        rootStage.setScene(scene);
	    //rootStage.getIcons().add(new Image(this.getClass().getResourceAsStream( "media_library.png" ))); 
	    rootStage.show();
	}

//private dataTable accessors/mutators
	
	private VBox setupDataTable() {
		VBox container = new VBox();
		container.setMinHeight(650);
		dataTable = new TableView<>();
		dataTable.setId("Data Table");
		dataTable.setMinHeight(650);
		dataTable.setMaxHeight(650);
		dataTable.setMinWidth(2*WINDOW_MIN_WIDTH/3 + 100);
		BorderPane.setMargin(dataTable, new Insets(10,10,10,10));
		
		//initialize the columns
		dataTable.getColumns().setAll(getDataTableColumns());
		
		//sort and filter the data
		SortedList<Card> sortedData = getSortedData();
		
		// Bind the SortedList comparator to the TableView comparator
		sortedData.comparatorProperty().bind(dataTable.comparatorProperty());
		
		//set click event henadling
		setDataTableClickEvents();
		
		// Show the Data
		dataTable.setItems(sortedData);
		container.setMaxHeight(WINDOW_MIN_HEIGHT - 200);
		//VBox.setVgrow(dataTable, Priority.NEVER);
		container.getChildren().addAll(dataTable);
		return container;
	}
	
	private void setDataTableClickEvents(){
		dataTable.setRowFactory(tv -> {
			TableRow<Card> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				try{
					if(event.getButton() == MouseButton.SECONDARY){
						ContextMenu cMenu = new ContextMenu();
						MenuItem remove = new MenuItem("Remove");
						remove.setOnAction((ActionEvent ev) ->{
							Main.getMasterData().remove(row.getItem().getUUID());
							updateDataTable();
		                	updateFileSystem();
						});
						
						cMenu.getItems().add(remove);
						row.setOnContextMenuRequested(e ->
							cMenu.show(row, event.getScreenX(), event.getScreenY())
						);
						
					} else{
						if(event.getClickCount() == 1){
							System.out.println("Clicked");
							System.out.println(row.getItem());
						} else if(event.getClickCount() == 2){
							System.out.println("Playing: \n" + row.getItem());
							File mediaFile = new File(row.getItem().getLibraryFilePath());
							Media mediaToPlay = new Media(mediaFile.toURI().toString());
							artistLabel.setText(row.getItem().getArtistName() + " - " + row.getItem().getAlbumName());
							songLabel.setText(row.getItem().getSongName());
							player.stop();
							player = new MediaPlayer(mediaToPlay);
							player.setAutoPlay(true);
							updatePlayer();
						}
					}
				} catch(MediaException e){
					Stage errorWindow = new Stage();
					VBox componentLayout = new VBox();
					Label errorLabel = new Label(e.getMessage());
					VBox.setMargin(errorLabel, new Insets(10,10,10,10));
					componentLayout.getChildren().addAll(errorLabel);
					Scene scene = new Scene(componentLayout);
					errorWindow.setScene(scene);
					errorWindow.show();
				}
			});
			return row;
		});
	}
	
	private SortedList<Card> getSortedData(){
		//get the data
		List<Card> data = Main.getMasterDataAsList();
				
		//wrap the ObservableList in a FilteredList
		FilteredList<Card> filteredData = new FilteredList<>(FXCollections.observableList(data), p -> true);
		
		//set the filter Predicate whenever the filter changes
		search.textProperty().addListener((observable, oldValue, newValue)-> {
			filteredData.setPredicate(media -> {
				//If filter is empty, display all
				if(newValue == null || newValue.isEmpty()){
					return true;
				}
				
				String lowerCaseFilter = newValue.toLowerCase();
				if(media.getSongName().toLowerCase().contains(lowerCaseFilter))
					return true;
				else if(media.getArtistName().toString().toLowerCase().contains(lowerCaseFilter))
					return true;
				else if(media.getAlbumName().toLowerCase().contains(lowerCaseFilter))
					return true;
				else if(media.getGenre().toLowerCase().contains(lowerCaseFilter))
					return true;
				else return false;
			});
		});
		
		//wrap the filtered list in a sorted list
		SortedList<Card> sortedData = new SortedList<>(filteredData);
		
		return sortedData;
	}
	
	private void updateDataTable(){
		//sort and filter the data
		SortedList<Card> sortedData = getSortedData();
		
		// Bind the SortedList comparator to the TableView comparator
		sortedData.comparatorProperty().bind(dataTable.comparatorProperty());
		
		//set click events handling
		setDataTableClickEvents();
		
		// Show the Data
		dataTable.setItems(sortedData);
	}
	
	private List<TableColumn<Card, ?>> getDataTableColumns(){
		//Column 1: Name
		TableColumn<Card, String> mediaNameCol = new TableColumn<>("Name");
		mediaNameCol.setCellValueFactory(cellData -> cellData.getValue().getSongNameProperty());
		
		//Column 2: Artist
		TableColumn<Card, String> mediaArtistCol = new TableColumn<>("Artist(s)");
		mediaArtistCol.setCellValueFactory(cellData -> cellData.getValue().getArtistNameProperty());
		
		//Column 3: Album
		TableColumn<Card, String> mediaAlbumCol = new TableColumn<>("Album");
		mediaAlbumCol.setCellValueFactory(cellData -> cellData.getValue().getAlbumNameProperty());
		
		//Column 4: Album Number
		TableColumn<Card, Number> mediaNumberCol = new TableColumn<>("Number");
		mediaNumberCol.setCellValueFactory(cellData -> cellData.getValue().getAlbumNumberProperty());
		
		//Column 5: Genre
		TableColumn<Card, String> mediaGenreCol = new TableColumn<>("Genre");
		mediaGenreCol.setCellValueFactory(cellData -> cellData.getValue().getGenreProperty());
		
		//Column 6: Song Length
		TableColumn<Card, Number> mediaLengthCol = new TableColumn<>("Length");
		mediaLengthCol.setCellValueFactory(cellData -> cellData.getValue().getSongLengthProperty());
		return Arrays.asList(mediaNameCol, mediaArtistCol, mediaAlbumCol, mediaNumberCol,mediaGenreCol, mediaLengthCol);		
	}
	
	
//public accessors/mutators	
}
