package wangyuec15.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import wangyuec15.ppmtool.domain.User;
import wangyuec15.ppmtool.exceptions.UsernameAlreadyExistsException;
import wangyuec15.ppmtool.repositories.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCrptPasswordEncoder;
	
	public User saveUser(User newUser) {
		
		try {
			newUser.setPassword(bCrptPasswordEncoder.encode(newUser.getPassword()));
			//Username has to be unique
			newUser.setUsername(newUser.getUsername());
			//Make sure that password and confirmed passwoed match
			//we don't persist or show the confirm password
			newUser.setConfirmPassword("");
			return userRepository.save(newUser);
		}catch (Exception e) {
			throw new UsernameAlreadyExistsException("Username '"+newUser.getUsername()+"' already exists.");
		}
		
	}
}
