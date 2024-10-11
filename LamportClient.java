import java.io.*;
import java.net.*;
import java.util.Random;

public class LamportClient {
    private static int logicalClock = 0;
    private static int clientId;

    public static void main(String[] args) {
        clientId = new Random().nextInt(1000); // ID aleatorio para el cliente
        try (Socket socket = new Socket("localhost", 8080)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Incrementar el reloj lógico antes de enviar la solicitud
            logicalClock++;
            String request = clientId + "," + logicalClock;
            out.println(request);

            // Leer la respuesta del servidor
            String serverResponse = in.readLine();
            System.out.println("Respuesta del servidor: " + serverResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
