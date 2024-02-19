package mx.uaemex.fi.linc61.lfilters.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import mx.uaemex.fi.linc61.lfilters.init.LinearFilters;

public interface IControllerFXML {

	default Parent getActivity(String n) {

		Parent parent = null;

		try {
			parent = new FXMLLoader(LinearFilters.class.getClassLoader().getResource(n)).load();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return parent;
	}

}
