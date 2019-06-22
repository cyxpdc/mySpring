package test;

import com.pdc.spring.helper.DatabaseHelper;
import org.smart4j.plugin.security.SmartSecurity;

import java.util.Set;

/**
 * 应用安全控制
 *
 * @author pdc
 */
public class AppSecurity implements SmartSecurity {
    /**
     * 根据用户名获取密码
     * @param username 用户名
     * @return
     */
    public String getPassword(String username) {
        String sql = "SELECT password FROM user WHERE username = ?";
        return DatabaseHelper.query(sql, username);
    }

    /**
     * 根据用户名获取角色名集合
     * @param username 用户名
     * @return
     */
    public Set<String> getRoleNameSet(String username) {
        String sql = "SELECT r.role_name " +
                "FROM user u, user_role ur, role r " +
                "WHERE u.id = ur.user_id " +
                "AND r.id = ur.role_id " +
                "AND u.username = ?";
        return DatabaseHelper.querySet(sql, username);
    }
    /**
     * 根据角色名获取权限名集合
     * @param roleName 角色名
     * @return 权限名集合
     */
    public Set<String> getPermissionNameSet(String roleName) {
        String sql = "SELECT p.permission_name " +
                "FROM role r, role_permission rp, permission p " +
                "WHERE r.id = rp.role_id " +
                "AND p.id = rp.permission_id " +
                "AND r.role_name = ?";
        return DatabaseHelper.querySet(sql, roleName);
    }
}
