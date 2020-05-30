package client.model;

import java.io.Serializable;

public class AssignUser2UserGroup implements Serializable {

    private String username;
    private String groupName;
    private int sumRate;
    private int displNum;
    private double assignIndicator;

    public AssignUser2UserGroup(String username, String groupName, int sumRate, int displNum){
        this.username = username;
        this.groupName = groupName;
        this.sumRate = sumRate;
        this.displNum = displNum;
        if(displNum != 0)
            this.assignIndicator = (double)sumRate / (double)displNum;
        else
            this.assignIndicator = 0;
    }

    public String getUsername() { return username; }
    public String getGroupName() { return groupName; }
    public int getSumRate() { return sumRate; }
    public int getDisplNum() { return displNum; }
    public double getAssignIndicator() { return assignIndicator; }

    public void setUsername(String username) { this.username = username; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public void setSumRate(int sumRate) { this.sumRate = sumRate; }
    public void setDisplNum(int displNum) { this.displNum = displNum; }
    public void setAssignIndicator(double assignIndicator) { this.assignIndicator = assignIndicator; }
}
