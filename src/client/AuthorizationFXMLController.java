package client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import server.Authorization.Authorization;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AuthorizationFXMLController extends FXMLController implements Initializable {

    //объявление атрибутов
    //---------------------------------

    private Authorization authService;
    private FXMLController children;
    private FXMLController parent;
    private int idUserDB;

    @FXML
    AnchorPane authAP;
    @FXML
    Label title;
    @FXML
    Label nameLabel;
    @FXML
    Label secretQuestLabel;
    @FXML
    Label userGroupLabel;
    @FXML
    ToggleButton reg; //радиокнопка "Регистрция"
    @FXML
    ToggleButton auth; //радиокнопка "Авторизация"
    @FXML
    ToggleButton user; //радиокнопка "Пользователь"
    @FXML
    ToggleButton admin; //радиокнопка "Администратор"
    @FXML
    TextField userName; //поле "Имя пользователя"
    @FXML
    PasswordField password; //поле "Пароль"
    @FXML
    TextField name; //поле "Имя"
    @FXML
    TextField userGroup; //поле "Группа пользователей"
    @FXML
    TextField secretQuest; //поле "Секретный вопрос"
    @FXML
    Button submit; //кнопка "Ввод"

    ToggleGroup actionType = new ToggleGroup();
    ToggleGroup userType = new ToggleGroup();
    Tooltip userNameTooltip = new Tooltip();
    Tooltip passwordTooltip = new Tooltip();
    Tooltip nameTooltip = new Tooltip();
    Tooltip secretQuestTooltip = new Tooltip();

    //реализация методов
    //--------------------------------------------

    //необходимые геттеры
    @Override
    public FXMLController getChildren() {
        return children;
    }

    @Override
    public FXMLController getParent() {
        return parent;
    }

    public int getIdUserDB(){
        return idUserDB;
    }

    //необходимые сеттеры
    @Override
    public void setParent(FXMLController controller) {
        this.parent = controller;
    }

    @Override
    public void setId(int id) {
        this.idUserDB = id;
    }

    //общая инициализация
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            //подключение к интерфейсу авторизации сервера
            Registry registry1 = LocateRegistry.getRegistry(0);
            authService = (Authorization) registry1.lookup("Authorization");
            authService.reConnectToDB();
        }
        catch (RemoteException e) {
            Logger.getLogger(AuthorizationFXMLController.class.getName()).log(Level.SEVERE, null, e);
        }
        catch (NotBoundException e) {
            Logger.getLogger(AuthorizationFXMLController.class.getName()).log(Level.SEVERE, null, e);
        } catch (SQLException e) {
            Logger.getLogger(AuthorizationFXMLController.class.getName()).log(Level.SEVERE, null, e);
        }
        initNodes();
    }

    //инициализация узлов
    private void initNodes() {
        //задание начальных свойств узлов
        ContextualAd.primaryStage.setTitle("Окно авторизации");
        //формирование групп radiobutton
        reg.setToggleGroup(actionType);
        auth.setToggleGroup(actionType);
        user.setToggleGroup(userType);
        admin.setToggleGroup(userType);
        //определение текста radiobutton
        reg.setUserData(reg.getText());
        auth.setUserData(auth.getText());
        user.setUserData(auth.getText());
        admin.setUserData(auth.getText());
        //задание выбранных radiobutton
        auth.setSelected(true);
        user.setSelected(true);
        //задание видимости узлов
        name.setVisible(false);
        nameLabel.setVisible(false);
        secretQuest.setVisible(false);
        secretQuestLabel.setVisible(false);
        userGroup.setVisible(false);
        userGroupLabel.setVisible(false);
        //задание текстов подсказок
        userNameTooltip.setText("Введите логин, чтобы авторизоваться");
        passwordTooltip.setText("Введите пароль, чтобы авторизоваться");
        nameTooltip.setText("Введите ваше имя, чтобы зарегистрироваться");
        secretQuestTooltip.setText("Введите фамилию разработчика для идентификации личности администратора");
        //задание соответствия подсказок и узлов
        userName.setTooltip(userNameTooltip);
        password.setTooltip(passwordTooltip);
        name.setTooltip(nameTooltip);
        secretQuest.setTooltip(secretQuestTooltip);
        //задание текста для узлов
        submit.setText("Вход");

        //задание методов нажатия на кнопки (визуальная часть)
        //------------------------------------------

        //нажатие на кнопку "Регистрация"
        reg.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                title.setText(reg.getUserData().toString());
                name.setVisible(true);
                nameLabel.setVisible(true);
                if (admin.isSelected()) {
                    secretQuest.setVisible(true);
                    secretQuestLabel.setVisible(true);
                }
                if (user.isSelected()) {
                    userGroup.setVisible(true);
                    userGroupLabel.setVisible(true);
                }
                userNameTooltip.setText("Введите логин, чтобы зарегистрироваться");
                passwordTooltip.setText("Введите пароль, чтобы зарегистрироваться");
                submit.setText("Создать");
            }
        });

        //нажатие на кнопку "Авторизация"
        auth.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                title.setText(auth.getUserData().toString());
                name.setVisible(false);
                nameLabel.setVisible(false);
                secretQuest.setVisible(false);
                secretQuestLabel.setVisible(false);
                userGroup.setVisible(false);
                userGroupLabel.setVisible(false);
                submit.setText("Вход");
            }
        });

        //нажатие на кнопку "Пользователь"
        user.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (reg.isSelected()) {
                    secretQuest.setVisible(false);
                    secretQuestLabel.setVisible(false);
                    userGroup.setVisible(true);
                    userGroupLabel.setVisible(true);
                }
            }
        });

        //нажатие на кнопку "Администратор"
        admin.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (reg.isSelected()) {
                    secretQuest.setVisible(true);
                    secretQuestLabel.setVisible(true);
                    userGroup.setVisible(false);
                    userGroupLabel.setVisible(false);
                }
            }
        });
    }

    //обработка нажатия на кнопку подтверждения
    @FXML
    private void handleSubmitButtonAction(ActionEvent event) throws IOException,SQLException {
        //задание границ полей
        userName.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
        password.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
        name.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
        secretQuest.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
        userGroup.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));

        if (user.isSelected()) {
            //пользователь

            //проверка
            boolean validationFlag = userValidation();

            if(auth.isSelected() && validationFlag){
                //авторизация
                idUserDB = authService.checkAuthorizationInformation("user",userName.getText(),password.getText());
                if(idUserDB!=0) {
                    //успешно
//                    authService.closeConnectionDB();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Информация");
                    alert.setContentText("Авторизация пользователя прошла успешно!");
                    alert.setHeaderText(null);
                    alert.show();
                    showUserScreen(idUserDB);
                }
                else{
                    //неудачно
                    userName.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
                    password.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
                }
            }
            if(reg.isSelected() && validationFlag){
                //регистрация
                authService.writeRegistrationInformation("user",userName.getText(),password.getText(),name.getText(),userGroup.getText());
//                authService.closeConnectionDB();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Информация");
                alert.setContentText("Регистрация пользователя прошла успешно!");
                alert.setHeaderText(null);
                alert.show();
                userName.clear();
                password.clear();
                name.clear();
                userGroup.clear();
            }
        }
        else {
            //администратор

            //проверка
            boolean validationFlag = adminValidation();

            if(auth.isSelected() && validationFlag){
                //авторизация
                idUserDB = authService.checkAuthorizationInformation("admin",userName.getText(),password.getText());
                //if(service1.checkAuthorizationInformation("admin",userName.getText(),password.getText())) {
                if(idUserDB!=0){
                    //успешно
//                    authService.closeConnectionDB();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Информация");
                    alert.setContentText("Авторизация администратора прошла успешно!");
                    alert.setHeaderText(null);
                    alert.show();
                    showAdminScreen(idUserDB);
                }
                else{
                    //неудачно
                    userName.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
                    password.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
                }
            }
            if(reg.isSelected() && validationFlag){
                //регистрация
                authService.writeRegistrationInformation("admin",userName.getText(),password.getText(),name.getText(),"admins");
//                authService.closeConnectionDB();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Информация");
                alert.setContentText("Регистрация администратора прошла успешно!");
                alert.setHeaderText(null);
                alert.show();
                userName.clear();
                password.clear();
                name.clear();
                secretQuest.clear();
            }
        }
    }

    //открытие окна работы пользователя
    private void showUserScreen(int id) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("userScreen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("client/style.css");
        ContextualAd.primaryStage.setScene(scene);
        ContextualAd.primaryStage.setTitle("Окно работы пользователя");

        authService.closeConnections();

        children = loader.getController();
        children.setParent(this);
        children.setId(id);
        ContextualAd.primaryStage.show();
    }

    //открытие окна работы администратора
    private void showAdminScreen(int id) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("adminScreen.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("client/style.css");
        ContextualAd.primaryStage.setScene(scene);
        ContextualAd.primaryStage.setTitle("Окно работы администратора");

        authService.closeConnections();

        children = loader.getController();
        children.setParent(this);
        children.setId(id);
        ContextualAd.primaryStage.show();
    }

    //проверки
    private boolean userValidation() throws RemoteException{
        if (!authService.validationUserName(userName.getText())) {
            userName.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
            return false;
        }
        if (!authService.validationPassword(password.getText())) {
            password.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
            return false;
        }
        if (reg.isSelected() && !authService.validationName(name.getText())) {
            name.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
            return false;
        }
        if (reg.isSelected() && !authService.validationUserGroup(userGroup.getText())) {
            userGroup.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
            return false;
        }
        return true;
    }

    private boolean adminValidation() throws RemoteException{
        if (!authService.validationUserName(userName.getText())) {
            userName.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
            return false;
        }
        if (!authService.validationPassword(password.getText())) {
            password.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
            return false;
        }
        if (reg.isSelected() && !authService.validationName(name.getText())) {
            name.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
            return false;
        }
        if (reg.isSelected() && !authService.checkSecretQuest(secretQuest.getText())) {
            secretQuest.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1.5))));
            return false;
        }
        return true;
    }
}
