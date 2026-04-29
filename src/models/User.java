package models;

import java.io.Serializable;
import java.util.Objects;

import enumerations.Language;

public abstract class User implements UserComponent,Serializable{
	private static final long serialVersionUID = 1L;
	private String userId;
	private String login;
	private String name;
	private String password;
	public Language currentLanguage = Language.RU;
	
	public User(String userId,String name,String password,String login) {
		this.name=name;
		this.password=password;
		this.login=login;
		this.userId=userId;
	}

	 public boolean login(String enteredId, String enteredPassword) {
		 if (this.userId.equals(enteredId) && this.password.equals(enteredPassword)) {
			 System.out.println("Welcome, " + name + "!");
	         return true;
	        }
		 return false;
	}
	
	 public void switchLanguage(Language newLanguage) {
	        this.currentLanguage = newLanguage;
	        String message = "";
	        switch (newLanguage) {
	            case EN -> message = "Language changed to English";
	            case RU -> message = "Язык изменен на русский";
	            case KZ -> message = "Тіл қазақшаға өзгертілді";
	        }
	        System.out.println(message);
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
	        System.out.println("Уведомление для " + getName() + ": " + newsMessage);
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
	
}
