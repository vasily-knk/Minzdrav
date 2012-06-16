package ru.spbau.kononenko.drunkgame;

import ru.spbau.kudinkin.simulator.Simulator;
import ru.spbau.kudinkin.simulator.characters.Personage;
import ru.spbau.kudinkin.simulator.characters.instances.Beggar;
import ru.spbau.kudinkin.simulator.characters.instances.Lantern;
import ru.spbau.kudinkin.simulator.characters.instances.Pillar;
import ru.spbau.kudinkin.simulator.characters.instances.Policeman;
import ru.spbau.kudinkin.simulator.exceptions.StopSimulationException;
import ru.spbau.kudinkin.simulator.exceptions.TileReferenceException;
import ru.spbau.kudinkin.simulator.fields.Field;
import ru.spbau.kudinkin.simulator.fields.instances.Square;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MyMain {

    public static void main(String[] args) {

        List<Integer> inspectorCoord = Arrays.asList(13, 14);
        Field square = new Square(15, 15);
        //Field hex = new Hex(8);

        Pillar pillar = new Pillar();
        Lantern lantern = new Lantern();
        Inspector inspector = new Inspector(inspectorCoord, 2);
        MyBeggar beggar = new MyBeggar();
        Policeman policeman = new Policeman();

        HashMap<Personage, List<Integer>> startingGrid = new HashMap<>();

        startingGrid.put(pillar, Arrays.asList(7, 7));
        startingGrid.put(lantern, Arrays.asList(10, 2));
        startingGrid.put(inspector, inspectorCoord);
        startingGrid.put(beggar, Arrays.asList(5, 0));
        startingGrid.put(policeman, Arrays.asList(14, 4));

        try {

            Simulator simulator = new Simulator(square,
                                                Arrays.asList((Personage) pillar, lantern, inspector, beggar, policeman),
                                                startingGrid,
                                                1000);

            simulator.simulate();

        } catch (TileReferenceException e) {

            System.err.println(e.getMessage());

        } catch (StopSimulationException re) {

            // SALUTATE ENDING OF THE SIMULATION PROCEDURE

        } finally {
            System.out.println("Beggar's money: " + beggar.getMoney());

        }
    }

}