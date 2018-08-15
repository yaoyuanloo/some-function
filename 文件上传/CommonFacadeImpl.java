package com.douples.facade.common.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;
import org.apache.http.client.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.douples.common.constant.CommonStatus;
import com.douples.common.util.CommonUtil;
import com.douples.common.util.DICItemDTO;
import com.douples.common.util.FileUtils;
import com.douples.common.util.HisFlowHandelPool;
import com.douples.facade.common.CommonFacade;
import com.douples.facade.formula.FormulaFacade;
import com.douples.facade.prescriptionInfo.PrescriptionInfoFacade;
import com.douples.facade.questionnaire.QuestionnaireFacade;
import com.douples.facade.report.ReportOrderFacade;
import com.douples.framework.dao.impl.DaoSupport;
import com.douples.framework.entity.IUser;
import com.douples.framework.util.PageData;
@Service("commonFacade")
public class CommonFacadeImpl implements CommonFacade {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	@Resource(name = "formulaFacade")
	private FormulaFacade formulaFacade;
	
	@Resource(name="questionnaireFacade")
	private QuestionnaireFacade questionnaireFacade;
	
	@Resource(name="reportOrderFacade")
	private ReportOrderFacade reportOrderFacade;
	
	@Resource(name="prescriptionInfoFacade")
	private PrescriptionInfoFacade prescriptionInfoFacade;
	
	
	@Override
	public List<PageData> findProvinceList() throws Exception {
		List<PageData> provinceList = (List<PageData>) dao.findForList("AddressMapper.getProvinceList",null);
		return provinceList;
	}
	
	@Override
	public List<PageData> getProvinceList() throws Exception {
		List<PageData> provinceList = (List<PageData>) dao.findForList("AddressMapper.findProvinceList",null);
		return provinceList;
	}
	@Override
	public List<PageData> findCityList(PageData pd) throws Exception {
		
		List<PageData> cityList = (List<PageData>) dao.findForList("AddressMapper.getCityList",pd);
		return cityList;
	}
	
	@Override
	public List<PageData> getCityList(PageData pd) throws Exception {
		
		List<PageData> cityList = (List<PageData>) dao.findForList("AddressMapper.findCityList",pd);
		return cityList;
	}
	@Override
	public List<PageData> findAreaList(PageData pd) throws Exception {
		List<PageData> areaList = (List<PageData>) dao.findForList("AddressMapper.getAreaList",pd);
		return areaList;
	}
	
