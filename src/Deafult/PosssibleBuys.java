package Deafult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

public class PosssibleBuys extends FileDataModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2229751656898832656L;

	public PosssibleBuys(File dataFile) throws IOException {
		super(dataFile);
	}

	@Override
	protected long readTimestampFromString(String Value) {
		StringBuilder timestamp = new StringBuilder();
		timestamp.append(Value.substring(0, 4));
		timestamp.append(Value.substring(5, 7));
		timestamp.append(Value.substring(8, 10));
		timestamp.append(Value.substring(11, 13));
		timestamp.append(Value.substring(14, 16));
		return Long.parseLong(timestamp.toString());
	}

	public static void main(String[] args) throws IOException, TasteException {
		
		PosssibleBuys number_of_clicks = new PosssibleBuys(new File(
				"data/number_of_clicks"));
		PosssibleBuys possiblebuys = new PosssibleBuys(new File(
				"data/possiblebuys"));
		File file = new File("/Users/Bharath/Documents/695 Project/PossibleSimilarUsers");
		if (!file.exists()) {
			file.createNewFile();
		}
		
		ArrayList<Long> fakeItems = new ArrayList<Long>();
		fakeItems.add((long) 214760762);
		fakeItems.add((long) 214824613);
		fakeItems.add((long) 214760807);
		fakeItems.add((long) 214822217);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		LongPrimitiveIterator user_itr = possiblebuys.getUserIDs();
		//System.out.println(number_of_clicks.getPreferencesForItem(214530776));
		while(user_itr.hasNext()){
			Long user = user_itr.nextLong();
			FastIDSet items = possiblebuys.getItemIDsFromUser(user);
			for(Long each : items){
				if(!fakeItems.contains(each)){
					try{
						PreferenceArray users_pref = number_of_clicks.getPreferencesForItem(each);
						users_pref.sortByValueReversed();
						for(int i = 0 ; i < users_pref.length(); i++){
							if(users_pref.getValue(i) >= possiblebuys.getPreferenceValue(user,each)){
								if(users_pref.getValue(i) == possiblebuys.getPreferenceValue(user,each)){
									System.out.println(user+","+each+","+users_pref.getUserID(i));
									bw.write(user+","+each+","+users_pref.getUserID(i));
									bw.newLine();
								}
							}
						}
					}catch(Exception e){
						fakeItems.add(each);
					}
					
				}
			}
		}
		
		
	}

}
