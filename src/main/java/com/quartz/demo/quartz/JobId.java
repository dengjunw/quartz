package com.quartz.demo.quartz;

import java.util.Random;

public class JobId {
    private int id;
    private static char[][] idc = lut();
    private static Random rng = new Random();

    public JobId(int id) {
        this.id = id;
    }

    private char[] byteToChars(Byte b) {
        return idc[b + 128];
    }

    private static char[][] lut() {
        char[][] rv = new char[256][2];
        int idx = 0;

        for(byte b = -128; b >= -128 && b <= 127 && idx < 256; ++idx) {
            byte bb;
            if (b < 0) {
                bb = (byte)(b + 256);
            } else {
                bb = b;
            }

            String s = String.format("%02x", bb);
            rv[idx] = new char[]{s.charAt(0), s.charAt(1)};
            ++b;
        }

        return rv;
    }

    public String toString() {
        StringBuilder b = new StringBuilder(8);
        b.append(this.byteToChars((byte)((int)((long)(this.id >> 24) & 255L))));
        b.append(this.byteToChars((byte)((int)((long)(this.id >> 16) & 255L))));
        b.append(this.byteToChars((byte)((int)((long)(this.id >> 8) & 255L))));
        b.append(this.byteToChars((byte)((int)((long)this.id & 255L))));
        return b.toString();
    }

    public static String id() {
        return (new JobId(rng.nextInt())).toString();
    }
}
