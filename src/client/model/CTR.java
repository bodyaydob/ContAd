package client.model;

import java.io.Serializable;

public class CTR implements Serializable {

    private String adName;
    private int displNum;
    private int clickNum;
    private double CTR;

    public CTR(String adName, int displNum, int clickNum){
        this.adName = adName;
        this.displNum = displNum;
        this.clickNum = clickNum;
        if (displNum != 0)
            this.CTR = (double)clickNum / (double)displNum;
        else
            this.CTR = 0;
    }

    public String getAdName() { return adName; }
    public int getDisplNum() { return displNum; }
    public int getClickNum() { return clickNum; }
    public double getCTR() { return CTR; }

    public void setAdName(String adName) { this.adName = adName; }
    public void setDisplNum(int displNum) { this.displNum = displNum; }
    public void setClickNum(int clickNum) { this.clickNum = clickNum; }
    public void setCTR(double CTR) { this.CTR = CTR; }

}
