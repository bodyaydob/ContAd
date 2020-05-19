package client;

import java.rmi.RemoteException;

public class FXMLController {
    private FXMLController children;
    private FXMLController parent;
    private int id;

    public FXMLController getChildren(){
        return children;
    }
    public void setParent(FXMLController controller){
        this.parent = controller;
    }
    public FXMLController getParent(){
        return parent;
    }


    public void setId(int id) throws RemoteException {
        this.id = id;
    }
}
