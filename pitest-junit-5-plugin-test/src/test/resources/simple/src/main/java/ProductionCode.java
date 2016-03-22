import java.util.Random;

public class ProductionCode {

    int x = 10;

    boolean ran = false;

    public static void main(String[] args) {
        new ProductionCode().calculateOutput();
    }

    void calculateOutput() {
        x = new Random().nextInt();
        if(x < 5) {
            System.out.println("Hello World");
        } else {
            ran = true;
        }
    }

}