package mx.uaemex.fi.linc34.iscissors.init;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mx.uaemex.fi.linc34.iscissors.controller.Controller;

public class FXApplication  extends Application{
	
	public static void main (String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		
		Font.loadFont(FXApplication.class.getClassLoader().getResourceAsStream("Khula-Light.ttf"),20);
		
		new Controller(stage).onCreate(this.getParameters().getRaw());
		
	}	
	

}
