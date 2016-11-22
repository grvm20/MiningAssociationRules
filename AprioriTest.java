import java.io.BufferedReader;

import java.io.FileReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class AprioriTest {

	static List<List<String>> transactionList = new ArrayList<List<String>>();
	static List<Rule> finalrules = new ArrayList<Rule>();
	static List<Set<String>> largeItemList = new ArrayList<Set<String>>();
	
	public static void AprioriGen(String dataSet,double minSupport,double minConfidence) {

		System.out.println("Start processing file");
		List<Set<Set<String>>> largeItemSetList = new ArrayList<Set<Set<String>>>();
		
		Set<String> itemSet = new HashSet<String>();
		Map<String,Integer> itemSupportMap = new HashMap<String,Integer>();
		Map<Set<String>,Integer> largeItemSupportMap = new HashMap<Set<String>,Integer>();
		
		try
		{
			String fileName = "Resources/New_York_City_Leading_Causes_of_Death.csv";//+ dataSet + ".csv";
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			while((line = br.readLine()) != null)
			{
				String[] items = line.split(",");
				List<String> transaction = new ArrayList<String>();
				for(String i : items)
				{
					if(itemSupportMap.containsKey(i))
					{
						int newCount = (itemSupportMap.get(i))+1;
						itemSupportMap.remove(i);
						itemSupportMap.put(i, newCount);
						itemSet.add(i);
					}
					else
					{
						itemSupportMap.put(i, 1);
					}
					transaction.add(i);
				}
				transactionList.add(transaction);
			}
			br.close();
			System.out.println("Start 1st set ");
			Iterator<Entry<String, Integer>> iterator = itemSupportMap.entrySet().iterator();
			Set<String> temp = new HashSet<String>();
			while(iterator.hasNext())
			{
				Entry<String, Integer> entry = iterator.next();
				String key = (String)entry.getKey();
				Integer value = (Integer)entry.getValue();
				if((double)(value)/transactionList.size() >= minSupport)
				{
					temp.add(key);
				}
			}
			Set<Set<String>> setString = new HashSet<Set<String>>();
			setString.add(temp);
			largeItemSetList.add(setString);
			System.out.println("Start remaining sets");
			int k = 1;
			while(largeItemSetList.get(k-1).size() != 0)
			{
				System.out.println("Large item set k:"+k);
				Set<Set<String>> addToLargeItemSetList = new HashSet<Set<String>>(); //APRIORI-GEN---new candidates
				ArrayList<String> entryList = new ArrayList<String>();
				ArrayList<Set<String>> preCkList = new ArrayList<Set<String>>();
				ArrayList<Set<String>> ckList = new ArrayList<Set<String>>();

				Iterator<Set<String>> iterator3 = largeItemSetList.get(k-1).iterator();
				while(iterator3.hasNext())
				{
					Set<String> s = iterator3.next();
					for(String string : s)
					{
						entryList.add(string);
					}
				}
				System.out.println("Checkpoint1 "+k);
				preCkList = getSubsets(entryList);
				
				//pruning candidates not included in previous level-- could be done in getsubsets
				for(int i = 0; i < preCkList.size(); i ++)
				{
					if(preCkList.get(i).size() == k+1)
					{
						ckList.add(preCkList.get(i));
					}
				}
				System.out.println("Checkpoint2 "+k);

				for(int i = 0; i < ckList.size(); i ++)
				{

					if(!largeItemSetList.get(k-1).containsAll(ckList.get(i)))
					{
						ckList.remove(i);
						continue;
					}
				}
				System.out.println("Checkpoint "+k);


				for(int i = 0; i < ckList.size(); i ++)
				{
					int count = 0;
					for(int j = 0; j < transactionList.size(); j ++)
					{
						if(transactionList.get(j).containsAll(ckList.get(i)))
							count ++;
					}
					if((double)((double)count/transactionList.size()) > minSupport)
					{
						addToLargeItemSetList.add(ckList.get(i));
					}
				}
				k ++;
				largeItemSetList.add(addToLargeItemSetList);
			}
			System.out.println("LargeItem Sets extraction done");
			largeItemSetList.remove(largeItemSetList.size()-1); //Since last contains null
			Set<Set<String>> tempSet = largeItemSetList.get(0);
			Iterator<Set<String>> tempIter = tempSet.iterator();
			Set<String> tempTempSet = tempIter.next();
			Iterator<String> tempTempIter = tempTempSet.iterator();
			Map<String, Integer> refMap = new HashMap<String, Integer>();
			while(tempTempIter.hasNext())
			{
				String enterStringInSet = tempTempIter.next();
				Set<String> newEntry = new HashSet<String>();
				newEntry.add(enterStringInSet);
				Integer newEntryCount = -1;
				
				Iterator<Entry<String, Integer>> innerIter = itemSupportMap.entrySet().iterator();
				while(innerIter.hasNext())
				{
					Entry<String, Integer> entry = innerIter.next();
					String key = (String)entry.getKey();
					Integer value = (Integer)entry.getValue();
					if(key.equals(enterStringInSet))
					{
						newEntryCount = value;
						refMap.put(key, value);
					}
				}
				largeItemSupportMap.put(newEntry, newEntryCount);
			}
			
			for(int i = 1; i < largeItemSetList.size(); i ++)
			{
				Set<Set<String>> setSetString = largeItemSetList.get(i);
				Iterator<Set<String>> innerIter = setSetString.iterator();
				while(innerIter.hasNext())
				{
					Set<String> keySet = innerIter.next();
					Integer supportValue = 0;
					supportValue = getSupportCount(keySet);
					largeItemSupportMap.put(keySet, supportValue);
				}
			}
			System.out.println("Start rules processing");
			
			List<Rule> tempRuleList = new ArrayList<Rule>();
			Iterator<Entry<Set<String>, Integer>> iter = largeItemSupportMap.entrySet().iterator();
			while(iter.hasNext())
			{
				Entry<Set<String>, Integer> entry = iter.next();
				Set<String> key = (Set<String>)entry.getKey();
				Integer value = (Integer)entry.getValue();
				Iterator<Entry<Set<String>, Integer>> inneriter = largeItemSupportMap.entrySet().iterator();
				while(inneriter.hasNext())
				{
					Entry<Set<String>, Integer> innerEntry = inneriter.next();
					Set<String> innerKey = (Set<String>)innerEntry.getKey();
					Integer innerValue = (Integer)innerEntry.getValue();
					
					if(union(key, innerKey).size() != 0 && innerKey.size() == 1)
					{
						Rule rule = new Rule(transactionList);
						rule.setLhs(key);
						rule.setRhs(innerKey);
						rule.setLhsCount(value);
						rule.setRhsCount(innerValue);
						tempRuleList.add(rule);
					}
				}
			}
			
			
			for(int i = 0; i < tempRuleList.size(); i ++)
			{
				Rule currentRule = tempRuleList.get(i);
				if(currentRule.getLeft().containsAll(currentRule.getRight()))
				{
					tempRuleList.remove(currentRule);
					continue;
				}
				if(currentRule.calculateConfidence() < minConfidence)
				{
					tempRuleList.remove(currentRule);
					continue;
				}
				finalrules.add(currentRule);
			}
			SetComparator comp =  new SetComparator(largeItemSupportMap);
			TreeMap<Set<String>, Integer> sortedSupportMap = new TreeMap<Set<String>, Integer>(comp);
			sortedSupportMap.putAll(largeItemSupportMap);
			
			Comparator<Rule> comparator = new Comparator<Rule>() {
				public int compare(Rule r1, Rule r2) {
					if(r1.confidence <= r2.confidence)
						return 1;
					return -1;
				}
			}; 
			System.out.println("Rule processing complete");
			Collections.sort(finalrules, comparator);
			
			
			for(int i = 0; i < finalrules.size(); i ++)
			{
				System.out.println(finalrules.get(i));
			}
			
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static ArrayList<Set<String>> getSubsets(ArrayList<String> set)
	{

		ArrayList<Set<String>> subsetCollection = new ArrayList<Set<String>>();

		if (set.size() == 0) {
			subsetCollection.add(new HashSet<String>());
		} else {
			ArrayList<String> reducedSet = new ArrayList<String>();

			reducedSet.addAll(set);

			String first = reducedSet.remove(0);
			ArrayList<Set<String>> subsets = getSubsets(reducedSet);
			subsetCollection.addAll(subsets);

			subsets = getSubsets(reducedSet);

			for (Set<String> subset : subsets) {
				subset.add(first);
			}

			subsetCollection.addAll(subsets);
		}

		return subsetCollection;
	}
	
	public static <T>Set<T> union(Set<T> setA, Set<T> setB) {
	    Set<T> tmp = new HashSet<T>(setA);
	    tmp.addAll(setB);
	    return tmp;
	  }
	
	private static int getSupportCount(Set<String> set)
	{
		int count = 0;
		for(int i = 0; i < transactionList.size(); i ++)
		{
			if(transactionList.get(i).containsAll(set))
			{
				count ++;
			}
		}
		return count;
	}
	public static void main(String args[]){
		AprioriGen("Sample",0.07,0.01);
	}
}