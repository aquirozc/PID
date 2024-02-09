package mx.uaemex.fi.linc34.rpixels.controller;

import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import mx.uaemex.fi.linc34.rpixels.helper.FXImageIO;

public class Controller implements IControllerFXML {
	
	private Parent parent = getActivity("Main.fxml");
	private ImageView target_vw = (ImageView) parent.lookup("#image_vw");
	private ScrollPane scrollPane = (ScrollPane) parent.lookup("#scrollpane");
	private VBox canvas = (VBox) parent.lookup("#canvas");
	private Button saveBtn = (Button) parent.lookup("#btn");
	
	private Image ogImage;
	private Rectangle selectedArea = new Rectangle(0,0,0,0);
	private FXImageIO imgHelper; 
		
	private boolean isMouseDragging = false;
	
	public Controller(Stage stage) {
		
		Scene scene = new Scene(parent);
		stage.setTitle("Visor de Imagenes");
		stage.setScene(scene);
		stage.show();
		
		target_vw = (ImageView) parent.lookup("#image_vw");
		canvas = (VBox) parent.lookup("#canvas");
		canvas.getChildren().add(selectedArea);
		
		scrollPane.viewportBoundsProperty().addListener(this::updateCanvasSize);
		canvas.addEventFilter(MouseEvent.ANY, this::handleMouseSelection);
		
		saveBtn.setOnMouseClicked(this::replaceArea);
		
		imgHelper = new FXImageIO(stage);
		
	}
	
	
	public void onCreate(List<String> param) {
		
		ogImage = imgHelper.loadImageFromArgs(param);
		int width = (int) ogImage.getWidth();
		int height = (int) ogImage.getHeight();
		
		target_vw.setImage(new WritableImage(ogImage.getPixelReader(),width,height));
		target_vw.setFitWidth(width);
		target_vw.setFitHeight(height);
		
	}
	
	private void handleMouseSelection(MouseEvent event) {
		
		if(event.getEventType() == MouseEvent.MOUSE_PRESSED){
            isMouseDragging = true;
            
            selectedArea.setTranslateX(event.getX());
            selectedArea.setTranslateY(event.getY());
            selectedArea.setWidth(0);
            selectedArea.setHeight(0);
         }
        if(event.getEventType() == MouseEvent.MOUSE_DRAGGED && isMouseDragging){
            selectedArea.setWidth(Math.min(event.getX() - selectedArea.getTranslateX(), canvas.getMaxWidth()));
            selectedArea.setHeight(Math.min(event.getY() - selectedArea.getTranslateY(), canvas.getMaxHeight()));
        }
        if(event.getEventType() == MouseEvent.MOUSE_RELEASED) {
        	isMouseDragging = false;
        }
		
	}
	
	private void replaceArea(MouseEvent event) {
		
		int xOffset = (int) (scrollPane.getHvalue() * (target_vw.getFitWidth() - scrollPane.viewportBoundsProperty().get().getWidth()));
		int yOffset = (int) (scrollPane.getVvalue() * (target_vw.getFitHeight() - scrollPane.viewportBoundsProperty().get().getHeight()));
		
		int x0 = (int) (selectedArea.getTranslateX() + xOffset);
		int x1 = (int) (x0 + selectedArea.getWidth());
		
		int y0 = (int) (selectedArea.getTranslateY() + yOffset);
		int y1 = (int) (y0 + selectedArea.getHeight());
		
		imgHelper.overrideRegionRandom((WritableImage) target_vw.getImage(), x0, x1, y0, y1);
		
		isMouseDragging = false;
		selectedArea.setWidth(0);
		selectedArea.setHeight(0);
		
	}
	
	private void updateCanvasSize(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
		canvas.setMaxWidth(newValue.getWidth() - 20);
        canvas.setMaxHeight(newValue.getHeight() - 20);
	}
	
}
