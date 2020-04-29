package server.AnalysisText;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface Analysis extends Remote {
    Object[]          getURLS(int count)                  throws RemoteException;

    ArrayList<String> getLemmas(String text,
                                int maxLengthStopWord)    throws RemoteException;

    int[]             getWords (ArrayList<String> lemmas) throws RemoteException, SQLException;

    String            getNameUser (int id)                throws RemoteException;

    void              closeConnectionDB()                 throws RemoteException;

    String            getMaxCat(int[] countWords)         throws RemoteException;

    String            getImage (String nameCat)           throws RemoteException;

    int               getAdId (String adPath)             throws RemoteException;

    void              writeHistory (int userId,
                                    int adId,
                                    String url1,
                                    String url2)          throws RemoteException;
}
