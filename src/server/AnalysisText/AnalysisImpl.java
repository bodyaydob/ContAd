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
    Connection connection;
    DBControlImpl dbci = new DBControlImpl();

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

    @Override
    public int[] getWords(ArrayList<String> lemmas) throws RemoteException, SQLException {
        int[] result = new int[9];
        /*for (int i : result)
            i = 0;*/
        ResultSet resultRS = null;
        connection = dbci.connectPostgre();
        Statement statement = null;
        statement = connection.createStatement();
        for (String lemma : lemmas) {
            resultRS = statement.executeQuery("SELECT * " +
                                                    "FROM words " +
                                                    "WHERE upper(value) = '" +
                                                   lemma.toUpperCase() + "'");
            while (resultRS.next()) {
                System.out.println(resultRS.getInt("id_cat") + "\n" + resultRS.getString("value"));
                result[resultRS.getInt("id_cat") - 1]++;
            }
        }
        return result;
    }

    @Override
    public void closeConnectionDB() throws RemoteException {
        dbci.closeConnect(connection);
    }

    @Override
    public String getNameUser(int id) throws RemoteException {
        ResultSet resultRS = null;
        String result = null;
        try {
            connection = dbci.connectPostgre();
            Statement statement = null;
            statement = connection.createStatement();
            resultRS = statement.executeQuery("SELECT name " +
                                                    "FROM users " +
                                                    "WHERE id = '" + id + "'");
            while (resultRS.next())
                result = resultRS.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

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

    @Override
    public String getImage(String nameCat) throws RemoteException {
        ResultSet resultRS = null;
        String result = null;
        String[] resultAr ;
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
            connection = dbci.connectPostgre();
            Statement statement = null;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            resultRS = statement.executeQuery("SELECT path " +
                                                       "FROM ads JOIN " +
                                                            "categories ON categories.id = ads.id_cat " +
                                                        "WHERE categories.name = '" + nameCat + "' AND  " +
                                                              "ads.priority = '" + priority+"'");
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

    @Override
    public void writeHistory(int userId, int adId, String url1, String url2) throws RemoteException {
        int idAd = 0;
        connection = dbci.connectPostgre();
        Statement statement = null;
        Statement statement1 = null;

        try {
            statement = connection.createStatement();
            //Выполним запрос
            statement.executeUpdate("INSERT INTO history (id_user, id_ad, url) " +
                                            "VALUES ('"+userId+"','"+adId+"','{\""+url1+"\",\""+url2+"\"}')");
        }
        catch (SQLException e) {
            Logger.getLogger(AuthorizationImpl.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public int getAdId(String adPath) throws RemoteException {
        ResultSet resultRS = null;
        int result = 0;
        try {
            connection = dbci.connectPostgre();
            Statement statement = null;
            statement = connection.createStatement();
            resultRS = statement.executeQuery("SELECT id " +
                                                       "FROM ads  " +
                                                       "WHERE path = '" + adPath + "'");
            while (resultRS.next())
                result = resultRS.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Object[] getURLS(int count) throws RemoteException {
        Object[] result = new Object[3];
        result[0] = count;
        Connection connection = dbci.connectSqlite();
        ResultSet resultSet1 = null;
        ResultSet resultSet2 = null;
        Statement statement = null;
        String URL1 = "";
        String URL2 = "";
        String key1 = "";
        String key2 = "";
        int id1 = 0,id2 = 0;


        try
        {
            statement = connection.createStatement ();
            resultSet1 = statement.executeQuery ("SELECT id,url,MAX(last_visit_time) " +
                                                        "FROM urls");

            while (resultSet1.next ())
            {
                result[1] = resultSet1.getString("url");
                result[1] += " ";
                id1 = resultSet1.getInt("id");
            }
            try{
                resultSet1 = statement.executeQuery("SELECT lower_term " +
                                                            "FROM keyword_search_terms " +
                                                            "WHERE url_id ="+ id1);
                while (resultSet1.next()){
                    result[1] += resultSet1.getString("lower_term");
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            System.out.println(result[1]);
            statement.executeUpdate("DELETE FROM urls " +
                                            "WHERE id ="+id1);
            statement.close();
            connection.close();
            if(count == 2) {
                connection = DriverManager
                        .getConnection("jdbc:sqlite:C:/Users/1/AppData/Local/Google/Chrome/User Data/Default/History");
                statement = connection.createStatement();
                resultSet2 = statement
                        .executeQuery("SELECT id,url,MAX(last_visit_time) " +
                                            "FROM urls");

                while (resultSet2.next()) {
                    result[2] = resultSet2.getString("url");
                    result[2] +=" ";
                    id2 = resultSet2.getInt("id");
                }
                try {
                    resultSet2 = statement.executeQuery("SELECT lower_term " +
                                                                "FROM keyword_search_terms " +
                                                                "WHERE url_id =" + id2);
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
                statement.close ();
                dbci.closeConnect(connection);
            }

            catch (Exception e)
            {
                e.printStackTrace ();
            }
        }
        return result;
    }
}
