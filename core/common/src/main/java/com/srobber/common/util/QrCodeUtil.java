package com.srobber.common.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import com.srobber.common.exeption.WrapException;
import lombok.extern.slf4j.Slf4j;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码工具类(生成二维码,解码二维码)
 *
 * @author chensenlai
 */
@Slf4j
public class QrCodeUtil {

	private static final String CHARSET = "utf-8";
	private static final String FORMAT = "png";
	/**
	 * 二维码尺寸
	 */
	private static final int QRCODE_SIZE = 560;
	
	private static Hashtable<EncodeHintType, Object> ENCODE_HINTS = new Hashtable<EncodeHintType, Object>();
	private static Map<DecodeHintType, Object> DECODE_HINTS = new HashMap<DecodeHintType, Object>();
	static {
		ENCODE_HINTS.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		ENCODE_HINTS.put(EncodeHintType.CHARACTER_SET, CHARSET);
		ENCODE_HINTS.put(EncodeHintType.MARGIN, 0);
		
		DECODE_HINTS.put(DecodeHintType.CHARACTER_SET, CHARSET);
	}
	

	public static byte[] encodeQRCode(String content) {
		ByteArrayOutputStream bos = null;
		try{
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
					BarcodeFormat.QR_CODE, 
					QRCODE_SIZE, 
					QRCODE_SIZE, 
					ENCODE_HINTS);
			bos = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, FORMAT, bos);
			return bos.toByteArray();
		} catch (Exception e) {
			log.error("QRCode encode error. {}", e.getMessage());
			throw new WrapException(e);
		} finally {
			if(bos != null) {
				try {
					bos.close();
				} catch (IOException e) {}
			}
		}
	}
	
	public static String decodeQRCode(byte[] bs) {
		ByteArrayInputStream bis = null;
		try {
			bis = new ByteArrayInputStream(bs);
			BufferedImage image = ImageIO.read(bis);
			MultiFormatReader formatReader = new MultiFormatReader();
			LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Result result = formatReader.decode(binaryBitmap, DECODE_HINTS);
            return result.getText();
		} catch (Exception e) {
			log.error("QRCode decode error. {}", e.getMessage());
			throw new WrapException(e);
		}  finally {
			if(bis != null) {
				try {
					bis.close();
				} catch (IOException e) {}
			}
		}
	}
	
	public static byte[] drawLogo(byte[] qrCode, byte[] logo){
		ByteArrayInputStream qrCodeInputStream = null;
		ByteArrayInputStream logoInputStream = null;
		ByteArrayOutputStream bos = null;
		try{
        	qrCodeInputStream = new ByteArrayInputStream(qrCode);
        	logoInputStream = new ByteArrayInputStream(logo);
        	//读取二维码图片  
        	BufferedImage qrCodeImgBuf = ImageIO.read(qrCodeInputStream);  
            Graphics2D g = qrCodeImgBuf.createGraphics();  
            //读取logo图片,设置二维码大小，太大，会覆盖二维码，此处20%  
            BufferedImage logoImgBuf = ImageIO.read(logoInputStream);  
            int logoWidth = logoImgBuf.getWidth() > qrCodeImgBuf.getWidth()*2 /10 ? (qrCodeImgBuf.getWidth()*2 /10) : logoImgBuf.getWidth();  
            int logoHeight = logoImgBuf.getHeight() > qrCodeImgBuf.getHeight()*2 /10 ? (qrCodeImgBuf.getHeight()*2 /10) : logoImgBuf.getHeight();  
            //设置logo图片放置位置 (中心)
            int x = (qrCodeImgBuf.getWidth() - logoWidth) / 2;  
            int y = (qrCodeImgBuf.getHeight() - logoHeight) / 2;  
            //开始合并绘制图片  
            g.drawRoundRect(x, y, logoWidth, logoHeight, 15 ,15);  
            g.setStroke(new BasicStroke(2));  
            g.setColor(Color.WHITE);  
            g.drawRect(x, y, logoWidth, logoHeight);  
            g.drawImage(logoImgBuf, x, y, logoWidth, logoHeight, null);  
            g.dispose();  
            logoImgBuf.flush();  
            qrCodeImgBuf.flush();
            bos = new ByteArrayOutputStream();
            ImageIO.write(qrCodeImgBuf, "png", bos);
            return bos.toByteArray();
        } catch(Exception e) {  
			log.error("QRCode drawLogo error. {}", e.getMessage());
			throw new WrapException(e);
        }  finally {
        	try {
        		if(qrCodeInputStream != null) {
            		qrCodeInputStream.close();
            	}
            	if(logoInputStream != null) {
            		logoInputStream.close();
            	}
            	if(bos != null) {
            		bos.close();
            	}
        	} catch(IOException ioe) {}
        }
    }  
}