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

//    Connection connection;
    DBControlImpl dbci = new DBControlImpl();

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
        /*for (int i : result)
            i = 0;*/
        ResultSet resultRS = null;
//        connection = dbci.connectPostgre();
        for (String lemma : lemmas) {
            resultRS = dbci.getWord(lemma);
            while (resultRS.next()) {
                System.out.println(resultRS.getInt("id_cat") + "\n" + resultRS.getString("value"));
                result[resultRS.getInt("id_cat") - 1]++;
            }
        }
        return result;
    }

    //закрытие соединения
    @Override
    public void closeConnectionDB() throws RemoteException {
//        dbci.closeConnect(connection);
//        TODO: 2. организовать соединение с БД в начале работы метода и закрытие в конце. клиент не должен задумываться о БД.
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
//            connection = dbci.connectPostgre();
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
    public void writeHistory(int userId, int adId, String url1, String url2) throws RemoteException {
        int idAd = 0;
//        connection = dbci.connectPostgre();
        try {
            dbci.insertHistory(userId, adId, url1, url2);
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
//            connection = dbci.connectPostgre();
            resultRS = dbci.getAdId(adPath);
            while (resultRS.next())
                result = resultRS.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //получение списка адресов посещенных веб-страниц
    @Override
    public Object[] getURLS(int count) throws RemoteException {
        Object[] result = new Object[3];
        result[0] = count;
//        Connection connection = dbci.connectSqlite();
        ResultSet resultSet1 = null;
        ResultSet resultSet2 = null;
        String URL1 = "";
        String URL2 = "";
        String key1 = "";
        String key2 = "";
        int id1 = 0,
            id2 = 0;

        try
        {
            resultSet1 = dbci.getURLFromBrowser();

            while (resultSet1.next ())
            {
                result[1] = resultSet1.getString("url");
                result[1] += " ";
                id1 = resultSet1.getInt("id");
            }
            try{
                resultSet1 = dbci.getLowerTermFromBrowser(id1);
                while (resultSet1.next()){
                    result[1] += resultSet1.getString("lower_term");
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            System.out.println(result[1]);
            dbci.deleteBrowserHistory(id1);
//            connection.close();
            if(count == 2) {
//                connection = dbci.connectSqlite();
                resultSet2 = dbci.getURLFromBrowser();

                while (resultSet2.next()) {
                    result[2] = resultSet2.getString("url");
                    result[2] +=" ";
                    id2 = resultSet2.getInt("id");
                }
                try {
                    resultSet2 = dbci.getLowerTermFromBrowser(id2);
                    while (resultSet2.next()) {
                        result[2] += resultSet2.getString("lower_term");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(result[2]);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace ();
        }

        finally
        {
            try
            {
                resultSet1.close ();
                resultSet2.close ();
//                dbci.closeConnect();
            }

            catch (Exception e)
            {
                e.printStackTrace ();
            }
        }
        return result;
    }
}

//TODO: 1. пройтись по обращениям к БД, адаптировать их к новой БД.
