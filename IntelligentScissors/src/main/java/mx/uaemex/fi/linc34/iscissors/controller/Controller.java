package mx.uaemex.fi.linc34.iscissors.controller;

import java.awt.image.BufferedImage;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mx.uaemex.fi.linc34.iscissors.helper.Dijkstra;
import mx.uaemex.fi.linc34.iscissors.helper.FXImageIO;
import mx.uaemex.fi.linc34.iscissors.helper.LiveWire;
import mx.uaemex.fi.linc34.iscissors.helper.LiveWire.Pixel;

public class Controller implements IControllerFXML {
	
	private Parent parent = getActivity("Main.fxml");
	private ImageView target_vw = (ImageView) parent.lookup("#image_vw");
	private ImageView path_vw = (ImageView) parent.lookup("#path_vw");
	private VBox canvas = (VBox) parent.lookup("#canvas");
	
	private Button openBtn = (Button) parent.lookup("#open_btn");
	private Button saveBtn = (Button) parent.lookup("#save_btn");
	private Button undoBtn = (Button) parent.lookup("#undo_btn");
	private Slider zoomBar = (Slider) parent.lookup("#zoom_bar");
	
	private RadioButton dragModeRB = (RadioButton) parent.lookup("#dragmode_rb");
	private RadioButton advModeRB = (RadioButton) parent.lookup("#advmode_rb");
	
	private Image ogImage;
	private FXImageIO imgHelper; 
		
	private boolean isAdvModeEnabled = false;
	private boolean isDragModeEnabled = true;
	
	private double zoomFactor = 1;
	private double[][] map ;
	
	boolean wait = false;
	
	private LiveWire.Pixel seed;
	
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
		path_vw = (ImageView) parent.lookup("#path_vw");
		canvas = (VBox) parent.lookup("#canvas");
		
		target_vw.boundsInParentProperty().addListener(this::updateCanvasSize);
		canvas.setOnMouseClicked(this::onClick);
		//canvas.addEventFilter(MouseEvent.ANY, this::handleMouseSelection);
		
		openBtn.setOnMouseClicked(e -> onCreate(null));
		saveBtn.setOnMouseClicked(e -> imgHelper.saveImageToDisk((WritableImage) target_vw.getImage()));
		undoBtn.setOnMouseClicked(e -> redrawImage());
		
		advModeRB.selectedProperty().addListener(this::enableAdvancedMode);
		dragModeRB.selectedProperty().addListener(this::enableDragMode);
		dragModeRB.setSelected(true);
		
		zoomBar.valueProperty().addListener(this::updateZoomLevel);
		
		imgHelper = new FXImageIO(stage);
		
	}
	
	
	public void onCreate(List<String> param) {
		
		ogImage = imgHelper.loadImageFromArgs(param);
		
		if (ogImage == null) {
			return;
		}		
		
		redrawImage();
		
	}
	
	private void enableAdvancedMode(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		
		if(!newValue) {
			return;
		}
		
		isDragModeEnabled = false;
		isAdvModeEnabled = true;
		
	}
	
	private void enableDragMode(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		
		if(!newValue) {
			return;
		}
		
		isDragModeEnabled = true;
		isAdvModeEnabled = false;
		
	}
	
	public void handleMouseSelection(MouseEvent event) {
		
		int x = (int) (event.getX()/ zoomFactor);
		int y = (int) (event.getY()/ zoomFactor);
	
		WritableImage canvas = (WritableImage) path_vw.getImage();
		
		if(event.getEventType() == MouseEvent.MOUSE_PRESSED) {
			
			seed = new Pixel(x,y);	
			canvas.getPixelWriter().setArgb(x, y, 0xFF00FF00);
			
			try {
				
				Thread.ofVirtual().start(() -> {
					WritableImage src = (WritableImage) target_vw.getImage();
					BufferedImage img = new BufferedImage((int)src.getWidth(), (int) src.getHeight(), BufferedImage.TYPE_INT_RGB);
					img = SwingFXUtils.fromFXImage(src, img);
					map = new LiveWire(img).pixelWeightMap;
				}).join();
				
			} catch (Exception e) {}
			
        }
		
        if(event.getEventType() == MouseEvent.MOUSE_DRAGGED  && isDragModeEnabled){
        	
        	if (wait) return;
        	
        	wait = true;
        	
        	Thread.ofVirtual().start(() -> {
        		
        		List<int[]> path = Dijkstra.dijkstra(map, seed.x(), seed.y(), x, y);	
        		
        		Platform.runLater(() -> {
        			for (int[] p : path) {
            			canvas.getPixelWriter().setArgb(p[0], p[1], 0xFFFF0000);
            		}
            		
            		seed = new Pixel(x, y);	
            		path_vw.setImage(canvas);	
        		});
        		
        		try {
					Thread.sleep(600);
				} catch (InterruptedException e) {}
        		
        		wait = false;
        		
        	});
     
        }
		
	}
	
	private void redrawImage() {

		if (ogImage == null) return;
		
		seed  = null;
		zoomBar.setValue(100);
		
		int width = (int) ogImage.getWidth();
		int height = (int) ogImage.getHeight();
		
		target_vw.setImage(new WritableImage(ogImage.getPixelReader(),width,height));
		target_vw.setFitWidth(width);
		target_vw.setFitHeight(height);
		
		path_vw.setImage(new WritableImage(width,height));
		path_vw.setFitWidth(width);
		path_vw.setFitHeight(height);
		
	}
	
	public void onClick(MouseEvent event) {
		
		int x = (int) (event.getX()/ zoomFactor);
		int y = (int) (event.getY()/ zoomFactor);
		
		
		WritableImage canvas = (WritableImage) path_vw.getImage();
		
		canvas.getPixelWriter().setArgb(x, y, 0xFF00FF00);
		
		System.out.println(seed);
		
		if (seed == null) {
			seed = new Pixel(x,y);	
			
			try {
				Thread.ofVirtual().start(() -> {
					WritableImage src = (WritableImage) target_vw.getImage();
					BufferedImage img = new BufferedImage((int)src.getWidth(), (int) src.getHeight(), BufferedImage.TYPE_INT_RGB);
					img = SwingFXUtils.fromFXImage(src, img);
					map = new LiveWire(img).pixelWeightMap;
				}).join();
				System.out.println("done");
			} catch (Exception e) {
				e.printStackTrace();
				seed = null;
			}
			return;
		}
		
		if (map == null) return;
		
		List<int[]> path = Dijkstra.dijkstra(map, seed.x(), seed.y(), x, y);	
		
		for (int[] p : path) {
			canvas.getPixelWriter().setArgb(p[0], p[1], 0xFFFF0000);
		}
		
		seed = new Pixel(x, y);	
		path_vw.setImage(canvas);	
	}
	
	private void updateCanvasSize(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
		canvas.setMaxWidth(newValue.getWidth());
        canvas.setMaxHeight(newValue.getHeight());
	}
	
	private void updateZoomLevel(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		
		if (ogImage == null) {
	        return;
	    }

	    double newFactor = (double) newValue / 100d;

	    target_vw.setFitHeight(ogImage.getHeight() * newFactor);
	    target_vw.setFitWidth(ogImage.getWidth() * newFactor);
	    
	    path_vw.setFitHeight(ogImage.getHeight() * newFactor);
	    path_vw.setFitWidth(ogImage.getWidth() * newFactor);

	    zoomFactor = newFactor;
		
	}
	
	
}
