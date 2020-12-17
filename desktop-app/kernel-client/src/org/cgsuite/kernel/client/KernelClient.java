/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cgsuite.kernel.client;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.cgsuite.kernel.KernelRequest;
import org.cgsuite.kernel.KernelResponse;
import org.cgsuite.output.StyledTextOutput;
import scala.collection.JavaConverters;

/**
 *
 * @author asiegel
 */
public class KernelClient {

    private final static Logger logger = Logger.getLogger(KernelClient.class.getName());

    public static KernelClient client;
    
    static {
        
        client = new KernelClient();
        
    }
    
    Process kernelProcess;
    Thread kernelDispatchThread;
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;
    
    boolean resetting = false;
    LinkedList<KernelQuery> queue = new LinkedList<>();
    
    public KernelClient() {
       
        startKernel();
        
    }
    
    public synchronized void postRequest(String input, KernelCallback callback) {
        
        queue.add(new KernelQuery(input, callback));
        notifyAll();
        
    }
    
    synchronized KernelQuery peekQuery() {
        
        return queue.peek();
        
    }
    
    synchronized KernelQuery popQuery() {
        
        return queue.pop();
        
    }
    
    synchronized boolean isQueryAvailable() {
        
        return !queue.isEmpty();
        
    }
    
    synchronized KernelQuery waitForQuery() throws InterruptedException {
        
        if (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException exc) {
            }
        }

        return queue.peek();
        
    }
    
    void startKernel() {
         
        try {
            
            String[] command = new String[] {
                System.getProperty("java.home") + "/bin/java",
                "-Xmx" + KernelOptions.getHeapSizeMb() + "m",
                "-Xss4m",
                "-cp",
                "build/cluster/modules/ext/cgsuite-core.jar",
                "org.cgsuite.kernel.Kernel"
            };
            kernelProcess = Runtime.getRuntime().exec(command);
            logger.info("Kernel process started.");
            in = new ObjectInputStream(kernelProcess.getInputStream());
            logger.info("Kernel input stream initialized.");
            out = new ObjectOutputStream(kernelProcess.getOutputStream());
            out.flush();
            logger.info("Kernel output stream initialized.");
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
        
        kernelDispatchThread = new Thread(new KernelDispatcher());
        kernelDispatchThread.start();

    }
    
    public void resetKernel() {
        
        logger.info("Resetting kernel.");
        resetting = true;
        try {
            logger.info("Destroying kernel process ...");
            kernelProcess.destroyForcibly().waitFor();
        } catch (InterruptedException exc)
        {
        }
        try {
            logger.info("Waiting for dispatch thread to finish ...");
            synchronized (this) {
                notifyAll();
            }
            kernelDispatchThread.join();
        } catch (InterruptedException exc)
        {
        }
        resetting = false;
        startKernel();
        
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
            
            while (!resetting) {

                try {
                    final KernelQuery query = waitForQuery();
                    if (!resetting) {
                        assert(query != null);
                        KernelRequest request = new KernelRequest(query.input);
                        logger.log(Level.INFO, "Posting request to kernel: {0}", query.input);
                        long startTime = System.currentTimeMillis();
                        out.writeObject(request);
                        out.flush();
                        boolean done = false;
                        while (!done) {
                            KernelResponse response = (KernelResponse) in.readObject();
                            if (response.output() == null) {
                                assert response.exc() != null && response.isFinal();
                                final KernelDispatch dispatch = new KernelDispatch("Kernel error.", response.isFinal());
                                SwingUtilities.invokeLater(() -> {
                                    showErrorDialog(response.exc(), false);
                                    query.callback.receive(dispatch);
                                });
                                done = true;
                            } else {
                                final KernelDispatch dispatch = new KernelDispatch(JavaConverters.seqAsJavaList(response.output()), response.isFinal());
                                SwingUtilities.invokeLater(() -> query.callback.receive(dispatch));
                                done = response.isFinal();
                            }
                        }
                        long duration = System.currentTimeMillis() - startTime;
                        logger.log(Level.INFO, "Calculation completed in {0} milliseconds.", duration);
                        popQuery();
                    }
                } catch (IOException | ClassNotFoundException | InterruptedException exc) {
                    if (!resetting) {
                        SwingUtilities.invokeLater(() -> showErrorDialog(exc, true));
                        break;
                    }
                }
                
            }
            
            logger.info("Kernel dispatch thread received reset; shutting down.");
            
            final KernelDispatch dispatch = new KernelDispatch(Collections.singletonList(new StyledTextOutput("Kernel was reset.")), true);
            while (isQueryAvailable()) {
                final KernelQuery query = popQuery();
                SwingUtilities.invokeLater(() -> query.callback.receive(dispatch));
            }
            
        }
        
    }
    
    public void showErrorDialog(Throwable exc, boolean unexpectedShutdown) {
        
        String message;
        String details = null;
        
        if (unexpectedShutdown) {
            
            message =
                "<html>The kernel shut down unexpectedly. This may be due<br>" +
                "to a bug in CGSuite. Please file a bug report at:<br>" +
                "<a href=\"http://www.cgsuite.org/bugs\">http://www.cgsuite.org/bugs</a><br>&nbsp;<br>" +
                "Details of the crash can be found below. You will need to reset<br>" +
                "the kernel (select \"Reset Kernel\" from the System menu).</html>";
            
        } else if (exc instanceof OutOfMemoryError) {
            
            message =
                "<html>The kernel has run out of memory. This can happen<br>" +
                "when running a large calculation. You can increase the amount<br>" +
                "of memory assigned to the kernel in the \"Kernel\" tab of the<br>" +
                "CGSuite Preferences window.<br>&nbsp;<br>" +
                "The kernel may be in an unstable state, and a Kernel Reset is<br>" +
                "recommended (select \"Reset Kernel\" from the System menu).</html>";
            
        } else {
        
            message =
                "<html>The kernel encountered an unexpected error. This may be due<br>" +
                "to a bug in CGSuite. Please file a bug report at:<br>" +
                "<a href=\"http://www.cgsuite.org/bugs\">http://www.cgsuite.org/bugs</a><br>&nbsp;<br>" +
                "Details of the error can be found below.</html>";
            details = ExceptionUtils.getStackTrace(exc);
            
        }
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(message));
        
        if (details != null) {
            
            JScrollPane detailsPane = new JScrollPane();
            JTextArea detailsArea = new JTextArea(details);
            detailsArea.setEditable(false);
            detailsPane.setViewportView(detailsArea);
            detailsPane.setMaximumSize(new Dimension(1000, 200));
            detailsPane.setPreferredSize(new Dimension(1000, 200));
            
            panel.add(Box.createVerticalStrut(20));
            panel.add(detailsPane);
            
        }
        
        JOptionPane.showMessageDialog(
            null,
            panel,
            "Kernel Error",
            JOptionPane.ERROR_MESSAGE
        );
        
    }
    
    class StreamLogger implements Runnable {
        
        BufferedReader reader;
        
        StreamLogger(InputStream inputStream) {
            logger.info("new stream logger");
            this.reader = new BufferedReader(new InputStreamReader(inputStream));
        }
        
        public void run() {
            logger.info("new stream logger");
            String line;
            try {
                while ((line = (reader.readLine())) != null) {
                    logger.info(line);
                }
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
        
    }
    
}
