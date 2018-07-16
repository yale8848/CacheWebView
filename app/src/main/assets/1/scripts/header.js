var jieqiUserId = 0;
var jieqiUserName = '';
var jieqiUserPassword = '';
var jieqiUserGroup = 0;
var jieqiNewMessage = 0;
var jieqiUserVip = 0;
var jieqiUserHonor = '';
var jieqiUserGroupName = '';
var jieqiUserVipName = '';


var timestamp = Math.ceil((new Date()).valueOf()/1000); //当前时间戳
var flag_overtime = -1;
if(document.cookie.indexOf('jieqiUserInfo') >= 0){
	var jieqiUserInfo = get_cookie_value('jieqiUserInfo');
	//document.write(jieqiUserInfo);
	start = 0;
	offset = jieqiUserInfo.indexOf(',', start);
	while(offset > 0){
		tmpval = jieqiUserInfo.substring(start, offset);
		tmpidx = tmpval.indexOf('=');
		if(tmpidx > 0){
           tmpname = tmpval.substring(0, tmpidx);
		   tmpval = tmpval.substring(tmpidx+1, tmpval.length);
		   if(tmpname == 'jieqiUserId') jieqiUserId = tmpval;
		   else if(tmpname == 'jieqiUserName_un') jieqiUserName = tmpval;
		   else if(tmpname == 'jieqiUserPassword') jieqiUserPassword = tmpval;
		   else if(tmpname == 'jieqiUserGroup') jieqiUserGroup = tmpval;
		   else if(tmpname == 'jieqiNewMessage') jieqiNewMessage = tmpval;
		   else if(tmpname == 'jieqiUserVip') jieqiUserVip = tmpval;
		   else if(tmpname == 'jieqiUserHonor_un') jieqiUserHonor = tmpval;
		   else if(tmpname == 'jieqiUserGroupName_un') jieqiUserGroupName = tmpval;
		}
		start = offset+1;
		if(offset < jieqiUserInfo.length){
		  offset = jieqiUserInfo.indexOf(',', start);
		  if(offset == -1) offset =  jieqiUserInfo.length;
		}else{
          offset = -1;
		}
	}
	flag_overtime = get_cookie_value('overtime');
} else {
	delCookie('overtime');
}

function setCookie(c_name,value,expiredays)
{
    var exdate=new Date()
    exdate.setDate(exdate.getDate()+365)
    document.cookie=c_name+ "=" +escape(value)+";expires="+exdate.toGMTString()+";path=/";
}

function getCookie(c_name)
{
    if (document.cookie.length>0){
        c_start=document.cookie.indexOf(c_name + "=");
        if (c_start!=-1){
            c_start=c_start + c_name.length+1;
            c_end=document.cookie.indexOf(";",c_start);
            if (c_end==-1) c_end=document.cookie.length;
            return unescape(document.cookie.substring(c_start,c_end));
        }
    }
    return "";
}

function delCookie(name){
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval=getCookie(name);
    document.cookie= name + "=;expires="+exp.toGMTString();
}
function get_cookie_value(Name) {
  var search = Name + "=";
　var returnvalue = "";
　if (document.cookie.length > 0) {
　  offset = document.cookie.indexOf(search)
　　if (offset != -1) {
　　  offset += search.length
　　  end = document.cookie.indexOf(";", offset);
　　  if (end == -1)
　　  end = document.cookie.length;
　　  returnvalue=unescape(document.cookie.substring(offset, end));
　　}
　}
　return returnvalue;
}

