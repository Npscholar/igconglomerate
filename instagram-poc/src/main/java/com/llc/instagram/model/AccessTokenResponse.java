package com.llc.instagram.model;

public class AccessTokenResponse {
	private User user;
	private String access_token;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public String getAccessToken() {
		return access_token;
	}

	public void setAccessToken(String accessToken) {
		this.access_token = accessToken;
	}
	
}
