package ru.spbau.kononenko.drunkgame;

import ru.spbau.kudinkin.simulator.characters.instances.Tippler;
import ru.spbau.kudinkin.simulator.descriptors.Move;
import ru.spbau.kudinkin.simulator.exceptions.StopSimulationException;
import ru.spbau.kudinkin.simulator.services.Seed;
import ru.spbau.kudinkin.simulator.characters.Personage;

import java.util.*;


public class Inspector extends Personage {
    private Set<Tippler> photographed = new HashSet<>();
    private Photographer photographer = new Photographer();
    private boolean goingBack = false;

    private List<Integer> dockPoint;
    private int radius;
    
    private int moveCoord;
    private boolean moveDir;
    private boolean movePending = true;

    

    final public class Photographer extends Trigger {
        public Photographer() { 
            super(TYPE.ENTRANCE); 
        }

        @Override
        public void fire(TYPE phase, Object... args) {
            Personage personage = (Personage) args[1];
            if (phase == TYPE.ENTRANCE && personage instanceof Tippler
                    && ((Tippler) personage).isAsleep()) {
                
                photographed.add((Tippler) personage);
                if (photographed.size() >= 40 && goingBack == false)
                    goingBack = true;
            }
        }
    } // class Photographer
    
    
    public Inspector (List<Integer> dockPoint, int radius) {
        this.dockPoint = dockPoint;
        this.radius = radius;
        movePending = true;
    }

    private void updateDirection() {
        List<Integer> currentCoordinates = getPosition().getCoordinates();
        moveCoord = Seed.getRandomInt() % currentCoordinates.size();
        moveDir = Seed.getRandomBoolean();
    }

    @Override
    public void makeMove() {
        if (!goingBack) {
            // change direction if:
            // move failed (pending flag not removed)
            // about every 10th move
            if (movePending || Seed.getRandomInt() % 10 == 0)
                updateDirection();

            List<Integer> currentCoordinates =  getPosition().getCoordinates();
            List<Integer> targetCoordinates =   new ArrayList<>(currentCoordinates);

            targetCoordinates.set(moveCoord, currentCoordinates.get(moveCoord) + (moveDir ? 1 : -1));

            getDispatcher().registerMovement(Move.make(targetCoordinates, this));

            movePending = true;
            return;
        } 


        List<Integer> coordinates = new ArrayList<>(getPosition().getCoordinates());

        if (dockPoint.equals(coordinates))
            throw new StopSimulationException();

        while(true) { 

            int i = Seed.getRandomInt() % coordinates.size();
            if ((this.dockPoint.get(i) - coordinates.get(i)) != 0) {

                coordinates.set(i, coordinates.get(i) +
                        ((this.dockPoint.get(i) - coordinates.get(i)) < 0 ? -1 : +1));

                break;
            }

        }

        getDispatcher().registerMovement(Move.make(coordinates, this));

    }

    @Override
    public Inspector getActualEntity() {
        return this;
    }

    @Override
    public String getPictogram() {
        return "^";
    }

    @Override
    public void accommodate() {

        // Register lighter's trigger in the sphere of radius ``Radius''

        List<Integer> currentCoordinates = getPosition().getCoordinates();

        // "Draws" discrete n-sphere.
        // In the case of 2 dimensions:
        //
        // |---------- X
        // |     x
        // |    xxx
        // |   xxxxx
        // |  xxxxxxx
        // |   xxxxx
        // |    xxx
        // |     x
        // |
        // Y

        deployTriggers(this.photographer, currentCoordinates, radius - 1);
        movePending = false;
    }

    @Override
    public void pack() {
        List<Integer> currentCoordinates = getPosition().getCoordinates();
        undeployTriggers(this.photographer, currentCoordinates, radius - 1);
    }


    private void deployTriggers(Trigger trigger, List<Integer> _coords, int _radius) {
        if (_radius == -1) {
            int s = 0;
            for (int i=0; i < _coords.size(); ++i)
                s += Math.abs(_coords.get(i) - getPosition().getCoordinates().get(i));

            if (s <= radius) {
                getDispatcher().registerTrigger(trigger, _coords);
            }

            return;
        }    
    
        List<Integer> _ntuple = new ArrayList<>(_coords);
        for (int i=-radius; i <= radius; ++i) {
            _ntuple.set(_radius, _coords.get(_radius) + i);
            deployTriggers(trigger, _ntuple, _radius - 1);
        }
    }

    private void undeployTriggers(Trigger trigger, List<Integer> _coords, int _radius) {

        if (_radius == -1) {
            int s = 0;
            for (int i=0; i < _coords.size(); ++i)
                s += Math.abs(_coords.get(i) - getPosition().getCoordinates().get(i));

            if (s <= radius) {
                getDispatcher().unregisterTrigger(trigger, _coords);
            }

            return;
        }

        List<Integer> _ntuple = new ArrayList<>(_coords);

        for (int i=-radius; i <= radius; ++i) {
            _ntuple.set(_radius, _coords.get(_radius) + i);
            undeployTriggers(trigger, _ntuple, _radius - 1);
        }
    }
}