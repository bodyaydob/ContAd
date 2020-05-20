package server.AnalysisText;

import org.apache.lucene.morphology.russian.RussianMorphology;
import server.Authorization.AuthorizationImpl;
import server.ControlDB.DBControlImpl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnalysisImpl implements Analysis {
    //атрибуты
    //----------------------------------------------
    DBControlImpl dbci = new DBControlImpl(2);

    //реализация методов
    //----------------------------------------------

    //получение списка лемм
    @Override
    public ArrayList<String> getLemmas(String text, int maxLengthStopWord) throws RemoteException {
        String[] arrayWords;
        ArrayList<String> words = new ArrayList<String>();
        ArrayList<String> result = new ArrayList<String>();
        String[] strings = text.split("\n");
        for (String string : strings) {
            string = string.replaceAll("\\p{M}", "");
            string = string.toLowerCase();
            string = string.replace('ё', 'е');
            arrayWords = string.split("[\\s\\p{P}\\d\\w\\p{S}\\p{Z}\\p{C}]");
            for (String word : arrayWords)
                if (word.length() > maxLengthStopWord && word.matches("[а-яё]*"))
                    words.add(word.toLowerCase());
        }
        try {
            RussianMorphology russianMorphology = new RussianMorphology();
            for (String word : words) {
                result.add(russianMorphology.getNormalForms(word).get(0));
            }
        } catch (IOException e) {

        }
        return result;
    }

    //получение количества принадлежностей лемм к категориям
    @Override
    public int[] getCntCatsOfLemm(ArrayList<String> lemmas) throws RemoteException, SQLException {
        int[] result = new int[9];
        ResultSet resultRS = null;
        for (String lemma : lemmas) {
            resultRS = dbci.getWord(lemma);
            while (resultRS.next()) {
                System.out.println(resultRS.getInt("id_cat") + "\n" + resultRS.getString("name"));
                result[resultRS.getInt("id_cat") - 1]++;
            }
        }
        return result;
    }

    //получение имени пользователя
    @Override
    public String getNameUser(int id) throws RemoteException {
        return dbci.getNameUser(id);
    }

    //получение наибоолеевстречающейся категории
    @Override
    public String getMaxCat(int[] countWords) throws RemoteException {
        int max = 0;
        int pos = 0;
        for (int i = 0; i < countWords.length; i++) {
            if (max < countWords[i]) {
                max = countWords[i];
                pos = i;
            }
        }
        switch (pos) {
            case 0:
                return "Искусство";
            case 1:
                return "Электроника";
            case 2:
                return "Наука";
            case 3:
                return "Спорт";
            case 4:
                return "Автомобили";
            case 5:
                return "Кухня";
            case 6:
                return "Религия";
            case 7:
                return "Маркетинг";
            case 8:
                return "Медицина";
            default:
                return "пустая";
        }
    }

    //получение пути до рекламного предложения
    @Override
    public String getImage(String nameCat) throws RemoteException {
        ResultSet resultRS = null;
        String result = null;
        int i = 0;
        Random random = new Random();
        int priority ;
        double a = Math.random();
        if (a<=0.4)
            priority = 1;
        else{
            if(a<=0.75)
                priority = 2;
            else priority = 3;
        }
        try {
            resultRS = dbci.getPathAd(nameCat, priority);
            while (resultRS.next()) {
                i++;
            }
            i = random.nextInt(i);
            resultRS.absolute(i+1);

            result = resultRS.getString("path");
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //запись истории
    @Override
    public void writeHistory(int userId, int adId, String[] urls, int rate, boolean click) throws RemoteException {
        int idAd = 0;
        try {
            dbci.insertHistory(userId, adId, urls, rate, click);
            dbci.updateAd(adId, rate);
        }
        catch (SQLException e) {
            Logger.getLogger(AuthorizationImpl.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //получение ID рекламного предложения
    @Override
    public int getAdId(String adPath) throws RemoteException {
        ResultSet resultRS = null;
        int result = 0;
        try {
            resultRS = dbci.getAdId(adPath);
            while (resultRS.next())
                result = resultRS.getInt("id_ad");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //получение списка адресов посещенных веб-страниц
    @Override
    public String[] getURLS(int count) throws RemoteException {
        String[] result = new String[count];
        ResultSet resultSet = null;
        int id = 0;
        try {
            for(int i = 0; i < count; i++){
                resultSet = dbci.getURLFromBrowser();
                while (resultSet.next ()){
                    result[i] = resultSet.getString("url") + " ";
                    id = resultSet.getInt("id");
                }
                System.out.println(result[i]);
                dbci.deleteBrowserHistory(id);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }

        finally {
            try {
                resultSet.close ();
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }
        return result;
    }

    //закрытие соединений с БД
    @Override
    public void closeConnections() throws RemoteException {
        dbci.closeConnects();
    }

    //переподключение к БД
    @Override
    public void reConnectToDB() throws RemoteException, SQLException {
        dbci.reCreateConnections();
    }

    //добавление групповых данных по категориям в БД
    @Override
    public int[] addGroupsCatDataToDB(int idUser, int[] catData) throws RemoteException, SQLException{
        //получение ID группы пользователей
        int usrGrp = dbci.getUserGroupIDByUser(idUser);
        int catID = 1;
        int oldCatData = 0;
        for(int cntWrd : catData){
            oldCatData = dbci.getGrpCatData(usrGrp, catID);
            cntWrd += oldCatData;
            dbci.addGrpCatData(usrGrp, catID, cntWrd);
            catData[catID-1] = cntWrd;
            catID++;
        }
        return catData;
    }
}

