package com.github.carlhmitchell.failsafealert.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.github.carlhmitchell.failsafealert.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class EmailServerDataXmlParser {

    public static String lastErrorMessage = "";
    private static final String DEBUG_TAG = EmailServerDataXmlParser.class.getSimpleName();

    /**
     * Static method to parse XML data into an ArrayList of EmailServerData objects
     * @param context is the calling context
     * @param xmlResourceId is the resource id of the XML resource to be parsed
     * @return null if parse error, or ArrayList of objects if successful
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static ArrayList<EmailServerData> parse(Context context, int xmlResourceId) {
        Resources res = context.getApplicationContext().getResources();
        XmlResourceParser parser = res.getXml(xmlResourceId);
        String text = "";

        ArrayList<EmailServerData> serverDataArrayList = new ArrayList<>();
        try {
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
                            currentServer.setServerName(text);
                        } else if (tagname.equalsIgnoreCase("protocol")) {
                            currentServer.setProtocol(text);
                        } else if (tagname.equalsIgnoreCase("mailhost")) {
                            currentServer.setMailhost(text);
                        } else if (tagname.equalsIgnoreCase("port")) {
                            currentServer.setPort(Integer.parseInt(text));
                        } else if (tagname.equalsIgnoreCase("auth")) {
                            currentServer.setAuth(Boolean.parseBoolean(text));
                        } else if (tagname.equalsIgnoreCase("sslport")) {
                            currentServer.setSslport(Integer.parseInt(text));
                        } else if (tagname.equalsIgnoreCase("fallback")) {
                            currentServer.setFallback(Boolean.parseBoolean(text));
                        } else if (tagname.equalsIgnoreCase("quitwait")) {
                            currentServer.setQuitwait(Boolean.parseBoolean(text));
                        } else {
                            Log.e(DEBUG_TAG, "Error, invalid XML!");
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(DEBUG_TAG, "Parse error");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(DEBUG_TAG, "IO Error");
            e.printStackTrace();
        }
        return serverDataArrayList;
    }
}
