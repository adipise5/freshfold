package com.freshfold.dto;

public class PersonnelSignupRequest extends SignupRequest {
    private String employeeId;
    private String phoneNumber;
    private Integer yearsExperience;
    private Double rating;

    public PersonnelSignupRequest() {}

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Integer getYearsExperience() { return yearsExperience; }
    public void setYearsExperience(Integer yearsExperience) { this.yearsExperience = yearsExperience; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
}
