document.addEventListener("DOMContentLoaded", async () => {
  const insumoId = new URLSearchParams(window.location.search).get("id");
  if (insumoId) await cargarDatosInsumo(insumoId);
});

/* ===============================
      CARGAR DATOS
=============================== */
async function cargarDatosInsumo(id) {
  try {
    const response = await fetch(`http://localhost:8080/api/insumos/${id}`);
    if (!response.ok)
      throw new Error(`Error al obtener el insumo: ${response.status}`);

    const insumo = await response.json();

    document.getElementById("nombre").value = insumo.nombre || "";
    document.getElementById("cantidad").value = insumo.stock || 0;
    document.getElementById("unidadM").value = insumo.unidadM || "";
  } catch (error) {
    console.error("Error al cargar el insumo:", error);

    Swal.fire({
      icon: "error",
      title: "Error",
      text: "Hubo un error al cargar los datos del insumo.",
    });
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const formulario = document.getElementById("insumoForm");

  formulario.addEventListener("submit", async (evento) => {
    evento.preventDefault();

    if (!formulario.checkValidity()) {
      formulario.classList.add("was-validated");
      return;
    }

    const insumoId = new URLSearchParams(window.location.search).get("id");
    if (!insumoId) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "No se encontró el ID del insumo.",
      });
      return;
    }

    const datosInsumo = {
      id: insumoId,
      nombre: document.getElementById("nombre").value,
      stock: parseFloat(document.getElementById("cantidad").value) || 0,
      unidadM: document.getElementById("unidadM").value,
    };

    // CONFIRMACIÓN CON SWEETALERT
    const confirm = await Swal.fire({
      title: "¿Actualizar insumo?",
      text: "Se guardarán los cambios realizados.",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Sí, actualizar",
      cancelButtonText: "Cancelar",
      reverseButtons: true,
    });

    if (!confirm.isConfirmed) return;

    // LOADING
    Swal.fire({
      title: "Actualizando...",
      allowOutsideClick: false,
      didOpen: () => Swal.showLoading()
    });

    const resultado = await actualizarInsumo(insumoId, datosInsumo);

    if (resultado) {
      formulario.classList.remove("was-validated");

      Swal.fire({
        icon: "success",
        title: "¡Insumo actualizado!",
        text: "El insumo se ha actualizado correctamente.",
        timer: 1800,
        showConfirmButton: false
      });

      setTimeout(() => {
        window.location.href = "/inventario";
      }, 1800);

    } else {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "Hubo un problema al actualizar el insumo.",
      });
    }
  });

  /* ===============================
        ACTUALIZAR INSUMO
  =============================== */
  async function actualizarInsumo(id, datosInsumo) {
    try {
      const token = document
        .querySelector('meta[name="_csrf"]')
        .getAttribute("content");
      const header = document
        .querySelector('meta[name="_csrf_header"]')
        .getAttribute("content");

      const response = await fetch(`http://localhost:8080/api/insumos/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          [header]: token
        },
        body: JSON.stringify(datosInsumo),
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error(`Error HTTP: ${response.status}, Detalle: ${errorText}`);
        throw new Error(`Error HTTP: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error("Error al actualizar el insumo:", error);
      return null;
    }
  }
});
