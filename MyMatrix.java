import Jama.Matrix;
import java.util.ArrayList;

/**
 * Created by Carey on 9/29/2014.
 */

public class MyMatrix {

    private Matrix a;
    private int rows, cols;
    private double res = 0;

    public MyMatrix(double[][] m) {
        a = new Matrix(m);
        rows = m.length;
        cols = m[0].length;
    }

    public MyMatrix(Matrix m) {
        a = m;
        rows = m.getRowDimension();
        cols = m.getColumnDimension();
    }

    public MyMatrix(int i, int j) {
        a = new Matrix(i, j);
        rows = i;
        cols = j;
    }

    public MyMatrix(MyVector ... vecs) {
        a = new Matrix(vecs[0].numRows(), vecs.length);
        for (int i = 0; i < vecs.length; i++) {
            for (int j = 0; j < vecs[0].numRows(); j++) {
                a.set(i, j, vecs[i].get(j));
            }
        }
        rows = vecs[0].numRows();
        cols = vecs.length;
    }

    public Matrix toMatrix() {
        return a;
    }

    public boolean isSquare() { return (numCols() == numRows()); }

    public int numRows() { return rows; }

    public int numCols() { return cols; }

    public double get(int i, int j) { return a.get(i, j); }

    public double[] getCol(int j) {
        double[] temp = new double[numRows()];
        for (int i = 0; i < numRows(); i++) {
            temp[i] = this.get(i, j);
        }
        return temp;
    }

    public void set(int i, int j, double data) { a.set(i, j, data); }

    public MyMatrix append(MyVector vec) {
        if (vec.numRows() != this.numRows()) { throw new RuntimeException("Invalid Vector"); }
        MyMatrix temp = new MyMatrix(this.numRows(), this.numCols() + 1);
        for (int i = 0; i < temp.numRows(); i++) {
            for (int j = 0; j < temp.numCols(); j++) {
                if (j == temp.numCols() - 1) {
                    temp.set(i, j, vec.get(i));
                } else {
                    temp.set(i, j, this.get(i, j));
                }
            }
        }
        return temp;
    }

    public MyVector[] toVectors() {
        MyVector[] temp = new MyVector[this.numCols()];
        for (int j = 0; j < this.numCols(); j++) {
            double[] vec = new double[this.numRows()];
            for (int i = 0; i < this.numRows(); i++) {
                vec[i] = this.get(i, j);
            }
            temp[j] = new MyVector(vec);
        }
        return temp;
    }

    public MyMatrix plus(MyMatrix m) {
        return new MyMatrix(a.plus(m.toMatrix()));
    }

    public MyMatrix minus(MyMatrix m) {
        return new MyMatrix(a.minus(m.toMatrix()));
    }

    public MyMatrix identity() {
        MyMatrix res;
        if (numRows() > numCols()) {
            res = new MyMatrix(numRows(), numRows());
        } else {
            res = new MyMatrix(numCols(), numCols());
        }
        for (int i = 0; i < res.numRows(); i++) {
            for (int j = 0; j < res.numCols(); j++) {
                if (i == j) { res.set(i, j, 1); }
                else { res.set(i, j, 0); }
            }
        }
        return res;
    }

    public MyMatrix scale(double coef) {
        return new MyMatrix(a.times(coef));
    }

    public MyMatrix transpose() {
        return new MyMatrix(a.transpose());
    }

    public MyMatrix times(MyMatrix m) {
        if (numCols() != m.numRows()) {
            throw new RuntimeException("Invalid Matrix");
        }
        Matrix res = new Matrix(a.getRowDimension(), m.numCols());
        for (int i = 0; i < res.getRowDimension(); i++) {
            for (int j = 0; j < res.getColumnDimension(); j++) {
                double[] aY = new double[numCols()];
                double[] bX = new double[m.numRows()];
                for (int n = 0; n < aY.length; n++) {
                    aY[n] = a.get(i, n);
                }
                for (int n = 0; n < bX.length; n++) {
                    bX[n] = m.get(n, j);
                }
                MyVector u = new MyVector(aY);
                MyVector v = new MyVector(bX);
                res.set(i, j, u.dot(v));
            }
        }
        return new MyMatrix(res);
    }

    public MyMatrix splice(int row, int col) {
        MyMatrix temp = new MyMatrix(numRows() - 1, numCols() - 1);
        for (int i = 0; i < numRows() - 1; i++) {
            for (int j = 0; j < numCols() - 1; j++) {
                if (i < row) {
                    if (j < col) {
                        temp.set(i, j, this.get(i, j));
                    } else {
                        temp.set(i, j, this.get(i, j + 1));
                    }
                } else {
                    if (j < col) {
                        temp.set(i, j, this.get(i + 1, j));
                    } else {
                        temp.set(i, j, this.get(i + 1, j + 1));
                    }
                }
            }
        }
        return temp;
    }

    private double determinant(MyMatrix m) {
        if (m.numCols() == 2) {
            return (m.get(0,0) * m.get(1,1)) - (m.get(0,1) * m.get(1,0));
        } else {
            double res = 0;
            for (int n = 0; n < m.numCols(); n++) {
                if (n % 2 == 0) {
                    res = res + (m.get(0, n) * determinant(m.splice(0, n)));
                } else {
                    res = res - (m.get(0, n) * determinant(m.splice(0, n)));
                }
            }
            return res;
        }
    }

