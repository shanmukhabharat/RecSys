package Deafult;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.AbstractDataModel;
import org.apache.mahout.cf.taste.impl.similarity.GenericUserSimilarity.UserUserSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.similarity.PreferenceInferrer;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import com.google.common.base.Preconditions;


public class SimilarityMeasure implements UserSimilarity{
	
	private final DataModel training;
	private final DataModel test;
	private final FastByIDMap<FastByIDMap<Double>> similarityMaps;
	
	public SimilarityMeasure(DataModel training, DataModel test) throws TasteException{
		Preconditions.checkArgument(training != null, "training is null");
		Preconditions.checkArgument(test != null, "test is null");
	    this.training = training;
	    this.test = test;
	    similarityMaps = new FastByIDMap<FastByIDMap<Double>>();
	    //computeSimilarities(training, test);
	}

	private void computeSimilarities(DataModel training, DataModel test) throws TasteException {
		// TODO Auto-generated method stub
		UserUserSimilarity final_sim;
		LongPrimitiveIterator it = test.getUserIDs();
		while(it.hasNext()){
			 Long userID1 = it.nextLong();
			 LongPrimitiveIterator it2 = training.getUserIDs();
			 while(it2.hasNext()){
				 Long userID2 = it2.nextLong();
				 Double sim = userSimilarity(userID1,userID2);
				 //final_sim = new UserUserSimilarity(userID1,userID2,sim);
				 
				 FastByIDMap<Double> map = similarityMaps.get(userID1);
				 if(map == null){
				      map = new FastByIDMap<Double>();
			          similarityMaps.put(userID1, map);
			     }
			     map.put(userID2, sim);
			     System.out.println("userId" + userID1 + "mapping" + similarityMaps.get(userID1));
			  
			 
			 }
		 }
	}

	public FastByIDMap<FastByIDMap<Double>> computeNeighborsForId(long userID, ArrayList<Long> sim_users) throws TasteException{
		 for(Long each : sim_users){
			 Double sim = userSimilarity(userID,each);
			 //final_sim = new UserUserSimilarity(userID1,userID2,sim);
			 
			 FastByIDMap<Double> map = similarityMaps.get(userID);
			 if(map == null){
			      map = new FastByIDMap<Double>();
		          similarityMaps.put(userID, map);
		     }
		        map.put(each, sim);
		        System.out.println("userId" + userID + "mapping" + similarityMaps.get(userID));
		  
		 
		 }
		return similarityMaps;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
		// TODO Auto-generated method stub
	}

	//requires: userID1 from test set and userID2 from training set
	//ensures: the similarity measure between the user from test set and the user from training set
	@Override
	public double userSimilarity(long userID1, long userID2)
			throws TasteException {
		
		float pref_measure = 0, month_measure = 0, day_measure = 0, hours_measure = 0, minutes_measure = 0, sum = 0, diff = 0; 
		ArrayList<Float> similarities = new ArrayList<Float>();
		
		DataModel training = getTrainingModel();
		DataModel test = getTestModel();
		FastIDSet test_items = test.getItemIDsFromUser(userID1);
	    FastIDSet training_items = training.getItemIDsFromUser(userID2);
	    //for each item in test user
	    for(long test_item: test_items){
	    	long test_timestamp = test.getPreferenceTime(userID1, test_item);
	    	String test_timestamp_s = Long.toString(test_timestamp); 
	    	Long test_timestamp_month = Long.parseLong(test_timestamp_s.substring(4,6));
	    	Long test_timestamp_day = Long.parseLong(test_timestamp_s.substring(6,8));
	    	Long test_timestamp_hours = Long.parseLong(test_timestamp_s.substring(8, 10));
	    	Long test_timestamp_minutes = Long.parseLong(test_timestamp_s.substring(10));
	    	Float test_pref = test.getPreferenceValue(userID1, test_item);
	    	//for each item in training user
	    	for(long training_item: training_items){
	    		float similarity_per_item = 0;
	    		long training_timestamp = training.getPreferenceTime(userID2, training_item);
	    		Float training_pref = training.getPreferenceValue(userID2, training_item);
	    		String training_timestamp_s = Long.toString(training_timestamp);
	    		Long training_timestamp_month = Long.parseLong(training_timestamp_s.substring(4,6));
		    	Long training_timestamp_day = Long.parseLong(training_timestamp_s.substring(6,8));
		    	Long training_timestamp_hours = Long.parseLong(training_timestamp_s.substring(8, 10));
		    	Long training_timestamp_minutes = Long.parseLong(training_timestamp_s.substring(10));
		    	
		    	//Preference similarity
		    	if(training_pref == test_pref)
		    		pref_measure = 1;
		    	else
		    		pref_measure = 0;
		    	
		    	similarity_per_item = similarity_per_item + pref_measure;
		    	
		    	//month similarity
		    	if(test_timestamp_month == training_timestamp_month)
		    		month_measure = 1;
		    	else{
		    		diff =  Math.abs(training_timestamp_month - test_timestamp_month);
		    		month_measure = 1-((float)diff/12);
		    	}
		    	
		    	similarity_per_item = similarity_per_item + month_measure;
		    	
		    	//day similarity
		    	if(test_timestamp_day == training_timestamp_day)
		    		day_measure = 1;
		    	else{
		    		diff = Math.abs(training_timestamp_day - test_timestamp_day);
		    		day_measure = 1-(diff/30);
		    	}
		    	
		    	similarity_per_item = similarity_per_item + day_measure;
		    	
		    	//hours similarity 
		    	if(test_timestamp_hours == training_timestamp_hours)
		    		hours_measure = 1;
		    	else{
		    		diff = Math.abs(training_timestamp_hours - test_timestamp_hours);
		    		hours_measure = 1-(diff/24);
		    	}
		    	
		    	similarity_per_item = similarity_per_item + hours_measure;
		    	
		    	//minutes similarity
		    	if(test_timestamp_minutes == training_timestamp_minutes)
		    		minutes_measure = 1;
		    	else{
		    		diff = Math.abs(training_timestamp_minutes - test_timestamp_minutes);
		    		minutes_measure = 1-(diff/60);
		    	}
		    		
		    	similarity_per_item = similarity_per_item + minutes_measure;
		    	
		    	float final_similarity_per_item = similarity_per_item/5;
		    	similarities.add(final_similarity_per_item);
		    }
	    }
	    for(float each : similarities){
	    	sum = sum+each;
	    }
		return (double) sum/similarities.size();
	}

	
	@Override
	public void setPreferenceInferrer(PreferenceInferrer inferrer) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
		
	}
	
	public FastByIDMap<FastByIDMap<Double>> getSimilarities(){
		return similarityMaps;
	}
	
	protected DataModel getTrainingModel() {
		return training;
	}

	protected DataModel getTestModel() {
		return test;
	}

}
