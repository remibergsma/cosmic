package com.cloud.api.command.admin.usage;

import com.cloud.api.APICommand;
import com.cloud.api.ApiConstants;
import com.cloud.api.BaseListCmd;
import com.cloud.api.Parameter;
import com.cloud.api.command.user.offering.ListServiceOfferingsCmd;
import com.cloud.api.response.ListResponse;
import com.cloud.api.response.TrafficMonitorResponse;
import com.cloud.api.response.ZoneResponse;
import com.cloud.host.Host;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@APICommand(name = "listTrafficMonitors", description = "List traffic monitor Hosts.", responseObject = TrafficMonitorResponse.class,
        requestHasSensitiveInfo = false, responseHasSensitiveInfo = false)
public class ListTrafficMonitorsCmd extends BaseListCmd {
    public static final Logger s_logger = LoggerFactory.getLogger(ListServiceOfferingsCmd.class.getName());
    private static final String s_name = "listtrafficmonitorsresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name = ApiConstants.ZONE_ID, type = CommandType.UUID, entityType = ZoneResponse.class, required = true, description = "zone Id")
    private long zoneId;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public long getZoneId() {
        return zoneId;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public void execute() {
        final List<? extends Host> trafficMonitors = _networkUsageService.listTrafficMonitors(this);

        final ListResponse<TrafficMonitorResponse> listResponse = new ListResponse<>();
        final List<TrafficMonitorResponse> responses = new ArrayList<>();
        for (final Host trafficMonitor : trafficMonitors) {
            final TrafficMonitorResponse response = _responseGenerator.createTrafficMonitorResponse(trafficMonitor);
            response.setObjectName("trafficmonitor");
            response.setResponseName(getCommandName());
            responses.add(response);
        }

        listResponse.setResponses(responses);
        listResponse.setResponseName(getCommandName());
        this.setResponseObject(listResponse);
    }

    @Override
    public String getCommandName() {
        return s_name;
    }
}
