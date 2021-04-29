package com.srobber.common.util;

import com.srobber.common.exeption.WrapException;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;

/**
 * https请求客户端
 * 短连接,请求后关闭连接
 *
 * @author chensenlai
 */
@Slf4j
public class HttpsClient {

	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";

	public static String get(String url) {
		return httpsRequest(url, METHOD_GET, null, null, Charset.defaultCharset());
	}

	public static String get(String url, Charset charset) {
		return httpsRequest(url, METHOD_GET, null, null, charset);
	}

	public static String get(String url, Map<String, String> headerMap, Charset charset) {
		return httpsRequest(url, METHOD_GET, headerMap, null, charset);
	}

	public static String post(String url, String body) {
		return httpsRequest(url, METHOD_POST, null, body, Charset.defaultCharset());
	}

	public static String post(String url, String body, Charset charset) {
		return httpsRequest(url, METHOD_POST, null, body, charset);
	}

	public static String post(String url, Map<String, String> headerMap, String body, Charset charset) {
		return httpsRequest(url, METHOD_POST, headerMap, body, charset);
	}

	private static String httpsRequest(String url, String method, Map<String, String> headerMap,
									   String data, Charset charset) {
		HttpsURLConnection conn = null;
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			conn = (HttpsURLConnection) (new URL(url)).openConnection();
			StringBuffer buf = new StringBuffer(1024);

			conn.setSSLSocketFactory(ssf);

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			conn.setRequestMethod(method);

			// 设置请求头
			if(headerMap != null) {
				for(Map.Entry<String, String> entity : headerMap.entrySet()) {
					conn.setRequestProperty(entity.getKey(), entity.getValue());
				}
			}

			// 刷请求数据
			if (data != null) {
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(data.getBytes(charset));
				outputStream.close();
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
			String str = null;
			while ((str = br.readLine()) != null) {
				buf.append(str);
			}
			br.close();
			return buf.toString();
		} catch (Exception e) {
			log.error("https {} {} {} error. {}", method, url, data, e.getMessage());
			throw new WrapException(e);
		} finally {
			conn.disconnect();
		}
	}
	
	public static String getPublicKey(String filePath) {
        String key="";
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            FileInputStream in = new FileInputStream(filePath);
 
            //生成一个证书对象并使用从输入流 inStream 中读取的数据对它进行初始化。
            Certificate c = cf.generateCertificate(in);
            PublicKey publicKey = c.getPublicKey();
            key = Base64.getEncoder().encodeToString((publicKey.getEncoded()));
        } catch (CertificateException e) {
        	log.error("file {} not certficate. {}", filePath, e.getMessage());
			throw new WrapException(e);
        } catch (FileNotFoundException e) {
        	log.error("file {} not found. {}", filePath, e.getMessage());
			throw new WrapException(e);
        }
        return key;
    }

    public HttpsClient(){}
}

class MyX509TrustManager implements X509TrustManager {
	/** 检查客户端证书 */
	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
	}
	/** 检查服务器端证书 */
	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
	}
	/** 返回受信任的X509证书数组 */
	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}
