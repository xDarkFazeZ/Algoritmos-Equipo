import java.io.*;
import java.net.*;
import java.util.concurrent.Semaphore;
import java.util.Date;

public class ClienteExclusionMutua {

    private static final Semaphore semaphore = new Semaphore(1); // Exclusión mutua usando un semáforo

    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 8080;

        try {
            // Solicitar el token antes de intentar conectarse al servidor
            System.out.println("Cliente está esperando el token para conectarse...");
            semaphore.acquire();  // Adquirir el semáforo

            // Conexión al servidor después de adquirir el token
            Socket socket = new Socket(host, puerto);
            System.out.println("Conectado al servidor después de obtener el token.");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            int clientId = (int) (Math.random() * 1000);
            long timestamp = new Date().getTime();

            // Enviar tipo de cliente, ID y timestamp al servidor
            out.println("ExclusionMutua:" + clientId + ":" + timestamp);
            System.out.println("Cliente ExclusionMutua " + clientId + " ha enviado la solicitud (Timestamp: " + timestamp + ")");

            String respuestaServidor = in.readLine();
            System.out.println("Respuesta del servidor: " + respuestaServidor);

            if ("Atendido".equals(respuestaServidor)) {
                System.out.println("Cliente Exlusion Mutua con ID " + clientId + " está siendo atendido.");
            } else {
                System.out.println("Cliente Exlusion Mutua con ID " + clientId + " no pudo ser atendido. Se retira.");
            }

            semaphore.release();  // Liberar el semáforo
            System.out.println("Cliente " + clientId + " ha liberado el token.");

            socket.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
