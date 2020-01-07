package test;

import com.pdc.plugin.soap.SoapHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * 客户 SOAP 服务单元测试
 * @author pdc
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
