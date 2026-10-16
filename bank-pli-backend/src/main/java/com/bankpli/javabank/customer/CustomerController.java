package com.bankpli.javabank.customer;

import java.net.URI;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service; public CustomerController(CustomerService service){this.service=service;}
    @GetMapping public Page<Customer> search(@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="20")int size){return service.search(q,page,Math.min(size,100));}
    @GetMapping("/{id}") public Customer get(@PathVariable long id){return service.get(id);}
    @PostMapping public ResponseEntity<Customer> create(@RequestBody CustomerRequest r){Customer c=service.create(r);return ResponseEntity.created(URI.create("/api/customers/"+c.getId())).body(c);}
    @PutMapping("/{id}") public Customer update(@PathVariable long id,@RequestBody CustomerRequest r){return service.update(id,r);}
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable long id){service.delete(id);return ResponseEntity.noContent().build();}
    @PostMapping("/{id}/manual-review") public Customer flag(@PathVariable long id,@RequestBody Map<String,String> body){return service.flag(id,body.getOrDefault("reason","Manual review requested"));}
}
