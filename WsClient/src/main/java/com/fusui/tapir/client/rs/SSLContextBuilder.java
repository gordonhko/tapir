package com.fusui.tapir.client.rs;
///*
// *
// */
//package com.fusui.tapir.facade.cli;
//
//import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.security.KeyStore;
//import java.security.SecureRandom;
//
//import javax.net.ssl.KeyManagerFactory;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManagerFactory;
//
///**
// * This provides a builder and bean-like interface for creating {@link SSLContext} instances.
// *
// * TODO Move this to a common project.
// */
//public class SSLContextBuilder {
//
//   // private Decryptor decryptor = Decryptor.getDefault();
//    private String pathPrefix = null;
//    private String keystorePath = null;
//    private String keystorePassword = null;
//    private String keystoreType = "JKS";
//    private String truststorePath = null;
//    private String truststorePassword = null;
//    private String truststoreType = "JKS";
//    private SecureRandom secureRandom = null;
//
//    public static SSLContextBuilder newBuilder() {
//        return new SSLContextBuilder();
//    }
//
////    public void setDecryptor(Decryptor decryptor) {
////        this.decryptor = decryptor;
////    }
////
////    public SSLContextBuilder decryptor(Decryptor decryptor) {
////        setDecryptor(decryptor);
////        return this;
////    }
//
//    public void setPathPrefix(String path) {
//        this.pathPrefix = path;
//    }
//
//    public SSLContextBuilder pathPrefix(String path) {
//        setPathPrefix(path);
//        return this;
//    }
//
//    public void setKeystorePassword(String password) {
//        this.keystorePassword = password;
//    }
//
//    public SSLContextBuilder keystorePassword(String password) {
//        setKeystorePassword(password);
//        return this;
//    }
//
//    public void setKeystorePath(String path) {
//        this.keystorePath = path;
//    }
//
//    public SSLContextBuilder keystorePath(String path) {
//        setKeystorePath(path);
//        return this;
//    }
//
//    public void setKeystoreType(String type) {
//        this.keystoreType = type;
//    }
//
//    public SSLContextBuilder keystoreType(String type) {
//        setKeystoreType(type);
//        return this;
//    }
//
//    public void setTruststorePassword(String password) {
//        this.truststorePassword = password;
//    }
//
//    public SSLContextBuilder truststorePassword(String password) {
//        setTruststorePassword(password);
//        return this;
//    }
//
//    public void setTruststorePath(String path) {
//        this.truststorePath = path;
//    }
//
//    public SSLContextBuilder truststorePath(String path) {
//        setTruststorePath(path);
//        return this;
//    }
//
//    public void setTruststoreType(String type) {
//        this.truststoreType = type;
//    }
//
//    public SSLContextBuilder truststoreType(String type) {
//        setTruststoreType(type);
//        return this;
//    }
//
//    public void setSecureRandom(SecureRandom rng) {
//        this.secureRandom = rng;
//    }
//
//    public SSLContextBuilder secureRandom(SecureRandom rng) {
//        setSecureRandom(rng);
//        return this;
//    }
//
//    private BufferedInputStream open(String path) throws IOException {
//        String parent = pathPrefix;
//        if (parent == null) {
//            // Legacy approach to creating an absolute path
//            final String prop = System.getProperty("com.bom.was.dir.propertyname");
//            if (prop != null) {
//                parent = System.getProperty(prop, null);
//            }
//        }
//        return new BufferedInputStream(new FileInputStream(new File(parent, path)));
//    }
//
//    private char[] decrypt(String password) {
//        if (password == null) {
//            return null;
//        } else {
//        	return password.toCharArray();
//            //return decryptor.decrypt(password).toCharArray();
//        }
//    }
//
//    public SSLContext build() {
//        if (keystoreType == null) throw new IllegalStateException("Keystore type is null");
//        if (keystorePath == null) throw new IllegalStateException("Keystore path is null");
//        if (truststoreType == null) throw new IllegalStateException("Truststore type is null");
//        if (truststorePath == null) throw new IllegalStateException("Truststore path is null");
//
//        try {
//            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            final KeyStore ts = KeyStore.getInstance(truststoreType);
//            final BufferedInputStream tss = open(truststorePath);
//            try {
//                ts.load(tss, decrypt(truststorePassword));
//            } finally {
//                tss.close();
//            }
//            tmf.init(ts);
//
//            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//            final KeyStore ks = KeyStore.getInstance(keystoreType);
//            final BufferedInputStream kss = open(keystorePath);
//            try {
//                ks.load(kss, decrypt(keystorePassword));
//            } finally {
//                kss.close();
//            }
//            kmf.init(ks, decrypt(keystorePassword));
//
//            final SSLContext ctx = SSLContext.getInstance("TLS");
//            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom);
//            return ctx;
//        } catch (Exception e) {
//            throw new IllegalStateException("Failed to create SSLContext", e);
//        }
//    }
//
//}
