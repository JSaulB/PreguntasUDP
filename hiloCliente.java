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
    private static final int TIEMPO_ESPERA = 100; // milisegundos
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
            
            // Arrays para guardar las respuestas del cliente
            String[] respuestasCliente = new String[preguntas.length];
            boolean[] respuestasCorrectas_array = new boolean[preguntas.length];
            
            int puntaje = 0;
            
            // Preparar para escribir en el archivo
            File archivo = new File(RUTA + NOMBRE_ARCHIVO);
            FileWriter fw = new FileWriter(archivo, true); // true para append
            BufferedWriter textoEscribir = new BufferedWriter(fw);
            
            // Escribir información inicial
            Date fechaActual = new Date();
            SimpleDateFormat formato = new SimpleDateFormat("MMM-dd-YYYY HH:mm:ss");
            String fecha = formato.format(fechaActual);
            String IP = direccionCliente.getHostAddress();
            
            textoEscribir.write("\nPREGUNTAS\n");
            textoEscribir.write("Fecha: " + fecha + "\n");
            textoEscribir.write("IP del cliente: " + IP + "\n\n");
            
            // Enviar mensaje inicial
            enviarMensaje("INICIO DEL JUEGO", direccionCliente, puertoCliente);
            Thread.sleep(TIEMPO_ESPERA);
            enviarMensaje("Responde las siguientes " + preguntas.length + " preguntas:", direccionCliente, puertoCliente);
            Thread.sleep(TIEMPO_ESPERA);
            
            // Procesar cada pregunta una por una
            for (int i = 0; i < preguntas.length; i++) {
                // Enviar pregunta actual
                enviarMensaje("PREGUNTA Nro " + (i + 1) + ": " + preguntas[i], direccionCliente, puertoCliente);
                Thread.sleep(TIEMPO_ESPERA);
                
                // Recibir respuesta
                byte[] bufferRespuesta = new byte[1024];
                DatagramPacket paqueteRespuesta = new DatagramPacket(bufferRespuesta, bufferRespuesta.length);
                socketUDP.receive(paqueteRespuesta);
                
                String respuestaRecibida = new String(paqueteRespuesta.getData(), 0, paqueteRespuesta.getLength())
                    .trim().toLowerCase();
                
                // Guardar la respuesta del cliente
                respuestasCliente[i] = respuestaRecibida;
                
                // Verificar respuesta
                if (respuestaRecibida.equals(respuestasCorrectas[i])) {
                    puntaje++;
                    respuestasCorrectas_array[i] = true;
                    enviarMensaje("tu respuesta es: CORRECTA", direccionCliente, puertoCliente);
                } else {
                    respuestasCorrectas_array[i] = false;
                    enviarMensaje("tu respuesta es: INCORRECTA. La respuesta correcta es: " + respuestasCorrectas[i], 
                                direccionCliente, puertoCliente);
                }
                Thread.sleep(TIEMPO_ESPERA);
                
                enviarMensaje("PUNTAJE ACTUAL: " + puntaje + "/" + (i + 1), direccionCliente, puertoCliente);
                Thread.sleep(TIEMPO_ESPERA);
                
                // Escribir la pregunta y respuesta en el archivo
                textoEscribir.write("Pregunta " + (i+1) + ": " + preguntas[i] + "\n");
                textoEscribir.write("Respuesta del cliente: " + respuestasCliente[i] + "\n");
                textoEscribir.write("Respuesta correcta: " + respuestasCorrectas[i] + "\n");
                textoEscribir.write("¿Fue correcta? " + (respuestasCorrectas_array[i] ? "Sí" : "No") + "\n\n");
            }
            
            // Escribir resumen final en el archivo
            textoEscribir.write("RESUMEN DE LAS PREGUNTAS\n");
            textoEscribir.write("Puntaje total: " + puntaje + "/" + preguntas.length + "\n");
            textoEscribir.write("================================\n\n");
            
            // Cerrar el archivo
            textoEscribir.close();
            
            // Enviar mensajes finales
            enviarMensaje("SE ACABÓ EL JUEGO", direccionCliente, puertoCliente);
            Thread.sleep(TIEMPO_ESPERA);
            enviarMensaje("Puntaje final: " + puntaje + "/" + preguntas.length, direccionCliente, puertoCliente);
            Thread.sleep(TIEMPO_ESPERA);
            
            // Mensaje según el puntaje
            if (puntaje == 5) {
                enviarMensaje("¡Excelente! Respondiste todas las preguntas correctamente", direccionCliente, puertoCliente);
            } else if (puntaje == 4) {
                enviarMensaje("¡Muy bien! Te equivocaste en una, ánimo", direccionCliente, puertoCliente);
            } else if (puntaje == 3) {
                enviarMensaje("Regular. Te equivocaste en dos, todavía puedes mejorar", direccionCliente, puertoCliente);
            } else {
                enviarMensaje("Tienes que estudiar un poco más", direccionCliente, puertoCliente);
            }
            Thread.sleep(TIEMPO_ESPERA);
            
            enviarMensaje("Gracias por participar!", direccionCliente, puertoCliente);
            Thread.sleep(TIEMPO_ESPERA);
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