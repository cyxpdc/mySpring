package org.smart4j.plugin.security.password;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import com.pdc.spring.util.CodecUtil;

/**
 * MD5 密码匹配器
 *
 * @author pdc
 */
public class Md5CredentialsMatcher implements CredentialsMatcher {
    /**
     * AuthenticationToken可获取从表单提交过来的密码，该密码为明文，未通过MD5加密
     * AuthenticationInfo可获取数据库中存储的密码，已通过MD5加密
     * @param token
     * @param info
     * @return
     */
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        //获取从表单提交过来的密码明文、尚未通过MD5加密
        String submitted = String.valueOf(((UsernamePasswordToken) token).getPassword());
        //获取数据库中存储的密码，已通过MD5加密
        String encrypted = String.valueOf(info.getCredentials());
        //使用CodecUtil将表单数据加密后与数据库密码相比较
        return CodecUtil.md5(submitted).equals(encrypted);
    }
}
