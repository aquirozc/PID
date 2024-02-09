package mx.uaemex.fi.linc34.rpixels.init;

import javafx.application.Application;
import javafx.stage.Stage;
import mx.uaemex.fi.linc34.rpixels.controller.Controller;

public class FXApplication  extends Application{
	
	public static void main (String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		
		new Controller(stage).onCreate(this.getParameters().getRaw());
		
	}	
	

}
