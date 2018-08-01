package com.github.carlhmitchell.failsafealert.utilities;

//Model

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class EmailServerDataXmlParser {
    private static final String DEBUG_TAG = EmailServerDataXmlParser.class.getSimpleName();

    /**
     * Static method to parse XML data into an ArrayList of EmailServerData objects
     *
     * @param context is the calling context
     * @param xmlResourceId is the resource id of the XML resource to be parsed
     * @return null if parse error, or ArrayList of objects if successful
     */
    public static ArrayList<EmailServerData> parse(Context context, int xmlResourceId) {
        ArrayList<EmailServerData> serverDataArrayList = new ArrayList<>();
        try {
            Resources res = context.getApplicationContext().getResources();
            XmlResourceParser parser = res.getXml(xmlResourceId);
            String text = "";

            EmailServerData currentServer = null;
            parser.next();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("server")) {
                            // Make a new instance of EmailServerData
                            currentServer = new EmailServerData();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("server")) {
                            // Add the server to the list
                            serverDataArrayList.add(currentServer);
                        } else if (tagname.equalsIgnoreCase("serverName")) {
                            Objects.requireNonNull(currentServer).setServerName(text);
                        } else if (tagname.equalsIgnoreCase("protocol")) {
                            Objects.requireNonNull(currentServer).setProtocol(text);
                        } else if (tagname.equalsIgnoreCase("mailhost")) {
                            Objects.requireNonNull(currentServer).setMailhost(text);
                        } else if (tagname.equalsIgnoreCase("port")) {
                            Objects.requireNonNull(currentServer).setPort(Integer.parseInt(text));
                        } else if (tagname.equalsIgnoreCase("auth")) {
                            Objects.requireNonNull(currentServer).setAuth(Boolean.parseBoolean(text));
                        } else if (tagname.equalsIgnoreCase("sslport")) {
                            Objects.requireNonNull(currentServer).setSslport(Integer.parseInt(text));
                        } else if (tagname.equalsIgnoreCase("fallback")) {
                            Objects.requireNonNull(currentServer).setFallback(Boolean.parseBoolean(text));
                        } else if (tagname.equalsIgnoreCase("quitwait")) {
                            Objects.requireNonNull(currentServer).setQuitwait(Boolean.parseBoolean(text));
                        } else if (tagname.equalsIgnoreCase("server") || tagname.equalsIgnoreCase("emailServers")) {
                            // No-op, closing the list.
                            SDLog.v(DEBUG_TAG, "Got an ending tag");
                        } else {
                            SDLog.e(DEBUG_TAG, "Error, invalid XML!");
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            SDLog.e(DEBUG_TAG, "Parse error");
            e.printStackTrace();
        } catch (IOException e) {
            SDLog.e(DEBUG_TAG, "IO Error");
            e.printStackTrace();
        } catch (NullPointerException e) {
            SDLog.e(DEBUG_TAG, "Null pointer exception");
            e.printStackTrace();
        }
        return serverDataArrayList;
    }
}
