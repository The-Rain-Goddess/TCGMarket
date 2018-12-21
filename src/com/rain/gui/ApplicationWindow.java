package com.rain.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ApplicationWindow extends Application{
	
//private fields	
	private final double WINDOW_MIN_WIDTH = 1200;
	private final double WINDOW_MIN_HEIGHT = 800;

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
		
		//set main window size
		componentWindow.setMinHeight(WINDOW_MIN_HEIGHT);
		componentWindow.setMinWidth(WINDOW_MIN_WIDTH);
		
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

}
