package comp231.g4.wemeet.model;


public class GroupMemeber {
    public int groupId;
    public String name;
    public String number;

    public GroupMemeber(int groupId, String name,String phoneNumber) {
        this.groupId = groupId;
        this.name = name;
        this.number = phoneNumber;
    }
}