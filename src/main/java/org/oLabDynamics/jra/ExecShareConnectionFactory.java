package org.oLabDynamics.jra;

import jra.ConnectionFactory;

/**
 * 
 * Connexion information to access the REST Web Service (entry point URI) and user authentication. 
 * 
 * @author Benoit Charroux
 *
 */
public class ExecShareConnectionFactory implements ConnectionFactory{
	
	//String[] serverEntryPoints;
	//String serverEntryPointsList;
	String libraryForReproducibilityURL;
	String infrastructureForReproducibilityURL;
	String userName;
	String password;
	
	/*public String[] getServerEntryPoints() {
		return serverEntryPoints;
	}*/

	public String getUserName() {
		return userName;
	}
		
/*	public String getServerEntryPointsList() {
		return serverEntryPointsList;
	}

	public void setServerEntryPointsList(String serverEntryPointsList) {
		this.serverEntryPointsList = serverEntryPointsList;
		String[] entries = serverEntryPointsList.split(",");
		for(int i=0; i<entries.length; i++){
			entries[i] = entries[i].trim();
		}
		this.serverEntryPoints = entries;
	}*/
	

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Override
	public String getLibraryForReproducibilityURL() {
		return libraryForReproducibilityURL;
	}

	public void setLibraryForReproducibilityURL(String libraryForReproducibilityURL) {
		this.libraryForReproducibilityURL = libraryForReproducibilityURL;
	}

	@Override
	public String getInfrastructureForReproducibilityURL() {
		return infrastructureForReproducibilityURL;
	}

	public void setInfrastructureForReproducibilityURL(
			String infrastructureForReproducibilityURL) {
		this.infrastructureForReproducibilityURL = infrastructureForReproducibilityURL;
	}

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int getMajorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getProviderName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getProviderMajorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getProviderMinorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getProviderVersion() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
