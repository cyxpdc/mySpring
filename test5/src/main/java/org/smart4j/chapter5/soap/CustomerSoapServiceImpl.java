package org.smart4j.chapter5.soap;

import org.smart4j.chapter5.model.Customer;
import org.smart4j.chapter5.service.CustomerService;
import com.pdc.spring.annotation.Inject;
import com.pdc.spring.annotation.Service;
import com.pdc.plugin.soap.Soap;

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
