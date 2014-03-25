package org.oLabDynamics.client;

/**
 * 
 * Connexion information to access the REST Web Service (entry point URI) and user authentication. 
 * 
 * @author Benoit Charroux
 *
 */
public class ExecShareConnexionFactory {
	
	String[] serverEntryPoints;
	String serverEntryPointsList;
	String userName;
	String password;
	
	public String[] getServerEntryPoints() {
		return serverEntryPoints;
	}

	public String getUserName() {
		return userName;
	}
		
	public String getServerEntryPointsList() {
		return serverEntryPointsList;
	}

	public void setServerEntryPointsList(String serverEntryPointsList) {
		this.serverEntryPointsList = serverEntryPointsList;
		String[] entries = serverEntryPointsList.split(",");
		for(int i=0; i<entries.length; i++){
			entries[i] = entries[i].trim();
		}
		this.serverEntryPoints = entries;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
