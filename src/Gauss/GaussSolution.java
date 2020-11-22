package Gauss;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

public class GaussSolution {
    private double Nx;
    private double Ny;
    private double Nz;

    private double startIntegralOne;
    private double endIntegralOne;

    private double startIntegralTwo;
    private double endIntegralTwo;

    private double startIntegralThree;
    private double endIntegralThree;

    private String textIntegralFunc;

    public GaussSolution(double nx, double ny, double nz, double startIntegralOne, double endIntegralOne,
                         double startIntegralTwo, double endIntegralTwo,
                         double startIntegralThree, double endIntegralThree, String textIntegralFunc) {
        Nx = nx;
        Ny = ny;
        Nz = nz;
        this.startIntegralOne = startIntegralOne;
        this.endIntegralOne = endIntegralOne;
        this.startIntegralTwo = startIntegralTwo;
        this.endIntegralTwo = endIntegralTwo;
        this.startIntegralThree = startIntegralThree;
        this.endIntegralThree = endIntegralThree;
        this.textIntegralFunc = textIntegralFunc;
    }

    public double Solution() {
        double result = 0;

        double hx = (endIntegralOne - startIntegralOne) / Nx;
        double hy = (endIntegralTwo - startIntegralTwo) / Ny;
        double hz = (endIntegralThree - startIntegralThree) / Nz;

        double tempHx = startIntegralOne;
        while (tempHx < endIntegralOne) {
            double tempHy = startIntegralTwo;
            while (tempHy < endIntegralTwo) {
                double tempHz = startIntegralThree;
                while (tempHz < endIntegralThree) {
                    Argument x = new Argument("x", leftFindFunc(tempHx, tempHx + hx) +
                            rightFinFunc(tempHx, tempHx + hx));

                    Argument y = new Argument("y", leftFindFunc(tempHy, tempHy + hy) +
                            rightFinFunc(tempHy, tempHy + hy));

                    Argument z = new Argument("z", leftFindFunc(tempHz, tempHz + hz) +
                            rightFinFunc(tempHz, tempHz + hz));

                    double res = findInterval(tempHx, tempHx + hx) * findInterval(tempHy, tempHy + hy) *
                            findInterval(tempHz, tempHz + hz);

                    Expression e = new Expression(textIntegralFunc, x, y, z);

                    result += res * e.calculate();

                    tempHz += 2 * hz;
                }
                tempHy += 2 * hy;
            }
            tempHx += 2 * hx;
        }
        return result;
    }

    private double leftFindFunc(double a, double b) {
        double result = 0;

        result = ((a + b) / 2) - ((b - a) / (2 * Math.sqrt(3)));

        return result;
    }

    private double rightFinFunc(double a, double b) {
        double result = 0;

        result = ((a + b) / 2) + ((b - a) / (2 * Math.sqrt(3)));

        return result;
    }

    private double findInterval(double a, double b) {
        return (b - a) / 2;
    }
}
