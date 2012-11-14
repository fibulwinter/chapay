package net.fibulwinter.physic;

import com.google.common.base.Optional;
import net.fibulwinter.geometry.Disk;
import net.fibulwinter.geometry.LineSegment;
import net.fibulwinter.utils.PairOperation;
import net.fibulwinter.geometry.V;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class Continuum {
    private static double TIME_QUANTUM=0.5;

    private List<Body> bodies=newArrayList();
    private double time=0.0;
    private FrictionModel frictionModel;

    public Continuum(FrictionModel frictionModel) {
        this.frictionModel = frictionModel;
    }

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
                body.move(timeStep, frictionModel.getFriction(body.getCenter()));
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
        Optional<V> touchPoint = PairOperation.applySingle(body1.getShape(), body2.getShape(),
            new PairOperation<Disk, Disk, V>(Disk.class, Disk.class) {
                @Override
                public Optional<V> performWithPair(Disk diskA, Disk diskB) {
                    double sumRadiuses = diskA.getRadius() + diskB.getRadius();
                    if (diskA.getCenter().inDistance(diskB.getCenter(), sumRadiuses)) {
                        V vAB = diskB.getCenter().subtract(diskA.getCenter());
                        return Optional.of(diskA.getCenter().addScaled(vAB, diskA.getRadius() / sumRadiuses));
                    } else {
                        return Optional.absent();
                    }
                }
            },
            new PairOperation<Disk, LineSegment, V>(Disk.class, LineSegment.class) {
                @Override
                public Optional<V> performWithPair(Disk disk, LineSegment lineSegment) {
                    double distFromLine = disk.getCenter().subtract(lineSegment.getCenter()).dot(lineSegment.getNormal());
                    if(distFromLine<0 || distFromLine>disk.getRadius()){
                        return Optional.absent();
                    }else{
                        V left = lineSegment.getNormal().left();
                        double a = left.dot(disk.getCenter().subtract(lineSegment.getCenter()));
                        if(Math.abs(a)>lineSegment.getLength()/2){
/*
                            if(line.getP1().inDistance(disk.getCenter(),disk.getRadius())){
                                return Optional.of(line.getP1());
                            }
                            if(line.getP2().inDistance(disk.getCenter(),disk.getRadius())){
                                return Optional.of(line.getP2());
                            }
*/
                            return Optional.absent();
                        }
                        return Optional.of(lineSegment.getCenter().addScaled(left, a));
                    }
                }
            }
        );
        if(touchPoint.isPresent()){
            body1.getShape().avoid(touchPoint.get());
            body2.getShape().avoid(touchPoint.get());
            PairOperation.applySingle(body1, body2,
                    new PairOperation<DynamicBody, DynamicBody, V>(DynamicBody.class, DynamicBody.class) {
                        @Override
                        public Optional<V> performWithPair(DynamicBody dynamicBodyA, DynamicBody dynamicBodyB) {
                            interactCircles(dynamicBodyA, dynamicBodyB);
                            return Optional.absent();
                        }
                    },
                    new PairOperation<DynamicBody, StaticBody, V>(DynamicBody.class, StaticBody.class) {
                        @Override
                        public Optional<V> performWithPair(DynamicBody dynamicBody, StaticBody staticBody) {
                            if(staticBody.getShape() instanceof LineSegment){
                                LineSegment lineSegment = (LineSegment) staticBody.getShape();

                                double speedInsideLine = dynamicBody.getSpeed().dot(lineSegment.getNormal());
                                if(speedInsideLine<0){
                                    double resultSpeedOut = -speedInsideLine;
                                    dynamicBody.setSpeed(dynamicBody.getSpeed().add(lineSegment.getNormal().scale(resultSpeedOut*2)));
                                }
                            }else if(staticBody.getShape() instanceof Disk){
                                Disk staticDisk = (Disk) staticBody.getShape();

                                V normal = staticDisk.getCenter().subtract(dynamicBody.getCenter()).normal();
                                double speedInsideLine = dynamicBody.getSpeed().dot(normal);
                                if(speedInsideLine>0){
                                    double resultSpeedOut = -speedInsideLine;
                                    dynamicBody.setSpeed(dynamicBody.getSpeed().add(normal.scale(resultSpeedOut * 2)));
                                }
                            }
                            return Optional.absent();
                        }
                    }
            );
        }
    }

    private void interactCircles(DynamicBody bodyA, DynamicBody bodyB) {
        V vAB = bodyB.getCenter().subtract(bodyA.getCenter());
        V normalAB = vAB.normal();
        double speedA1 = bodyA.getSpeed().dot(normalAB);
        double speedB1 = bodyB.getSpeed().dot(normalAB);
        double speedA2 = speedA1-speedB1;
        double speedB2 = speedB1-speedB1;
        if(speedA2>0){
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
}
