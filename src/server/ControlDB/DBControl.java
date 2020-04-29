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
    Connection          connectPostgre()                  throws RemoteException;

    Connection          connectSqlite()                   throws RemoteException;

    void                closeConnect (Connection connect) throws RemoteException;

    ArrayList<History>  getHistoryList()                  throws RemoteException;

    String              getNameUser (int id)              throws RemoteException;

    ArrayList<String>   getCategoriesNameList()           throws RemoteException;

    void                addCategory(String name)          throws RemoteException;

    void                addWord(String word,
                                String catName)           throws RemoteException;

    void                addAd (String path,
                               int priority,
                               String catName)            throws RemoteException;

    ArrayList<Category> getCategoriesList()               throws RemoteException;

    void                deleteCategory(int id)            throws RemoteException;

    ArrayList<Word>     getWordsList()                    throws RemoteException;

    ArrayList<Ad>       getAdsList()                      throws RemoteException;

    void                deleteWord(int id)                throws RemoteException;

    void                deleteAd(int id)                  throws RemoteException;

    ResultSet           getAuthUserId(Connection connect,
                                      String type,
                                      String userName,
                                      String password)    throws RemoteException, SQLException;

    ResultSet           getUserGroupId(Connection connect,
                                       String group)      throws RemoteException, SQLException;

    ResultSet           getUserTypeId(Connection connect,
                                      String type)        throws RemoteException, SQLException;

    void                insertRegUser(Connection connect,
                                      int typeId,
                                      int groupId,
                                      String username,
                                      String password,
                                      String name)        throws RemoteException, SQLException;
}
