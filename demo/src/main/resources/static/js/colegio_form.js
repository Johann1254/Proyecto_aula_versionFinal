const apiColegios = "http://localhost:8080/api/colegios";

let insumosDisponibles = [];
let insumosActuales = [];
let uniformes = [];

/* ========================================================
    CARGAR INSUMOS
======================================================== */
async function cargarInsumos() {
  try {
    const res = await fetch(`${apiColegios}/insumos`);
    insumosDisponibles = await res.json();

    const select = document.getElementById("selectInsumo");
    select.innerHTML = '<option value="">Seleccione un insumo</option>';

    insumosDisponibles.forEach(i => {
      const opt = document.createElement("option");
      opt.value = i.nombre;
      opt.textContent = `${i.nombre} (Stock: ${i.stock} ${i.unidadM})`;
      select.appendChild(opt);
    });
  } catch (err) {
    console.error("Error cargando insumos:", err);
    Swal.fire("Error", "No se pudieron cargar los insumos.", "error");
  }
}
cargarInsumos();

/* ========================================================
    AUTOCOMPLETAR UNIDAD
======================================================== */
document.getElementById("selectInsumo").addEventListener("change", (e) => {
  const insumoSel = insumosDisponibles.find(i => i.nombre === e.target.value);
  document.getElementById("unidadMedida").value = insumoSel ? insumoSel.unidadM : "";
});

/* ========================================================
    AGREGAR INSUMO A INSUMOSACTUALES
======================================================== */
document.getElementById("btnAgregarInsumo").addEventListener("click", () => {
  const nombre = document.getElementById("selectInsumo").value;
  const unidadMedida = document.getElementById("unidadMedida").value;
  const cantidad = parseFloat(document.getElementById("cantidadInsumo").value);

  if (!nombre || !unidadMedida || isNaN(cantidad)) {
    Swal.fire("Campos incompletos", "Debe llenar todos los datos del insumo.", "warning");
    return;
  }

  insumosActuales.push({ nombre, unidadMedida, cantidad });
  actualizarTablaInsumos();
  limpiarCamposInsumo();
});


function actualizarTablaInsumos() {
  const tbody = document.querySelector("#tablaInsumos tbody");
  tbody.innerHTML = insumosActuales.map((i, idx) => `
      <tr>
        <td>${i.nombre}</td>
        <td>${i.unidadMedida}</td>
        <td>${i.cantidad}</td>
        <td>
          <button class="btn btn-danger btn-sm" onclick="eliminarInsumo(${idx})">
            <i class="bi bi-trash"></i> X
          </button>
        </td>
      </tr>
  `).join("");
}

async function eliminarInsumo(i) {
  const r = await Swal.fire({
    icon: "warning",
    title: "¿Eliminar insumo?",
    text: "Este insumo será removido del uniforme.",
    showCancelButton: true,
    confirmButtonText: "Sí, eliminar",
    cancelButtonText: "Cancelar"
  });

  if (!r.isConfirmed) return;

  insumosActuales.splice(i, 1);
  actualizarTablaInsumos();

  Swal.fire({
    icon: "success",
    title: "Insumo eliminado",
    timer: 1200,
    showConfirmButton: false
  });
}

function limpiarCamposInsumo() {
  document.getElementById("selectInsumo").value = "";
  document.getElementById("unidadMedida").value = "";
  document.getElementById("cantidadInsumo").value = "";
}

/* ========================================================
    PRENDAS DINÁMICAS
======================================================== */
const prendaSelect = document.getElementById("prenda");
const tipoSelect = document.getElementById("tipo");
const tallaSelect = document.getElementById("talla");

document.querySelectorAll("input[name='genero']").forEach(radio => {
  radio.addEventListener("change", actualizarOpcionesPrenda);
});
tipoSelect.addEventListener("change", actualizarOpcionesPrenda);
prendaSelect.addEventListener("change", actualizarTallas);

function actualizarOpcionesPrenda() {
  const genero = document.querySelector("input[name='genero']:checked")?.value;
  const tipo = tipoSelect.value;
  let prendas = [];

  if (tipo === "Diario") {
    prendas = (genero === "Masculino")
      ? ["Camisa","Suéter","Guayabera","Pantalón"]
      : ["Camisa","Suéter","Falda","Jomper"];
  } else if (tipo === "Educación física") {
    prendas = ["Sueter", "Sudadera"];
  }

  prendaSelect.innerHTML = '<option value="">Seleccione</option>';
  prendas.forEach(p => prendaSelect.innerHTML += `<option value="${p}">${p}</option>`);

  tallaSelect.innerHTML = '<option value="">Seleccione una prenda</option>';
}

