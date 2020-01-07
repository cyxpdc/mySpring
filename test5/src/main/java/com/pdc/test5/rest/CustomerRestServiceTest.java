package com.pdc.test5.rest;

import com.pdc.plugin.rest.RestHelper;
import com.pdc.test5.model.Customer;
import org.testng.Assert;


/**
 * 客户 REST 服务单元测试
 * @author pdc
 */
public class CustomerRestServiceTest {

    //@Test
    public void getCustomerTest() {
        //也可以直接浏览器输入：http://localhost:8080/rest/CustomerRestService/customer/1
        String wadl = "http://localhost:8080/rest/CustomerRestService";
        CustomerRestService customerRestService = RestHelper.createClient(wadl, CustomerRestService.class);
        Customer customer = customerRestService.getCustomer(1);
        Assert.assertNotNull(customer);
    }
}
