package utils;

import java.io.Serializable;

import core.LocalizationManager;

//import java.util.*;

import models.Employee;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	private Employee sender;
	private Employee receiver;
	private String text;
	
//	private List<Message> inbox=new ArrayList<>();
	
	public Message(Employee sender,Employee receiver,String text) {
		this.sender=sender;
		this.receiver=receiver;
		this.text=text;
	}
	
//	public void receiveMessage(Message m) {
//		inbox.add(m);
//	}
//	
//	public void showInbox() {
//		for(Message m :inbox) {
//			System.out.println(m);
//		}
//	}
	
	@Override
	public String toString() {
		return LocalizationManager.getString("message_format", sender.getName(), receiver.getName(), text);
	}

}