	@Override
	public List<PageData> getAreaList(PageData pd) throws Exception {
		List<PageData> areaList = (List<PageData>) dao.findForList("AddressMapper.findAreaList",pd);
		return areaList;
	}
	@Override
	public List<PageData> finOrgInfoByUserId(String userId) throws Exception {
		//根据登陆用户ID,获取用户所属身份
		List<PageData> resultList = new ArrayList<PageData>();
		List<Integer> orgIds = new ArrayList<Integer>();
		PageData pd = new PageData();
		List<PageData> types = (List<PageData>) dao.findForList("UserMapper.findUserTypefoByUserId",userId);
		Integer type = null;
		Long id = null;
		if(types != null && types.size()>0){
			type = (Integer) types.get(0).get("user_type");
			id = (Long) types.get(0).get("user_id");
		}
		if(type == null){
			return resultList;
		}
		pd.put("type",type);
		pd.put("id",id);
		switch(type){
		 case CommonStatus.EMPLOYEE:
			 orgIds = (List<Integer>) dao.findForList("StaffMapper.findOrgIdByUserId",pd);
			 break;
		 case CommonStatus.STUDENT:
			 orgIds = (List<Integer>) dao.findForList("StudentMapper.findOrgIdByUserId",pd);
			 break;
		 case CommonStatus.PARENTS:
			 orgIds = (List<Integer>) dao.findForList("ParentsMapper.findOrgIdByUserId",pd);
			 break;
		 case CommonStatus.SYSTEMUSER:
			 pd.put("userId",userId);
			 orgIds = (List<Integer>) dao.findForList("OrganMapper.findOrgIdByUserId",pd);
			 break;
		}
		//根据校区ID获取校区信息
		if(userId.equals("1")){
			resultList = (List<PageData>) dao.findForList("OrganMapper.findByAdminIdAndSts",pd);
		}else{
			if(orgIds != null && orgIds.size()>0){
				for(Integer orgId:orgIds){
					pd.put("ID",orgId);
					pd.put("sts","1");
					PageData org =  (PageData) dao.findForObject("OrganMapper.findByIdAndSts",pd);		
					if(org!=null){
						resultList.add(org);
					}
				}
			}
		}
		return resultList;
	}
	/**
	 * 根据参数生成单号
	 * @param pre
	 * @return
	 * @throws Exception 
	 */
	public  String updateOrderNo(String pre,String orgId,String dicCode) throws Exception{
		String orderNo = "";
		DICItemDTO dicItemDTO = null;
		PageData pd = new PageData();
		pd.put("dic_code",dicCode);
		//pd.put("dic_code","ORDER_NO_STR_PART");
		//pd.put("dic_code","ORDER_NO_STR_PART");
		pd.put("property_1",orgId);
		List<DICItemDTO> config = HisFlowHandelPool.queryConfigInfo(pd,dao);
		if(config != null && config.size()>0){
			dicItemDTO = config.get(0);
			if(StringUtil.isBlank(pre)){
				SimpleDateFormat data = new SimpleDateFormat("yyyyMM");
				String no = data.format(new Date());
				Long dic_value = Long.valueOf(dicItemDTO.getValue());
				//orderNo = dicItemDTO.getCode()+"_"+no+String.format("%06d",dic_value);
				orderNo = no+String.format("%06d",dic_value);
				//更新单据在数据字典value自动递增1
				if(StringUtil.isBlank(dicItemDTO.getValue())){
					pd.put("dic_code","ORDER_NO_STR_PART");
					pd.put("property_1",orgId);
					pd.put("item_name",CommonUtil.getConfig("common","ORDER_NO_START"));
				}else{
					Long value= Long.valueOf(dicItemDTO.getValue());
					value = value+1;
					pd.put("dic_code","ORDER_NO_STR_PART");
					pd.put("property_1",orgId);
					pd.put("item_name",String.format("%06d",value));
				}
				
				dao.update("DicItemsMapper.updateDicItem",pd);
			}else{
				SimpleDateFormat data = new SimpleDateFormat("yyyyMM");
				String no = data.format(new Date());
				Long dic_value = Long.valueOf(dicItemDTO.getValue());
				//orderNo = pre+"_"+dicItemDTO.getCode()+"_"+no+String.format("%06d",dic_value);
				orderNo = pre+"_"+no+String.format("%06d",dic_value);
				//更新单据在数据字典value自动递增1
				if(StringUtil.isBlank(dicItemDTO.getValue())){
					pd.put("dic_code","ORDER_NO_STR_PART");
					pd.put("property_1",orgId);
					pd.put("item_name",CommonUtil.getConfig("common","ORDER_NO_START"));
				}else{
					Long value= Long.valueOf(dicItemDTO.getValue());
					value = value+1;
					pd.put("dic_code","ORDER_NO_STR_PART");
					pd.put("property_1",orgId);
					pd.put("item_name",String.format("%06d",value));
				}
				
				dao.update("DicItemsMapper.updateDicItem",pd);
			}
		}
		return orderNo;
	}
	
	
	/**
	 * 上传文件
     * @date 2017-09-15
     * @param PageData 数据对象
	 */
	@Override
	public List<PageData> uploadFile(MultipartFile[] files, PageData pd) throws Exception {
		List<PageData> resultList = new ArrayList<PageData>();
		if (files.length > 0) {
			for (MultipartFile file : files) {
				resultList.add(uploadFile(file, pd));
			}
		}
		return resultList;
	}
	
