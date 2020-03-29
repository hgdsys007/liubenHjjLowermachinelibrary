package com.higer.lowermachinelibrary.statics;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

final public class TcpContext {
    public static Queue<byte[]> tcpDataList=new LinkedBlockingQueue<>();
}
