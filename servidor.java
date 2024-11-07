package Preguntas_UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class servidor {
    public static void main(String[] args) {
        try {
            DatagramSocket socketUDP = new DatagramSocket(5000);
            System.out.println("Servidor UDP iniciado en puerto 5000");

            // Buffer para recibir datos
            byte[] bufferRecepcion = new byte[1024];
            DatagramPacket paqueteRecepcion = new DatagramPacket(bufferRecepcion, bufferRecepcion.length);
            
            // Esperar conexión de cliente
            socketUDP.receive(paqueteRecepcion);
            
            // Crear un único hilo para manejar este cliente
            hiloCliente hiloCliente = new hiloCliente(socketUDP, paqueteRecepcion);
            hiloCliente.start();
            
            // Esperar a que el hilo termine
            hiloCliente.join();
            
            // Cerrar el socket
            socketUDP.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}