	public PageData uploadFile(MultipartFile file, PageData pd)throws Exception {
		boolean upLoadFlag = StringUtils.isNotBlank(pd.getString("fileToUploadFileName"));
		String fileToUploadFileName=pd.getString("fileToUploadFileName");
		if ((upLoadFlag) && (!fileToUploadFileName.substring(fileToUploadFileName.lastIndexOf("."),fileToUploadFileName.length()).equalsIgnoreCase(".png"))
				&& (!fileToUploadFileName.substring(fileToUploadFileName.lastIndexOf("."),fileToUploadFileName.length()).equalsIgnoreCase(".jpg"))
				&& (!fileToUploadFileName.substring(fileToUploadFileName.lastIndexOf("."),fileToUploadFileName.length()).equalsIgnoreCase(".gif"))
				&& (!fileToUploadFileName.substring(fileToUploadFileName.lastIndexOf("."),fileToUploadFileName.length()).equalsIgnoreCase(".bmp"))
				&& (!fileToUploadFileName.substring(fileToUploadFileName.lastIndexOf("."),fileToUploadFileName.length()).equalsIgnoreCase(".jpeg"))) {
			pd.put("result", false);
			pd.put("msg", "对不起,你上传的文件格式不允许!请重新上传图片格式的文件!");
			return pd;
		}
		if (checkCode(pd.getString("fileToUploadFileName"))) {
			pd.put("result", false);
			pd.put("msg", "文件名不能含【空格】,【逗号】或特殊符号【!,@,#,$,&,*,(,),=,:,/,;,?,+,',|, 】，请删除文件名中的空格或特殊符号后再试!");
			return pd;
		}
		
		StringBuffer finalFilePath = new StringBuffer();
		String newFilePath = CommonUtil.getConfig("common","REPORT_FILEPATH");
		finalFilePath.append(newFilePath);
		finalFilePath.append("\\");
		finalFilePath.append(DateUtils.formatDate(new Date(), "yyyyMMdd"));
		String pcbName = file.getOriginalFilename();
		finalFilePath.append("\\");
		finalFilePath.append(pcbName);
	    File filePrefix=new File(finalFilePath.toString());
	    if (!filePrefix.exists()) {
	    	filePrefix.mkdirs();
		}
	    
	    //上传
	    File uploadFile=new File(FileUtils.path(finalFilePath.toString()));
		file.transferTo(uploadFile);
		PageData result = new PageData();
		result.put("PHOTO_PATCH",finalFilePath.toString().replace("D:", "").replace("E:", "").replace("F:", ""));
		result.put("PHOTO_NAME",pcbName);
		result.put("PHOTO_DT",CommonUtil.dateToString4(new Date()));
		
		result.put("result", true);
		result.put("msg","文件上传成功");
		return result;
	}
	
	// 检查文件是否已经上传过
		private boolean checkFileExists(String filePatch) {
			File f = new File(filePatch);
			if (!f.exists()) {
				return false;
			}
			return true;
		}
	
	//检验上传文件名称的格式是否正确
	private boolean checkCode(String fileName) {
		boolean result = false;
		String[] nameCode = "!,@,#,$,&,*,(,),=,:,/,;,?,+,',|, ".split(",");
		for (int i = 0; i < nameCode.length; i++) {
			if (fileName.indexOf(nameCode[i]) != -1) {
				result = true;
				break;
			}
		}
		if (fileName.indexOf(",") != -1) {
			result = true;
		}
		return result;
	}
	
