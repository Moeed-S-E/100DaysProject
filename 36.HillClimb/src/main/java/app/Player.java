package app;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
//import javafx.scene.transform.Rotate;

public class Player extends Sprite {
    private boolean onGround = false;
    public double gas = 100;
    private int suspension = 0;
    private double[] suspensionLevels = {0.8, 0.75, 0.7, 0.65, 0.6, 0.55, 0.5, 0.45, 0.4, 0.35};
    private int speed = 0;
    private double[] speedLevels = {0.35, 0.5, 0.6, 0.8, 0.9, 1.0, 1.1, 1.2};
    private int tank = 0;
    private double[] tankLevels = {1, 1.25, 1.5, 1.75, 2, 2.35, 2.7, 3, 3.5, 4};
    private int engine = 0;
    private double[] engineLevels = {0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4, 1.5};
    private double frontWheelY = 0;
    private double rearWheelY = 0;
    private double suspensionForce = 0.1;
    private double damping = 0.9;
    private boolean canJump = true; // For jump debouncing

    public Player(String imagePath) {
        super(imagePath);
        w = 70;
        h = 45;
    }

    public void allowJump() {
        canJump = true; // Called when Up Arrow is released
    }

    public String upgrade() {
        double rand = Math.random();
        if (rand < 0.25 && suspension < 9) {
            suspension++;
            return "Suspension";
        } else if (rand < 0.5 && speed < 9) {
            speed++;
            return "Speed";
        } else if (rand < 0.75 && tank < 9) {
            tank++;
            return "Gas Tank";
        } else if (rand < 1 && engine < 9) {
            engine++;
            return "Engine";
        } else {
            return "Nothing";
        }
    }

    public void stats(GraphicsContext g, int x, int y, int h) {
        g.setFill(Color.WHITE);
        g.fillText("Suspension: " + (suspension + 1), x, y);
        g.fillText("Speed: " + (speed + 1), x, y + h);
        g.fillText("Gas Tank: " + (tank + 1), x, y + h * 2);
        g.fillText("Engine: " + (engine + 1), x, y + h * 3);
    }

    public boolean brokenDown() {
        return gas <= 0.0 && vel.magSqr() < 1 && onGround;
    }

    public void restart() {
        pos.set(0, Game.HEIGHT / 2);
        vel.set(0, 0);
        acc.set(0, 0);
        gas = 100.0;
        frontWheelY = 0;
        rearWheelY = 0;
        canJump = true;
    }

    public double getFuel() {
        return Math.ceil(gas * 10) / 10;
    }

    @Override
    public void draw(GraphicsContext g) {
        int carX = 100;
        int carCenterX = carX + w / 2;
        int floorIndex = Math.min(Math.max(carCenterX, 0), Board.floor.length - 1);
        int floorY = Board.floor[floorIndex];
        pos.y = floorY - h;

        int wheelOffset = w / 3;
        int frontWheelX = carCenterX + wheelOffset;
        int rearWheelX = carCenterX - wheelOffset;
        frontWheelX = Math.min(Math.max(frontWheelX, 0), Board.floor.length - 1);
        rearWheelX = Math.min(Math.max(rearWheelX, 0), Board.floor.length - 1);
        double frontFloorY = Board.floor[frontWheelX];
        double rearFloorY = Board.floor[rearWheelX];

        double slopeAngle = Math.atan2(frontFloorY - rearFloorY, frontWheelX - rearWheelX);
        g.save();
        g.translate(carX + w / 2, pos.y + h / 2);
        g.rotate(Math.toDegrees(slopeAngle));
        g.drawImage(image, -w / 2, -h / 2, w, h);
        g.restore();
    }

    public void update() {
        onGround = false;
        int carCenterX = 100 + w / 2;
        int floorIndex = Math.min(Math.max(carCenterX, 0), Board.floor.length - 1);
        int floorY = Board.floor[floorIndex];

        int wheelOffset = w / 3;
        int frontWheelX = carCenterX + wheelOffset;
        int rearWheelX = carCenterX - wheelOffset;
        frontWheelX = Math.min(Math.max(frontWheelX, 0), Board.floor.length - 1);
        rearWheelX = Math.min(Math.max(rearWheelX, 0), Board.floor.length - 1);
        double frontFloorY = Board.floor[frontWheelX];
        double rearFloorY = Board.floor[rearWheelX];

        double frontTargetY = frontFloorY - h;
        double rearTargetY = rearFloorY - h;
        double frontDisplacement = frontWheelY - frontTargetY;
        double rearDisplacement = rearWheelY - rearTargetY;
        double frontSpringForce = -suspensionForce * frontDisplacement * suspensionLevels[suspension];
        double rearSpringForce = -suspensionForce * rearDisplacement * suspensionLevels[suspension];
        frontSpringForce *= damping;
        rearSpringForce *= damping;

        frontWheelY += frontSpringForce;
        rearWheelY += rearSpringForce;
        pos.y = (frontWheelY + rearWheelY) / 2;

        if (pos.y >= floorY - h && vel.y >= 0) { // Only reset velocity when falling
            pos.y = floorY - h;
            vel.y = 0;
            onGround = true;
        }

        double eng = engineLevels[engine];
        double gasUse = 1.0 / 50 / tankLevels[tank];
        if (Game.keys.contains(KeyCode.LEFT) && gas > 0) {
            acc.x += -eng;
            gas -= gasUse;
//            System.out.println("Left movement: gas reduced by " + gasUse + ", new gas=" + gas);
        }
        if (Game.keys.contains(KeyCode.RIGHT) && gas > 0) {
            acc.x += eng;
            gas -= gasUse;
//            System.out.println("Right movement: gas reduced by " + gasUse + ", new gas=" + gas);
        }

        acc.add(0, GRAVITY);
        vel.add(acc);
        pos.add(vel);
        acc.set(0, 0);
        vel.x *= 0.96;
        vel.y *= 0.9; // Reduced damping for more noticeable jumps

        // Jump logic with debouncing
        if (Game.keys.contains(KeyCode.UP) && gas > 0 && onGround && canJump) {
            vel.y = -10 * eng;
            canJump = false; // Prevent repeated jumps until key release
//            System.out.println("Jump triggered: onGround=" + onGround + ", gas=" + gas + ", vel.y=" + vel.y);
        }

        vel.limit(12 * speedLevels[speed]);
        if (gas < 0) {
            gas = 0;
        }
    }

    @Override
    public String toString() {
        return pos.toString() + ";" + vel.toString() + ";" + gas + ";" + suspension + ";" + speed + ";" + tank + ";" + engine + ";" + frontWheelY + ";" + rearWheelY + ";" + canJump;
    }

    public void fromString(String data) {
        String[] vars = data.split(";");
        if (vars.length < 9) {
            System.out.println("Unable to load Player data.");
            return;
        }
        String[] Pos = vars[0].split(",");
        String[] Vel = vars[1].split(",");

        pos.set(Double.parseDouble(Pos[0]), Double.parseDouble(Pos[1]));
        vel.set(Double.parseDouble(Vel[0]), Double.parseDouble(Vel[1]));
        gas = Double.parseDouble(vars[2]);
        suspension = Integer.parseInt(vars[3]);
        speed = Math.min(Integer.parseInt(vars[4]), 9);
        tank = Integer.parseInt(vars[5]);
        engine = Integer.parseInt(vars[6]);
        frontWheelY = Double.parseDouble(vars[7]);
        rearWheelY = Double.parseDouble(vars[8]);
        if (vars.length > 9) {
            canJump = Boolean.parseBoolean(vars[9]);
        }
    }
}