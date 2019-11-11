
public class Skeleton extends Creature {

    public Skeleton(Animation left, Animation right,
                    Animation deadLeft, Animation deadRight) {
        super(left, right, deadLeft, deadRight);
    }


    public float getMaxSpeed() {
        return 0.05f;
    }

}
