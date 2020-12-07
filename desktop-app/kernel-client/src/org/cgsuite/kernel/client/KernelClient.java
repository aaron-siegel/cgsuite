/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.kernel.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.cgsuite.kernel.KernelRequest;
import org.cgsuite.kernel.KernelResponse;
import org.cgsuite.output.Output;
import scala.collection.JavaConverters;

/**
 *
 * @author asiegel
 */
public class KernelClient {

    private final static Logger log = Logger.getLogger(KernelClient.class.getName());

    public static KernelClient client;
    
    static {
        
        client = new KernelClient();
        
    }
    
    Process kernelProcess;
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;
    
    final Object lock = new Object();
    LinkedList<KernelQuery> queue = new LinkedList<>();
    
    public KernelClient() {
        
        try {
            String[] command = new String[] {
                System.getProperty("java.home") + "/bin/java",
                "-cp",
                "build/cluster/modules/ext/cgsuite-core.jar",
                "org.cgsuite.kernel.Kernel"
            };
            kernelProcess = Runtime.getRuntime().exec(command);
            log.info("Kernel process started.");
            in = new ObjectInputStream(kernelProcess.getInputStream());
            log.info("Kernel input stream initialized.");
            out = new ObjectOutputStream(kernelProcess.getOutputStream());
            out.flush();
            log.info("Kernel output stream initialized.");
            /*
            log.info("isAlive: " + kernelProcess.isAlive());
            log.info(System.getProperty("netbeans.user"));
            Properties properties = System.getProperties();
            Iterator<Map.Entry<Object,Object>> elements = properties.entrySet().iterator();
            List<String> entries = new ArrayList<String>();
            while (elements.hasNext()) {
                entries.add(elements.next().toString());
            }
            entries.sort(String.CASE_INSENSITIVE_ORDER);
            for (String entry : entries) {
                log.info(entry);
            }
            // TODO Use a message to indicate readiness
            new Thread(new StreamLogger(kernelProcess.getInputStream())).run();
            new Thread(new StreamLogger(kernelProcess.getErrorStream())).run();
            log.info("Sleeping...");
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException exc) {
            }
            */
            //java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(kernelProcess.getInputStream()));
            //String str;
            //while ((str = reader.readLine()) != null) {
            //    log.info(str);
            //}
            /*
            log.info("isAlive: " + kernelProcess.isAlive());
            log.info("Connecting to socket");
            socket = new Socket("127.0.0.1", port);
            log.info("Creating streams");
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            log.info("Done creating streams");
            */
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
        
        log.info("Hello A");
        new Thread(new KernelDispatcher()).start();
        log.info("Hello B");
                
    }
    
    public synchronized void postRequest(String input, KernelCallback callback) {
        
        queue.add(new KernelQuery(input, callback));
        log.info("Hello there");
        notifyAll();
        
    }
    
    synchronized KernelQuery popQuery() {
        
        log.info("popQuery: " + queue);
        if (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException exc) {
            }
        }

        assert(!queue.isEmpty());
        return queue.pop();
        
    }
    
    class KernelQuery {
        
        String input;
        KernelCallback callback;
         
        public KernelQuery(String input, KernelCallback callback) {
            this.input = input;
            this.callback = callback;
        }
       
    }
    
    class KernelDispatcher implements Runnable {

        @Override
        public void run() {

            while (true) {
                
                final KernelQuery query = popQuery();

                try {
                    KernelRequest request = new KernelRequest(query.input);
                    log.info("Posting request to kernel: " + query.input);
                    out.writeObject(request);
                    out.flush();
                    final KernelResponse response = (KernelResponse) in.readObject();
                    SwingUtilities.invokeLater(() -> query.callback.receive(response));
                } catch (IOException | ClassNotFoundException exc) {
                    throw new RuntimeException(exc);
                }
                
            }
            
        }
        
    }
    
    class StreamLogger implements Runnable {
        
        BufferedReader reader;
        
        StreamLogger(InputStream inputStream) {
            log.info("new stream logger");
            this.reader = new BufferedReader(new InputStreamReader(inputStream));
        }
        
        public void run() {
            log.info("new stream logger");
            String line;
            try {
                while ((line = (reader.readLine())) != null) {
                    log.info(line);
                }
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
        
    }
    
}
