package com.pdc.plugin.rest.test;

import com.pdc.plugin.rest.Rest;
import com.pdc.spring.annotation.Inject;
import com.pdc.spring.annotation.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
