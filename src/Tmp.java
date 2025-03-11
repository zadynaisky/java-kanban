import java.util.HashMap;
import java.util.Map;

public class Tmp {
    public static void main(String[] args) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "one");
        map.put(2, "two");

        String s = map.get(3);
        System.out.println(s == null);
    }
}
