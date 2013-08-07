package examples.cdi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.internal.task.api.UserGroupCallback;

public class MyUserGroupCallback implements UserGroupCallback {
	
	@SuppressWarnings("serial")
	private Set<String> users = new HashSet<String>(){{
		add("Administrator".intern());
		add("Daniel".intern());
		add("Mondo".intern());
		add("Gaspar".intern()); 
	}};

	@Override
	public boolean existsGroup(String arg0) {
		return false;
	}

	@Override
	public boolean existsUser(String userName) {
		// In a real scenario, this should be the code accessing LDAP etc.
		return users.contains(userName.intern());
	}

	@Override
	public List<String> getGroupsForUser(String arg0, List<String> arg1,
			List<String> arg2) {
		return null;
	}

}
