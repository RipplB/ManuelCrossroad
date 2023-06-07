greenBonus(0).

laneOfLight(light1, 0, 2).
laneOfLight(light2, 0, 1).
laneOfLight(light3, 0, 0).
laneOfLight(light4, 1, 2).
laneOfLight(light5, 1, 1).
laneOfLight(light6, 1, 0).
laneOfLight(light7, 2, 2).
laneOfLight(light8, 2, 1).
laneOfLight(light9, 2, 0).
laneOfLight(light10, 3, 2).
laneOfLight(light11, 3, 1).
laneOfLight(light12, 3, 0).

summarize_list([], 0).

summarize_list(List, Sum)
:- .length(List, Length) & summarize_list(List, 0, Sum).

summarize_list(List, CurrentIndex, IncreasedAcc)
:- .length(List, Length) & CurrentIndex < Length & NextIndex = CurrentIndex + 1 & summarize_list(List, NextIndex, Accumulator)
    & .nth(CurrentIndex, List, CurrentValue) & IncreasedAcc = Accumulator + CurrentValue.
    

summarize_list(List, Index, 0)
:- .length(List, Index).

+leave(Car) : greenBonus(GreenBonus) & ReducedGreenBonus = GreenBonus - 3
    <-  .abolish(value(Car, _));
        .abolish(leave(Car));
        .max([ReducedGreenBonus, 0], ResultedGreenBonus);
        -+greenBonus(ResultedGreenBonus).

+green : true
    <-  -+greenBonus(9);
        .abolish(green);
        .my_name(MyName);
        ?laneOfLight(MyName, Side, Lane);
        .broadcast(untell, red(Side, Lane));
        .broadcast(tell, green(Side, Lane)).

+halt : true
    <-  .abolish(halt);
        .my_name(MyName);
        ?laneOfLight(MyName, Side, Lane);
        .broadcast(tell, red(Side, Lane));
        .broadcast(untell, green(Side, Lane)).


+?lightValue(TotalValue, Side, Lane) : greenBonus(GreenBonus)
    <-  .findall(Val, value(_, Val), Values);
        ?summarize_list(Values, CarValues);
        TotalValue = CarValues + GreenBonus;
        .my_name(MyName);
        ?laneOfLight(MyName, Side, Lane).