	/**
	 * 生成检测报告 & 处方单 & 处方单明细
     * @date 2018-06-29
     * @param PageData 数据对象
	 */
	public PageData updateGenerateCheckReport(PageData pageData, IUser user) throws Exception{
		PageData pd = new PageData();
		PageData question = new PageData();
		List<PageData> opticalTestList = new ArrayList<PageData>();
		List<PageData> cellTestList = new ArrayList<PageData>();
		List<PageData> geneTestList = new ArrayList<PageData>();
		Boolean opticalTestFlag = false;
		Boolean cellTestFlag = false;
		Boolean geneTestFlag = false;
		String checkType = pageData.getString("checkType");
		String checkItem = pageData.getString("checkItem");
		if(checkType == null || "".equals(checkType) || checkItem == null || "".equals(checkItem)){
			throw new Exception();
		}
		question = (PageData) dao.findForObject("TestOpticalInfoMapper.findQuestionOrderId", pageData);
		if(question!=null){
			 //光学检测, 光检测类型(checkType)仅包含光学检测一项(checkItem)
			if("optical".equals(checkType) && "OpticalTest".equals(checkItem)){
				opticalTestFlag = true;
			}
			//细胞检测, 细胞检测类型(checkType)包含光学检测和细胞检测两项(checkItem)
			if("cell".equals(checkType)){
				if("OpticalTest".equals(checkItem)){
					opticalTestFlag = true;
					cellTestList = (List<PageData>) this.dao.findForList("TestCellMapper.listCellTestDataByOrderId", pageData);
					if(cellTestList != null && !cellTestList.isEmpty()){
						cellTestFlag = true;
					}
				}else if("CellTest".equals(checkItem)){
					cellTestFlag = true;
					opticalTestList = (List<PageData>) this.dao.findForList("TestOpticalInfoMapper.listOpticalTestDataByOrderId", pageData);
					if(opticalTestList != null && !opticalTestList.isEmpty()){
						opticalTestFlag = true;
					}
				}
			}
			//基因检测, 基因检测类型(checkType)包含光学检测和细胞检测和基因检测三项(checkItem)
			if("gene".equals(checkType)){
				if("OpticalTest".equals(checkItem)){
					opticalTestFlag = true;
					cellTestList = (List<PageData>) this.dao.findForList("TestCellMapper.listCellTestDataByOrderId", pageData);
					if(cellTestList != null && !cellTestList.isEmpty()){
						cellTestFlag = true;
					}
					geneTestList = (List<PageData>) this.dao.findForList("TestGeneMapper.listGeneTestDataByOrderId", pageData);
					if(geneTestList != null && !geneTestList.isEmpty()){
						geneTestFlag = true;
					}
				}else if("CellTest".equals(checkItem)){
					cellTestFlag = true;
					opticalTestList = (List<PageData>) this.dao.findForList("TestOpticalInfoMapper.listOpticalTestDataByOrderId", pageData);
					if(opticalTestList != null && !opticalTestList.isEmpty()){
						opticalTestFlag = true;
					}
					//geneTestList = (List<PageData>) this.dao.findForList("TestGeneMapper.listGeneTestDataByOrderId", pageData);
					if(geneTestList != null && !geneTestList.isEmpty()){
						geneTestFlag = true;
					}
				}else if("GeneTest".equals(checkItem)){
					geneTestFlag = true;
					opticalTestList = (List<PageData>) this.dao.findForList("TestOpticalInfoMapper.listOpticalTestDataByOrderId", pageData);
					if(opticalTestList != null && !opticalTestList.isEmpty()){
						opticalTestFlag = true;
					}
					cellTestList = (List<PageData>) this.dao.findForList("TestCellMapper.listCellTestDataByOrderId", pageData);
					if(cellTestList != null && !cellTestList.isEmpty()){
						cellTestFlag = true;
					}
				}
			}
			pd.put("results_id", question.get("id"));
		}else{
			pd.put("results_id", "");
		}
		//生成基因检测报告 & 处方单 处方单明细
		pd = updateGenerateData(pageData, user);
		//光学检测(仅光学检测)全部完成, 则生成光学检测报告
		if("optical".equals(checkType) && opticalTestFlag){
			pd.put("report_name", "光学检测报告");
			this.dao.save("TestReportInfoMapper.addReportInfo", pd);
			//this.dao.save("TestPrescriptionInfoMapper.addPrescriptionInfo", pd);
			//this.dao.save("TestPrescriptionDetailsMapper.addPrescriptionDetails", pd);
			  //调用算法生成套装
			prescriptionInfoFacade.doPrescriptionDetails(pd);
			pd.put("result", true);
			return pd;
		}
		//细胞检测(含光学检测 & 细胞检测)全部完成, 则生成细胞检测报告
		if("cell".equals(checkType) && opticalTestFlag && cellTestFlag){
			pd.put("report_name", "细胞检测报告");
			this.dao.save("TestReportInfoMapper.addReportInfo", pd);
			//this.dao.save("TestPrescriptionInfoMapper.addPrescriptionInfo", pd);
			//this.dao.save("TestPrescriptionDetailsMapper.addPrescriptionDetails", pd);
			  //调用算法生成套装
			prescriptionInfoFacade.doPrescriptionDetails(pd);
			pd.put("result", true);
			return pd;
		}
		//基因检测(含光学检测 & 细胞检测 & 基因检测)全部完成, 则生成基因检测报告
		if("gene".equals(checkType) && opticalTestFlag && cellTestFlag && geneTestFlag){
			pd.put("report_name", "基因检测报告");
			this.dao.save("TestReportInfoMapper.addReportInfo", pd);
			//this.dao.save("TestPrescriptionInfoMapper.addPrescriptionInfo", pd);
			//this.dao.save("TestPrescriptionDetailsMapper.addPrescriptionDetails", pd);
			prescriptionInfoFacade.doPrescriptionDetails(pd);
			pd.put("result", true);
			return pd;
		}
		pd.put("result", false);
		pd.put("report_id", "");
		return pd;
	}
	
