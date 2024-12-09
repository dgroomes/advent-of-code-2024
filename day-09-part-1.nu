def main [--example] {
    let variant = if $example { "example" } else { "full" }
    let file = $"input/day-09-($variant).txt"

    # file/free length pairs
    let ff = open $file | str trim | split chars | each { into int } | append 0 | chunks 2

    let disk = $ff | enumerate | each { |it|
        let id = $it.index
        let file_length = $it.item.0
        let free_length = $it.item.1
        let file_blocks = 0..<$file_length | each { $id }
        let free_blocks = 0..<$free_length | each { "." }
        [ ...$file_blocks ...$free_blocks ]
    } | flatten

    let ddisk = $disk | defrag
    $ddisk | check-sum
}

def defrag [] {
    mut disk = $in
    mut i = 0
    mut j = ($disk | length) - 1

    loop {
        if $i >= $j { break }

        let l = $disk | get $i
        let r = $disk | get $j

        if $r == "." {
            $j = $j - 1
            continue
        }

        if $l != "." {
            $i = $i + 1
            continue
        }

        $disk = $disk | update $i $r
        $disk = $disk | update $j "."
        $j = $j - 1
        $i = $i + 1
    }

    return $disk
}

def check-sum [] {
    where $it != "." | enumerate | reduce --fold 0 { |it, acc| ($it.index * $it.item) + $acc }
}
