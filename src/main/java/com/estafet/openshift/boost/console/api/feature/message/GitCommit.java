
package com.estafet.openshift.boost.console.api.feature.message;

import java.util.List;

import com.estafet.openshift.boost.messages.model.CommitMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sha",
    "node_id",
    "commit",
    "url",
    "html_url",
    "comments_url",
    "author",
    "committer",
    "parents"
})
public class GitCommit {

    @JsonProperty("sha")
    private String sha;
    @JsonProperty("node_id")
    private String nodeId;
    @JsonProperty("commit")
    private Commit commit;
    @JsonProperty("url")
    private String url;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("comments_url")
    private String commentsUrl;
    @JsonProperty("author")
    private Author_ author;
    @JsonProperty("committer")
    private Committer_ committer;
    @JsonProperty("parents")
    private List<Parent> parents = null;

    @JsonProperty("sha")
    public String getSha() {
        return sha;
    }

    @JsonProperty("sha")
    public void setSha(String sha) {
        this.sha = sha;
    }

    @JsonProperty("node_id")
    public String getNodeId() {
        return nodeId;
    }

    @JsonProperty("node_id")
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @JsonProperty("commit")
    public Commit getCommit() {
        return commit;
    }

    @JsonProperty("commit")
    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("html_url")
    public String getHtmlUrl() {
        return htmlUrl;
    }

    @JsonProperty("html_url")
    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    @JsonProperty("comments_url")
    public String getCommentsUrl() {
        return commentsUrl;
    }

    @JsonProperty("comments_url")
    public void setCommentsUrl(String commentsUrl) {
        this.commentsUrl = commentsUrl;
    }

    @JsonProperty("author")
    public Author_ getAuthor() {
        return author;
    }

    @JsonProperty("author")
    public void setAuthor(Author_ author) {
        this.author = author;
    }

    @JsonProperty("committer")
    public Committer_ getCommitter() {
        return committer;
    }

    @JsonProperty("committer")
    public void setCommitter(Committer_ committer) {
        this.committer = committer;
    }

    @JsonProperty("parents")
    public List<Parent> getParents() {
        return parents;
    }

    @JsonProperty("parents")
    public void setParents(List<Parent> parents) {
        this.parents = parents;
    }
    
    public CommitMessage createCommitMessage(String repo) {
    	return CommitMessage.builder()
    			.setCommitId(sha)
    			.setMessage(commit.getMessage())
    			.setRepo(repo)
    			.build();
    }

}
