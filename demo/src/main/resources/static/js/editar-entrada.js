document.addEventListener("DOMContentLoaded", () => {
  const formulario = document.getElementById("formEntrada");
  const inputNombre = document.getElementById("nombre");
  const inputInsumoId = document.getElementById("insumoIdSeleccionado");
  const inputCantidad = document.getElementById("cantidad");
  const inputProveedor = document.getElementById("proveedor");
  const tablaInsumosBody = document.getElementById("tabla-insumos");
  const listaSugerencias = document.getElementById("sugerencias");

  const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute("content");
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute("content");

  const entradaId = localStorage.getItem("entradaIdEditar");

  if (!entradaId) {
    Swal.fire({
      icon: "error",
      title: "Entrada no encontrada",
      text: "No se encontró una entrada para editar.",
      confirmButtonColor: "#d33"
    });
    return;
  }

  let detallesEntrada = [];
  let indiceEditando = -1;

  // ────────────────────────────────────────────────
  // CARGAR ENTRADA
  // ────────────────────────────────────────────────
  async function cargarEntrada(id) {
    try {
      const res = await fetch(`http://localhost:8080/api/entradas/${id}`);
      if (!res.ok) throw new Error("Entrada no encontrada");

      const entrada = await res.json();

      // Cargar proveedores
      const resProv = await fetch("http://localhost:8080/api/proveedores");
      const dataProv = await resProv.json();
      const proveedores = dataProv.data || [];

      // Llenar select proveedores
      inputProveedor.innerHTML = '<option value="">Seleccione un proveedor</option>';

      proveedores.forEach((prov) => {
        const option = document.createElement("option");
        option.value = prov.id;
        option.textContent = prov.nombre;
        inputProveedor.appendChild(option);
      });

      document.getElementById("fecha").value = entrada.fecha;
      document.getElementById("descripcion").value = entrada.descripcion;

      if (entrada.proveedor) {
        inputProveedor.value = proveedores.find(p => p.nombre === entrada.proveedor)?.id || "";
      }

      // Cargar detalles
      if (entrada.detalles?.length > 0) {
        detallesEntrada = entrada.detalles;
        renderizarTablaInsumos();
      }

    } catch (error) {
      console.error(error);
      Swal.fire({
        icon: "error",
        title: "Error al cargar",
        text: "Ocurrió un error al cargar la entrada.",
        confirmButtonColor: "#d33"
      });
    }
  }

  // ────────────────────────────────────────────────
  // TABLA DE INSUMOS
  // ────────────────────────────────────────────────
  function renderizarTablaInsumos() {
    tablaInsumosBody.innerHTML = "";

    detallesEntrada.forEach((detalle, index) => {
      const fila = document.createElement("tr");

      fila.innerHTML = `
        <td>${detalle.nombre}</td>
        <td>${detalle.cantidad}</td>
        <td>
          <button type="button" class="btn btn-primary btn-circle data-index="${index}">
            <i class="fas fa-edit"></i>
          </button>
          <button type="button" class="btn btn-danger btn-circle btn-eliminar" data-index="${index}">
            <i class="fas fa-trash"></i>
          </button>
        </td>
      `;

      tablaInsumosBody.appendChild(fila);
    });

    // botón eliminar con SweetAlert
    tablaInsumosBody.querySelectorAll(".btn-eliminar").forEach((btn) => {
      btn.addEventListener("click", (e) => {
        const index = parseInt(e.target.closest("button").getAttribute("data-index"));

        Swal.fire({
          title: "¿Eliminar insumo?",
          text: "Esta acción no se puede deshacer.",
          icon: "warning",
          showCancelButton: true,
          confirmButtonColor: "#d33",
          cancelButtonColor: "#3085d6",
          confirmButtonText: "Eliminar",
          cancelButtonText: "Cancelar"
        }).then((result) => {
          if (result.isConfirmed) {
            detallesEntrada.splice(index, 1);
            renderizarTablaInsumos();
            resetFormulario();

            Swal.fire({
              icon: "success",
              title: "Insumo eliminado",
              timer: 1200,
              showConfirmButton: false
            });
          }
        });
      });
    });

    // botón editar
    tablaInsumosBody.querySelectorAll(".btn-editar").forEach((btn) => {
      btn.addEventListener("click", (e) => {
        const index = parseInt(e.target.closest("button").getAttribute("data-index"));
        cargarInsumoAlFormulario(index);
      });
    });
  }

  function cargarInsumoAlFormulario(index) {
    const d = detallesEntrada[index];
    inputNombre.value = d.nombre;
    inputInsumoId.value = d.insumoId;
    inputCantidad.value = d.cantidad;

    indiceEditando = index;
    document.getElementById("btnAgregarInsumo").textContent = "Actualizar";
  }

  function resetFormulario() {
    inputNombre.value = "";
    inputInsumoId.value = "";
    inputCantidad.value = "";
    indiceEditando = -1;

    document.getElementById("btnAgregarInsumo").textContent = "Agregar";
  }

  // ────────────────────────────────────────────────
  // BOTÓN AGREGAR / ACTUALIZAR INSUMO
  // ────────────────────────────────────────────────
  document.getElementById("btnAgregarInsumo").addEventListener("click", () => {
    const nombre = inputNombre.value.trim();
    const insumoId = inputInsumoId.value;
    const cantidad = parseInt(inputCantidad.value, 10);
    const proveedor = inputProveedor.options[inputProveedor.selectedIndex].text;

    if (!nombre || !insumoId || isNaN(cantidad) || cantidad <= 0) {
      Swal.fire({
        icon: "warning",
        title: "Datos incompletos",
        text: "Ingresa un insumo válido con cantidad mayor a cero.",
        confirmButtonColor: "#3085d6"
      });
      return;
    }

    const nuevoDetalle = { insumoId, nombre, cantidad, proveedor };

    if (indiceEditando >= 0) {
      detallesEntrada[indiceEditando] = nuevoDetalle;
      indiceEditando = -1;
      document.getElementById("btnAgregarInsumo").textContent = "Agregar";
    } else {
      detallesEntrada.push(nuevoDetalle);
    }

    renderizarTablaInsumos();
    resetFormulario();
  });

  // ────────────────────────────────────────────────
  // GUARDAR / ACTUALIZAR ENTRADA
  // ────────────────────────────────────────────────
  if (formulario) {
    formulario.addEventListener("submit", async (evento) => {
      evento.preventDefault();

      if (!formulario.checkValidity()) {
        formulario.classList.add("was-validated");
        return;
      }

      if (detallesEntrada.length === 0) {
        Swal.fire({
          icon: "warning",
          title: "Sin insumos",
          text: "Debes agregar al menos un insumo.",
          confirmButtonColor: "#3085d6"
        });
        return;
      }

      const datosActualizados = {
        id: entradaId,
        fecha: document.getElementById("fecha").value,
        descripcion: document.getElementById("descripcion").value,
        proveedor: inputProveedor.options[inputProveedor.selectedIndex].text,
        detalles: detallesEntrada
      };

      const headers = { "Content-Type": "application/json" };
      if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;

      try {
        const res = await fetch(`http://localhost:8080/api/entradas/${entradaId}`, {
          method: "PUT",
          headers,
          body: JSON.stringify(datosActualizados)
        });

        if (!res.ok) {
          throw new Error("Error al actualizar");
        }

        Swal.fire({
          icon: "success",
          title: "Entrada actualizada",
          text: "Los cambios se guardaron correctamente.",
          confirmButtonColor: "#3085d6"
        }).then(() => {
          window.location.href = "/entradas";
        });

      } catch (error) {
        Swal.fire({
          icon: "error",
          title: "Error al actualizar",
          text: "Ocurrió un error al guardar los cambios.",
          confirmButtonColor: "#d33"
        });
      }
    });
  }

  // ────────────────────────────────────────────────
  // AUTOCOMPLETADO DE INSUMOS
  // ────────────────────────────────────────────────
  inputNombre.addEventListener("input", async () => {
    const texto = inputNombre.value.trim();

    inputInsumoId.value = "";
    inputCantidad.disabled = true;

    if (texto.length < 2) {
      listaSugerencias.innerHTML = "";
      return;
    }

    try {
      const respuesta = await fetch(`/api/insumos/buscar?nombre=${encodeURIComponent(texto)}`);
      const sugerencias = await respuesta.json();
      listaSugerencias.innerHTML = "";

      if (sugerencias.length > 0) {
        sugerencias.forEach((insumo) => {
          const item = document.createElement("button");
          item.classList.add("list-group-item", "list-group-item-action");
          item.textContent = insumo.nombre;

          item.addEventListener("click", () => {
            inputNombre.value = insumo.nombre;
            inputInsumoId.value = insumo.id || insumo._id;
            inputCantidad.disabled = false;
            listaSugerencias.innerHTML = "";
          });

          listaSugerencias.appendChild(item);
        });
      } else {
        const item = document.createElement("div");
        item.classList.add("list-group-item");
        item.textContent = "Sin resultados";
        listaSugerencias.appendChild(item);
      }
    } catch (err) {
      console.error("Error al obtener sugerencias:", err);
    }
  });

  document.addEventListener("click", (e) => {
    if (!e.target.closest("#nombre") && !e.target.closest("#sugerencias")) {
      listaSugerencias.innerHTML = "";
    }
  });

  cargarEntrada(entradaId);
});
