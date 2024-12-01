def main [--example] {
    let input = if $example { "input/day-1-example.txt" } else { "input/day-1-full.txt" }

    let table = open $input | str replace --all --regex "[ ]+ " (char tab) | from tsv --noheaders

    let left = $table | get column0 | sort
    let right_counts = $table | get column1 | group-by --to-table | insert count { |row| $row.items | length } | reduce --fold {} { |row, acc| $acc | insert $row.group $row.count }

    $left | each { |it| let count = $right_counts | get --ignore-errors ($it | into string) | default 0; $count * $it } | math sum
}
