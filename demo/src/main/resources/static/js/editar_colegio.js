// editar_colegio.js
const api = "http://localhost:8080/api/colegios";
let idColegio = window.location.pathname.split("/").pop();

let uniformes = [];             // uniformes cargados + los que agregue el usuario
let insumosDisponibles = [];    // insumos desde la BD (tienen nombre, stock, unidadM)
let insumosActuales = [];       // insumos temporales para el uniforme que estamos armando/editar

// --- Inicialización: cargar colegio, insumos y uniformes ---
document.addEventListener("DOMContentLoaded", () => {
  cargarInsumos();      // llena select de insumos (muestra stock y unidad)
  cargarColegio();      // carga nombre/dirección y luego llama cargarUniformes()
  configurarListeners();
});

// -------------------------
// CARGAS INICIALES
// -------------------------
async function cargarColegio() {
  try {
    const res = await fetch(`${api}/${idColegio}`);
    if (!res.ok) throw new Error("No se pudo cargar el colegio");
    const col = await res.json();
    document.getElementById("nombreColegio").value = col.nombre || "";
    document.getElementById("direccionColegio").value = col.direccion || "";
    await cargarUniformes();
  } catch (err) {
    console.error("Error cargando colegio:", err);
  }
}

async function cargarUniformes() {
  try {
    const res = await fetch(`${api}/${idColegio}/uniformes`);
    if (!res.ok) throw new Error("No se pudieron cargar los uniformes");
    const data = await res.json();
    // Aseguramos que venga un array
    uniformes = Array.isArray(data) ? data : [];
    // Si los uniformes traen insumos, los dejamos tal cual, sino inicializamos array vacío
    uniformes = uniformes.map(u => ({ ...u, insumos: u.insumos ? u.insumos.slice() : [] }));
    actualizarTablaUniformes();
  } catch (err) {
    console.error("Error cargando uniformes:", err);
    uniformes = [];
  }
}

async function cargarInsumos() {
  try {
    // Usamos la ruta /insumos para obtener stock y unidad
    const res = await fetch(`${api}/insumos`);
    if (!res.ok) throw new Error("No se pudo obtener la lista de insumos.");
    insumosDisponibles = await res.json();

    const select = document.getElementById("insumoSelect"); // acorde a tu HTML
    select.innerHTML = '<option value="">Seleccione un insumo</option>';

    insumosDisponibles.forEach(i => {
      const opt = document.createElement("option");
      // valor por nombre para mantener compatibilidad con tu payload (nombre aparece en insumos del uniforme)
      opt.value = i.nombre;
      opt.textContent = `${i.nombre} (Stock: ${i.stock} ${i.unidadM})`;
      select.appendChild(opt);
    });
  } catch (err) {
    console.error("Error cargando insumos:", err);
  }
}

// -------------------------
// CONFIGURAR LISTENERS (botones y selects)
// -------------------------
function configurarListeners() {
  // Tipo / Prenda / Genero -> prenda dinámica y tallas
  document.getElementById("tipo").addEventListener("change", actualizarOpcionesPrenda);
  document.querySelectorAll("input[name='genero']").forEach(radio => radio.addEventListener("change", actualizarOpcionesPrenda));
  document.getElementById("prenda").addEventListener("change", actualizarTallas);

  // Autocompletar unidad al seleccionar insumo
  document.getElementById("insumoSelect").addEventListener("change", (e) => {
    const nombre = e.target.value;
    const ins = insumosDisponibles.find(i => i.nombre === nombre);
    document.getElementById("unidadMedida").value = ins ? ins.unidadM : "";
  });

  // Agregar insumo temporal al uniforme actual
  document.getElementById("btnAgregarInsumo").addEventListener("click", (ev) => {
    ev.preventDefault();
    agregarInsumoTemporal();
  });

  // Agregar uniforme a la lista local
  document.getElementById("btnAgregarUniforme").addEventListener("click", (ev) => {
    ev.preventDefault();
    agregarUniformeLocal();
  });

  // Guardar todo (colegio + uniformes)
  document.getElementById("btnGuardar").addEventListener("click", async (ev) => {
    ev.preventDefault();
    await guardarCambios();
  });
}

