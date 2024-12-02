def main [--example] {
    let variant = if $example { "example-safe" } else { "full" }
    let file = $"input/day-02-($variant).txt"
    let reports = open $file | lines | each { split row ' ' | each { into int } }

    $reports | where (is-safe) | length
}

def is-safe [] {
    let report = $in
    mut i = 0
    mut dampener_available = true
    mut unsafe_just_detected =
    let len = $asc_report | length
    loop {
        if $i == ($len - 1) { return (-1) }

        let a = $asc_report | get $i
        let b = $asc_report | get ($i + 1)

        let diff = $b - $a
        if $diff <= 0 or $diff > 3 { return ($i + 1) }
        $i = $i + 1
    }

    return (-1)
}