    public double determinant() {
        if (!isSquare() || this.numCols() < 2) {
            throw new RuntimeException("Invalid Matrix");
        }
        return determinant(new MyMatrix(a));
        }

    public MyMatrix cofactor() {
        MyMatrix temp1 = new MyMatrix(this.numRows(), this.numCols());
            for (int i = 0; i < temp1.numRows(); i++) {
                for (int j = 0; j < temp1.numCols(); j++) {
                    if ((i + j) % 2 == 0) {
                        temp1.set(i, j, this.splice(i, j).determinant());
                    } else {
                        temp1.set(i, j, -1 * this.splice(i, j).determinant());
                    }
                }
            }
        return temp1;
    }

    public MyMatrix inverse() {
        if (determinant() == 0) {
            return null;
        }

        MyMatrix temp = new MyMatrix(this.numRows(), this.numCols());
        temp.set(0,0, this.get(1,1));
        temp.set(0,1, -this.get(0,1));
        temp.set(1,0, -this.get(1,0));
        temp.set(1,1, this.get(0,0));
        temp.scale(1/this.determinant());
        return temp;
    }

    public MyMatrix[] qRHouseHolder() {
        if (numCols() > numRows()) { throw new RuntimeException("Invalid Matrix"); }
        ArrayList<MyMatrix> hHolder = new ArrayList<MyMatrix>();
        MyMatrix temp = new MyMatrix(numRows(), numCols());
        for (int i = 0; i < numRows(); i++) {
            for (int j = 0; j < numCols(); j++) {
                temp.set(i,j, this.get(i,j));
            }
        }
        for (int j = 0; j < numCols(); j++) {
            if (!zeroesBelow(j, j)) {
                MyVector aVec = new MyVector(temp.getCol(j));
                for (int i = 0; i < j; i++) {
                    aVec.set(i, 0);
                }
                MyVector uVec = aVec;
                uVec.set(j, aVec.get(j) + aVec.norm());
                uVec = uVec.normalize();
                hHolder.add(temp.identity().minus(((uVec.toMyMatrix().times(uVec.transpose()).scale(2)))));
                temp = hHolder.get(j).times(temp);
            }
        }
        MyMatrix[] qR = new MyMatrix[2];
        MyMatrix hhTemp = this.identity();
        for (int i = 0; i < hHolder.size(); i++) {
            hhTemp = hHolder.get(i).times(hhTemp);
        }
        qR[0] = hhTemp.transpose();     // q
        qR[1] = temp;                   // r
        return qR;
    }

    public MyMatrix[] qRGivens() {
        if (numCols() > numRows()) { throw new RuntimeException("Invalid Matrix"); }
        ArrayList<MyMatrix> givens = new ArrayList<MyMatrix>();
        MyMatrix temp = new MyMatrix(numRows(), numCols());
        for (int i = 0; i < numRows(); i++) {
            for (int j = 0; j < numCols(); j++) {
                temp.set(i,j, this.get(i,j));
            }
        }
        MyMatrix tempGivens;
        int end;
        if (numCols() == numRows()) {
            end = 1;
        } else { end = 0; }
        for (int j = 0; j < numCols() - end; j++) {
            boolean flag = true;
            while(!temp.zeroesBelow(j, j) && flag) {
                tempGivens = temp.identity();
                int index = -1;
                for (int i = j + 1; i < numRows(); i++) {
                    if ((temp.get(i,j) > .0000001 || temp.get(i,j) < -.0000001) && index == -1) {
                        index = i;
                    }
                }
                double x = temp.get(j, j);
                double y = temp.get(index, j);
                double cosTheta = x / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
                double sinTheta = -y / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
                tempGivens.set(j, j, cosTheta);
                tempGivens.set(index, j, sinTheta);
                tempGivens.set(j, index, -sinTheta);
                tempGivens.set(index, index, cosTheta);
                givens.add(tempGivens);
                temp = tempGivens.times(temp);
            }
        }
        MyMatrix [] qR = new MyMatrix[2];
        tempGivens = temp.identity();
        for (int i = 0; i < givens.size(); i++) {
            tempGivens = tempGivens.times(givens.get(i).transpose());
        }
        qR[0] = tempGivens; // Q
        qR[1] = temp;       // R
        return qR;
    }

    public double trace() {
        if (!isSquare()) { throw new RuntimeException("Invalid Matrix"); }
        double res = 0;
        for (int i = 0; i < this.numRows(); i++) {
            res += this.get(i,i);
        }
        return res;
    }

    public boolean zeroesBelow(int pivot, int col) {
        boolean init = false;
        for (int i = pivot + 1; i < numRows(); i++) {
            if (get(i, col) > .0000001 || get(i, col) < -.0000001) {
                return false;
            }
        }
        return true;
    }

    public void print() {
        for (int i = 0; i < numRows(); i++) {
            for (int j = 0; j < numCols(); j++) {
                if (get(i, j) >= 0) { System.out.print(" "); }
                System.out.print(get(i,j) + " ");
            }
            System.out.println();
        }
    }
}
