
package com.estafet.openshift.boost.console.api.feature.model.commit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "author",
    "committer",
    "message",
    "tree",
    "url",
    "comment_count",
    "verification"
})
public class Commit {

    @JsonProperty("author")
    private Author author;
    @JsonProperty("committer")
    private Committer committer;
    @JsonProperty("message")
    private String message;
    @JsonProperty("tree")
    private Tree tree;
    @JsonProperty("url")
    private String url;
    @JsonProperty("comment_count")
    private Integer commentCount;
    @JsonProperty("verification")
    private Verification verification;

    @JsonProperty("author")
    public Author getAuthor() {
        return author;
    }

    @JsonProperty("author")
    public void setAuthor(Author author) {
        this.author = author;
    }

    @JsonProperty("committer")
    public Committer getCommitter() {
        return committer;
    }

    @JsonProperty("committer")
    public void setCommitter(Committer committer) {
        this.committer = committer;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("tree")
    public Tree getTree() {
        return tree;
    }

    @JsonProperty("tree")
    public void setTree(Tree tree) {
        this.tree = tree;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("comment_count")
    public Integer getCommentCount() {
        return commentCount;
    }

    @JsonProperty("comment_count")
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    @JsonProperty("verification")
    public Verification getVerification() {
        return verification;
    }

    @JsonProperty("verification")
    public void setVerification(Verification verification) {
        this.verification = verification;
    }

}
