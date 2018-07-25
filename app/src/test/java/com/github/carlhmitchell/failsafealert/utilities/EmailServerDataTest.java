package com.github.carlhmitchell.failsafealert.utilities;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class EmailServerDataTest {
    private EmailServerData data;

    public EmailServerDataTest() {
        data = new EmailServerData();
        data.setQuitwait(false);
        data.setFallback(false);
        data.setSslport(101);
        data.setAuth(true);
        data.setMailhost("test.example.com");
        data.setProtocol("smtp");
        data.setPort(100);
        data.setServerName("Test Name");
    }

    @Test
    public void getProtocol() {
        Assert.assertEquals("smtp", data.getProtocol());
    }
    @Test
    public void setProtocol() {
        data.setProtocol("abcd");
        Assert.assertEquals("abcd", data.getProtocol());
    }

    @Test
    public void getServerName() {
        Assert.assertEquals("Test Name", data.getServerName());
    }
    @Test
    public void setServerName() {
        data.setServerName("Test SetServerName Name");
        Assert.assertEquals("Test SetServerName Name", data.getServerName());
    }

    @Test
    public void getMailhost() {
        Assert.assertEquals("test.example.com", data.getMailhost());
    }
    @Test
    public void setMailhost() {
        data.setMailhost("smtp.example.com");
        Assert.assertEquals("smtp.example.com", data.getMailhost());
    }

    @Test
    public void getPort() {
        Assert.assertEquals(100, data.getPort());
    }
    @Test
    public void setPort() {
        data.setPort(555);
        Assert.assertEquals(555, data.getPort());
    }

    @Test
    public void getAuth() {
        Assert.assertTrue(data.getAuth());
    }
    @Test
    public void setAuth() {
        data.setAuth(false);
        Assert.assertFalse(data.getAuth());
    }

    @Test
    public void getSslport() {
        Assert.assertEquals(101, data.getSslport());
    }
    @Test
    public void setSslport() {
        data.setSslport(465);
        Assert.assertEquals(465, data.getSslport());
    }

    @Test
    public void getFallback() {
        Assert.assertFalse(data.getFallback());
    }
    @Test
    public void setFallback() {
        data.setFallback(true);
        Assert.assertTrue(data.getFallback());
    }

    @Test
    public void getQuitwait() {
        Assert.assertFalse(data.getQuitwait());
    }
    @Test
    public void setQuitwait() {
        data.setQuitwait(true);
        Assert.assertTrue(data.getQuitwait());
    }
}