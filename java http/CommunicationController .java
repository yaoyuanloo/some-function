package com.douples.controller.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.douples.common.websocket.LiveWebSocketHandler;
import com.douples.controller.rtc.WebRTCRoomManager;
import com.douples.facade.communicationFacade.CommunicationFacade;
import com.douples.framework.core.impl.BaseController;
import com.douples.framework.util.PageData;
import com.google.gson.Gson;

import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "mobile/CommunicationController")
public class CommunicationController extends BaseController {
	

	/**
	 * 视频结束
	 */
	@SuppressWarnings({ "unchecked" })
	public PageData closeVedio(String user) {
		InetAddress address;
		try {
			address = InetAddress.getLocalHost();//获取的是本地的IP地址 //LAPTOP-PI4K538J/172.16.51.185
			String hostAddress = address.getHostAddress();//获取本地IP地址：172.16.51.185
//			String url = "http://121.46.4.32:8071/OnlineConsultController/OnlineConsultController_updateDoctorStatus";//视频系统请求的路径
			String url = "http://192.168.1.177:8080/OnlineConsultController/OnlineConsultController_updateDoctorStatus";//视频系统请求的路径
			String params = "userId="+user+"&isBusy=N";
			String result = sendPost(url, params);// java 发送post请求视频系统 ,让视频系统创建房 间

			// json转Map
			Gson gson = new Gson();
			Map<String, Object> temp = new HashMap<String, Object>();
			temp = gson.fromJson(result, temp.getClass());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		
		PageData pd =new PageData();
		return pd;
	}
	
	/**
     * 向指定 URL 发送POST方法的请求(http)
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader( new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }  
    

}
