package com.fusui.tapir.common;


/*
 * @author gko
 */

// GKO TODO move all properties to external resource. DB or file
public class FoundryProperties {
	

	private static final FoundryProperties singleton = new FoundryProperties();
	
	private String dsType							= "jndi";
	private String dsJndiName						= "java:comp/env/jdbc/workflow";
	private String dsDriverClass					= null; 
	private String dsUrl							= null;
	private String dsUsername						= null;
	private String dsPassword						= null;
	// SSL cert 
	
	//truststore_path=com.bom.was.pki.client.keystore.filename.self
	//truststore_password=com.bom.was.pki.client.keystore.password.self
	//keystore_path=com.bom.was.pki.trustedAuthorities.keystore.filename
	//keystore_password=com.bom.was.pki.trustedAuthorities.keystore.password
	
	private String truststorePath = null;
	private String truststorePassword = null;
	private String keystorePath = null;
	private String keystorePassword = null;

	private boolean jerseyServerIgnoreClient=false;
		
	
	private FoundryProperties() {
	}
	
	public static FoundryProperties getFoundryProperties() {
		return singleton;
	}
	
	public void reset() {
		dsType	= "jndi";
		dsJndiName = "java:comp/env/jdbc/workflow";
		dsDriverClass = null; 
		dsUrl = null;
		dsUsername = null;
		dsPassword = null;
		
		
		truststorePath = null;
		keystorePath = null;
		
		jerseyServerIgnoreClient=false;
	}

	
	/***********************************************************************************
	 *
	 * Data Source
	 * 
	 ***********************************************************************************/

	public String getDsType() {
		return dsType;
	}

	public void setDsType(String dataSourceType) {
		this.dsType = dataSourceType;
	}
	
	public String getDsJndiName() {
		return dsJndiName;
	}

	public void setDsJndiName(String dsJndiName) {
		this.dsJndiName = dsJndiName;
	}

	public String getDsDriverClass() {
		return dsDriverClass;
	}

	public void setDsDriverClass(String dsDriverClass) {
		this.dsDriverClass = dsDriverClass;
	}

	public String getDsUrl() {
		return dsUrl;
	}

	public void setDsUrl(String dsUrl) {
		this.dsUrl = dsUrl;
	}

	public String getDsUsername() {
		return dsUsername;
	}

	public void setDsUsername(String dsUsername) {
		this.dsUsername = dsUsername;
	}
	
	public String getDsPassword() {
		return dsPassword;
	}

	public void setDsPassword(String dsPassword) {
		this.dsPassword = dsPassword;
	}
	

	
	public String getTruststorePath() {
		return truststorePath;
	}

	public void setTruststorePath(String truststorePath) {
		this.truststorePath = truststorePath;
	}

	public String getTruststorePassword() {
		return truststorePassword;
	}

	public void setTruststorePassword(String truststorePassword) {
		this.truststorePassword = truststorePassword;
	}

	public String getKeystorePath() {
		return keystorePath;
	}

	public void setKeystorePath(String keystorePath) {
		this.keystorePath = keystorePath;
	}

	public String getKeystorePassword() {
		return keystorePassword;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	
	/***********************************************************************************
	 *
	 * Common
	 * 
	 ***********************************************************************************/

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append ("version="+FoundryVersion.FOUNDRY_VERSION);
		sb.append("\n");
	
		return sb.toString();
	}

	

}
