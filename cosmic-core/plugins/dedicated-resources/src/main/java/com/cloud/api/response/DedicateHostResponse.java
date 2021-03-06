package com.cloud.api.response;

import com.cloud.api.BaseResponse;
import com.cloud.serializer.Param;

import com.google.gson.annotations.SerializedName;

public class DedicateHostResponse extends BaseResponse {
    @SerializedName("id")
    @Param(description = "the ID of the dedicated resource")
    private String id;

    @SerializedName("hostid")
    @Param(description = "the ID of the host")
    private String hostId;

    @SerializedName("hostname")
    @Param(description = "the name of the host")
    private String hostName;

    @SerializedName("domainid")
    @Param(description = "the domain ID of the host")
    private String domainId;

    @SerializedName("accountid")
    @Param(description = "the Account ID of the host")
    private String accountId;

    @SerializedName("affinitygroupid")
    @Param(description = "the Dedication Affinity Group ID of the host")
    private String affinityGroupId;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(final String hostId) {
        this.hostId = hostId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(final String hostName) {
        this.hostName = hostName;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(final String domainId) {
        this.domainId = domainId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(final String accountId) {
        this.accountId = accountId;
    }

    public String getAffinityGroupId() {
        return affinityGroupId;
    }

    public void setAffinityGroupId(final String affinityGroupId) {
        this.affinityGroupId = affinityGroupId;
    }
}
