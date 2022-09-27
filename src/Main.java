
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {


    public static void main(String[] args) throws IOException {
        String file = "./../data/kittens.txt";
        Parser parser = new Parser(file);
        parser.setupData();
        parser.solver();
        parser.printResult();
    }


}
