import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;

sealed interface Match {
    int idx();

    record Mul(int idx, int product) implements Match {}

    record Do(int idx) implements Match {}

    record Dont(int idx) implements Match {}
}

void main() throws IOException {
    var memory = memory();
    var matches = new ArrayList<Match>();

    // Find all matches across 'mul', 'do' and "don't" instructions and then sort it.
    var mulMatcher = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)").matcher(memory);
    while (mulMatcher.find()) {
        var op1 = Integer.valueOf(mulMatcher.group(1));
        var op2 = Integer.valueOf(mulMatcher.group(2));
        var product = op1 * op2;
        matches.add(new Match.Mul(mulMatcher.start(), product));
    }

    var doMatcher = Pattern.compile("do\\(\\)").matcher(memory);
    while (doMatcher.find()) matches.add(new Match.Do(doMatcher.start()));

    var dontMatcher = Pattern.compile("don't\\(\\)").matcher(memory);
    while (dontMatcher.find()) matches.add(new Match.Dont(dontMatcher.start()));

    matches.sort(Comparator.comparing(Match::idx));

    long sum = 0;
    boolean enabled = true;
    for (var match : matches) {
        switch (match) {
            case Match.Do _ -> enabled = true;
            case Match.Dont _ -> enabled = false;
            case Match.Mul(_, int product) -> {
                if (enabled) sum += product;
            }
        }
    }

    System.out.println(sum);
}

private static String memory() throws IOException {
    String path;
    if (System.getenv().containsKey("EXAMPLE")) {
        path = "input/day-03-part-2-example.txt";
    } else {
        path = "input/day-03-full.txt";
    }

    return Files.readString(Path.of(path));
}



