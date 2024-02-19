package mx.uaemex.fi.linc61.lfilters.controller;

import java.awt.image.BufferedImage;
import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import mx.uaemex.fi.linc61.lfilters.helper.AWTImageToolkit;
import mx.uaemex.fi.linc61.lfilters.helper.AWTKernel;
import mx.uaemex.fi.linc61.lfilters.helper.FXImageToolkit;

public class Controller implements IControllerFXML {
	
	private Parent parent = getActivity("Main.fxml");
	private ImageView target_vw = (ImageView) parent.lookup("#image_vw");
	
	private Button openBtn = (Button) parent.lookup("#open_btn");
	private Button saveBtn = (Button) parent.lookup("#save_btn");
	private Button undoBtn = (Button) parent.lookup("#undo_btn");
	private Slider zoomBar = (Slider) parent.lookup("#zoom_bar");
	
	private Button laplaceBtn = (Button) parent.lookup("#laplace_btn");
	private Button gaussBtn = (Button) parent.lookup("#gauss_btn");
	private Button meanBtn = (Button) parent.lookup("#mean_btn");
	
	private AWTImageToolkit imgHelperAWT = new AWTImageToolkit();
	private Image ogImage;
	private FXImageToolkit imgHelperFX; 
	
	public Controller(Stage stage) {
		
		Scene scene = new Scene(parent);
		stage.setMinWidth(800);
		stage.setMinHeight(575);
		stage.setWidth(900);
		stage.setHeight(600);
		stage.setTitle("Editor de Imagenes (Alejandro Quiroz Carmona");
		stage.setScene(scene);
		stage.show();
		
		target_vw = (ImageView) parent.lookup("#image_vw");
		imgHelperFX = new FXImageToolkit(stage);
		
		openBtn.setOnMouseClicked(e -> onCreate(null));
		saveBtn.setOnMouseClicked(e -> imgHelperFX.saveImageToDisk((WritableImage) target_vw.getImage()));
		undoBtn.setOnMouseClicked(e -> redrawImage());
		
		laplaceBtn.setOnMouseClicked(e -> applyFilter(AWTKernel.LAPLACE_KERNEL));
		gaussBtn.setOnMouseClicked(e -> applyFilter(AWTKernel.GAUSS_KERNEL));
		meanBtn.setOnMouseClicked(e -> applyFilter(AWTKernel.MEAN_KERNEL));
		
		zoomBar.valueProperty().addListener(this::updateZoomLevel);
		
		
		
	}
	
	
	public void onCreate(List<String> param) {
		
		ogImage = imgHelperFX.loadImageFromArgs(param);
		
		if (ogImage == null) {
			return;
		}		
		
		ogImage = imgHelperFX.toGrayScale(ogImage);
		redrawImage();
		
	}
	
	private void applyFilter(AWTKernel kernel) {
	
		Image input = target_vw.getImage();
		
		int width = (int) ogImage.getWidth();
		int height = (int) ogImage.getHeight();
		
		Thread.ofVirtual().start(() -> {
			
			BufferedImage bInput = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
			bInput = SwingFXUtils.fromFXImage(input, bInput);
			bInput = imgHelperAWT.toGrayScale(bInput);		
			long l = System.currentTimeMillis();
			bInput = imgHelperAWT.applyLinearFilter(bInput, kernel);
			System.out.println(System.currentTimeMillis() - l);
			
			SwingFXUtils.toFXImage(bInput, (WritableImage) input);
			
		});
	}
	
	private void redrawImage() {

		if (ogImage == null) return;
		
		zoomBar.setValue(100);
		
		int width = (int) ogImage.getWidth();
		int height = (int) ogImage.getHeight();
		
		target_vw.setImage(new WritableImage(ogImage.getPixelReader(),width,height));
		target_vw.setFitWidth(width);
		target_vw.setFitHeight(height);
	}
	
	
	private void updateZoomLevel(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		
		if (ogImage == null) {
	        return;
	    }

	    target_vw.setFitHeight(ogImage.getHeight() * newValue.doubleValue() / 100d);
	    target_vw.setFitWidth(ogImage.getWidth() * newValue.doubleValue() / 100d);
		
	}
	
	
}
