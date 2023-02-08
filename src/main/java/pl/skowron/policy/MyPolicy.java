package pl.skowron.policy;

import java.security.*;

public class MyPolicy extends Policy
{
    @Override
    public PermissionCollection getPermissions(CodeSource codesource)
    {
        Permissions p = new Permissions();
        p.add(new AllPermission());
        return p;
    }
}
