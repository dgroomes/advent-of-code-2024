# ABANDON. Forget about diagonal...

def main [--example] {
    let variant = if $example { "example" } else { "full" }
    let file = $"input/day-04-($variant).txt"
    let word_search = open $file | lines

    # Left to right
    # Right to left
    # Up down
    # Down up
    # Diagonal down left to right
    # Diagonal up left to right
    # Diagonal down right to left
    # Diagonal up right to left

    $word_search | each { xmas-occurrences } | math sum | print
}

def xmas-occurrences [] {
    let str = $in
    let len = $str | str length
    mut count = 0
    for i in 0..<($len) {
        let partial = $str | str substring $i..-1
        if ($partial | str starts-with XMAS) {
            $count = $count + 1
        }
    }
    return $count
}
