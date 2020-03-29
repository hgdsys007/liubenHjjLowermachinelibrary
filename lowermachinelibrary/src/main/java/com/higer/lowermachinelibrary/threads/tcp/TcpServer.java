package com.higer.lowermachinelibrary.threads.tcp;

import com.higer.lowermachinelibrary.adas.AdasService;
import com.higer.lowermachinelibrary.threads.BaseThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

final public class TcpServer extends BaseThread {
    private AdasService currentWorkAdas=null;

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);// 本机IP  192.168.100.100
            while (isWork()) {
                System.out.println("Server开始~~~监听~~~");
                // accept方法会阻塞，直到有客户端与之建立连接
                Socket socket = serverSocket.accept();
                System.out.println("===================================");
                if(socket!=null)
                {
                    if(currentWorkAdas!=null)
                    {
                        currentWorkAdas.stopWork();
                        currentWorkAdas=null;
                    }
                    System.out.println("接收到连接请求--=------------");
                }
                currentWorkAdas = new AdasService(socket);
                currentWorkAdas.startWork();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
