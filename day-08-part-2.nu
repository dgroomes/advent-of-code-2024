def main [--example] {
    let variant = if $example { "example" } else { "full" }
    let file = $"input/day-08-($variant).txt"
    let city = open $file | lines | each { split chars }

    let city_bounds = 0..<($city | length)

    let antennas = $city | enumerate | each { |row|
        $row.item | enumerate | each { |col|
            { freq: $col.item x: $col.index y: $row.index }
        }
    } | flatten | where freq != '.'

    let antennas_by_freq = $antennas | group-by --to-table freq | rename freq coords | update coords { |row| $row.coords | select x y }

    # Circuitous to get here, but with 'antennas_by_freq' we finally we have a table of frequencies to the
    # coordinates of antennas tuned to that frequency.
    # ╭───┬──────┬───────────────╮
    # │ # │ freq │    coords     │
    # ├───┼──────┼───────────────┤
    # │ 0 │ 0    │ ╭───┬───┬───╮ │
    # │   │      │ │ # │ x │ y │ │
    # │   │      │ ├───┼───┼───┤ │
    # │   │      │ │ 0 │ 8 │ 1 │ │
    # │   │      │ │ 1 │ 5 │ 2 │ │
    # │   │      │ │ 2 │ 7 │ 3 │ │
    # │   │      │ │ 3 │ 4 │ 4 │ │
    # │   │      │ ╰───┴───┴───╯ │
    # │ 1 │ A    │ ╭───┬───┬───╮ │
    # │   │      │ │ # │ x │ y │ │
    # │   │      │ ├───┼───┼───┤ │
    # │   │      │ │ 0 │ 6 │ 5 │ │
    # │   │      │ │ 1 │ 8 │ 8 │ │
    # │   │      │ │ 2 │ 9 │ 9 │ │
    # │   │      │ ╰───┴───┴───╯ │
    # ╰───┴──────┴───────────────╯

    let anti_nodes = $antennas_by_freq | each { |freq_set|
        let freq = $freq_set.freq
        let coords = $freq_set.coords

        $coords | each { |central|
            $coords | each { |neighbor|
                if $neighbor == $central { return }

                mut anti_ns = [$central $neighbor]

                mut a = $central
                mut b = $neighbor
                loop {
                    let c = anti-node $a $b
                    if (in-bounds $city_bounds $c | not $in) { break }

                    $anti_ns = $anti_ns | append $c
                    $a = $b
                    $b = $c
                }

                $anti_ns
            } | flatten
        } | flatten
    } | flatten

    $anti_nodes | sort-by y x | uniq | length
}

# Given two nodes (a, b) compute the next anti-node (c) that follows the line a -> b -> c.
#
# For example:
#     anti-node { x: 8 y: 8 } { x: 9 y: 9 }
# Returns:
#     { x: 10 y: 10 }
#
def anti-node [a b] {
    let x = $b.x * 2 - $a.x
    let y = $b.y * 2 - $a.y
    { x: $x y: $y }
}

def in-bounds [bounds coord] {
    (($coord.x in $bounds) and ($coord.y in $bounds))
}
