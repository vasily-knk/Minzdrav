package ru.spbau.kononenko.drunkgame;


import ru.spbau.kudinkin.simulator.characters.Personage;
import ru.spbau.kudinkin.simulator.characters.instances.Beggar;
import ru.spbau.kudinkin.simulator.characters.instances.Bottle;

public class MyBeggar extends Beggar {
    private int money = 0;

    public int getMoney() {
        return money;
    }

    @Override
    public void accommodate() {
        for (Personage personage : getPosition().getInhabitants()) {
            if (personage.getClass().equals(Bottle.class)) {
                ++money;
                break;
            }
        }

        super.accommodate();

    }
}
