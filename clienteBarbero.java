import java.io.*;
import java.net.*;
import java.util.Date;

public class clienteBarbero {

    public static void main(String[] args) {
        String host = "localhost"; // Dirección del servidor
        int puerto = 8080; // Puerto del servidor

        try (Socket socket = new Socket(host, puerto)) {
            System.out.println("Conectado al servidor en " + host + ":" + puerto);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            int clientId = (int) (Math.random() * 1000);
            long timestamp = new Date().getTime();

            // Enviar tipo de cliente, ID y timestamp al servidor
            out.println("Aglrotimo Barbero:" + clientId + ":" + timestamp);
            System.out.println("Cliente con Algoritmo de Barbero y con ID " + clientId + " ha enviado la solicitud (Timestamp: " + timestamp + ")");

            String respuestaServidor = in.readLine();
            System.out.println("Respuesta del servidor: " + respuestaServidor);

            if ("Atendido".equals(respuestaServidor)) {
                System.out.println("Cliente con Algoritmo de Barbero y con ID " + clientId + " está siendo atendido.");
            } else {
                System.out.println("Cliente con Algoritmo de Barbero y con ID " + clientId + " no pudo ser atendido. Se retira.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
