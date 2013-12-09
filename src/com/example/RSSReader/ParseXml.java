package com.example.RSSReader;

import android.os.AsyncTask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ParseXml extends AsyncTask<URL, String, ArrayList<RssTags>> {
    final String log = "createLog";
    public static  ArrayList<RssTags>  rssArrayList = null;
    HttpURLConnection urlConnection = null;
    InputStream inputStream=null;

    @Override
    protected ArrayList<RssTags> doInBackground(URL... urls) {

        try {

            urlConnection = (HttpURLConnection) urls[0].openConnection();
            // urlConnection.setRequestMethod("GET");
            // urlConnection.setDoOutput(true);
            urlConnection.connect();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            inputStream = urlConnection.getInputStream();
            Document doc = builder.parse(inputStream);


             rssArrayList = new ArrayList<RssTags>();

            NodeList nodes = doc.getElementsByTagName("item");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                NodeList title = element.getElementsByTagName("title");
                NodeList link = element.getElementsByTagName("link");
                NodeList description = element.getElementsByTagName("description");
                NodeList pubDate = element.getElementsByTagName("pubDate");

                Element eTitle = (Element) title.item(0); //
                Element eLink = (Element) link.item(0);
                Element eDescription = (Element) description.item(0);
                Element ePubDate = (Element) pubDate.item(0);

                RssTags rssTags = new RssTags(eTitle,eLink,eDescription,ePubDate);
//
                rssArrayList.add(rssTags);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }


        return rssArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<RssTags> s) {
       // Log.d(log, s+");
    }
}
