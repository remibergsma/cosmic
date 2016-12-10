package org.apache.cloudstack.api.command.admin.host;

import com.cloud.api.response.HostResponse;
import com.cloud.event.EventTypes;
import com.cloud.host.Host;
import com.cloud.user.Account;
import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiCommandJobType;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.ApiErrorCode;
import org.apache.cloudstack.api.BaseAsyncCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.context.CallContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@APICommand(name = "prepareHostForMaintenance", description = "Prepares a host for maintenance.", responseObject = HostResponse.class,
        requestHasSensitiveInfo = false, responseHasSensitiveInfo = false)
public class PrepareForMaintenanceCmd extends BaseAsyncCmd {
    public static final Logger s_logger = LoggerFactory.getLogger(PrepareForMaintenanceCmd.class.getName());

    private static final String s_name = "preparehostformaintenanceresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name = ApiConstants.ID, type = CommandType.UUID, entityType = HostResponse.class, required = true, description = "the host ID")
    private Long id;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public static String getResultObjectName() {
        return "host";
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getEventType() {
        return EventTypes.EVENT_MAINTENANCE_PREPARE;
    }

    @Override
    public String getEventDescription() {
        return "preparing host: " + getId() + " for maintenance";
    }

    public Long getId() {
        return id;
    }

    @Override
    public Long getInstanceId() {
        return getId();
    }

    @Override
    public ApiCommandJobType getInstanceType() {
        return ApiCommandJobType.Host;
    }

    @Override
    public void execute() {
        final Host result = _resourceService.maintain(this);
        if (result != null) {
            final HostResponse response = _responseGenerator.createHostResponse(result);
            response.setResponseName("host");
            this.setResponseObject(response);
        } else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Failed to prepare host for maintenance");
        }
    }

    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public long getEntityOwnerId() {
        final Account account = CallContext.current().getCallingAccount();
        if (account != null) {
            return account.getId();
        }

        return Account.ACCOUNT_ID_SYSTEM; // no account info given, parent this command to SYSTEM so ERROR events are tracked
    }
}
