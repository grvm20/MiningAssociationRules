import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class SetComparator implements Comparator<Set<String>> {

    Map<Set<String>, Integer> hm;
    public SetComparator(Map<Set<String>, Integer> hm) {
        this.hm = hm;
    }

    public int compare(Set<String> s1, Set<String> s2) {
        if (hm.get(s1) >= hm.get(s2)) {
            return -1;
        } else {
            return 1;
        }
    }
}
