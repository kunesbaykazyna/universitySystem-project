package models;

import java.io.Serializable;
import java.util.Objects;

import core.LocalizationManager;
import enumerations.Language;

public abstract class User implements UserComponent,Serializable{
	private static final long serialVersionUID = 1L;
	private String userId;
	private String login;
	private String name;
	private String password;
	public Language currentLanguage = Language.RU;
	private boolean isResearcher = false;

	public User(String userId,String name,String password,String login) {
		this.name=name;
		this.password=password;
		this.login=login;
		this.userId=userId;
	}

//	 public boolean login(String enteredId, String enteredPassword) {
//		 if (this.userId.equals(enteredId) && this.password.equals(enteredPassword)) {
//				System.out.println(LocalizationManager.getString("welcome_user", name));
//	         return true;
//	        }
//		 return false;
//	}
	
	public boolean login(String enteredLogin, String enteredPassword) {
	    if (this.login.equals(enteredLogin) && this.password.equals(enteredPassword)) {
			System.out.println(LocalizationManager.getString("welcome_user", name));
	        return true;
	    }
	    return false;
	}
	
	 public void switchLanguage(Language newLanguage) {
		    LocalizationManager.setLanguage(newLanguage);
		    this.currentLanguage = newLanguage;
			System.out.println(LocalizationManager.getString("language.changed"));
		}
	 
	 @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        User user = (User) o;
	        return Objects.equals(userId, user.userId) && Objects.equals(login, user.login);
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(userId, login);
	    }
	 
	    @Override
		 public String toString() {
			 return String.format("User[ID='%s', Name='%s']", userId, name);
		 }
	    
	    public void update(String newsMessage) {
			System.out.println(LocalizationManager.getString("user_notification", getName(), newsMessage));
	    }
	    
		 public String getUserId() {
		 	 return userId;
		 }
	
		 public String getLogin() {
		 	 return login;
		 }
	
		 public String getName() {
		 	 return name;
		 }
	
		 public String getPassword() {
			 return password;
		 }
		 public boolean isResearcher() { return isResearcher; }
		 
		 public void setResearcher(boolean isResearcher) { this.isResearcher = isResearcher; }
			
}
