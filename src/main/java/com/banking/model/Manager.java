package com.banking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Manager class represents a bank manager with administrative privileges
 * GRASP - High Cohesion: Manager focuses only on management operations
 */
@Entity
@DiscriminatorValue("MANAGER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Manager extends User {
    
    @Column(name = "employee_id", unique = true, length = 20)
    private String employeeId;
    
    @Column(name = "department", length = 50)
    private String department;
    
    @Column(name = "branch", length = 100)
    private String branch;
}
