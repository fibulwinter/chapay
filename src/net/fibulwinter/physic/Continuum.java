package net.fibulwinter.physic;

import com.google.common.base.Optional;
import net.fibulwinter.geometry.Disk;
import net.fibulwinter.geometry.LineSegment;
import net.fibulwinter.model.Checker;
import net.fibulwinter.model.Level;
import net.fibulwinter.utils.PairOperation;
import net.fibulwinter.geometry.V;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class Continuum {
    private static double TIME_QUANTUM=0.5;

    private List<Body> bodies=newArrayList();
    private double time=0.0;
    private final Level level;

    public Continuum(Level level) {
        this.level = level;
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
                body.move(timeStep, level.getFriction(body.getCenter()));
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

                                double speedInsideLine = dynamicBody.getVelocity().dot(lineSegment.getNormal());
                                if(speedInsideLine<0){
                                    double resultSpeedOut = -speedInsideLine;
                                    dynamicBody.setVelocity(dynamicBody.getVelocity().add(lineSegment.getNormal().scale(resultSpeedOut * 2)));
                                }
                            }else if(staticBody.getShape() instanceof Disk){
                                Disk staticDisk = (Disk) staticBody.getShape();

                                V normal = staticDisk.getCenter().subtract(dynamicBody.getCenter()).normal();
                                double speedInsideLine = dynamicBody.getVelocity().dot(normal);
                                if(speedInsideLine>0){
                                    double resultSpeedOut = -speedInsideLine;
                                    dynamicBody.setVelocity(dynamicBody.getVelocity().add(normal.scale(resultSpeedOut * 2)));
                                }
                            }
                            return Optional.absent();
                        }
                    }
            );
        }
    }

    private void interactCircles2(DynamicBody bodyA, DynamicBody bodyB) {
        //this - bodyB, ball - bodyA
        // get the mtd
    V delta = (bodyB.getCenter().subtract(bodyA.getCenter()));
    double d = delta.getLength();
    // minimum translation distance to push balls apart after intersecting
    V mtd = delta.scale(((Checker.RADIUS*2)-d)/d);


    // resolve intersection --
    // inverse mass quantities
    double im1 = 1 / bodyB.getMass();
    double im2 = 1 / bodyA.getMass();

    // push-pull them apart based off their mass
//    position = position.add(mtd.multiply(im1 / (im1 + im2)));
//    ball.position = ball.position.subtract(mtd.multiply(im2 / (im1 + im2)));

    // impact speed
    V v = (bodyB.getVelocity().subtract(bodyA.getVelocity()));
    double vn = v.dot(delta.normal());

    // sphere intersecting but moving away from each other already
    if (vn > 0.0f) return;

    double restitution=1;
    // collision impulse
    double i = (-(1.0f + restitution) * vn) / (im1 + im2);
    V impulse = mtd.scale(i);

    // change in momentum
    bodyB.setVelocity(bodyB.getVelocity().addScaled(impulse,im1));
    bodyA.setVelocity(bodyA.getVelocity().addScaled(impulse,-im2));
    }

    private void interactCircles(DynamicBody bodyA, DynamicBody bodyB) {
        V vAB = bodyB.getCenter().subtract(bodyA.getCenter());
        V normalAB = vAB.normal();
        double speedA1 = bodyA.getVelocity().dot(normalAB);
        double speedB1 = bodyB.getVelocity().dot(normalAB);
        double speedA2 = speedA1-speedB1;
        double speedB2 = speedB1-speedB1;
        if(speedA2>0){
            double mass = bodyA.getMass() + bodyB.getMass();
            double speedA3 = speedA2*(bodyA.getMass()-bodyB.getMass())/mass;
            double speedB3 = 2*speedA2*bodyA.getMass()/mass;
            double speedA4 = speedA3+speedB1;
            double speedB4 = speedB3+speedB1;
            V psA=bodyA.getVelocity().subtract(normalAB.scale(speedA1));
            V psB=bodyB.getVelocity().subtract(normalAB.scale(speedB1));
            bodyA.setVelocity(psA.addScaled(normalAB, speedA4));
            bodyB.setVelocity(psB.addScaled(normalAB, speedB4));
        }
    }
}
