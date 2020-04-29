package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

public class HelpFXMLController implements Initializable {
    @FXML
    ToggleButton aboutProgramButt;
    @FXML
    ToggleButton aboutAuthorsButt;
    @FXML
    Label helpLabel;
    private String aboutPr = "Данная система разработана в рамках выполнения\n" +
            "выпускной квалификационной работы бакалавра\n" +
            "Шлыкова Владислава Александровича на тему\n" +
            "\"Разработака автоматизированной системы\n" +
            "формирования контекстной рекламы на основе\n" +
            "анализа поисковых запросов\".\n" +
            "В данной системе пользователи делятся на 2 вида:\n" +
            "пользователь и администратор. Администратор\n" +
            "может просматривать историю предоставления\n" +
            "пользователям рекламных предложений, а также\n" +
            "добавлять и удалять информацию в базу данных.\n" +
            "Пользователь может проанализировать свою сетевую\n" +
            "активность автоматически, система возьмет адреса\n" +
            "веб-страниц из истории браузера, или, введя\n" +
            "адреса в текстовые поля. Система предоставит\n" +
            "пользователю рекламное предложение в виде\n" +
            "изображения, которое максимально подходит под\n" +
            "поисковой запрос и содержание веб-страниц.\n" +
            "Пользователь может повторить проверку, нажав на\n" +
            "кнопку с закругленной стрелкой.\n" +
            "Удачи в использовании системы!";
    private String aboutAuth = "Данная система разработана студентом группы\n" +
            "6413-020302D Самарского национального\n" +
            "исследовательского университета имени\n" +
            "академика С.П. Королева\n"+
            "Шлыковым Владиславом Александровичем.";
    private ToggleGroup tg;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tg = new ToggleGroup();
        aboutAuthorsButt.setToggleGroup(tg);
        aboutProgramButt.setToggleGroup(tg);
        aboutProgramButt.setSelected(true);
        helpLabel.setText(aboutPr);
    }

    public void handleAboutProgramButton(ActionEvent event) {
        helpLabel.setText(aboutPr);
    }

    public void handleAboutAuthorsButton(ActionEvent event) {
        helpLabel.setText(aboutAuth);
    }
}
