def main [--example] {
    let variant = if $example { "example" } else { "full" }
    let file = $"input/day-02-($variant).txt"
    let reports = open $file | lines | each { split row ' ' | each { into int } }

    $reports | where (is-safe-check-six-ways) | length
}

# Pretty naive, but we're going to check the report for safety "6-ways": a combination of reversing and dropping both
# sides of the first encountered "problem level"
def is-safe-check-six-ways [] {
    if ($in | is-safe-check-three-ways) { return true }
    return ($in | reverse | is-safe-check-three-ways)
}

def is-safe-check-three-ways [] {
  let report = $in
  let lvl = $report | problem-level
  if ($lvl == -1) { return true }

  if ($report | drop nth $lvl | problem-level) == -1 { return true }

  return (($report | drop nth ($lvl + 1) | problem-level) == -1)
}

def problem-level [] {
    let report = $in
    mut i = 0
    let len = $report | length
    loop {
        if $i == ($len - 1) { return (-1) }

        let a = $report | get $i
        let b = $report | get ($i + 1)

        let diff = $b - $a
        if $diff <= 0 or $diff > 3 { return $i }
        $i = $i + 1
    }

    return (-1)
}
