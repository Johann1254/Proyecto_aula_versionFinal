document.addEventListener("DOMContentLoaded", async () => {
  const id = new URLSearchParams(window.location.search).get("id");
  if (!id) {
    Swal.fire({
      icon: "error",
      title: "Proveedor no encontrado",
      text: "No se proporcionó un ID válido.",
    });
    return;
  }

  try {
    const res = await fetch(`http://localhost:8080/api/proveedores/${id}`);
    const p = await res.json();

    document.getElementById("id").value = p.id;
    document.getElementById("nombre").value = p.nombre;
    document.getElementById("telefono").value = p.telefono;
    document.getElementById("nit").value = p.nit;
    document.getElementById("direccion").value = p.direccion;

  } catch (err) {
    Swal.fire({
      icon: "error",
      title: "Error al cargar los datos",
      text: "No se pudo obtener la información del proveedor.",
    });
    return;
  }

  document.getElementById("proveedorForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const proveedor = {
      id,
      nombre: document.getElementById("nombre").value,
      telefono: document.getElementById("telefono").value,
      nit: document.getElementById("nit").value,
      direccion: document.getElementById("direccion").value,
    };

    // Confirmación antes de actualizar
    const confirmacion = await Swal.fire({
      title: "¿Actualizar proveedor?",
      text: "Los datos serán modificados.",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Sí, actualizar",
      cancelButtonText: "Cancelar",
      reverseButtons: true,
    });

    if (!confirmacion.isConfirmed) return;

    try {
      const res = await fetch(`http://localhost:8080/api/proveedores/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(proveedor),
      });

      if (!res.ok) throw new Error("Error al actualizar");

      await Swal.fire({
        icon: "success",
        title: "Proveedor actualizado",
        text: "Los datos se guardaron correctamente.",
        timer: 1500,
        showConfirmButton: false,
      });

      window.location.href = "/proveedores";

    } catch (err) {
      Swal.fire({
        icon: "error",
        title: "Error al actualizar",
        text: "No se pudo actualizar la información del proveedor.",
      });
    }
  });
});
