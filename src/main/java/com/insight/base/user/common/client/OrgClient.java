package com.insight.base.user.common.client;

import com.insight.base.user.common.config.FeignClientConfig;
import com.insight.utils.pojo.base.Reply;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 宣炳刚
 * @date 2019-08-31
 * @remark Feign客户端
 */
@FeignClient(name = "base-organize", configuration = FeignClientConfig.class)
public interface OrgClient {

    /**
     * 查询指定ID的机构的下级机构ID
     *
     * @param id 机构ID
     * @return Reply
     */
    @GetMapping("/base/organize/v1.0/organizes")
    Reply getSubOrganizes(@RequestParam Long id);
}
