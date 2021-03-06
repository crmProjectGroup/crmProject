/**
*描述：提前三天发短信预祝客户节日快乐
*      
*创建人：武申金
*创建时间：2018/4/26
*最后修改时间：2018/4/26
*/
//获取CS     
CCService cs=new CCService(userInfo);
java.text.SimpleDateFormat sdf= new java.text.SimpleDateFormat("yyyy-MM-dd");
Calendar c=Calendar.getInstance();
c.add(Calendar.DATE,3);
String stgrq=sdf.format(c.getTime());//得到当前时间三天后的日期
stgrq=stgrq.substring(5,10);//截取三天以后的月和日
String content="";
if("01-01".equals(stgrq)){
       content="您好，三天后是元旦节，提前祝您元旦节快乐！"; 
}else if("05-01".equals(stgrq)){
       content="您好，三天后是五一劳动节，提前祝您五一劳动节快乐！";
}else if("10-01".equals(stgrq)){
       content="您好，三天后是国庆节，提前祝您国庆节快乐！";
}
  if(!content.equals("")){
	String sql="shouji <> ''";
	List<CCObject>lxrlist=cs.cquery("Contact",sql);
	for(CCObject cc:lxrlist){
	String lxrxm=(String)cc.get("name"); 
	String shouji=(String)cc.get("shouji");	   	   
	String result = "-20";
	String urls = "http://smsapi.c123.cn/OpenPlatform/OpenApi?action=sendOnce&ac=1001@1777110010&authkey=856e313548bf4e7cd413&cgid=0&csid=0&m="+shouji+"&c=【瑞信行】"+java.net.URLEncoder.encode(lxrxm+content, "utf-8");
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

