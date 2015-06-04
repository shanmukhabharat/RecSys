package Deafult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;

public class ErrorMeasures {

	public static void main(String[] args) throws IOException {
		
		int tp=0,fp=0,fn=0;
		float overall_score =0;
		ArrayList<Long> users = new ArrayList<Long>();
		ArrayList<Long> test_users = new ArrayList<Long>();
		//HashMap<Long, Long> user_item = new HashMap<Long, Long>();
		//HashMap<Long, Long> p_user_item = new HashMap<Long, Long>();
		FastByIDMap<FastByIDMap<Long>> user_item = new FastByIDMap<FastByIDMap<Long>>();
		FastByIDMap<FastByIDMap<Long>> p_user_item = new FastByIDMap<FastByIDMap<Long>>();
		ArrayList<Long> As = new ArrayList<Long>();
		ArrayList<Long> Bs = new ArrayList<Long>();
		
		FileReader fr=new FileReader(new File("data/cv1_test_buys"));
		BufferedReader br=new BufferedReader(fr);     
		String line;
		while ((line=br.readLine())!= null){ 
			String words[] = line.split(",");
			String user = words[0];
			String item = words[1];
			if(!users.contains(Long.parseLong(user))){
				users.add(Long.parseLong(user));
			}
			FastByIDMap<Long> map1 = user_item.get(Long.parseLong(user));
			 if(map1 == null){
			      map1 = new FastByIDMap<Long>();
			      user_item.put(Long.parseLong(user), map1);     
			 }
			 map1.put(Long.parseLong(item), (long) 1);
			 System.out.println("map" + map1 + "user" + user);
		}
			
		ArrayList<Long> p_users = new ArrayList<Long>();
		FileReader fr1=new FileReader(new File("data/cv1_finalResult"));
		BufferedReader br1=new BufferedReader(fr1);     
		String line1;
		while ((line1=br1.readLine())!= null){ 
			String words[] = line1.split(":");
			String user = words[0];
			String item = words[1];
			String items[] = item.split(",");
			p_users.add(Long.parseLong(user));
			FastByIDMap<Long> map = p_user_item.get(Long.parseLong(user));
			if(map == null){
			      map = new FastByIDMap<Long>();
			      p_user_item.put(Long.parseLong(user), map); 
			}
			for(int i = 0 ; i<items.length;i++){
				map.put(Long.parseLong(items[i]), (long) 1);
			}
			 System.out.println("map" + map + "user" + user);
			if(users.contains(Long.parseLong(user)))
				tp++;
			else
				fp++;
		}
		
		FileReader fr2=new FileReader(new File("data/cv1_test_clicks"));
		BufferedReader br2=new BufferedReader(fr2);     
		String line2;
		while ((line2=br2.readLine())!= null){ 
			String words[] = line2.split(",");
			String user = words[0];
			if(!test_users.contains(Long.parseLong(user))){
				test_users.add(Long.parseLong(user));
			}
		}
		
		ArrayList<Long> temp = new ArrayList<Long>(users);
		temp.removeAll(p_users);
		fn = temp.size();
		System.out.println("user" + users.size() + "p_users" + p_users.size());
		System.out.println("tp" + tp + "fp" + fp + "fn" + fn);
		System.out.println("Precision" + (float)tp/(tp+fp));
		System.out.println("Recall" + (float)tp/(tp+fn));
		
		for(Long each : p_users){
			if(users.contains(each)){	
				FastByIDMap<Long> map1 = user_item.get((each));
				FastByIDMap<Long> map2 = p_user_item.get((each));
				LongPrimitiveIterator itr1 = map1.keySetIterator();
				LongPrimitiveIterator itr2 = map2.keySetIterator();
				while(itr1.hasNext()){
					Bs.add(itr1.nextLong());
				}
				while(itr2.hasNext()){
					As.add(itr2.nextLong());
				}
				
				ArrayList<Long> temp_As = new ArrayList<Long>(As);
				As.retainAll(Bs);
				for(Long item: temp_As){
					if(!Bs.contains(item))
						Bs.add(item);
				}
				overall_score = overall_score+((float)users.size()/test_users.size())+((float)As.size()/Bs.size());
				As.clear();
				Bs.clear();
			}else{
				overall_score = overall_score - ((float)users.size()/test_users.size());
			}
		}
		
		System.out.println("Final score" + overall_score);
		
		
	}
 }

