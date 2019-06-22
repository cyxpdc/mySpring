package org.smart4j.plugin.security;

import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.CachingSecurityManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.smart4j.plugin.security.realm.SmartCustomRealm;
import org.smart4j.plugin.security.realm.SmartJdbcRealm;

/**
 * 安全过滤器
 * 两个自定义的Realm：SmartJdbcRealm和SmartCustomRealm
 * 分别提供基于sql配置文件的实现和基于编程接口SmartSecurity的实现
 *
 * @author pdc
 */
public class SmartSecurityFilter extends ShiroFilter {

    @Override
    public void init() throws Exception {
        super.init();
        WebSecurityManager webSecurityManager = super.getSecurityManager();
        //设置Realm，可同时支持多个Realm，并按照先后顺序用逗号分隔
        setRealms(webSecurityManager);
        //设置缓存，用于减少数据库查询次数，降低IO访问
        setCache(webSecurityManager);
    }

    /**
     * 设置WebSecurityManager的realms
     * @param webSecurityManager
     */
    private void setRealms(WebSecurityManager webSecurityManager) {
        //读取smart.properties的smart.plugin.security.realms配置项
        //key为smart.plugin.security.realms，value为custom或jdbc
        String securityRealms = SecurityConfig.getRealms();
        if (securityRealms != null) {
            //根据逗号进行拆分
            String[] securityRealmArray = securityRealms.split(",");
            if (securityRealmArray.length > 0) {
                RealmSecurityManager realmSecurityManager = (RealmSecurityManager) webSecurityManager;
                for (String securityRealm : securityRealmArray)
                    //"jdbc"，忽略配置文件大小写
                    //添加基于JDBC的Realm，需配置相关SQL查询语句
                    if (securityRealm.equalsIgnoreCase(SecurityConstant.REALMS_JDBC))
                        realmSecurityManager.setRealm(new SmartJdbcRealm());
                    // "custom"，忽略配置文件大小写
                    // 添加基于定制化的Realm，需实现SmartSecurity接口
                    else if (securityRealm.equalsIgnoreCase(SecurityConstant.REALMS_CUSTOM))
                        realmSecurityManager.setRealm(new SmartCustomRealm(SecurityConfig.getSmartSecurity()));
            }
        }
    }


   /* private void addJdbcRealm(Set<Realm> realms) {
        SmartJdbcRealm smartJdbcRealm = new SmartJdbcRealm();
        realms.add(smartJdbcRealm);
    }

    private void addCustomRealm(Set<Realm> realms) {
        //读取smart.properties的smart.plugin.custom.class配置项,如org.smart4j.chapter5.AppSecurity
        SmartSecurity smartSecurity = SecurityConfig.getSmartSecurity();
        //添加自己实现的Realm
        SmartCustomRealm smartCustomRealm = new SmartCustomRealm(smartSecurity);
        realms.add(smartCustomRealm);
    }*/

    private void setCache(WebSecurityManager webSecurityManager) {
        ////读取smart.properties的smart.plugin.security.cache配置项
        if (SecurityConfig.isCache()) {
            CachingSecurityManager cachingSecurityManager = (CachingSecurityManager) webSecurityManager;
            //使用基于内存的CacheManager
            CacheManager cacheManager = new MemoryConstrainedCacheManager();
            cachingSecurityManager.setCacheManager(cacheManager);
        }
    }
}
