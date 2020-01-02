package org.smart4j.chapter5.soap;

import org.junit.Assert;
import org.junit.Test;
import org.smart4j.chapter5.model.Customer;
import org.smart4j.chapter5.rest.CustomerRestService;
import com.pdc.plugin.rest.RestHelper;

/**
 * 客户 REST 服务单元测试
 *
 * @author pdc
 * @since 1.0.0
 */
public class CustomerRestServiceTest {

    @Test
    public void getCustomerTest() {
        //也可以直接浏览器输入：http://localhost:8080/rest/CustomerRestService/customer/1
        String wadl = "http://localhost:8080/rest/CustomerRestService";
        CustomerRestService customerRestService = RestHelper.createClient(wadl, CustomerRestService.class);
        Customer customer = customerRestService.getCustomer(1);
        Assert.assertNotNull(customer);
    }
}
