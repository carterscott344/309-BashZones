package coms309.models;

import jakarta.persistence.*;

@Entity
public class Laptop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double cpuClock;
    private int cpuCores;
    private int ram;
    private String manufacturer;
    private double cost;

    // Default constructor
    public Laptop() {
    }

    // Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public double getCpuClock() {
        return cpuClock;
    }
    public void setCpuClock(double cpuClock) {
        this.cpuClock = cpuClock;
    }
    public int getCpuCores() {
        return cpuCores;
    }
    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }
    public int getRam() {
        return ram;
    }
    public void setRam(int ram) {
        this.ram = ram;
    }
    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }
}
