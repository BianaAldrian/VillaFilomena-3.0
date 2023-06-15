package com.example.villafilomena.Models;

public class Comments_Model {
    String id, comment_to, comment_by, comment;

    public Comments_Model(String id, String comment_to, String comment_by, String comment) {
        this.id = id;
        this.comment_to = comment_to;
        this.comment_by = comment_by;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public String getComment_to() {
        return comment_to;
    }

    public String getComment_by() {
        return comment_by;
    }

    public String getComment() {
        return comment;
    }
}
