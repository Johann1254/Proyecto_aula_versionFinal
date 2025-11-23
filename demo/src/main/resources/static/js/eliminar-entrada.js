document.addEventListener("DOMContentLoaded", () => {
  let entradaIdSeleccionada = null;
  let botonSeleccionado = null;

  // Escuchar clicks en botones de eliminación
  document.addEventListener("click", async (e) => {
    const boton = e.target.closest(".btn-eliminar");
    if (!boton) return;

    e.preventDefault();
    entradaIdSeleccionada = boton.getAttribute("data-id");
    botonSeleccionado = boton;

    if (entradaIdSeleccionada) confirmarEliminacion();
  });

  // Confirmación usando SweetAlert2
  function confirmarEliminacion() {
    Swal.fire({
      title: "¿Eliminar entrada?",
      text: "Esta acción no se puede deshacer.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Sí, eliminar",
      cancelButtonText: "Cancelar",
      reverseButtons: true
    }).then(async (result) => {
      if (result.isConfirmed) {
        const eliminado = await eliminarEntrada(entradaIdSeleccionada);

        if (eliminado) {
          // Eliminar todas las filas relacionadas
          const idEntrada = entradaIdSeleccionada;
          document.querySelectorAll("tr").forEach((fila) => {
            const btn = fila.querySelector(".btn-eliminar");
            if (btn && btn.getAttribute("data-id") === idEntrada) {
              fila.remove();
            }
          });

          Swal.fire({
            title: "Eliminado",
            text: "La entrada fue eliminada correctamente.",
            icon: "success",
            timer: 1500,
            showConfirmButton: false
          });
        } else {
          Swal.fire({
            title: "Error",
            text: "No se pudo eliminar la entrada.",
            icon: "error"
          });
        }
      }

      entradaIdSeleccionada = null;
      botonSeleccionado = null;
    });
  }

  // Método para llamar al backend
  async function eliminarEntrada(id) {
    try {
      const token = document
        .querySelector('meta[name="_csrf"]')
        ?.getAttribute("content");
      const header = document
        .querySelector('meta[name="_csrf_header"]')
        ?.getAttribute("content");

      const headers = token && header ? { [header]: token } : {};

      const response = await fetch(`http://localhost:8080/api/entradas/${id}`, {
        method: "DELETE",
        headers: headers,
      });

      if (!response.ok) {
        console.error("Error al eliminar:", response.status, await response.text());
        return false;
      }

      return true;
    } catch (error) {
      console.error("Error de red:", error);
      return false;
    }
  }
});
