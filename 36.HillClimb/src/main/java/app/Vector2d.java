package app;
public class Vector2d {
    public double x, y;

    Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    Vector2d() {
        this.x = 0.0;
        this.y = 0.0;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector2d o) {
        this.x = o.x;
        this.y = o.y;
    }

    public double magSqr() {
        return x * x + y * y;
    }

    public double mag() {
        return Math.sqrt(x * x + y * y);
    }

    public void normalize() {
        double len = mag();
        x /= len;
        y /= len;
    }

    public void mult(double m) {
        x *= m;
        y *= m;
    }

    public void setMag(double m) {
        normalize();
        mult(m);
    }

    public void limit(double maxMag) {
        double m = magSqr();
        if (m > maxMag * maxMag) {
            setMag(maxMag);
        }
    }

    public double angTo(Vector2d o) {
        return Math.atan2(y - o.y, x - o.x);
    }

    public void add(Vector2d o) {
        x += o.x;
        y += o.y;
    }

    public void add(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public void sub(Vector2d o) {
        x -= o.x;
        y -= o.y;
    }

    public void sub(double x, double y) {
        this.x -= x;
        this.y -= y;
    }

    @Override
    public String toString() {
        return "" + x + "," + y;
    }
}