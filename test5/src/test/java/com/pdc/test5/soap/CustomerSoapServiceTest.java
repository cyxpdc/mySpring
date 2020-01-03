package com.pdc.test5.soap;

import org.junit.Assert;
import org.junit.Test;
import com.pdc.test5.model.Customer;
import com.pdc.plugin.soap.SoapHelper;

/**
 * 客户 SOAP 服务单元测试
 *
 * @author pdc
 * @since 1.0.0
 */
public class CustomerSoapServiceTest {

    @Test
    public void getCustomerTest() {
        String wsdl = "http://localhost:8080/soap/CustomerSoapService";
        CustomerSoapService customerSoapService = SoapHelper.createClient(wsdl, CustomerSoapService.class);
        Customer customer = customerSoapService.getCustomer(1);
        Assert.assertNotNull(customer);
    }
}
