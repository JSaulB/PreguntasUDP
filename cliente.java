package Preguntas_UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class cliente {
    public static void main(String[] args) {
        try {
            DatagramSocket socketUDP = new DatagramSocket();
            InetAddress direccionServidor = InetAddress.getByName("localhost");
            Scanner scanner = new Scanner(System.in);
            
            // Enviar mensaje inicial para establecer conexión
            String mensajeInicial = "Conectar";
            byte[] bufferEnvio = mensajeInicial.getBytes();
            DatagramPacket paqueteEnvio = new DatagramPacket(
                bufferEnvio, bufferEnvio.length, direccionServidor, 5000);
            socketUDP.send(paqueteEnvio);
            
            while (true) {
                // Recibir mensaje del servidor
                byte[] bufferRecepcion = new byte[1024];
                DatagramPacket paqueteRecepcion = new DatagramPacket(bufferRecepcion, bufferRecepcion.length);
                socketUDP.receive(paqueteRecepcion);
                
                String mensajeRecibido = new String(paqueteRecepcion.getData(), 0, paqueteRecepcion.getLength());
                
                if (mensajeRecibido.equals("Conexion terminada")) {
                    break;
                }
                
                System.out.println(mensajeRecibido);
                
                if (mensajeRecibido.startsWith("PREGUNTA Nro")) {
                    System.out.print("Tu respuesta: ");
                    String respuesta = scanner.nextLine();
                    
                    // Enviar respuesta al servidor
                    bufferEnvio = respuesta.getBytes();
                    paqueteEnvio = new DatagramPacket(
                        bufferEnvio, bufferEnvio.length, direccionServidor, 5000);
                    socketUDP.send(paqueteEnvio);
                }
            }
            
            scanner.close();
            socketUDP.close();
            
        } catch (Exception e) {
            System.out.println("Error cliente");
            e.printStackTrace();
        }
    }
}