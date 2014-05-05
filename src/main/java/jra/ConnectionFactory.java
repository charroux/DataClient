package jra;

public interface ConnectionFactory {
	
	String getLibraryForReproducibilityURL();
	
	String getInfrastructureForReproducibilityURL();
	
	int getMajorVersion();

	int	getMinorVersion();
	
	String getProviderName();
	
	String 	getVersion();
	
	int getProviderMajorVersion();
	
	int getProviderMinorVersion();
	
	String 	getProviderVersion(); 

}
