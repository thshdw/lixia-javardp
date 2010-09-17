

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

/**
 * Servlet implementation class WebSocketChatServlet
 */
public class WebSocketChatServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
     
	private final Set<ChatWebSocket> _members = new CopyOnWriteArraySet<ChatWebSocket>();
	
    /**
     * @see WebSocketServlet#WebSocketServlet()
     */
    public WebSocketChatServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getServletContext().getNamedDispatcher("default").forward(request,response);
	}

	@Override
	protected WebSocket doWebSocketConnect(HttpServletRequest arg0, String arg1) {
		return new ChatWebSocket();
	}

	class ChatWebSocket implements WebSocket
    {
        Outbound _outbound;

        public void onConnect(Outbound outbound)
        {
            _outbound=outbound;
            _members.add(this);
        }
        
        public void onMessage(byte frame, byte[] data,int offset, int length)
        {}

        public void onMessage(byte frame, String data)
        {
            for (ChatWebSocket member : _members)
            {
                try
                {
                    member._outbound.sendMessage(frame,data);
                }
                catch(IOException e) {}
            }
        }

        public void onDisconnect()
        {
            _members.remove(this);
        }
    }
}
