package com.ja7ude.aprs.w2aprso;

import android.hardware.usb.UsbDeviceConnection;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import java.io.IOException;

/**
 * Created by ooba on 2015/01/10.
 */
public class Ftdi {
    //private static final String TAG = "FTDI";
    FT_Device ftDevice = null;
    private int intBaudRate = 0;
    private int intDataBits = 0;

    private UsbDeviceConnection mConnection = null;

    //The constructor
    Ftdi(){
    }

    public boolean initialize(UsbDeviceConnection connection){
        D2xxManager d2manager;
        if( connection == null )
            return false;
        mConnection = connection;
        //mConnection.controlTransfer(REQTYPE_HOST_TO_DEVICE, CP2101_UART, UART_ENABLE, 0, null, 0, 100);
        try {
            d2manager = D2xxManager.getInstance(MainActivity.mainActivity);
        } catch (D2xxManager.D2xxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        int devCount = d2manager.createDeviceInfoList(MainActivity.mainActivity);
        if( devCount == 0 )
            return false;
        ftDevice = d2manager.openByIndex(MainActivity.mainActivity, 0);
        if( ftDevice == null )
            return false;
        return true;
    }

    public void disable(){
        if( ftDevice != null )
            ftDevice.close();
    }

    public void setup(UsbSerialDriver.BaudRate r, UsbSerialDriver.DataBits D, UsbSerialDriver.StopBits S, UsbSerialDriver.Parity P, UsbSerialDriver.FlowControl F) throws IOException {
        if( mConnection == null || ftDevice == null )
            throw new IOException("Connection closed");
        //ftDevice.setBitMode((byte)0, D2xxManager.FT_BITMODE_RESET);
        ftDevice.setBitMode((byte)0, D2xxManager.FT_BITMODE_FAST_SERIAL);
        int baud;
        switch(r){
            case B0: baud = 0; break;
            case B50: baud = 50; break;
            case B75: baud = 75; break;
            case B150: baud = 150; break;
            case B300: baud = 300; break;
            case B600: baud = 600; break;
            case B1200: baud = 1200; break;
            case B1800: baud = 1800; break;
            case B2400: baud = 2400; break;
            case B4800: baud = 4800; break;
            case B9600: baud = 9600; break;
            case B19200: baud = 19200; break;
            case B38400: baud = 38400; break;
            case B57600: baud = 57600; break;
            case B115200: baud = 115200; break;
            case B230400: baud = 230400; break;
            case B460800: baud = 460800; break;
            case B500000: baud = 500000; break;
            case B576000: baud = 576000; break;
            case B921600: baud = 921600; break;
            default: throw new IOException("Baudrate not supported");
        }
        ftDevice.setBaudRate(baud);
        intBaudRate = baud;
        byte dataBits = 0;
        byte stopBits = 0;
        byte parity = 0;
        switch(D){
            case D7: dataBits = D2xxManager.FT_DATA_BITS_7; break;
            case D8: dataBits = D2xxManager.FT_DATA_BITS_8; break;
            default: throw new IOException("Data bits not supported");
        }
        intDataBits = dataBits;
        switch(S){
            case S1: stopBits = D2xxManager.FT_STOP_BITS_1; break;
            //case S1_5: bits |= BITS_STOP_1_5; break;
            case S2: stopBits = D2xxManager.FT_STOP_BITS_2; break;
            default: throw new IOException("Stop bits not supported");
        }
        switch(P){
            case NONE: parity = D2xxManager.FT_PARITY_NONE; break;
            case ODD: parity = D2xxManager.FT_PARITY_ODD; break;
            case EVEN: parity = D2xxManager.FT_PARITY_EVEN; break;
        }
        ftDevice.setDataCharacteristics(dataBits, stopBits, parity);
        //Flow control always OFF
        ftDevice.setFlowControl(D2xxManager.FT_FLOW_NONE, (byte)0, (byte)0);
        //Activate RTS and DTR
        ftDevice.setRts();
        ftDevice.setDtr();
    }

    public int getBaudRate(){
        return intBaudRate;
    }

    public int getBits(){
        return intDataBits;
    }

    public int read( byte[] data, int length, long wait_ms ){
        return ftDevice.read( data, length, wait_ms );
    }

    public int read( byte[] data, int length ){
        return ftDevice.read( data, length );
    }

    public int read( byte[] data ){
        return ftDevice.read( data );
    }
}
