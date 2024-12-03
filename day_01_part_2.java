import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

void main() throws IOException {
    String path;
    if (System.getenv().containsKey("EXAMPLE")) {
        path = "input/day-01-example.txt";
    } else {
        path = "input/day-01-full.txt";
    }

    var left = new ArrayList<Long>();
    var rCounts = new HashMap<Long, Long>();

    for (var line : Files.readAllLines(Path.of(path))) {
        var splits = line.split(" +");
        left.add(Long.valueOf(splits[0]));
        var r = Long.valueOf(splits[1]);
        var count = rCounts.get(r);
        if (count == null) {
            rCounts.put(r, 1L);
        } else {
            rCounts.put(r, count + 1);
        }
    }

    long sum = 0;
    for (var i : left) {
        var rCount = rCounts.get(i);
        if (rCount != null) sum += (long) i * rCount;
    }
    System.out.println(sum);
}
