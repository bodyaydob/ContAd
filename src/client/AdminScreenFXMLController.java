package client;

import client.model.Ad;
import client.model.Category;
import client.model.History;
import client.model.Word;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import server.ControlDB.DBControl;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminScreenFXMLController extends FXMLController implements Initializable {
    private ObservableList<String> choiceList;
    private ObservableList<String> catNameList;
    private List<History> historyList;
    private List<Category> categoriesList;
    private List<Word> wordsList;
    private List<Ad> adsList;
    private ToggleGroup tg;
    private FXMLController children;
    private FXMLController parent;
    private int idUserDB;
    private DBControl service;
    private boolean firstAction = true;

    TableColumn<History, String> userCol;
    TableColumn<History, String> adCol;
    TableColumn<History, String> url1Col;
    TableColumn<History, String> url2Col;

    TableColumn<Category,String> nameCol;

    TableColumn<Word,String> valueCol;
    TableColumn<Word,String> catCol;

    TableColumn<Ad,String> pathCol;
    TableColumn<Ad,Integer> prioCol;
    TableColumn<Ad,String> categCol;


    @FXML
    ComboBox choice;
    @FXML
    ToggleButton addToggleBut;
    @FXML
    ToggleButton delToggleBut;
    @FXML
    TableView table;
    @FXML
    Label nameUser;
    @FXML
    GridPane fields;
    @FXML
    Label lbfield1;
    @FXML
    Label lbfield2;
    @FXML
    Label lbfield3;
    @FXML
    TextField field1;
    @FXML
    ChoiceBox<String> field2;
    @FXML
    TextField field3;
    @FXML
    Button delOKButt;


    @Override
    public FXMLController getChildren() {
        return children;
    }

    @Override
    public void setParent(FXMLController controller) {
        this.parent = controller;
    }

    @Override
    public FXMLController getParent() {
        return parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Registry registry = LocateRegistry.getRegistry(0);
            service = (DBControl) registry.lookup("DBControl");
        } catch (RemoteException e) {
            Logger.getLogger(AuthorizationFXMLController.class.getName()).log(Level.SEVERE, null, e);
        } catch (NotBoundException e) {
            Logger.getLogger(AuthorizationFXMLController.class.getName()).log(Level.SEVERE, null, e);
        }
        initNodes();

        userCol = new TableColumn<History, String>("Пользователь");
        adCol = new TableColumn<History, String>("Реклама");
        url1Col = new TableColumn<History, String>("Запрос 1");
        url2Col = new TableColumn<History, String>("Запрос 2");

        nameCol = new TableColumn<Category,String>("Имя");

        valueCol = new TableColumn<Word,String>("Слово");
        catCol = new TableColumn<Word,String>("Категория");

        pathCol = new TableColumn<Ad,String>("Путь к файлу");
        prioCol = new TableColumn<Ad,Integer>("Приоритет");
        categCol = new TableColumn<Ad,String>("Категория");

        userCol.setCellValueFactory(new PropertyValueFactory<>("user"));
        adCol.setCellValueFactory(new PropertyValueFactory<>("ad"));
        url1Col.setCellValueFactory(new PropertyValueFactory<>("url1"));
        url2Col.setCellValueFactory(new PropertyValueFactory<>("url2"));

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        pathCol.setCellValueFactory(new PropertyValueFactory<>("path"));
        prioCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        categCol.setCellValueFactory(new PropertyValueFactory<>("category"));

    }

    private void initNodes() {
        choiceList = FXCollections.observableArrayList("...Хочу подумать...", "Реклама", "Словарь лемм", "Категории", "История");
        choice.setItems(choiceList);
        tg = new ToggleGroup();
        addToggleBut.setToggleGroup(tg);
        delToggleBut.setToggleGroup(tg);
        addToggleBut.setSelected(true);
        fields.setVisible(false);
        delOKButt.setVisible(false);
    }

    public void handleChoiceBoxAction(ActionEvent event) throws RemoteException {
        if (firstAction) {
            nameUser.setText(service.getNameUser(idUserDB));
            firstAction = false;
        }
        if (choice.getValue().equals("...Хочу подумать...")) {
            addToggleBut.setVisible(false);
            delToggleBut.setVisible(false);
            table.setVisible(false);
            fields.setVisible(false);
            delOKButt.setVisible(false);
        } else {
            if (choice.getValue().equals("История")) {
                table.getColumns().clear();
                addToggleBut.setVisible(false);
                delToggleBut.setVisible(false);
                fields.setVisible(false);
                delOKButt.setVisible(false);
                table.getColumns().addAll(userCol, adCol, url1Col, url2Col);
                historyList = service.getHistoryList();
                table.setItems(FXCollections.observableArrayList(historyList));
                table.setVisible(true);

            } else {
                addToggleBut.setVisible(true);
                delToggleBut.setVisible(true);
                addToggleBut.setSelected(true);
                table.setVisible(false);
                delOKButt.setVisible(false);
                drawField();
            }
        }


    }

    @Override
    public void setId(int id) {
        this.idUserDB = id;
    }

    public void handleLogoutAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("authorization.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("client/style.css");
        ContextualAd.primaryStage.setScene(scene);
        ContextualAd.primaryStage.setTitle("Окно авторизации");

        children = loader.getController();
        children.setParent(this);
        ContextualAd.primaryStage.show();
    }

    public void handleAddAction(ActionEvent event) throws RemoteException {
        drawField();
        table.setVisible(false);
        delOKButt.setVisible(false);
    }

    private void drawField() throws RemoteException {
        fields.setVisible(true);
        switch ((String) choice.getValue()) {
            case "Реклама":
                lbfield1.setVisible(true);
                lbfield2.setVisible(true);
                lbfield3.setVisible(true);
                lbfield1.setText("Путь");
                lbfield2.setText("Категория");
                lbfield3.setText("Приоритет");
                field1.setVisible(true);
                field2.setVisible(true);
                field3.setVisible(true);
                field1.setText("");
                field3.setText("");
                field2.setItems(FXCollections.observableArrayList(service.getCategoriesNameList()));
                break;
            case "Категории":
                lbfield1.setVisible(true);
                lbfield2.setVisible(false);
                lbfield3.setVisible(false);
                lbfield1.setText("Название");
                lbfield2.setText("");
                lbfield3.setText("");
                field1.setVisible(true);
                field2.setVisible(false);
                field3.setVisible(false);
                field1.setText("");
                field3.setText("");
                break;
            case "Словарь лемм":
                lbfield1.setVisible(true);
                lbfield2.setVisible(true);
                lbfield3.setVisible(false);
                lbfield1.setText("Значение");
                lbfield2.setText("Категория");
                lbfield3.setText("");
                field1.setVisible(true);
                field2.setVisible(true);
                field3.setVisible(false);
                field1.setText("");
                field3.setText("");
                field2.setItems(FXCollections.observableArrayList(service.getCategoriesNameList()));
                break;
            default:
                lbfield1.setVisible(false);
                lbfield2.setVisible(false);
                lbfield3.setVisible(false);
                lbfield1.setText("");
                lbfield2.setText("");
                lbfield3.setText("");
                field1.setVisible(false);
                field2.setVisible(false);
                field3.setVisible(false);
                break;
        }
    }

    public void handleDelAction(ActionEvent event) throws RemoteException{
        fields.setVisible(false);
        table.getColumns().clear();
        delOKButt.setVisible(true);
        switch ((String) choice.getValue()) {
            case "Реклама":
                table.getColumns().addAll(pathCol,prioCol,categCol);
                adsList = service.getAdsList();
                table.setItems(FXCollections.observableArrayList(adsList));
                table.setVisible(true);
                break;
            case "Категории":
                table.getColumns().addAll(nameCol);
                categoriesList = service.getCategoriesList();
                table.setItems(FXCollections.observableArrayList(categoriesList));
                table.setVisible(true);
                break;
            case "Словарь лемм":
                table.getColumns().addAll(valueCol, catCol);
                wordsList = service.getWordsList();
                table.setItems(FXCollections.observableArrayList(wordsList));
                table.setVisible(true);
                break;
            default:

                break;
        }
    }

    public void handleAddOKAction(ActionEvent event) throws RemoteException{
        switch ((String) choice.getValue()) {
            case "Реклама":
                service.addAd(field1.getText(),Integer.parseInt(field3.getText()), field2.getValue());
                field1.setText("");
                field3.setText("");
                break;
            case "Категории":
                service.addCategory(field1.getText());
                field1.setText("");
                break;
            case "Словарь лемм":
                service.addWord(field1.getText(),field2.getValue());
                field1.setText("");
                break;
            default:

                break;
        }
    }

    public void handleDelOKAction(ActionEvent event) throws RemoteException{
        switch ((String) choice.getValue()) {
            case "Реклама":
                Ad ad = (Ad) table.getSelectionModel().getSelectedItem();
                service.deleteAd(ad.getId());
                adsList = service.getAdsList();
                table.setItems(FXCollections.observableArrayList(adsList));
                break;
            case "Категории":
                Category cat = (Category) table.getSelectionModel().getSelectedItem();
                service.deleteCategory(cat.getId());
                categoriesList = service.getCategoriesList();
                table.setItems(FXCollections.observableArrayList(categoriesList));
                break;
            case "Словарь лемм":
                Word word = (Word) table.getSelectionModel().getSelectedItem();
                service.deleteWord(word.getId());
                wordsList = service.getWordsList();
                table.setItems(FXCollections.observableArrayList(wordsList));
                break;
            default:

                break;
        }
    }
}
