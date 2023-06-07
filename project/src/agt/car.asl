//pos(1, 2, 0).   The current position (side, lane, distance)
                //where side is (0 = top, 1 = right, 2 = bottom, 3 = left)
                //lane is (0 = left, 1 = middle, 2 = right)
                //and distance is measured from the start
//target(3).  //where this car wants to go. (side) where side is the same as above
//xdistance(10).     where the crossroad is. If distance reaches it, this agent has no more things to think about.
value(0).

lightOfLane(Side, Lane, Light)
:- NumberOfLight = 3 * Side + 2 - Lane + 1 & .concat("light", NumberOfLight, Light).

!proceed.


desired_lane(TargetLane)
:- pos(CurrentSide, _, _) & target(Target) & ((CurrentSide + Target) mod 2 == 0) & (TargetLane = 1).

desired_lane(TargetLane)
:- pos(0, _, _) & target(1) & (TargetLane = 0).
desired_lane(TargetLane)
:- pos(0, _, _) & target(3) & (TargetLane = 2).

desired_lane(TargetLane)
:- pos(1, _, _) & target(2) & (TargetLane = 0).
desired_lane(TargetLane)
:- pos(1, _, _) & target(0) & (TargetLane = 2).

desired_lane(TargetLane)
:- pos(2, _, _) & target(3) & (TargetLane = 0).
desired_lane(TargetLane)
:- pos(2, _, _) & target(1) & (TargetLane = 2).

desired_lane(TargetLane)
:- pos(3, _, _) & target(0) & (TargetLane = 0).
desired_lane(TargetLane)
:- pos(3, _, _) & target(2) & (TargetLane = 2).

next_lane(TargetLane)
:- desired_lane(DesiredLane) & pos(_, Lane, _) & DesiredLane \== Lane & ((DesiredLane + Lane) mod 2 == 0) & TargetLane = 1.

next_lane(TargetLane)
:- desired_lane(TargetLane) & pos(_, Lane, _) & (TargetLane == Lane | ((TargetLane + Lane) mod 2 == 1)).

+!proceed : pos(_, Lane, Dist) & next_lane(Lane) & xdistance(Dist)
    <-  !entry_xroad.

+!proceed : pos(Side, Lane, Dist) & next_lane(DesiredLane) & Lane \== DesiredLane & not car(Side, DesiredLane, Dist, _)
    <-  .print("Reeval pos from (", Side, ", ", Lane, ", ", Dist, ")");
        move(Side, DesiredLane, Dist);
        .print("New lane: ", DesiredLane);
        -+pos(Side, DesiredLane, Dist);
        !proceed.

+!proceed : pos(Side, Lane, Dist) & xdistance(MaxDist) & Dist < MaxDist & NextDist = Dist + 1 & next_lane(DesiredLane)
    & not car(Side, Lane, NextDist, _) & (((green(Side, Lane) | NextDist < MaxDist) & DesiredLane == Lane) | (NextDist < MaxDist - 1))
    <-  .print("Moving forwards from (", Side, ", ", Lane, ", ", Dist, ") to ", NextDist);
        move(Side, Lane, NextDist);
        .print("New pos (", Side, ", ", Lane, ", ", NextDist, ")");
        -+pos(Side, Lane, NextDist);
        !proceed.

+!proceed : pos(Side, Lane, Dist) & NextDist = Dist + 1 & (car(Side, Lane, NextDist, _) | not green(Side, Lane))
    & next_lane(DesiredLane) & Lane == DesiredLane & value(CurrentValue) & Value = CurrentValue + 1 & lightOfLane(Side, Lane, Light)
    <-  sleep;
        .print("Waiting for greenlight");
        .my_name(MyName);
        .send(Light, untell, value(MyName, CurrentValue));
        .send(Light, tell, value(MyName, Value));
        -+value(Value);
        !proceed.

+!proceed : true <- .print("Can't move"); sleep; !proceed.
-!proceed : true <- .print("Can't move"); sleep; !proceed.

+!entry_xroad : pos(Side, Lane, _) & lightOfLane(Side, Lane, Light)
    <-  .my_name(MyName);
        .send(Light, tell, leave(MyName));
        !finish.

+!finish : pos(Side, _, _) & target(Target)
    <-  finish(Side, Target); !finish.