String pyzt_new = String.valueOf(record_new.get("pyzt"));// 批阅状态
		String pyzt_old = String.valueOf(record_old.get("pyzt"));// 批阅状态
		/**
		 * 提交批阅后共享抄送人并发送邮件通知
		 */
		if ("草稿".equals(pyzt_old) && "未批阅".equals(pyzt_new)) {
			CCService cs = new CCService(userInfo);
			String tjr = String.valueOf(record_new.get("ownerid"));// 提交人
			List<CCObject> user = cs.cquery("ccuser", "id__c = '" + tjr + "'");
			tjr = String.valueOf(user.get(0).get("name"));
			String csr = null == record_new.get("csr") ? "" : String.valueOf(record_new.get("csr"));// 抄送人
			if (!"".equals(csr)) {
				String id = String.valueOf(record_new.get("id"));// 工作报告ID
				String[] csr_list = csr.split(";");// 抄送人列表
				StringBuffer csr_ids = new StringBuffer();// 抄送人ID拼装成sql in查询格式
				// 共享
				for (String userid : csr_list) {
					CCObject share = new CCObject("TheWorkReport", CCObject.IS_SHARED);
					// 给共享对象赋值
					share.put("parentid", id);// 要共享的记录id
					share.put("accesslevel", "Read");// 访问权限级别，取值范围（全部：Read只读）
					share.put("rowcause", "Manual");// 原因（手动共享：Manual,所有人：Owner）
					share.put("userorgroupid", userid);// 共享给用户、角色等（要取ID值）
					// 批阅人插入共享表
					cs.insert(share);
					// 抄送人ID拼装成sql in查询格式
					csr_ids.append("'").append(userid).append("',");
				}
				// 发送通知邮件
				MultiLanguage multiLang = new MultiLanguage(userInfo, "zh");// 初始化多语言
				// 获取用户邮箱地址
				List<CCObject> user_list = cs.cqlQuery("ccuser",
						"select email from ccuser where id in (" + csr_ids.substring(0, csr_ids.length() - 1) + ")");
				String[] toAddress = new String[user_list.size()];
				for (int i = 0; i < user_list.size(); i++) {
					toAddress[i] = String.valueOf(user_list.get(i).get("email"));
				}
				String subject = "工作报告抄送提醒";
				String content = tjr + "提交了" + String.valueOf(record_new.get("ksrq")) + "至"
						+ String.valueOf(record_new.get("jsrq")) + "的工作报告 ";
				// 多语言处理
				multiLang.initLanguageResource(subject, "en", "Work report reminding");
				subject = multiLang.translate(subject);
				multiLang.initLanguageResource(content, "en", tjr + " submitted a work report from "
						+ String.valueOf(record_new.get("ksrq")) + " to " + String.valueOf(record_new.get("jsrq")));
				content = multiLang.translate(content);
				SendEmail sendEmail = new SendEmail(userInfo);
				sendEmail.sendMailFromSystem(toAddress, null, null, subject, content, false);
			}
		}