package com.pdc.interfaces;

import com.pdc.beans.MethodInfo;
import com.pdc.beans.ServerInfo;

/**
 * rest请求调用handler
 * 
 * @author pdc
 *
 */
public interface RestHandler {

	/**
	 * 初始化服务器信息
	 * 
	 * @param serverInfo
	 */
	void init(ServerInfo serverInfo);

	/**
	 * 调用rest请求, 返回接口
	 * 
	 * @param methodInfo
	 * @return
	 */
	Object invokeRest(MethodInfo methodInfo);

}
