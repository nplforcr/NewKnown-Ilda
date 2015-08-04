package resultProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.Pair;
import annotation.MentionProperty;

public class GenerateIdent {
	public static HashMap<String, Integer> mpos; // mention => id
	public static List<String> mm; // mention
	public static List<Pair> mid; // id1 => id2(descending)
	public static int fileID = 3;

	public GenerateIdent(String inputFile1, String inputFile2, String outputFile)
			throws IOException {
		mpos = new HashMap<String, Integer>();
		mm = new ArrayList<String>();
		mid = new ArrayList<Pair>();
		this.processDoc(inputFile1, inputFile2, outputFile);
	}

	/**
	 * addressing baseresult and mention list
	 * 
	 * @param inputFile1
	 * @param inputFile2
	 * @param outputFile
	 * @throws IOException
	 */
	private void processDoc(String inputFile1, String inputFile2,
			String outputFile) throws IOException {
		// mention id => property
		HashMap<Integer, MentionProperty> map = new HashMap<Integer, MentionProperty>();
		List<List<MentionProperty>> resList = new ArrayList<List<MentionProperty>>();
		List<Integer> lpos = new ArrayList<Integer>();
		FileReader fr1 = new FileReader(inputFile1);
		FileReader fr2 = new FileReader(inputFile2);
		BufferedReader br1 = new BufferedReader(fr1);
		BufferedReader br2 = new BufferedReader(fr2);
		String str1 = null, str2 = null;
		while ((str1 = br1.readLine()) != null) {
			String[] data = str1.split(":");
			int topic = Integer.parseInt(data[0].split(" ")[0]);
			float topicProb = Float
					.parseFloat(getContentOfParentheses(data[0]));
			process(map, topic, topicProb, data[1].trim().split("\\s+"),
					resList);
			str1 = null;
		}
		int rowno = 0;
		while ((str2 = br2.readLine()) != null) {
			rowno++;
			if (rowno != fileID)
				continue;
			String[] data = str2.split("=");
			int id = 0;
			for (int i = data.length - 2; i >= 0; i = i - 2) {
				mpos.put(data[i], id);
				id++;
			}
		}
		fr1.close();
		fr2.close();
		FileWriter fw = new FileWriter(outputFile);
		for (int i = 0; i < resList.size(); i++) {
			if (resList.get(i).size() < 2)
				continue;
			for (int j = 0; j < resList.get(i).size() - 1; j++) {
				for (int k = j + 1; k < resList.get(i).size(); k++) {
					mm.add(resList.get(i).get(j).getMention());
					mm.add(resList.get(i).get(k).getMention());
				}
			}
		}
		for (int i = 0; i < mm.size(); i += 2) {
			int pos1 = -1, pos2 = -1;
			if (mpos.containsKey(mm.get(i)) && mpos.containsKey(mm.get(i + 1))) {
				pos1 = mpos.get(mm.get(i));
				pos2 = mpos.get(mm.get(i + 1));
				if(pos1 > pos2)
				{
					int tmp = pos1;
					pos1 = pos2;
					pos2 = tmp;
				}
				if(pos1 == pos2) continue;
				Pair pair = new Pair(pos1, pos2);
				mid.add(pair);
			}
		}
		Collections.sort(mid);
		for (int i = 0; i < mid.size(); i++) {
			String pos1 = mid.get(i).getFirst().toString();
			String pos2 = mid.get(i).getSecond().toString();
			System.out.println("IDENT " + pos1 + " " + pos2);
			fw.write("IDENT " + pos1 + " " + pos2 + "\r\n");
		}
		fw.close();
	}

	/**
	 * get string in quotation
	 * 
	 * @param s
	 * @return String
	 */
	private String getContentOfParentheses(String s) {
		String res = "";
		int flag = 0;
		for (int i = 0; i < s.length() && s.charAt(i) != ')'; i++) {
			if (s.charAt(i) == '(') {
				flag = 1;
				continue;
			}
			if (1 == flag) {
				res += s.charAt(i);
			}
		}
		return res;
	}

	/**
	 * addressing baseresult
	 * 
	 * @param map
	 * @param topic
	 * @param topicProb
	 * @param str
	 * @param lid
	 */
	private void process(HashMap<Integer, MentionProperty> map, int topic,
			float topicProb, String[] str, List<List<MentionProperty>> resList) {
		List<MentionProperty> tmp = new ArrayList<MentionProperty>();
		for (int i = 0; i < str.length; i += 3) {
			int position = Integer.parseInt(str[i].trim());
			String mention = str[i + 1].trim();
			while (getContentOfParentheses(str[i + 2].trim()).isEmpty()) {
				mention = "" + mention + str[i + 2];
				i++;
			}
			String s = getContentOfParentheses(str[i + 2].trim());
			float prob = Float.parseFloat(s);
			/*
			 * if (prob < 0.01) continue;
			 */
			if (map.containsKey(position)
					&& (prob * topicProb <= map.get(position).getProb()
							* map.get(position).getTopicProb())) {
				continue;
			}
			MentionProperty mp = new MentionProperty();
			mp.setTopic(topic);
			mp.setPosition(position);
			mp.setMention(mention);
			mp.setTopicProb(topicProb);
			mp.setProb(prob);
			if (!map.containsKey(position)) {
				map.put(position, mp);
				tmp.add(mp);
			}
		}
		resList.add(tmp);
	}

	public static void main(String[] args) throws IOException {
		String inputFile1 = "input/nips.result.txt";
		String inputFile2 = "input/nips.mention.txt";
		String outputFile = "output/output.txt";
		GenerateIdent gi = new GenerateIdent(inputFile1, inputFile2, outputFile);
	}
}