function login(){
document.writeln("<div style=\"display:none\" >");
document.writeln("</div>");
document.writeln("<div class=\"ywtop\"><div class=\"ywtop_con\"><div class=\"ywtop_sethome\"><a onClick=\"this.style.behavior='url(#default#homepage)';this.setHomePage('http://www.ranwen.org');\" href=\"javascript:void(0)\">将本站设为首页</a></div>");
document.writeln("<div class=\"ywtop_addfavorite\"><a href=\"javascript:void(0)\" onClick=\"addFavorite2()\">收藏燃文小说</a></div>");
document.write('<div class="nri">');
if(jieqiUserId != 0 && jieqiUserName != '' && (document.cookie.indexOf('PHPSESSID') != -1 || jieqiUserPassword != '')){
  if(jieqiUserVip == 1) jieqiUserVipName='<span class="hottext">至尊VIP-</span>';
  document.write('Hi,<a href="/userdetail.php?uid='+jieqiUserId+'" target="_top">'+jieqiUserName+'</a>&nbsp;&nbsp;<a href="/modules/article/bookcase.php?uid='+jieqiUserId+'" target="_top">我的书架</a>');
  if(jieqiNewMessage > 0){
	  document.write(' | <a href="/message.php?uid='+jieqiUserId+'&box=inbox" target="_top"><span class=\"hottext\">您有短信</span></a>');
  }else{
	  document.write(' | <a href="/message.php?uid='+jieqiUserId+'&box=inbox" target="_top">查看短信</a>');
  }
  document.write(' | <a href="/userdetail.php?uid='+jieqiUserId+'" target="_top">查看资料</a> | <a href="/logout.php" target="_self">退出登录</a>&nbsp;');
}else{
  var jumpurl="";
  if(location.href.indexOf("jumpurl") == -1){
    jumpurl=location.href;
  }
  document.write('<form name="frmlogin" id="frmlogin" method="post" action="/login.php?do=submit&action=login&usecookie=1&jumpurl="'+jumpurl+'&jumpreferer=1>');
  document.write('<div class="cc"><div class="txt">账号：</div><div class="inp"><input type="text" name="username" id="username" /></div></div>');
  document.write('<div class="cc"><div class="txt">密码：</div><div class="inp"><input type="password" name="password" id="password" /></div></div>');
  document.write('<div class="frii"><input type="submit" class="int" value=" " /></div><div class="ccc"><div class="txtt"><a href="/getpass.php">忘记密码</a></div><div class="txtt"><a href="/register.php">用户注册</a></div></div></form>');
}
 document.write('</div></div></div>');
}
/****************加入收藏，设置首页***************/
function addFavorite2() {
    var url = window.location;
    var title = document.title;
    var ua = navigator.userAgent.toLowerCase();
    if (ua.indexOf("360se") > -1) {
        alert("由于360浏览器功能限制，请按 Ctrl+D 手动收藏！");
    }
    else if (ua.indexOf("msie 8") > -1) {
        window.external.AddToFavoritesBar(url, title); //IE8
    }
    else if (document.all) {
		try{
			window.external.addFavorite(url, title);
		}catch(e){
			alert('您的浏览器不支持,请按 Ctrl+D 手动收藏!');
		}
    }
    else {
		displayDialog('<!doctype html><html lang=\"en\"><head><meta charset=\"GBK\"><title>收藏</title></head><body><p style=\"color:red;width:360px;height:120px;line-height:30px;text-indent: 10px;font-size: 16px;padding-top: 30px;text-align:center;\">您的浏览器不支持,请按 Ctrl+D 手动收藏!<br><a href=\"https://www.ranwena.com\">https://www.ranwena.com&nbsp;&nbsp;&nbsp;燃文小说</a><br><input type="button" style=\"border: 2px solid #8bcee4;font-size: 20px;width:60px;height:30px;margin-top:15px\"onclick="closeDialog()" value="确&nbsp;定"></button></p></body></html>');
    }
}
function displayDialog(html){
	var dialog;
	dialog = document.getElementById("dialog");
	if(dialog != null) closeDialog();
	dialog = document.createElement("div");
	dialog.setAttribute('id','dialog');
	dialog.style.zIndex = "6000";
	if(document.all){
		dialog.style.width = "400px";
		dialog.style.height = "330px";
	}
	document.body.appendChild(dialog);
	var close_btn='<a href="Javascript:void(0);" onclick="closeDialog()" class="dialogx"></a>';
	dialog.innerHTML =close_btn+html+"<div class='cl'></div>";
	//$('dialog').innerHTML = html + '<iframe src="" frameborder="0" style="position:absolute;visibility:inherit;top:0px;left:0px;width:expression(this.parentNode.offsetWidth);height:expression(this.parentNode.offsetHeight);z-index:-1;filter=\'progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)\';"></iframe>';
	var dialog_w = parseInt(dialog.clientWidth);
	var dialog_h = parseInt(dialog.clientHeight);
	var page_w = pageWidth();
	var page_h = pageHeight();
	var page_l = pageLeft();
	var page_t = pageTop();
	var dialog_top = page_t + (page_h / 2) - (dialog_h / 2);
	if(dialog_top < page_t) dialog_top = page_t;
	var dialog_left = page_l + (page_w / 2) - (dialog_w / 2);
	if(dialog_left < page_l) dialog_left = page_l + page_w - dialog_w;

	dialog.style.left = dialog_left + "px";
	dialog.style.top =  dialog_top + "px";
	dialog.style.visibility = "visible";
}
function closeDialog(){
	var dialog = document.getElementById("dialog");
	if(document.body){
		document.body.removeChild(dialog);
	}else{
		document.documentElement.removeChild(dialog);
	}
	hideMask();
}
function hideMask(){
	var mask = document.getElementById("mask");
	if(mask != null){
		if(document.body) document.body.removeChild(mask);
		else document.documentElement.removeChild(mask);
	}
}
function pageWidth(){
	return window.innerWidth != null ? window.innerWidth : document.documentElement && document.documentElement.clientWidth ? document.documentElement.clientWidth : document.body != null ? document.body.clientWidth : null;
}
function pageHeight(){
	return window.innerHeight != null? window.innerHeight : document.documentElement && document.documentElement.clientHeight ? document.documentElement.clientHeight : document.body != null? document.body.clientHeight : null;
}
function pageTop(){
	return typeof window.pageYOffset != 'undefined' ? window.pageYOffset : document.documentElement && document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop ? document.body.scrollTop : 0;
}
function pageLeft(){
	return typeof window.pageXOffset != 'undefined' ? window.pageXOffset : document.documentElement && document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft ? document.body.scrollLeft : 0;
}
/*************************************************/
function footer(){
document.writeln("<p>本站所有小说为转载作品，所有章节均由网友上传，转载至本站只是为了宣传本书让更多读者欣赏。</p>");
document.writeln("<p>Copyright &copy; 2015 燃文小说 All Rights Reserved.</p>");
document.writeln("<p>冀ICP备11007602号</p>");
document.writeln("<div style=\"display:none\" >");
var _hmt = _hmt || [];
(function() {
  var hm = document.createElement("script");
  hm.src = "https://hm.baidu.com/hm.js?ee14cb63bc8751a3543b202efae71cbc";
  var s = document.getElementsByTagName("script")[0];
  s.parentNode.insertBefore(hm, s);
})();
document.writeln("</div>");
}

