document.addEventListener("DOMContentLoaded", () => {
  const tablaUsuarios = document.getElementById("dataTable");

  if (!tablaUsuarios) {
    console.error("⚠️ No se encontró la tabla de usuarios (#dataTable)");
    return;
  }

  // Evento para detectar click en botones .btn-eliminar
  tablaUsuarios.addEventListener("click", async (e) => {
    const btn = e.target.closest(".btn-eliminar");
    if (!btn) return;

    const idSeleccionado = btn.dataset.id;
    const filaSeleccionada = btn.closest("tr");

    // Confirmación con SweetAlert2
    const confirmacion = await Swal.fire({
      title: "¿Eliminar usuario?",
      text: "Esta acción no se puede deshacer.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Sí, eliminar",
      cancelButtonText: "Cancelar",
      reverseButtons: true
    });

    if (!confirmacion.isConfirmed) return;

    try {
      const response = await fetch(`/api/usuarios/${idSeleccionado}`, {
        method: "DELETE",
      });

      if (!response.ok) {
        Swal.fire("Error", "No se pudo eliminar el usuario.", "error");
        return;
      }

      // Animación de desaparición
      filaSeleccionada.classList.add("fade-out");
      setTimeout(() => {
        filaSeleccionada.remove();

        // Si estás usando DataTable, recargar
        if ($.fn.DataTable.isDataTable('#dataTable')) {
          $('#dataTable').DataTable().ajax.reload();
        }
      }, 300);

      Swal.fire("Eliminado", "El usuario fue eliminado correctamente.", "success");

    } catch (error) {
      Swal.fire("Error", "No se pudo conectar con el servidor.", "error");
      console.error(error);
    }
  });
});
