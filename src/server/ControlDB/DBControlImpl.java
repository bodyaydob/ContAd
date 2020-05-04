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
//    Connection connection;
    Connection connectPostgre;
    Connection connectSqlite;

    //реализация методов
    //----------------------------------------------------------

    //конструкторы
    public DBControlImpl(){
        connectPostgre = connectPostgre();
        connectSqlite = connectSqlite();
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
//        connection = connectPostgre();
//        Statement statement = null;
//        Statement statement1 = null;

        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            resultSet = statement.executeQuery("SELECT history.id, " +
                                                           "history.url, " +
                                                           "users.username, " +
                                                           "ads.path " +
                                                        "FROM history, " +
                                                             "users, " +
                                                             "ads " +
                                                        "WHERE history.id_user = users.id AND " +
                                                              "history.id_ad = ads.id");
            while (resultSet.next()) {
                Array urls = resultSet.getArray("url");
                String[] urlss = (String[]) urls.getArray();
                int id = resultSet.getInt("id");
                String user = resultSet.getString("username");
                String ad = resultSet.getString("path");
                String url1 = urlss[0];
                String url2 = urlss[1];
                History his = new History(id, user, ad, url1, url2);
                resultList.add(his);
            }
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
//        closeConnect(connection);
        return resultList;
    }

    //получение имени пользователя
    @Override
    public String getNameUser(int id) throws RemoteException {
        ResultSet resultRS = null;
        String result = null;
        try {
//            connection = connectPostgre();
//            Statement statement = null;
            Statement statement = connectPostgre.createStatement();
            resultRS = statement.executeQuery("SELECT name " +
                                                       "FROM users " +
                                                       "WHERE id = '" + id + "'");
            while (resultRS.next())
                result = resultRS.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        closeConnect(connection);
        return result;
    }

    //получение списка имен категорий
    @Override
    public ArrayList<String> getCategoriesNameList() throws RemoteException {
        ResultSet resultSet = null;
        ArrayList<String> resultList = new ArrayList<String>();
//        connection = connectPostgre();
//        Statement statement = null;
//        Statement statement1 = null;

        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            resultSet = statement.executeQuery("SELECT name " +
                                                        "FROM categories");
            while (resultSet.next())
                resultList.add(resultSet.getString("name"));
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
//        closeConnect(connection);
        return resultList;
    }

    //добавить категорию
    @Override
    public void addCategory(String name) throws RemoteException {
//        connection = connectPostgre();
//        Statement statement = null;

        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            statement.executeUpdate("INSERT INTO categories (name) " +
                                            "VALUES ('" + name + "')");
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
//        closeConnect(connection);
    }

    //добавить слово
    @Override
    public void addWord(String word, String catName) throws RemoteException {
        ResultSet result = null;
//        connection = connectPostgre();
//        Statement statement = null;
        int catId = 0;

        try {
            Statement statement = connectPostgre.createStatement();
            result = statement.executeQuery("SELECT id " +
                                                     "FROM categories " +
                                                     "WHERE categories.name = '" + catName + "'");
            while (result.next())
                catId = result.getInt("id");

            //Выполним запрос
            statement.executeUpdate("INSERT INTO words (value, " +
                                                            "id_cat) " +
                                            "VALUES ('" + word + "',"
                                                        + catId + ")");
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
//        closeConnect(connection);
    }

    //добавить рекламное предложение
    @Override
    public void addAd(String path, int priority, String catName) throws RemoteException {
        ResultSet result = null;
//        connection = connectPostgre();
//        Statement statement = null;
        int catId = 0;

        try {
            Statement statement = connectPostgre.createStatement();
            result = statement.executeQuery("SELECT id " +
                                                     "FROM categories " +
                                                     "WHERE categories.name = '" + catName + "'");
            while (result.next())
                catId = result.getInt("id");

            //Выполним запрос
            statement.executeUpdate("INSERT INTO ads (path, " +
                                                          "priority, " +
                                                          "id_cat) " +
                                         "VALUES ('" + path + "',"
                                                     + priority + ","
                                                     + catId +")");
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
//        closeConnect(connection);
    }

    //получение списка категорий
    @Override
    public ArrayList<Category> getCategoriesList() throws RemoteException {
        ResultSet resultSet = null;
        ArrayList<Category> resultList = new ArrayList<Category>();
//        connection = connectPostgre();
//        Statement statement = null;
        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            resultSet = statement.executeQuery("SELECT * " +
                                                        "FROM categories");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                Category cat = new Category(id, name);
                resultList.add(cat);
            }
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
//        closeConnect(connection);
        return resultList;
    }

    //удалить категорию
    @Override
    public void deleteCategory(int id) throws RemoteException {
//        connection = connectPostgre();
//        Statement statement = null;

        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            statement.executeUpdate("DELETE FROM categories " +
                                            "WHERE id =" + id);
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
//        closeConnect(connection);
    }

    //получение списка слов
    @Override
    public ArrayList<Word> getWordsList() throws RemoteException {
        ResultSet resultSet = null;
        ArrayList<Word> resultList = new ArrayList<Word>();
//        connection = connectPostgre();
//        Statement statement = null;
        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            resultSet = statement.executeQuery("SELECT words.id, " +
                                                           "words.value, " +
                                                           "categories.name " +
                                                        "FROM words, " +
                                                             "categories " +
                                                        "WHERE words.id_cat = categories.id");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String value = resultSet.getString("value");
                String cat = resultSet.getString("name");
                Word word = new Word(id, value, cat);
                resultList.add(word);
            }
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
//        closeConnect(connection);
        return resultList;
    }

    //получение списка рекламных предложений
    @Override
    public ArrayList<Ad> getAdsList() throws RemoteException {
        ResultSet resultSet = null;
        ArrayList<Ad> resultList = new ArrayList<Ad>();
//        connection = connectPostgre();
//        Statement statement = null;
        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            resultSet = statement.executeQuery("SELECT ads.id, " +
                                                           "ads.path, " +
                                                           "ads.priority, " +
                                                           "categories.name " +
                                                        "FROM ads, " +
                                                             "categories " +
                                                        "WHERE ads.id_cat = categories.id");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String path = resultSet.getString("path");
                int priority = resultSet.getInt("priority");
                String cat = resultSet.getString("name");
                Ad ad = new Ad(id, path,priority, cat);
                resultList.add(ad);
            }
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
//        closeConnect(connection);
        return resultList;
    }

    //удалить слово
    @Override
    public void deleteWord(int id) throws RemoteException {
//        connection = connectPostgre();
//        Statement statement = null;

        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            statement.executeUpdate("DELETE FROM words " +
                                            "WHERE id =" + id);
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
//        closeConnect(connection);
    }

    //удалить рекламу
    @Override
    public void deleteAd(int id) throws RemoteException {
//        connection = connectPostgre();
//        Statement statement = null;

        try {
            Statement statement = connectPostgre.createStatement();
            //Выполним запрос
            statement.executeUpdate("DELETE FROM ads " +
                                            "WHERE id =" + id);
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
//        closeConnect(connection);
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
    public ResultSet getUserGroupId(String group) throws RemoteException, SQLException {
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
                                            "FROM words " +
                                            "WHERE upper(value) = '" + lemma.toUpperCase() + "'");
    }

    //получение пути к рекламному предложению
    @Override
    public ResultSet getPathAd(String catName, int priority) throws RemoteException, SQLException {
        Statement statement = connectPostgre.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        return statement.executeQuery("SELECT path " +
                                            "FROM ads JOIN " +
                                                 "categories ON categories.id = ads.id_cat " +
                                            "WHERE categories.name = '" + catName + "' AND  " +
                                                  "ads.priority = '" + priority+"'");
    }

    //добавление записи истории
    @Override
    public void insertHistory(int userId, int adId, String url1, String url2) throws RemoteException, SQLException {
        Statement statement = connectPostgre.createStatement();
        statement.executeUpdate("INSERT INTO history (id_user, id_ad, url) " +
                                        "VALUES ('"+userId+"','"+adId+"','{\""+url1+"\",\""+url2+"\"}')");
    }

    //получение ID рекламного предлоежния
    @Override
    public ResultSet getAdId(String adPath) throws RemoteException, SQLException {
        Statement statement = connectPostgre.createStatement();
        return statement.executeQuery("SELECT id " +
                                            "FROM ads  " +
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
}
