!start.

+!start: true <- start; !run.

+!run: true &
        .random(SideSeed) & Side = ((40 * SideSeed) div 10) &
        .random(LaneSeed) & Lane = ((30 * LaneSeed) div 10)
    <-  lights(Side, Lane, green); !run.