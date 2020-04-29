package server;

import server.AnalysisText.AnalysisImpl;
import server.Authorization.AuthorizationImpl;
import server.ControlDB.DBControl;
import server.ControlDB.DBControlImpl;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ContextualAdServer {
    public static void main(String args[]) throws RemoteException {
        System.out.println("Запуск сервера...");
        try{
            Registry registry = LocateRegistry.createRegistry(1099);
            AuthorizationImpl obj = new  AuthorizationImpl();//создаем объект удаленного интерфейса
            AnalysisImpl obj2 = new AnalysisImpl();
            DBControlImpl obj3 = new DBControlImpl();
            Remote stub = UnicastRemoteObject.exportObject(obj, 0);
            Remote stub2 = UnicastRemoteObject.exportObject(obj2, 0);
            Remote stub3 = UnicastRemoteObject.exportObject(obj3, 0);
            registry.rebind("Authorization", stub);
            registry.rebind("Analysis", stub2);
            registry.rebind("DBControl", stub3);
            System.out.print("Сервер запущен...");
        }
        catch(Exception e)
        {
            System.out.println("Ошибка сервера... "+ e.toString());
            System.out.println("Ошибка сервера... "+ e.getLocalizedMessage());
        }
    }
}
