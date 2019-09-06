package com.arep;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {

    private static ArrayList<String> imgext = new ArrayList<>(Arrays.asList("jpg","png","img","gif"));
    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;


    public static void main(String[] args) throws IOException {
        while (true) {

            try {
                serverSocket = new ServerSocket(getPort());
            } catch (IOException e) {
                System.err.println("Could not listen on port: " + getPort());
                System.exit(1);
            }
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            ResolveRequest(clientSocket.getOutputStream());

            clientSocket.close();
            serverSocket.close();
        }


    }
    private static void ResolveRequest(OutputStream out) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String line = null;
        String path = null;
        while((line=bf.readLine())!=null) {
            //System.out.println(line);
            if (!bf.ready()) break;
            if (line.contains("GET")) {
                String [] splitedLine = line.split(" ");
                path =splitedLine[1];
            }
        }
        if(path!=null) {
            serve(path, out);
        }

    }

    private static void serve(String path, OutputStream out) {
        String ext = null;
        String [] splited =path.split("/");
        if (path.length() > 3)
        {
            ext = path.substring(path.length() - 3);
        }
        if(imgext.contains(ext)){
            try {
                serveImage(path,out,ext);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(splited.length>1 &&splited[1].equals("apps")){
            serveApp(splited,out);
        }
        else{
            serveHtml(path, out);

        }
    }

    private static void serveApp(String[] path, OutputStream out) {


            try {
                Class clase = Class.forName("com.arep.apps."+path[2]);
                Boolean params = true;
                String response = null;
                String[] splited = null;
                try {
                    if(path.length==5){
                        splited = path[4].split("&");
                    }
                    else {
                        params=false;
                    }
                    ArrayList<Method> metodos=new ArrayList<>(Arrays.asList(clase.getMethods()));
                    HashMap<String,Method> map = new HashMap<>();
                    for(Method m: metodos) {
                        map.put(m.getName(),m);
                    }
                    if(params){
                        response = (String) map.get(path[3]).invoke(clase.newInstance(), splited);
                    }
                    else{
                        response = (String) map.get(path[3]).invoke(clase.newInstance());
                    }
                    System.out.println(response);
                    PrintStream responseWeb = new PrintStream(out);
                    DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm:ss z");
                    responseWeb.println("HTTP/1.1 200 OK\r\n"+"Content-Type: text/html\r\n"+"\r\n");
                    if(params) {
                        responseWeb.println("The Result of " + path[2] + "." + path[3] + "(" + path[4].replace("&", ",") + ") is: " + response);
                    }
                    else{
                        responseWeb.println("The Result of " + path[2] + "." + path[3] + "() is: " + response);
                    }
                    responseWeb.flush();
                    responseWeb.close();
                }catch (Exception ex){
                    notFound(out);
                }
            } catch (Exception e) {
                notFound(out);
            }

    }

    private static void serveHtml(String path, OutputStream out) {
        Scanner scanner = null;
        try {
            scanner = new Scanner( new File(System.getProperty("user.dir"),"src/main/resources/"+path));
            String htmlString = scanner.useDelimiter("\\Z").next();
            scanner.close();
            byte htmlBytes[] = htmlString.getBytes("UTF-8");
            PrintStream response = new PrintStream(out);
            response.println("HTTP/1.1 200 OK\r\n"+"Content-Type: text/html\r\n"+"\r\n");
            response.println();
            response.println(htmlString);
            response.flush();
            response.close();

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            notFound(out);

        }
    }

    private static void notFound(OutputStream out) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(System.getProperty("user.dir"),"src/main/resources/NOTFOUND.html"));
            String htmlString = scanner.useDelimiter("\\Z").next();
            scanner.close();
            byte htmlBytes[] = htmlString.getBytes("UTF-8");
            PrintStream response = new PrintStream(out);
            response.println("HTTP/1.1 200 OK\r\n"+"Content-Type: text/html\r\n"+"\r\n");
            response.println();
            response.println(htmlString);
            response.flush();
            response.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    private static void serveImage(String path,OutputStream outputStream,String ext) throws IOException {
        PrintWriter response = new PrintWriter(outputStream, true);
        try{
            response.println("HTTP/1.1 200 OK");
            response.println("Content-Type: image/"+ext+"\r\n");
            BufferedImage image= ImageIO.read(new File(System.getProperty("user.dir"),"src/main/resources/"+path));

            ImageIO.write(image, ext, new MemoryCacheImageOutputStream(outputStream));
            response.flush();
            response.close();
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            BufferedImage image= ImageIO.read(new File(System.getProperty("user.dir"),"src/main/resources/imagenes/error.png"));
            ImageIO.write(image, ext, outputStream);
            response.flush();
            response.close();
        }
    }


    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 8080; //returns default port if heroku-port isn't set (i.e.on localhost)
    }

}