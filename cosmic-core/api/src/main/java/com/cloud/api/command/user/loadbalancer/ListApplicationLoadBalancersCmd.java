package com.cloud.api.command.user.loadbalancer;

import com.cloud.acl.RoleType;
import com.cloud.api.APICommand;
import com.cloud.api.ApiConstants;
import com.cloud.api.BaseListTaggedResourcesCmd;
import com.cloud.api.Parameter;
import com.cloud.api.response.ApplicationLoadBalancerResponse;
import com.cloud.api.response.FirewallRuleResponse;
import com.cloud.api.response.ListResponse;
import com.cloud.api.response.NetworkResponse;
import com.cloud.network.lb.ApplicationLoadBalancerRule;
import com.cloud.network.rules.LoadBalancerContainer.Scheme;
import com.cloud.utils.Pair;
import com.cloud.utils.exception.InvalidParameterValueException;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@APICommand(name = "listLoadBalancers", description = "Lists load balancers", responseObject = ApplicationLoadBalancerResponse.class, since = "4.2.0",
        requestHasSensitiveInfo = false, responseHasSensitiveInfo = false)
public class ListApplicationLoadBalancersCmd extends BaseListTaggedResourcesCmd {
    public static final Logger s_logger = LoggerFactory.getLogger(ListApplicationLoadBalancersCmd.class.getName());

    private static final String s_name = "listloadbalancersresponse";

    // ///////////////////////////////////////////////////
    // ////////////// API parameters /////////////////////
    // ///////////////////////////////////////////////////

    @Parameter(name = ApiConstants.ID, type = CommandType.UUID, entityType = FirewallRuleResponse.class, description = "the ID of the load balancer")
    private Long id;

    @Parameter(name = ApiConstants.NAME, type = CommandType.STRING, description = "the name of the load balancer")
    private String loadBalancerName;

    @Parameter(name = ApiConstants.SOURCE_IP, type = CommandType.STRING, description = "the source IP address of the load balancer")
    private String sourceIp;

    @Parameter(name = ApiConstants.SOURCE_IP_NETWORK_ID,
            type = CommandType.UUID,
            entityType = NetworkResponse.class,
            description = "the network ID of the source IP address")
    private Long sourceIpNetworkId;

    @Parameter(name = ApiConstants.SCHEME, type = CommandType.STRING, description = "the scheme of the load balancer. Supported value is internal in the current release")
    private String scheme;

    @Parameter(name = ApiConstants.NETWORK_ID, type = CommandType.UUID, entityType = NetworkResponse.class, description = "the network ID of the load balancer")
    private Long networkId;

    @Parameter(name = ApiConstants.FOR_DISPLAY, type = CommandType.BOOLEAN, description = "list resources by display flag; only ROOT admin is eligible to pass this parameter",
            since = "4.4", authorized = {RoleType.Admin})
    private Boolean display;

    // ///////////////////////////////////////////////////
    // ///////////////// Accessors ///////////////////////
    // ///////////////////////////////////////////////////

    public Long getId() {
        return id;
    }

    public String getLoadBalancerRuleName() {
        return loadBalancerName;
    }

    public String getLoadBalancerName() {
        return loadBalancerName;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public Long getSourceIpNetworkId() {
        return sourceIpNetworkId;
    }

    @Override
    public Boolean getDisplay() {
        if (display != null) {
            return display;
        }
        return super.getDisplay();
    }

    public Scheme getScheme() {
        if (scheme != null) {
            if (scheme.equalsIgnoreCase(Scheme.Internal.toString())) {
                return Scheme.Internal;
            } else {
                throw new InvalidParameterValueException("Invalid value for scheme. Supported value is internal");
            }
        }
        return null;
    }

    public Long getNetworkId() {
        return networkId;
    }

    @Override
    public void execute() {
        final Pair<List<? extends ApplicationLoadBalancerRule>, Integer> loadBalancers = _appLbService.listApplicationLoadBalancers(this);
        final ListResponse<ApplicationLoadBalancerResponse> response = new ListResponse<>();
        final List<ApplicationLoadBalancerResponse> lbResponses = new ArrayList<>();
        for (final ApplicationLoadBalancerRule loadBalancer : loadBalancers.first()) {
            final ApplicationLoadBalancerResponse lbResponse =
                    _responseGenerator.createLoadBalancerContainerReponse(loadBalancer, _lbService.getLbInstances(loadBalancer.getId()));
            lbResponse.setObjectName("loadbalancer");
            lbResponses.add(lbResponse);
        }
        response.setResponses(lbResponses, loadBalancers.second());
        response.setResponseName(getCommandName());
        this.setResponseObject(response);
    }

    // ///////////////////////////////////////////////////
    // ///////////// API Implementation///////////////////
    // ///////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return s_name;
    }
}
