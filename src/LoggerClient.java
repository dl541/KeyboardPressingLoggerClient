import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Scanner;

public class LoggerClient {
    private Socket socket;
    private PrintWriter out;
    private Scanner sc;
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
    public static void main(String[] args) throws IOException {
        LoggerClient t = new LoggerClient();
        t.initializeConnection();
        int logIndex = 0;

        long time = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(time);
        BufferedWriter writer = new BufferedWriter(new FileWriter(String.format("%s.txt",df.format(timestamp))));

        while(t.sc.hasNext()) {
            String receivedMessage = t.sc.nextLine();

            if (receivedMessage.equals("quit")){
                break;
            }

            time = System.currentTimeMillis();
            timestamp = new Timestamp(time);
            String log = String.format("%s\t%s\t%s\n",logIndex,df.format(timestamp), receivedMessage);
            writer.write(log);
            System.out.println(log);
            logIndex += 1;
        }

        writer.close();
    }

    private void initializeConnection(){
        //Create socket connection
        try{
            socket = new Socket("localhost", 38300);
            out = new PrintWriter(socket.getOutputStream(), true);
            //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sc = new Scanner(socket.getInputStream());

            // add a shutdown hook to close the socket if system crashes or exists unexpectedly
            Thread closeSocketOnShutdown = new Thread(() -> {
                try {
                    System.out.println("Connection closed");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Runtime.getRuntime().addShutdownHook(closeSocketOnShutdown);

        } catch (UnknownHostException e) {
            System.err.println("Socket connection problem (Unknown host)" + Arrays.toString(e.getStackTrace()));
        } catch (IOException e) {
            System.err.println("Could not initialize I/O on socket " + Arrays.toString(e.getStackTrace()));
        }
    }
}
