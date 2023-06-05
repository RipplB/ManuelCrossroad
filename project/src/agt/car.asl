//pos(1, 2, 0).   The current position (side, lane, distance)
                //where side is (0 = top, 1 = right, 2 = bottom, 3 = left)
                //lane is (0 = left, 1 = middle, 2 = right)
                //and distance is measured from the start
//target(3).  //where this car wants to go. (side) where side is the same as above

!init.


desired_lane(TargetLane)
:- pos(CurrentSide, _, _) & target(Target) & ((CurrentSide + Target) mod 2 == 0) & (TargetLane = 1).

desired_lane(TargetLane)
:- pos(0, _, _) & target(3) & (TargetLane = 2).

desired_lane(TargetLane)
:- pos(CurrentSide, _, _) & target(Target) & (Target > CurrentSide) & (TargetLane = 0).

desired_lane(TargetLane)
:- pos(3, _, _) & target(0) & (TargetLane = 0).

desired_lane(TargetLane)
:- (TargetLane = 2).

+!init:
        .random(SideSeed) & Side = ((30 * SideSeed) div 10) &
        .random(LaneSeed) & Lane = ((20 * LaneSeed) div 10) &
        .random(TargetSeed) & Target = ((30 * TargetSeed) div 10) &
        Target \== Side
    <-  -+pos(Side, Lane, 0);
        -+target(Target);
        !test.

+!init: true <- !init.

+!test : pos(Side, Lane, Dist) & desired_lane(NewLane) & target(Target)
    <-  .print("Reeval pos from (", Side, ", ", Lane, ", ", Dist, ") when target is ", Target);
        -+pos(Side, NewLane, Dist);
        .print("New lane: ", NewLane);
        createCar(pls).