document.addEventListener("DOMContentLoaded", async () => {
  const usuarioId = new URLSearchParams(window.location.search).get("id");
  if (usuarioId) await cargarDatosUsuario(usuarioId);

  const formulario = document.getElementById("usuarioForm");

  formulario.addEventListener("submit", async (e) => {
    e.preventDefault();

    if (!formulario.checkValidity()) {
      formulario.classList.add("was-validated");
      return;
    }

    // CONFIRMAR ACTUALIZACIÓN
    const confirmacion = await Swal.fire({
      title: "¿Guardar cambios?",
      text: "Se actualizará la información del usuario.",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Sí, actualizar",
      cancelButtonText: "Cancelar",
      reverseButtons: true,
    });

    if (!confirmacion.isConfirmed) {
      return; // usuario canceló
    }

    const datosUsuario = {
      id: document.getElementById("id").value,
      usuario: document.getElementById("usuario").value,
      correo: document.getElementById("correo").value,
      contrasena: document.getElementById("contrasena").value,
      rol: document.getElementById("rol").value,
      activo: document.getElementById("activo").checked,
    };

    // LOADING
    Swal.fire({
      title: "Actualizando usuario...",
      text: "Por favor espera",
      allowOutsideClick: false,
      allowEscapeKey: false,
      didOpen: () => Swal.showLoading(),
    });

    const resultado = await actualizarUsuario(datosUsuario.id, datosUsuario);

    if (resultado) {
      Swal.fire({
        icon: "success",
        title: "¡Usuario actualizado!",
        text: "Los datos se guardaron correctamente.",
        confirmButtonText: "Aceptar",
      }).then(() => {
        window.location.href = "/usuarios";
      });
    } else {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "Hubo un problema al actualizar el usuario.",
        confirmButtonText: "Cerrar",
      });
    }
  });
});


// ================================
// CARGAR DATOS DEL USUARIO
// ================================
async function cargarDatosUsuario(id) {
  try {
    const response = await fetch(`http://localhost:8080/api/usuarios/${id}`);
    if (!response.ok) throw new Error(`Error al obtener usuario: ${response.status}`);

    const usuario = await response.json();

    document.getElementById("id").value = usuario.id || "";
    document.getElementById("usuario").value = usuario.usuario || "";
    document.getElementById("correo").value = usuario.correo || "";
    document.getElementById("rol").value = usuario.rol || "";
    document.getElementById("activo").checked = usuario.activo || false;

  } catch (error) {
    console.error("Error al cargar usuario:", error);

    Swal.fire({
      icon: "error",
      title: "No se pudo cargar el usuario",
      text: "Por favor, inténtalo más tarde.",
    });
  }
}


// ================================
// ACTUALIZAR USUARIO
// ================================
async function actualizarUsuario(id, datosUsuario) {
  try {
    const response = await fetch(`http://localhost:8080/api/usuarios/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(datosUsuario),
    });

    if (!response.ok) throw new Error(`HTTP ${response.status}`);

    return await response.json();

  } catch (error) {
    console.error("Error al actualizar:", error);
    return null;
  }
}
