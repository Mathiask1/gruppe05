package edu.sdu.sensumbosted.presentation;

import edu.sdu.sensumbosted.Main;
import javafx.fxml.Initializable;

public abstract class SensumController implements Initializable {

    final Main main;

    SensumController(Main main) {
        this.main = main;
    }

}
