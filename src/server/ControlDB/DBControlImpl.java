package server.ControlDB;

import client.model.Ad;
import client.model.Category;
import client.model.History;
import client.model.Word;

import java.rmi.RemoteException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//ПОДКЛЮЧЕНИЕ К БД
public class DBControlImpl implements DBControl {
    //атрибуты
    //----------------------------------------------------------
    Connection connectPostgre;
    Connection connectSqlite;

    //реализация методов
    //----------------------------------------------------------

    //конструкторы
    public DBControlImpl(){
        connectPostgre = connectPostgre();
    }

    public DBControlImpl(int countConnect){
        connectPostgre = connectPostgre();
        if (countConnect > 1){
            connectSqlite = connectSqlite();
        }
    }

    //подключение к Postgre
    private Connection connectPostgre() {
        connectPostgre = null;
        String url = "jdbc:postgresql://127.0.0.1:5432/contadv";
        String name = "postgres";
        String password = "1234";
        try {
            //Загружаем драйвер
            Class.forName("org.postgresql.Driver");
            System.out.println("\nДрайвер подключен...");
            //Создаём соединение
            connectPostgre = DriverManager.getConnection(url, name, password);
            System.out.println("Соединение с PostgreSQL установлено...");

        } catch (Exception ex) {
            //выводим наиболее значимые сообщения
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connectPostgre;
    }

    //подключение к Sqlite
    private Connection connectSqlite() {
        connectSqlite = null;
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("\nДрайвер подключен...");
            connectSqlite = DriverManager.getConnection("jdbc:sqlite:C:/Users/shlyk/AppData/Local/Google/Chrome/User Data/Default/History");
            System.out.println("Соединение с SQLite установлено...");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return connectSqlite;
    }

    //закрытие соединения
    @Override
    public void closeConnects() throws RemoteException {
        if (connectPostgre != null) {
            try {
                connectPostgre.close();
                System.out.println("Соединение с Postgre закрыто...");
            } catch (SQLException ex) {
                Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (connectSqlite != null) {
            try {
                connectSqlite.close();
                System.out.println("Соединение с Sqlite закрыто...");
            } catch (SQLException ex) {
                Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //получение списка истории
    @Override
    public ArrayList<History> getHistoryList() throws RemoteException {
        ResultSet resultSet = null;
        ArrayList<History> resultList = new ArrayList<History>();
        String[] urlsString;
        try {
            Statement statement = connectPostgre.createStatement();
            resultSet = statement.executeQuery("SELECT id_history, " +
                                                           "urls, " +
                                                           "rate, " +
                                                           "click, " +
                                                           "username, " +
                                                           "path, " +
                                                           "user_group.name " +
                                                        "FROM history JOIN " +
                                                             "\"user\" ON history.id_user = \"user\".id_user JOIN " +
                                                             "ad ON history.id_ad = ad.id_ad JOIN " +
                                                             "user_group ON \"user\".id_usr_grp = user_group.id_group " +
                                                        "ORDER BY history.id_history");
            while (resultSet.next()) {
                Array urls = resultSet.getArray("urls");
                if (urls != null)
                    urlsString = (String[]) urls.getArray();
                else
                    urlsString = new String[1];

                int id = resultSet.getInt("id_history");

                String user = resultSet.getString("username");

                String ad = resultSet.getString("path");

                int rate = resultSet.getInt("rate");

                boolean click = resultSet.getBoolean("click");

                String  userGroup = resultSet.getString("name");

                History his = new History(id, user, userGroup, ad, urlsString, rate, click);
                resultList.add(his);
            }
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        catch (Exception e){
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        return resultList;
    }

    //получение имени пользователя
    @Override
    public String getNameUser(int id) throws RemoteException {
        ResultSet resultRS = null;
        String result = null;
        try {
            Statement statement = connectPostgre.createStatement();
            resultRS = statement.executeQuery("SELECT name " +
                                                       "FROM \"user\" " +
                                                       "WHERE id_user = '" + id + "'");
            while (resultRS.next())
                result = resultRS.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //получение списка имен категорий
    @Override
    public ArrayList<String> getCategoriesNameList() throws RemoteException {
        ResultSet resultSet = null;
        ArrayList<String> resultList = new ArrayList<String>();
        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            resultSet = statement.executeQuery("SELECT name " +
                                                        "FROM category");
            while (resultSet.next())
                resultList.add(resultSet.getString("name"));
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        return resultList;
    }

    //добавить категорию
    @Override
    public void addCategory(String name) throws RemoteException {
        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            statement.executeUpdate("INSERT INTO category (name) " +
                                            "VALUES ('" + name + "')");
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //добавить слово
    @Override
    public void addWord(String word, String catName) throws RemoteException {
        ResultSet result = null;
        int catId = 0;

        try {
            Statement statement = connectPostgre.createStatement();
            catId = getCatId(catName);

            //Выполним запрос
            statement.executeUpdate("INSERT INTO word (name, " +
                                                           "id_cat) " +
                                            "VALUES ('" + word + "',"
                                                        + catId + ")");
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //добавить рекламное предложение
    @Override
    public void addAd(String path, int priority, String catName) throws RemoteException {
        ResultSet result = null;
        int catId = 0;

        try {
            Statement statement = connectPostgre.createStatement();
            catId = getCatId(catName);

            //Выполним запрос
            statement.executeUpdate("INSERT INTO ad (path, " +
                                                         "priority, " +
                                                         "id_cat) " +
                                         "VALUES ('" + path + "',"
                                                     + priority + ","
                                                     + catId +")");
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //получение списка категорий
    @Override
    public ArrayList<Category> getCategoriesList() throws RemoteException {
        ResultSet resultSet = null;
        ArrayList<Category> resultList = new ArrayList<Category>();
        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            resultSet = statement.executeQuery("SELECT * " +
                                                        "FROM category");
            while (resultSet.next()) {
                int id = resultSet.getInt("id_cat");
                String name = resultSet.getString("name");
                Category cat = new Category(id, name);
                resultList.add(cat);
            }
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        return resultList;
    }

    //удалить категорию
    @Override
    public void deleteCategory(int id) throws RemoteException {
        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            statement.executeUpdate("DELETE FROM category " +
                                            "WHERE id_cat =" + id);
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //получение списка слов
    @Override
    public ArrayList<Word> getWordsList() throws RemoteException {
        ResultSet resultSet = null;
        ArrayList<Word> resultList = new ArrayList<Word>();
        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            resultSet = statement.executeQuery("SELECT id_wrd, " +
                                                           "word.name AS wrd_name, " +
                                                           "category.name AS cat_name " +
                                                        "FROM word JOIN " +
                                                             "category ON word.id_cat = category.id_cat");
            while (resultSet.next()) {
                int id = resultSet.getInt("id_wrd");
                String value = resultSet.getString("wrd_name");
                String cat = resultSet.getString("cat_name");
                Word word = new Word(id, value, cat);
                resultList.add(word);
            }
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        return resultList;
    }

    //получение списка рекламных предложений
    @Override
    public ArrayList<Ad> getAdsList() throws RemoteException {
        ResultSet resultSet = null;
        ArrayList<Ad> resultList = new ArrayList<Ad>();
        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            resultSet = statement.executeQuery("SELECT id_ad, " +
                                                           "path, " +
                                                           "priority, " +
                                                           "ad_rate, " +
                                                           "displ_cnt, " +
                                                           "name " +
                                                        "FROM ad JOIN " +
                                                             "category ON ad.id_cat = category.id_cat " +
                                                        "ORDER BY id_ad");
            while (resultSet.next()) {
                int id = resultSet.getInt("id_ad");
                String path = resultSet.getString("path");
                int priority = resultSet.getInt("priority");
                String cat = resultSet.getString("name");
                int adRate = resultSet.getInt("ad_rate");
                int displCnt = resultSet.getInt("displ_cnt");
                Ad ad = new Ad(id, path, priority, cat, adRate, displCnt);
                resultList.add(ad);
            }
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        return resultList;
    }

    //удалить слово
    @Override
    public void deleteWord(int id) throws RemoteException {
        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            statement.executeUpdate("DELETE FROM word " +
                                            "WHERE id_wrd =" + id);
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //удалить рекламу
    @Override
    public void deleteAd(int id) throws RemoteException {
        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            statement.executeUpdate("DELETE FROM ad " +
                                            "WHERE id_ad =" + id);
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //получение ID авторизующегося пользователя
    @Override
    public ResultSet getAuthUserId(String type, String userName, String password) throws RemoteException, SQLException {
        Statement statement = connectPostgre.createStatement();
        return statement.executeQuery("SELECT id_user " +
                                               "FROM \"user\" JOIN " +
                                                     "user_type ON \"user\".id_usr_type = user_type.id_type " +
                                               "WHERE upper(username) = '" + userName.toUpperCase() + "' AND " +
                                                     "upper(user_type.name) = '" + type.toUpperCase() + "' AND " +
                                                     "password = '" + password + "'");
    }

    //получение ID группы пользователя
    @Override
    public ResultSet getUserGroupIDByName(String group) throws RemoteException, SQLException {
        Statement statement = connectPostgre.createStatement();
        return statement.executeQuery("SELECT id_group " +
                                            "FROM user_group " +
                                            "WHERE name = '" + group + "'");
    }

    //получение ID типа пользователя
    @Override
    public ResultSet getUserTypeId(String type) throws RemoteException, SQLException {
        Statement statement = connectPostgre.createStatement();
        return statement.executeQuery("SELECT id_type " +
                                            "FROM user_type " +
                                            "WHERE upper(name) = '" + type.toUpperCase() + "'");
    }

    //добавление регистрирующегося пользователя
    @Override
    public void insertRegUser(int typeId, int groupId, String username, String password, String name) throws RemoteException, SQLException {
        Statement statement = connectPostgre.createStatement();
        statement.executeUpdate("INSERT INTO \"user\" (id_usr_type,id_usr_grp,username,password,name) " +
                                        "VALUES ('" + typeId + "','" + groupId + "','" + username + "','" + password + "','" + name +"')");
    }

    //получение слова
    @Override
    public ResultSet getWord(String lemma) throws RemoteException, SQLException {
        Statement statement = connectPostgre.createStatement();
        return statement.executeQuery("SELECT * " +
                                            "FROM word " +
                                            "WHERE upper(name) = '" + lemma.toUpperCase() + "'");
    }

    //получение пути к рекламному предложению
    @Override
    public ResultSet getPathAd(String catName, int priority) throws RemoteException, SQLException {
        Statement statement = connectPostgre.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        return statement.executeQuery("SELECT path " +
                                            "FROM ad JOIN " +
                                                 "category ON category.id_cat = ad.id_cat " +
                                            "WHERE upper(category.name) = '" + catName.toUpperCase() + "' AND  " +
                                                  "ad.priority = '" + priority+"'");
    }

    //добавление записи истории
    @Override
    public void insertHistory(int userId, int adId, String[] urls, int rate, boolean click) throws RemoteException, SQLException {
        Statement statement = connectPostgre.createStatement();
        String urlsString = "";
        int grpId = getUserGroupIDByUser(userId);
        urlsString = "{\"" + urls[0] + "\"";
        for (int i = 1; i < urls.length; i++)
            if (urls[i].length() > 30)
                urlsString = urlsString + ", \"" + urls[i].substring(0,30) + "\"";
            else
                urlsString = urlsString + ", \"" + urls[i] + "\"";
        urlsString += "}";
        statement.executeUpdate("INSERT INTO history (id_user, id_ad, urls, id_usr_grp, rate, click) " +
                                        "VALUES (" +userId + "," + adId + ",'" + urlsString + "'," + grpId + "," + rate + ", '" + click + "')");
    }

    //получение ID рекламного предлоежния
    @Override
    public ResultSet getAdId(String adPath) throws RemoteException, SQLException {
        Statement statement = connectPostgre.createStatement();
        return statement.executeQuery("SELECT id_ad " +
                                            "FROM ad  " +
                                            "WHERE path = '" + adPath + "'");
    }

    //получение URL из файла истории браузера
    @Override
    public ResultSet getURLFromBrowser() throws RemoteException, SQLException {
        Statement statement = connectSqlite.createStatement ();
        return statement.executeQuery ("SELECT id," +
                                                   "url," +
                                                   "MAX(last_visit_time) " +
                                                "FROM urls");
    }

    //получение lowerTerm из файла истории браузера
    @Override
    public ResultSet getLowerTermFromBrowser(int id) throws RemoteException, SQLException {
        Statement statement = connectSqlite.createStatement ();
        return statement.executeQuery("SELECT lower_term " +
                                            "FROM keyword_search_terms " +
                                            "WHERE url_id ="+ id);
    }

    //удалить запись истории из браузера
    @Override
    public void deleteBrowserHistory(int id) throws RemoteException, SQLException {
        Statement statement = connectSqlite.createStatement ();
        statement.executeUpdate("DELETE FROM urls " +
                                        "WHERE id ="+id);
    }

    //получение ID категории
    public int getCatId(String catName){
        int catId = 0;
        try {
            Statement statement = connectPostgre.createStatement();
            ResultSet result = statement.executeQuery("SELECT id_cat " +
                                                            "FROM category " +
                                                            "WHERE category.name = '" + catName + "'");
            while (result.next())
                catId = result.getInt("id_cat");
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        return catId;
    }

    //пересоздание подключений
    @Override
    public void reCreateConnections() throws RemoteException, SQLException {
        if(connectPostgre != null && connectPostgre.isClosed() == true){
            connectPostgre = connectPostgre();
        }
        if(connectSqlite != null && connectSqlite.isClosed() == true){
            connectSqlite = connectSqlite();
        }
    }

    //получение ID группы пользователей по ID пользователя
    @Override
    public int getUserGroupIDByUser(int userID) throws RemoteException, SQLException {
        int usrGrpID = 0;
        Statement statement = connectPostgre.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT id_usr_grp " +
                                                            "FROM \"user\" " +
                                                            "WHERE id_user = " + userID);
        while (resultSet.next())
            usrGrpID = resultSet.getInt("id_usr_grp");
        return usrGrpID;
    }

    //получение групповой информации для пользователя по категории
    @Override
    public int getGrpCatData(int groupID, int catID) throws RemoteException, SQLException {
        int cntWords = 0;
        Statement statement = connectPostgre.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT cnt_wrds " +
                                                            "FROM group_cat_data " +
                                                            "WHERE id_group = " + groupID + " AND " +
                                                                  "id_cat = " + catID);
        while (resultSet.next())
            cntWords = resultSet.getInt("cnt_wrds");
        return cntWords;
    }

    //добавление групповой информации для пользователя по категории
    @Override
    public void addGrpCatData(int groupID, int catID, int cnt) throws RemoteException, SQLException{
        Statement statement = connectPostgre.createStatement();
        statement.executeUpdate("INSERT INTO group_cat_data (id_group, " +
                                                                "id_cat, " +
                                                                "cnt_wrds) " +
                                        "VALUES (" + groupID + ", " + catID + ", " + cnt  + ") " +
                                        "ON CONFLICT (id_group, id_cat) " +
                                        "DO UPDATE " +
                                            "SET cnt_wrds = " + cnt);
    }

    //обновление исторических данных рекламы
    @Override
    public void updateAd(int adId, int rate) throws RemoteException, SQLException {
        int adRate = 0;
        int displCnt = 0;

        Statement statement = connectPostgre.createStatement();
        ResultSet resulSet = statement.executeQuery("SELECT ad_rate, " +
                                                                "displ_cnt " +
                                                            "FROM ad " +
                                                            "WHERE id_ad = " + adId);

        while (resulSet.next()) {
            adRate = resulSet.getInt("ad_rate");
            displCnt = resulSet.getInt("displ_cnt");
        }

        adRate += rate;
        displCnt += 1;

        statement.executeUpdate("UPDATE ad " +
                                        "SET ad_rate = " + adRate + ", " +
                                            "displ_cnt = " + displCnt + " " +
                                        "WHERE id_ad = " + adId);
    }
}
