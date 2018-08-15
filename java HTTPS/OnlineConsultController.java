package com.douples.controller.onlineConsult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.douples.common.util.CommonUtil;
import com.douples.common.websocket.LiveWebSocketHandler;
import com.douples.framework.core.impl.BaseController;
import com.douples.framework.page.Page;
import com.douples.framework.util.PageData;
import com.douples.framework.util.SSLClient;
import com.google.gson.Gson;

import net.sf.json.JSONObject;

/* 

/**
 * 类名称：OnlineConsultController 在线咨询
 * 
 * @author cwy
 * @date 2018年6月25日
 * @Description
 */
@Controller
@RequestMapping(value = "/OnlineConsultController")
public class OnlineConsultController extends BaseController {

	/**
	 * 接收顾客发起的视频请求
	 * 
	 * @author lyq
	 * @date 2018年7月9日 11:21:23
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/OnlineConsultController_requestVideo")
	@ResponseBody
	public Object requestVideo(HttpServletRequest request) throws Exception {
		JSONObject json = CommonUtil.getParam(request);
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.putAll(json);	
		PageData pd2 = new PageData();
		Map map = new HashMap();
		map.put("u", pd.get("customer_id"));
		try {
			
//			String url = "https://121.46.4.32:8443/dps-liveTV/mobile/CommunicationController/CommunicationController_createRoom";//视频系统请求的路径
			String url = "https://192.168.1.177:8443/dps-liveTV/mobile/CommunicationController/CommunicationController_createRoom";//视频系统请求的路径
			String result = doPost(url, map, "utf-8");// java 发送post请求视频系统 ,让视频系统创建房间

			// json转Map
			Gson gson = new Gson();
			Map<String, Object> temp = new HashMap<String, Object>();
			temp = gson.fromJson(result, temp.getClass());

			// 给小程序返回地址
			String customURL = temp.get("roomLink").toString() + "?r=" + temp.get("room").toString() + "&u=" + pd.get("customer_id").toString();
			videoURL = temp.get("roomLink").toString();
			
			List<PageData> customerList = onlineConsultFacade.findCustomer(pd);//查询顾客表
			if (customerList.size() == 0) {//不存在顾客
				pd.put("code", "50004");
				pd.put("msg", "顾客ID不存在！");
				return pd;
			}

			List<PageData> onlineDoctor = onlineConsultFacade.findOnlineDoctor(pd);//获取一名在线的咨询师
			if (onlineDoctor.size() > 0) {//当前有咨询师在线
				//随机抽取咨询师
				int random = (int) (Math.random() * (onlineDoctor.size()-1));
				pd.put("u", onlineDoctor.get(random).get("userId"));
				pd.put("room", temp.get("room").toString());
				pd.put("roomLink", temp.get("roomLink").toString());
				
				LiveWebSocketHandler websocket = new LiveWebSocketHandler();
				websocket.sendNewProductDatas(pd);// 推送到客户端
				
				
				
				pd.put("userId", onlineDoctor.get(random).get("userId"));
				pd.put("isBusy", "Y");
				onlineConsultFacade.updateDoctorStatus(pd);//扫一扫的时候    更新咨询师状态  
				
				pd.put("code", "10000");
				pd.put("msg", "获取信息成功！");
				pd2.put("videoURL", customURL);
				pd.put("data", pd2);
			}else{//当前没有咨询师在线
				pd.put("code", "50005");
				pd.put("msg", "当前没有空闲咨询师，请稍后...");
			}
			
		} catch (Exception e) {
			pd.put("code", "50006");
			pd.put("msg", "获取信息失败！");
			e.printStackTrace();
		}
		pd.remove("customer_id");
		return pd;
	}


	/**
	 * 向https 发送post请求，参数用map接收
	 * 
	 * @param url 地址
	 * @param map 参数
	 * @return 返回值
	 */
	public String doPost(String url, Map<String, String> map, String charset) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = new SSLClient();
			httpPost = new HttpPost(url);
			// 设置参数
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			Iterator iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> elem = (Entry<String, String>) iterator.next();
				list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
			}
			if (list.size() > 0) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
				httpPost.setEntity(entity);
			}
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	
	
	/**
	 * 静态页  二维码
	 * @author 
	 * @date 2018年7月16日 10:45:56
	 */
	@RequestMapping(value = "/OnlineConsultController_getQRCode")
	@ResponseBody
	public Object getQRCode(Page page) throws Exception {
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
			pd = onlineConsultFacade.getQRCode(pd);
			pd.put("flag", true);
			pd.put("msg", "操作成功！");
		} catch (Exception e) {
			pd.put("flag", false);
			pd.put("msg", "操作失败！");
			e.printStackTrace();
		}

		return pd;
	}
	
}
