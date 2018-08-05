/**
 * 
 */
package com.yonyou.i18n.main;

/**
 * @author wenfa
 *
 */
public class I18nMain {

	/**
	 * 国际化工具类入口
	 * 
	 * 一、后台java国际化
	 * 
	 * 		1、 针对java的国际化
	 * 
	 * 二、前台国际化
	 * 		1、 基于react的国际化
	 * 
	 * 			1.1、主要针对js的国际化
	 * 
	 * 
	 * 		2、 基于UUI的国际化
	 * 
	 * 			2.1、针对html的国际化
	 * 
	 * 			2.2、针对js的国际化
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		StepBy sb = new StepBy();
		
		sb.init();
		
		sb.extract();
		
		sb.resource();
		
		sb.replace();
		
		System.exit(0);
		
	}

}
