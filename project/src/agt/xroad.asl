
lightOfLane(Side, Lane, Light)
:- NumberOfLight = 3 * Side + 2 - Lane + 1 & .concat("light", NumberOfLight, Light).

blocked_by(_, 2, []).
blocked_by(0, 1, [lane(1, 1), lane(2, 0), lane(3, 1), lane(3, 0)]).
blocked_by(1, 1, [lane(2, 1), lane(3, 0), lane(0, 1), lane(0, 0)]).
blocked_by(2, 1, [lane(3, 1), lane(0, 0), lane(1, 1), lane(1, 0)]).
blocked_by(3, 1, [lane(0, 1), lane(1, 0), lane(2, 1), lane(2, 0)]).
blocked_by(0, 0, [lane(1, 1), lane(1, 0), lane(2, 0), lane(2, 1), lane(3, 0)]).
blocked_by(1, 0, [lane(2, 1), lane(2, 0), lane(3, 0), lane(3, 1), lane(0, 0)]).
blocked_by(2, 0, [lane(3, 1), lane(3, 0), lane(0, 0), lane(0, 1), lane(1, 0)]).
blocked_by(3, 0, [lane(0, 1), lane(0, 0), lane(1, 0), lane(1, 1), lane(2, 0)]).

!start.

+!start: true <- start; !run.

+!run: true
    <-  .send(light1, askOne, lightValue(_, _, _), Light1Value);
        .send(light2, askOne, lightValue(_, _, _), Light2Value);
        .send(light3, askOne, lightValue(_, _, _), Light3Value);
        .send(light4, askOne, lightValue(_, _, _), Light4Value);
        .send(light5, askOne, lightValue(_, _, _), Light5Value);
        .send(light6, askOne, lightValue(_, _, _), Light6Value);
        .send(light7, askOne, lightValue(_, _, _), Light7Value);
        .send(light8, askOne, lightValue(_, _, _), Light8Value);
        .send(light9, askOne, lightValue(_, _, _), Light9Value);
        .send(light10, askOne, lightValue(_, _, _), Light10Value);
        .send(light11, askOne, lightValue(_, _, _), Light11Value);
        .send(light12, askOne, lightValue(_, _, _), Light12Value);
        .sort([Light1Value, Light2Value, Light3Value, Light4Value, Light5Value, Light6Value, Light7Value, Light8Value, Light9Value, Light10Value, Light11Value, Light12Value], SortedValues);
        .broadcast(untell, halt);
        !turn_valid_green(SortedValues, 11, []);
        // .nth(11, SortedValues, lightValue(Val, Side, Lane));
        // lights(Side, Lane, green);
        // ?lightOfLane(Side, Lane, Light);
        // .send(Light, tell, green);
        .wait(2000);
        !halt.

        
+!turn_valid_green(_, -1, _) : true
    <-  true.

+!turn_valid_green(WinnerList, Index, _) : Index \== -1 & .nth(Index, WinnerList, lightValue(0, _, _))
    <-  true.

+!turn_valid_green(WinnerList, Index, Blocklist) : .nth(Index, WinnerList, lightValue(_, Side, Lane)) & .member(lane(Side, Lane), Blocklist)
    <-  NextIndex = Index - 1;
        !turn_valid_green(WinnerList, NextIndex, Blocklist).

+!turn_valid_green(WinnerList, Index, Blocklist) : .nth(Index, WinnerList, lightValue(Val, Side, Lane)) & not .member(lane(Side, Lane), Blocklist) & Val > 0
    <-  lights(Side, Lane, green);
        ?lightOfLane(Side, Lane, Light);
        .send(Light, tell, green);

        NextIndex = Index - 1;
        ?blocked_by(Side, Lane, BlocklistExtension);
        .concat(Blocklist, BlocklistExtension, ExtendedBlocklist);
        !turn_valid_green(WinnerList, NextIndex, ExtendedBlocklist).

+!halt: true
    <-  lights(0, 0, red);
        .broadcast(tell, halt);
        .wait(2000);
        !run.