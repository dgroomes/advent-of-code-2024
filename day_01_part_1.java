import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

void main() throws IOException {
    String path;
    if (System.getenv().containsKey("EXAMPLE")) {
        path = "input/day-01-example.txt";
    } else {
        path = "input/day-01-full.txt";
    };

    var left = new ArrayList<Integer>();
    var right = new ArrayList<Integer>();

    for (var line : Files.readAllLines(Path.of(path))) {
        String[] splits = line.split(" +");
        left.add(Integer.valueOf(splits[0]));
        right.add(Integer.valueOf(splits[1]));
    }

    Collections.sort(left);
    Collections.sort(right);

    long sum = 0;
    for (int i = 0; i < left.size(); i++) {
        var l = left.get(i);
        var r = right.get(i);
        sum += Math.abs(l - r);
    }

    System.out.println(sum);
}
