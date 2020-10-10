/**
*描述：定时获取短信回复内容
*
*创建人：武申金
*创建时间：2018/4/24
*最后修改时间：2018/4/24
*/
//获取CS
CCService cs = new CCService(userInfo);
java.text.SimpleDateFormat sdf= new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
String result = "-20";
String resultxml = "";
List<CCObject> list = new ArrayList<CCObject>();

String urls = "http://smsapi.c123.cn/DataPlatform/DataApi?action=getReply&ac=1001@1777110010&authkey=856e313548bf4e7cd413";
java.net.HttpURLConnection httpconn = null;
try {
  result = urls;
  java.net.URL url = new java.net.URL(urls);
  httpconn = (java.net.HttpURLConnection) url.openConnection();
  java.io.BufferedReader rd = new java.io.BufferedReader(new java.io.InputStreamReader(httpconn.getInputStream()));
  String str = "";
  while((str = rd.readLine()) != null) {
    resultxml += str;
  }
  org.dom4j.Document doc = null;
  doc = org.dom4j.DocumentHelper.parseText(resultxml);

  org.dom4j.Element rootElt = doc.getRootElement(); // 获取根节点

  java.util.Iterator iter = rootElt.elementIterator("Item");
  // 遍历Item节点
  while (iter.hasNext()) {
    CCObject obj = new CCObject("xm");
    org.dom4j.Element recordEle = (org.dom4j.Element) iter.next();
    obj.put("mobile",recordEle.attributeValue("mobile"));
    obj.put("content",recordEle.attributeValue("content"));
    obj.put("time",recordEle.attributeValue("time"));
    list.add(obj);
  }
  result = resultxml.indexOf("result=") != -1?resultxml.substring(resultxml.indexOf("result=\"")+8,resultxml.indexOf("\">")):"";
  rd.close();
} catch (java.net.MalformedURLException e) {
  e.printStackTrace();
} catch (java.io.IOException e) {
  e.printStackTrace();
} catch (Exception e) {
  e.printStackTrace();
} finally {
  if (httpconn != null) {
    httpconn.disconnect();
    httpconn = null;
  }
}

if(list.size()>0) {
  for(CCObject item:list) {
    String mobile = item.get("mobile")==null?"":item.get("mobile")+"";
    String content = item.get("content")==null?"":item.get("content")+"";
    String time = item.get("time")==null?"":item.get("time")+"";
    Date date = new java.util.Date(Long.valueOf(time)*1000);
    List<CCObject> khlist = cs.cqlQuery("Account","select id as id from Account where dianhua = '"+mobile+"' and is_deleted='0' and (recordtype = '2018496272C934EtLhWs' or recordtype = '20186166515AE4A8ZfOc')"); //这里用电话去搜客户,但是没有去限定客户的手机号唯一
    if(khlist.size()>0) {
      //String reg = "^([1-9]|(1[0-0]))$"; 
      String reg = "^[1-2]$"; //这里正则去匹配判断客户回复内容, 要求前面回复为数字1|2, 其他随意
      int fens=6;
      if(content.matches(reg)) {
        cs.cqlQuery("Account","update Account set khmyd = '"+content+"' where id = '"+khlist.get(0).get("id")+"'");
      }
      CCObject obj = new CCObject("hfdxnr");
      obj.put("hfhm",mobile);
      obj.put("hfnr",content);
      obj.put("hfsj",sdf.format(date));
      obj.put("khmc",khlist.get(0).get("id")+"");
      cs.insert(obj);
    }
  }
}