import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

void main() throws IOException {
    var reports = readReports();

    int safe = 0;
    outer: for (var report : reports) {
        var trend = 0;
        for (int i = 0; i < report.size() - 1; i++) {
            var diff = report.get(i + 1) - report.get(i);
            if ((diff < 0 && trend > 0) || (diff > 0 && trend < 0)) continue outer;
            trend = diff;
            if (0 == diff || Math.abs(diff) > 3) continue outer;
        }
        safe++;
    }

    System.out.println(safe);
}

private static List<List<Integer>> readReports() throws IOException {
    String path;
    if (System.getenv().containsKey("EXAMPLE")) {
        path = "input/day-02-example.txt";
    } else {
        path = "input/day-02-full.txt";
    }

    var reports = new ArrayList<List<Integer>>();
    for (var line : Files.readAllLines(Path.of(path))) {
        var report = new ArrayList<Integer>();
        var splits = line.split(" +");
        for (var split : splits) report.add(Integer.valueOf(split));
        reports.add(report);
    }

    return reports;
}
