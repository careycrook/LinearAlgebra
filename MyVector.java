import Jama.Matrix;
import org.omg.SendingContext.RunTime;

import java.text.DecimalFormat;

/**
 * Created by Carey on 9/30/2014.
 */
public class MyVector {

    private Matrix v;

    public MyVector(double[] vec) {
        v = new Matrix(vec, vec.length);
    }


    public MyVector(int size) {
        v = new Matrix(size, 1);
    }

    public MyVector(Matrix m) {
        if (m.getColumnDimension() != 1) { throw new RuntimeException("Invalid Vector Dimensions"); }
        else { v = m; }
    }

    public MyVector(MyMatrix m) {
        if (m.numCols() != 1) { throw new RuntimeException("Invalid Vector Dimensions"); }
        else { v = m.toMatrix(); }
    }

    public Matrix toMatrix() { return v; }

    public MyMatrix toMyMatrix() { return new MyMatrix(v); }

    public double get(int i) { return v.get(i, 0); }

    public void set(int i, double data) { v.set(i, 0, data); }

    public int numRows() { return v.getRowDimension(); }

    public int numCols() { return 1; }

    public MyVector zeroVect() {
        double[] temp = new double[this.numRows()];
        for (int i = 0; i < this.numRows(); i++) {
            temp[i] = 0;
        }
        return new MyVector(temp);
    }

    public MyVector plus(MyVector u) {
        return new MyVector(v.plus(u.toMatrix()));
    }

    public MyVector minus(MyVector u) {
        return new MyVector(v.minus(u.toMatrix()));
    }

    public MyVector scale(double coef) {
        return new MyVector(v.times(coef));
    }

    public double norm() {
        double res = 0.0;
        for (int i = 0; i < numRows(); i++) {
            res += (get(i) * get(i));
        }
        res = Math.sqrt(res);
        Math.round(res);
        return res;
    }

    public MyVector normalize() {
        return this.scale(1 / this.norm());
    }

    public double dot(MyVector u) {
        double res = 0.0;
        MyVector v = this;
        for (int i = 0; i < this.numRows(); i++) {
            res += (v.get(i) * u.get(i));
        }
        return res;
    }

    public MyVector projectOnTo(MyVector v) {
        return v.scale(this.dot(v) / (v.dot(v)));
    }

    public MyMatrix transpose() { return new MyMatrix(v.transpose()); }

    // theta must be radians
    public MyVector rotate(double theta) {
        if (this.numRows() != 2) { throw new RuntimeException("Invalid Vector (must be 2x2 if no axis parameter given)"); }
        MyMatrix rotationM = new MyMatrix(2, 2);
        rotationM.set(0, 0, Math.cos(theta));
        rotationM.set(0, 1, -1 * Math.sin(theta));
        rotationM.set(1, 0, Math.sin(theta));
        rotationM.set(1, 1, Math.cos(theta));

        return new MyVector(rotationM.times(this.toMyMatrix()));
    }

    public MyVector rotate(double theta, char axis) {
        if (this.numRows() == 2) { return rotate(theta); }
        else if (this.numRows() > 3) { throw new RuntimeException("Invalid Vector (vector length > 3"); }
        else if (axis == 'x') {
            MyMatrix rotationM = new MyMatrix(3,3);
            rotationM.set(0,0, 1);
            rotationM.set(0,1, 0);
            rotationM.set(0,2, 0);
            rotationM.set(1,0, 0);
            rotationM.set(1,1, Math.cos(theta));
            rotationM.set(1,2, -1 * Math.sin(theta));
            rotationM.set(2,0, 0);
            rotationM.set(2,1, Math.sin(theta));
            rotationM.set(2,2, Math.cos(theta));
            return new MyVector(rotationM.times(this.toMyMatrix()));
        } else if (axis == 'y') {
            MyMatrix rotationM = new MyMatrix(3,3);
            rotationM.set(0,0, Math.cos(theta));
            rotationM.set(0,1, 0);
            rotationM.set(0,2, Math.sin(theta));
            rotationM.set(1,0, 0);
            rotationM.set(1,1, 1);
            rotationM.set(1,2, 0);
            rotationM.set(2,0, Math.cos(theta));
            rotationM.set(2,1, 0);
            rotationM.set(2,2, Math.cos(theta));
            return new MyVector(rotationM.times(this.toMyMatrix()));
        } else if (axis == 'z') {
            MyMatrix rotationM = new MyMatrix(3,3);
            rotationM.set(0,0, Math.cos(theta));
            rotationM.set(0,1, -1 * Math.sin(theta));
            rotationM.set(0,2, 0);
            rotationM.set(1,0, Math.sin(theta));
            rotationM.set(1,1, Math.cos(theta));
            rotationM.set(1,2, 0);
            rotationM.set(2,0, 0);
            rotationM.set(2,1, 0);
            rotationM.set(2,2, 1);
            return new MyVector(rotationM.times(this.toMyMatrix()));
        }
        else { throw new RuntimeException("Invalid Axis (only accepts x, y, or z)"); }
    }

    public double[] toArray() {
        double[] res = new double[this.numRows()];
        for (int i = 0; i < numRows(); i++) {
            res[i] = this.get(i);
        }
        return res;
    }

    public void print() {
        for (int i = 0; i < this.numRows(); i++) {
            System.out.print(" " + this.get(i));
        }
        System.out.println();
    }
}
