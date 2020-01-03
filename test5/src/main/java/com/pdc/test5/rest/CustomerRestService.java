package com.pdc.test5.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.pdc.test5.model.Customer;
import com.pdc.test5.service.CustomerService;
import com.pdc.spring.annotation.Inject;
import com.pdc.spring.annotation.Service;
import com.pdc.plugin.rest.Rest;

/**
 * 客户 REST 服务
 *
 * @author pdc
 * @since 1.0.0
 */
@Rest
@Service
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerRestService {

    @Inject
    private CustomerService customerService;

    @GET
    @Path("/customer/{id}")
    public Customer getCustomer(@PathParam("id") long customerId) {
        return customerService.getCustomer(customerId);
    }
}
