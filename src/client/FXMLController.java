package client;

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


    public void setId(int id) {
        this.id = id;
    }
}
