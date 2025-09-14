package com.example.reporting.controllers;

import com.example.reporting.entities.*;
import com.example.reporting.repositories.ReportRepository;
import com.example.reporting.repositories.SupplierRepository;
import com.example.reporting.repositories.SupplyOrderRepository;
import com.example.reporting.services.Service;
import com.example.reporting.services.TransactionPredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")

@RestController
@RequestMapping("/api")
public class controller {

    public final Service service;
    public final ReportRepository reportRepository;
    public final SupplierRepository supplierRepository;
    public final SupplyOrderRepository supplyOrderRepository;
    public final TransactionPredictionService predictionService;

    public controller(Service service, ReportRepository reportRepository, SupplierRepository supplierRepository, SupplyOrderRepository supplyOrderRepository, TransactionPredictionService predictionService) {
        this.service = service;
        this.reportRepository = reportRepository;
        this.supplierRepository = supplierRepository;
        this.supplyOrderRepository = supplyOrderRepository;
        this.predictionService = predictionService;
    }


    //Materials

    @GetMapping("/getAll")
    public List<Material>getAllMaterials(){
        return service.getAllMaterial();
    }

@PostMapping("/AddMat")
public ResponseEntity<Material>AddMAterial(@RequestBody Material material){
        return service.AddMaterial(material);
}
@PutMapping("/UpdateMat/{id}")
public ResponseEntity<Material>UpdateMaterial(@RequestBody Material material, @PathVariable Long id){
        return service.UpdateMat(material,id);
}
@DeleteMapping("/DeleteMat/{id}")
public void DeleteMAerial(@PathVariable Long id){
        service.DeleteMat(id);
}



//Transaction

    @GetMapping("/getAllT")
    public List<Transaction>getAllTransaction(){
        return service.getAllTransaction();
    }

@PostMapping("/AddTrans")
public ResponseEntity<Transaction>AddTransaction(@RequestBody Transaction transaction){
        return  service.AddTrans(transaction);
}
@PutMapping("/UpdateTrans/{id}")
public ResponseEntity<Transaction>UpdateTranscation(@RequestBody Transaction transaction,@PathVariable Long id){
        return service.UpdateTrans(transaction,id);
}
@DeleteMapping("/DeleteTrans/{id}")
public void DeleteTrans(@PathVariable Long id){
        service.DeleteTrans(id);
}



    //Employee

    @GetMapping("/getAllEm")

    public List<Employee>getAllEmployee(){
        return service.getAllEmployee();
    }



    @PostMapping("/AddEmp")
    public ResponseEntity<Employee>AddEmp(@RequestBody Employee employee){
        return service.AddEmpl(employee);
    }
@PutMapping("/UpdateEmp/{id}")
    public ResponseEntity<Employee> UpdateEmp(@RequestBody Employee employee,@PathVariable Long id){
        return service.UpdateEmpl(employee,id);
}


@DeleteMapping("/DeleteEmp/{id}")
    public void DeleteEmp(@PathVariable Long id){
        service.DeleteEmp(id);
}


//reports



    @PostMapping("/reports/post")
    public Report saveReport(@RequestBody Report report) {
        report.setCreatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    @GetMapping("/reports/get")
    public List<Report> getReports() {
        return reportRepository.findAll();
    }

    @DeleteMapping("/reports/delete/{id}")
    public void DeleteR(@PathVariable Long id){
        reportRepository.deleteById(id);
    }




    //supplier
    @PostMapping("/supplier/addSupplier")
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        return ResponseEntity.ok(supplierRepository.save(supplier));
    }

    @GetMapping("/supplier/getSuppliers")
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(supplierRepository.findAll());
    }

    @GetMapping("/supplier/getByIdSupplier/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        return supplier.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/supplier/updateSupplier/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplierDetails) {
        Optional<Supplier> optionalSupplier = supplierRepository.findById(id);
        if (optionalSupplier.isPresent()) {
            Supplier supplier = optionalSupplier.get();
            supplier.setName(supplierDetails.getName());
            supplier.setContact_person(supplierDetails.getContact_person());
            supplier.setPhone(supplierDetails.getPhone());
            supplier.setEmail(supplierDetails.getEmail());
            return ResponseEntity.ok(supplierRepository.save(supplier));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/supplier/Delete/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        if (supplierRepository.existsById(id)) {
            supplierRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

// orders
@PostMapping("/addOrders")
public ResponseEntity<SupplyOrder> createSupplyOrder(@RequestBody SupplyOrder supplyOrder) {
    return ResponseEntity.ok(supplyOrderRepository.save(supplyOrder));
}

    @GetMapping("/getOrders")
    public ResponseEntity<List<SupplyOrder>> getAllSupplyOrders() {
        return ResponseEntity.ok(supplyOrderRepository.findAll());
    }

    @GetMapping("/getByIdOrders/{id}")
    public ResponseEntity<SupplyOrder> getSupplyOrderById(@PathVariable Long id) {
        Optional<SupplyOrder> supplyOrder = supplyOrderRepository.findById(id);
        return supplyOrder.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/UpdateOrders/{id}")
    public ResponseEntity<SupplyOrder> updateSupplyOrder(@PathVariable Long id, @RequestBody SupplyOrder supplyOrderDetails) {
        Optional<SupplyOrder> optionalSupplyOrder = supplyOrderRepository.findById(id);
        if (optionalSupplyOrder.isPresent()) {
            SupplyOrder supplyOrder = optionalSupplyOrder.get();
            supplyOrder.setSupplier(supplyOrderDetails.getSupplier());
            supplyOrder.setMaterial(supplyOrderDetails.getMaterial());
            supplyOrder.setOrder_date(supplyOrderDetails.getOrder_date());
            supplyOrder.setQuantity_ordered(supplyOrderDetails.getQuantity_ordered());
            supplyOrder.setStatus(supplyOrderDetails.getStatus());
            supplyOrder.setTotal_cost(supplyOrder.getTotal_cost());
            return ResponseEntity.ok(supplyOrderRepository.save(supplyOrder));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/order/{id}")
    public ResponseEntity<Void> deleteSupplyOrder(@PathVariable Long id) {
        if (supplyOrderRepository.existsById(id)) {
            supplyOrderRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/predict")
    public Map<String, String> predict(@RequestBody Map<String, Object> request) {
        String type = predictionService.predictTransactionType(Long.valueOf(request.get("transactionId").toString()));
        return Map.of("type", type);
    }


    
}



