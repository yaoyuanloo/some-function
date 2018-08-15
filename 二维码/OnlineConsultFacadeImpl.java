package com.douples.facade.onlineConsult.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.douples.common.util.DateUtils;
import com.douples.common.util.QRCodeUtil;
import com.douples.facade.onlineConsult.OnlineConsultFacade;
import com.douples.framework.util.PageData;

@Service("onlineConsultFacade")
public class OnlineConsultFacadeImpl implements OnlineConsultFacade {
	//二维码的基本配置信息(宽 & 高 & 格式 & 存储路径)
	public static final Object[] QRCODEINFO = {250, 250, "png", "D:/uploadFiles/"};
	
	/**
	 * 二维码
	 * 
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@Override
	public PageData getQRCode(PageData pd) throws Exception {
		String packageCode = pd.getString("customerId");
		String qrCodeName = "QRCode"+ packageCode + DateUtils.formatDate(new Date(), "yyyyMMddHHmmss") + "." + (String) QRCODEINFO[2];
		String qrCode = QRCodeUtil.generateQRCode(packageCode, (Integer) QRCODEINFO[0], (Integer) QRCODEINFO[1], (String) QRCODEINFO[2], (String) QRCODEINFO[3] + qrCodeName);
		pd.put("pathQRCode", qrCode.replace("D:", ""));
		return pd;
	}
	

}
