package com.cloud.api.query.dao;

import com.cloud.api.query.vo.AccountJoinVO;
import com.cloud.api.response.AccountResponse;
import com.cloud.api.response.ResourceLimitAndCountResponse;
import com.cloud.user.Account;
import com.cloud.utils.db.GenericDao;
import org.apache.cloudstack.api.ResponseObject.ResponseView;

public interface AccountJoinDao extends GenericDao<AccountJoinVO, Long> {

    AccountResponse newAccountResponse(ResponseView view, AccountJoinVO vol);

    AccountJoinVO newAccountView(Account vol);

    void setResourceLimits(AccountJoinVO account, boolean accountIsAdmin, ResourceLimitAndCountResponse response);
}
