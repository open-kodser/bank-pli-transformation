package com.bankpli.javabank.batch;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/batch")public class BatchController{private final BatchService service;public BatchController(BatchService s){service=s;}@PostMapping("/generate")public BatchService.Summary generate(@RequestParam(defaultValue="10")int count){return service.generate(count);}}
