package com.martinatanasov.reactivemongo.mapper;

import com.martinatanasov.reactivemongo.domain.Customer;
import com.martinatanasov.reactivemongo.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    Customer customerDTOToCustomer(CustomerDTO customerDTO);

    CustomerDTO customerToCustomerDTO(Customer customer);
}
