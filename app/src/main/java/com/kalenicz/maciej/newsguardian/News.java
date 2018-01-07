package com.kalenicz.maciej.newsguardian;


public class News {
    private String headline;
    private String author;
    private String timePublished;
    private String description;
    private String articleLink;
    private String section;

    public News(String headline, String author, String timePublished, String description, String articleLink, String section) {
        this.headline = headline;
        this.author = author;
        this.timePublished = timePublished;
        this.description = description;
        this.articleLink = articleLink;
        this.section = section;
    }

    public String getHeadline() {
        return headline;
    }

    public String getAuthor() {
        return author;
    }

    public String getTimePublished() {
        return timePublished;
    }

    public String getDescription() {
        return description;
    }

    public String getArticleLink() {
        return articleLink;
    }

    public String getSection() {
        return section;
    }
}
