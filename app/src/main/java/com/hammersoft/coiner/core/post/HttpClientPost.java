package com.hammersoft.coiner.core.post;

import com.hammersoft.coiner.core.post.exception.HttpPostException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpClientPost {
	
	private Logger logger = Logger.getLogger(this.getClass());

	public JSONObject request(String address) throws HttpPostException {
		
		try {
			logger.info("Efetuando Requisi��o...");
			JSONObject returnObj = doGet(address);
			logger.info("JSON: " + returnObj.toString());
			return returnObj;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			logger.info("Erro na forma��o de Url: " + e.getMessage());
			throw new HttpPostException(e.getMessage(), e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("Falha de conex�o: " + e.getMessage());
			throw new HttpPostException(e.getMessage(), e);
		} catch (JSONException e) {
			e.printStackTrace();
			logger.info("Erro na composi��o da requisi��o: " + e.getMessage());
			throw new HttpPostException(e.getMessage(), e);
		}
	}
	
	public JSONObject doGet(String host) throws IOException, JSONException, HttpPostException{
		URL obj = new URL(host);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		logger.info("Url do GET: " + obj.toExternalForm());
		
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setUseCaches(false);
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/html;charset=utf8");
        con.setRequestProperty("Connection", "keep-alive");
 
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		//wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		logger.info("\nSending 'GET' request to URL : " + host);
		logger.info("Response Code : " + responseCode);
		
		logger.info("C�digo de retorno da requisi��o: " + responseCode);

		if (responseCode != 200) {
			String error = readFromStream(con.getErrorStream());
			logger.info(error);
			con.disconnect();
			throw new HttpPostException("Erro: " + responseCode);
		}
 
		String response = readFromStream(con.getInputStream());
		
		logger.info("Resposta: " + response);
		
		JSONObject json = new JSONObject(response.replace("[", "").replace("]", ""));
		
		logger.info(response.toString());
		
		con.disconnect();
 
		return json;
	}
	
	private String readFromStream(InputStream stream) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		
		in.close();
		
		return response.toString();
	}
	
	public JSONObject getFromURL(String addr) throws IOException, JSONException{
		URL url = new URL(addr);
	    String response = readFromStream(url.openStream());
	    if(response.startsWith("[")){
	    	return new JSONObject(response.substring(1, response.length() - 2));
	    }
	    return new JSONObject(response);
	}
	
	public JSONObject getFromURL(String addr, String adjust) throws IOException, JSONException{
		URL url = new URL(addr);
	    String response = readFromStream(url.openStream());
	    if(response.startsWith("[")){
	    	String adjusted = "{\"" + adjust + "\" : [" + response.substring(1, response.length() - 1) + "]}";
	    	return new JSONObject(adjusted);
	    }
	    return new JSONObject(response);
	}
}