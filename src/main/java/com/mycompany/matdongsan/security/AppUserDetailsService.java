package com.mycompany.matdongsan.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mycompany.matdongsan.dao.UserCommonDataDao;
import com.mycompany.matdongsan.dto.UserCommonData;

@Service
public class AppUserDetailsService implements UserDetailsService {
	@Autowired
	private UserCommonDataDao userCommonDataDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserCommonData userCommonData = userCommonDataDao.selectByUnumber(username);
		
		if (userCommonData == null) {
			throw new UsernameNotFoundException(username);
		}

		List<GrantedAuthority> authorities = new ArrayList<>();
//      authorities.add(new SimpleGrantedAuthority(member.getMrole()));

		AppUserDetails userDetails = new AppUserDetails(userCommonData, authorities);
		return userDetails;
	}
}
