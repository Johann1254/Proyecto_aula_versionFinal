document.addEventListener("DOMContentLoaded", () => {
  let idSeleccionado = null;

  // Detectar clic en botón eliminar
  document.body.addEventListener("click", async (e) => {
    const btn = e.target.closest(".btn-eliminar");
    if (!btn) return;

    idSeleccionado = btn.dataset.id;

    // Mostrar confirmación con SweetAlert2
    const confirmacion = await Swal.fire({
      title: "¿Eliminar proveedor?",
      text: "Esta acción no se puede deshacer.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Sí, eliminar",
      cancelButtonText: "Cancelar",
      reverseButtons: true,
    });

    if (!confirmacion.isConfirmed) return;

    try {
      const response = await fetch(`http://localhost:8080/api/proveedores/${idSeleccionado}`, {
        method: "DELETE",
      });

      if (!response.ok) throw new Error("Error al eliminar");

      Swal.fire({
        icon: "success",
        title: "Eliminado",
        text: "Proveedor eliminado correctamente",
        timer: 1500,
        showConfirmButton: false,
      });

      setTimeout(() => window.location.reload(), 1500);

    } catch (error) {
      console.error("Error al eliminar proveedor:", error);

      Swal.fire({
        icon: "error",
        title: "Error",
        text: "No se pudo eliminar el proveedor.",
      });
    }
  });
});
