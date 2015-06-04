package Deafult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;

public class FinalResult {

	public FinalResult() {
		// TODO Auto-generated constructor stub
	}

	public static String toString(Long user, FastByIDMap<FastByIDMap<Double>> result){
		StringBuilder str = new StringBuilder();
		str.append(user);
		str.append(" : " );
		FastByIDMap<Double> map = result.get(user);
		LongPrimitiveIterator itr = map.keySetIterator();
		while(itr.hasNext()){
			Long item = itr.nextLong();
			str.append(item.toString());
			str.append(",");
		}
		str.deleteCharAt(str.length()-1);
		return str.toString();
	}
	
	public static void main(String[] args) throws IOException {
		FastByIDMap<FastByIDMap<Double>> result = new FastByIDMap<FastByIDMap<Double>>();
		FileReader fr=new FileReader(new File("data/Predictions"));
		BufferedReader br=new BufferedReader(fr);     
		ArrayList<Long> users = new ArrayList<Long>();
		String line;
		Double temp = 0D;
		while ((line=br.readLine())!= null){ 
			String words[] = line.split(",");
			String user = words[0];
			String item = words[1];
			if(!users.contains(Long.parseLong(user))){
				users.add(Long.parseLong(user));
			}
			FastByIDMap<Double> map = result.get(Long.parseLong(user));
			if(map == null){
			    map = new FastByIDMap<Double>();
		        result.put(Long.parseLong(user), map);
		    }
		    map.put(Long.parseLong(item), temp);  
		}
		File file = new File("/Users/Bharath/Documents/695 workspace/cv1_finalResult");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		for(Long each : users){
			String prediction = toString(each, result);
			bw.write(prediction);
			bw.newLine();
		}
		bw.close();
	}
}
