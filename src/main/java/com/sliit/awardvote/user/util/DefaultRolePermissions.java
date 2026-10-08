package com.sliit.awardvote.user.util;

import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.UserRole;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class DefaultRolePermissions {

    private static final Map<UserRole, Set<Permission>> DEFAULTS = new EnumMap<>(UserRole.class);

    static {
        DEFAULTS.put(UserRole.SYSTEM_ADMIN, EnumSet.allOf(Permission.class));
        DEFAULTS.put(UserRole.AWARDS_STAFF, EnumSet.of(Permission.MANAGE_AWARDS, Permission.REVIEW_NOMINATIONS, Permission.HANDLE_FEEDBACK));
        DEFAULTS.put(UserRole.JUDGE, EnumSet.of(Permission.JUDGE_NOMINATIONS));
        DEFAULTS.put(UserRole.MARKETING_OFFICER, EnumSet.of(Permission.MANAGE_CONTENT, Permission.MANAGE_NOTIFICATIONS, Permission.HANDLE_FEEDBACK));
        DEFAULTS.put(UserRole.SPONSOR_COORDINATOR, EnumSet.of(Permission.MANAGE_SPONSORS));
        DEFAULTS.put(UserRole.NOMINEE, EnumSet.noneOf(Permission.class));
        DEFAULTS.put(UserRole.PUBLIC_USER, EnumSet.of(Permission.VOTE));
    }

    private DefaultRolePermissions() {
    }

    public static Set<Permission> forRole(UserRole role) {
        return DEFAULTS.getOrDefault(role, EnumSet.noneOf(Permission.class));
    }
}
