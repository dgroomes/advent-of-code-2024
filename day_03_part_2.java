import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;

void main() throws IOException {
    var memory = memory();
    var matches = new ArrayList<Match>();

    // Find all matches across 'mul', 'do' and "don't" instructions and then sort it.
    {
        var matcher = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)").matcher(memory);
        while (matcher.find()) {
            var op1 = Integer.valueOf(matcher.group(1));
            var op2 = Integer.valueOf(matcher.group(2));
            var product = op1 * op2;
            matches.add(new Match.Mul(matcher.start(), product));
        }
    }

    {
        var matcher = Pattern.compile("do\\(\\)").matcher(memory);
        while (matcher.find()) {
            matches.add(new Match.Do(matcher.start()));
        }
    }

    {
        var matcher = Pattern.compile("don't\\(\\)").matcher(memory);
        while (matcher.find()) {
            matches.add(new Match.Dont(matcher.start()));
        }
    }

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

sealed interface Match {
    int idx();

    record Mul(int idx, int product) implements Match {}

    record Do(int idx) implements Match {}

    record Dont(int idx) implements Match {}
}


