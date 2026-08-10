package com.erikjarquin.ventas.service;

import java.util.List;

import com.erikjarquin.ventas.model.dto.Permissions.PermissionResponse;

public interface PermissionService {
    List<PermissionResponse> getAllPermissions();
}
