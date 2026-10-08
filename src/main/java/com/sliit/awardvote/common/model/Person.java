package com.sliit.awardvote.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;


 //OOP concept: MULTILEVEL INHERITANCE + POLYMORPHISM
 //Declares one abstract method, getRoleDescription(), that every concrete
 //subclass must implement in its own way. Calling person.getRoleDescription()
 //on a list of mixed Person subtypes will invoke a different implementation
 //for each object at runtime (runtime polymorphism).

@MappedSuperclass
public abstract class Person extends BaseEntity {

    @Column(nullable = false)
    protected String fullName;

    @Column(nullable = false)
    protected String email;

    protected String phone;


    public abstract String getRoleDescription();

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
