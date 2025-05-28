package com.example.MangaWebSite.models;

import java.io.*;

public class ComicsUtil {
    public static byte[] serializeFloatArray(float[] array) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        for (float f : array) {
            dos.writeFloat(f);
        }
        return baos.toByteArray();
    }

    public static float[] deserializeFloatArray(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        int floatCount = bytes.length / 4;
        float[] array = new float[floatCount];
        for (int i = 0; i < floatCount; i++) {
            array[i] = dis.readFloat();
        }
        return array;
    }
}

