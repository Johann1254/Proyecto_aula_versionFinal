document.addEventListener("DOMContentLoaded", () => {
  let insumoIdSeleccionado = null;
  let botonSeleccionado = null;

  // Escuchar clicks en botones de eliminación
  document.addEventListener("click", async (e) => {
    const boton = e.target.closest(".btn-eliminar");
    if (!boton) return;

    e.preventDefault();
    insumoIdSeleccionado = boton.getAttribute("data-id");
    botonSeleccionado = boton;

    if (!insumoIdSeleccionado) return;

    // SWEETALERT DE CONFIRMACIÓN
    const confirmacion = await Swal.fire({
      title: "¿Eliminar insumo?",
      text: "Esta acción no se puede deshacer.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Sí, eliminar",
      cancelButtonText: "Cancelar",
      reverseButtons: true
    });

    if (!confirmacion.isConfirmed) return;

    // LOADING
    Swal.fire({
      title: "Eliminando...",
      allowOutsideClick: false,
      didOpen: () => Swal.showLoading()
    });

    const eliminado = await eliminarInsumo(insumoIdSeleccionado);

    if (eliminado) {
      const fila = botonSeleccionado.closest("tr");
      if (fila) fila.remove();

      Swal.fire({
        icon: "success",
        title: "Insumo eliminado",
        timer: 1500,
        showConfirmButton: false
      });
    } else {
      Swal.fire({
        icon: "error",
        title: "Error al eliminar",
        text: "No se pudo eliminar el insumo."
      });
    }

    insumoIdSeleccionado = null;
    botonSeleccionado = null;
  });

  // FUNCIÓN PARA ELIMINAR
  async function eliminarInsumo(id) {
    try {
      const token = document
        .querySelector('meta[name="_csrf"]')
        ?.getAttribute("content");
      const header = document
        .querySelector('meta[name="_csrf_header"]')
        ?.getAttribute("content");

      const headers = token && header ? { [header]: token } : {};

      const response = await fetch(`http://localhost:8080/api/insumos/${id}`, {
        method: "DELETE",
        headers: headers,
      });

      if (!response.ok) {
        console.error("Error al eliminar:", response.status, await response.text());
        return false;
      }

      return true;
    } catch (error) {
      console.error("Error de red al eliminar insumo:", error);
      return false;
    }
  }
});
