lightOfLane(Side, Lane, Light)
:- NumberOfLight = 3 * Side + 2 - Lane + 1 & .concat("light", NumberOfLight, Light).

!start.

+!start : pos(Side, Lane, Dist) & lightOfLane(Side, Lane, Light) & .my_name(MyName)
    <-  .print("DID SOMEONE CALL AMBULANCE?????");
        .send(Light, tell, value(MyName, 4000));
        !proceed.


+!proceed : pos(_, Lane, Dist) & xdistance(Dist)
    <-  !entry_xroad.

+!proceed : pos(Side, Lane, Dist) & xdistance(MaxDist) & Dist < MaxDist & NextDist = Dist + 1
    & not car(Side, Lane, NextDist, _) & ((green(Side, Lane) | NextDist < MaxDist) | (NextDist < MaxDist - 1))
    <-  move(Side, Lane, NextDist);
        -+pos(Side, Lane, NextDist);
        !proceed.

+!proceed : true <- .print("Can't move"); sleep; !proceed.
-!proceed : true <- .print("Can't move"); sleep; !proceed.

+!entry_xroad : pos(Side, Lane, _) & lightOfLane(Side, Lane, Light)
    <-  .my_name(MyName);
        .send(Light, untell, value(MyName, 4000));
        !finish.

+!finish : pos(Side, _, _)
    <-  finish(Side); !finish.