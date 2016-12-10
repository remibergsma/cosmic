package com.cloud.api.query.dao;

import com.cloud.api.query.vo.DomainJoinVO;
import com.cloud.api.response.DomainResponse;
import com.cloud.api.response.ResourceLimitAndCountResponse;
import com.cloud.domain.Domain;
import com.cloud.utils.db.GenericDao;
import org.apache.cloudstack.api.ResponseObject.ResponseView;

public interface DomainJoinDao extends GenericDao<DomainJoinVO, Long> {

    DomainResponse newDomainResponse(ResponseView view, DomainJoinVO vol);

    DomainJoinVO newDomainView(Domain vol);

    void setResourceLimits(DomainJoinVO domain, boolean isRootDomain, ResourceLimitAndCountResponse response);
}
