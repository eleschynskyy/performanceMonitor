package com.mtr.dam.data.objects;

public class User {

	private String username;
	private String password;

	public User setUsername(String username) {
		this.username = username;
		return this;
	}

	public User setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + "]";
	}

}