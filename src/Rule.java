import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Rule {
	
	Set<String> left;
	int leftSup;
	Set<String> right;	
	int rightSup;
	double confidence;
	double support;
	
List<List<String>> transactionList = new ArrayList<List<String>>();
	
	Rule(List<List<String>> tl)
	{
		transactionList = tl;
		left = new HashSet<String>();
		right = new HashSet<String>();
	}
	
	private int calculateSupport(Set<String> set)
	{
		int c = 0;
		for(int i = 0; i < transactionList.size(); i ++)
		{
			if(transactionList.get(i).containsAll(set))
			{
				c ++;
			}
		}
		return c;
	}
	
	public void setLhs(Set<String> left) {
		this.left = left;
	}
	
	public Set<String> getLeft() {
		return left;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(left.toArray()) + " ==> " + Arrays.toString(right.toArray()) + " (Confidence: " + confidence * 100 + "%, Support: " + (calculateSupport(union(left,right)) / (double)transactionList.size()) * 100 + "%)";
	}
	
	public int getLeftCount() {
		return leftSup;
	}
	
	public Set<String> getRight() {
		return right;
	}
	
	public int getRightSup() {
		return rightSup;
	}
	
	public void setLhsCount(int leftSupport) {
		this.leftSup = leftSupport;
	}
	
	public void setRhs(Set<String> right) {
		this.right = right;
	}
	
	public void setRhsCount(int rightSupport) {
		this.rightSup = rightSupport;
	}
	
	public double calculateConfidence()
	{
		confidence = calculateSupport(union(left, right));
		support = (int)confidence;
		confidence /= calculateSupport(left);
		return confidence;
	}
	
	public <T>Set<T> union(Set<T> setA, Set<T> setB) {
	    Set<T> tmp = new HashSet<T>(setA);
	    tmp.addAll(setB);
	    return tmp;
	  }
	

}
