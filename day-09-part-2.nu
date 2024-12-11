# NOT FULLY IMPLEMENTED. This got too hard. Partly a skill issue on my part (how to wrangle immutable data without
# creating a mess of variables/names) and partly because I think I insisted on the wrong algorithm. I segmented the data
# but I probably should have just kept it as a list of blocks, like the problem statement is described and how I
# implemented part 1.
def main [--example] {
    let variant = if $example { "example" } else { "full" }
    let file = $"input/day-09-($variant).txt"

    # file/blank length pairs
    let fb = open $file | str trim | split chars | each { into int } | append 0 | chunks 2

    let disk = $fb | enumerate | reduce --fold { pos: 0 segments: [] } { |it, acc|
        let pos = $acc.pos
        mut segments = $acc.segments

        let file = {
            pos: $pos
            id: $it.index
            length: $it.item.0
        }

        let blank = {
            pos: ($file.pos + $file.length)
            id: "."
            length: $it.item.1
        }

        $segments = $segments | append $file
        if $blank.length != 0 {
            $segments = $segments | append $blank
        }

        let n_pos = $blank.pos + $blank.length

        { pos: $n_pos segments: $segments }
    } | get segments

    $disk | defrag
}

# Algorithm:
#
#   - March forward from the left side step-by-step
#   - The idea is to pull the next eligible file-block segment, and incrementally compute the hash and march forward
#   - The trick: keep track of "block-segment lists by length". E.g. ten-length block-segments are in their own list
#   - Once all empty block segments have been used up, incrementally compute the has of block segments left over in the lists
#   - A key performance optimization insight is that there are only 9 different block segment lengths (1-9).
#   - ... This has turned out poorly. The algorithm didn't quite work like I wanted (I need to mutate the and I thought
#     I could get away with just "marching forward and incrementally incrementing the checksum) and the immutable nature
#     of Nushell is really making things difficult. I'm surprised there isn't an "update <row critera> <update fn>" for
#     tables and tables are such a big thing in Nushell.
def defrag [] {
    mut disk = $in
    print $disk

    mut segments_by_len = $disk
        | enumerate
        | each { |it| { idx: $it.index pos: $it.item.pos id: $it.item.id length: $it.item.length } }
        | reverse
        | where $it.id != "."
        | group-by { |it| $it.length }

    mut i = 0
    let end = $disk | length
    mut check_sum = 0
    mut safety = 0

    loop {
        print $"Loop i=($i)"
        if $safety > 100 { print "SAFETY VALVE!"; return $segments_by_len }
        $safety = $safety + 1

        if $i >= $end { print "\tEnd"; break }

        let l = $disk | get $i

        print $"\tl segment: ($l)"

        if $l.id != "." {
            print $"\tNon-blank block encountered. Incrementing checksum."
            $i = $i + 1
            let pos_n = $l.pos + $l.length
            let id = $l.id | into int
            let inc_check_sum = check-sum $l.pos $l.length ($l.id | into int)
            $check_sum = $check_sum + $inc_check_sum
            print $"\tIncremented checksum to ($check_sum)"
            continue
        }

        mut blank_blocks = $l.length
        mut pos = $l.pos
        loop {
            print $"\tLoop. blank_blocks=\(($blank_blocks)\)"
            if $safety > 100 { print "\t\tSAFETY VALVE!"; return $segments_by_len }
            $safety = $safety + 1

            # This is long in the tooth. We need to find eligible segment lists (length) and then find the segment list
            # containing the segment farthest to the right.
            let rows = $segments_by_len | transpose length segments
                | update length { |it| $it.length | into int }
                | where $it.length <= $blank_blocks
                | sort-by --reverse { |it| $it.segments | first | get pos }

            if ($rows | is-empty) {
                print $"\tBlank space is too small \(($blank_blocks)\) for any segments."
                break
            }

            let row = $rows | first
            let len = $row.length
            let seg_to_place = $row.segments | first
            print $"\tFound segment to place ($seg_to_place)"
            let inc_check_sum = check-sum $pos $seg_to_place.length ($seg_to_place.id | into int)
            $pos = $pos + $seg_to_place.length
            $disk = $disk | update $seg_to_place.idx { id: '.' pos: $seg_to_place.pos length: $seg_to_place.length }
            print $"\tBlanked out segment. Here is the disk now."
            print $disk
            $check_sum = $check_sum + $inc_check_sum
            print $"\tIncremented checksum to ($check_sum)"

            let key = $len | into string
            let segments_n = $row.segments | skip 1
            if ($segments_n | is-empty) {
                print $"\tSegments are empty for length ($len). Removing this entry."
                $segments_by_len = $segments_by_len | reject $key
            } else {
                $segments_by_len = $segments_by_len | update $key $segments_n
            }

            $blank_blocks = $blank_blocks - $len
        }

        $i = $i + 1
    }

    $check_sum
}

def check-sum [start length id] {
    let end = $start + $length
    $start..<$end | reduce --fold 0 { |pos, sum| $pos * $id + $sum }
}
