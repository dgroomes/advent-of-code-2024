import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

void main() throws IOException {
    String path;
    if (System.getenv().containsKey("EXAMPLE")) {
        path = "input/day-03-example.txt";
    } else {
        path = "input/day-03-full.txt";
    }

    var content = Files.readString(Path.of(path));

    var pattern = Pattern.compile("""
            mul
            
            \\(
            
            (\\d{1,3}) # Capture the first operand: any number between 1 and 3 digits. The parentheses indicate a capturing group.
            ,
            (\\d{1,3}) 
            
            \\)
            """, Pattern.COMMENTS);
    var matcher = pattern.matcher(content);

    long sum = 0;
    while (matcher.find()) {
        var op1 = Long.valueOf(matcher.group(1));
        var op2 = Long.valueOf(matcher.group(2));
        sum += op1 * op2;
    }
    System.out.printf("%d%n", sum);
}

