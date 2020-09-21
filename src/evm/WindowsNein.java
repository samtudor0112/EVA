package evm;

/**
 * evm.Main class to run program
 * Cant launch a jar with a main class that extends Application thus this
 * wrapper main class
 */
public class WindowsNein {
    public static void main(String[] args) {
        String[] a = new String[] {"program_name", "config/config.txt"};
        Main.main(a);
    }
}
