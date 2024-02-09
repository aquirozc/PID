package mx.uaemex.fi.linc34.rpixels.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import mx.uaemex.fi.linc34.rpixels.init.RandomPixels;

public interface IControllerFXML {

	default Parent getActivity(String n) {

		Parent parent = null;

		try {
			parent = new FXMLLoader(RandomPixels.class.getClassLoader().getResource(n)).load();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return parent;
	}

}
