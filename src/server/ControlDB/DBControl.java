package server.ControlDB;

import client.model.Ad;
import client.model.Category;
import client.model.History;
import client.model.Word;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;

public interface DBControl extends Remote {
    //объявление методов
    //----------------------------------------

    //подключение к Postgre
    Connection          connectPostgre()                  throws RemoteException;
    //подключение к Sqlite
    Connection          connectSqlite()                   throws RemoteException;
    //закрытие соединения
    void                closeConnect (Connection connect) throws RemoteException;
    //получение списка истории
    ArrayList<History>  getHistoryList()                  throws RemoteException;
    //получение имени пользователя
    String              getNameUser (int id)              throws RemoteException;
    //получение списка имен категорий
    ArrayList<String>   getCategoriesNameList()           throws RemoteException;
    //получение списка категорий
    ArrayList<Category> getCategoriesList()               throws RemoteException;
    //получение ID авторизующегося пользователя
    ResultSet           getAuthUserId(Connection connect,
                                      String type,
                                      String userName,
                                      String password)    throws RemoteException, SQLException;
    //получение ID группы пользователя
    ResultSet           getUserGroupId(Connection connect,
                                       String group)      throws RemoteException, SQLException;
    //получение ID типа пользователя
    ResultSet           getUserTypeId(Connection connect,
                                      String type)        throws RemoteException, SQLException;
    //получение слова
    ResultSet           getWord(Connection connect,
                                String lemma)             throws RemoteException, SQLException;
    //получение пути к рекламному предложению
    ResultSet           getPathAd(Connection connect,
                                  String catName,
                                  int priority)           throws RemoteException, SQLException;
    //получение списка слов
    ArrayList<Word>     getWordsList()                    throws RemoteException;
    //получение списка рекламных предложений
    ArrayList<Ad>       getAdsList()                      throws RemoteException;
    //получение ID рекламного предлоежния
    ResultSet           getAdId(Connection connect,
                                String adPath)            throws RemoteException, SQLException;
    //получение URL из файла истории браузера
    ResultSet           getURLFromBrowser(Connection connect) throws RemoteException, SQLException;
    //получение lowerTerm из файла истории браузера
    ResultSet           getLowerTermFromBrowser(Connection connect,
                                                int id)   throws  RemoteException, SQLException;
    //добавить категорию
    void                addCategory(String name)          throws RemoteException;
    //добавить слово
    void                addWord(String word,
                                String catName)           throws RemoteException;
    //добавить рекламное предложение
    void                addAd (String path,
                               int priority,
                               String catName)            throws RemoteException;
    //добавление регистрирующегося пользователя
    void                insertRegUser(Connection connect,
                                      int typeId,
                                      int groupId,
                                      String username,
                                      String password,
                                      String name)        throws RemoteException, SQLException;
    //добавление записи истории
    void                insertHistory(Connection connect,
                                      int userId,
                                      int adId,
                                      String url1,
                                      String url2)        throws RemoteException, SQLException;
    //удалить категорию
    void                deleteCategory(int id)            throws RemoteException;
    //удалить слово
    void                deleteWord(int id)                throws RemoteException;
    //удалить рекламу
    void                deleteAd(int id)                  throws RemoteException;
    //удалить запись истории из браузера
    void                deleteBrowserHistory(Connection connect,
                                             int id)      throws RemoteException, SQLException;
}

//TODO: 1. разобраться: в некоторые методы передаю соединение, в некоторые - нет. сделать однообразно.