package com.pdc.plugin.security.realm;

import java.util.HashSet;
import java.util.Set;

import com.pdc.plugin.security.SecurityConstant;
import com.pdc.plugin.security.SmartSecurity;
import com.pdc.plugin.security.password.Md5CredentialsMatcher;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

/**
 * 基于 Smart 的自定义 Realm（需要实现 SmartSecurity 接口）
 * 将相关信息都放在AuthenticationInfo对象中
 *
 * @author pdc
 */
public class SmartCustomRealm extends AuthorizingRealm {

    private final SmartSecurity smartSecurity;

    public SmartCustomRealm(SmartSecurity smartSecurity) {
        this.smartSecurity = smartSecurity;//如用来测试的AppSecurity
        super.setName(SecurityConstant.REALMS_CUSTOM);
        super.setCredentialsMatcher(new Md5CredentialsMatcher());//使用MD5加密算法
    }

    /**
     * 用于认证，存储用户对应的用户名和密码
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    public AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token == null) {
            throw new AuthenticationException("parameter token is null");
        }
        //通过AuthenticationToken对象获取从表单中提交过来的用户名
        String username = ((UsernamePasswordToken) token).getUsername();
        //通过SmartSecurity接口并根据用户名获取数据库中存放的密码
        String password = smartSecurity.getPassword(username);
        //将用户名和密码放入AuthenticationInfo对象中，便于后续的认证操作
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo();
        authenticationInfo.setPrincipals(new SimplePrincipalCollection(username, super.getName()));
        authenticationInfo.setCredentials(password);
        return authenticationInfo;
    }

    /**
     * 用于授权，存储用户对应的角色和权限
     * @param principals
     * @return
     */
    @Override
    public AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("parameter principals is null");
        }
        //获取已认证用户的用户名
        String username = (String) super.getAvailablePrincipal(principals);
        //通过SmartSecurity接口并根据用户名获取角色名集合
        Set<String> roleNameSet = smartSecurity.getRoleNameSet(username);
        //通过SmartSecurity接口并根据用户名获取与其对应的权限名集合
        Set<String> permissionNameSet = new HashSet<>();
        if (roleNameSet != null && roleNameSet.size() > 0) {
            for (String roleName : roleNameSet) {
                Set<String> currentPermissionNameSet = smartSecurity.getPermissionNameSet(roleName);
                permissionNameSet.addAll(currentPermissionNameSet);
            }
        }
        //将角色名集合与权限集名合放入AuthorizationInfo对象中，便于后续的授权操作
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(roleNameSet);
        authorizationInfo.setStringPermissions(permissionNameSet);
        return authorizationInfo;
    }
}
