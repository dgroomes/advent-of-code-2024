def main [--example] {
    let variant = if $example { "example" } else { "full" }
    let file = $"input/day-02-($variant).txt"
    let reports = open $file | lines | each { split row ' ' | each { into int } }

    $reports | where (is-safe) | length
}

def is-safe [] {
    mut report = $in

    # Normalize a seemingly descending report into an ascending report
    if ($report | first) > ($report | last) {
        $report = $report | reverse
    }

    mut i = 0
    let len = $report | length
    loop {
        if $i == ($len - 1) { return true }

        let a = $report | get $i
        let b = $report | get ($i + 1)

        let diff = $b - $a
        if $diff <= 0 or $diff > 3 { return false }

        $i = $i + 1
    }
}
