
package com.estafet.openshift.boost.console.api.feature.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "zipball_url",
    "tarball_url",
    "commit",
    "node_id"
})
public class GitTag {

    @JsonProperty("name")
    private String name;
    @JsonProperty("zipball_url")
    private String zipballUrl;
    @JsonProperty("tarball_url")
    private String tarballUrl;
    @JsonProperty("commit")
    private CommitTag commit;
    @JsonProperty("node_id")
    private String nodeId;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("zipball_url")
    public String getZipballUrl() {
        return zipballUrl;
    }

    @JsonProperty("zipball_url")
    public void setZipballUrl(String zipballUrl) {
        this.zipballUrl = zipballUrl;
    }

    @JsonProperty("tarball_url")
    public String getTarballUrl() {
        return tarballUrl;
    }

    @JsonProperty("tarball_url")
    public void setTarballUrl(String tarballUrl) {
        this.tarballUrl = tarballUrl;
    }

    @JsonProperty("commit")
    public CommitTag getCommit() {
        return commit;
    }

    @JsonProperty("commit")
    public void setCommit(CommitTag commit) {
        this.commit = commit;
    }

    @JsonProperty("node_id")
    public String getNodeId() {
        return nodeId;
    }

    @JsonProperty("node_id")
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

}
