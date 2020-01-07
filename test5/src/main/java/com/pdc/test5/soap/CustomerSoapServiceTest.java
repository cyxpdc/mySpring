package com.pdc.test5.soap;

import com.pdc.plugin.soap.SoapHelper;
import com.pdc.test5.model.Customer;
import org.junit.Assert;
import org.junit.Test;

/**
 * 客户 SOAP 服务单元测试
 * @author pdc
 */
public class CustomerSoapServiceTest {

    @Test
    public void getCustomerTest() {
        //或者直接请求http://localhost:8080/soap/CustomerSoapService
        String wsdl = "http://localhost:8080/soap/CustomerSoapService";
        CustomerSoapService customerSoapService = SoapHelper.createClient(wsdl, CustomerSoapService.class);
        Customer customer = customerSoapService.getCustomer(1);
        Assert.assertNotNull(customer);
    }
}
