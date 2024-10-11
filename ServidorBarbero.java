import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.PriorityQueue;

public class ServidorBarbero {

    private static final int NUMERO_SILLAS = 3; // Número de sillas disponibles en la sala de espera
    private static PriorityQueue<ClientRequest> requestQueue = new PriorityQueue<>();
    private static int sillasDisponibles = NUMERO_SILLAS;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Servidor de barbero iniciado y esperando conexiones en el puerto 8080...");

            while (true) {
                // Aceptar una nueva conexión de cliente
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String clientMessage = in.readLine();

                // Extraer tipo de cliente, ID y timestamp del cliente
                String[] parts = clientMessage.split(":");
                String clientType = parts[0];
                int clientId = Integer.parseInt(parts[1]);
                long clientTimestamp = Long.parseLong(parts[2]);

                System.out.println("Cliente de tipo: " + clientType + " con ID: " + clientId + " se ha conectado.");

                synchronized (requestQueue) {
                    if (sillasDisponibles > 0) {
                        // Hay sillas disponibles, cliente puede esperar
                        sillasDisponibles--;
                        System.out.println("Cliente " + clientId + " se sienta en una silla de espera.");
                        requestQueue.add(new ClientRequest(clientId, clientTimestamp));

                        // Atender al cliente y enviar mensaje de "Atendido"
                        System.out.println("Atendiendo al Cliente " + clientId + "...");
                        out.println("Atendido");

                        // Simulación de cortar el pelo (tiempo que tarda el barbero)
                        Thread.sleep(1000);

                        System.out.println("Cliente " + clientId + " ha sido atendido y se va.");
                        sillasDisponibles++; // Liberar silla después de atender

                    } else {
                        // No hay sillas disponibles, cliente se retira
                        System.out.println("Cliente " + clientId + " se va porque no hay sillas disponibles.");
                        out.println("Rechazado");
                    }
                }

                // Cerrar la conexión con el cliente
                clientSocket.close();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class ClientRequest implements Comparable<ClientRequest> {
        int clientId;
        long timestamp;

        public ClientRequest(int clientId, long timestamp) {
            this.clientId = clientId;
            this.timestamp = timestamp;
        }

        @Override
        public int compareTo(ClientRequest other) {
            return Long.compare(this.timestamp, other.timestamp);
        }
    }
}
