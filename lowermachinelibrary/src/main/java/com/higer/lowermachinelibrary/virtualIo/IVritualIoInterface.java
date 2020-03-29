package com.higer.lowermachinelibrary.virtualIo;

public interface IVritualIoInterface {
     void init();
     void output(byte[] data);
     byte[] read();
     void write(byte[] data);
}
