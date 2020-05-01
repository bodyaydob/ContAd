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
    Object[]          getURLS(int count)                  throws RemoteException;
    //получение списка лемм
    ArrayList<String> getLemmas(String text,
                                int maxLengthStopWord)    throws RemoteException;
    //получение количества принадлежностей лемм к категориям 
    int[]             getCntCatsOfLemm(ArrayList<String> lemmas) throws RemoteException, SQLException;
    //получение имени пользователя
    String            getNameUser (int id)                throws RemoteException;
    //закрытие соединения
    void              closeConnectionDB()                 throws RemoteException;
    //получение наибоолеевстречающейся категории
    String            getMaxCat(int[] countWords)         throws RemoteException;
    //получение пути до рекламного предложения
    String            getImage (String nameCat)           throws RemoteException;
    //получение ID рекламного предложения
    int               getAdId (String adPath)             throws RemoteException;
    //запись истории
    void              writeHistory (int userId,
                                    int adId,
                                    String url1,
                                    String url2)          throws RemoteException;
}
