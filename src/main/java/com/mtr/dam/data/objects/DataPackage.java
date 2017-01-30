package com.mtr.dam.data.objects;

public class DataPackage {

	private String username;
	private String password;
	private String filePrefix;
	private int filesNumber;
	private int testCaseNum;

	public DataPackage setUsername(String username) {
		this.username = username;
		return this;
	}

	public DataPackage setPassword(String password) {
		this.password = password;
		return this;
	}
	
	public DataPackage setfilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
		return this;
	}
	
	public DataPackage setfilesNumber(String filesNumber) {
		this.filesNumber = Integer.parseInt(filesNumber);
		return this;
	}
	
	public DataPackage setTestCaseNum(String testCaseNum) {
		this.testCaseNum = Integer.parseInt(testCaseNum);
		return this;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public String getfilePrefix() {
		return filePrefix;
	}
	
	public int getfilesNumber() {
		return filesNumber;
	}
	
	public int getTestCaseNum() {
		return testCaseNum;
	}

	@Override
	public String toString() {
		return "Data [username=" + username + ", filePrefix=" + filePrefix + ", filesNumber=" + filesNumber + "]";
	}

}