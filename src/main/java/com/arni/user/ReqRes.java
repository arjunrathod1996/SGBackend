package com.arni.user;

import java.util.List;

import jakarta.persistence.Column;

public class ReqRes {

	private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String city;
    private String role;
    private String email;
    private String password;
    private String userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String otp;
    private String staffLink;
    private User ourUsers;
    private List<User> ourUsersList;
    
	public User ReqRes(User user) {
		return user;
	}
	
	// Default constructor
    public ReqRes() {
    }

    // Constructor with statusCode and message
    public ReqRes(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public String getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(String expirationTime) {
		this.expirationTime = expirationTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public String getStaffLink() {
		return staffLink;
	}
	public void setStaffLink(String staffLink) {
		this.staffLink = staffLink;
	}
	public User getOurUsers() {
		return ourUsers;
	}
	public void setOurUsers(User ourUsers) {
		this.ourUsers = ourUsers;
	}
	public List<User> getOurUsersList() {
		return ourUsersList;
	}
	public void setOurUsersList(List<User> ourUsersList) {
		this.ourUsersList = ourUsersList;
	}
    
    
	
}
