package org.smart4j.plugin.security.realm;

import org.apache.shiro.realm.jdbc.JdbcRealm;
import com.pdc.spring.helper.DatabaseHelper;
import org.smart4j.plugin.security.SecurityConfig;
import org.smart4j.plugin.security.password.Md5CredentialsMatcher;

/**
 * 基于 Smart 的 JDBC Realm（需要提供相关 smart.plugin.security.jdbc.* 配置项,即相关sql语句）
 *
 * @author pdc
 */
public class SmartJdbcRealm extends JdbcRealm {

    public SmartJdbcRealm() {
        super.setDataSource(DatabaseHelper.getDataSource());//获得数据源
        super.setAuthenticationQuery(SecurityConfig.getJdbcAuthcQuery());//根据用户名获取密码
        super.setUserRolesQuery(SecurityConfig.getJdbcRolesQuery());//根据用户名获取角色名集合
        super.setPermissionsQuery(SecurityConfig.getJdbcPermissionsQuery());//根据角色名获取权限名集合
        super.setPermissionsLookupEnabled(true);//开启后可连接permission表进行查询
        super.setCredentialsMatcher(new Md5CredentialsMatcher());//MD5加密算法
    }
}
