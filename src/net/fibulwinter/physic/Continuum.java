package net.fibulwinter.physic;

import android.util.Log;
import com.google.common.base.Optional;
import net.fibulwinter.model.Checker;
import net.fibulwinter.utils.PairOperation;
import net.fibulwinter.utils.V;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class Continuum {
    private static double TIME_QUANTUM=1;

    private List<Body> bodies=newArrayList();
    private double time=0.0;

    public List<Body> getBodies() {
        return bodies;
    }

    public double getTime() {
        return time;
    }

    public void simulate(double dTime){
        double targetTime = time + dTime;
        while (time<targetTime){
            double timeStep = Math.min(TIME_QUANTUM, targetTime - time);
            time+= timeStep;
            for(Body body:bodies){
                body.move(timeStep);
            }
            for (int i = 0, bodiesSize = bodies.size(); i < bodiesSize; i++) {
                Body body1 = bodies.get(i);
                for(int j=i+1; j<bodiesSize; j++){
                    Body body2 = bodies.get(j);
                    if(!body1.isFixed() || !body2.isFixed()){
                        checkInteraction(body1, body2);
                    }
                }

            }
        }
    }

    private void checkInteraction(Body body1, Body body2) {
        Optional<V> touchPoint = PairOperation.applySingle(body1, body2,
            new PairOperation<Circle, Circle, V>(Circle.class, Circle.class) {
                @Override
                public Optional<V> performWithPair(Circle circleA, Circle circleB) {
                    double sumRadiuses = circleA.getRadius() + circleB.getRadius();
                    if (circleA.getCenter().inDistance(circleB.getCenter(), sumRadiuses)) {
                        V vAB = circleB.getCenter().subtract(circleA.getCenter());
                        return Optional.of(circleA.getCenter().addScaled(vAB, circleA.getRadius() / sumRadiuses));
                    } else {
                        return Optional.absent();
                    }
                }
            },
            new PairOperation<Circle, LineObstacle, V>(Circle.class, LineObstacle.class) {
                @Override
                public Optional<V> performWithPair(Circle circle, LineObstacle line) {
                    if(circle.getCenter().subtract(line.getCenter()).dot(line.getNormal())>circle.getRadius()){
                        return Optional.absent();
                    }else{
                        V left = line.getNormal().left();
                        double a = left.dot(circle.getCenter().subtract(line.getCenter()));
                        return Optional.of(line.getCenter().addScaled(left, a));
                    }
                }
            }
        );
        if(touchPoint.isPresent()){
            body1.avoid(touchPoint.get());
            body2.avoid(touchPoint.get());
            PairOperation.applySingle(body1, body2,
                    new PairOperation<Circle, Circle, V>(Circle.class, Circle.class) {
                        @Override
                        public Optional<V> performWithPair(Circle circleA, Circle circleB) {
                            interactCircles(circleA,circleB);
                            return Optional.absent();
                        }
                    },
                    new PairOperation<Circle, LineObstacle, V>(Circle.class, LineObstacle.class) {
                        @Override
                        public Optional<V> performWithPair(Circle circle, LineObstacle line) {
                            double resultSpeedOut = -circle.getSpeed().dot(line.getNormal());
                            circle.setSpeed(circle.getSpeed().add(line.getNormal().scale(resultSpeedOut*2)));
                            return Optional.absent();
                        }
                    }
            );
        }
    }

    private void interactCircles(Circle bodyA, Circle bodyB) {
        V vAB = bodyB.getCenter().subtract(bodyA.getCenter());
        V normalAB = vAB.normal();
        double speedA1 = bodyA.getSpeed().dot(normalAB);
        double speedB1 = bodyB.getSpeed().dot(normalAB);
        double speedA2 = speedA1-speedB1;
        double speedB2 = speedB1-speedB1;
        double mass = bodyA.getMass() + bodyB.getMass();
        double speedA3 = speedA2*(bodyA.getMass()-bodyB.getMass())/mass;
        double speedB3 = 2*speedA2*bodyA.getMass()/mass;
        double speedA4 = speedA3+speedB1;
        double speedB4 = speedB3+speedB1;
        V psA=bodyA.getSpeed().subtract(normalAB.scale(speedA1));
        V psB=bodyB.getSpeed().subtract(normalAB.scale(speedB1));
        bodyA.setSpeed(psA.addScaled(normalAB, speedA4));
        bodyB.setSpeed(psB.addScaled(normalAB, speedB4));
    }
}
