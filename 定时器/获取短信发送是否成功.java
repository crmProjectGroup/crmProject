/**
*描述：定时获取短信是否接收成功
*
*创建人：何俊
*创建时间：2018/11/5
*最后修改时间：2018/11/5
*/
//获取CS
java.util.Calendar cal = java.util.Calendar.getInstance();
cal.setTime(new Date());
int year = cal.get(Calendar.YEAR);//当前年份
int month = cal.get(Calendar.MONTH)+1;//当前月份
int day = cal.get(Calendar.DATE);//当前天数
CCService cs = new CCService(userInfo);
java.text.SimpleDateFormat sdf= new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
String result = "-20";
String resultxml = "";
List<CCObject> list = new ArrayList<CCObject>();
String urlss = "http://smsapi.c123.cn/DataPlatform/DataApi?action=getSendState&ac=1001@1775810010&authkey=0d9f091e6cb422ef8d5c";   //获取发送状态的api借口, 返回xml
//<Item id="1" msgid="1522261879" mobile="15915864312" result="0" return="DELIVRD"/>
//String urls = "http://smsapi.c123.cn/DataPlatform/DataApi?action=getReply&ac=1001@1775810010&authkey=0d9f091e6cb422ef8d5c"; //获取回复内容
java.net.HttpURLConnection httpconn = null; 
try{
	//result = urls;
	//java.net.URL url = new java.net.URL(urls);
	java.net.URL urla = new java.net.URL(urlss); //将字符串变成url对象
	httpconn = (java.net.HttpURLConnection) urla.openConnection(); //打开url链接
	java.io.BufferedReader rd = new java.io.BufferedReader(new java.io.InputStreamReader(httpconn.getInputStream())); //将url的返回对象从字符串变为字节对象
	String str = "";
	while((str = rd.readLine()) != null){  //将多条api记录拼成字符串resultxml
		resultxml += str;
	}

	org.dom4j.Document doc = null; //用org.dom4j.Document处理拼成字符串的xml
	doc = org.dom4j.DocumentHelper.parseText(resultxml);

	org.dom4j.Element rootElt = doc.getRootElement(); // 获取根节点

	java.util.Iterator iter = rootElt.elementIterator("Item"); //迭代器解析xml,以Item为根节点

	// 遍历Item节点
	while (iter.hasNext()) {
		CCObject obj = new CCObject("xms"); //new一个CCObject对象来存储
		org.dom4j.Element recordEle = (org.dom4j.Element) iter.next();
		obj.put("mobile",recordEle.attributeValue("mobile")); //获取手机号码
		//obj.put("content",recordEle.attributeValue("content"));	
		obj.put("result",recordEle.attributeValue("result"));	//获取结果
		//obj.put("time",recordEle.attributeValue("time"));
		obj.put("return",recordEle.attributeValue("return")); //获取返回内容
		list.add(obj);
	}
	result = resultxml.indexOf("result=") != -1?resultxml.substring(resultxml.indexOf("result=\"")+8,resultxml.indexOf("\">")):""; //??

	rd.close();
}catch (java.net.MalformedURLException e) {
	e.printStackTrace();
}catch (java.io.IOException e) {
	e.printStackTrace();
}catch (Exception e){
	e.printStackTrace();
}finally{
	if (httpconn != null) {
		httpconn.disconnect();
		httpconn = null;
	}

}

if(list.size()>0){

	for(CCObject item:list){
		String mobile = item.get("mobile")==null?"":item.get("mobile")+""; //手机号码
		//String content = item.get("content")==null?"":item.get("content")+"";
		String resultr = item.get("result")==null?"":item.get("result")+""; //发送状态
		//String time = item.get("time")==null?"":item.get("time")+"";
		//Date date = new java.util.Date(Long.valueOf(time)*1000);
		String returnr = item.get("return")==null?"":item.get("return")+""; //状态报告
		List<CCObject> khlist = cs.cqlQuery("Account","select id as id from Account where lxrdh = '"+mobile+"' and is_deleted='0' and YEAR(createdate)='"+year+"' and MONTH(createdate)='"+month+"' and DAY(createdate)='"+day+"' and ifnull(spfsdxcg,'') = ''");
		if(khlist.size()>0){
			if("0".equals(resultr) && "DELIVRD".equals(returnr)){
				cs.cqlQuery("Account","update Account set spfsdxcg = 'Y' where id = '"+khlist.get(0).get("id")+"'");
			}
			
			//不用更新回复短信内容
			//CCObject obj = new CCObject("hfdxnr");
			//obj.put("hfhm",mobile);
			//obj.put("hfnr",content);
			//obj.put("hfsj",sdf.format(date));
			//obj.put("khmc",khlist.get(0).get("id")+"");
			//cs.insert(obj);
		}
	}
}