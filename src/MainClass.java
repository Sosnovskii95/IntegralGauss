import Gauss.GaussSolution;

public class MainClass {

    public static void main(String[] args) {
        GaussSolution solution = new GaussSolution(2,2,2,0,1,0,1,0,1,"sin(x)*cos(y)*z^2");
        System.out.println(solution.Solution());
    }
}
