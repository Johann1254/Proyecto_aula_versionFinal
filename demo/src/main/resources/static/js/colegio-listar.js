const apiColegios = "/api/colegios"; // API servida por la aplicación

// ======================================================
// 📊 DataTable con ServerSide (como entradas y proveedores)
// ======================================================
$(document).ready(function () {
    $("#tablaColegios").DataTable({
        serverSide: true,
        processing: true,
        responsive: true,
        autoWidth: false,
        paging: true,
        ordering: true,
        searching: true,
        pageLength: 5,
        lengthMenu: [5, 20, 50, 100],

        language: {
            search: "Buscar:",
            lengthMenu: "Mostrar _MENU_ registros",
            info: "Mostrando _START_ a _END_ de _TOTAL_ colegios",
            paginate: {
                first: "Primero",
                last: "Último",
                next: "Siguiente",
                previous: "Anterior",
            },
            zeroRecords: "No hay colegios registrados",
        },

        // ======================================================
        // 📌 AJAX servidor → DataTables
        // ======================================================
        ajax: async function (data, callback) {
            try {
                const page = Math.floor(data.start / data.length);
                const size = data.length;

                const response = await fetch(`${apiColegios}?page=${page}&size=${size}`);

                if (!response.ok) {
                    throw new Error(`Error al obtener colegios`);
                }

                const json = await response.json();

                // Se aceptan estas estructuras:
                const colegios = json.data || json.content || json || [];

                const formattedData = colegios.map((c) => ({
                    nombre: c.nombre,
                    direccion: c.direccion || "-",
                    acciones: `
                        <button class="btn btn-primary btn-circle title="Editar"
                                onclick="editarColegio('${c.id}')">
                            <i class="fas fa-edit"></i>
                        </button>

                        <button class="btn btn-danger btn-circle btn-eliminar"
                                title="Eliminar"
                                onclick="eliminarColegio('${c.id}')">
                            <i class="fas fa-trash"></i>
                        </button>
                    `
                }));

                callback({
                    draw: data.draw,
                    recordsTotal: json.recordsTotal || json.totalElements || colegios.length,
                    recordsFiltered: json.recordsFiltered || json.totalElements || colegios.length,
                    data: formattedData,
                });

            } catch (error) {
                console.error("Error al obtener colegios:", error);
            }
        },

        // ======================================================
        // Mapeo de columnas
        // ======================================================
        columns: [
            { data: "nombre", title: "Nombre del Colegio" },
            { data: "direccion", title: "Dirección" },
            {
                data: "acciones",
                title: "Acciones",
                orderable: false,
                searchable: false,
            },
        ],
    });
});


// ======================================================
// ✏️ Editar colegio
// ======================================================
function editarColegio(id) {
    window.location.href = `/colegio/editar/${id}`;
}


// ======================================================
// 🗑️ Eliminar con SweetAlert
// ======================================================
async function eliminarColegio(id) {
    const confirmacion = await Swal.fire({
        title: "¿Eliminar colegio?",
        text: "Esta acción también eliminará los uniformes asociados.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#d33",
        cancelButtonColor: "#3085d6"
    });

    if (!confirmacion.isConfirmed) return;

    try {
        const res = await fetch(`${apiColegios}/${id}`, { method: "DELETE" });

        if (res.ok) {
            Swal.fire({
                title: "Eliminado",
                text: "El colegio ha sido eliminado correctamente.",
                icon: "success",
                timer: 2000,
                showConfirmButton: false
            });

            $("#tablaColegios").DataTable().ajax.reload(null, false);

        } else {
            const err = await res.text();
            Swal.fire({
                title: "Error",
                text: "Error al eliminar: " + err,
                icon: "error"
            });
        }
    } catch (err) {
        console.error(err);
        Swal.fire({
            title: "Error de conexión",
            text: "No se pudo conectar al servidor.",
            icon: "error"
        });
    }
}
