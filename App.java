package minimaceraoyunu;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
public class App {
    public static void main(String[] args) {
       try {
           System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
           System.err.println("UTF-8 ayarı başarısız oldu.");
        }
    
        GameEngine game = new GameEngine();
        game.run();
    }
}