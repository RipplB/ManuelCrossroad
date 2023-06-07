
lightOfLane(Side, Lane, Light)
:- NumberOfLight = 3 * Side + 2 - Lane + 1 & .concat("light", NumberOfLight, Light).

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
        .nth(11, SortedValues, lightValue(Val, Side, Lane));
        .broadcast(untell, halt);
        lights(Side, Lane, green);
        ?lightOfLane(Side, Lane, Light);
        .send(Light, tell, green);
        .wait(2000);
        !halt.

+!halt: true
    <-  lights(0, 0, red);
        .broadcast(tell, halt);
        .wait(2000);
        !run.