function actualizarTallas() {
  const prenda = prendaSelect.value;
  let tallas = [];

  if (prenda === "Pantalón" || prenda === "Sudadera") {
    tallas = ["06", "08", "10", "12", "14", "16"];
  } else {
    tallas = ["S", "M", "L", "XL"];
  }

  tallaSelect.innerHTML = '<option value="">Seleccione</option>';
  tallas.forEach(t => tallaSelect.innerHTML += `<option value="${t}">${t}</option>`);
}

/* ========================================================
    AGREGAR UNIFORME
======================================================== */
document.getElementById("btnAgregarUniforme").addEventListener("click", () => {
  const prenda = prendaSelect.value;
  const tipo = tipoSelect.value;
  const talla = tallaSelect.value;
  const genero = document.querySelector("input[name='genero']:checked")?.value;

  if (!prenda || !tipo || !talla || insumosActuales.length === 0) {
    Swal.fire("Datos incompletos", "Complete los datos del uniforme y agregue insumos.", "warning");
    return;
  }

  uniformes.push({
    prenda,
    tipo,
    talla,
    genero,
    insumos: [...insumosActuales]
  });

  insumosActuales = [];
  actualizarTablaInsumos();
  actualizarTablaUniformes();
  limpiarCamposUniforme();
});


function actualizarTablaUniformes() {
  const tbody = document.querySelector("#tablaUniformes tbody");
  tbody.innerHTML = uniformes.map((u, idx) => `
      <tr>
        <td>${u.prenda}</td>
        <td>${u.tipo}</td>
        <td>${u.talla}</td>
        <td>${u.genero}</td>
        <td>${u.insumos.length}</td>
        <td><button class="btn btn-danger btn-sm" onclick="eliminarUniforme(${idx})">X</button></td>
      </tr>
  `).join("");
}

async function eliminarUniforme(i) {
  const r = await Swal.fire({
    icon: "warning",
    title: "¿Eliminar uniforme?",
    text: "Se eliminará este uniforme de la lista.",
    showCancelButton: true,
    confirmButtonText: "Sí, eliminar",
    cancelButtonText: "Cancelar"
  });

  if (!r.isConfirmed) return;

  uniformes.splice(i, 1);
  actualizarTablaUniformes();

  Swal.fire({
    icon: "success",
    title: "Uniforme eliminado",
    timer: 1200,
    showConfirmButton: false
  });
}

function limpiarCamposUniforme() {
  tipoSelect.value = "";
  prendaSelect.innerHTML = '<option value="">Seleccione un tipo primero</option>';
  tallaSelect.innerHTML = '<option value="">Seleccione una prenda</option>';
}

/* ========================================================
    GUARDAR TODO EL COLEGIO
======================================================== */
document.getElementById("btnGuardar").addEventListener("click", async () => {
  const nombre = document.getElementById("nombreColegio").value.trim();
  const direccion = document.getElementById("direccionColegio").value.trim();

  if (!nombre || !direccion || uniformes.length === 0) {
    Swal.fire("Campos incompletos", "Debe completar los datos del colegio y agregar uniformes.", "warning");
    return;
  }

  const confirm = await Swal.fire({
    icon: "question",
    title: "¿Registrar colegio?",
    text: "Se guardará el colegio junto con todos los uniformes.",
    showCancelButton: true,
    confirmButtonText: "Sí, guardar",
    cancelButtonText: "Cancelar"
  });

  if (!confirm.isConfirmed) return;

  const payload = { nombre, direccion, uniformes };

  try {
    const res = await fetch(`${apiColegios}/registrarCompleto`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    if (res.ok) {
      Swal.fire({
        icon: "success",
        title: "Registro completado",
        text: "Colegio y uniformes registrados correctamente.",
        timer: 1500,
        showConfirmButton: false
      });

      uniformes = [];
      actualizarTablaUniformes();
    } else {
      const err = await res.text();
      Swal.fire("Error", err, "error");
    }
  } catch (e) {
    Swal.fire("Error", "No se pudo completar el registro.", "error");
    console.error(e);
  }
});
