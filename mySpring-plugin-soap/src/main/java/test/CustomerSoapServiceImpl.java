package test;

import com.pdc.plugin.soap.Soap;
import com.pdc.spring.annotation.Inject;
import com.pdc.spring.annotation.Service;



/**
 * 客户 SOAP 服务接口实现
 *
 * @author pdc
 * @since 1.0.0
 */
@Soap
@Service
public class CustomerSoapServiceImpl implements CustomerSoapService {

    @Inject
    private CustomerService customerService;

    public Customer getCustomer(long customerId) {
        return customerService.getCustomer(customerId);
    }
}
