package com.example.ble.util;

import android.util.Log;

import cn.com.heaton.blelibrary.ble.utils.ByteUtils;

/**
 * Created  by Administrator on 2022/5/14 15:20
 */
public class RamdomUtil {

    public static long ramDomNum(String hexString,byte[] randomBytes) {
        //byte[] randomBytes= ByteUtils.hexStr2Bytes(hexString);

  /*      int seedTemp32 = (((randomBytes[0] ^ randomBytes[3])) & 0x0ff) + (((randomBytes[1] ^ randomBytes[3])) & 0x0ff) + (((randomBytes[1] ^ randomBytes[3])) & 0x00ff) + randomBytes[0];
        Log.e("TAG", "seedTemp32= " + getUnsignedIntt(seedTemp32));
        //真正想要的值 2424879451
        int randomKey32 = seedTemp32 ^ (0x20211010);
        Log.e("TAG", "randomKey32= " + getUnsignedIntt(randomKey32));*/

        String hexStr1 = Integer.toHexString(getUnsignedByte((byte) (randomBytes[0] ^ randomBytes[3])));
        String hexStr2 = Integer.toHexString(getUnsignedByte((byte) (randomBytes[1] ^ randomBytes[3])));
        String hexStr3 = Integer.toHexString(getUnsignedByte((byte) (randomBytes[1] ^ randomBytes[3])));
        String hexStr4 = Integer.toHexString(getUnsignedByte(randomBytes[3]));
        if (hexStr1.length() < 2) {
            hexStr1 = '0' + hexStr1;
        }
        if (hexStr2.length() < 2) {
            hexStr2 = '0' + hexStr2;
        }
        if (hexStr3.length() < 2) {
            hexStr3 = '0' + hexStr3;
        }
        if (hexStr4.length() < 2) {
            hexStr4 = '0' + hexStr4;
        }
        String hexStr=hexStr1+hexStr2+hexStr3+hexStr4;
        Log.e("TAG"," hexStr=" + hexStr);
        //真正想要的值 2424879451
        byte[] bytes= ByteUtils.hexStr2Bytes(hexStr);
        String hexStr11 = Integer.toHexString(getUnsignedByte((byte) (bytes[0] ^0x20)));
        String hexStr22 = Integer.toHexString(getUnsignedByte((byte) (bytes[1] ^0x21)));
        String hexStr33 = Integer.toHexString(getUnsignedByte((byte) (bytes[2] ^0x10)));
        String hexStr44 = Integer.toHexString(getUnsignedByte((byte) (bytes[3] ^0x10)));
        if (hexStr11.length() < 2) {
            hexStr11 = '0' + hexStr11;
        }
        if (hexStr22.length() < 2) {
            hexStr22 = '0' + hexStr22;
        }
        if (hexStr33.length() < 2) {
            hexStr33 = '0' + hexStr33;
        }
        if (hexStr44.length() < 2) {
            hexStr44 = '0' + hexStr44;
        }
        String hexStrings=hexStr11+hexStr22+hexStr33+hexStr44;
        Log.e("TAG"," hexStrings=" + hexStrings);
        //转换成10进制数
        long randomKey32 = Long.parseLong(hexStrings, 16);
      //long randomKey32 = getUnsignedInt(0xad4747c6 ^ 0x20211010);
        Log.e("TAG"," randomKey32=" + randomKey32);
        return randomKey32;
    }

    //转换为无符号数
    public static int getUnsignedByte (byte data){      //将data字节型数据转换为0~255 (0xFF 即BYTE)。
        return data&0x0FF ;

    }

    //转换为无符号数
    public static int getUnsignedByte (short data){      //将data字节型数据转换为0~65535 (0xFFFF 即 WORD)。
        return data&0x0FFFF ;
    }

    //转换为无符号数
    public static long getUnsignedInt(int data){     //将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
        return data&0x0FFFFFFFF ;
    }



    /**
     * 将指定byte数组以16进制的形式打印到控制台
     *
     * @param hint
     *            String
     * @param b
     *            byte[]
     * @return void
     */
    public static void printHexString(String hint, byte[] b) {
        System.out.print(hint);
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print(hex.toUpperCase() + " ");
        }
        System.out.println("");
    }

    /**
     *
     * @param b
     *            byte[]
     * @return String
     */
    public static String Bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += " 0x" + hex.toUpperCase();
        }
        return ret;
    }

    /**
     * 将两个ASCII字符合成一个字节； 如："EF"–> 0xEF
     *
     * @param src0
     *            byte
     * @param src1
     *            byte
     * @return byte
     */
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[] {src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" –> byte[]{0x2B, 0×44, 0xEF,
     * 0xD9}
     *
     * @param src
     *            String
     * @return byte[]
     */
    public static byte[] HexString2Bytes(String src) {
        if (null == src || 0 == src.length()) {
            return null;
        }
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < (tmp.length / 2); i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    public static void main(String[] args) {
        String s = "00004E0055AA66BB";
        System.out.println(s);
        byte[] b = HexString2Bytes(s);
        System.out.println(Bytes2HexString(b));
    }



}
