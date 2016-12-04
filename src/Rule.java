import java.util.Arrays;
import java.util.Set;

public class Rule implements Comparable<Rule> {

	Set<String> left;
	Set<String> right;
	double confidence;
	double support;

	public Rule(Set<String> left, Set<String> right, double confidence,
			double support) {
		this.left = left;
		this.right = right;
		this.confidence = confidence;
		this.support = support;
	}

	@Override
	public String toString() {
		return Arrays.toString(left.toArray()) + " ==> "
				+ Arrays.toString(right.toArray()) + " (Confidence: "
				+ confidence * 100 + "%, Support: " + support + "%)";
	}

	@Override
	public int compareTo(Rule that) {
		return -1*(this.confidence < that.confidence ? -1
				: this.confidence > that.confidence ? 1 : 0);
	}

}
