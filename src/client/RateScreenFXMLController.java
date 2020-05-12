package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.stage.Stage;
import server.ControlDB.DBControl;
import server.ControlDB.DBControlImpl;

import javax.swing.text.html.parser.Parser;

import static javafx.collections.FXCollections.observableArrayList;

public class RateScreenFXMLController extends FXMLController implements Initializable {

    private ObservableList<Integer> rateList;
    private ObservableList<String> clickList;
    private String nameUser;
    private UserScreenFXMLController parent;

    @FXML
    Label titleLabel;
    @FXML
    ComboBox rateBox;
    @FXML
    ComboBox clickBox;
    @FXML
    Button OKButt;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rateList = observableArrayList(1,2,3,4,5,6,7,8,9,10);
        clickList = observableArrayList("Да", "Нет");
        rateBox.setItems(rateList);
        clickBox.setItems(clickList);
        rateBox.setValue(1);
        clickBox.setValue("Да");
    }

    public void handleOKAction(ActionEvent actionEvent) {
        boolean click;

        if (clickBox.getValue().toString() == "Да")
            click = true;
        else
            click = false;
        parent.setRate((int)rateBox.getValue());
        parent.setClickOnAd(click);
        Stage thisStage = (Stage) OKButt.getScene().getWindow();
        thisStage.close();
    }

    public void setNameUser(String nameUser){ titleLabel.setText(nameUser + titleLabel.getText());}

    @Override
    public void setParent(FXMLController controller) { parent = (UserScreenFXMLController) controller; }

}
