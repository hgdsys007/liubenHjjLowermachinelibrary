package com.higer.lowermachinelibrary.functionBlock;

import java.io.OutputStream;

public interface IWriter {
    void setOutputStream(OutputStream outputStream);
    void write(byte[] data,int iLen) throws Exception;
    void write(String s) throws Exception;
    void writeInteger(int i) throws Exception;
    void writeItoS(int i) throws Exception;
}
