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
import org.sqlite.core.DB;
import server.ControlDB.DBControl;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
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
    private DBControl DBConstrolService;
    private String oldUserGroup;

    TableColumn<History, String> userCol;
    TableColumn<History, String> userGrpCol;
    TableColumn<History, String> adCol;
    TableColumn<History, String> urlsCol;
    TableColumn<History, String> rateCol;
    TableColumn<History, String> clickCol;

    TableColumn<Category,String> nameCol;

    TableColumn<Word,String> valueCol;
    TableColumn<Word,String> catCol;

    TableColumn<Ad,String> pathCol;
    TableColumn<Ad,Integer> prioCol;
    TableColumn<Ad,String> categCol;
    TableColumn<Ad,String> avRateCol;
    TableColumn<Ad,String> displCntCol;


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
            DBConstrolService = (DBControl) registry.lookup("DBControl");
            DBConstrolService.reCreateConnections();
        } catch (RemoteException e) {
            Logger.getLogger(AuthorizationFXMLController.class.getName()).log(Level.SEVERE, null, e);
        } catch (NotBoundException e) {
            Logger.getLogger(AuthorizationFXMLController.class.getName()).log(Level.SEVERE, null, e);
        } catch (SQLException e) {
            Logger.getLogger(AuthorizationFXMLController.class.getName()).log(Level.SEVERE, null, e);
        }
        initNodes();

        userCol = new TableColumn<History, String>("Пользователь");
        userGrpCol = new TableColumn<History, String>("Группа пользователя");
        adCol = new TableColumn<History, String>("Реклама");
        urlsCol = new TableColumn<History, String>("Веб-запросы");
        rateCol = new TableColumn<History, String>("Оценка");
        clickCol = new TableColumn<History, String>("Переход на рекламу");

        nameCol = new TableColumn<Category,String>("Имя");

        valueCol = new TableColumn<Word,String>("Слово");
        catCol = new TableColumn<Word,String>("Категория");

        pathCol = new TableColumn<Ad,String>("Путь к файлу");
        prioCol = new TableColumn<Ad,Integer>("Приоритет");
        categCol = new TableColumn<Ad,String>("Категория");
        avRateCol = new TableColumn<Ad,String>("Средняя оценка");
        displCntCol = new TableColumn<Ad,String>("Кол-во показов");

        userCol.setCellValueFactory(new PropertyValueFactory<>("user"));
        userGrpCol.setCellValueFactory(new PropertyValueFactory<>("userGroup"));
        adCol.setCellValueFactory(new PropertyValueFactory<>("ad"));
        urlsCol.setCellValueFactory(new PropertyValueFactory<>("urls"));
        rateCol.setCellValueFactory(new PropertyValueFactory<>("rate"));
        clickCol.setCellValueFactory(new PropertyValueFactory<>("click"));

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        pathCol.setCellValueFactory(new PropertyValueFactory<>("path"));
        prioCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        categCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        avRateCol.setCellValueFactory(new PropertyValueFactory<>("avRate"));
        displCntCol.setCellValueFactory(new PropertyValueFactory<>("displCnt"));
    }

    private void initNodes() {
        choiceList = FXCollections.observableArrayList("...Хочу подумать...", "Реклама", "Словарь лемм", "Категории", "История", "Пользователи");
        choice.setItems(choiceList);
        tg = new ToggleGroup();
        addToggleBut.setToggleGroup(tg);
        delToggleBut.setToggleGroup(tg);
        addToggleBut.setSelected(true);
        fields.setVisible(false);
        delOKButt.setVisible(false);
    }

    public void handleChoiceBoxAction(ActionEvent event) throws RemoteException {
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
                table.getColumns().addAll(userCol, userGrpCol, adCol, urlsCol, rateCol, clickCol);
                historyList = DBConstrolService.getHistoryList();
                table.setItems(FXCollections.observableArrayList(historyList));
                table.setVisible(true);

            }
            else if (choice.getValue().equals("Пользователи")){
                addToggleBut.setVisible(false);
                delToggleBut.setVisible(false);
                table.setVisible(false);
                delOKButt.setVisible(false);
                drawField();
            }
            else {
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
    public void setId(int id) throws RemoteException {
        this.idUserDB = id;
        nameUser.setText(DBConstrolService.getNameUser(idUserDB));
    }

    public void handleLogoutAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("authorization.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("client/style.css");
        ContextualAd.primaryStage.setScene(scene);
        ContextualAd.primaryStage.setTitle("Окно авторизации");

        DBConstrolService.closeConnects();

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
                field2.setItems(FXCollections.observableArrayList(DBConstrolService.getCategoriesNameList()));
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
                field2.setItems(FXCollections.observableArrayList(DBConstrolService.getCategoriesNameList()));
                break;
            case "Пользователи":
                lbfield1.setVisible(true);
                lbfield2.setVisible(true);
                lbfield3.setVisible(false);
                lbfield1.setText("Группа пользователей");
                lbfield2.setText("Пользователь");
                lbfield3.setText("");
                field1.setVisible(true);
                field2.setVisible(true);
                field3.setVisible(false);
                field1.setText("");
                field3.setText("");
                field2.setItems(FXCollections.observableArrayList(DBConstrolService.getUsernameList()));
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
                table.getColumns().addAll(pathCol, prioCol, categCol, avRateCol, displCntCol);
                adsList = DBConstrolService.getAdsList();
                table.setItems(FXCollections.observableArrayList(adsList));
                table.setVisible(true);
                break;
            case "Категории":
                table.getColumns().addAll(nameCol);
                categoriesList = DBConstrolService.getCategoriesList();
                table.setItems(FXCollections.observableArrayList(categoriesList));
                table.setVisible(true);
                break;
            case "Словарь лемм":
                table.getColumns().addAll(valueCol, catCol);
                wordsList = DBConstrolService.getWordsList();
                table.setItems(FXCollections.observableArrayList(wordsList));
                table.setVisible(true);
                break;
            default:

                break;
        }
    }

    public void handleAddOKAction(ActionEvent event) throws RemoteException, SQLException {
        switch ((String) choice.getValue()) {
            case "Реклама":
                DBConstrolService.addAd(field1.getText(),Integer.parseInt(field3.getText()), field2.getValue());
                field1.setText("");
                field3.setText("");
                break;
            case "Категории":
                DBConstrolService.addCategory(field1.getText());
                field1.setText("");
                break;
            case "Словарь лемм":
                DBConstrolService.addWord(field1.getText(),field2.getValue());
                field1.setText("");
                break;
            case "Пользователи":
                if (oldUserGroup != field1.getText())
                    DBConstrolService.updateUserGroup(field2.getValue(), field1.getText());
            default:

                break;
        }
    }

    public void handleDelOKAction(ActionEvent event) throws RemoteException{
        switch ((String) choice.getValue()) {
            case "Реклама":
                Ad ad = (Ad) table.getSelectionModel().getSelectedItem();
                DBConstrolService.deleteAd(ad.getId());
                adsList = DBConstrolService.getAdsList();
                table.setItems(FXCollections.observableArrayList(adsList));
                break;
            case "Категории":
                Category cat = (Category) table.getSelectionModel().getSelectedItem();
                DBConstrolService.deleteCategory(cat.getId());
                categoriesList = DBConstrolService.getCategoriesList();
                table.setItems(FXCollections.observableArrayList(categoriesList));
                break;
            case "Словарь лемм":
                Word word = (Word) table.getSelectionModel().getSelectedItem();
                DBConstrolService.deleteWord(word.getId());
                wordsList = DBConstrolService.getWordsList();
                table.setItems(FXCollections.observableArrayList(wordsList));
                break;
            default:

                break;
        }
    }

    public void handleChoiceFieldAction(ActionEvent actionEvent) throws RemoteException, SQLException {
        oldUserGroup = field1.getText();
        field1.setText(DBConstrolService.getUserGroupNameByUsername(field2.getValue()));
    }
}
