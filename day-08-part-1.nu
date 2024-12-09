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

                let anti_n = anti-node $central $neighbor
                if (($anti_n.x in $city_bounds) and ($anti_n.y in $city_bounds)) {
                    return { freq: $freq x: $anti_n.x y: $anti_n.y }
                }
            }
        } | flatten
    } | flatten | where (is-not-empty)

    $anti_nodes | reject freq | uniq | length
}

def anti-node [a b] {
    let x = $a.x * 2 - $b.x
    let y = $a.y * 2 - $b.y
    { x: $x y: $y }
}
