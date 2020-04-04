package com.fusui.tapir.client.rs;
///*
// * 
// */
//package com.fusui.tapir.facade.cli;
//
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//
//import org.glassfish.jersey.client.ClientConfig;
//
//import com.fusui.tapir.common.FoundryProperties;
//
///**
// * Creates SSL-aware Jersey clients.
// */
//public class JerseyClientFactory {
//
//    private final SSLContextBuilder builder = SSLContextBuilder.newBuilder();
//
//    private JerseyClientFactory() {}
//
//    public static JerseyClientFactory newFactory() {
//        return new JerseyClientFactory();
//    }
//
//    public void setKeystorePassword(String password) {
//        builder.setKeystorePassword(password);
//    }
//
//    public void setKeystorePath(String path) {
//        builder.setKeystorePath(path);
//    }
//
//    public void setKeystoreType(String type) {
//        builder.setKeystoreType(type);
//    }
//
//    public void setTruststorePassword(String password) {
//        builder.setTruststorePassword(password);
//    }
//
//    public void setTruststorePath(String path) {
//        builder.setTruststorePath(path);
//    }
//
//    public void setTruststoreType(String type) {
//        builder.setTruststoreType(type);
//    }
//
//    public JerseyClientFactory withProperties(FoundryProperties properties) {
//        setTruststorePath(properties.getTruststorePath());
//        setTruststorePassword(properties.getTruststorePassword());
//        setKeystorePath(properties.getKeystorePath());
//        setKeystorePassword(properties.getKeystorePassword());
//        return this;
//    }
//
//    public Client create() {
//    	// Jersey 1.x
////    	final ClientConfig config = new DefaultClientConfig();
////    	final HTTPSProperties props = new HTTPSProperties(null, builder.build()); // Null host verifier to skip
////    	config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, props);
////    	config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
////    	return Client.create(config);
//        
//    	final ClientConfig config = new ClientConfig();
//        return ClientBuilder.newBuilder()
//                .withConfig(config)
//                //.hostnameVerifier(new TrustAllHostNameVerifier())
//                .sslContext(builder.build())
//                .build();
//    }
//
//}
