/**
 * 
 */
package com.yonyou.i18n.main;

import java.util.List;

import com.yonyou.i18n.core.ExtractChar;
import com.yonyou.i18n.core.ReplaceFile;
import com.yonyou.i18n.core.ResourcesFile;
import com.yonyou.i18n.core.ScanAllFiles;
import com.yonyou.i18n.model.PageNode;

/**
 * 
 * 整体的实现步骤是：
 * 
 * 1、 对文件中的中文进行抽取
 * 2、 对中文进行替换
 * 3、 同时需要往文件中写入固定的依赖行
 * @author wenfa
 *
 */
public class StepBy {

	// 所有的数据都是通过该对象进行传递的
	private List<PageNode> pageNodes = null;
	
	/**
	 * 初始化项目目录
	 * 
	 * 加载所有文件
	 */
	public void init(){
		
		this.pageNodes = (new ScanAllFiles()).loadNodes();
		
	}
	
	/**
	 * 通过字符集范围进行抽取
	 * 
	 * @param args
	 */
	public  void extract() {

		ExtractChar.doExtract(pageNodes);
		
				
	}
	
	/**
	 * 写入资源文件
	 */
	public void resource(){
		
		ResourcesFile.writeResourceFile(pageNodes);
		
		// 分目录写入资源文件
		ResourcesFile.writeResourceFileByDirectory(pageNodes);
		
		
	}
	
	/**
	 * 直接替换
	 * @param args
	 */
	public  void replace() {

		ReplaceFile.updateFilesByReplace(pageNodes);
		
	}
	

	
	
	
}
