package com.arni.user;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.arni.Business.Business;
import com.arni.Role.Role;
import com.arni.Utils.BigBaseEntity;
import com.arni.Utils.Utils;
import com.arni.customer.Customer;
import com.arni.merchant.Merchant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "user")
public class User extends BigBaseEntity implements UserDetails {  // Implement UserDetails

	@ManyToOne
    @JoinColumn(name = "business_id")
    private Business business;
    
	@ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "user_id")
    private String userId;

    @Column(unique = true) //, nullable = true
    private String email;

    private String password;
    private String phoneNumber;

    @Column(length = 1000)
    private String profilePic;
    
    private boolean enabled = true;
    private boolean emailVerified = false;
    private boolean phoneVerfied = false;

    /*@Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private Providers provider = Providers.SELF;*/
    
    @Enumerated(EnumType.STRING)
    @Column(name = "login_method")
    private LoginMethod loginMethod ;

    @Column(name = "provider_user_id")  
    private String providerUserId;

    private String otp;

    @Column(name = "staff_link")
    private String staffLink;

    @ManyToOne
    @Enumerated(EnumType.ORDINAL)
    @JoinColumn(name = "role_id")
    private Role role;
    
    @Transient
    String roleName;
    
    private Date validity;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @PrePersist
    protected void onCreate() {
        Date now = Utils.now();
        if (this.getCreationTime() == null)
            this.setCreationTime(now);
        if (this.getUpdateTime() == null)
            this.setUpdateTime(now);
    }

    @PreUpdate
    protected void onUpdate() {
        this.setUpdateTime(Utils.now());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getName().name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled() {
    	 return true;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /*public boolean isEnabled() {
        return enabled;
    }*/

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isPhoneVerfied() {
        return phoneVerfied;
    }

    public void setPhoneVerfied(boolean phoneVerfied) {
        this.phoneVerfied = phoneVerfied;
    }

    /*public Providers getProvider() {
        return provider;
    }

    public void setProvider(Providers provider) {
        this.provider = provider;
    }*/

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public String getStaffLink() {
        return staffLink;
    }

    public void setStaffLink(String staffLink) {
        this.staffLink = staffLink;
    }

	public Date getValidity() {
		return validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
	}

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

	public LoginMethod getLoginMethod() {
		return loginMethod;
	}

	public void setLoginMethod(LoginMethod loginMethod) {
		this.loginMethod = loginMethod;
	}

    
    
}
