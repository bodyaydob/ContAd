package server.Authorization;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface Authorization extends Remote {
    //объявление методов
    //----------------------------

    //авторизация
    int     checkAuthorizationInformation(String type,
                                          String userName,
                                          String password) throws RemoteException,SQLException;
    //регистрация
    void    writeRegistrationInformation(String type,
                                         String userName,
                                         String password,
                                         String name,
                                         String groupText) throws RemoteException;
    //проверка логина
    boolean validationUserName (String userName)           throws RemoteException;
    //проверка пароля
    boolean validationPassword (String password)           throws RemoteException;
    //проверка имени
    boolean validationName (String name)                   throws RemoteException;
    //проверка группы пользоваетеля
    boolean validationUserGroup (String userGroup)         throws RemoteException;
    //проверка секретного вопроса
    boolean checkSecretQuest (String answer)               throws RemoteException;
    //закрытие соединений с БД
    void    closeConnections()                             throws RemoteException;
    //переподключение к БД
    void    reConnectToDB()                                throws RemoteException, SQLException;
}
