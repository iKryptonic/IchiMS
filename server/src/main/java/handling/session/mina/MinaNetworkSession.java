package handling.session.mina;

import handling.session.NetworkSession;
import org.apache.mina.common.IoSession;

import java.net.SocketAddress;

public class MinaNetworkSession implements NetworkSession {

    private final IoSession ioSession;

    public MinaNetworkSession(IoSession ioSession) {
        this.ioSession = ioSession;
    }

    @Override
    public void write(byte[] packet) {
        ioSession.write(packet);
    }

    @Override
    public void close() {
        ioSession.close();
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return ioSession.getRemoteAddress();
    }

    @Override
    public boolean isConnected() {
        return ioSession.isConnected();
    }


}
