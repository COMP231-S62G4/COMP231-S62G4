package comp231.g4.wemeet.model;

import java.util.ArrayList;
import java.util.List;

public class Group {
	public int id;
	public String name;
	public List<Contact> groupMembers;

	public Group(int id, String name) {
		this.id = id;
		this.name = name;
		groupMembers = new ArrayList<Contact>();
	}

	public boolean addGroupMemeber(Contact contact) {
		if (groupMembers.contains(contact)) {
			return false;
		} else {
			return groupMembers.add(contact);
		}
	}

	public boolean removeGroupMemeber(Contact contact) {
		if (groupMembers.contains(contact)) {
			return false;
		} else {
			return groupMembers.remove(contact);
		}
	}

	public List<Contact> getGroupMemebers(Contact contact) {
		return new ArrayList<Contact>(groupMembers);
	}
}
