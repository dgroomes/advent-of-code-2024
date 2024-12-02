def main [--example] {
    let variant = if $example { "example" } else { "full" }
    let file = $"input/day-1-($variant).txt"
    let input = open $file | str replace --all --regex "[ ]+ " (char tab) | from tsv --noheaders

    let left = $input | get column0 | sort | wrap left
    let right = $input | get column1 | sort | wrap right

    ($left | merge $right) | each { |it| $it.left - $it.right | math abs } | math sum
}
