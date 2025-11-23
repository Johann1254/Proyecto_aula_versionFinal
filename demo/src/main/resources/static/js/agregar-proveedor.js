document.getElementById("formProveedor").addEventListener("submit", async (e) => {
  e.preventDefault();

  const proveedor = {
    nombre: document.getElementById("nombre").value,
    telefono: document.getElementById("telefono").value,
    nit: document.getElementById("nit").value,
    direccion: document.getElementById("direccion").value,
  };

  // Confirmación antes de guardar
  const confirmacion = await Swal.fire({
    title: "¿Registrar proveedor?",
    text: "Verifica que los datos estén correctos.",
    icon: "question",
    showCancelButton: true,
    confirmButtonText: "Sí, registrar",
    cancelButtonText: "Cancelar",
    reverseButtons: true,
  });

  if (!confirmacion.isConfirmed) return;

  try {
    const response = await fetch("http://localhost:8080/api/proveedores", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(proveedor),
    });

    // Si hay error 409 (duplicado)
    if (response.status === 409) {
      const mensaje = await response.text(); // Recibe "Proveedor_duplicado" o "NIT_duplicado"

      if (mensaje === "Proveedor_duplicado") {
        Swal.fire({
          icon: "warning",
          title: "Proveedor ya registrado",
          text: "Ya existe un proveedor con ese nombre.",
        });
      } else if (mensaje === "NIT_duplicado") {
        Swal.fire({
          icon: "warning",
          title: "NIT duplicado",
          text: "Ya existe un proveedor con ese NIT.",
        });
      } else {
        Swal.fire({
          icon: "error",
          title: "Error",
          text: "Conflicto desconocido.",
        });
      }
      return;
    }

    if (!response.ok) throw new Error("Error al registrar");

    // Éxito
    await Swal.fire({
      icon: "success",
      title: "Proveedor registrado correctamente",
      showConfirmButton: false,
      timer: 1700,
    });

    window.location.href = "/proveedores";

  } catch (err) {
    Swal.fire({
      icon: "error",
      title: "Error",
      text: "No se pudo registrar el proveedor.",
    });
  }
});
