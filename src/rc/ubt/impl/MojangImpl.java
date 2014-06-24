package rc.ubt.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MojangImpl
{
	
	//this is support class
	//it expected to perform mojang request in async\querry
	//storing inside concurrent hashmap (check implementation in java or desktop)
	//and givin results at later time
	//results will require manual GC over time
	
	
	static byte[] requestdataA = "[{\"name\":\"".getBytes();
	static byte[] requestdataB = "\",\"agent\":\"minecraft\"}]".getBytes();
	static byte[] name = "RawCodenn".getBytes();
	
	//{"profiles":[{"id":"b5a7d8c4ac5b4e54bae59f8fe2a5a382","name":"RawCode"}],"size":1}
	//{"profiles":[],"size":0}
	
	//async netIO with IO pump baked by atomic hashtable
	
    static public void main(String... arg) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://api.mojang.com/profiles/page/1").openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "custom");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        
        
    	
        DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
        writer.write(requestdataA);
        writer.write(name);
        writer.write(requestdataB);
        writer.flush();
        writer.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        
        System.out.println(reader.readLine());

        reader.close();
        
    }
}
