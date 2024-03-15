package mx.uaemex.fi.linc34.iscissors.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import mx.uaemex.fi.linc34.iscissors.init.IntelligentScissors;

public interface IControllerFXML {

	default Parent getActivity(String n) {

		Parent parent = null;

		try {
			parent = new FXMLLoader(IntelligentScissors.class.getClassLoader().getResource(n)).load();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return parent;
	}

}
