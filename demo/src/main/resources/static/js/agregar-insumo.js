document.addEventListener("DOMContentLoaded", () => {
  const formulario = document.getElementById("insumoForm");
  const campoCantidad = document.getElementById("cantidad");

  // Extraer CSRF
  const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

  /* ============================================================
      FUNCIÓN PARA CREAR INSUMO
  ============================================================ */
  async function crearInsumo(datosInsumo) {
    try {
      const respuesta = await fetch("http://localhost:8080/api/insumos", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          [csrfHeader]: csrfToken,
        },
        body: JSON.stringify(datosInsumo),
      });

      if (!respuesta.ok) {
        if (respuesta.status === 409) {
          const msg = await respuesta.text();
          Swal.fire({
            title: "Nombre duplicado",
            text: msg,
            icon: "warning",
          });
          return null;
        } else {
          throw new Error(`Error HTTP: ${respuesta.status}`);
        }
      }

      return await respuesta.json();
    } catch (error) {
      console.error("Error al crear el insumo:", error);
      Swal.fire({
        title: "Error",
        text: "Hubo un problema al guardar el insumo.",
        icon: "error",
      });
      return null;
    }
  }

  /* ============================================================
      EVENTO SUBMIT — VALIDACIONES + SWEETALERT
  ============================================================ */
  formulario.addEventListener("submit", async (e) => {
    e.preventDefault();

    if (!formulario.checkValidity()) {
      formulario.classList.add("was-validated");
      return;
    }

    const stockIngresado = parseInt(campoCantidad.value, 10);

    const datosInsumo = {
      nombre: document.getElementById("nombre").value,
      stock: stockIngresado,
      unidadM: document.getElementById("unidadM").value,
    };

    // Advertencia de stock bajo
    if (stockIngresado <= 20) {
      await Swal.fire({
        title: "Stock bajo",
        text: "El stock ingresado es igual o menor a 20. ¿Deseas continuar?",
        icon: "warning",
        confirmButtonText: "Sí, continuar",
        showCancelButton: true,
        cancelButtonText: "Cancelar",
      }).then((result) => {
        if (!result.isConfirmed) return;
      });
    }

    // Confirmación general
    const confirmacion = await Swal.fire({
      title: "¿Guardar insumo?",
      text: "Confirma que deseas registrar este insumo.",
      icon: "question",
      confirmButtonText: "Guardar",
      showCancelButton: true,
      cancelButtonText: "Cancelar",
      reverseButtons: true,
    });

    if (!confirmacion.isConfirmed) return;

    // Loading
    Swal.fire({
      title: "Guardando...",
      text: "Por favor espera un momento.",
      allowOutsideClick: false,
      allowEscapeKey: false,
      didOpen: () => Swal.showLoading(),
    });

    const resultado = await crearInsumo(datosInsumo);

    if (resultado) {
      // Éxito
      Swal.fire({
        title: "¡Insumo guardado!",
        text: "El insumo se agregó correctamente.",
        icon: "success",
        timer: 2500,
        timerProgressBar: true,
      });

      formulario.reset();
      formulario.classList.remove("was-validated");
    }
  });

  /* ============================================================
      BOTÓN LIMPIAR
  ============================================================ */
  document.getElementById("btnLimpiar").addEventListener("click", () => {
    formulario.reset();
    formulario.classList.remove("was-validated");

    Swal.fire({
      title: "Formulario limpiado",
      icon: "info",
      timer: 1500,
      showConfirmButton: false,
    });
  });
});
