package com.mycompany.matdongsan.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.mycompany.matdongsan.dto.UserCommonData;

public class AppUserDetails extends User {
   private UserCommonData userEmail;

   public AppUserDetails(UserCommonData userEmail, List<GrantedAuthority> authorities) {
      super(userEmail.getUemail(), userEmail.getUpassword(), userEmail.isUremoved(), true, true, true, authorities);
      this.userEmail = userEmail;
   }

   public UserCommonData getUser() {
      return userEmail;
   }
}
