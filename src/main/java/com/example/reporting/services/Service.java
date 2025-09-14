package com.example.reporting.services;


import com.example.reporting.entities.Employee;
import com.example.reporting.entities.Material;
import com.example.reporting.entities.Transaction;
import com.example.reporting.repositories.EmployeeRepository;
import com.example.reporting.repositories.MaterialRepository;
import com.example.reporting.repositories.TransactionRepository;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

@org.springframework.stereotype.Service
public class Service {

public final MaterialRepository materialRepository;
public final EmployeeRepository employeeRepository;
public final TransactionRepository transactionRepository;

    public Service(MaterialRepository materialRepository, EmployeeRepository employeeRepository, TransactionRepository transactionRepository) {
        this.materialRepository = materialRepository;
        this.employeeRepository = employeeRepository;
        this.transactionRepository = transactionRepository;
    }


    public List<Material>getAllMaterial(){
        return materialRepository.findAll();
    }

    public List<Employee>getAllEmployee(){
        return employeeRepository.findAll();
    }

    public List<Transaction>getAllTransaction(){
        return transactionRepository.findAll();
    }


public ResponseEntity<Employee> AddEmpl(Employee employee){
Employee employee1=new Employee();
employee1.setDepartment(employee.getDepartment());
employee1.setName(employee.getName());
employee1.setRole(employee.getRole());
    Employee em= employeeRepository.save(employee1);
   return ResponseEntity.ok(em);
    }
    public  Employee findById(Long id){
        return employeeRepository.findById(id).get();
    }

    public ResponseEntity<Employee>UpdateEmpl(Employee employee,Long id){
Employee employee1=employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("not found"+id));
employee1.setRole(employee.getRole());
employee1.setName(employee.getName());
employee1.setDepartment(employee.getDepartment());
Employee em=employeeRepository.save(employee1);
return ResponseEntity.ok(em);

    }
public void DeleteEmp(Long id){
employeeRepository.deleteById(id);
}

//Materials
    public ResponseEntity<Material>AddMaterial(Material material){
        Material material1=new Material();
        material1.setName(material.getName());
        material1.setQuantity(material.getQuantity());
        material1.setType(material.getType());

        Material mat=materialRepository.save(material1);
        return ResponseEntity.ok(mat);
    }
    public ResponseEntity<Material>UpdateMat(Material material,Long id){
        Material material1=materialRepository.findById(id).orElseThrow(() -> new RuntimeException("not found"+id));
        material1.setType(material.getType());
        material1.setName(material.getName());
        material1.setQuantity(material.getQuantity());
        Material em=materialRepository.save(material1);
        return ResponseEntity.ok(em);

    }
    public void DeleteMat(Long id){
        materialRepository.deleteById(id);
    }


    public ResponseEntity<Transaction>AddTrans(Transaction transaction){
        Transaction transaction1=new Transaction();
        transaction1.setDate(transaction.getDate());
        transaction1.setQuantity(transaction.getQuantity());
Employee employee=employeeRepository.findById(transaction.getEmployee().getId()).orElseThrow(()->new RuntimeException("not found"+ transaction.getEmployee().getId()));
Material material=materialRepository.findById(transaction.getMaterial().getId()).orElseThrow(()->new RuntimeException("not found"+ transaction.getMaterial().getId()));
transaction1.setEmployee(employee);
transaction1.setMaterial(material);
        Transaction mat=transactionRepository.save(transaction1);
        return ResponseEntity.ok(mat);
    }

    public ResponseEntity<Transaction>UpdateTrans(Transaction transaction,Long id){
        Transaction transaction1=transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("not found"+id));
        transaction1.setQuantity(transaction.getQuantity());
        transaction1.setDate(transaction.getDate());
        if (transaction.getMaterial() != null) {
            Material tache = materialRepository.findById(transaction.getMaterial().getId())
                    .orElseThrow(() -> new RuntimeException("Tâche introuvable avec ID: " + transaction.getMaterial().getId()));
            transaction1.setMaterial(tache);
        }
        if (transaction.getEmployee() != null) {
            Employee tache = employeeRepository.findById(transaction.getEmployee().getId())
                    .orElseThrow(() -> new RuntimeException("Tâche introuvable avec ID: " + transaction.getEmployee().getId()));
            transaction1.setEmployee(tache);
        }
        Transaction em=transactionRepository.save(transaction1);
        return ResponseEntity.ok(em);

    }
    public void DeleteTrans(Long id){
        transactionRepository.deleteById(id);
    }



}

