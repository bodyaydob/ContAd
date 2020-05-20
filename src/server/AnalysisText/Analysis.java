package server.AnalysisText;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface Analysis extends Remote {
    //объявление методов
    //-----------------------------------------

    //получение списка адресов посещенных веб-страниц
    String[]          getURLS(int count)                         throws RemoteException;
    //получение списка лемм
    ArrayList<String> getLemmas(String text,
                                int maxLengthStopWord)           throws RemoteException;
    //получение количества принадлежностей лемм к категориям 
    int[]             getCntCatsOfLemm(ArrayList<String> lemmas) throws RemoteException, SQLException;
    //получение имени пользователя
    String            getNameUser (int id)                       throws RemoteException;
    //получение наибоолеевстречающейся категории
    String            getMaxCat(int[] countWords)                throws RemoteException;
    //получение пути до рекламного предложения
    String            getImage (String nameCat)                  throws RemoteException;
    //получение ID рекламного предложения
    int               getAdId (String adPath)                    throws RemoteException;
    //запись истории
    void              writeHistory (int userId,
                                    int adId,
                                    String[] urls,
                                    int rate,
                                    boolean click)               throws RemoteException;
    //закрытие соединений с БД
    void              closeConnections()                         throws RemoteException;
    //переподключение к БД
    void              reConnectToDB()                            throws RemoteException, SQLException;
    //добавление групповых данных по категориям в БД
    int[]             addGroupsCatDataToDB(int idUser,
                                           int[] catData)        throws RemoteException, SQLException;
}
