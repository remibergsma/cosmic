package org.apache.cloudstack.api.command.admin.usage;

import com.cloud.api.response.SuccessResponse;
import com.cloud.exception.ConcurrentOperationException;
import com.cloud.exception.InsufficientCapacityException;
import com.cloud.exception.NetworkRuleConflictException;
import com.cloud.exception.ResourceAllocationException;
import com.cloud.exception.ResourceUnavailableException;
import com.cloud.user.Account;
import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.ApiErrorCode;
import org.apache.cloudstack.api.BaseCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@APICommand(name = "removeRawUsageRecords", description = "Safely removes raw records from cloud_usage table", responseObject = SuccessResponse.class, since = "4.6.0",
        requestHasSensitiveInfo = false, responseHasSensitiveInfo = false)
public class RemoveRawUsageRecordsCmd extends BaseCmd {
    public static final Logger s_logger = LoggerFactory.getLogger(RemoveRawUsageRecordsCmd.class.getName());

    private static final String s_name = "removerawusagerecordsresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////
    @Parameter(name = ApiConstants.INTERVAL, type = CommandType.INTEGER, required = true,
            description = "Specify the number of days (greater than zero) to remove records that are older than those number of days from today. For example, specifying 10 would" +
                    " result in removing all the records created before 10 days from today")
    private Integer interval;

    public Integer getInterval() {
        return interval;
    }

    @Override
    public void execute() throws ResourceUnavailableException, InsufficientCapacityException, ServerApiException, ConcurrentOperationException, ResourceAllocationException,
            NetworkRuleConflictException {

        final boolean result = _usageService.removeRawUsageRecords(this);
        if (result) {
            final SuccessResponse response = new SuccessResponse(getCommandName());
            response.setResponseName(getCommandName());
            this.setResponseObject(response);
        } else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR,
                    "Failed to remove old raw usage records from cloud_usage table, a job is likely running at this time. Please try again after 15 mins.");
        }
    }

    @Override
    public String getCommandName() {
        return s_name;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public long getEntityOwnerId() {
        return Account.ACCOUNT_ID_SYSTEM;
    }
}
