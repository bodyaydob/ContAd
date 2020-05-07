package server.Authorization;

import server.ControlDB.DBControlImpl;
import sun.awt.geom.AreaOp;

import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthorizationImpl implements Authorization {

    //атрибуты
    //----------------------------------------

    DBControlImpl dbci = new DBControlImpl(1);

    //реализация методов
    //-----------------------------------------

    //проверки на пустоту
    @Override
    public boolean validationUserName(String userName) {
        if (userName.isEmpty())
            return false;
        else
            return true;
    }

    @Override
    public boolean validationPassword(String password) {
        if (password.isEmpty())
            return false;
        else
            return true;
    }

    @Override
    public boolean validationName(String name) {
        if (name.isEmpty())
            return false;
        else
            return true;
    }

    @Override
    public boolean validationUserGroup(String userGroup) {
        if (userGroup.isEmpty())
            return false;
        else
            return true;
    }

    //проверка секретного вопроса
    @Override
    public boolean checkSecretQuest(String answer) {
        if (answer.equals("Шлыков"))
            return true;
        else
            return false;
    }

    //авторизация
    @Override
    public int checkAuthorizationInformation(String type, String userName, String password) throws RemoteException,SQLException {
        ResultSet result = null;
        int resultId = 0;
        try {
            //выполнение запроса
            result = dbci.getAuthUserId(type, userName, password);
            //result это указатель на первую строку с выборки
            //чтобы вывести данные мы будем использовать
            //метод next() , с помощью которого переходим к следующему элементу

            while (result.next()) {
                resultId = result.getInt("id_user");
            }
        } catch (SQLException e) {
            System.out.println("Ошибочная попытка авторизации...");
            Logger.getLogger(AuthorizationImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        return resultId;
    }

    //регистрация
    @Override
    public void writeRegistrationInformation(String type, String userName, String password, String name, String group) throws RemoteException {
        ResultSet result = null;
        int groupId = 0;
        int typeId = 0;

        try {
            //получение ID группы пользователей
            result = dbci.getUserGroupId(group);
            while (result.next()) {
                groupId = result.getInt("id_group");
            }
            //получение ID типа пользователя
            result = dbci.getUserTypeId(type);
            while (result.next()) {
                typeId = result.getInt("id_type");
            }
            //выполнение обновления
            dbci.insertRegUser(typeId, groupId, userName, password, name);
        }
        catch (SQLException e) {
            System.out.println("Ошибочная попытка регистрации...");
            Logger.getLogger(AuthorizationImpl.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //закрытие соединений
    @Override
    public void closeConnections() throws RemoteException {
        dbci.closeConnects();
    }

    //переподключение к БД
    @Override
    public void reConnectToDB() throws RemoteException, SQLException {
        dbci.reCreateConnections();
    }
}
