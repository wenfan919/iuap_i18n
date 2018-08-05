/**
 * 
 */
package com.yonyou.i18n.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.yonyou.i18n.utils.ConfigUtils;
import com.yonyou.i18n.utils.Helper;
import com.yonyou.i18n.utils.StringUtils;
import com.yonyou.i18n.utils.TextUtils;

/**
 * @author wenfa
 *
 */
public class MatchChar  { 
	
	private static Logger logger = Logger.getLogger(MatchChar.class);
	
	// 抽取的规则：匹配规则以及整体性规则
	//	regexChiExpNote=^[^//*]*[\u0391-\uffe5]+
	//	regexSimpleChinese=([\u0391-\uffe5]+)
	static String regexChiExpNote = ConfigUtils.getPropertyValue("regexChiExpNote");
	
	static String regexChiExpNote4Html = ConfigUtils.getPropertyValue("regexChiExpNote4Html");
	
	static String regexSimpleChinese = ConfigUtils.getPropertyValue("regexSimpleChinese");
	
	public MatchChar() {
	}

	/**
	 * 找出文件中的中文字符串，并放到pageNode对象中
	 * 
	 * 该部分需要注意的是中英文混合的情况，需要记录明确的日志信息，以便排查异常情况 
	 * 
	 * @param file
	 * @return
	 * @throws InterruptedException
	 */
	public static LinkedHashMap<Integer, LinkedHashMap<Integer, String>> search(File file) throws Exception{ 
		
		LinkedHashMap<Integer, LinkedHashMap<Integer, String>> theWholeChineseInfo = new LinkedHashMap<Integer, LinkedHashMap<Integer, String>>();
		
		LineNumberReader inScanner = null;
		
		logger.info("--path:--" + file.getPath());
		
		try { 
			
			String encoding = TextUtils.getFileCharset(file);
			
	        inScanner = new LineNumberReader(new InputStreamReader(new FileInputStream(file),encoding));
	        
			String lineInfo = null; 
			
			while ((lineInfo = inScanner.readLine()) != null) { 
				
				// 正则表达式匹配，该部分匹配的是中文
				// 而且是去除注释(//、 /**/)等信息之后的中文
				Pattern pattern = Pattern.compile(regexChiExpNote); 
				Matcher matcher = pattern.matcher(lineInfo);

//				matcher.lookingAt();
//				matcher.matches();
				if(matcher.find()){ 
					
					// 再次进行中文精确匹配，该部分取出的标准的中文字符串
					LinkedHashMap<Integer, String> chinese = StringUtils.getLineChineseList(lineInfo, regexSimpleChinese);
					
					// 该部分将中文获取后，需要进行中文字符串的整体性校验
					// 比如：java文件，“--提示信息：下载Excel失败！--”，正则匹配只能匹配中文字符“提示信息下载失败”，而我们需要的是整个字符串
					// 该部分主要以“ ‘ 为标记处理
					// 具体的处理逻辑是：
					// 如果只有一个，那作为独立的字符串，
					// 如果多于两个，先把第一个与第二个之间进行判断是否存在界定符，
					// 	如果存在，则第一个为独立字符串，保持，remove（0）再去递归找第二个与第三个，
					// 	如果不存在，则将两者与之间的内容相连后作为第一个字符串，remove（1）再进行比较
					LinkedHashMap<Integer, String> info = new LinkedHashMap<Integer, String>();
					
					if(StringUtils.getFinalLineChineseList(lineInfo,chinese,info)==0){
						logger.info("--info:--" + info);
						
						LinkedHashMap<Integer, String> finalInfo = StringUtils.finalParse(lineInfo, info);
						
						theWholeChineseInfo.put(inScanner.getLineNumber(), finalInfo);
					}
					
				} 
			} 
			inScanner.close(); 
			return theWholeChineseInfo; 
		} catch (IOException e) { 
			e.printStackTrace(); 
			return null; 
		} finally{
			if(inScanner!=null)
				inScanner.close(); 
		}
	} 
	
	
	
