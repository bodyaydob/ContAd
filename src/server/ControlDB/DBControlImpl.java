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
    Connection connection;

    @Override
    public Connection connectPostgre() throws RemoteException {
        connection = null;
        String url = "jdbc:postgresql://127.0.0.1:5432/contadv";
        String name = "postgres";
        String password = "1234";
        try {
            //Загружаем драйвер
            Class.forName("org.postgresql.Driver");
            System.out.println("\nДрайвер подключен...");
            //Создаём соединение
            connection = DriverManager.getConnection(url, name, password);
            System.out.println("Соединение с PostgreSQL установлено...");

        } catch (Exception ex) {
            //выводим наиболее значимые сообщения
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }

    @Override
    public Connection connectSqlite() throws RemoteException {
        connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("\nДрайвер подключен...");
            connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/1/AppData/Local/Google/Chrome/User Data/Default/History");
            System.out.println("Соединение с SQLite установлено...");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public void closeConnect(Connection connect) throws RemoteException {
        if (connect != null) {
            try {
                connect.close();
                System.out.println("Соединение закрыто...");
            } catch (SQLException ex) {
                Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public ArrayList<History> getHistoryList() throws RemoteException {
        ResultSet resultSet = null;
        ArrayList<History> resultList = new ArrayList<History>();
        connection = connectPostgre();
        Statement statement = null;
        Statement statement1 = null;

        try {
            statement = connection.createStatement();
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
        closeConnect(connection);
        return resultList;
    }

    @Override
    public String getNameUser(int id) throws RemoteException {
        ResultSet resultRS = null;
        String result = null;
        try {
            connection = connectPostgre();
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
        closeConnect(connection);
        return result;
    }

    @Override
    public ArrayList<String> getCategoriesNameList() throws RemoteException {
        ResultSet resultSet = null;
        ArrayList<String> resultList = new ArrayList<String>();
        connection = connectPostgre();
        Statement statement = null;
        Statement statement1 = null;

        try {
            statement = connection.createStatement();
            //Выполним запрос
            resultSet = statement.executeQuery("SELECT name " +
                                                        "FROM categories");
            while (resultSet.next())
                resultList.add(resultSet.getString("name"));
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        closeConnect(connection);
        return resultList;
    }

    @Override
    public void addCategory(String name) throws RemoteException {
        connection = connectPostgre();
        Statement statement = null;

        try {
            statement = connection.createStatement();
            //Выполним запрос
            statement.executeUpdate("INSERT INTO categories (name) " +
                                            "VALUES ('" + name + "')");
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        closeConnect(connection);
    }

    @Override
    public void addWord(String word, String catName) throws RemoteException {
        ResultSet result = null;
        connection = connectPostgre();
        Statement statement = null;
        int catId = 0;

        try {
            statement = connection.createStatement();
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
        closeConnect(connection);
    }

    @Override
    public void addAd(String path, int priority, String catName) throws RemoteException {
        ResultSet result = null;
        connection = connectPostgre();
        Statement statement = null;
        int catId = 0;

        try {
            statement = connection.createStatement();
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
        closeConnect(connection);
    }

    @Override
    public ArrayList<Category> getCategoriesList() throws RemoteException {
        ResultSet resultSet = null;
        ArrayList<Category> resultList = new ArrayList<Category>();
        connection = connectPostgre();
        Statement statement = null;
        try {
            statement = connection.createStatement();
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
        closeConnect(connection);
        return resultList;
    }

    @Override
    public void deleteCategory(int id) throws RemoteException {
        connection = connectPostgre();
        Statement statement = null;

        try {
            statement = connection.createStatement();
            //Выполним запрос
            statement.executeUpdate("DELETE FROM categories " +
                                            "WHERE id =" + id);
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        closeConnect(connection);
    }

    @Override
    public ArrayList<Word> getWordsList() throws RemoteException {
        ResultSet resultSet = null;
        ArrayList<Word> resultList = new ArrayList<Word>();
        connection = connectPostgre();
        Statement statement = null;
        try {
            statement = connection.createStatement();
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
        closeConnect(connection);
        return resultList;
    }

    @Override
    public ArrayList<Ad> getAdsList() throws RemoteException {
        ResultSet resultSet = null;
        ArrayList<Ad> resultList = new ArrayList<Ad>();
        connection = connectPostgre();
        Statement statement = null;
        try {
            statement = connection.createStatement();
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
        closeConnect(connection);
        return resultList;
    }

    @Override
    public void deleteWord(int id) throws RemoteException {
        connection = connectPostgre();
        Statement statement = null;

        try {
            statement = connection.createStatement();
            //Выполним запрос
            statement.executeUpdate("DELETE FROM words " +
                                            "WHERE id =" + id);
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        closeConnect(connection);
    }

    @Override
    public void deleteAd(int id) throws RemoteException {
        connection = connectPostgre();
        Statement statement = null;

        try {
            statement = connection.createStatement();
            //Выполним запрос
            statement.executeUpdate("DELETE FROM ads " +
                                            "WHERE id =" + id);
        } catch (SQLException e) {
            Logger.getLogger(DBControlImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        closeConnect(connection);
    }

    @Override
    public ResultSet getAuthUserId(Connection connect, String type, String userName, String password) throws RemoteException, SQLException {
        Statement statement = connect.createStatement();
        return statement.executeQuery("SELECT id_user " +
                                               "FROM \"user\" JOIN " +
                                                     "user_type ON \"user\".id_usr_type = user_type.id_type " +
                                               "WHERE upper(username) = '" + userName.toUpperCase() + "' AND " +
                                                     "upper(user_type.name) = '" + type.toUpperCase() + "' AND " +
                                                     "password = '" + password + "'");
    }

    @Override
    public ResultSet getUserGroupId(Connection connect, String group) throws RemoteException, SQLException {
        Statement statement = connect.createStatement();
        return statement.executeQuery("SELECT id_group " +
                                            "FROM user_group " +
                                            "WHERE name = '" + group + "'");
    }

    @Override
    public ResultSet getUserTypeId(Connection connect, String type) throws RemoteException, SQLException {
        Statement statement = connect.createStatement();
        return statement.executeQuery("SELECT id_type " +
                                            "FROM user_type " +
                                            "WHERE upper(name) = '" + type.toUpperCase() + "'");
    }

    @Override
    public void insertRegUser(Connection connect, int typeId, int groupId, String username, String password, String name) throws RemoteException, SQLException {
        Statement statement = connect.createStatement();
        statement.executeUpdate("INSERT INTO \"user\" (id_usr_type,id_usr_grp,username,password,name) " +
                                        "VALUES ('" + typeId + "','" + groupId + "','" + username + "','" + password + "','" + name +"')");
    }
}
