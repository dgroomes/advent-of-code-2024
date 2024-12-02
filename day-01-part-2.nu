def main [--example] {
    let variant = if $example { "example" } else { "full" }
    let file = $"input/day-1-($variant).txt"
    let input = open $file | str replace --all --regex "[ ]+ " (char tab) | from tsv --noheaders

    let left = $input | get column0
    let right_counts = $input | get column1 | group-by --to-table | insert count { |row| $row.items | length } | reduce --fold {} { |row, acc| $acc | insert $row.group $row.count }

    $left | each { |l|
        let count = $right_counts | get --ignore-errors ($l | into string) | default 0;
        $count * $l
    } | math sum
}
