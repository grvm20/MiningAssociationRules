import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class AprioriTest {

	static List<Set<String>> transactionList = new ArrayList<>();
	static List<Rule> finalrules = new ArrayList<Rule>();
	static List<Set<String>> largeItemList = new ArrayList<Set<String>>();
	static Map<Set<String>, Double> supports = new HashMap<>();
	static Set<Set<String>> set1 = new HashSet<>();

	public static void AprioriGen(String dataSet, double minSupport,
			double minConfidence) {

		CSVParser csvFileParser = null;
		CSVFormat csvFileFormat = CSVFormat.DEFAULT;

		System.out.println("Start processing file");
		List<List<List<String>>> largeItemSetList = new ArrayList<>();

		Set<String> itemSet = new HashSet<>();
		Map<String, Integer> itemSupportMap = new HashMap<>();
		String fileName = "Resources/Inspections.csv";

		try (FileReader fileReader = new FileReader(fileName)) {
			csvFileParser = new CSVParser(fileReader, csvFileFormat);
			List<CSVRecord> csvRecords = csvFileParser.getRecords();

			for (int i = 0; i < csvRecords.size(); i++) {
				CSVRecord record = csvRecords.get(i);
				Iterator<String> itero = record.iterator();
				Set<String> transaction = new HashSet<>();
				while (itero.hasNext()) {
					String item = "'" + itero.next() + "'";
					itemSupportMap.put(item,
							itemSupportMap.getOrDefault(item, 0) + 1);
					itemSet.add(item);
					transaction.add(item);
				}
				transactionList.add(transaction);

			}

			System.out.println("Start 1st set ");
			List<List<String>> level1 = new ArrayList<>();
			for (Map.Entry<String, Integer> entry : itemSupportMap.entrySet()) {
				String key = entry.getKey();
				Integer value = entry.getValue();
				if ((double) (value) / transactionList.size() >= minSupport) {
					List<String> list = new ArrayList<>();
					list.add(key);
					level1.add(list);
					supports.put(new HashSet<>(list), (double) (value)
							/ transactionList.size());
				}
			}

			largeItemSetList.add(level1);
			System.out.println("Start remaining sets");
			int k = 1;

			while (largeItemSetList.get(k - 1).size() != 0) {
				List<List<String>> candidateLists = new ArrayList<>();
				for (int i = 0; i < largeItemSetList.get(k - 1).size(); i++) {
					for (int j = i + 1; j < largeItemSetList.get(k - 1).size(); j++) {
						List<String> list1 = largeItemSetList.get(k - 1).get(i);
						List<String> list2 = largeItemSetList.get(k - 1).get(j);
						if (equalAllButOneElements(list1, list2)) {
							if (!list1.get(list1.size() - 1).equals(
									list2.get(list2.size() - 1))) {
								List<String> newList = new ArrayList<>(list1);
								newList.add(list2.get(list2.size() - 1));
								Collections.sort(newList);
								candidateLists.add(newList);
							}
						}

					}
				}
				System.out.println("K: " + k);

				List<List<String>> largeCandidates = calculateLargeList(
						candidateLists, transactionList, minSupport);

				largeItemSetList.add(largeCandidates);

				k++;

			}

			System.out.println("LargeItem Sets extraction done");

			largeItemSetList.remove(largeItemSetList.size() - 1); // Since last
																	// contains
																	// null

			for (int i = 1; i < largeItemSetList.size(); i++) {

				for (int j = 0; j < largeItemSetList.get(i).size(); j++) {
					List<String> list = largeItemSetList.get(i).get(j);
					partition(list, list, 0, new ArrayList<>(), minConfidence,
							supports.get(new HashSet<>(list)));
				}

			}

			System.out.println("Rule processing complete");
			Collections.sort(finalrules);

			for (int i = 0; i < finalrules.size(); i++) {
				System.out.println(finalrules.get(i));
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<List<String>> calculateLargeList(
			List<List<String>> candidateLists,
			List<Set<String>> transactionList2, double minSupport) {

		List<List<String>> largeData = new ArrayList<>();
		Map<List<String>, Integer> map = new HashMap<>();
		for (Set<String> transaction : transactionList2) {

			for (List<String> list : candidateLists) {
				Set<String> set = new HashSet<>(list);
				if (transaction.containsAll(set)) {
					map.put(list, map.getOrDefault(list, 0) + 1);
				}
			}

		}

		for (Map.Entry<List<String>, Integer> entry : map.entrySet()) {
			if ((double) (entry.getValue()) / transactionList.size() >= minSupport) {
				List<String> list = entry.getKey();
				largeData.add(list);
				supports.put(new HashSet<>(list), (double) (entry.getValue())
						/ transactionList.size());
			}
		}

		return largeData;
	}

	private static boolean equalAllButOneElements(List<String> list1,
			List<String> list2) {
		if (list1.size() != list2.size())
			return false;
		for (int i = 0; i < list1.size() - 1; i++) {
			if (!list1.get(i).equals(list2.get(i)))
				return false;
		}
		return true;
	}

	private static void partition(List<String> original, List<String> list1,
			int start, List<String> list2, double minConfidence,
			double supportNumerator) {
		if (set1.contains(new HashSet<>(list1))) {
			return;
		}
		if (!(list1.size() == 0) && !(list2.size() == 0)) {
			Double supportDenominator = supports.get(new HashSet<>(list1));

			double conf = supportNumerator / (double) supportDenominator;

			if (conf >= minConfidence) {
				Rule rule = new Rule(new HashSet<>(list1),
						new HashSet<>(list2), conf, supportNumerator);
				finalrules.add(rule);
			}

			set1.add(new HashSet<>(list1));

		}

		for (int i = 0; i < list1.size(); i++) {
			String temp = list1.get(i);
			list2.add(temp);
			list1.remove(i);
			partition(original, list1, i + 1, new ArrayList<>(list2),
					minConfidence, supportNumerator);
			list1.add(i, temp);
			list2.remove(list2.size() - 1);
		}

	}

	public static void main(String args[]) {
		AprioriGen("Sample", 0.01, 0.5);
	}
}