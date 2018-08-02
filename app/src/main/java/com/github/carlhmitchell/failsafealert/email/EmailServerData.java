package com.github.carlhmitchell.failsafealert.email;

//Model

public class EmailServerData {
    private String serverName;
    private String protocol;
    private String mailhost;
    private int port;
    private boolean auth;
    private int sslport;
    private boolean fallback;
    private boolean quitwait;

    /**
     * Constructor for EmailServerData. Provides gmail as a default.
     */
    EmailServerData() {
        serverName = "Gmail";
        protocol = "smtp";
        mailhost = "smtp.gmail.com";
        port = 465;
        auth = true;
        sslport = 465;
        fallback = false;
        quitwait = false;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getMailhost() {
        return mailhost;
    }

    public void setMailhost(String mailhost) {
        this.mailhost = mailhost;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean getAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public int getSslport() {
        return sslport;
    }

    public void setSslport(int sslport) {
        this.sslport = sslport;
    }

    public boolean getFallback() {
        return fallback;
    }

    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }

    public boolean getQuitwait() {
        return quitwait;
    }

    public void setQuitwait(boolean quitwait) {
        this.quitwait = quitwait;
    }
}
