package org.apache.cloudstack.api.command.user.autoscale;

import com.cloud.api.response.AutoScaleVmProfileResponse;
import com.cloud.api.response.TemplateResponse;
import com.cloud.api.response.UserResponse;
import com.cloud.event.EventTypes;
import com.cloud.network.as.AutoScaleVmProfile;
import com.cloud.user.Account;
import org.apache.cloudstack.acl.RoleType;
import org.apache.cloudstack.acl.SecurityChecker.AccessType;
import org.apache.cloudstack.api.ACL;
import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiCommandJobType;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.ApiErrorCode;
import org.apache.cloudstack.api.BaseAsyncCustomIdCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.context.CallContext;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@APICommand(name = "updateAutoScaleVmProfile", description = "Updates an existing autoscale vm profile.", responseObject = AutoScaleVmProfileResponse.class, entityType =
        {AutoScaleVmProfile.class},
        requestHasSensitiveInfo = false, responseHasSensitiveInfo = false)
public class UpdateAutoScaleVmProfileCmd extends BaseAsyncCustomIdCmd {
    public static final Logger s_logger = LoggerFactory.getLogger(UpdateAutoScaleVmProfileCmd.class.getName());

    private static final String s_name = "updateautoscalevmprofileresponse";

    // ///////////////////////////////////////////////////
    // ////////////// API parameters /////////////////////
    // ///////////////////////////////////////////////////

    @ACL(accessType = AccessType.OperateEntry)
    @Parameter(name = ApiConstants.ID,
            type = CommandType.UUID,
            entityType = AutoScaleVmProfileResponse.class,
            required = true,
            description = "the ID of the autoscale vm profile")
    private Long id;

    @Parameter(name = ApiConstants.TEMPLATE_ID,
            type = CommandType.UUID,
            entityType = TemplateResponse.class,
            description = "the template of the auto deployed virtual machine")
    private Long templateId;

    @Parameter(name = ApiConstants.AUTOSCALE_VM_DESTROY_TIME,
            type = CommandType.INTEGER,
            description = "the time allowed for existing connections to get closed before a vm is destroyed")
    private Integer destroyVmGraceperiod;

    @Parameter(name = ApiConstants.COUNTERPARAM_LIST,
            type = CommandType.MAP,
            description = "counterparam list. Example: counterparam[0].name=snmpcommunity&counterparam[0].value=public&counterparam[1].name=snmpport&counterparam[1].value=161")
    private Map counterParamList;

    @Parameter(name = ApiConstants.AUTOSCALE_USER_ID,
            type = CommandType.UUID,
            entityType = UserResponse.class,
            description = "the ID of the user used to launch and destroy the VMs")
    private Long autoscaleUserId;

    @Parameter(name = ApiConstants.FOR_DISPLAY, type = CommandType.BOOLEAN, description = "an optional field, whether to the display the profile to the end user or not", since =
            "4.4", authorized = {RoleType.Admin})
    private Boolean display;

    // ///////////////////////////////////////////////////
    // ///////////// API Implementation///////////////////
    // ///////////////////////////////////////////////////

    @Override
    public void execute() {
        CallContext.current().setEventDetails("AutoScale Policy Id: " + getId());
        final AutoScaleVmProfile result = _autoScaleService.updateAutoScaleVmProfile(this);
        if (result != null) {
            final AutoScaleVmProfileResponse response = _responseGenerator.createAutoScaleVmProfileResponse(result);
            response.setResponseName(getCommandName());
            setResponseObject(response);
        } else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Failed to update autoscale vm profile");
        }
    }

    // ///////////////////////////////////////////////////
    // ///////////////// Accessors ///////////////////////
    // ///////////////////////////////////////////////////

    public Long getId() {
        return id;
    }

    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public long getEntityOwnerId() {
        final AutoScaleVmProfile vmProfile = _entityMgr.findById(AutoScaleVmProfile.class, getId());
        if (vmProfile != null) {
            return vmProfile.getAccountId();
        }
        return Account.ACCOUNT_ID_SYSTEM; // no account info given, parent this command to SYSTEM so ERROR events are
        // tracked
    }

    public Long getTemplateId() {
        return templateId;
    }

    public Map getCounterParamList() {
        return counterParamList;
    }

    public Long getAutoscaleUserId() {
        return autoscaleUserId;
    }

    public Integer getDestroyVmGraceperiod() {
        return destroyVmGraceperiod;
    }

    public Boolean getDisplay() {
        return display;
    }

    @Override
    public String getEventType() {
        return EventTypes.EVENT_AUTOSCALEVMPROFILE_UPDATE;
    }

    @Override
    public String getEventDescription() {
        return "Updating AutoScale Vm Profile. Vm Profile Id: " + getId();
    }

    @Override
    public ApiCommandJobType getInstanceType() {
        return ApiCommandJobType.AutoScaleVmProfile;
    }

    @Override
    public void checkUuid() {
        if (getCustomId() != null) {
            _uuidMgr.checkUuid(getCustomId(), AutoScaleVmProfile.class);
        }
    }
}
