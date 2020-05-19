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
                             "выпускной квалификационной работы магистра\n" +
                             "Шлыкова Владислава Александровича на тему\n" +
                             "\"Разработака автоматизированной системы\n" +
                             "формирования контекстной рекламы на основе\n" +
                             "анализа сетевой активности пользователей\".\n\n" +

                             "Данная программная система предназначена для\n" +
                             "формирования контекстной рекламы на основе сетевой\n" +
                             "активности групп пользователей. Система поможет\n" +
                             "рекламным агентствам оптимизировать свои действия \n" +
                             "по настройке и построению рекламных кампаний.\n\n" +

                             "В данной системе пользователи делятся на 2 вида:\n" +
                             "пользователи и администраторы.\n" +
                             "Администратор может просматривать и изменять\n" +
                             "информацию в базе данных\n" +
                             "Пользователь может запустить процесс анализа\n" +
                             "сетевой активности, система предоставит\n" +
                             "пользователю рекламное предложение в виде\n" +
                             "изображения, которое будет максимально подходить\n" +
                             "под интересы группы пользователей,\n" +
                             "в которой он состоит.\n\n" +
                             "Удачи в использовании системы!";
    private String aboutAuth = "Данная система разработана студентом группы\n" +
                               "6238-020402D Самарского национального\n" +
                               "исследовательского университета имени\n" +
                               "академика С.П. Королева\n"+
                               "Шлыковым Владиславом Александровичем.\n";
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