	private PageData updateGenerateData(PageData pageData, IUser user) throws Exception{
		PageData resultPd = new PageData();
		resultPd.putAll(pageData);
		//检测报告数据
		//String report_id = DateUtils.formatDate(new Date(), "yyyyMMddHHmmss");
		resultPd.put("report_id", UUID.randomUUID().toString());
		resultPd.put("order_id", pageData.getString("order_id_add"));
		resultPd.put("customer_id", pageData.getString("customer_id_add"));
		resultPd.put("test_shop_id", pageData.get("shop_id_add"));
		resultPd.put("doctors_order", "");
		resultPd.put("model_id", "");
		resultPd.put("create_operator", user.getUserName());
		resultPd.put("create_date", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
		resultPd.put("update_operator", user.getUserName());
		resultPd.put("update_date", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
		resultPd.put("is_delete", "N");
		//处方单数据
		//resultPd.put("prescription_id", "prescription-"+DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		resultPd.put("prescription_id",  UUID.randomUUID().toString());
		resultPd.put("prescription_type", "0");
		resultPd.put("prescription_state", "1");
		resultPd.put("results_id", pageData.get("results_id"));
		//处方单明细数据
		//String details_id = DateUtils.formatDate(new Date(), "yyyyMMddHHmmss");
		resultPd.put("details_id", UUID.randomUUID().toString());
		
		return resultPd;
	}
	
	/**
	 * 光学改变等级更新报告数据 
     * @date 2018年7月28日 09:20:55
     * @param PageData reportType-报告类型；targetType-等级类型；riskLevel-等级 ;userNo-更新人
	 */
	public PageData updateOpticalReportLevelToData(PageData pd) throws Exception{
		dao.update("TestOpticalInfoMapper.updateLevelToData", pd);
		return pd;
	}
	/**
	 * 细胞改变等级更新报告数据 
     * @date 2018年7月28日 09:20:55
     * @param PageData reportType-报告类型；targetType-等级类型；riskLevel-等级 ;userNo-更新人
	 */
	public PageData updateCellReportLevelToData(PageData pd) throws Exception{
		dao.update("TestCellMapper.updateLevelToData", pd);
		return pd;
	}
	/**
	 * 基因等级更新报告数据 
     * @date 2018年7月28日 09:20:55
     * @param PageData reportType-报告类型；targetType-等级类型；riskLevel-等级 ;userNo-更新人
	 */
	public PageData updateGeneReportLevelToData(PageData pd) throws Exception{
		
		return pd;
	}
	
}
