package server.Authorization;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface Authorization extends Remote {
    int     checkAuthorizationInformation(String type,
                                          String userName,
                                          String password) throws RemoteException,SQLException;

    void    writeRegistrationInformation(String type,
                                         String userName,
                                         String password,
                                         String name,
                                         String groupText) throws RemoteException;

    boolean validationUserName (String userName)           throws RemoteException;

    boolean validationPassword (String password)           throws RemoteException;

    boolean validationName (String name)                   throws RemoteException;

    boolean validationUserGroup (String userGroup)         throws RemoteException;

    boolean checkSecretQuest (String answer)               throws RemoteException;
    
    void    closeConnectionDB()                            throws RemoteException;
}
