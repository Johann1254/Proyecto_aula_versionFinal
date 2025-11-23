document.addEventListener("DOMContentLoaded", () => {
  document.addEventListener("click", async (e) => {
    const boton = e.target.closest(".btn-eliminar");
    if (!boton) return;

    e.preventDefault();

    const salidaId = boton.getAttribute("data-id");
    if (!salidaId) return;

    // SweetAlert – Confirmación
    const confirmacion = await Swal.fire({
      title: "¿Eliminar salida?",
      text: "Esta acción no se puede deshacer.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Sí, eliminar",
      cancelButtonText: "Cancelar",
    });

    if (!confirmacion.isConfirmed) return;

    // Obtener CSRF
    const token = document
      .querySelector('meta[name="_csrf"]')
      ?.getAttribute("content");
    const header = document
      .querySelector('meta[name="_csrf_header"]')
      ?.getAttribute("content");

    const headers = token && header ? { [header]: token } : {};

    // Petición DELETE
    try {
      const response = await fetch(
        `http://localhost:8080/api/salidas/${salidaId}`,
        {
          method: "DELETE",
          headers: headers,
        }
      );

      if (!response.ok) {
        throw new Error(await response.text());
      }

      // SweetAlert – Eliminado correctamente
      Swal.fire({
        icon: "success",
        title: "Eliminado",
        text: "La salida fue eliminada correctamente.",
        timer: 1500,
        showConfirmButton: false,
      });

      // Actualiza DataTable sin reiniciar la página
      if ($("#dataTable").length) {
        $("#dataTable").DataTable().ajax.reload(null, false);
      }

    } catch (error) {
      console.error("Error al eliminar salida:", error);

      Swal.fire({
        icon: "error",
        title: "Error",
        text: "No se pudo eliminar la salida.",
      });
    }
  });
});
