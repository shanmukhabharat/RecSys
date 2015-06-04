package Deafult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;

public class Initial extends FileDataModel{
	
	public Initial(File dataFile) throws IOException {
		super(dataFile);
	}
	
	@Override
	protected long readTimestampFromString(String Value){
		StringBuilder timestamp = new StringBuilder();
		timestamp.append(Value.substring(0,4));
		timestamp.append(Value.substring(5,7));
		timestamp.append(Value.substring(8,10));
		timestamp.append(Value.substring(11,13));
		timestamp.append(Value.substring(14,16));
		return Long.parseLong(timestamp.toString());	
	}
	
	public static void main(String[] args) throws TasteException, IOException, ParseException {
		Initial test = new Initial(new File("data/cv1_test_clicks"));
		Initial test_num_of_clicks = new Initial(new File("data/cv1_test_num_of_clicks"));
		ArrayList<Long> topItems = new ArrayList<Long>();
		FileReader fr=new FileReader(new File("data/cv1_topItems"));
		BufferedReader br=new BufferedReader(fr);     
		String line;
		while ((line=br.readLine())!= null){ 
	 	   	String words[] = line.split(",");
	 	   	Long item = Long.parseLong(words[0]);
	 	   	topItems.add(item);
		}

		File file = new File("/Users/Bharath/Documents/695 workspace/cv1_possibleBuys");
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		LongPrimitiveIterator user_itr = test.getUserIDs();
		while(user_itr.hasNext()){
			Long user = user_itr.nextLong();
			FastIDSet items = test.getItemIDsFromUser(user);
			for(Long each : items){
				Long timestamp  =  test.getPreferenceTime(user,each);
				String s_timestamp = timestamp.toString();
				SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyymmdd");
				Date MyDate = newDateFormat.parse(s_timestamp);
				newDateFormat.applyPattern("EEEE");
				
				String day = newDateFormat.format(MyDate);
				Float num_of_clicks = test_num_of_clicks.getPreferenceValue(user, each);
				String month = s_timestamp.substring(4,6);
				int unique_items_clicked = test_num_of_clicks.getItemIDsFromUser(user).size();
				if(!(day == "Tuesday" && month != "08" && num_of_clicks < 2 
									&& !topItems.contains(each) && unique_items_clicked < 2)){
					bw.write(user+","+each+","+num_of_clicks);
					bw.newLine();
				}		
			}
		}
		
		bw.close();
	}
}