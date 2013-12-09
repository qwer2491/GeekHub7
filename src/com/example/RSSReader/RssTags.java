package com.example.RSSReader;

import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 09.11.13
 * Time: 23:06
 * To change this template use File | Settings | File Templates.
 */
public class RssTags {
    String title="";
    String link="";
    String description="";
    String pubDate="";

    public RssTags(String title, String link, String description, String pubDate) {
        this.title = title;
        this.pubDate = pubDate;
        this.link = link;
        this.description = description;
    }

    public RssTags(Element title, Element link, Element description, Element pubDate) {
        this.title = title.getTextContent();
        this.pubDate = pubDate.getTextContent();
        this.link = link.getTextContent();
        this.description = description.getTextContent();
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }


    @Override
    public String toString() {
        return "\n" +
                 title +"\n" +
                 link + "\n" +
                 description + "\n" +
                 pubDate + "\n" +
                "\n";
    }

    public String toStringWithOutTitle() {
        return  description + "\n"+ "\n" +
                link + "\n" + "\n"+
                pubDate + "\n" ;
    }
}