// -------------------------
// PRINNICIPALES FUNCIONES: prendas y tallas (idénticas a la guía)
// -------------------------
function actualizarOpcionesPrenda() {
  const genero = document.querySelector("input[name='genero']:checked")?.value;
  const tipo = document.getElementById("tipo").value;
  const prendaSelect = document.getElementById("prenda");
  const tallaSelect = document.getElementById("talla");

  let prendas = [];

  if (tipo === "Diario") {
    prendas = (genero === "Masculino") ? ["Camisa", "Pantalón"] : ["Camisa", "Falda"];
  } else if (tipo === "Educación física") {
    prendas = ["Camisa", "Sudadera"];
  }

  prendaSelect.innerHTML = '<option value="">Seleccione</option>';
  prendas.forEach(p => {
    const opt = document.createElement("option");
    opt.value = p;
    opt.textContent = p;
    prendaSelect.appendChild(opt);
  });

  tallaSelect.innerHTML = '<option value="">Seleccione una prenda</option>';
}

function actualizarTallas() {
  const prenda = document.getElementById("prenda").value;
  const tallaSelect = document.getElementById("talla");
  let lista = [];

  if (prenda === "Pantalón" || prenda === "Sudadera") {
    lista = ["06", "08", "10", "12", "14", "16"];
  } else {
    lista = ["S", "M", "L", "XL"];
  }

  tallaSelect.innerHTML = '<option value="">Seleccione</option>';
  lista.forEach(t => {
    const opt = document.createElement("option");
    opt.value = t;
    opt.textContent = t;
    tallaSelect.appendChild(opt);
  });
}

// -------------------------
// MANEJO DE INSUMOS (temporal para el uniforme en edición)
// -------------------------
function agregarInsumoTemporal() {
  const nombre = document.getElementById("insumoSelect").value;
  const unidad = document.getElementById("unidadMedida").value.trim();
  const cantidadRaw = document.getElementById("cantidadInsumo").value;
  const cantidad = cantidadRaw === "" ? NaN : parseFloat(cantidadRaw);

  if (!nombre || !unidad || isNaN(cantidad) || cantidad <= 0) {
    Swal.fire({
      icon: "warning",
      title: "Campos incompletos",
      text: "Complete correctamente los datos del insumo (nombre, unidad, cantidad mayor a 0)."
    });;
    return;
  }

  // Agregar a insumosActuales
  insumosActuales.push({
    nombre,
    unidadMedida: unidad,
    cantidad
  });

  actualizarTablaInsumos();
  limpiarCamposInsumo();
}

