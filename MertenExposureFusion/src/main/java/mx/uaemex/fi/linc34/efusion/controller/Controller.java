package mx.uaemex.fi.linc34.efusion.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.uaemex.fi.linc34.efusion.helper.FXImageIO;
import mx.uaemex.fi.linc34.efusion.helper.HDRImageMaker;

public class Controller implements IControllerFXML {
	
	private Parent parent = getActivity("Main.fxml");
	private ImageView target_vw = (ImageView) parent.lookup("#image_vw");
	private VBox canvas = (VBox) parent.lookup("#canvas");
	
	private Button openBtn = (Button) parent.lookup("#open_btn");
	private Button saveBtn = (Button) parent.lookup("#save_btn");
	private Button undoBtn = (Button) parent.lookup("#undo_btn");
	private Button editBtn = (Button) parent.lookup("#edit_btn");
	private Slider zoomBar = (Slider) parent.lookup("#zoom_bar");
	
	private RadioButton dragModeRB = (RadioButton) parent.lookup("#dragmode_rb");
	private RadioButton advModeRB = (RadioButton) parent.lookup("#advmode_rb");
	private TextField x0TF = (TextField) parent.lookup("#x0_tf");
	private TextField y0TF = (TextField) parent.lookup("#y0_tf");
	private TextField x1TF = (TextField) parent.lookup("#x1_tf");
	private TextField y1TF = (TextField) parent.lookup("#y1_tf");
	
	private Image ogImage;
	private Rectangle selectedArea = new Rectangle(0,0,0,0);
	private FXImageIO imgHelper; 
		
	private boolean isAdvModeEnabled = false;
	private boolean isDragModeEnabled = true;
	private double zoomFactor = 1;
	
