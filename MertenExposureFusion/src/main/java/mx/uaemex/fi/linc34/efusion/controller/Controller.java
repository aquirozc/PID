package mx.uaemex.fi.linc34.efusion.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import mx.uaemex.fi.linc34.efusion.helper.ImageToolkit;

public class Controller implements IControllerFXML {
	
	private Parent parent = getActivity("Main.fxml");
	private ImageView target_vw = (ImageView) parent.lookup("#image_vw");
	
	private Button openBtn = (Button) parent.lookup("#open_btn");
	private Button saveBtn = (Button) parent.lookup("#save_btn");
	private Slider zoomBar = (Slider) parent.lookup("#zoom_bar");
	
	private WritableImage ogImage;
	private ImageToolkit imgToolkit;
		
	public Controller(Stage stage) {
		
		Scene scene = new Scene(parent);
		stage.setMinWidth(800);
		stage.setMinHeight(575);
		stage.setWidth(900);
		stage.setHeight(600);
		stage.setTitle("Editor de Imagenes");
		stage.setScene(scene);
		stage.show();
		
		target_vw = (ImageView) parent.lookup("#image_vw");
		
		openBtn.setOnMouseClicked(e -> onCreate(null));
		saveBtn.setOnMouseClicked(e -> imgToolkit.saveImageToDisk(ogImage));
		
		zoomBar.valueProperty().addListener(this::updateZoomLevel);
		
		imgToolkit = new ImageToolkit(stage);
		
	}
	
	
	public void onCreate(List<String> param) {
		
		File dir = imgToolkit.changeWorkingDirectory();
		
		Thread.ofVirtual().start(() -> {
			BufferedImage hdr = imgToolkit.generateHDRImage(dir);
			ogImage = new WritableImage(hdr.getWidth(),	 hdr.getHeight());
			
			SwingFXUtils.toFXImage(hdr, ogImage);	
			target_vw.setImage(ogImage);
			System.out.println("done");
		});
		
	}
	
	private void updateZoomLevel(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		
		if (ogImage == null) {
	        return;
	    }

	    target_vw.setFitHeight(ogImage.getHeight() * (double) newValue / 100d);
	    target_vw.setFitWidth(ogImage.getWidth() * (double) newValue / 100d);

	}
	
	
}
