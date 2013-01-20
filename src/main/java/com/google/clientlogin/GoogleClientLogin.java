package com.google.clientlogin;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin.Response;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;

/**
 * Hello world!
 * 
 */
public class GoogleClientLogin {

    public static final String BadAuthentication = "BadAuthentication";
    public static final String NotVerified = "NotVerified";
    public static final String TermsNotAgreed = "TermsNotAgreed";
    public static final String CaptchaRequired = "CaptchaRequired";
    public static final String Unknown = "Unknown";
    public static final String AccountDeleted = "AccountDeleted";
    public static final String AccountDisabled = "AccountDisabled";
    public static final String ServiceDisabled = "ServiceDisabled";
    public static final String ServiceUnavailable = "ServiceUnavailable";
    private static final Map<String, String> errorMsg = new HashMap<String, String>();
    static {
	errorMsg.put(BadAuthentication,
		"The login request used a username or password that is not recognized.");
	errorMsg.put(
		NotVerified,
		"The account email address has not been verified. The user will need to access their Google account directly to resolve the issue before logging in using a non-Google application.");
	errorMsg.put(
		TermsNotAgreed,
		"The user has not agreed to terms. The user will need to access their Google account directly to resolve the issue before logging in using a non-Google application.");
	errorMsg.put(
		CaptchaRequired,
		"A CAPTCHA is required. (A response with this error code will also contain an image URL and a CAPTCHA token.)");
	errorMsg.put(
		Unknown,
		"The error is unknown or unspecified; the request contained invalid input or was malformed.");
	errorMsg.put(AccountDeleted, "The user account has been deleted.");
	errorMsg.put(AccountDisabled, "The user account has been disabled.");
	errorMsg.put(
		ServiceDisabled,
		"The user's access to the specified service has been disabled. (The user account may still be valid.)");
	errorMsg.put(ServiceUnavailable,
		"The service is not available; try again later.");
    }

    private String errorMessage;
    private String captchaToken;
    private String captchaUrl;
    private HttpTransport transport;
    private ClientLogin authenticator;
    private String authToken;
    private String service;

    /**
     * @param service
     */
    public GoogleClientLogin(String service) {
	super();
	this.service = service;
	authenticator = new ClientLogin();
	transport = GoogleTransport.create();
	authenticator.authTokenType = service;
    }

    /**
     * @param username
     * @param password
     * @throws ClientLoginException
     */
    public void authenticate(String username, String password)
	    throws ClientLoginException {

	try {

	    // authenticate with ClientLogin
	    authenticator.username = username;
	    authenticator.password = password;
	    Response response = authenticator.authenticate();
	    this.authToken = response.auth;

	} catch (HttpResponseException e) {
	    parseError(e);

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    /**
     * @param e
     * @throws ClientLoginException
     */
    private void parseError(HttpResponseException e)
	    throws ClientLoginException {
	try {

	    ClientLogin.ErrorInfo errorInfo = e.response.parseAs(ClientLogin.ErrorInfo.class);
	    errorMessage = errorMsg.get(errorInfo.error);
	    if (errorInfo.error.equals(CaptchaRequired)) {
		captchaToken = errorInfo.captchaToken;
		captchaUrl = errorInfo.captchaUrl;
		throw new CaptchaRequiredException(errorMessage, e);

	    } else
		throw new ClientLoginException(errorMessage, e);
	} catch (IOException e1) {

	    throw new ClientLoginException(e1);
	}
    }

    /**
     * @param username
     * @param password
     * @param captchaAnswer
     * @throws ClientLoginException
     */
    public void authenticate(String username, String password,
	    String captchaAnswer) throws ClientLoginException {
	authenticator.username = username;
	authenticator.password = password;
	authenticator.captchaToken = this.captchaToken;
	authenticator.captchaAnswer = captchaAnswer;
	try {
	    Response response = authenticator.authenticate();
	    this.authToken = response.auth;
	} catch (HttpResponseException e) {
	    parseError(e);
	} catch (IOException e) {
	    throw new ClientLoginException(e);
	}
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
	return errorMessage;
    }

    /**
     * @return the captchaUrl
     */
    public String getCaptchaUrl() {
	return captchaUrl;
    }

    /**
     * @return the captchaImage
     */
    public BufferedImage getCaptchaImage() {

	BufferedImage image = null;
	try {
	    URL url = new URL("https://www.google.com/accounts/"+ getCaptchaUrl());
	    image = ImageIO.read(url);
	} catch (MalformedURLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return null;

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return null;
	}

	return image;
    }

    /**
     * @return the authToken
     */
    public String getAuthToken() {
	return authToken;
    }

   
}
