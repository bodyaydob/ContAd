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
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserScreenFXMLController extends FXMLController implements Initializable {

    //объвление атрибутов
    //------------------------------------

    private FXMLController children;
    private FXMLController parent;
    private ArrayList<String> words;
    private Analysis analysisService;
    private int countPages = 1;
    private int idUserDB;
    private ToggleGroup tg = new ToggleGroup();
    private boolean flagTest = false;
    private String url1="";
    private String url2="";

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
    ToggleButton onePageButt;
    @FXML
    ToggleButton twoPageButt;
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
    public void setId(int id) {
        this.idUserDB = id;
    }

    //общая инициализация
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            //подключение к интерфейсу анализа сервера
            Registry registry = LocateRegistry.getRegistry(0);
            analysisService = (Analysis) registry.lookup("Analysis");
        } catch (RemoteException e) {
            Logger.getLogger(AuthorizationFXMLController.class.getName()).log(Level.SEVERE, null, e);
        } catch (NotBoundException e) {
            Logger.getLogger(AuthorizationFXMLController.class.getName()).log(Level.SEVERE, null, e);
        }
        words = new ArrayList<String>();
        initNodes();
    }

    //инициализация узлов
    private void initNodes() {
        //задание групп radiobutton
        onePageButt.setToggleGroup(tg);
        twoPageButt.setToggleGroup(tg);
        onePageButt.setSelected(true);

        //задание видимости/активности узлов
        plusButt.setVisible(false);
        textTA1.setVisible(false);
        textTA2.setVisible(false);
        retryButt.setDisable(true);

        //инициализация спинера кол-ва стопслов
        int initStopWordsValue = 2;
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,5, initStopWordsValue);
        spinnerStopWord.setValueFactory(valueFactory);
        spinnerStopWord.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
    }

    //обработка нажатия на кнопку "Анализ"
    public void handleSubmitButton(ActionEvent event) throws RemoteException, SQLException {
        nameUser.setText(analysisService.getNameUser(idUserDB));
        retryButt.setDisable(false);
        analysisButt.setDisable(true);
        analysisService.closeConnectionDB();
        String text = null;
        Document doc1 = null;
        Document doc2 = null;
        if (flagTest) {
            plusButt.setDisable(true);
            url1 = textTA1.getText();
            if (countPages == 2)
                url2 = textTA2.getText();
        }
        else {
            Object[] urls = analysisService.getURLS(2);
            url1 = urls[1].toString();
            if (countPages == 2)
                url2 = urls[2].toString();
        }
        try {
            doc1 = Jsoup.connect(url1).get();
            if (countPages == 2)
                doc2 = Jsoup.connect(url2).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc1 != null) {
            text = doc1.getElementsByTag("p").text() + ' ' + doc1.getElementsByTag("h1").text() + ' ' + doc1.getElementsByTag("h2").text();
            if (countPages == 2)
                text += "\n" + doc2.getElementsByTag("p").text() + ' ' + doc2.getElementsByTag("h1").text() + ' ' + doc2.getElementsByTag("h2").text();
        }
        words = analysisService.getLemmas(text, spinnerStopWord.getValue());
        int[] countWords = analysisService.getCntCatsOfLemm(words);
        analysisService.closeConnectionDB();
        System.out.println("ИСКУССТВО\n" + countWords[0] + " слов");
        System.out.println("\nЭЛЕКТРОНИКА\n" + countWords[1] + " слов");
        System.out.println("\nНАУКА\n" + countWords[2] + " слов");
        System.out.println("\nСПОРТ\n" + countWords[3] + " слов");
        System.out.println("\nАВТОМОБИЛИ\n" + countWords[4] + " слов");
        System.out.println("\nКУХНЯ\n" + countWords[5] + " слов");
        System.out.println("\nРЕЛИГИЯ\n" + countWords[6] + " слов");
        System.out.println("\nМАРКЕТИНГ\n" + countWords[7] + " слов");
        System.out.println("\nМЕДИЦИНА\n" + countWords[8] + " слов");
        //вычисление максимума
        String category = analysisService.getMaxCat(countWords);
        result.setText(category);
        Class<?> clazz = this.getClass();
        String imagePath = analysisService.getImage(category);
        analysisService.closeConnectionDB();
        InputStream input = clazz.getResourceAsStream(imagePath);
        Image image = new Image(input);
        ad.setImage(image);
        int adId = analysisService.getAdId(imagePath);
        analysisService.closeConnectionDB();
        analysisService.writeHistory(idUserDB, adId, url1, url2);
        analysisService.closeConnectionDB();

    }

    //обработка нажатия на кнопку "+"
    public void handleAddButton(ActionEvent event) {
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

        children = loader.getController();
        children.setParent(this);
        ContextualAd.primaryStage.show();
    }

    //обработка нажатия на кнопку "Тест"
    public void handleTestButtonAction(ActionEvent event) {
        onePageButt.setVisible(false);
        twoPageButt.setVisible(false);
        countPagesLabel.setVisible(false);
        testButt.setVisible(false);
        plusButt.setVisible(true);
        textTA1.setVisible(true);
        flagTest = true;
    }

    //обработка нажатия на кнопку "1" (1 обрабатываемая страница)
    public void handleOnePageButtonAction(ActionEvent event) {
        countPages = 1;
    }

    //обработка нажатия на кнопку "2" (2 обрабатываемые страницы)
    public void handleTwoPageButtonAction(ActionEvent event) {
        countPages = 2;
    }

    //обработка нажатия на кнопку "Повторить"
    public void handleRetryButton(ActionEvent event) {
        analysisButt.setDisable(false);
        retryButt.setDisable(true);
        ad.setImage(null);
        result.setText("Результат :");
        if(flagTest) {
            testButt.setVisible(true);
            onePageButt.setVisible(true);
            twoPageButt.setVisible(true);
            countPagesLabel.setVisible(true);
            plusButt.setDisable(false);
            plusButt.setVisible(false);
            textTA1.setVisible(false);
            textTA2.setVisible(false);
            textTA1.setText("");
            textTA2.setText("");
        }
        flagTest = false;
        onePageButt.setSelected(true);
        countPages = 1;
        url1="";
        url2="";
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
}
