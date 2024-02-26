package tmp.uqam.stage.genetic.operations;

import java.util.Random;

public class DeterministRandom extends Random {

    private int value;

    public DeterministRandom(int value) {
        super();
        this.value = value;
    }

    public int nextInt() {
        return value;
    }

    public int nextInt(int bound) {
        return value;
    }
}
