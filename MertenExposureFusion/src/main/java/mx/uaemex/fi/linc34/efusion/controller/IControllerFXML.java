package mx.uaemex.fi.linc34.efusion.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import mx.uaemex.fi.linc34.efusion.init.ExposureFusion;

public interface IControllerFXML {

	default Parent getActivity(String n) {

		Parent parent = null;

		try {
			parent = new FXMLLoader(ExposureFusion.class.getClassLoader().getResource(n)).load();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return parent;
	}

}
