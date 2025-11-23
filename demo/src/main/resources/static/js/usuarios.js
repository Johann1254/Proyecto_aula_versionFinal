document.addEventListener("DOMContentLoaded", async () => {
  await cargarUsuarios();
});

async function cargarUsuarios() {
  try {
    const response = await fetch("http://localhost:8080/api/usuarios");
    if (!response.ok) throw new Error(`Error HTTP: ${response.status}`);

    const usuarios = await response.json();
    llenarTablaUsuarios(usuarios);
  } catch (error) {
    console.error("Error al cargar usuarios:", error);
  }
}

function llenarTablaUsuarios(usuarios) {
  const tbody = document.querySelector("#dataTable tbody");
  tbody.innerHTML = "";

  usuarios.forEach((u) => {
    const fila = document.createElement("tr");

    fila.innerHTML = `
      <td>${u.id}</td>
      <td>${u.usuario}</td>
      <td>${u.correo}</td>
      <td>${u.rol}</td>
      <td>
        <span class="badge ${u.activo ? "bg-success" : "bg-danger"}">
          ${u.activo ? "Activo" : "Inactivo"}
        </span>
      </td>
      <td class="text-center">
        <a href="/usuarios/editar?id=${u.id}" class="btn btn-primary btn-circle">
          <i class="fas fa-edit"></i>
        </a>
        <button class="btn btn-danger btn-circle btn-eliminar" data-id="${u.id}">
          <i class="fas fa-trash"></i>
        </button>
      </td>
    `;

    tbody.appendChild(fila);
  });

  inicializarEventosEliminar();
}

function inicializarEventosEliminar() {
  document.querySelectorAll(".btn-eliminar").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      const id = e.currentTarget.getAttribute("data-id");
      mostrarAlertaEliminar(id);
    });
  });
}

function mostrarAlertaEliminar(id) {
  const overlay = document.getElementById("overlay");
  const alerta = document.getElementById("alertaConfirmacion");
  const btnEliminar = alerta.querySelector(".track");
  const btnCancelar = alerta.querySelector(".dismiss");

  overlay.style.display = "block";
  alerta.classList.remove("d-none");

  // Confirmar eliminación
  btnEliminar.onclick = async () => {
    await eliminarUsuario(id);
    overlay.style.display = "none";
    alerta.classList.add("d-none");
  };

  // Cancelar
  btnCancelar.onclick = () => {
    overlay.style.display = "none";
    alerta.classList.add("d-none");
  };
}

async function eliminarUsuario(id) {
  try {
    const response = await fetch(`http://localhost:8080/api/usuarios/${id}`, {
      method: "DELETE",
    });

    if (!response.ok) throw new Error(`Error al eliminar: ${response.status}`);
    console.log(`Usuario ${id} eliminado correctamente`);
    await cargarUsuarios(); // Recargar la tabla
  } catch (error) {
    console.error("Error al eliminar usuario:", error);
  }
}
