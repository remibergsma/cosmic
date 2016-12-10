package org.apache.cloudstack.api.command.user.network;

import com.cloud.api.response.ListResponse;
import com.cloud.api.response.NetworkOfferingResponse;
import com.cloud.api.response.NetworkResponse;
import com.cloud.api.response.ZoneResponse;
import com.cloud.offering.NetworkOffering;
import com.cloud.utils.Pair;
import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.BaseListCmd;
import org.apache.cloudstack.api.Parameter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@APICommand(name = "listNetworkOfferings", description = "Lists all available network offerings.", responseObject = NetworkOfferingResponse.class,
        requestHasSensitiveInfo = false, responseHasSensitiveInfo = false)
public class ListNetworkOfferingsCmd extends BaseListCmd {
    public static final Logger s_logger = LoggerFactory.getLogger(ListNetworkOfferingsCmd.class.getName());
    private static final String s_name = "listnetworkofferingsresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////
    @Parameter(name = ApiConstants.ID, type = CommandType.UUID, entityType = NetworkOfferingResponse.class, description = "list network offerings by ID")
    private Long id;

    @Parameter(name = ApiConstants.NAME, type = CommandType.STRING, description = "list network offerings by name")
    private String networkOfferingName;

    @Parameter(name = ApiConstants.DISPLAY_TEXT, type = CommandType.STRING, description = "list network offerings by display text")
    private String displayText;

    @Parameter(name = ApiConstants.TRAFFIC_TYPE, type = CommandType.STRING, description = "list by traffic type")
    private String trafficType;

    @Parameter(name = ApiConstants.IS_DEFAULT, type = CommandType.BOOLEAN, description = "true if need to list only default network offerings. Default value is false")
    private Boolean isDefault;

    @Parameter(name = ApiConstants.SPECIFY_VLAN, type = CommandType.BOOLEAN, description = "the tags for the network offering.")
    private Boolean specifyVlan;

    @Parameter(name = ApiConstants.AVAILABILITY, type = CommandType.STRING, description = "the availability of network offering. Default value is required")
    private String availability;

    @Parameter(name = ApiConstants.ZONE_ID,
            type = CommandType.UUID,
            entityType = ZoneResponse.class,
            description = "list network offerings available for network creation in specific zone")
    private Long zoneId;

    @Parameter(name = ApiConstants.STATE, type = CommandType.STRING, description = "list network offerings by state")
    private String state;

    @Parameter(name = ApiConstants.NETWORK_ID,
            type = CommandType.UUID,
            entityType = NetworkResponse.class,
            description = "the ID of the network. Pass this in if you want to see the available network offering that a network can be changed to.")
    private Long networkId;

    @Parameter(name = ApiConstants.GUEST_IP_TYPE, type = CommandType.STRING, description = "list network offerings by guest type: shared or isolated")
    private String guestIpType;

    @Parameter(name = ApiConstants.SUPPORTED_SERVICES,
            type = CommandType.LIST,
            collectionType = CommandType.STRING,
            description = "list network offerings supporting certain services")
    private List<String> supportedServices;

    @Parameter(name = ApiConstants.SOURCE_NAT_SUPPORTED,
            type = CommandType.BOOLEAN,
            description = "true if need to list only netwok offerings where source NAT is supported, false otherwise")
    private Boolean sourceNatSupported;

    @Parameter(name = ApiConstants.SPECIFY_IP_RANGES,
            type = CommandType.BOOLEAN,
            description = "true if need to list only network offerings which support specifying ip ranges")
    private Boolean specifyIpRanges;

    @Parameter(name = ApiConstants.TAGS, type = CommandType.STRING, description = "list network offerings by tags", length = 4096)
    private String tags;

    @Parameter(name = ApiConstants.IS_TAGGED, type = CommandType.BOOLEAN, description = "true if offering has tags specified")
    private Boolean isTagged;

    @Parameter(name = ApiConstants.FOR_VPC, type = CommandType.BOOLEAN, description = "the network offering can be used" + " only for network creation inside the VPC")
    private Boolean forVpc;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public String getNetworkOfferingName() {
        return networkOfferingName;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getTrafficType() {
        return trafficType;
    }

    public Long getId() {
        return id;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public Boolean getSpecifyVlan() {
        return specifyVlan;
    }

    public String getAvailability() {
        return availability;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public String getState() {
        return state;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public String getGuestIpType() {
        return guestIpType;
    }

    public List<String> getSupportedServices() {
        return supportedServices;
    }

    public Boolean getSourceNatSupported() {
        return sourceNatSupported;
    }

    public Boolean getSpecifyIpRanges() {
        return specifyIpRanges;
    }

    public String getTags() {
        return tags;
    }

    public Boolean isTagged() {
        return isTagged;
    }

    public Boolean getForVpc() {
        return forVpc;
    }

    @Override
    public void execute() {
        final Pair<List<? extends NetworkOffering>, Integer> offerings = _configService.searchForNetworkOfferings(this);
        final ListResponse<NetworkOfferingResponse> response = new ListResponse<>();
        final List<NetworkOfferingResponse> offeringResponses = new ArrayList<>();
        for (final NetworkOffering offering : offerings.first()) {
            final NetworkOfferingResponse offeringResponse = _responseGenerator.createNetworkOfferingResponse(offering);
            offeringResponses.add(offeringResponse);
        }

        response.setResponses(offeringResponses, offerings.second());
        response.setResponseName(getCommandName());
        this.setResponseObject(response);
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////
    @Override
    public String getCommandName() {
        return s_name;
    }
}
