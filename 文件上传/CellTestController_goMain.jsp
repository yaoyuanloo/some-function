<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width" />

	    <script type="text/javascript" src="<%=path %>/static/js/ttdms/jquery.min.js"></script>
		<link href="<%=path %>/static/css/ttdms/bootstrap-table.min.css" rel="stylesheet" type="text/css" />
	    <link href="<%=path %>/static/css/ttdms/bootstrap.min.css" rel="stylesheet" type="text/css" />
	    <script type="text/javascript" src="<%=path %>/static/js/ttdms/bootstrap.min.js"></script>
	    <link href="<%=path %>/static/css/ttdms/sweetalert.css" rel="stylesheet" type="text/css" />
	    <link rel="stylesheet" href="<%=path%>/static/css/ttdms/fileinput.css">  
	    <link rel="stylesheet" href="<%=path%>/static/css/ttdms/fileinput.min.css">  
	    <script type="text/javascript" src="<%=path %>/static/js/ttdms/sweetalert.min.js"></script>
	    <script type="text/javascript" src="<%=path %>/static/js/ttdms/moment-with-locales.min.js"></script>
	    <script type="text/javascript" src="<%=path %>/static/js/ttdms/bootstrap-table.min.js"></script>
	    <script type="text/javascript" src="<%=path %>/static/js/ttdms/bootstrap-table-zh-CN.min.js"></script>
	    <script type="text/javascript" src="<%=path %>/static/js/ttdms/bootstrapValidator.min.js"></script>
	    <script type="text/javascript" src="<%=path %>/static/js/ttdms/bootstrap-treeview.js"></script>
	    <link href="<%=path %>/static/css/ttdms/bootstrapValidator.min.css" rel="stylesheet" type="text/css" />
	    <script type="text/javascript" src="<%=path %>/static/js/ttdms/jquery-form.js"></script>
	    <script type="text/javascript" src="<%=path %>/static/My97DatePicker/WdatePicker.js"></script>
	    <script src="<%=path %>/static/js/ttdms/showCombobox.js"></script>
	     <script type="text/javascript" src="<%=path %>/static/layui/layui.all.js" ></script>
	     <script type="text/javascript" src="<%=path %>/static/js/ttdms/fileinput.js"></script>
	     <script type="application/javascript" src="<%=path %>/static/js/ttdms/ajaxfileupload.js"></script>
	    
	<script type="text/javascript">
	

	/*************************************** 文件上传窗口 ******************************/
	function uploadFile(obj){
		if(obj=="photo_center_edit"){
			obj="photo_center";
			$("#editPicUl").empty();
		}
		$("#fileToUpload").val('');
		$("#fileImgName").val(obj);
		$('#uploadDisplay').hide();
		document.getElementById('fileToUpload').readOnly=false;
		$("#uploadDiv").modal({
			keyboard : true
		})
	}
   
</script>
</head>
<body>


	
	<!-------------------------------上传图片----------------------------------->
 	<div class="modal fade" data-backdrop="static" id="uploadDiv" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" 
 			aria-hidden="true" style="display: none;width: 100%;min-height: 330px;z-index: 1200;position: absolute;">
		<div class="modal-dialog cy-modal-dialog-f">
			<div class="modal-content" style="height: 250px;">
				<div class="modal-body" style="height: 180px;">
					<form id="uploadForm" method="post" novalidate="novalidate" enctype="multipart/form-data">
						<table id="tblAdd" width="100%" border="0" align="center" cellspacing="0" class="view">
							<input type="hidden" id="fileImgName">
		                   	<tr>
								<td  align="right" width="10%">选择文件：&nbsp;&nbsp;</td>
								<td  width="40%">
									<input id="fileToUpload" type="file" name="fileToUpload" multiple="multiple" size="200">
								</td>
							</tr>
							<tr>
								<td  colspan="2" align="center" height="26">
								    <font color="red">格式为*.jpg、*.png，文件名不能含【空格】或【!,@,#,$,&,*,(,),=,:,/,;,?,+,',|】等特殊符号。</font>
								</td>
							</tr>
							<tr height="32" id="uploadDisplay" style="display:none">
								<td colspan="2" align="center" height="60">
									<img src="<%=path %>/static/img/images/logining.gif"/>&nbsp;<br>
									正在上传文件，不要关闭本窗口，请耐心等候
								</td>
							</tr>
			                  <tr>
			                       <td colspan="2" style="text-align:right; padding-top:10px">
			                       		<a href="javascript:void(0);" class="btn btn-default"  onclick="BindFileEvent();">确定</a>
			                       		<a href="javascript:void(0);" class="btn btn-default"  onclick="javascript:$('#uploadDiv').modal('hide')">关闭</a>
			                       </td>
			                  </tr>
		             	 </table>
					</form>
				</div>
			</div>
		</div>
	</div>
 	
	2018/8/15
</body>
</html>