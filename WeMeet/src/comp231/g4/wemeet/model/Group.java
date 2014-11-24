package comp231.g4.wemeet.model;

import java.util.ArrayList;
import java.util.List;

public class Group {
	public int id;
	public String name;
	public List<GroupMemeber> groupMembers;

	public Group(int id, String name) {
		this.id = id;
		this.name = name;
		groupMembers = new ArrayList<GroupMemeber>();
	}

	public boolean addGroupMemeber(GroupMemeber member) {
		if (groupMembers.contains(member)) {
			return false;
		} else {
			return groupMembers.add(member);
		}
	}

	public boolean removeGroupMemeber(GroupMemeber member) {
		if (groupMembers.contains(member)) {
			return false;
		} else {
			return groupMembers.remove(member);
		}
	}

	public List<GroupMemeber> getGroupMemebers(Contact contact) {
		return new ArrayList<GroupMemeber>(groupMembers);
	}
}
