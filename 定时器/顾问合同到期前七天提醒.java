/**
*描述：顾问合同到期前七天提醒
*      
*创建人：武申金
*创建时间：2018/7/13
*最后修改时间：2018/7/13
*/
//获取CS     
CCService cs=new CCService(userInfo);
java.text.SimpleDateFormat sdf= new java.text.SimpleDateFormat("yyyy-MM-dd");
Calendar c=Calendar.getInstance();
c.add(Calendar.DATE,7);
String stgrq=sdf.format(c.getTime());//得到当前时间三天后的日期
List<CCObject>htlist=cs.cquery("contract","recordtype='2018D52C045D7EBBZE8r' and htjsrq='"+stgrq+"'");
for(CCObject cc:htlist){
String htmc=(String)cc.get("htmc");//合同名称
String owneridccname=(String)cc.get("owneridccname");//所有人名称
String shouji = cc.get("syrdh") == null ? "" :cc.get("syrdh").toString();//所有人电话
if(!"".equals(shouji)){
String result = "-20";
String urls = "http://smsapi.c123.cn/OpenPlatform/OpenApi?action=sendOnce&ac=1001@1775810010&authkey=0d9f091e6cb422ef8d5c&cgid=0&csid=0&m="+shouji+"&c=【瑞信行】"+java.net.URLEncoder.encode(owneridccname+"您好，您的顾问合同:"+htmc+",将于七天后到期!", "utf-8");
java.net.HttpURLConnection httpconn = null;
try{
result = urls;
java.net.URL url = new java.net.URL(urls);
httpconn = (java.net.HttpURLConnection) url.openConnection();
java.io.BufferedReader rd = new java.io.BufferedReader(new java.io.InputStreamReader(httpconn.getInputStream()));
String resultxml = rd.readLine();
result = resultxml.indexOf("result=") != -1?resultxml.substring(resultxml.indexOf("result=\"")+8,resultxml.indexOf("\">")):"";
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
}	
} 