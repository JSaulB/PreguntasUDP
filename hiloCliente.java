package Preguntas_UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class hiloCliente extends Thread {
    private DatagramSocket socketUDP;
    private DatagramPacket paqueteCliente;
    private static final String RUTA = "C:/Users/APP DISTRIBUIDAS/Documents/";
    private static final String NOMBRE_ARCHIVO = "respuestasClientes.txt";
    
    public hiloCliente(DatagramSocket socketUDP, DatagramPacket paqueteCliente) {
        this.socketUDP = socketUDP;
        this.paqueteCliente = paqueteCliente;
    }
    
    public void run() {
        try {
            InetAddress direccionCliente = paqueteCliente.getAddress();
            int puertoCliente = paqueteCliente.getPort();
            
            // Preguntas y respuestas
            String[] preguntas = {
                "¿En que año el humano llegó a la luna?",
                "¿Cuál es la capital de Alemania?",
                "¿Cuál es la fórmula Química del Agua?",
                "¿En que año se descubrió America?",
                "¿En qué país esta la estatua de la libertad?"
            };
            
            String[] respuestasCorrectas = {
                "1969",
                "berlin",
                "h2o",
                "1492",
                "estados unidos"
            };
            
            String[] respuestasCliente = new String[preguntas.length];
            boolean[] respuestasCorrectas_array = new boolean[preguntas.length];
            
            int puntaje = 0;
            
            File archivo = new File(RUTA + NOMBRE_ARCHIVO);
            FileWriter fw = new FileWriter(archivo, true); 
            BufferedWriter textoEscribir = new BufferedWriter(fw);
            
            Date fechaActual = new Date();
            SimpleDateFormat formato = new SimpleDateFormat("MMM-dd-YYYY HH:mm:ss");
            String fecha = formato.format(fechaActual);
            String IP = InetAddress.getLocalHost().getHostAddress();
            
            textoEscribir.write("\nPREGUNTAS\n");
            textoEscribir.write("Fecha: " + fecha + "\n");
            textoEscribir.write("IP del cliente: " + IP + "\n\n");
            
            enviarMensaje("INICIO DEL JUEGO", direccionCliente, puertoCliente);
            enviarMensaje("Responde las siguientes " + preguntas.length + " preguntas:", direccionCliente, puertoCliente);
            
            for (int i = 0; i < preguntas.length; i++) {
                enviarMensaje("PREGUNTA Nro " + (i + 1) + ": " + preguntas[i], direccionCliente, puertoCliente);
                
                byte[] bufferRespuesta = new byte[1024];
                DatagramPacket paqueteRespuesta = new DatagramPacket(bufferRespuesta, bufferRespuesta.length);
                socketUDP.receive(paqueteRespuesta);
                
                String respuestaRecibida = new String(paqueteRespuesta.getData(), 0, paqueteRespuesta.getLength())
                    .trim().toLowerCase();
                
                respuestasCliente[i] = respuestaRecibida;
                
                if (respuestaRecibida.equals(respuestasCorrectas[i])) {
                    puntaje++;
                    respuestasCorrectas_array[i] = true;
                    enviarMensaje("tu respuesta es: CORRECTA", direccionCliente, puertoCliente);
                } else {
                    respuestasCorrectas_array[i] = false;
                    enviarMensaje("tu respuesta es: INCORRECTA. La respuesta correcta es: " + respuestasCorrectas[i], 
                                direccionCliente, puertoCliente);
                }
                
                enviarMensaje("PUNTAJE ACTUAL: " + puntaje + "/" + (i + 1), direccionCliente, puertoCliente);
                
                textoEscribir.write("Pregunta " + (i+1) + ": " + preguntas[i] + "\n");
                textoEscribir.write("Respuesta del cliente: " + respuestasCliente[i] + "\n");
                textoEscribir.write("Respuesta correcta: " + respuestasCorrectas[i] + "\n");
                textoEscribir.write("¿Fue correcta? " + (respuestasCorrectas_array[i] ? "Sí" : "No") + "\n\n");
            }
            
            textoEscribir.write("RESUMEN DE LAS PREGUNTAS\n");
            textoEscribir.write("Puntaje total: " + puntaje + "/" + preguntas.length + "\n");
            textoEscribir.write("================================\n\n");
            
            textoEscribir.close();
            
            enviarMensaje("SE ACABÓ EL JUEGO", direccionCliente, puertoCliente);
            enviarMensaje("Puntaje final: " + puntaje + "/" + preguntas.length, direccionCliente, puertoCliente);
            
            if (puntaje == 5) {
                enviarMensaje("¡Excelente! Respondiste todas las preguntas correctamente", direccionCliente, puertoCliente);
            } else if (puntaje == 4) {
                enviarMensaje("¡Muy bien! Te equivocaste en una, ánimo", direccionCliente, puertoCliente);
            } else if (puntaje == 3) {
                enviarMensaje("Regular. Te equivocaste en dos, todavía puedes mejorar", direccionCliente, puertoCliente);
            } else {
                enviarMensaje("Tienes que estudiar un poco más", direccionCliente, puertoCliente);
            }
            
            enviarMensaje("Gracias por participar!", direccionCliente, puertoCliente);
            enviarMensaje("Conexion terminada", direccionCliente, puertoCliente);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void enviarMensaje(String mensaje, InetAddress direccion, int puerto) throws Exception {
        byte[] bufferEnvio = mensaje.getBytes();
        DatagramPacket paqueteEnvio = new DatagramPacket(bufferEnvio, bufferEnvio.length, direccion, puerto);
        socketUDP.send(paqueteEnvio);
    }
}