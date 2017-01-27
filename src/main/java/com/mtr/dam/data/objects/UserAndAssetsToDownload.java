package com.mtr.dam.data.objects;

public class UserAndAssetsToDownload {

	private String username;
	private String password;
	private int assetsToDownload;

	public UserAndAssetsToDownload setUsername(String username) {
		this.username = username;
		return this;
	}

	public UserAndAssetsToDownload setPassword(String password) {
		this.password = password;
		return this;
	}
	
	public UserAndAssetsToDownload setAssetsToDownload(String assetsToDownload) {
		this.assetsToDownload = Integer.parseInt(assetsToDownload);
		return this;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public int getAssetsToDownload() {
		return assetsToDownload;
	}

	@Override
	public String toString() {
		return "Data [username=" + username + ", assetsToDownload=" + assetsToDownload + "]";
	}

}