function panel(){
document.writeln("<form action=\"https://www.ranwena.com/modules/article/search.php\" name=\"form\" id=\"sform\" target=\"_blank\" method=\"get\">");
document.writeln("<div class=\"search\">");
document.writeln("<input type=\"hidden\" name=\"searchtype\" value=\"keywords\">");
document.writeln("<input type=\"hidden\" name=\"ie\" value=\"gbk\">");
document.writeln("<input name=\"searchkey\" type=\"text\" class=\"input\" placeholder=\"输入需要搜索的小说\" onblur=\"if (value ==\'\'){value=\'输入需要搜索的小说\'}\" onfocus=\"if (value ==\'输入需要搜索的小说\'){value =\'\'}\" id=\"wd\"/><span class=\"s_btn\"><input type=\"submit\" value=\" 搜 索 \" class=\"button\"></span>");
document.writeln("</div>");
document.writeln("</form>");
}

function listindex(){
}

function list1(){
}

function read1(){
}

function read2(){
/*document.writeln("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
document.writeln("<tr> ");
document.writeln('<td><\/td>');
document.writeln('<td><\/td>');
document.writeln('<td><\/td>');
document.writeln("<\/tr>");
document.writeln("<\/table>");*/
}

function read3(){
}

function read4(){
}

function readtan(){
}