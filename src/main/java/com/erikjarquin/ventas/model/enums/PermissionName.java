package com.erikjarquin.ventas.model.enums;

public enum PermissionName {
    //Compras
    VER_COMPRAS,

    //Productos
    VER_PRODUCTOS,
    CREAR_PRODUCTOS,
    EDITAR_PRODUCTOS,
    ELIMINAR_PRODUCTOS,

    //Clientes
    VER_CLIENTES,

    //Usuarios
    VER_USUARIOS,
    CREAR_USUARIOS,
    EDITAR_USUARIOS,
    ACTIVAR_USUARIOS,
    DESACTIVAR_USUARIOS,

    //Categorías
    VER_CATEGORIAS,
    CREAR_CATEGORIAS,
    ELIMINAR_CATEGORIAS,

    //Ventas
    VER_VENTAS,
    CREAR_VENTAS,
    CANCELAR_VENTAS,

    //Caja
    VER_CAJA,
    ABRIR_CAJA,
    CERRAR_CAJA,
    CORTE_CAJA,

    //Roles
    VER_ROLES,
    CREAR_ROLES,
    ELIMINAR_ROLES,
    EDITAR_ROLES,

    //Reportes
    VER_REPORTES,
    EXPORTAR_REPORTES,

    //Facturas
    VER_FACTURAS,

    //Configuración
    VER_CONFIGURACION,
    EDITAR_CONFIGURACION,

    //Pagos
    PROCESAR_PAGOS
}