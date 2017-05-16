package com.jeesd.redis.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 对象序列化工具类
 * @author song
 *
 */
public class SerializingUtil {
	
	/**
	 * 对实体进行序列化操作
	 * @param source
	 * @return
	 */
	public static byte[] serialize(Object source) {
		ByteArrayOutputStream byteOut = null;  
        ObjectOutputStream ObjOut = null;  
        try {  
            byteOut = new ByteArrayOutputStream();  
            ObjOut = new ObjectOutputStream(byteOut);  
            ObjOut.writeObject(source);  
            ObjOut.flush();  
        }  
        catch (IOException e) {  
            e.printStackTrace();
        }  
        finally {  
            try {  
                if (null != ObjOut) {  
                    ObjOut.close();  
                }  
            } catch (IOException e) {  
                ObjOut = null;  
            }  
        }  
        return byteOut.toByteArray();  
	}
	
	/**
	 * 对实体进行反序列化操作
	 * @param source
	 * @return
	 */
	public static Object deserialize(byte[] source) {
		ObjectInputStream ObjIn = null;  
        Object retVal = null;  
        try {  
            ByteArrayInputStream byteIn = new ByteArrayInputStream(source);  
            ObjIn = new ObjectInputStream(byteIn);  
            retVal = ObjIn.readObject();  
        }  
        catch (Exception e) {  
            e.printStackTrace(); 
        }  
        finally {  
            try {  
                if(null != ObjIn) {  
                    ObjIn.close();  
                }  
            } catch (IOException e) {  
                ObjIn = null;  
            }  
        }  
        return retVal;  
	}
}