function actualizarTablaInsumos() {
  const tbody = document.querySelector("#tablaInsumos tbody");
  tbody.innerHTML = insumosActuales.map((i, idx) => `
    <tr>
      <td>${i.nombre}</td>
      <td>${i.unidadMedida}</td>
      <td>${i.cantidad}</td>
      <td><button class="btn btn-danger btn-sm" onclick="eliminarInsumo(${idx})">X</button></td>
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
  document.getElementById("insumoSelect").value = "";
  document.getElementById("unidadMedida").value = "";
  document.getElementById("cantidadInsumo").value = "";
}

// -------------------------
// AGREGAR / MOSTRAR UNIFORMES
// -------------------------
function agregarUniformeLocal() {
  const prenda = document.getElementById("prenda").value.trim();
  const tipo = document.getElementById("tipo").value.trim();
  const talla = document.getElementById("talla").value.trim();
  const genero = document.querySelector("input[name='genero']:checked")?.value || document.querySelector("input[name='genero']:checked")?.value;

  // Validaciones: igual que en la guía
  if (!prenda || !tipo || !talla) {
    return Swal.fire({
      icon: "warning",
      title: "Datos faltantes",
      text: "Debe completar Tipo, Prenda y Talla."
    });
  }

  if (insumosActuales.length === 0) {
    return Swal.fire({
      icon: "warning",
      title: "Sin insumos",
      text: "Agregue al menos un insumo al uniforme."
    });
  }

  // Empaquetamos uniforme (sin id -> nuevo) y asignamos insumosActuales
  const nuevo = {
    prenda,
    tipo,
    talla,
    genero,
    colegioId: idColegio,
    insumos: insumosActuales.map(i => ({ ...i })) // clonar
  };

  uniformes.push(nuevo);
  // limpiar insumos temporales y campos del formulario de uniforme
  insumosActuales = [];
  actualizarTablaInsumos();
  actualizarTablaUniformes();
  limpiarCamposUniforme();
}

function actualizarTablaUniformes() {
  const tbody = document.querySelector("#tablaUniformes tbody");
  tbody.innerHTML = uniformes.map((u, idx) => `
    <tr>
      <td>${u.prenda || ""}</td>
      <td>${u.tipo || ""}</td>
      <td>${u.talla || ""}</td>
      <td>${u.genero || ""}</td>
      <td>${u.insumos ? u.insumos.length : 0}</td>
      <td>
        <button class="btn btn-danger btn-sm" onclick="eliminarUniforme(${idx})">X</button>
      </td>
    </tr>
  `).join("");
}

async function eliminarUniforme(i) {
  const r = await Swal.fire({
    icon: "warning",
    title: "¿Eliminar uniforme?",
    text: "Este uniforme será eliminado de la lista.",
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
  document.getElementById("tipo").value = "";
  document.getElementById("prenda").innerHTML = '<option value="">Seleccione un tipo primero</option>';
  document.getElementById("talla").innerHTML = '<option value="">Seleccione una prenda</option>';
  document.querySelectorAll("input[name='genero']").forEach(r => r.checked = false);
  // limpiar insumos (ya lo hace agregarUniformeLocal)
  insumosActuales = [];
  actualizarTablaInsumos();
}

// -------------------------
// GUARDAR CAMBIOS (COLEGIO + UNIFORMES)
// -------------------------
async function guardarCambios() {
  const nombre = document.getElementById("nombreColegio").value.trim();
  const direccion = document.getElementById("direccionColegio").value.trim();

  if (!Array.isArray(uniformes) || uniformes.length === 0) {
    const r = await Swal.fire({
      icon: "question",
      title: "No hay uniformes",
      text: "¿Desea guardar el colegio sin uniformes?", 
      showCancelButton: true,
      confirmButtonText: "Guardar",
      cancelButtonText: "Cancelar"
    });

    if (!r.isConfirmed) return;
  }

  if (!nombre || !direccion) {
    return Swal.fire({
      icon: "warning",
      title: "Campos faltantes",
      text: "Debe completar el nombre y dirección del colegio."
    });
  }

  

  try {
    // 1) Actualizar el colegio
    const resCole = await fetch(`${api}/${idColegio}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ nombre, direccion })
    });
    if (!resCole.ok) throw new Error("Error actualizando colegio.");

    // 2) Asegurarnos que cada uniforme tenga colegioId
    uniformes = uniformes.map(u => ({ ...u, colegioId: idColegio }));

    // 3) Actualizar uniformes (PUT que en tu backend borra los antiguos y guarda los nuevos)
    const resUni = await fetch(`${api}/${idColegio}/uniformes`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(uniformes)
    });
    if (!resUni.ok) {
      const txt = await resUni.text();
      throw new Error("Error guardando uniformes: " + txt);
    }

    Swal.fire({
      icon: "success",
      title: "Cambios guardados",
      text: "El colegio y sus uniformes fueron actualizados correctamente.",
      timer: 2000
    });

    // recargar para reflejar ids nuevos / estado real
    await cargarUniformes();

  } catch (err) {
    console.error(err);
    Swal.fire({
      icon: "error",
      title: "Error al guardar",
      text: err.message || "Ocurrió un error inesperado."
    });;
  }
}