	Stage stage ;	
	public Controller(Stage stage) {
		
		this.stage = stage;
		
		Scene scene = new Scene(parent);
		stage.setMinWidth(800);
		stage.setMinHeight(575);
		stage.setWidth(900);
		stage.setHeight(600);
		stage.setTitle("Editor de Imagenes");
		stage.setScene(scene);
		stage.show();
		
		target_vw = (ImageView) parent.lookup("#image_vw");
		canvas = (VBox) parent.lookup("#canvas");
		canvas.getChildren().add(selectedArea);
		
		target_vw.boundsInParentProperty().addListener(this::updateCanvasSize);
		canvas.addEventFilter(MouseEvent.ANY, this::handleMouseSelection);
		
		openBtn.setOnMouseClicked(e -> onCreate(null));
		saveBtn.setOnMouseClicked(e -> imgHelper.saveImageToDisk((WritableImage) target_vw.getImage()));
		undoBtn.setOnMouseClicked(e -> redrawImage());
		editBtn.setOnMouseClicked(this::replaceArea);
		
		advModeRB.selectedProperty().addListener(this::enableAdvancedMode);
		dragModeRB.selectedProperty().addListener(this::enableDragMode);
		dragModeRB.setSelected(true);
		
		x0TF.textProperty().addListener(this::validateNumInput);
		y0TF.textProperty().addListener(this::validateNumInput);
		x1TF.textProperty().addListener(this::validateNumInput);
		y1TF.textProperty().addListener(this::validateNumInput);
		
		this.toggleTextInputs(false);
		
		zoomBar.valueProperty().addListener(this::updateZoomLevel);
		
		imgHelper = new FXImageIO(stage);
		
	}
	
	
	public void onCreate(List<String> param) {
		
		File file = new DirectoryChooser().showDialog(stage);
		
		Thread.ofVirtual().start(() -> {
			
			long l = System.currentTimeMillis();
			
			BufferedImage[] stack =	Stream.of(file.listFiles()).map(f -> {
												try {
													return ImageIO.read(f);
												} catch (IOException e) {
													return null;
												}
					
			}).filter(i-> i != null).toArray(BufferedImage[]::new);
		
			
			System.out.println(stack.length);
			
			try {
				ImageIO.write(new HDRImageMaker().bleedImages(stack), "jpg", new File("/Volumes/Elements/Pene.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println(System.currentTimeMillis() - l);
			
		});
		
	}
	
	private void enableAdvancedMode(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		
		if(!newValue) {
			return;
		}
		
		isDragModeEnabled = false;
		isAdvModeEnabled = true;
		
		resetSelection();
		toggleTextInputs(true);
		
	}
	
	private void enableDragMode(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		
		if(!newValue) {
			return;
		}
		
		isDragModeEnabled = true;
		isAdvModeEnabled = false;
		
		resetInput();
		toggleTextInputs(false);
		
	}
	
	private void handleMouseSelection(MouseEvent event) {
		
		if(event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            selectedArea.setTranslateX(event.getX());
            selectedArea.setTranslateY(event.getY());
            selectedArea.setWidth(0);
            selectedArea.setHeight(0);
         }
        if(event.getEventType() == MouseEvent.MOUSE_DRAGGED  && isDragModeEnabled){
        	int width = (int) (event.getX() - selectedArea.getTranslateX());
        	int height = (int) (event.getY() - selectedArea.getTranslateY());
        	
        	
            selectedArea.setWidth(Math.min(width, canvas.getMaxWidth() - (selectedArea.getTranslateX())));
            selectedArea.setHeight(Math.min(height, canvas.getMaxHeight() - (selectedArea.getTranslateY())));   
        }
		
	}
	
	private void redrawImage() {

		if (ogImage == null) return;
		
		resetSelection();
		zoomBar.setValue(100);
		
		int width = (int) ogImage.getWidth();
		int height = (int) ogImage.getHeight();
		
		target_vw.setImage(new WritableImage(ogImage.getPixelReader(),width,height));
		target_vw.setFitWidth(width);
		target_vw.setFitHeight(height);
	}
	
	private void resetSelection() {
		selectedArea.setWidth(0);
		selectedArea.setHeight(0);
	};
	
	private void resetInput() {
		
		x0TF.setText("");
		y0TF.setText("");
		x1TF.setText("");
		y1TF.setText("");
		
	}
	
	private void replaceArea(MouseEvent event) {
		
		int x0 = (int) (selectedArea.getTranslateX() / zoomFactor);
		int x1 = (int) (x0 + selectedArea.getWidth() / zoomFactor);
		
		int y0 = (int) (selectedArea.getTranslateY() / zoomFactor);
		int y1 = (int) (y0 + selectedArea.getHeight() / zoomFactor);
		
		if (isAdvModeEnabled) {
			x0 = Integer.parseInt("0".concat(x0TF.getText()));
			y0 = Integer.parseInt("0".concat(y0TF.getText()));
			x1 = Integer.parseInt("0".concat(x1TF.getText()));
			y1 = Integer.parseInt("0".concat(y1TF.getText()));
		}
		
		try {
			imgHelper.overrideRegionRandom((WritableImage) target_vw.getImage(), x0, x1, y0, y1);
		} catch (Exception e) {
			new Alert(AlertType.ERROR,"Los limites de la selección deben estar dentro de la imagen").showAndWait();
		}
		
		resetSelection();
		resetInput();
		
	}
	
	private void toggleTextInputs(boolean v) {
		
		x0TF.setEditable(v);
		y0TF.setEditable(v);
		x1TF.setEditable(v);
		y1TF.setEditable(v);
		
	}
	
	private void validateNumInput(ObservableValue<? extends String> observable, String oldValue, String newValue)  {
		
		if (newValue.matches("\\d*")) {
	        return;
	    }
		
		((StringProperty)observable).setValue(oldValue);
		
		
	}
	
	private void updateCanvasSize(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
		canvas.setMaxWidth(newValue.getWidth() - 20);
        canvas.setMaxHeight(newValue.getHeight() - 20);
	}
	
	private void updateZoomLevel(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		
		if (ogImage == null) {
	        return;
	    }

	    double newFactor = (double) newValue / 100d;
	    double zoomDelta = newFactor / zoomFactor;

	    target_vw.setFitHeight(ogImage.getHeight() * newFactor);
	    target_vw.setFitWidth(ogImage.getWidth() * newFactor);

	    selectedArea.setTranslateX(selectedArea.getTranslateX() * zoomDelta);
	    selectedArea.setTranslateY(selectedArea.getTranslateY() * zoomDelta);
	    selectedArea.setWidth(selectedArea.getWidth() * zoomDelta);
	    selectedArea.setHeight(selectedArea.getHeight() * zoomDelta);

	    zoomFactor = newFactor;
		
	}
	
	
}
