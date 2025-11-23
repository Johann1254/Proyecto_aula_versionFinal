document.addEventListener("DOMContentLoaded", () => {

  const formulario = document.getElementById("usuarioForm");

  formulario.addEventListener("submit", async (evento) => {
    evento.preventDefault();

    if (!formulario.checkValidity()) {
      formulario.classList.add("was-validated");
      return;
    }

    // CONFIRMAR CREACIÓN
    const confirmacion = await Swal.fire({
      title: "¿Registrar usuario?",
      text: "Se creará un nuevo usuario en el sistema.",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Sí, registrar",
      cancelButtonText: "Cancelar",
      reverseButtons: true,
    });

    if (!confirmacion.isConfirmed) return;

    const nuevoUsuario = {
      usuario: document.getElementById("usuario").value,
      correo: document.getElementById("correo").value,
      contrasena: document.getElementById("contrasena").value,
      rol: document.getElementById("rol").value,
      activo: document.getElementById("activo").checked,
    };

    // LOADING
    Swal.fire({
      title: "Guardando usuario...",
      text: "Por favor espera",
      allowOutsideClick: false,
      allowEscapeKey: false,
      didOpen: () => Swal.showLoading(),
    });

    const resultado = await crearUsuario(nuevoUsuario);

    // -----------------------------
    // MANEJO DE RESULTADO
    // -----------------------------
    if (resultado === true) {
      Swal.fire({
        icon: "success",
        title: "¡Usuario registrado!",
        text: "El usuario se ha creado correctamente.",
      }).then(() => {
        formulario.reset();
        window.location.href = "/usuarios";
      });
    }

    if (resultado === "usuario_duplicado") {
      Swal.fire({
        icon: "error",
        title: "Usuario duplicado",
        text: "Ya existe un usuario con ese nombre.",
      });
    }

    if (resultado === "correo_duplicado") {
      Swal.fire({
        icon: "error",
        title: "Correo duplicado",
        text: "Ya existe un usuario con ese correo.",
      });
    }

    if (resultado === false) {
      Swal.fire({
        icon: "error",
        title: "Error inesperado",
        text: "Hubo un problema al registrar el usuario.",
      });
    }
  });

  // ===================================
  // FUNCIÓN: CREAR USUARIO
  // ===================================
  async function crearUsuario(usuario) {
    try {
      const response = await fetch("/api/usuarios", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(usuario)
      });

      // --- MANEJO DE 409 (CONFICTO) ---
      if (response.status === 409) {
        const error = await response.text();
        return error;  // ← devuelve "usuario_duplicado" o "correo_duplicado"
      }

      // Si no es 200 OK entonces error general
      if (!response.ok) {
        return false;
      }

      // ÉXITO
      return true;

    } catch (error) {
      console.error("Error al crear usuario:", error);
      return false;
    }
  }

});
