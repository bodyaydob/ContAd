package server.ControlDB;

import client.model.Ad;
import client.model.Category;
import client.model.History;
import client.model.Word;
import org.postgresql.core.SqlCommand;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;

public interface DBControl extends Remote {
    //объявление методов
    //----------------------------------------

    //закрытие соединения
    void                closeConnects() throws RemoteException;
    //получение списка истории
    ArrayList<History>  getHistoryList()                  throws RemoteException;
    //получение имени пользователя
    String              getNameUser (int id)              throws RemoteException;
    //получение списка имен категорий
    ArrayList<String>   getCategoriesNameList()           throws RemoteException;
    //получение списка категорий
    ArrayList<Category> getCategoriesList()               throws RemoteException;
    //получение ID авторизующегося пользователя
    ResultSet           getAuthUserId(String type,
                                      String userName,
                                      String password)    throws RemoteException, SQLException;
    //получение ID группы пользователя по названию
    ResultSet           getUserGroupIDByName(String group) throws RemoteException, SQLException;
    //получение ID группы пользователя по ID пользователя
    int                 getUserGroupIDByUser(int userID)   throws RemoteException, SQLException;
    //получение ID типа пользователя
    ResultSet           getUserTypeId(String type)        throws RemoteException, SQLException;
    //получение слова
    ResultSet           getWord(String lemma)             throws RemoteException, SQLException;
    //получение пути к рекламному предложению
    ResultSet           getPathAd(String catName,
                                  int priority)           throws RemoteException, SQLException;
    //получение списка слов
    ArrayList<Word>     getWordsList()                    throws RemoteException;
    //получение списка рекламных предложений
    ArrayList<Ad>       getAdsList()                      throws RemoteException;
    //получение ID рекламного предлоежния
    ResultSet           getAdId(String adPath)            throws RemoteException, SQLException;
    //получение URL из файла истории браузера
    ResultSet           getURLFromBrowser() throws RemoteException, SQLException;
    //получение lowerTerm из файла истории браузера
    ResultSet           getLowerTermFromBrowser(int id)   throws  RemoteException, SQLException;
    //получение групповой информации для пользователя по категории
    int                 getGrpCatData(int userID,
                                      int catID)          throws RemoteException, SQLException;
    //добавить категорию
    void                addCategory(String name)          throws RemoteException;
    //добавить слово
    void                addWord(String word,
                                String catName)           throws RemoteException;
    //добавить рекламное предложение
    void                addAd (String path,
                               int priority,
                               String catName)            throws RemoteException;
    //добавить групповую информацию для пользователя по категории
    void                addGrpCatData(int userID,
                                      int catID,
                                      int cnt)            throws RemoteException, SQLException;
    //добавление регистрирующегося пользователя
    void                insertRegUser(int typeId,
                                      int groupId,
                                      String username,
                                      String password,
                                      String name)        throws RemoteException, SQLException;
    //добавление записи истории
    void                insertHistory(int userId,
                                      int adId,
                                      String url1,
                                      String url2,
                                      int rate,
                                      boolean click)      throws RemoteException, SQLException;
    //удалить категорию
    void                deleteCategory(int id)            throws RemoteException;
    //удалить слово
    void                deleteWord(int id)                throws RemoteException;
    //удалить рекламу
    void                deleteAd(int id)                  throws RemoteException;
    //удалить запись истории из браузера
    void                deleteBrowserHistory(int id)      throws RemoteException, SQLException;
    //пересоздание подключений
    void                reCreateConnections()             throws RemoteException, SQLException;
}
