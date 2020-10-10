/**
*描述：提前三天发短信预祝客户农历节日快乐
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
int year = c.get(Calendar.YEAR);//当前年份
String stgrq=sdf.format(c.getTime());//得到当前时间三天后的日期
String [] strArray = stgrq.split("-");	
NongLi nl = new NongLi();
String lunarBirthday = nl.getDate(strArray[0] + strArray[1] + strArray[2]);
String content="";

if(lunarBirthday.contains("一月初一")){
   content="冬风辞旧岁，瑞雪兆丰年。值此新春来临之际，我们怀着感恩的心，向您致以亲切的问候和诚挚的谢意！过去的"+year+"，因为合作让我们有了更多的接触和交流，您的理解和信任，达观与睿智，让我们倍受启迪并心存感激。因为有您的鼎力相助，瑞信行在过去的一年里获得了长足的进步。新的一年承载新的希望，开启新的征程。"+(year+1)+"，我们不忘初心再前行，致力打造中国工商地产服务第一选择，以过硬的专业为您奉上更为贴心的服务。【深圳瑞信行全体员工在此恭祝您在新的一年，大展宏图、阖家幸福、身体健康、平安如意！】";
}else if(lunarBirthday.contains("五月初五")){
   content="偶尔的繁忙，不代表遗忘；夏日的到来，愿你心情舒畅，曾落下的问候，这一刻一起补偿，所有的关心，凝聚这条短信，【深圳瑞信行全体员工在此祝您端午节快乐！】";
}else if(lunarBirthday.contains("八月十五")){
   content="您好，三天后是中秋节，提前祝您中秋节快乐！";
}
if(!content.equals("")){
	String sql="shouji <> ''";
	List<CCObject>lxrlist=cs.cquery("Contact",sql);
	for(CCObject cc:lxrlist){
	String lxrxm=(String)cc.get("name"); 
	String shouji=(String)cc.get("shouji");	   	   
	String result = "-20";
	String urls = "http://smsapi.c123.cn/OpenPlatform/OpenApi?action=sendOnce&ac=1001@1775810010&authkey=0d9f091e6cb422ef8d5c&cgid=0&csid=0&m="+shouji+"&c=【深圳瑞信行】"+java.net.URLEncoder.encode(lxrxm+content, "utf-8");
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