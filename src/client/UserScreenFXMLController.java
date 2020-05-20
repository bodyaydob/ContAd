package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import server.AnalysisText.Analysis;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserScreenFXMLController extends FXMLController implements Initializable {

    //объвление атрибутов
    //------------------------------------

    private FXMLController children;
    private RateScreenFXMLController childrenRate;
    private FXMLController parent;
    private ArrayList<String> words;
    private Analysis analysisService;
    private int countPages;
    private int idUserDB;
    private ToggleGroup tg = new ToggleGroup();
    private boolean flagTest = false;
    private String[] urls;
    private int rate;
    private boolean clickOnAd;

    @FXML
    TextArea textTA1;
    @FXML
    TextArea textTA2;
    @FXML
    Label result;
    @FXML
    Button plusButt;
    @FXML
    Label nameUser;
    @FXML
    ImageView ad;
    @FXML
    Button testButt;
    @FXML
    Spinner<Integer> spinnerCntPages = new Spinner<>();
    @FXML
    Button retryButt;
    @FXML
    Button helpButt;
    @FXML
    Button analysisButt;
    @FXML
    Label countPagesLabel;
    @FXML
    Spinner<Integer> spinnerStopWord = new Spinner<>();

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

    //необходимые сеттеры
    @Override
    public void setParent(FXMLController controller) {
        this.parent = controller;
    }

    @Override
    public void setId(int id) throws RemoteException {
        this.idUserDB = id;
        //задание надписи имени пользователя
        nameUser.setText(analysisService.getNameUser(idUserDB));
    }

    public void setRate(int rate){ this.rate = rate; }

    public void setClickOnAd(boolean clickOnAd){ this.clickOnAd = clickOnAd; }

    //общая инициализация
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            //подключение к интерфейсу анализа сервера
            Registry registry = LocateRegistry.getRegistry(0);
            analysisService = (Analysis) registry.lookup("Analysis");
            analysisService.reConnectToDB();
        } catch (RemoteException e) {
            Logger.getLogger(AuthorizationFXMLController.class.getName()).log(Level.SEVERE, null, e);
        } catch (NotBoundException e) {
            Logger.getLogger(AuthorizationFXMLController.class.getName()).log(Level.SEVERE, null, e);
        } catch (SQLException e) {
            Logger.getLogger(AuthorizationFXMLController.class.getName()).log(Level.SEVERE, null, e);
        }
        words = new ArrayList<String>();
        initNodes();
    }

    //инициализация узлов
    private void initNodes() {
        //задание видимости/активности узлов
        plusButt.setVisible(false);
        textTA1.setVisible(false);
        textTA2.setVisible(false);
        retryButt.setDisable(true);

        //инициализация спинера кол-ва стопслов
        int initStopWordsValue = 2;
        SpinnerValueFactory<Integer> valueFactoryStoppWords = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,5, initStopWordsValue);
        spinnerStopWord.setValueFactory(valueFactoryStoppWords);
        spinnerStopWord.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);

        //инициализация спинера кол-ва стопслов
        int initCntPagesValue = 2;
        SpinnerValueFactory<Integer> valueFactoryCntPages = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,10, initCntPagesValue);
        spinnerCntPages.setValueFactory(valueFactoryCntPages);
        spinnerCntPages.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
    }

    //обработка нажатия на кнопку "Анализ"
    public void handleSubmitButton(ActionEvent event) throws IOException, SQLException, InterruptedException {
        //задание начальных свойств
        retryButt.setDisable(false);
        analysisButt.setDisable(true);
        String text = null;
        if (flagTest == false)
            countPages = spinnerCntPages.getValue();
        Document[] docs = new Document[countPages];
        urls = new String[countPages];

        if (flagTest) {
            plusButt.setDisable(true);
            urls[0] = textTA1.getText();
            if (countPages == 2)
                urls[1] = textTA2.getText();
        }
        else
            //получение последних ссылок на веб-страницы
            this.urls = analysisService.getURLS(countPages);
        try {
            //выгрузка содержимого веб-страниц
            for(int i = 0; i<countPages; i++) {
                docs[i] = Jsoup.connect(urls[i]).get();
                //получение текста из выгруженных документов
                text += docs[i].getElementsByTag("p").text() + ' ' + docs[i].getElementsByTag("h1").text() + ' ' + docs[i].getElementsByTag("h2").text();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //парсинг полученного текста, получение списка лемм
        words = analysisService.getLemmas(text, spinnerStopWord.getValue());
        //получение кол-ва слов по категориям
        int[] countWords = analysisService.getCntCatsOfLemm(words);
        //логирование кол-ва слов по категориям
        System.out.println("ИСКУССТВО\n" + countWords[0] + " слов");
        System.out.println("\nЭЛЕКТРОНИКА\n" + countWords[1] + " слов");
        System.out.println("\nНАУКА\n" + countWords[2] + " слов");
        System.out.println("\nСПОРТ\n" + countWords[3] + " слов");
        System.out.println("\nАВТОМОБИЛИ\n" + countWords[4] + " слов");
        System.out.println("\nКУХНЯ\n" + countWords[5] + " слов");
        System.out.println("\nРЕЛИГИЯ\n" + countWords[6] + " слов");
        System.out.println("\nМАРКЕТИНГ\n" + countWords[7] + " слов");
        System.out.println("\nМЕДИЦИНА\n" + countWords[8] + " слов");

        //добавить в БД значения по кол-ву слов в каждой категории для группы
        countWords = analysisService.addGroupsCatDataToDB(idUserDB, countWords);
        //вычисление "главной" категории
        String category = analysisService.getMaxCat(countWords);
        result.setText(category);
        //получение изображения КР
        Class<?> clazz = this.getClass();
        String imagePath = analysisService.getImage(category);
        InputStream input = clazz.getResourceAsStream(imagePath);
        Image image = new Image(input);
        //вывод изображения КР
        ad.setImage(image);
        int adId = analysisService.getAdId(imagePath);
        //оценка пользователя
        showRateScreen();
        //запись истории
        analysisService.writeHistory(idUserDB, adId, urls, rate, clickOnAd);

    }

    //обработка нажатия на кнопку "+"
    public void handleAddButton(ActionEvent event) {
        //кол-во обрабатываемых веб-страниц увеличивается до 2ух
        textTA2.setVisible(true);
        countPages = 2;
        plusButt.setDisable(true);
    }

    //обработка нажатия на кнопку "Выйти"
    public void handleLogoutAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("authorization.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("client/style.css");
        ContextualAd.primaryStage.setScene(scene);
        ContextualAd.primaryStage.setTitle("Окно авторизации");

        analysisService.closeConnections();

        children = loader.getController();
        children.setParent(this);
        ContextualAd.primaryStage.show();
    }

    //обработка нажатия на кнопку "Тест"
    public void handleTestButtonAction(ActionEvent event) {
        //отображение полей для ввода веб-страниц
        spinnerCntPages.setVisible(false);
        countPagesLabel.setVisible(false);
        testButt.setVisible(false);
        plusButt.setVisible(true);
        textTA1.setVisible(true);
        flagTest = true;
        countPages = 1;
    }

    //обработка нажатия на кнопку "Повторить"
    public void handleRetryButton(ActionEvent event) {
        analysisButt.setDisable(false);
        retryButt.setDisable(true);
        ad.setImage(null);
        result.setText("Результат :");
        if(flagTest) {
            testButt.setVisible(true);
            spinnerCntPages.setVisible(true);
            countPagesLabel.setVisible(true);
            plusButt.setDisable(false);
            plusButt.setVisible(false);
            textTA1.setVisible(false);
            textTA2.setVisible(false);
            textTA1.setText("");
            textTA2.setText("");
        }
        flagTest = false;
        urls = null;
    }

    //обработка нажатия на кнопку "Помощь"
    public void handleHelpButton(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("help.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("client/style.css");
        Stage helpStage = new Stage();
        helpStage.setScene(scene);
        helpStage.setTitle("Справочная информация");
        helpStage.show();
    }

    //
    private void showRateScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("rateScreen.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("client/style.css");
        Stage rateStage = new Stage();
        rateStage.setScene(scene);
        rateStage.setTitle("Оценка рекламного предложения");

        childrenRate = loader.getController();
        childrenRate.setParent(this);
        childrenRate.setNameUser(nameUser.getText());

        rateStage.showAndWait();
    }


}
