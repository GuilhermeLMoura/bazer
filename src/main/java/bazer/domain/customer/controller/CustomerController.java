package bazer.domain.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clientes")
public class CustomerController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin(){
        return "endpoint ADMIN";
    }

    @GetMapping("/vendedor")
    @PreAuthorize("hasRole('VENDEDOR')")
    public String vendedor(){
        return "endpoint VENDEDOR";
    }

    @GetMapping("/comprador")
    @PreAuthorize("hasRole('COMPRADOR')")
    public String comprador(){
        return "endpoint COMPRADOR";
    }

}
