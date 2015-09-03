package com.llc.instagram.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.llc.instagram.model.AccessTokenResponse;

@Controller
public class HomeController {

	private static final Logger LOGGER = LogManager.getLogger();
	final String accessTokenURL = "https://api.instagram.com/oauth/access_token";
	final String clientId = "538d7997094b4ed59b78ca64ad637eb4";
	final String clientSecret = "722c968f724f46d099016797d3cfa18b";
	final String redirectURL = "http://localhost:8080/instagram-poc/access";
	final String redirectURL1 = "http://ec2-52-1-233-246.compute-1.amazonaws.com:8080/instagram-poc/access";
	final String authURLImplicit = "https://api.instagram.com/oauth/authorize/?client_id="+clientId+"&redirect_uri="+redirectURL+"&response_type=token";
	final String authURLExplicit ="https://api.instagram.com/oauth/authorize/?client_id="+clientId+"&redirect_uri="+redirectURL+"&response_type=code";
	final String implicitAccessToken = "2158323180.538d799.e1fc4cc862384af0800f6df35e39047f";
	private AccessTokenResponse token;
	
	@RequestMapping(value="/")
	public String authRedirect(){
		LOGGER.error("in instagram-poc");
		return "redirect:"+authURLExplicit;
	}

	@RequestMapping(value="/auth")
	public String getAccessCode(HttpServletResponse response){
		return "redirect:"+ authURLExplicit;
	}
	@RequestMapping(value="/access", method=RequestMethod.GET)
	public String getAccessToken(HttpServletResponse response, @RequestParam(value="error", required=false) String error, 
			@RequestParam(value="error_reason", required=false) String errorReason, 
			@RequestParam(value="error_description", required=false) String errorDescription,
			@RequestParam(value="code", required=false) String code){
		if (error!= null){
			LOGGER.error("about to return error");
			return errorReason + "\nUser Athentication failed because:" + errorDescription;
		}else{
			LOGGER.error("about to redirect to accesscode");
			return "redirect:/accesscode?code="+code; 
		}
	}
	@RequestMapping(value="/accesscode")
	public String postAccessCode(HttpServletResponse resp, @RequestParam("code") String code) throws UnsupportedOperationException, IOException{
		LOGGER.error("got into /postaccessCode");
		String url = "https://api.instagram.com/oauth/access_token";
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("client_id", clientId));
		urlParameters.add(new BasicNameValuePair("client_secret", clientSecret));
		urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
		urlParameters.add(new BasicNameValuePair("redirect_uri", redirectURL));
		urlParameters.add(new BasicNameValuePair("code", code));
		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse response = client.execute(post);
			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
			if(response.getStatusLine().getStatusCode() == 400){
				return "I told you not to press the nback button" + response.getStatusLine().getReasonPhrase();
			}
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			Gson gson = new Gson();
			token = gson.fromJson(result.toString(), AccessTokenResponse.class);
			LOGGER.error("I could call timeline but just look at yourself");
			return "redirect:"+token.getUser().getProfilePicture();
		} catch (UnsupportedEncodingException e) {
			return	"HomeController line 89";
		}
		
	}
	@RequestMapping(value="/timeline", method=RequestMethod.GET)
	public String getTimeline(HttpServletResponse resp){
		String url = "https://api.instagram.com/v1/users/self/feed?access_token="+token.getAccessToken();
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(request);
			System.out.println("Response Code : "+ response.getStatusLine().getStatusCode());
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			return result.toString();
		} catch (ClientProtocolException e) {
			return e.getCause().toString();
		} catch (IOException e) {
			return e.getCause().toString();
		}	
	}
	
	@RequestMapping(value="/test")
	public ModelAndView test(HttpServletResponse response) throws IOException{
		return new ModelAndView("home");
	}
	
		
}
