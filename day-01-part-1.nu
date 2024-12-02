def main [--example] {
    let input = if $example { "input/day-1-example.txt" } else { "input/day-1-full.txt" }

    let table = open $input | str replace --all --regex "[ ]+ " (char tab) | from tsv --noheaders

    let left = $table | get column0 | sort | wrap left
    let right = $table | get column1 | sort | wrap right

    ($left | merge $right) | each { |it| $it.left - $it.right | math abs } | math sum
}
