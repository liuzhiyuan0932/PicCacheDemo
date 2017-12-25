var xmlHttp;  
function createXMLHttp(){
    if (window.XMLHttpRequest) {
       xmlHttp = new XMLHttpRequest();                  
    } else {
       xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
}
      
function sendEmail(emailHref){
	    $('#emailMsg').html("");
		var toEmail = $("#toEmail").val();
		if(isBlank(toEmail)){
			$('#emailMsg').html("<span class='colorRed'>请输入您的邮箱！</span>");
			return ;
		}
		var regMail = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
		if(!toEmail.match(regMail)){
			$('#emailMsg').html("<span class='colorRed'>邮箱格式不正确，请重新输入！</span>");
			return ;
		}

		document.getElementById("emailButton").disabled = true;  // 按钮失效
		$("#emailButton").addClass("btn_blue_disable");
		$('#emailMsg').html("邮件发送中......");
		createXMLHttp();
		xmlHttp.onreadystatechange = sendEmailResult;
		xmlHttp.open("GET", emailHref.replace(/\&amp;/g, '&') + "&toEmail=" + toEmail, true);
		xmlHttp.setRequestHeader("If-Modified-Since","0");  // 防止缓存，否则ie下可能(ie设置有关)不能多次发送邮件
		xmlHttp.send(null);
}

function sendEmailResult(){
	if(xmlHttp.readyState == 4){
		if(xmlHttp.status == 200){
			document.getElementById("emailButton").disabled = false;  // 按钮有效
			$("#emailButton").removeClass("btn_blue_disable");
			if(xmlHttp.responseText === "fail"){
				$("#emailMsg").html("<span class='colorRed'>邮件发送失败，请重新发送！</span>");
			}else if(xmlHttp.responseText === "success"){
				$("#emailMsg").html("邮件发送成功，请注意查收！");
			}else if(xmlHttp.responseText === "canNotReceiveCNReport"){
				$("#emailMsg").html("该邮箱只接收英文报告，当前中文报告禁止发送！");
			}
		}
	}
}