import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;


public class CounterSocketServlet extends WebSocketServlet {

	@Override
	protected WebSocket doWebSocketConnect(HttpServletRequest arg0, String arg1) {
		return new CounterSocket();
	}

	final class CounterSocket implements WebSocket {

        private Outbound outbound;

        public void onConnect(final Outbound outbound) {
            System.out.println("onConnect");
            this.outbound = outbound;
        }
        
//        http://www.eb163.com/club/thread-6565-1-1.html
        //
        //http://tech.kaazing.com/documentation/howto-java.html 
//        How to Use the Kaazing Client Libraries in Java
        
        public void onMessage(final byte frame, final String data) {
//            System.out.println("onMessage");

            if (data.equals("Hello, Server!")) {
                new Thread() {

                    @Override
                    public void run() {
                        try {
                            outbound.sendMessage(frame, "Hello, browser :-)");

                            int i = 0;
                            while (true) {
                                sleep(1000);
                                outbound.sendMessage(frame, String.valueOf(i++));
                            }

                        } catch (final Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }.start();
            }
        }

        public void onMessage(final byte frame, final byte[] data,
                              final int offset, final int length) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void onDisconnect() {
            System.out.println("onDisconnect");
        }
    }
}
