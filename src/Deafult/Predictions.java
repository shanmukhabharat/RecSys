package Deafult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;

public class Predictions extends FileDataModel {

	private static ArrayList<Long> similarUsers = new ArrayList<Long>();
	private static ArrayList<Long> topItems = new ArrayList<Long>();
	private static ArrayList<Long> predicted_users = new ArrayList<Long>();
	
	public Predictions(File dataFile) throws IOException {
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

	public static void toString(Long user, ArrayList<Long> result) {
		StringBuilder str = new StringBuilder();
		str.append('{');
		for (Long each : result) {
			str.append(each);
			str.append(", ");
		}
		str.deleteCharAt(str.length() - 1);
		str.append('}');
		System.out.println("user :" + user + " Predictions :" + str.toString());
	}

	public static Long getUserWithMaxSimilarity(
			FastByIDMap<FastByIDMap<Double>> neighbors, Long user) {
		FastByIDMap<Double> temp = neighbors.get(user);
		String neighborhood = temp.toString();
		String neighborhood_users[] = neighborhood.split(",");
		Long maxUserID = 0L;
		Double maxSim = 0D;
		for (int i = 0; i < neighborhood_users.length; i++) {
			String each = neighborhood_users[i];
			String s[] = each.split("=");
			if (s[0].contains("{")) {
				s[0] = s[0].substring(1);
			}
			if (s[1].contains("}")) {
				s[1] = s[1].substring(0, s[1].length() - 1);
			}
			if (Double.parseDouble(s[1]) > maxSim) {
				maxSim = Double.parseDouble(s[1]);
				maxUserID = Long.parseLong(s[0]);
			}
		}
		return maxUserID;
	}

	public static int getUniqueItemsClicked(Long user, FileDataModel model)
			throws TasteException {
		FastIDSet similar_userID_items_clicks = model.getItemIDsFromUser(user);
		HashMap<Long, Integer> counter_clicks = new HashMap<Long, Integer>();
		ArrayList<Long> items = new ArrayList<Long>();
		for (Long each : similar_userID_items_clicks) {
			if (counter_clicks.containsKey(each)) {
				counter_clicks.put(each, counter_clicks.get(each) + 1);
			} else {
				counter_clicks.put(each, 1);
			}
			items.add(each);
		}
		return counter_clicks.size();
	}

	private static void parseTopItems() throws IOException{
		FileReader fr = new FileReader(
				"/Users/Bharath/Documents/695 workspace/cv1_topItems");
		BufferedReader br = new BufferedReader(fr);
		String line;
		while ((line = br.readLine()) != null) {
			String words[] = line.split(",");
			topItems.add(Long.parseLong(words[0]));
		}
	}
	
	public static void getPredictions(Long user, Long mostSimilar, int n_clicks, Long item,
			Predictions clicks, Predictions buys, Predictions test)
			throws TasteException, IOException {

		int uniqueItemsClicked = getUniqueItemsClicked(user, test);
		File file = new File(
				"/Users/Bharath/Documents/695 workspace/Predictions");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		if (mostSimilar != 0L) {
			try {
				if (buys.getItemIDsFromUser(mostSimilar).contains(item)) {
					bw.write(user + ": " + item);
					bw.newLine();
					bw.close();
					if(!predicted_users.contains(user))
						predicted_users.add(user);
				} else {
					if (uniqueItemsClicked >= 1) {
						long test_timestamp = test
								.getPreferenceTime(user, item);
						String test_timestamp_s = Long.toString(test_timestamp);
						Long test_timestamp_month = Long
								.parseLong(test_timestamp_s.substring(4, 6));
						Long test_timestamp_day = Long
								.parseLong(test_timestamp_s.substring(6, 8));
						Long test_timestamp_hours = Long
								.parseLong(test_timestamp_s.substring(8, 10));
						if ((test_timestamp_month == 8
								|| test_timestamp_month == 4 || test_timestamp_month == 5)
								&& ((test_timestamp_hours >= 8
										&& test_timestamp_hours <= 11)
										|| (test_timestamp_hours >=18 && test_timestamp_hours <= 20))
								&& topItems.contains(item)  && n_clicks>3 && predicted_users.contains(user))

						{
							bw.write(user + ": " + item);
							bw.newLine();
							bw.close();
						}
					}
				}
			} catch (Exception e) {
				System.out.println("In catch");
			}
		} else {
			if (uniqueItemsClicked == 1) {
				long test_timestamp = test
						.getPreferenceTime(user, item);
				String test_timestamp_s = Long.toString(test_timestamp);
				Long test_timestamp_month = Long
						.parseLong(test_timestamp_s.substring(4, 6));
				Long test_timestamp_day = Long
						.parseLong(test_timestamp_s.substring(6, 8));
				Long test_timestamp_hours = Long
						.parseLong(test_timestamp_s.substring(8, 10));
				if ((test_timestamp_month == 8
						|| test_timestamp_month == 4 || test_timestamp_month == 5)
						//&& test_timestamp_day >= 7
						//&& test_timestamp_day <= 21
						&& ((test_timestamp_hours >= 8
								&& test_timestamp_hours <= 11)
								|| (test_timestamp_hours >=18 && test_timestamp_hours <= 20))
						&& topItems.contains(item)  && n_clicks>3 )

				{
					bw.write(user + ": " + item);
					bw.newLine();
					bw.close();
				}
			}else{
				long test_timestamp = test
						.getPreferenceTime(user, item);
				String test_timestamp_s = Long.toString(test_timestamp);
				Long test_timestamp_month = Long
						.parseLong(test_timestamp_s.substring(4, 6));
				Long test_timestamp_day = Long
						.parseLong(test_timestamp_s.substring(6, 8));
				Long test_timestamp_hours = Long
						.parseLong(test_timestamp_s.substring(8, 10));
				if ((test_timestamp_month == 8
						|| test_timestamp_month == 4 || test_timestamp_month == 5)
						//&& test_timestamp_day >= 7
						//&& test_timestamp_day <= 21
						&& ((test_timestamp_hours >= 8
								&& test_timestamp_hours <= 11)
								|| (test_timestamp_hours >=18 && test_timestamp_hours <= 20))
						&& topItems.contains(item)  && n_clicks>3 && predicted_users.contains(user))
				{
					bw.write(user + ": " + item);
					bw.newLine();
					bw.close();
				}
			}
		}
	}

	public static void main(String[] args) throws IOException, TasteException {
		// TODO Auto-generated method stub

		Predictions clicks = new Predictions(new File("data/cv1_train_clicks"));
		Predictions test = new Predictions(new File("data/cv1_test_clicks"));
		Predictions buys = new Predictions(new File("data/cv1_train_buys"));
		parseTopItems();
		FileReader fr = new FileReader(
				"/Users/Bharath/Documents/695 workspace/cv1_similarusers");
		BufferedReader br = new BufferedReader(fr);
		String line;
		Long user = 0L;
		Long item = 0L;
		int n_clicks = 0;
		while ((line = br.readLine()) != null) {
			if (line.contains("similar:{")) {
				String words[] = line.split(" ");
				user = Long.parseLong(words[1]);
				item = Long.parseLong(words[3]);
				n_clicks = Integer.parseInt(words[5]);
				if (n_clicks != 1) {
					if (!words[7].contains("}") && !words[7].isEmpty()) {
						for (int i = 7; i < words.length; i++) {
							System.out.println(words[i]);
							if (!words[i].contains("}") && !words[i].isEmpty()){
								System.out.println("words[]" + words[i]);
								similarUsers.add(Long.parseLong(words[i]));
							}
						}
						if (line.contains("}")) {
							SimilarityMeasure similarities = new SimilarityMeasure(
									clicks, test);
							FastByIDMap<FastByIDMap<Double>> neighbors = similarities
									.computeNeighborsForId(user, similarUsers);
							similarUsers.clear();
							Long mostSimilar = getUserWithMaxSimilarity(
									neighbors, user);
							getPredictions(user, mostSimilar,n_clicks, item, clicks,
									buys, test);
						}
					}else{
						getPredictions(user, 0L, n_clicks,item, clicks, buys, test);
					}
				} else {
					getPredictions(user, 0L, n_clicks,item, clicks, buys, test);
				}
			} else {
				if (line.contains("}")) {
					String words[] = line.split(" ");
					for (int i = 0; i < words.length; i++) {
						if (!words[i].contains("}") && !words[i].isEmpty()){
							System.out.println("words[]" + words[i]);
							similarUsers.add(Long.parseLong(words[i]));
						}
					}
					SimilarityMeasure similarities = new SimilarityMeasure(
							clicks, test);
					FastByIDMap<FastByIDMap<Double>> neighbors = similarities
							.computeNeighborsForId(user, similarUsers);
					similarUsers.clear();
					Long mostSimilar = getUserWithMaxSimilarity(neighbors, user);
					System.out.println("similar " + mostSimilar);
					getPredictions(user, mostSimilar, n_clicks, item, clicks, buys, test);
				} else {
					String words[] = line.split(" ");
					for (int i = 0; i < words.length; i++) {
						similarUsers.add(Long.parseLong(words[i]));
					}
				}
			}
		}
	}

}