	/**
	 * 找出文件中的中文字符串，并放到pageNode对象中
	 * 
	 * 该部分需要注意的是中英文混合的情况，需要记录明确的日志信息，以便排查异常情况 
	 * 
	 * 20180717 update 添加对html单行注释的判断<!--(.*?)-->
	 * update html方案： 
	 * 1、 先把所有的注释全删除，然后再判断是否存在需要替换的中文
	 * 2、 再抽取中文时，带上前后的界定符号，保持中文的唯一性，与java、js的“” 、 ’‘一样，需要界定
	 * 3、 替换时直接替换即可（需要注意中文前后的空格）
	 * 
	 * @param file
	 * @return
	 * @throws InterruptedException
	 */
	public static LinkedHashMap<Integer, LinkedHashMap<Integer, String>> htmlSearch(File file) throws Exception{ 
		
		LinkedHashMap<Integer, LinkedHashMap<Integer, String>> theWholeChineseInfo = new LinkedHashMap<Integer, LinkedHashMap<Integer, String>>();
		
		LineNumberReader inScanner = null;
		LineNumberReader lnr = null;
		
		logger.info("--path:--" + file.getPath());
		
		try { 
			
			String encoding = TextUtils.getFileCharset(file);
			StringBuilder sb = new StringBuilder();
			
	        inScanner = new LineNumberReader(new InputStreamReader(new FileInputStream(file),encoding));
	        
			String lineInfo = null; 
			
			while ((lineInfo = inScanner.readLine()) != null) { 
				sb.append(lineInfo).append(Helper.getLineDelimiter());
			}
			
			// 去除注释
			// "<!--(.|[\r\n])*?-->"
			String r = "<!--(.*?)-->";
			String t = sb.toString().replaceAll(r, "");

			lnr = new LineNumberReader(new InputStreamReader(new ByteArrayInputStream(t.getBytes())));
			
			while ((lineInfo = lnr.readLine()) != null) { 
				
				// 正则表达式匹配，该部分匹配的是中文
				// 而且是去除注释(//、 /**/)等信息之后的中文
				Pattern pattern = Pattern.compile(regexChiExpNote4Html); 
				Matcher matcher = pattern.matcher(lineInfo);

//				matcher.lookingAt();
//				matcher.matches();
				// 20180717 update 添加对html单行注释的判断<!--(.*?)-->
//				if(matcher.find()){ 
//					String r = "<!--(.*?)-->";
//					String t = lineInfo.replaceAll(r, "");
//					
//					pattern = Pattern.compile(regexSimpleChinese);
//					matcher = pattern.matcher(t);
//					
//				}
				if(matcher.find()){ 
					
					// 再次进行中文精确匹配，该部分取出的标准的中文字符串
					LinkedHashMap<Integer, String> chinese = StringUtils.getLineChineseList(lineInfo, regexSimpleChinese);
					
					// 该部分将中文获取后，需要进行中文字符串的整体性校验
					// 比如：java文件，“--提示信息：下载Excel失败！--”，正则匹配只能匹配中文字符“提示信息下载失败”，而我们需要的是整个字符串
					// 该部分主要以“ ‘ 为标记处理
					// 具体的处理逻辑是：
					// 如果只有一个，那作为独立的字符串，
					// 如果多于两个，先把第一个与第二个之间进行判断是否存在界定符，
					// 	如果存在，则第一个为独立字符串，保持，remove（0）再去递归找第二个与第三个，
					// 	如果不存在，则将两者与之间的内容相连后作为第一个字符串，remove（1）再进行比较
					LinkedHashMap<Integer, String> info = new LinkedHashMap<Integer, String>();
					
					if(StringUtils.getFinalLineChineseList(lineInfo,chinese,info)==0){
						logger.info("--info:--" + info);
						
						LinkedHashMap<Integer, String> finalInfo = StringUtils.finalParse(lineInfo, info);
						
						theWholeChineseInfo.put(lnr.getLineNumber(), finalInfo);
					}
					
				} 
			} 
			inScanner.close(); 
			lnr.close();
			
			return theWholeChineseInfo; 
		} catch (IOException e) { 
			e.printStackTrace(); 
			return null; 
		} finally{
			if(inScanner!=null)
				inScanner.close(); 
			if(lnr!=null)
				lnr.close(); 
		}
	} 
	
	
	public static void main(String[] args){
		
		String t = "<!--页面功能名字--> <div class=\"apptitle\">";
		
		String r = "<!--(.*?)-->";
		
		Pattern pattern = Pattern.compile(r); 
		Matcher matcher = pattern.matcher(t);
		
		if(matcher.find()){ 
			matcher.group();
			
		}
		
		t = t.replaceAll(r, "");
		
		System.out.println(t);
		
	}

} 
