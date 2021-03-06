/**
*描述：定时获取短信回复内容
*      
*创建人：武申金
*创建时间：2018/4/24
*最后修改时间：2018/4/24
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
	   String urlss = "http://smsapi.c123.cn/DataPlatform/DataApi?action=getSendState&ac=1001@1775810010&authkey=0d9f091e6cb422ef8d5c";
           String urls = "http://smsapi.c123.cn/DataPlatform/DataApi?action=getReply&ac=1001@1775810010&authkey=0d9f091e6cb422ef8d5c";
           java.net.HttpURLConnection httpconn = null;
           try{
		result = urls;
		java.net.URL url = new java.net.URL(urls);
                java.net.URL urla = new java.net.URL(urlss);
		httpconn = (java.net.HttpURLConnection) url.openConnection();
		java.io.BufferedReader rd = new java.io.BufferedReader(new java.io.InputStreamReader(httpconn.getInputStream()));
                String str = "";
                while((str = rd.readLine()) != null){
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
            String mobile = item.get("mobile")==null?"":item.get("mobile")+"";
            String content = item.get("content")==null?"":item.get("content")+"";
            String time = item.get("time")==null?"":item.get("time")+"";
            Date date = new java.util.Date(Long.valueOf(time)*1000);
            List<CCObject> khlist = cs.cqlQuery("Account","select id as id from Account where lxrdh = '"+mobile+"' and is_deleted='0' and YEAR(createdate)='"+year+"' and MONTH(createdate)='"+month+"' and DAY(createdate)='"+day+"' and ifnull(khmyd,0)='0'");
            if(khlist.size()>0){
                String reg = "^([1-2])$";
		     if(content.matches(reg)){
                       if("1".equals(content)){
                             cs.cqlQuery("Account","update Account set khmyd = '5' where id = '"+khlist.get(0).get("id")+"'");
                             
                       }else{
                             cs.cqlQuery("Account","update Account set khmyd = '-10' where id = '"+khlist.get(0).get("id")+"'");
                            
                       }
		       
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