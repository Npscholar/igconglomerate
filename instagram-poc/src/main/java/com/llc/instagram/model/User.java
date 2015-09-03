package com.llc.instagram.model;

public class User {
	private String id;
	private String user_name;
	private String full_name;
	private String profile_picture;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserName() {
		return user_name;
	}
	public void setUserName(String userName) {
		this.user_name = userName;
	}
	public String getFullName() {
		return full_name;
	}
	public void setFullName(String fullName) {
		this.full_name = fullName;
	}
	public String getProfilePicture() {
		return profile_picture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profile_picture = profilePicture;
	}
}
