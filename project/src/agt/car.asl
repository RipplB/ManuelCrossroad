//pos(1, 2, 0).   The current position (side, lane, distance)
                //where side is (0 = top, 1 = right, 2 = bottom, 3 = left)
                //lane is (0 = left, 1 = middle, 2 = right)
                //and distance is measured from the start
//target(3).  //where this car wants to go. (side) where side is the same as above
//xdistance(10).     where the crossroad is. If distance reaches it, this agent has no more things to think about.

!proceed.


desired_lane(TargetLane)
:- pos(CurrentSide, _, _) & target(Target) & ((CurrentSide + Target) mod 2 == 0) & (TargetLane = 1).

desired_lane(TargetLane)
:- pos(0, Lane, _) & target(3) & (TargetLane = 2).

desired_lane(TargetLane)
:- pos(CurrentSide, _, _) & target(Target) & ((CurrentSide + Target) mod 2 \== 0) & (Target > CurrentSide) & (TargetLane = 0).

desired_lane(TargetLane)
:- pos(3, _, _) & target(0) & (TargetLane = 0).

desired_lane(TargetLane)
:- pos(CurrentSide, _, _) & target(Target) & ((CurrentSide + Target) mod 2 \== 0) & (Target < CurrentSide) & (TargetLane = 2).

next_lane(TargetLane)
:- desired_lane(DesiredLane) & pos(_, Lane, _) & DesiredLane \== Lane & ((DesiredLane + Lane) mod 2 == 0) & TargetLane = 1.

next_lane(TargetLane)
:- desired_lane(TargetLane) & pos(_, Lane, _) & (TargetLane == Lane | ((TargetLane + Lane) mod 2 == 1)).

// +!init:
//         .random(SideSeed) & Side = ((30 * SideSeed) div 10) &
//         .random(LaneSeed) & Lane = ((20 * LaneSeed) div 10) &
//         .random(TargetSeed) & Target = ((30 * TargetSeed) div 10) &
//         Target \== Side
//     <-  -+pos(Side, Lane, 0);
//         -+target(Target);
//         !test.

// +!init: true <- !init.

+!test : pos(Side, Lane, Dist) & desired_lane(NewLane) & target(Target)
    <-  .print("Reeval pos from (", Side, ", ", Lane, ", ", Dist, ") when target is ", Target);
        -+pos(Side, NewLane, Dist);
        move(Side, NewLane, Dist);
        .print("New lane: ", NewLane);
        createCar(pls).

+!proceed : pos(_, Lane, Dist) & next_lane(Lane) & xdistance(Dist)
    <-  !finish.

+!proceed : pos(Side, Lane, Dist) & next_lane(DesiredLane) & Lane \== DesiredLane & not car(Side, DesiredLane, Dist)
    <-  .print("Reeval pos from (", Side, ", ", Lane, ", ", Dist, ")");
        move(Side, DesiredLane, Dist);
        .print("New lane: ", DesiredLane);
        -+pos(Side, DesiredLane, Dist);
        !proceed.

+!proceed : pos(Side, Lane, Dist) & NextDist = Dist + 1 & not car(Side, Lane, NextDist)
    <-  .print("Moving forwards from (", Side, ", ", Lane, ", ", Dist, ") to ", NextDist);
        move(Side, Lane, NextDist);
        .print("New pos (", Side, ", ", Lane, ", ", NextDist, ")");
        -+pos(Side, Lane, NextDist);
        !proceed.

+!proceed : true <- .print("Can't move"); sleep; !proceed.
-!proceed : true <- .print("Can't move"); sleep; !proceed.

+!finish : pos(Side, _, _) & target(Target)
    <-  finish(Side, Target); !finish.