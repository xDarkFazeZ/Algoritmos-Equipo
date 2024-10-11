import java.io.*;
import java.net.*;
import java.util.PriorityQueue;

public class LamportServer {
    private static int logicalClock = 0;
    private static PriorityQueue<ClientRequest> requestQueue = new PriorityQueue<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Servidor iniciado y esperando conexiones...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void incrementClock(int receivedTimestamp) {
        logicalClock = Math.max(logicalClock, receivedTimestamp) + 1;
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                // Recibir la solicitud del cliente
                String inputLine = in.readLine();
                String[] parts = inputLine.split(",");
                int clientId = Integer.parseInt(parts[0]);
                int clientTimestamp = Integer.parseInt(parts[1]);

                // Ajustar el reloj lógico con el algoritmo de Lamport
                incrementClock(clientTimestamp);
                int serverTimestamp = logicalClock;

                // Añadir a la cola de solicitudes
                requestQueue.add(new ClientRequest(clientId, serverTimestamp));

                // Enviar respuesta al cliente con el timestamp del servidor
                out.println("Solicitud recibida, reloj del servidor ajustado a: " + serverTimestamp);

                // Procesar la solicitud en orden de llegada
                processRequests();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private synchronized void processRequests() {
            while (!requestQueue.isEmpty()) {
                ClientRequest request = requestQueue.poll();
                System.out.println("Procesando solicitud del cliente " + request.clientId + " con timestamp " + request.timestamp);
            }
        }
    }

    private static class ClientRequest implements Comparable<ClientRequest> {
        int clientId;
        int timestamp;

        public ClientRequest(int clientId, int timestamp) {
            this.clientId = clientId;
            this.timestamp = timestamp;
        }

        @Override
        public int compareTo(ClientRequest other) {
            return Integer.compare(this.timestamp, other.timestamp);
        }
    }
}
